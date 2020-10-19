package com.quence.sailaction.common;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.Selector;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.format.DateTimeFormatter;
import static java.time.LocalDateTime.*;


public class Client extends Thread {

    private static final byte[] ETX = {0b00000011};
    
    /**
     * @return the hbTimeout
     */
    public int getHbTimeout() {
        return hbTimeout;
    }
    private byte status;
    private final String clientName;
    private BufferedReader buffer;
    private int hbTimeout;
    private String sessionID = "";
    private String userSequenceID;
    private List<byte[]> messages;
    private String tsOut;
    private String tsIn;
    private String userID;
    public static Dequeuer dequeuer;
    protected static FileWriter logFile;
    protected static FileWriter debugFile;
    private volatile Instant instantNow;
    private volatile long nanoSeconds;
    private volatile List<String> logMessages;
    
    private static int getValue(byte b) {
        if (b < 0) {
            return 256 + b;
        }
        return b;
    }
    
    private static int getAlignmentSize(int i) {
        int align = (i+1)%4;
        if (align > 0) {
            align = 4 - align;
        }
        return align;
    }
    
    private static String getAlignment(String s) {
        String spaces = "";
        int rem = (s.length() + 1)%4;
        if (rem > 0) {
            for (int i = 0; i < 4-rem; i++)
                spaces += " ";
        }
        return spaces;
    }
    
    private static byte[] get4BytesLen(String s) {
        int data = s.length();
        byte[] result = new byte[4];
        result[3] = (byte) ((data & 0xFF000000) >> 24);
        result[2] = (byte) ((data & 0x00FF0000) >> 16);
        result[1] = (byte) ((data & 0x0000FF00) >> 8);
        result[0] = (byte) ((data & 0x000000FF));
        return result; 
    }

    public Client(String clientName) throws IOException {
    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSSSSS");
        this.clientName = clientName;
        this.userID = Ini.getValue(clientName, "userID");
        instantNow = Instant.now();
        nanoSeconds = System.nanoTime();
        buffer = null;
        this.status = 0b00001000;    //0001 loggedin - //0100 waiting response - //1000 got messages
        this.logMessages = new ArrayList<>();
        Dequeuer dequeuer = this.dequeuer;
        
        try {
            this.hbTimeout = Integer.parseInt(Ini.getValue(this.clientName, "heartbeat"));
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        // instantiate Dequeuer class and start thread
        /*
        dequeuer = new Dequeuer(this);
        Thread dequeuerTh = dequeuer;
        dequeuerTh.start();
        */
        System.out.println(dtf.format(now()) + " Client:Client:end " + clientName);
    }
    
    public void loadMsgs(FileReader fr) throws IOException {
    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSSSSS");
        buffer = new BufferedReader(fr);
        messages = new ArrayList<>();
        String inputStr = buffer.readLine();
        while (inputStr != null) {
            inputStr = inputStr.substring(0, 14) + Ini.getValue(this.clientName, "Trader_Id") + inputStr.substring(22);
            
            int inputLen = inputStr.length();
            byte[] byteLen = Client.get4BytesLen(inputStr);
            int messageLen = 4+inputLen+1+Client.getAlignmentSize(inputLen);
            messages.add(ByteBuffer.allocate(messageLen).put(byteLen).put(inputStr.getBytes()).put(Client.ETX).put(Client.getAlignment(inputStr).getBytes()).array());
            inputStr = buffer.readLine();
        }
        buffer.close();
        System.out.println(dtf.format(now()) + " Client:loadMsgs:end");
    }
    
    public void sendMsg(byte[] message ) throws IOException {
    		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSSSSS");
            this.userSequenceID = String.valueOf(Integer.parseInt(this.userSequenceID) + 1);
            int usLen = this.userSequenceID.length();
           // System.out.println(dtf.format(now()) + " Client:sendMsg:userSequenceID : "+ this.userSequenceID );
            for (int i = 0; i < 8-usLen; i++) {
                this.userSequenceID = "0" + this.userSequenceID;
            }
            byte[] msg = new byte[message.length];
            System.arraycopy(message, 0, msg, 0, 22);
            System.arraycopy(this.userSequenceID.getBytes(), 0, msg, 22, 8);
            System.arraycopy(message, 30, msg, 30, message.length-30);

            this.writeBus(msg);
             System.out.println(dtf.format(now()) + " Client:sendMsg:end");
    }
    
    public void sendLogin() {
    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSSSSS");
        try {
        	//System.out.println(dtf.format(now()) + "Client:sendLogin:");
            TC tc = new TC(this.getClientName(), sessionID.isEmpty());
            String tcStr = tc.toString();
            this.writeTec(tcStr.getBytes());
            System.out.println(dtf.format(now()) + "Client:sendLogin:end  TC : ");
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
          //  System.out.println(dtf.format(now()) + "Client:sendLogin:TC IOException");
        }
    }

    public void sendLogout() {
    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSSSSS");
        try {
            TD td = new TD(this.clientName, this.sessionID);
            String tdStr = td.toString();     
            this.writeTec(tdStr.getBytes());
            System.out.println(dtf.format(now()) + "Client:sendLogout:end");
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
           // System.out.println(dtf.format(now()) + "Client:sendLogin:TD IOException");
        }
    }

    public void connect(int port) throws IOException {
    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSSSSS");
        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());

        // Instantiate the thread for Client
        Client clientThread = this;
        
        //Dequeuer dequeuer = this.dequeuer;
        InetSocketAddress address = new InetSocketAddress(Ini.getValue("server", "address"), Integer.parseInt(Ini.getValue("server", "port")));
        logMessages.add( timeStamp +"  : "+ Client.getTime() + " " + this.userID + " X: Connecting\n");
        channel = AsynchronousSocketChannel.open();
        System.out.println(dtf.format(now()) + "Client:connect:AsynchronousSocketChannel.opened");
        channel.connect(address, null,
            new CompletionHandler() {
                @Override
                public void completed(Object result, Object attachment) {
                     Thread ClientThread = clientThread;
                     ClientThread.start();
      
                    sendLogin(); // send the login message TC 
                    System.out.println(dtf.format(now()) + "Client:connect:AsynchronousSocketChannel.connect:completed:sendLogin successfull");
                }
                
                @Override
                public void failed(Throwable e, Object attachment) {
                	System.out.println(dtf.format(now()) + "Client:connect:AsynchronousSocketChannel.open:failed");
                }
            }
        );
        System.out.println(dtf.format(now()) + "Client:connect:channel.connect successfull");
    }
    
    public void disconnect() throws IOException {
    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSSSSS");
        channel.close();
        logMessages.add(Client.getTime() + " " + this.userID + " X: Disconnecting\n" + "\n");
       // System.out.println(dtf.format(now()) + "Client:connect:disconnect:channel.close successful");
    }

    private void startRead(AsynchronousSocketChannel channel) {
    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSSSSS");
		Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
        System.out.println(timeStamp +"  : Start Read function AsynchronousSocket " + "Start read");
        ByteBuffer lenBuffer = ByteBuffer.allocate(4);
        Client c = this;
        
         channel.read(lenBuffer, null, new CompletionHandler() {
            @Override
            public void completed(Object result, Object attachment) {
                int bufArray = Client.getValue(lenBuffer.get(3))*255*255*255 + Client.getValue(lenBuffer.get(2))*255*255 + Client.getValue(lenBuffer.get(1))*255 + Client.getValue(lenBuffer.get(0));
                ByteBuffer buffer = ByteBuffer.allocate(bufArray + 1 + Client.getAlignmentSize(bufArray));
                //System.out.println(result);
                System.out.println(timeStamp +  "  : Message length is: " + (bufArray + 1 + Client.getAlignmentSize(bufArray)));
                channel.read(buffer, channel, new CompletionHandler() {
                    @Override
                    public void completed(Object result, Object attachment) {
                        tsIn = Client.getTime();
                        String message = (new String(buffer.array())).trim();
                        message = message.substring(0, message.length());
                        if (message.startsWith("TK")) { // get the TK returned message and extract the initial UserSequenceID
                                logMessages.add(tsIn + " " + c.userID + " I: " + message + "\n");
                                c.sessionID = message.substring(2, 6);
                                c.userSequenceID = message.substring(6);
                                System.out.println(dtf.format(now()) + "Client:startRead:channel.read:completed:TK:userSequenceID: " + c.userSequenceID);
                        } else {
                            logMessages.add(tsIn + " " + c.userID + " I: " + message + "\n");
                            System.out.println(dtf.format(now()) + "Client:startRead:channel.read:completed: " + message);
                        }
                        startRead(channel);
                    }
                    
                    @Override
                    public void failed(Throwable exc, Object attachment) {
                    	System.out.println(dtf.format(now()) + "Client:startRead:channel.read2:failed:");
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
                });
                System.out.println(dtf.format(now()) + "Client:startRead:channel.read2:successful");
            }

            @Override
            public void failed(Throwable e, Object attachment) {
            	System.out.println(dtf.format(now()) + "Client:startRead:channel.read1:failed:");
            }
        });
         System.out.println(dtf.format(now()) + "Client:startRead:channel.read1:successful");
    }
    @Override
    public void run() {
    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSSSSS");
        if (channel != null && channel.isOpen()) {
            // start reading received messages 
        	startRead(channel);
            System.out.println(dtf.format(now()) + "Client:run:Thread started:ChannelOpen: " + channel);
        } else  {
        	System.out.println(dtf.format(now()) + "Client:run:Thread started:ChannelNotOpen: " + channel);
        }
    }

    public void writeTec(byte[] input) throws IOException {
    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSSSSS");
		Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
        //System.out.println("before the if condition:  " +  channel);
        if (input != null) {
            Client c = this;
            int inputLen = input.length;
            String inputStr = new String(input);
            byte[] byteLen = Client.get4BytesLen(inputStr);
            byte[] msg = ByteBuffer.allocate(4+inputLen+1+Client.getAlignmentSize(inputLen)).put(byteLen).put(input).put(Client.ETX).put(Client.getAlignment(inputStr).getBytes()).array();
            channel.write(ByteBuffer.wrap(msg), null, new CompletionHandler() {
                @Override
                public void completed(Object result, Object attachment) {
                    tsOut = Client.getTime();
                    String message = new String(input);
                    logMessages.add(tsOut + " " + c.userID + " O: " + message + "\n");
                   System.out.println(dtf.format(now()) + "Client:writeTec:channel.write:completed:sentMessage: " + message);
                }
                @Override
                public void failed(Throwable exc, Object attachment) {
                	 System.out.println(dtf.format(now()) + "Client:writeTec:channel.write:failed");
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            });
            System.out.println(dtf.format(now()) + "Client:writeTec:channel.write: successful");
        }
    }
    
    public void writeBus(byte[] input) throws IOException {
    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSSSSS");
		Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
        if (input != null) {
            Client c = this;
            byte[] msg = ByteBuffer.allocate(input.length).put(input).array();
            channel.write(ByteBuffer.wrap(msg), null, new CompletionHandler() {
                @Override
                public void completed(Object result, Object attachment) {
                    tsOut = Client.getTime();
                    String message = (new String(input)).trim();
                    message = message.substring(4, message.length());
                    logMessages.add(tsOut + " " + c.userID + " O: " + message + "\n");
                     System.out.println(dtf.format(now()) + "Client:writeBus:channel.write:completed:sentMessage: " + message);
                   // messages.remove(0);
                }
                @Override
                public void failed(Throwable exc, Object attachment) {
                	System.out.println(dtf.format(now()) + "Client:writeBus:channel.write:failed");
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            });
           System.out.println(dtf.format(now()) + "Client:writeBus:channel.write: successful");
        }
    }

    private volatile AsynchronousSocketChannel channel;
    Selector selector;

    /**
     * @return the logged
     */
    public boolean isLogged() {
        return (0b00000001 == (status & 0b00000001));
    }

    public void setLogged() {
        status |= 0b00000001;
    }
    
    public void unsetLogged() {
        status &= 0b11111110;
    }
    
    public boolean isWaiting() {
        return (0b00000100 == (status & 0b00000100));
    }
    
    public void setWaiting() {
        status |= 0b00000100;
    }
    
    public void unsetWaiting() {
        status &= 0b11111011;
    }
    
    public boolean isSending() {
        return (0b00000010 == (status & 0b00000010));
    }
    
    public void setSending() {
        status |= 0b00000010;
    }
    
    public void unsetSending() {
        status &= 0b11111101;
    }

    public boolean gotQueue() {
        return (0b00001000 == (status & 0b00001000));
    }
    
    public void unsetQueue() {
        status &= 0b11110111;
    }
    
    public boolean isConnected() {
        return (0b00010000 == (status & 0b00010000));
    }
    
    public void setConnected() {
        status |= 0b00010000;
    }
    
    public void unsetConnected() {
        status &= 0b11101111;
    }

    /**
     * @return the clientName
     */
    public String getClientName() {
        return clientName;
    }
    
    public static String getTime() {
//        LocalDateTime now = LocalDateTime.now();
//        int year = now.getYear();
//        int month = now.getMonthValue();
//        int day = now.getDayOfMonth();
//        int hour = now.getHour();
//        int minute = now.getMinute();
//        int second = now.getSecond();
//        long nano = now.getNano();
//        
//        String time = String.format("%04d%02d%02d%02d%02d%02d%06d",year, month, day, hour, minute, second, nano/1000);
//        
//        
//        
//        Instant instant = Instant.now();
//        DateTimeFormatter.ISO_INSTANT.format(Instant.now().truncatedTo(ChronoUnit.MICROS));
        //return DateTimeFormatter.ISO_INSTANT.format(Instant.now().truncatedTo(ChronoUnit.MICROS));
        return Long.toString(System.nanoTime());
        //return Instant.now().toString();
        
    }

    /**
     * @return the instantNow
     */
    public Instant getInstantNow() {
        return instantNow;
    }

    /**
     * @return the nanoSeconds
     */
    public long getNanoSeconds() {
        return nanoSeconds;
    }

    /**
     * @return the logMessages
     */
    public List<String> getLogMessages() {
        return logMessages;
    }
    public  String getUserSequenceID() {	
    	return  userSequenceID;
    }
    public ArrayList<byte[]> getMessages(){
		return (ArrayList<byte[]>) messages;   	
    }
}
 
