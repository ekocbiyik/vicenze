package com.meyratech.vicenze.backend.utils;

import com.meyratech.vicenze.backend.model.CsvModel;
import com.meyratech.vicenze.backend.model.Invoice;
import com.meyratech.vicenze.backend.model.Project;
import com.meyratech.vicenze.backend.repository.service.IProjectService;
import com.meyratech.vicenze.backend.security.SecurityUtils;
import com.meyratech.vicenze.backend.security.UtilsForSpring;
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
                i.setEventType(c.getEVENT_TYPE());
                i.setMainItem(c.getMAIN_ITEM());
                i.setBook(c.getBOOK());
                i.setTransaction(c.getTRANSACTION());
                i.setExplanation(c.getEXPLANATION());
                i.setAmount(new BigDecimal(c.getAMOUNT().isEmpty() ? "0" : c.getAMOUNT()));
                i.setUnitPrice(new BigDecimal(c.getUNIT_PRICE()));
                i.setDate(LocalDateTime.from(LocalDate.parse(c.getDATE(), DateTimeFormatter.ofPattern("M/d/yyyy")).atStartOfDay()));
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


}