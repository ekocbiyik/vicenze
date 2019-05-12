package com.meyratech.vicenze.backend.repository.service;

import com.meyratech.vicenze.backend.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ekocbiyik on 12.05.2019
 */
@Service
public interface IUserService {

    User save(User user);

    User findById(Long id);

    List<User> findAll();

    void setLastLogin(String email, LocalDateTime loginTime);

}
