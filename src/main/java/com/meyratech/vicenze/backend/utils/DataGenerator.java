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
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
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
    private User systemUser;

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
        userService.findAll().forEach(u -> {
            if (u.getEmail().equals("admin@mail.com")) {
                systemUser = u;
                return;
            }
        });
        generateProjects();
        try {
            generateInvoice();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateUser() {
        if (userService.findAll().size() != 0L) {
            getLogger().info("Using existing database");
            return;
        }
        getLogger().info("Generating user data");

        User adminUser = new User();
        adminUser.setFirstName("SYSTEM");
        adminUser.setLastName("");

        // admin
        userService.save(createUser("admin@mail.com", passwordEncoder.encode("admin"), Role.ADMIN, "Admin", "Test", false, true, adminUser));
        userService.save(createUser("enbiya@mail.com", passwordEncoder.encode("enbiya"), Role.ADMIN, "Enbiya", "Test", false, true, adminUser));

        // barista
        userService.save(createUser("user@mail.com", passwordEncoder.encode("user"), Role.ACCOUNTANT, "User", "Test", false, true, adminUser));
        userService.save(createUser("barista@mail.com", passwordEncoder.encode("barista"), Role.ACCOUNTANT, "Barista", "Nikola", false, true, adminUser));
        userService.save(createUser("barista2@mail.com", passwordEncoder.encode("barista"), Role.ACCOUNTANT, "Barista2", "Nikola", false, true, adminUser));
        userService.save(createUser("barista3@mail.com", passwordEncoder.encode("barista"), Role.ACCOUNTANT, "Barista3", "Nikola", false, true, adminUser));

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
        getLogger().info("Generating invoice data");

        if (invoiceService.findAll().size() > 0) {
            return;
        }

        Map<String, Project> projectList = new HashMap<>();
        for (Project p : projectService.findAll()) {
            projectList.put(p.getProjectName(), p);
            projectList.put("", p);
        }


        InputStream in = getClass().getClassLoader().getResourceAsStream("vicenze.csv");
        BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

        CSVParser csvParser = new CSVParser(br, CSVFormat.EXCEL
                .withFirstRecordAsHeader()
                .withIgnoreHeaderCase()
                .withIgnoreEmptyLines()
                .withIgnoreSurroundingSpaces()
                .withTrim()
        );

        List<Invoice> invList = new ArrayList<>();
        Long rndNumber = 123_658_901L;
        Long rndCode = 826_680_001L;

        for (CSVRecord l : csvParser.getRecords()) {
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
                i.setVendor(vendor.isEmpty() ? "OTHER" : vendor);
                i.setInvoiceNumber(number.isEmpty() ? String.valueOf(++rndNumber) : number);
                i.setInvoiceCode(code.isEmpty() ? String.valueOf(++rndCode) : code);
                i.setEventType(eventType);
                i.setMainItem(mainItem);
                i.setBook(book);
                i.setTransaction(transaction);
                i.setExplanation(exp);
                i.setAmount(new BigDecimal(amount.isEmpty() ? "0" : amount));
                i.setUnitPrice(new BigDecimal(unitPrice));
                i.setDate(LocalDateTime.from(LocalDate.parse(date, DateTimeFormatter.ofPattern("M/d/yyyy")).atStartOfDay()));
                i.setCreatedBy(systemUser);
                i.setCreationDate(LocalDateTime.now());

                invList.add(i);
            } catch (Exception e) {
                System.out.println("hata ama sorun yok");
            }
        }

        invList.forEach(invoice -> invoiceService.save(invoice));
        getLogger().info("Generated invoice data");
    }

    private User createUser(String email, String passwordHash, String role, String firstName, String lastName, boolean locked, boolean isActive, User createdBy) {
        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(passwordHash);
        user.setRole(role);
        user.setLocked(locked);
        user.setActive(isActive);
        user.setCreatedBy(createdBy.getFullName());
        return user;
    }

    private Project createProject(String projectName, String description, String email, String phone) {
        Project project = new Project();
        project.setCompany(projectName.toUpperCase() + " COMPANY");
        project.setProjectLogo(String.format("logo-%s.png", projectService.findAll().size() % 40));
        project.setEmail(email);
        project.setPhone(phone);
        project.setProjectName(projectName);
        project.setDescription(description);
        project.setActive(new Random().nextBoolean());
        project.setCreatedBy(systemUser);
        return project;
    }

}
