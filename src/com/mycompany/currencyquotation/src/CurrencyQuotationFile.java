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
    private final String LIMITER = ";";
    private List<Currency> currencyList;
    private List<String> nameCurrencyList;
    private static Logger log;

    public CurrencyQuotationFile() throws Exception {
        this.currencyList = new ArrayList<Currency>();
        this.nameCurrencyList = new ArrayList<String>();
        this.log = Logs.getInstance().getLogger();
    }

    public List<Currency> getCurrentyList() {
        return currencyList;
    }

    public List<String> getCurrencyNameList() {
        return nameCurrencyList;
    }

    /**
     * Realiza a leitura do arquivo de moedas
     * @param dateQuotation
     * @throws Exception 
     */
    public void loadCurrencyFile(String dateQuotation) throws Exception {
        Properties props = ConfigProperties.getInstance().loadConfigProperties();
        String currencyFileDir = props.getProperty("currencyFile.dir");

        File currencyDir = new File(currencyFileDir);
        if (!currencyDir.exists()) {
            new File(currencyFileDir).mkdirs();
        }

        File currencyFile = getQuotationFile(dateQuotation, currencyDir);
        String fileName = getQuotationFileName(dateQuotation);
        String urlToDownload = props.getProperty("url.download");
        String fileExtension = props.getProperty("file.extension");

        if (Boolean.FALSE.equals(currencyFile.exists())) {
            log.info("Necessario download do arquivo de cotacao");
            URL url = new URL(String.format("%s%s%s", urlToDownload, fileName, fileExtension));
            String newFile = String.format("%s%s%s", currencyFileDir, fileName, fileExtension);
            try {
                downloadQuotationFile(url, newFile);
            } catch (Exception e) {
                throw new Exception("Download file error");
            }
            currencyFile = getQuotationFile(dateQuotation, currencyDir);
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
     * @param dateQuotation
     * @param currencyDir
     * @return File
     * @throws Exception 
     */
    protected File getQuotationFile(String dateQuotation, File currencyDir) throws Exception {
        File currencyFile = new File("");
        String fileName = getQuotationFileName(dateQuotation);

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
     * @param dateQuotation
     * @return String
     * @throws ParseException
     * @throws Exception 
     */
    protected String getQuotationFileName(String dateQuotation) throws ParseException, Exception {
        String fileName = "";

        SimpleDateFormat dateFormatIn = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar = Calendar.getInstance();
        try {
            dateFormatIn.setLenient(false);
            calendar.setTime(dateFormatIn.parse(dateQuotation));
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
    protected void downloadQuotationFile(URL url, String targetPath) throws FileNotFoundException, IOException {
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
    protected Calendar verifyBusinessDay(Calendar date) {
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
