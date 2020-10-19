/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.quence.sailaction.common;


import java.io.IOException;

/**
 *
 * @author rober
 */
public abstract class BusinessMessage {
    protected final String messageType;
    protected final String userTime;
    protected final String traderID;
    public static String userSequenceID;

    public BusinessMessage(String clientName, String mt) throws IOException {
        this.messageType = mt;
        this.userTime = MessageFormat.getTime(true);
        this.traderID = Ini.getValue(clientName, "traderID");
    }
    
    protected String getHeader() {
        return messageType + userTime + traderID + userSequenceID;
    }
}
