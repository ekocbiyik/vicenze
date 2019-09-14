package com.meyratech.vicenze.backend.security;

import com.meyratech.vicenze.backend.model.User;
import com.meyratech.vicenze.backend.repository.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationEventListener {

    @Autowired
    private IUserService userService;

    @EventListener
    public void authenticationFailed(AuthenticationFailureBadCredentialsEvent event) {
        String username = (String) event.getAuthentication().getPrincipal();
        User user = userService.findByEmail(username);
        if (user != null) {
            user.setTryCount(user.getTryCount() + 1);
            user.setLocked(user.getTryCount() > 2);
            userService.save(user);
        }
    }

}