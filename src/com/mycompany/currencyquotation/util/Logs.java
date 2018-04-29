/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.currencyquotation.util;

import com.mycompany.currencyquotation.src.ConfigProperties;
import java.io.File;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author ulisses.fernandes
 */
public class Logs {
    public Logger logger;
    FileHandler fh;

    private static final Logs INSTANCE = new Logs();
    
    private Logs() {
    }
    
    public static Logs getInstance(){
        return INSTANCE;
    }
    
    public Logger getLogger() throws Exception {
        Properties props = ConfigProperties.getInstance().loadConfigProperties();
        String logFile = props.getProperty("log.file");
        
        File f = new File(logFile);
        if(!f.exists()) {
            f.createNewFile();
        }
        
        fh = new FileHandler(logFile, true);
        logger = Logger.getLogger("test");
        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        
        return logger;
    }
}
