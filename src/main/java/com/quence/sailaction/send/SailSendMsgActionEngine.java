package com.quence.sailaction.send;


import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
 
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;
import com.neotys.extensions.action.engine.Context;
import com.neotys.extensions.action.engine.SampleResult;
import com.quence.sailaction.common.Client;

public class SailSendMsgActionEngine  implements ActionEngine{
	
	private ArrayList<byte[]> messages;
	private Client c ;
    private	int size;
    private int cycle;
    private int generate;
    private int delay;

	@Override
	public SampleResult execute(Context context, List<ActionParameter> parameters) {
		final SampleResult sampleResult = new SampleResult();
		final StringBuilder requestBuilder = new StringBuilder();
		final StringBuilder responseBuilder = new StringBuilder();
		Integer iterationNumber = context.getCurrentVirtualUser().getCurrentIteration();
		c = (Client)context.getCurrentVirtualUser().get("client");
		size = (int) context.getCurrentVirtualUser().get("size");
		messages =  (ArrayList<byte[]>) context.getCurrentVirtualUser().get("messages");
	    generate = (int) context.getCurrentVirtualUser().get("generate");
		for (ActionParameter temp:parameters) {
		
			switch (temp.getName().toLowerCase()) {
			case "cycle":							//If the current parameter is a message 
				cycle=  Integer.valueOf(temp.getValue()); 		//Initialize the message value
				break;		
			case "delay":							//If the current parameter is a message 
				delay=  Integer.valueOf(temp.getValue()); 		//Initialize the message value
				break;	
			default :
				break;
			}
		}

		sampleResult.sampleStart();
		 		 
		if (cycle != 0 ) { 
			System.out.println(" the number of of cycle is : " +cycle +" the size of the message is : "+ size+" the iteration number is : " + iterationNumber);
			if((cycle*size)+1==iterationNumber) {
				System.out.println(" StopVue start here is true : ");
				System.out.println(" the number of of cycle is : " +cycle +" the size of the message is : "+ size+" the iteration number is : " + iterationNumber);
				context.getVariableManager().setValue("stopVU", "true");
			} else {
				if (generate < size ) {
 		    	    try {
		    			c.sendMsg(messages.get(generate));
						System.out.println(" the number of of cycle is : " +cycle +" the size of the message is : "+ size+" the iteration number is : " + iterationNumber);

		    			context.getCurrentVirtualUser().put("generate",generate+1);
		    			Thread.sleep(delay);
  		    		} catch (IOException e) {
  		    			System.out.println("messege Not SENT at the iteration :  "+ iterationNumber);	
 		    		} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}       	   
               } else {
		        	int reset = 0 ;
	    			context.getCurrentVirtualUser().put("generate",reset);	
	    		    generate = (int) context.getCurrentVirtualUser().get("generate");
	    			try {
						c.sendMsg(messages.get(generate));
						System.out.println(" the number of of cycle is : " +cycle +" the size of the message is : "+ size+" the iteration number is : " + iterationNumber);
		    			Thread.sleep(delay);

		    			context.getCurrentVirtualUser().put("generate",generate+1);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		       }
			} 
		} else 	{
			
			if (generate < size ) {
		    	    try {
	    			c.sendMsg(messages.get(generate));
					System.out.println(" the number of of cycle is : " +cycle +" the size of the message is : "+ size+" the iteration number is : " + iterationNumber);
	    			context.getCurrentVirtualUser().put("generate",generate+1);
	    			Thread.sleep(delay);
		    		} catch (IOException e) {
		    			System.out.println("messege Not SENT at the iteration :  "+ iterationNumber);	
		    		} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}       	   
           } else {
	        	int reset = 0 ;
    			context.getCurrentVirtualUser().put("generate",reset);	
    		    generate = (int) context.getCurrentVirtualUser().get("generate");
    			try {
					c.sendMsg(messages.get(generate));
					System.out.println(" the number of of cycle is : " +cycle +" the size of the message is : "+ size+" the iteration number is : " + iterationNumber);
	    			Thread.sleep(delay);

	    			context.getCurrentVirtualUser().put("generate",generate+1);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
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

}
