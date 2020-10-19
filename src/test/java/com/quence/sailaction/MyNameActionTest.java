package com.quence.sailaction;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

//import com.quence.sailaction.send.SailSendMsgAction;
import com.quence.sailaction.logon.SailLogonAction;
import com.quence.sailaction.logout.SailLogOutAction;

public class MyNameActionTest {
	@Test
	public void shouldReturnType() {
		final SailLogonAction action = new SailLogonAction();
		assertEquals("Sail logon", action.getType());
		final SailLogOutAction action2 = new SailLogOutAction();
		assertEquals("Sail logout", action2.getType());
	//	final SailSendMsgAction action3 = new SailSendMsgAction();
		//assertEquals("Sail SendMsg", action3.getType());
	}

}
