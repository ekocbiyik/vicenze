package com.meyratech.vicenze.backend.utils;

import com.meyratech.vicenze.backend.model.Invoice;
import com.meyratech.vicenze.backend.model.Project;
import com.meyratech.vicenze.backend.model.Role;
import com.meyratech.vicenze.backend.model.User;
import com.meyratech.vicenze.backend.repository.service.InvoiceServiceImpl;
import com.meyratech.vicenze.backend.repository.service.ProjectServiceImpl;
import com.meyratech.vicenze.backend.repository.service.UserServiceImpl;
import com.meyratech.vicenze.backend.security.HasLogger;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@SpringComponent
public class DataGenerator implements HasLogger {

    private UserServiceImpl userService;
    private ProjectServiceImpl projectService;
    private InvoiceServiceImpl invoiceService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public DataGenerator(UserServiceImpl userService,
                         PasswordEncoder passwordEncoder,
                         ProjectServiceImpl projectService,
                         InvoiceServiceImpl invoiceService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.projectService = projectService;
        this.invoiceService = invoiceService;
    }

    @PostConstruct
    public void loadData() {
        generateUser();
        generateProjects();
//        try {
//            generateInvoice();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void generateUser() {
        if (userService.findAll().size() != 0L) {
            getLogger().info("Using existing database");
            return;
        }
        getLogger().info("Generating user data");

        // admin
        userService.save(createUser("admin@mail.com", passwordEncoder.encode("admin"), Role.ADMIN, "Malin", "Castro", false, true));

        // barista
        userService.save(createUser("barista@mail.com", passwordEncoder.encode("barista"), Role.ACCOUNTANT, "Barista", "Nikola", false, true));
        userService.save(createUser("barista2@mail.com", passwordEncoder.encode("barista"), Role.ACCOUNTANT, "Barista2", "Nikola", false, true));
        userService.save(createUser("barista3@mail.com", passwordEncoder.encode("barista"), Role.ACCOUNTANT, "Barista3", "Nikola", false, true));

        // customer
        userService.save(createUser("customer@mail.com", passwordEncoder.encode("customer"), Role.CUSTOMER, "Frank", "Riberry", false, true));
        userService.save(createUser("customer2@mail.com", passwordEncoder.encode("customer"), Role.CUSTOMER, "Frank2", "Riberry", false, true));
        userService.save(createUser("customer3@mail.com", passwordEncoder.encode("customer"), Role.CUSTOMER, "Frank3", "Riberry", false, true));
        userService.save(createUser("customer4@mail.com", passwordEncoder.encode("customer"), Role.CUSTOMER, "Frank4", "Riberry", false, true));

        getLogger().info("Generated user data");
    }

    private void generateProjects() {
        if (projectService.findAll().size() != 0L) {
            getLogger().info("Using existing database");
            return;
        }

        getLogger().info("Generating project data");

        List<String> pList = Arrays.asList("KIVELIS", "ELPIDOS", "IPSILANTOU", "DELIGIANNAKI", "SPDH", "NEW HQ", "ATHINON", "AIOLOU", "PAPAFOTI");
        for (String p : pList) {
            projectService.save(
                    createProject(
                            p,
                            "descipafdadftion adfdafadsfads sdfg rhertyerthb adfsdfret5434g wregerg6g  f f ",
                            p.trim().replaceAll(" ", "").toLowerCase() + "@mail.com",
                            "+90 212 754 43 21"
                    ));
        }
        getLogger().info("Generated project data");
    }


    private void generateInvoice() throws IOException {

        if (invoiceService.findAll().size() > 0) {
            return;
        }

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
                i.setCreatedBy("System");
                i.setCreationDate(LocalDateTime.now());

                invList.add(i);
            } catch (Exception e) {
                System.out.println("hata ama sorun yok");
            }
        });

        invList.forEach(invoice -> invoiceService.save(invoice));
    }

    private User createUser(String email, String passwordHash, String role, String firstName, String lastName, boolean locked, boolean isActive) {
        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(passwordHash);
        user.setRole(role);
        user.setLocked(locked);
        user.setActive(isActive);
        user.setCreatedBy("System");
        return user;
    }

    private Project createProject(String projectName, String description, String email, String phone) {
        Project project = new Project();
        project.setCompany(projectName.toUpperCase() + " COMPANY");
        project.setEmail(email);
        project.setPhone(phone);
        project.setProjectName(projectName);
        project.setDescription(description);
        project.setActive(new Random().nextBoolean());
        project.setCreatedBy("System");
        return project;
    }

}
