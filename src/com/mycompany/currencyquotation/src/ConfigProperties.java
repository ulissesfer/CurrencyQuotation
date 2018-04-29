/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.currencyquotation.src;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author ulisses.fernandes
 */
public final class ConfigProperties {
    private static final ConfigProperties INSTANCE = new ConfigProperties();
    
    private ConfigProperties() {
    }
    
    public static ConfigProperties getInstance(){
        return INSTANCE;
    }
    
    public Properties loadConfigProperties() throws FileNotFoundException, IOException {
        
        Properties props = new java.util.Properties();
        FileInputStream fis = new FileInputStream("config.properties");
        props.load(fis);
        
        return props;
    }
}
