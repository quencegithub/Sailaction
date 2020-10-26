package com.quence.sailaction.logout;

import static java.time.LocalDateTime.now;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.olingo.odata2.api.exception.ODataException;

import com.google.common.collect.Multiset.Entry;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;
import com.neotys.extensions.action.engine.Context;
import com.neotys.extensions.action.engine.SampleResult;
import com.neotys.rest.dataexchange.client.DataExchangeAPIClient;
import com.neotys.rest.dataexchange.client.DataExchangeAPIClientFactory;
import com.neotys.rest.dataexchange.model.ContextBuilder;
import com.neotys.rest.dataexchange.model.EntryBuilder;
import com.neotys.rest.dataexchange.model.StatusBuilder;
import com.neotys.rest.error.NeotysAPIException;
import com.quence.sailaction.common.Client;

public class SailLogOutActionEngine implements ActionEngine {
    private int BD;
    private int QP;
    private int OE;
	@Override
	public SampleResult execute(Context context, List<ActionParameter> parameters) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSSSSS");
		final SampleResult sampleResult = new SampleResult();
		final StringBuilder requestBuilder = new StringBuilder();
		final StringBuilder responseBuilder = new StringBuilder();
		Client c = (Client)context.getCurrentVirtualUser().get("client");
	     BD = (int) context.getCurrentVirtualUser().get("BD");
	     OE = (int) context.getCurrentVirtualUser().get("OE");
	     QP = (int) context.getCurrentVirtualUser().get("QP");
		sampleResult.sampleStart();
	    try {
			Entries();
			System.out.println(dtf.format(now()) + " SailLogOutActionEngine:SampleResult: # of sent messages: ");
	    	System.out.println(dtf.format(now()) + " SailLogOutActionEngine:SampleResult: OE-BD-QP:" + OE + "-" + BD + "-" + QP);
		} catch (GeneralSecurityException | IOException | ODataException | URISyntaxException | NeotysAPIException
				| InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}    	
		try {
			while(!c.getLogMessages().isEmpty()){ 
				Thread.sleep(1000); 
				System.out.println(dtf.format(now())+ " SailLogOutActionEngine:SampleResult: # messaggi in coda :"+ c.getLogMessages().size());
			}
 		    c.sendLogout();
 		    Thread.sleep(2000);
		    c.disconnect();
		    } catch (IOException e) {
			// TODO Auto-generated catch block
		    
				
			e.printStackTrace();
		} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		sampleResult.sampleEnd();
		
		sampleResult.setRequestContent(requestBuilder.toString());
		sampleResult.setResponseContent(responseBuilder.toString());
		return sampleResult;
	}

	@Override
	public void stopExecute() {
		// TODO Auto-generated method stub
		
	}

	public void Entries() throws GeneralSecurityException, IOException, ODataException, URISyntaxException, NeotysAPIException, InterruptedException {
	        final List<Entry> entriesToSend = new ArrayList<>();
			final DataExchangeAPIClient client = DataExchangeAPIClientFactory.newClient("http://localhost:7400/DataExchange/v1/Service.svc/","apiKeyToSend");	
			final EntryBuilder BDe = new EntryBuilder(Arrays.asList("BD"));
			final EntryBuilder QPe = new EntryBuilder(Arrays.asList("QP"));
			final EntryBuilder OEe = new EntryBuilder(Arrays.asList("OE"));
				//BDe.unit("units");
				BDe.value((double) BD);
				//QPe.unit("units");
				QPe.value((double) QP);
				//OEe.unit("units");
				OEe.value((double) OE);
				client.addEntry(BDe.build());
				client.addEntry(QPe.build());
				client.addEntry(OEe.build());
 			
		}
}
