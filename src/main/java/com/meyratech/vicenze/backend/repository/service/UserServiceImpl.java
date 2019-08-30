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

    public static final String MODIFY_LOCKED_USER_NOT_PERMITTED = "User has been locked and cannot be modified or deleted";
    private static final String DELETING_SELF_NOT_PERMITTED = "You cannot delete your own account";
    private final IUserDao userDao;

    @Autowired
    public UserServiceImpl(IUserDao userDao) {
        this.userDao = userDao;
    }

    @Transactional
    @Override
    public User save(User user) {
        return userDao.save(user);
    }

    @Override
    public User findById(Long id) {
        return userDao.getOne(id);
    }

    @Transactional
    @Override
    public List<User> findAll() {
        return userDao.findAll();
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
