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
public class TK {

    private final String messageHeader;
    private final String currentSessionID;
    private final String userSequenceID;

    public TK() {
        this.messageHeader = "TK";
        this.currentSessionID = " 841";
        this.userSequenceID = "00000001";
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return messageHeader + currentSessionID + userSequenceID;
    }
}
