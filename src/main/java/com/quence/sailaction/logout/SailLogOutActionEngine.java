package com.quence.sailaction.logout;

import java.io.IOException;
import java.util.List;

import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;
import com.neotys.extensions.action.engine.Context;
import com.neotys.extensions.action.engine.SampleResult;
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

}
