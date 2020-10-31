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
public class TD {

    private final String messageHeader;
    private final String userID;
    private final String sessionID;

    public TD(String clientName, String sID) throws IOException {
        this.messageHeader = "TD";
        this.userID = Ini.getValue(clientName, "userID");
        this.sessionID = sID;
    }

    @Override
    public String toString() {
        return messageHeader + userID + sessionID;
    }
}
