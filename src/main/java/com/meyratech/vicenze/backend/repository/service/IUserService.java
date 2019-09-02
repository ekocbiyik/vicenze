package com.meyratech.vicenze.backend.repository.service;

import com.meyratech.vicenze.backend.model.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ekocbiyik on 12.05.2019
 */
public interface IUserService {

    User save(User user);

    User findById(Long id);

    List<User> findAll();

    List<User> findAllAdmins();

    void setLastLogin(String email, LocalDateTime loginTime);

}
