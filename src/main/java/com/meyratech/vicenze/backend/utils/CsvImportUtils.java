package com.meyratech.vicenze.backend.utils;

import com.meyratech.vicenze.backend.model.CsvModel;
import com.meyratech.vicenze.backend.model.Invoice;
import com.meyratech.vicenze.backend.model.ItemDetails;
import com.meyratech.vicenze.backend.model.Project;
import com.meyratech.vicenze.backend.repository.service.IInvoiceService;
import com.meyratech.vicenze.backend.repository.service.IProjectService;
import com.meyratech.vicenze.backend.security.SecurityUtils;
import com.meyratech.vicenze.backend.security.UtilsForSpring;
import com.meyratech.vicenze.ui.util.UIUtils;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.ListDataProvider;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * ekocbiyik on 09.09.2019
 */
public class CsvImportUtils {

    private static Logger logger = LoggerFactory.getLogger(CsvImportUtils.class);

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
            logger.error(e.getMessage());
        } finally {
            return csvList;
        }
    }

    public static void validateInvoiceCsv(Grid<CsvModel> csvGrid, Grid<Invoice> invoiceGrid) {

        List<Project> projectList = UtilsForSpring.getSingleBeanOfType(IProjectService.class).findAll();
        ListDataProvider<CsvModel> csvDataProvider = (ListDataProvider<CsvModel>) csvGrid.getDataProvider();
        ListDataProvider<Invoice> invoiceProvider = (ListDataProvider<Invoice>) invoiceGrid.getDataProvider();
        List<CsvModel> validatedList = new ArrayList<>();

        for (CsvModel c : csvDataProvider.getItems()) {
            try {
                Invoice i = new Invoice();
                i.setProject(validateProject(c, projectList));
                i.setVendor(validateVendor(c));
                i.setInvoiceNumber(c.getNUMBER());
                i.setInvoiceCode(c.getCODE());
                i.setEventType(validateEventType(c));
                i.setMainItem(validateMainItem(c));
                i.setBook(validateBook(c));
                i.setTransaction(validateTransaction(c));
                i.setExplanation(c.getEXPLANATION());
                i.setAmount(validateAmount(c));
                i.setUnitPrice(validateUnitPrice(c));
                i.setDate(validateDate(c));
                i.setCreatedBy(SecurityUtils.getCurrentUser());
                i.setCreationDate(LocalDateTime.now());

                invoiceProvider.getItems().add(i);
                invoiceProvider.refreshAll();
                validatedList.add(c);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        csvDataProvider.getItems().removeAll(validatedList);
        csvDataProvider.refreshAll();
    }

    public static void insertInvoice2DB(Grid<Invoice> invoiceGrid) {
        IInvoiceService invoiceService = UtilsForSpring.getSingleBeanOfType(IInvoiceService.class);
        ListDataProvider<Invoice> invoiceProvider = (ListDataProvider<Invoice>) invoiceGrid.getDataProvider();
        List<Invoice> savedList = new ArrayList<>();

        if (invoiceProvider.getItems().size() == 0) {
            UIUtils.showNotification("Table is empty!");
            return;
        }

        for (Invoice i : invoiceProvider.getItems()) {
            try {
                invoiceService.save(i);
                savedList.add(i);
            } catch (Exception e) {
                e.printStackTrace(); // db error
            }
        }

        invoiceProvider.getItems().removeAll(savedList);
        invoiceProvider.refreshAll();
        UIUtils.showNotification(savedList.size() + " invioce(s) saved!");
    }

    private static Project validateProject(CsvModel csvModel, List<Project> projectList) throws Exception {
        Project project = projectList.stream()
                .filter(p -> csvModel.getPROJECT().equals(p.getProjectName()))
                .findAny()
                .orElse(null);
        if (project == null) throw new Exception("Project does not exist!");
        return project;
    }

    private static String validateVendor(CsvModel csvModel) throws Exception {
        if (csvModel.getVENDOR().isEmpty()) throw new Exception("Vendor does not exist!");
        return csvModel.getVENDOR();
    }

    private static String validateEventType(CsvModel csvModel) throws Exception {
        if (csvModel.getEVENT_TYPE().isEmpty() || !ItemDetails.eventTypeList.contains(csvModel.getEVENT_TYPE())) throw new Exception("EventType does not exist!");
        return csvModel.getEVENT_TYPE();
    }

    private static String validateMainItem(CsvModel csvModel) throws Exception {
        if (csvModel.getMAIN_ITEM().isEmpty() || !ItemDetails.mainItemList.contains(csvModel.getMAIN_ITEM())) throw new Exception("MainItem does not exist!");
        return csvModel.getMAIN_ITEM();
    }

    private static String validateBook(CsvModel csvModel) throws Exception {
        if (csvModel.getBOOK().isEmpty() || !ItemDetails.bookList.contains(csvModel.getBOOK())) throw new Exception("Book does not exist!");
        return csvModel.getBOOK();
    }

    private static String validateTransaction(CsvModel csvModel) throws Exception {
        if (csvModel.getTRANSACTION().isEmpty() || !ItemDetails.transactionList.contains(csvModel.getTRANSACTION())) throw new Exception("Transaction does not exist!");
        return csvModel.getTRANSACTION();
    }

    private static BigDecimal validateAmount(CsvModel csvModel) throws Exception {
        if (csvModel.getAMOUNT().isEmpty()) throw new Exception("Amount does not exist!");
        return new BigDecimal(csvModel.getAMOUNT());
    }

    private static BigDecimal validateUnitPrice(CsvModel csvModel) throws Exception {
        if (csvModel.getUNIT_PRICE().isEmpty()) throw new Exception("UnitPrice does not exist!");
        return new BigDecimal(csvModel.getUNIT_PRICE());
    }

    private static LocalDateTime validateDate(CsvModel csvModel) throws Exception {
        if (csvModel.getDATE().isEmpty()) throw new Exception("Date does not exist!");
        LocalDateTime date;
        String dateString = csvModel.getDATE();
        String exceptFormat = (dateString.contains(" ") && dateString.contains(":")) ? "d/M/yyyy HH:mm:ss" : "d/M/yyyy";
        String errorFormat = (dateString.contains(" ") && dateString.contains(":")) ? "M/d/yyyy HH:mm:ss" : "M/d/yyyy";

        try {
            if (exceptFormat.contains(":")) {
                date = LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(exceptFormat));
            } else {
                date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern(exceptFormat)).atStartOfDay();
            }
        } catch (Exception e) {
            if (errorFormat.contains(":")) {
                date = LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(errorFormat));
            } else {
                date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern(errorFormat)).atStartOfDay();
            }
        }
        return date;
    }

}
