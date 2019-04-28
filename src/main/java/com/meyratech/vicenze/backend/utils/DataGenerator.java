package com.meyratech.vicenze.backend.utils;

import com.meyratech.vicenze.backend.model.Project;
import com.meyratech.vicenze.backend.model.Role;
import com.meyratech.vicenze.backend.model.User;
import com.meyratech.vicenze.backend.repository.dao.IProjectDao;
import com.meyratech.vicenze.backend.repository.dao.IUserDao;
import com.meyratech.vicenze.backend.repository.service.ProjectServiceImpl;
import com.meyratech.vicenze.backend.repository.service.UserServiceImpl;
import com.meyratech.vicenze.backend.security.HasLogger;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@SpringComponent
public class DataGenerator implements HasLogger {

    private UserServiceImpl userService;
    private ProjectServiceImpl projectService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public DataGenerator(UserServiceImpl userService,
                         PasswordEncoder passwordEncoder,
                         ProjectServiceImpl projectService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.projectService = projectService;
    }

    @PostConstruct
    public void loadData() {
        generateUser();
        generateProjects();
    }

    private void generateUser() {
        if (userService.count() != 0L) {
            getLogger().info("Using existing database");
            return;
        }
        getLogger().info("Generating user data");

        // admin
        userService.save(null, createUser("admin@mail.com", passwordEncoder.encode("admin"), Role.ADMIN, "Malin", "Castro", false, true));

        // barista
        userService.save(null, createUser("barista@mail.com", passwordEncoder.encode("barista"), Role.ACCOUNTANT, "Barista", "Nikola", false, true));
        userService.save(null, createUser("barista2@mail.com", passwordEncoder.encode("barista"), Role.ACCOUNTANT, "Barista2", "Nikola", false, true));
        userService.save(null, createUser("barista3@mail.com", passwordEncoder.encode("barista"), Role.ACCOUNTANT, "Barista3", "Nikola", false, true));

        // customer
        userService.save(null, createUser("customer@mail.com", passwordEncoder.encode("customer"), Role.CUSTOMER, "Frank", "Riberry", false, true));
        userService.save(null, createUser("customer2@mail.com", passwordEncoder.encode("customer"), Role.CUSTOMER, "Frank2", "Riberry", false, true));
        userService.save(null, createUser("customer3@mail.com", passwordEncoder.encode("customer"), Role.CUSTOMER, "Frank3", "Riberry", false, true));
        userService.save(null, createUser("customer4@mail.com", passwordEncoder.encode("customer"), Role.CUSTOMER, "Frank4", "Riberry", false, true));

        getLogger().info("Generated user data");
    }

    private void generateProjects() {
        if (projectService.count() != 0L) {
            getLogger().info("Using existing database");
            return;
        }
        getLogger().info("Generating project data");
        for (int i = 0; i < 30; i++) {
            projectService.save(null, createProject("Proje1_" + i, "desciption_" + i, true));
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        getLogger().info("Generated project data");
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
        return user;
    }

    private Project createProject(String projectName, String description, boolean isActive) {
        Project project = new Project();
        project.setProjectName(projectName);
        project.setDescription(description);
        project.setActive(new Random().nextBoolean());
        return project;
    }

}
