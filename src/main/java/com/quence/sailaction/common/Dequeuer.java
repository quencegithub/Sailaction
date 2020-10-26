package com.quence.sailaction.common;

import static java.time.LocalDateTime.now;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Dequeuer extends Thread {
    private final Client client;
    private volatile   List<String> logMessages;
    private volatile Instant instantNow;
    private volatile long nanoSeconds;
    private volatile String clientName;
	Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
    
	public Dequeuer(Client client) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSSSSS");
        this.client = client;
        this.logMessages = this.client.getLogMessages();
        this.instantNow = this.client.getInstantNow();
        this.nanoSeconds = this.client.getNanoSeconds();
        this.clientName = this.client.getClientName();
      //  System.out.println(dtf.format(now()) + " Dequeuer:Dequeuer:end");
    }

    /**
     *
     */
    @Override
    public void run() {
    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSSSSS");
        
    	//System.out.println(dtf.format(now()) + " Dequeuer:run:started");
    	while (true) {
            try {
                sleep(1);
            } catch (InterruptedException ex) {
                Logger.getLogger(Dequeuer.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (!this.logMessages.isEmpty()) {
                try {
                    /*
                    String message = App.logMessages.get(0);
                    String[] parts = message.split("\\s");
                    parts[0] = parts[0].substring(0,26) + "Z";
                    */
                    String message = this.logMessages.get(0);
                    System.out.println("the Dequeuer:run:logged message: ---->>>>" + message+"<<<<<------------");
                    if(message!=null) {
                 // System.out.println(dtf.format(now()) + " Dequeuer:run:logged message:" + message);
                    String[] parts = message.split("\\s");
                    long nanoToAdd = Long.parseLong(parts[0]) - this.nanoSeconds;
                    parts[0] = this.instantNow.plusNanos(nanoToAdd).truncatedTo(ChronoUnit.MICROS).toString();
                    
                    String msg = String.join(" ", parts) + "\n";
                    Client.logFile = new FileWriter(Ini.getValue(this.clientName, "log"), true);
                    Client.logFile.write(msg);
                    this.logMessages.remove(message);
                    Client.logFile.close();
                    }
                   // System.out.println(dtf.format(now()) + " Dequeuer:run:end");
                } catch (Exception ex) {
                    System.out.println( " Dequeuer:run:Exception"+ ex.getStackTrace());
                }
            }
        }
    }
    
}
