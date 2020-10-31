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
public class KE extends BusinessMessage {

    private final String group1;
    private final String clientIDCodeQualifier;
    private final String clientIDCode;
    private final String group2;

    /**
     *
     * @param g1
     * @param clientName
     * @param mt
     * @param g2
     * @throws IOException
     */
    public KE(String g1, String clientName, String mt, String g2) throws IOException {
        super(clientName, mt);
        group1 = g1;
        clientIDCodeQualifier = Ini.getValue(clientName, "clientIDCodeQualifier");
        clientIDCode = Ini.getValue(clientName, "clientIDCode");
        group2 = g2;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return this.messageType + this.userTime + this.traderID + this.userSequenceID + this.group1 + this.clientIDCodeQualifier + this.clientIDCode + this.group2;
    }
}
