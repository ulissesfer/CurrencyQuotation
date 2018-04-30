/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.currencyquotation.src;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author ulisses.fernandes
 */
public class CalculeTest {
    static Currency currUsd;
    static Currency currEur;
    static Currency currBra;
    static Currency incorrectCurrency;
    static Calcule calcule; 
    
    public CalculeTest() {
    }
    
    @Before
    public void setUp() throws Exception {
        currUsd = new Currency("001", "A", "USD", BigDecimal.ZERO, BigDecimal.ZERO);
        currEur = new Currency("002", "A", "EUR", BigDecimal.ZERO, BigDecimal.ZERO);
        currBra = new Currency("003", "A", "BRA", BigDecimal.ZERO, BigDecimal.ZERO);
        incorrectCurrency = new Currency("004", "C", "AAA", BigDecimal.ZERO, BigDecimal.ZERO);
        calcule = new Calcule();
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void getCurrencyFromListTest() throws Exception {      
        List<Currency> listCurrency = new ArrayList<Currency>();
        listCurrency.add(currUsd);
        listCurrency.add(currEur);
        listCurrency.add(currBra);
        
        Currency usd = calcule.getCurrencyFromList(listCurrency, "USD");
        Currency eur = calcule.getCurrencyFromList(listCurrency, "EUR");
        Currency bra = calcule.getCurrencyFromList(listCurrency, "BRA");
        
        Assert.assertTrue(usd != null);
        Assert.assertTrue(eur != null);
        Assert.assertTrue(bra != null);
    }
    
    @Test
    public void quotationCalculeTest() throws Exception {
        currEur.setPurchaseValue(new BigDecimal("2.0000"));
        currBra.setPurchaseValue(new BigDecimal("1.0000"));

        List<Currency> currencyList = new ArrayList<Currency>();
        currencyList.add(currEur);
        currencyList.add(currBra);
        
        BigDecimal braToEur = calcule.quotationCalcule(currBra.getName(), currEur.getName(), 2, currencyList);
        Assert.assertEquals(braToEur, new BigDecimal("1.00"));
        
        BigDecimal eurToBra = calcule.quotationCalcule(currEur.getName(), currBra.getName(), 2, currencyList);
        Assert.assertEquals(eurToBra, new BigDecimal("4.00"));
    }
    
    @Test
    public void validFromCurrency() throws Exception {
        List<String> nameCurrencyList = new ArrayList<String>();
        nameCurrencyList.add(currUsd.getName());
        nameCurrencyList.add(currBra.getName());
        nameCurrencyList.add(currEur.getName());
        nameCurrencyList.add(incorrectCurrency.getName());
        
        try {
            calcule.dataValidate(incorrectCurrency.getName(), currUsd.getName(), 2, nameCurrencyList);
        } catch(Exception e) {
            Assert.assertEquals(e.getMessage(), "Currency ABS it's not valid");
        }
    }

    @Test
    public void validToCurrency() throws Exception {
        List<String> nameCurrencyList = new ArrayList<String>();
        nameCurrencyList.add(currUsd.getName());
        nameCurrencyList.add(currBra.getName());
        nameCurrencyList.add(currEur.getName());
        nameCurrencyList.add(incorrectCurrency.getName());
        
        try {
            calcule.dataValidate(currUsd.getName(), incorrectCurrency.getName(), 2, nameCurrencyList);
        } catch(Exception e) {
            Assert.assertEquals(e.getMessage(), "Currency ABS it's not valid");
        }
    }

    @Test
    public void validValueLessThanZero() throws Exception {
        List<String> nameCurrencyList = new ArrayList<String>();
        nameCurrencyList.add(currUsd.getName());
        nameCurrencyList.add(currBra.getName());
        nameCurrencyList.add(currEur.getName());
        nameCurrencyList.add(incorrectCurrency.getName());

        try {
            calcule.dataValidate(currBra.getName(), currEur.getName(), -2, nameCurrencyList);
        } catch (Exception e) {
            Assert.assertEquals(e.getMessage(), "Value is smaller than zero");
        }
    }
    
    @Test
    public void currencyQuotationTest() throws Exception {
        currEur.setPurchaseValue(new BigDecimal("2.0000"));
        currBra.setPurchaseValue(new BigDecimal("1.0000"));
        
        BigDecimal expected = calcule.currencyQuotation(currEur.getName(), currUsd.getName(), 2, "27/04/2018");

        Assert.assertTrue(expected != BigDecimal.ZERO);
    }
}