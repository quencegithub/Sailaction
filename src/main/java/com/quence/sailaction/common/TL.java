/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.quence.sailaction.common;

/**
 *
 * @author rober
 */
public class TL {

    private final String messageHeader;
    private final String currentSessionID;
    private final String userSequenceID;

    /**
     *
     */
    public TL() {
        this.messageHeader = "TL";
        this.currentSessionID = "4444";
        this.userSequenceID = "88888888";
    }

    @Override
    public String toString() {
        return messageHeader + currentSessionID + userSequenceID;
    }
}
