package com.meyratech.vicenze.backend.repository.service;

import com.meyratech.vicenze.backend.model.Role;
import com.meyratech.vicenze.backend.model.User;
import com.meyratech.vicenze.backend.repository.dao.IUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private IUserDao userDao;

    @Transactional
    @Override
    public User save(User user) {
        return userDao.save(user);
    }

    @Transactional
    @Override
    public User findById(Long id) {
        return userDao.getOne(id);
    }

    @Transactional
    @Override
    public User findByEmail(String email) {
        return userDao.findByEmailIgnoreCase(email);
    }

    @Transactional
    @Override
    public List<User> findAll() {
        return userDao.findAllByOrderByLastLoginDesc();
    }

    @Transactional
    @Override
    public List<User> findAllAdmins() {
        return userDao.findAllByRole(Role.ADMIN);
    }

    @Transactional
    @Override
    public void setLastLogin(String email, LocalDateTime loginTime) {
        userDao.setLastLogin(email, loginTime);
    }

}
