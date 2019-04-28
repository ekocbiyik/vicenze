package com.meyratech.vicenze.backend.repository.service;

import com.meyratech.vicenze.backend.model.User;
import com.meyratech.vicenze.backend.repository.dao.IUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meyratech.vicenze.backend.exceptions.UserFriendlyDataException;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements FilterableCrudService<User> {

    public static final String MODIFY_LOCKED_USER_NOT_PERMITTED = "User has been locked and cannot be modified or deleted";
    private static final String DELETING_SELF_NOT_PERMITTED = "You cannot delete your own account";
    private final IUserDao userDao;

    @Autowired
    public UserServiceImpl(IUserDao userDao) {
        this.userDao = userDao;
    }

    public Page<User> findAnyMatching(Optional<String> filter, Pageable pageable) {
        if (filter.isPresent()) {
            String repositoryFilter = "%" + filter.get() + "%";
            return getRepository()
                    .findByEmailLikeIgnoreCaseOrFirstNameLikeIgnoreCaseOrLastNameLikeIgnoreCaseOrRoleLikeIgnoreCase(
                            repositoryFilter, repositoryFilter, repositoryFilter, repositoryFilter, pageable);
        } else {
            return find(pageable);
        }
    }

    @Override
    public long countAnyMatching(Optional<String> filter) {
        if (filter.isPresent()) {
            String repositoryFilter = "%" + filter.get() + "%";
            return userDao.countByEmailLikeIgnoreCaseOrFirstNameLikeIgnoreCaseOrLastNameLikeIgnoreCaseOrRoleLikeIgnoreCase(
                    repositoryFilter, repositoryFilter, repositoryFilter, repositoryFilter);
        } else {
            return count();
        }
    }

    @Override
    public User findById(Long id) {
        return userDao.getOne(id);
    }

    @Override
    public IUserDao getRepository() {
        return userDao;
    }

    public Page<User> find(Pageable pageable) {
        return getRepository().findBy(pageable);
    }

    @Override
    public User save(User currentUser, User entity) {
        throwIfUserLocked(entity);
        return getRepository().saveAndFlush(entity);
    }

    @Override
    @Transactional
    public void delete(User currentUser, User userToDelete) {
        throwIfDeletingSelf(currentUser, userToDelete);
        throwIfUserLocked(userToDelete);
        FilterableCrudService.super.delete(currentUser, userToDelete);
    }

    private void throwIfDeletingSelf(User currentUser, User user) {
        if (currentUser.equals(user)) {
            throw new UserFriendlyDataException(DELETING_SELF_NOT_PERMITTED);
        }
    }

    private void throwIfUserLocked(User entity) {
        if (entity != null && entity.isLocked()) {
            throw new UserFriendlyDataException(MODIFY_LOCKED_USER_NOT_PERMITTED);
        }
    }

    @Override
    public User createNew(User currentUser) {
        return new User();
    }

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

}
