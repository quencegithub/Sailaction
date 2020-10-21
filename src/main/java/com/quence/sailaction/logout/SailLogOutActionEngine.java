package com.quence.sailaction.logout;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

import org.apache.olingo.odata2.api.exception.ODataException;

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

	@Override
	public SampleResult execute(Context context, List<ActionParameter> parameters) {
		
		final SampleResult sampleResult = new SampleResult();
		final StringBuilder requestBuilder = new StringBuilder();
		final StringBuilder responseBuilder = new StringBuilder();
		Client c = (Client)context.getCurrentVirtualUser().get("client");
   
		sampleResult.sampleStart();
		/*try {
			//Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		try {
 		    c.sendLogout();
 		    Thread.sleep(2000);
		    c.disconnect();
		    } catch (IOException e) {
			// TODO Auto-generated catch block
		    	try {
					FailedTest();
				} catch (GeneralSecurityException | IOException | ODataException | URISyntaxException
						| NeotysAPIException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
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
