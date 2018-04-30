/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.currencyquotation.src;

import com.mycompany.currencyquotation.util.Logs;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author ulisses.fernandes
 */
public class Calcule {
    private static Logger log;
    
    public Calcule() throws Exception {
        this.log = Logs.getInstance().getLogger();
    }

    /**
     * Responsável por realizar o cálculo das cotações das moedas
     * @param from
     * @param to
     * @param value
     * @param dateQuotation
     * @return BigDecimal
     * @throws Exception 
     */
    public BigDecimal currencyQuotation(String from, String to, Number value, String dateQuotation) throws Exception {
        BigDecimal retVal = new BigDecimal("0");

        CurrencyQuotationFile currencyQuotationFile = new CurrencyQuotationFile();
        
        try {
            log.info("Carrega arquivo de cotacoes");
            currencyQuotationFile.loadCurrencyFile(dateQuotation);
        } catch (Exception e) {
            log.warning(e.getMessage());
            e.printStackTrace();
        }

        List<Currency> currencyList = currencyQuotationFile.getCurrentyList();
        List<String> nameCurrencyList = currencyQuotationFile.getCurrencyNameList();
        
        dataValidate(from, to, value, nameCurrencyList);

        retVal = quotationCalcule(from, to, value, currencyList);
        
        return retVal;
    }

    /**
     * Valida os dados de entrada passada
     * @param from
     * @param to
     * @param value
     * @param nameCurrencyList
     * @throws Exception 
     */
    protected void dataValidate(String from, String to, Number value, List<String> nameCurrencyList) throws Exception {
        String errorMsg = "";
        Boolean hasError = false;
        
        log.info("Validacao dos dados de entrada");
        
        if(Boolean.FALSE.equals(nameCurrencyList.contains(from))) {
            hasError = true;
            errorMsg = String.format("Currency %s it's not valid", from);
        }
        
        if(Boolean.FALSE.equals(nameCurrencyList.contains(to))) {
            hasError = true;
            errorMsg = String.format("Currency %s it's not valid", to);
        }
        
        BigDecimal currencyValue = new BigDecimal(value.toString());
        if(currencyValue.compareTo(BigDecimal.ZERO) < 0) {
            hasError = true;
            errorMsg = String.format("Value is smaller than zero", to);
        }

        if(hasError) {            
            log.info("Erro na validacao de dados de entrada");
            log.warning(errorMsg);
            throw new Exception(errorMsg);
        }
    } 

    /**
     * Realiza o cálculo das cotações conforme regras do Banco Central,
     * verificando o tipo da moeda passada. O valor final é arredondado em duas
     * casas decimais. Ex: 2.390045 => 2.39
     * @param from
     * @param to
     * @param value
     * @param currencyList
     * @return BigDecimal
     */
    protected BigDecimal quotationCalcule(String from, String to, Number value, List<Currency> currencyList) {
        log.info("Calcula cotacao das moedas");
        log.info("De " + from +" ====> Para " + to);

        BigDecimal retVal = null;

        BigDecimal valueQuantity = new BigDecimal(value.doubleValue());
        Currency fromCurr = getCurrencyFromList(currencyList, from);
        Currency toCurr = getCurrencyFromList(currencyList, to);
        
        BigDecimal valFrom = fromCurr.getPurchaseValue();
        BigDecimal valTo = toCurr.getPurchaseValue();

        switch(toCurr.getType()) {
            case "A":
                log.info("Moeda destino do tipo A");
                retVal = valFrom.divide(valTo, RoundingMode.HALF_EVEN);
                retVal = retVal.multiply(valueQuantity);
                break;
            case "B":
                log.info("Moeda destino do tipo B");
                retVal = valFrom.multiply(valTo);
                retVal = retVal.multiply(valueQuantity);
                break;
            default:
                log.info("Nenhuma moeda destino encontrada");
                retVal = new BigDecimal("0");
        }

        log.info("Valor arredondado em 2 casas decimais");
        return retVal.setScale(2, RoundingMode.UP);
    }

    /**
     * Recupera a moeda da lista de moedas
     * @param currencyList
     * @param currencyName
     * @return Currency
     */
    protected Currency getCurrencyFromList(List<Currency> currencyList, String currencyName) {
        log.info("Busca moeda na lista de moedas");
        Currency retVal = null;
        
        for(Currency curr : currencyList) {
            if(curr.getName().equals(currencyName)) {
                retVal = curr;
                log.info("Encontrado moeda " + retVal.getName());
                break;
            }
        }
        
        return retVal;
    }
}