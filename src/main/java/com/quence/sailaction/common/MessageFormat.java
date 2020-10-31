/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.quence.sailaction.common;

import java.time.LocalDateTime;

/**
 *
 * @author rober
 */
public abstract class MessageFormat {

    protected String messageLen;

    /**
     *
     * @return
     */
    protected abstract int getLength();

    /**
     *
     * @return
     */
    protected abstract int getMessageSize();

    /**
     *
     * @param my_int
     * @param size
     * @return
     * @throws IOException
     */
    /*public static int byteArrayToInt(byte[] int_bytes) throws IOException {
        String s = "";
        for (int i=0;i<int_bytes.length; i++) {
            s += ((char)int_bytes[i]);
        }
        return Integer.parseInt(s);
    }*/
 /*
    public static String intToString(int my_int, int size) throws IOException {
        String s = Integer.toString(my_int);
        for (int i=0;i<size-s.length();i++) {
            s = "0" + s;
        }
        return s;
    }
     */
    /**
     *
     * @param withNano
     * @return
     */
    public static String getTime(boolean withNano) {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();
        int second = now.getSecond();
        long nano = now.getNano();

        String time = String.format("%02d%02d%02d", hour, minute, second);
        if (withNano) {
            time += String.valueOf(nano / 1000);
        }

        return time;
    }

    protected String get4BytesSize() {
        String s = Integer.toString(this.getMessageSize());
        for (int i = 0; i < 4 - String.valueOf(this.getMessageSize()).length(); i++) {
            s = "0" + s;
        }
        return s;
    }
}
