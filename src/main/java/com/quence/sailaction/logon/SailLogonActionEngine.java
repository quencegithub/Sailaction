package com.quence.sailaction.logon;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import org.apache.olingo.odata2.api.exception.ODataException;

import com.quence.sailaction.common.*;
import com.google.common.util.concurrent.Service.State;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;
import com.neotys.extensions.action.engine.Context;
import com.neotys.extensions.action.engine.SampleResult;
import com.neotys.rest.dataexchange.model.StatusBuilder;
import com.neotys.rest.error.NeotysAPIException;
import com.neotys.rest.dataexchange.client.DataExchangeAPIClient;
import com.neotys.rest.dataexchange.client.DataExchangeAPIClientFactory;
import com.neotys.rest.dataexchange.model.ContextBuilder;
import com.neotys.rest.dataexchange.model.EntryBuilder;
import com.neotys.rest.dataexchange.model.Status;
import com.neotys.rest.dataexchange.model.Status.*;
import com.neotys.rest.dataexchange.model.StatusBuilder;


import java.time.format.DateTimeFormatter;
import static java.time.LocalDateTime.*;

public class SailLogonActionEngine implements ActionEngine {
  public static   String Path = null;
  private List<byte[]> messages;
  private BufferedReader buffer;
  private   String clientName = null;

	@Override
	public SampleResult execute(Context context, List<ActionParameter> parameters) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSSSSS");
		final SampleResult sampleResult = new SampleResult();
		final StringBuilder requestBuilder = new StringBuilder();
		final StringBuilder responseBuilder = new StringBuilder(); 
		// Classe con TCP Socket Asynchronous Read/Write
		Client c = null;
	    
		for (ActionParameter temp:parameters) {
			switch (temp.getName().toLowerCase()) {
			case "path":							//If the current parameter is a message 
				Path = temp.getValue();			//Initialize the message value
				break;	
			case "client":							//If the current parameter is a message 
				clientName = temp.getValue();			//Initialize the message value
				break;			
			default :
				break;
			}
 		}
 
		sampleResult.sampleStart();
 		try {
 			// instanzio classe Client 
			c = new Client(clientName);
			//System.out.println(dtf.format(now()) + " SailLogonActionEngine:SampleResult: Class Client initialized");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			//System.out.println(dtf.format(now()) + " SailLogonActionEngine:SampleResult:Client class instantiate error");
			e1.printStackTrace();
		}
 
		try {
			 c.loadMsgs(new FileReader(Ini.getValue(clientName, "messagesOUT"))); 
		 	 //System.out.println(dtf.format(now()) + " SailLogonActionEngine:SampleResult:Msg loaded in memory");
 			 c.connect(Integer.parseInt(Ini.getValue("server", "port")));
 		 	 //System.out.println(dtf.format(now()) + " SailLogonActionEngine:SampleResult:c.connect done");

 			// do {}while(c.getUserSequenceID().isEmpty());
	    
		    context.getCurrentVirtualUser().put("client",c);
		    context.getCurrentVirtualUser().put("messages",c.getMessages());
		    context.getCurrentVirtualUser().put("delays",c.getDelays());
		    context.getCurrentVirtualUser().put("size",c.getMessages().size());
			context.getCurrentVirtualUser().put("generate",0);
		    } catch (IOException e) {
		    	// TODO Auto-generated catch block
		    	try {
					FailedTest();
				} catch (GeneralSecurityException | IOException | ODataException | URISyntaxException
						| NeotysAPIException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		     //	System.out.println(dtf.format(now()) + " SailLogonActionEngine:SampleResult:Client class instantiate error");
		    	e.printStackTrace();
		    	
		}
		
		// TODO Auto-generated method stub
	 	//System.out.println(dtf.format(now()) + " SailLogonActionEngine:SampleResult:end");

		sampleResult.sampleEnd();
 		sampleResult.setResponseContent(responseBuilder.toString());
		return sampleResult;
	}

	public String getPath() {
		return Path;
	}

	@Override
	public void stopExecute() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSSSSS");
		// TODO Auto-generated method stub
		System.out.println(dtf.format(now()) + " SailLogonActionEngine:stopExecute:");
	}
	
	private void FailedTest( ) throws GeneralSecurityException, IOException, ODataException, URISyntaxException, NeotysAPIException, InterruptedException {
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
