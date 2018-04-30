/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.currencyquotation.src;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ulisses.fernandes
 */
public class CurrencyQuotationFileTest {

    public CurrencyQuotationFileTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void checkBusinessDayTest() throws Exception {
        CurrencyQuotationFile currencyQuotationFile = new CurrencyQuotationFile();

        Calendar initDate = Calendar.getInstance();
        Calendar expectedDate = currencyQuotationFile.verifyBusinessDay(initDate);

        boolean dayIsSunday = initDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
        boolean dayIsSaturday = initDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;

        if (dayIsSunday || dayIsSaturday) {
            Assert.assertTrue(expectedDate.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY);
        } else {
            Assert.assertTrue(initDate == expectedDate);
        }
    }

    @Test
    public void downloadCurrencyQuotationFile() throws Exception {
        CurrencyQuotationFile currencyQuotationFile = new CurrencyQuotationFile();
        Properties props = ConfigProperties.getInstance().loadConfigProperties();
        String currencyFileDir = props.getProperty("currencyFile.dir");
        
        String urlToDownload = props.getProperty("url.download");

        String fileName = "20180424";
        String fileExtension = props.getProperty("file.extension");
        String destinationFile = String.format("%s%s%s", currencyFileDir, fileName, fileExtension);

        File currencyDir = new File(destinationFile);

        URL url = new URL(String.format("%s%s%s", urlToDownload, fileName, fileExtension));

        currencyQuotationFile.downloadQuotationFile(url, currencyDir.getPath());

        File file = currencyQuotationFile.getQuotationFile("24/04/2018", new File(currencyFileDir));

        Assert.assertTrue(file != null);
        
        boolean success = (new File(destinationFile)).delete();
    }

    @Test
    public void validDateQuotationTest() throws Exception {
        CurrencyQuotationFile currencyQuotationFile = new CurrencyQuotationFile();
        String date = "24/04/2018";
        
        String fileNameBaseadOnDate = currencyQuotationFile.getQuotationFileName(date);
        
        Assert.assertTrue(fileNameBaseadOnDate.equals("20180424"));
    }
    
    @Test
    public void loadCurrencyQuotationFileTest() throws Exception {
        CurrencyQuotationFile currencyQuotationFile = new CurrencyQuotationFile();
        String date = "24/04/2018";
        
        currencyQuotationFile.loadCurrencyFile(date);
        
        Assert.assertTrue(!currencyQuotationFile.getCurrentyList().isEmpty());
        Assert.assertTrue(!currencyQuotationFile.getCurrencyNameList().isEmpty());
        
        Properties props = ConfigProperties.getInstance().loadConfigProperties();
        String currencyFileDir = props.getProperty("currencyFile.dir");
        String fileExtension = props.getProperty("file.extension");
        File file = new File(String.format("%s%s%s", currencyFileDir, "20180424", fileExtension));
        file.delete();
    }
}
