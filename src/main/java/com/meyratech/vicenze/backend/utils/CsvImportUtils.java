package com.meyratech.vicenze.backend.utils;

import com.meyratech.vicenze.backend.model.CsvModel;
import com.meyratech.vicenze.backend.model.Invoice;
import com.meyratech.vicenze.backend.model.ItemDetailUtils;
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
                c.setProject(l.get("PROJECT"));
                c.setVendor(l.get("VENDOR"));
                c.setEventType(l.get("EVENT_TYPE"));
                c.setMainItem(l.get("MAIN ITEM"));
                c.setBook(l.get("BOOK"));
                c.setTransaction(l.get("TRANSACTION"));
                c.setNumber(l.get("NUMBER"));
                c.setCode(l.get("CODE"));
                c.setExplanation(l.get("EXPLANATION"));
                c.setAmount(l.get("AMOUNT").replaceAll("€", "").replaceAll(",", "").trim());
                c.setUnitPrice(l.get("UNIT_PRICE").replaceAll("€", "").replaceAll(",", "").trim());
                c.setTotalAmount(l.get("TOTAL_AMOUNT").replaceAll("€", "").replaceAll(",", "").trim());
                c.setDate(l.get("DATE"));

                csvList.add(c);
            });
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return csvList;
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
                i.setInvoiceNumber(c.getNumber());
                i.setInvoiceCode(c.getCode());
                i.setEventType(validateEventType(c));
                i.setMainItem(validateMainItem(c));
                i.setBook(validateBook(c));
                i.setTransaction(validateTransaction(c));
                i.setExplanation(c.getExplanation());
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
                .filter(p -> csvModel.getProject().equals(p.getProjectName()))
                .findAny()
                .orElse(null);
        if (project == null) {
            throw new Exception("Project does not exist!");
        }
        return project;
    }

    private static String validateVendor(CsvModel csvModel) throws Exception {
        if (csvModel.getVendor().isEmpty()) {
            throw new Exception("Vendor does not exist!");
        }
        return csvModel.getVendor();
    }

    private static String validateEventType(CsvModel csvModel) throws Exception {
        if (csvModel.getEventType().isEmpty() || !ItemDetailUtils.eventTypeList.contains(csvModel.getEventType())) {
            throw new Exception("EventType does not exist!");
        }
        return csvModel.getEventType();
    }

    private static String validateMainItem(CsvModel csvModel) throws Exception {
        if (csvModel.getMainItem().isEmpty() || !ItemDetailUtils.mainItemList.contains(csvModel.getMainItem())) {
            throw new Exception("MainItem does not exist!");
        }
        return csvModel.getMainItem();
    }

    private static String validateBook(CsvModel csvModel) throws Exception {
        if (csvModel.getBook().isEmpty() || !ItemDetailUtils.bookList.contains(csvModel.getBook())) {
            throw new Exception("Book does not exist!");
        }
        return csvModel.getBook();
    }

    private static String validateTransaction(CsvModel csvModel) throws Exception {
        if (csvModel.getTransaction().isEmpty() || !ItemDetailUtils.transactionList.contains(csvModel.getTransaction())) {
            throw new Exception("Transaction does not exist!");
        }
        return csvModel.getTransaction();
    }

    private static BigDecimal validateAmount(CsvModel csvModel) throws Exception {
        if (csvModel.getAmount().isEmpty()) {
            throw new Exception("Amount does not exist!");
        }
        return new BigDecimal(csvModel.getAmount());
    }

    private static BigDecimal validateUnitPrice(CsvModel csvModel) throws Exception {
        if (csvModel.getUnitPrice().isEmpty()) {
            throw new Exception("UnitPrice does not exist!");
        }
        return new BigDecimal(csvModel.getUnitPrice());
    }

    private static LocalDateTime validateDate(CsvModel csvModel) throws Exception {
        if (csvModel.getDate().isEmpty()) {
            throw new Exception("Date does not exist!");
        }
        LocalDateTime date;
        String dateString = csvModel.getDate();
        String exceptFormat = (dateString.contains(" ") && dateString.contains(":")) ? "d/M/yyyy HH:mm:ss" : "d/M/yyyy";
        String errorFormat = (dateString.contains(" ") && dateString.contains(":")) ? "M/d/yyyy HH:mm:ss" : "M/d/yyyy";

        try {
            date = exceptFormat.contains(":")
                    ? LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(exceptFormat))
                    : LocalDate.parse(dateString, DateTimeFormatter.ofPattern(exceptFormat)).atStartOfDay();
        } catch (Exception e) {
            date = exceptFormat.contains(":")
                    ? LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(errorFormat))
                    : LocalDate.parse(dateString, DateTimeFormatter.ofPattern(errorFormat)).atStartOfDay();
        }
        return date;
    }

}
