package com.meyratech.vicenze.backend.security;

import com.meyratech.vicenze.backend.model.Role;
import com.meyratech.vicenze.backend.model.User;
import com.meyratech.vicenze.backend.repository.dao.IUserDao;
import com.meyratech.vicenze.backend.repository.service.IUserService;
import com.meyratech.vicenze.ui.util.ViewConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;

/**
 * Configures spring security, doing the following:
 * <li>Bypass security checks for static resources,</li>
 * <li>Restrict access to the application, allowing only logged in users,</li>
 * <li>Set up the login form,</li>
 * <li>Configures the {@link UserDetailsServiceImpl}.</li>
 */
@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String LOGIN_PROCESSING_URL = "/login";
    private static final String LOGIN_FAILURE_URL = "/login?error";
    private static final String LOGIN_URL = "/login";
    private static final String LOGIN_SUCCESS_URL = "/" + ViewConst.PAGE_HOME;
    private static final String LOGOUT_SUCCESS_URL = "/" + ViewConst.PAGE_HOME;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IUserService userService;

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public CurrentUser currentUser(IUserDao userDao) {
        final String username = SecurityUtils.getUsername();
        User user = username != null ? userDao.findByEmailIgnoreCase(username) : null;
        return () -> user;
    }

    /**
     * Registers our UserDetailsService and the password encoder to be used on login attempts.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    /**
     * Require login to access internal pages and configure login form.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Not using Spring CSRF here to be able to use plain HTML for the login page
        http.csrf().disable()
                .requestCache().requestCache(new CustomRequestCache())
                .and().authorizeRequests()
                .requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()
                .anyRequest().hasAnyAuthority(Role.getAllRoles())
                .and()
                .formLogin()
                .defaultSuccessUrl(LOGIN_SUCCESS_URL, true)
                .loginPage(LOGIN_URL)
                .permitAll()
                .loginProcessingUrl(LOGIN_PROCESSING_URL)
                .failureUrl(LOGIN_FAILURE_URL)
                .successHandler(new CustomAuthenticationSuccessHandler(userService))
                .and()
                .logout()
                .logoutSuccessUrl(LOGOUT_SUCCESS_URL);
        http.sessionManagement().maximumSessions(1).sessionRegistry(sessionRegistry());
    }

    /**
     * Allows access to static resources, bypassing Spring security.
     */
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(
                "/VAADIN/**",   // Vaadin Flow static resources
                "/favicon.ico",             // the standard favicon URI
                "/robots.txt",              // the robots exclusion standard
                "/manifest.webmanifest",    // web application manifest
                "/sw.js",
                "/offline-page.html",
                "/icons/**",                // icons and images
                "/images/**",
                "/frontend/**",             // (development mode) static resources
                "/webjars/**",              // (development mode) webjars
                "/h2-console/**",           // (development mode) H2 debugging console
                "/frontend-es5/**",         // (production mode) static resources
                "/frontend-es6/**"
        );
    }

}
