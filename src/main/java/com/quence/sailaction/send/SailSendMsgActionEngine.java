package com.quence.sailaction.send;

import static java.time.LocalDateTime.now;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.olingo.odata2.api.exception.ODataException;

import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;
import com.neotys.extensions.action.engine.Context;
import com.neotys.extensions.action.engine.SampleResult;
import com.neotys.rest.error.NeotysAPIException;
import com.quence.sailaction.common.Client;
import com.neotys.rest.dataexchange.model.StatusBuilder;
import com.neotys.rest.dataexchange.client.DataExchangeAPIClient;
import com.neotys.rest.dataexchange.client.DataExchangeAPIClientFactory;
import com.neotys.rest.dataexchange.model.ContextBuilder;
import com.neotys.rest.dataexchange.model.EntryBuilder;

public class SailSendMsgActionEngine implements ActionEngine {

    private ArrayList<byte[]> messages;
    private ArrayList<Long> delays;
    private ArrayList<String> messagetype;

    private Client c;
    private int size;
    private int cycle;
    private int generate;
    private int BD;
    private int QP;
    private int OE;

    @Override
    public SampleResult execute(Context context, List<ActionParameter> parameters) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSSSSS");
        final SampleResult sampleResult = new SampleResult();
        final StringBuilder requestBuilder = new StringBuilder();
        final StringBuilder responseBuilder = new StringBuilder();
        Integer iterationNumber = context.getCurrentVirtualUser().getCurrentIteration();
        c = (Client) context.getCurrentVirtualUser().get("client");
        size = (int) context.getCurrentVirtualUser().get("size");
        messages = (ArrayList<byte[]>) context.getCurrentVirtualUser().get("messages");
        messagetype = (ArrayList<String>) context.getCurrentVirtualUser().get("messagetype");
        delays = (ArrayList<Long>) context.getCurrentVirtualUser().get("delays");
        generate = (int) context.getCurrentVirtualUser().get("generate");
        BD = (int) context.getCurrentVirtualUser().get("BD");
        OE = (int) context.getCurrentVirtualUser().get("OE");
        QP = (int) context.getCurrentVirtualUser().get("QP");

        for (ActionParameter temp : parameters) {

            switch (temp.getName().toLowerCase()) {
                case "cycle":							//If the current parameter is a message 
                    cycle = Integer.valueOf(temp.getValue()); 		//Initialize the message value
                    break;
                default:
                    break;
            }
        }

        sampleResult.sampleStart();

        if (cycle != 0) {
            //System.out.println(" the number of of cycle is : " +cycle +" the size of the message is : "+ size+" the iteration number is : " + iterationNumber);
            if ((cycle * size) + 1 == iterationNumber) {
                //System.out.println(" StopVue start here is true : ");
                //System.out.println(" the number of of cycle is : " +cycle +" the size of the message is : "+ size+" the iteration number is : " + iterationNumber);
                context.getVariableManager().setValue("stopVU", "true");
            } else {
                if (generate < size) {
                    try {
                        Thread.sleep(delays.get(generate));
                        c.sendMsg(messages.get(generate));

                        switch (messagetype.get(generate)) {
                            case "QP":
                                context.getCurrentVirtualUser().put("QP", QP + 1);
                                //System.out.println(" QP : " +messagetype.get(generate)+ " position  "+ QP);
                                break;
                            case "OE":
                                context.getCurrentVirtualUser().put("OE", OE + 1);
                                break;
                            case "BD":
                                context.getCurrentVirtualUser().put("BD", BD + 1);
                                //System.out.println(" BD : " +messagetype.get(generate)+ " position  "+ BD);
                                break;
                            default:
                                break;
                        }
                        //System.out.println(" the  Deleyas is the follows : "    + delays.get(generate));
                        context.getCurrentVirtualUser().put("generate", generate + 1);
                    } catch (IOException e) {
                        System.out.println(dtf.format(now()) + " SailSendMsgActionEngine:SampleResult:IOException: messege Not SENT at the iteration :  " + iterationNumber);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        System.out.println(dtf.format(now()) + " SailSendMsgActionEngine:SampleResult:InterruptedException: messege Not SENT at the iteration :  " + iterationNumber);
                        e.printStackTrace();
                    }
                } else {
                    int reset = 0;
                    context.getCurrentVirtualUser().put("generate", reset);
                    generate = (int) context.getCurrentVirtualUser().get("generate");
                    try {
                        Thread.sleep(delays.get(generate));
                        c.sendMsg(messages.get(generate));
                        switch (messagetype.get(generate)) {
                            case "QP":
                                context.getCurrentVirtualUser().put("QP", QP + 1);
                                //System.out.println(" QP : " +messagetype.get(generate)+ " position  "+ QP);

                                break;
                            case "OE":
                                context.getCurrentVirtualUser().put("OE", OE + 1);

                                break;
                            case "BD":
                                context.getCurrentVirtualUser().put("BD", BD + 1);
                                //System.out.println(" BD : " +messagetype.get(generate)+ " position  "+ BD);
                                break;
                            default:
                                break;
                        }
                        //	System.out.println(" the  Deleyas is the follows : "    + delays.get(generate));
                        context.getCurrentVirtualUser().put("generate", generate + 1);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        System.out.println(dtf.format(now()) + " SailSendMsgActionEngine:SampleResult:IOException: messege Not SENT at the iteration :  " + iterationNumber);
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        System.out.println(dtf.format(now()) + " SailSendMsgActionEngine:SampleResult:InterruptedException: messege Not SENT at the iteration :  " + iterationNumber);
                        e.printStackTrace();
                    }
                }
            }
        } else { // Cycle = 0 management 

            if (generate < size) {
                try {
                    Thread.sleep(delays.get(generate));
                    c.sendMsg(messages.get(generate));

                    switch (messagetype.get(generate)) {
                        case "QP":
                            context.getCurrentVirtualUser().put("QP", QP + 1);
                            //System.out.println(" QP : " +messagetype.get(generate)+ " position  "+ QP);
                            break;
                        case "OE":
                            context.getCurrentVirtualUser().put("OE", OE + 1);
                            break;
                        case "BD":
                            context.getCurrentVirtualUser().put("BD", BD + 1);
                            //System.out.println(" BD : " +messagetype.get(generate)+ " position  "+ BD);
                            break;
                        default:
                            break;
                    }
                    //System.out.println(" the message  : "    + new String(messages.get(generate))+"and the iteration number is :"+ iterationNumber);
                    context.getCurrentVirtualUser().put("generate", generate + 1);
                } catch (IOException e) {
                    System.out.println(dtf.format(now()) + " SailSendMsgActionEngine:SampleResult:IOException: messege Not SENT at the iteration :  " + iterationNumber);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    System.out.println(dtf.format(now()) + " SailSendMsgActionEngine:SampleResult:InterruptedException: messege Not SENT at the iteration :  " + iterationNumber);
                    e.printStackTrace();
                }
            } else {
                int reset = 0;
                context.getCurrentVirtualUser().put("generate", reset);
                generate = (int) context.getCurrentVirtualUser().get("generate");
                try {
                    Thread.sleep(delays.get(generate));
                    c.sendMsg(messages.get(generate));

                    switch (messagetype.get(generate)) {
                        case "QP":
                            context.getCurrentVirtualUser().put("QP", QP + 1);
                            //System.out.println(" QP : " +messagetype.get(generate)+ " position  "+ QP);

                            break;
                        case "OE":
                            context.getCurrentVirtualUser().put("OE", OE + 1);

                            break;
                        case "BD":
                            context.getCurrentVirtualUser().put("BD", BD + 1);
                            //	System.out.println(" BD : " +messagetype.get(generate)+ " position  "+ BD);
                            break;
                        default:
                            break;
                    }
                    //System.out.println(" the message  : "    + new String(messages.get(generate))+"and the iteration number is :"+ iterationNumber);
                    //System.out.println(" the  Deleyas is the follows : "    + delays.get(generate));
                    context.getCurrentVirtualUser().put("generate", generate + 1);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    System.out.println(dtf.format(now()) + " SailSendMsgActionEngine:SampleResult:IOException: messege Not SENT at the iteration :  " + iterationNumber);
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    System.out.println(dtf.format(now()) + " SailSendMsgActionEngine:SampleResult:InterruptedException: messege Not SENT at the iteration :  " + iterationNumber);
                    e.printStackTrace();
                }
            }

        }

        sampleResult.sampleEnd();

        sampleResult.setResponseContent(responseBuilder.toString());
        return sampleResult;
    }

    @Override
    public void stopExecute() {
        // TODO Auto-generated method stub

    }

    private void FailedTest() throws GeneralSecurityException, IOException, ODataException, URISyntaxException, NeotysAPIException, InterruptedException {
        final StatusBuilder sb = new StatusBuilder();
        sb.state(com.neotys.rest.dataexchange.model.Status.State.FAIL);
        sb.build();
        final ContextBuilder cb = new ContextBuilder();
        final DataExchangeAPIClient client = DataExchangeAPIClientFactory.newClient("http://localhost:7400/DataExchange/v1/Service.svc/");
        final EntryBuilder eb = new EntryBuilder(Arrays.asList("_ScriptName_", "Entry", "Path"), System.currentTimeMillis());
        eb.status(sb.build());
        client.addEntry(eb.build());

    }

}
