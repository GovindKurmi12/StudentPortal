package com.gk.config;

import com.gk.model.User;
import com.gk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@Lazy
public class DataInitializer implements CommandLineRunner {
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        // Create default admin user if no users exist
        if (!userService.userExists("admin")) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword("admin"); // Will be encoded by UserService
            adminUser.setEmail("admin@studentportal.com");
            adminUser.setFirstName("System");
            adminUser.setLastName("Administrator");
            adminUser.setEnabled(true);

            Set<String> roles = new HashSet<>();
            roles.add("ADMIN");
            roles.add("TEACHER");
            adminUser.setRoles(roles);

            userService.createUser(adminUser);
        }
    }
}
