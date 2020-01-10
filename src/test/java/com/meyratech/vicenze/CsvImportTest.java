package com.meyratech.vicenze;

import com.meyratech.vicenze.backend.model.Invoice;
import com.meyratech.vicenze.backend.model.Project;
import com.meyratech.vicenze.backend.model.User;
import com.meyratech.vicenze.backend.repository.service.IInvoiceService;
import com.meyratech.vicenze.backend.repository.service.IProjectService;
import com.meyratech.vicenze.backend.repository.service.IUserService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * ekocbiyik on 04.08.2019
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CsvImportTest {

    @Autowired
    private IProjectService projectService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IInvoiceService invoiceService;

    @Test
    public void csvReadTest() throws IOException {

        User adminUser = userService.findAllAdmins().get(0);

        Map<String, Project> projectList = new HashMap<>();
        for (Project p : projectService.findAll()) {
            projectList.put(p.getProjectName(), p);
            projectList.put("", p);
        }

        List<Invoice> invList = new ArrayList<>();
        String path = this.getClass().getClassLoader().getResource("vicenze.csv").getPath();
        Reader reader = Files.newBufferedReader(Paths.get(path));

        CSVParser csvParser = new CSVParser(reader, CSVFormat.EXCEL
                .withFirstRecordAsHeader()
                .withIgnoreHeaderCase()
                .withIgnoreEmptyLines()
                .withIgnoreSurroundingSpaces()
                .withTrim()
        );

        csvParser.forEach(l -> {

            String project = l.get("PROJECT");
            String vendor = l.get("VENDOR");
            String number = l.get("NUMBER");
            String code = l.get("CODE");
            String eventType = l.get("EVENT_TYPE");
            String mainItem = l.get("MAIN ITEM");
            String book = l.get("BOOK");
            String transaction = l.get("TRANSACTION");
            String exp = l.get("EXPLANATION");
            String amount = l.get("AMOUNT").replaceAll("€", "").replaceAll(",", "").trim();
            String unitPrice = l.get("UNIT_PRICE").replaceAll("€", "").replaceAll(",", "").trim();
            String date = l.get("DATE");

            try {
                Invoice i = new Invoice();
                i.setProject(projectList.get(project));
                if (i.getProject() == null) {
                    i.setProject(projectList.get("ATHINON"));
                }

                i.setVendor(vendor);
                i.setInvoiceNumber(number);
                i.setInvoiceCode(code);
                i.setEventType(eventType);
                i.setMainItem(mainItem);
                i.setBook(book);
                i.setTransaction(transaction);
                i.setExplanation(exp);
                i.setAmount(new BigDecimal(amount.isEmpty() ? "0" : amount));
                i.setUnitPrice(new BigDecimal(unitPrice));
                i.setDate(LocalDateTime.from(LocalDate.parse(date, DateTimeFormatter.ofPattern("M/d/yyyy")).atStartOfDay()));
                i.setCreatedBy(adminUser);
                i.setCreationDate(LocalDateTime.now());

                invList.add(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        for (Invoice i : invList) {
            try {
                invoiceService.save(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Test
    public void invoiceParameterTest() {

        List<Invoice> invoiceList = invoiceService.findAll();
        Set<String> eventType = new HashSet<>();
        Set<String> mainItem = new HashSet<>();
        Set<String> book = new HashSet<>();
        Set<String> transaction = new HashSet<>();

        invoiceList.forEach(invoice -> {
            eventType.add(invoice.getEventType());
            mainItem.add(invoice.getMainItem());
            book.add(invoice.getBook());
            transaction.add(invoice.getTransaction());
        });

        System.out.println("EVENT TYPE");
        eventType.forEach(System.out::println);

        System.out.println("MAIN ITEM");
        mainItem.forEach(System.out::println);

        System.out.println("BOOK");
        book.forEach(System.out::println);

        System.out.println("TRANSACTION");
        transaction.forEach(System.out::println);

        assert true;
    }

}
