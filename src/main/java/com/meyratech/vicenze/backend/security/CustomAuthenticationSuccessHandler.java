package com.meyratech.vicenze.backend.security;

import com.meyratech.vicenze.backend.model.User;
import com.meyratech.vicenze.backend.repository.service.IUserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private IUserService userService;

    public CustomAuthenticationSuccessHandler(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        super.onAuthenticationSuccess(request, response, authentication);
        User user = userService.findByEmail(authentication.getName());
        user.setLastLogin(LocalDateTime.now());
        user.setTryCount(0);
        userService.save(user);
    }
}