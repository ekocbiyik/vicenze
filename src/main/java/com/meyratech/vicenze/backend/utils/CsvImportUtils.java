package com.meyratech.vicenze.backend.utils;

import com.meyratech.vicenze.backend.model.CsvModel;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * ekocbiyik on 09.09.2019
 */
public class CsvImportUtils {

    public static List<CsvModel> uploadInvoiceCsv(InputStream inputStream) {

        List<CsvModel> csvList = new ArrayList<>();
        try {
            Reader reader = new InputStreamReader(inputStream);
            CSVParser csvParser = new CSVParser(reader, CSVFormat.EXCEL
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withIgnoreEmptyLines()
                    .withIgnoreSurroundingSpaces()
                    .withTrim()
            );

            csvParser.forEach(l -> {
                CsvModel c = new CsvModel();
                c.setPROJECT(l.get("PROJECT"));
                c.setVENDOR(l.get("VENDOR"));
                c.setEVENT_TYPE(l.get("EVENT_TYPE"));
                c.setMAIN_ITEM(l.get("MAIN ITEM"));
                c.setBOOK(l.get("BOOK"));
                c.setTRANSACTION(l.get("TRANSACTION"));
                c.setNUMBER(l.get("NUMBER"));
                c.setCODE(l.get("CODE"));
                c.setEXPLANATION(l.get("EXPLANATION"));
                c.setAMOUNT(l.get("AMOUNT").replaceAll("€", "").replaceAll(",", "").trim());
                c.setUNIT_PRICE(l.get("UNIT_PRICE").replaceAll("€", "").replaceAll(",", "").trim());
                c.setTOTAL_AMOUNT(l.get("TOTAL_AMOUNT").replaceAll("€", "").replaceAll(",", "").trim());
                c.setDATE(l.get("DATE"));

                csvList.add(c);
            });
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return csvList;
        }
    }

}
