package com.quence.sailaction.common;


import com.quence.sailaction.logon.SailLogonActionEngine;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rober
 */
public class Ini {

   
    
    public static String getValue(String section, String key) throws FileNotFoundException, IOException {
    	
        BufferedReader buffer = new BufferedReader(new FileReader(SailLogonActionEngine.Path));
        String line = buffer.readLine();
        while (line != null) {
            if (line.startsWith("[") && line.endsWith("]") && line.substring(1, line.length()-1).equals(section)) {
                line = buffer.readLine();
                while (line != null && !(line.startsWith("[") && line.endsWith("]"))) {
                    if (line.split("=")[0].equals(key)) {
                        return line.split("=")[1];
                    }
                    line = buffer.readLine();
                }
            } else {
                line = buffer.readLine();
            }
        }
        buffer.close();
        return null;
    }
}