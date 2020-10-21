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
public final class TC {
    private final String messageHeader;     //2
    private final String protocolVersion;   //2
    private final String userID;            //8
    private final String password;          //8
    private final String sessionID;         //4
    private final String time;              //6
    private final String exchangeMessageID; //6
    private final String inactivityInterval;//2
    private final String messageTypesNumber;//2
    private final String messageTypes;      //2

    public TC(String clientName, boolean isFirst) throws IOException {
        this.messageHeader = "TC";
        this.protocolVersion = Ini.getValue(clientName, "protocol version");
        this.userID = Ini.getValue(clientName, "userID");
        this.password = Ini.getValue(clientName, "password");
        this.sessionID = "    ";
        this.time =Ini.getValue(clientName, "TC").substring(24, 30);
        this.inactivityInterval =Ini.getValue(clientName, "TC").substring(30, 32);
        this.exchangeMessageID = Ini.getValue(clientName, "TC").substring(32, 38);
        this.messageTypesNumber = Ini.getValue(clientName, "TC").substring(38, 40);
        this.messageTypes = Ini.getValue(clientName, "TC").substring(40, Integer.parseInt(this.messageTypesNumber)*2+40);
     // this.messageTypes = "ERIEIXKBKDKEKFKGKIKJKMKNKOKPKSKXKYKZLAMBMMMUNCNDNGNMNPNTNUNXNYNZPNPUTATETHTKTLTMTOTTBP";
    }
    
    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return messageHeader + getProtocolVersion() + getUserID() + getPassword() + getSessionID() + time + exchangeMessageID + inactivityInterval + messageTypesNumber + messageTypes;
    }

    /**
     * @return the protocolVersion
     */
    public String getProtocolVersion() {
        return protocolVersion;
    }

    /**
     * @return the userID
     */
    public String getUserID() {
        return userID;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return the sessionID
     */
    public String getSessionID() {
        return sessionID;
    }
}
