package com.mycompany.currencyquotation.src;

import java.math.BigDecimal;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ulisses.fernandes
 */
public class Currency {

    private String codCurrency;
    private String type;
    private String name;
    private BigDecimal purchaseValue;
    private BigDecimal saleValue;
    
    public Currency() {}

    public Currency(String codCurrency, String type, String name, BigDecimal purchaseValue, BigDecimal saleValue) {
        this.codCurrency = codCurrency;
        this.type = type;
        this.name = name;
        this.purchaseValue = purchaseValue;
        this.saleValue = saleValue;
    }

    public void setCodCurrency(String codCurrency) {
        this.codCurrency = codCurrency;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPurchaseValue(BigDecimal purchaseValue) {
        this.purchaseValue = purchaseValue;
    }

    public void setSaleValue(BigDecimal saleValue) {
        this.saleValue = saleValue;
    }

    public String getCodCurrency() {
        return codCurrency;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPurchaseValue() {
        return purchaseValue;
    }

    public BigDecimal getSaleValue() {
        return saleValue;
    }

}
