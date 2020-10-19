package com.quence.sailaction.common;

import java.io.*;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Keyboard {
    static BufferedReader buffer;
    static {
        InputStreamReader bis = new InputStreamReader(System.in);
        buffer = new BufferedReader(bis);
    }

    public static String readline() throws IOException {
        /*try {
            sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Keyboard.class.getName()).log(Level.SEVERE, null, ex);
        */
        return buffer.readLine();
    }
}
