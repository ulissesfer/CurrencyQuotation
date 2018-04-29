/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.currencyquotation.src;

import com.mycompany.currencyquotation.util.Logs;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 *
 * @author ulisses.fernandes
 */
public class CurrencyQuotationFile {

    private final String URL_TO_DOWNLOAD = "http://www4.bcb.gov.br/Download/fechamento/%s.csv";
    private final String LIMITER = ";";
    private List<Currency> currencyList;
    private List<String> nameCurrencyList;
    private static Logger log;

    public CurrencyQuotationFile() {
        this.currencyList = new ArrayList<Currency>();
        this.nameCurrencyList = new ArrayList<String>();
    }

    public List<Currency> getCurrentyList() {
        return currencyList;
    }

    public List<String> getCurrencyNameList() {
        return nameCurrencyList;
    }

    /**
     * Realiza a leitura do arquivo de moedas
     * @param quotation
     * @throws Exception 
     */
    public void loadCurrencyFile(String quotation) throws Exception {
        log = Logs.getInstance().getLogger();
        Properties props = ConfigProperties.getInstance().loadConfigProperties();
        String userDir = props.getProperty("user.dir");

        File currencyDir = new File(userDir);
        if (!currencyDir.exists()) {
            new File(userDir).mkdirs();
        }

        File currencyFile = getQuotationFile(quotation, currencyDir);
        String fileName = getQuotationFileName(quotation);

        if (Boolean.FALSE.equals(currencyFile.exists())) {
            log.info("Necessario download do arquivo de cotacao");
            URL url = new URL(String.format(URL_TO_DOWNLOAD, fileName));
            try {
                downloadQuotationFile(url, currencyDir.getPath());
            } catch (Exception e) {
                throw new Exception("Download file error");
            }
            currencyFile = getQuotationFile(quotation, currencyDir);
        }

        if (currencyFile.exists()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(currencyFile.getPath())));
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] currencyData = line.split(LIMITER);

                String cod = currencyData[1];
                String type = currencyData[2];
                String name = currencyData[3];
                BigDecimal purchaseValue = new BigDecimal(currencyData[4].replace(",", "."));
                BigDecimal saleValue = new BigDecimal(currencyData[5].replace(",", "."));
                currencyList.add(new Currency(cod, type, name, purchaseValue, saleValue));
                
                nameCurrencyList.add(name);
            }
            reader.close();

        } else {
            throw new Exception("Quotation file doesn't exist in data base");
        }
    }

    /**
     * Recupera o arquivo de cotações do diretorio especificado nas propriedades
     * @param quotation
     * @param currencyDir
     * @return File
     * @throws Exception 
     */
    private static File getQuotationFile(String quotation, File currencyDir) throws Exception {
        File currencyFile = new File("");
        String fileName = getQuotationFileName(quotation);

        File fileList[] = currencyDir.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].getName().contains(fileName)) {
                currencyFile = fileList[i];
            }
        }

        return currencyFile;
    }

    /**
     * Monta o nome do arquivo conforme a data passada
     * @param quotation
     * @return String
     * @throws ParseException
     * @throws Exception 
     */
    private static String getQuotationFileName(String quotation) throws ParseException, Exception {
        String fileName = "";

        SimpleDateFormat dateFormatIn = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar = Calendar.getInstance();
        try {
            dateFormatIn.setLenient(false);
            calendar.setTime(dateFormatIn.parse(quotation));
        } catch (ParseException e) {
            throw new Exception("Incorrect date");
        }

        calendar = verifyBusinessDay(calendar);

        SimpleDateFormat dataFormatOut = new SimpleDateFormat("yyyyMMdd");
        fileName = dataFormatOut.format(calendar.getTime());

        return fileName;
    }

    /**
     * Realiza download do arquivo de cotações
     * @param url
     * @param targetPath
     * @throws FileNotFoundException
     * @throws IOException 
     */
    private void downloadQuotationFile(URL url, String targetPath) throws FileNotFoundException, IOException {
        InputStream is = url.openStream();
        FileOutputStream fos = new FileOutputStream(targetPath);

        int umByte = 0;
        while ((umByte = is.read()) != -1) {
            fos.write(umByte);
        }

        is.close();
        fos.close();
    }

    /**
     * Verifica se a data é dia útil, caso não, aponta para o primeiro dia útil
     * anterior. Não leva em conta feriados.
     * @param date
     * @return Calendar
     */
    private static Calendar verifyBusinessDay(Calendar date) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        log.info("Verifica dia da semana");
        if (date.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            date.add(Calendar.DAY_OF_MONTH, -2);
            log.info("Dia da semana ==> Domingo");
        }

        if (date.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            date.add(Calendar.DAY_OF_MONTH, -1);
            log.info("Dia da semana ==> Sabado");
        }
        
        log.info("Utiliza a data: " + df.format(date.getTime()));

        return date;
    }

}
