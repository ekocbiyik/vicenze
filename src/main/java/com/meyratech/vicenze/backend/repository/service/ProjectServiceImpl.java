package com.meyratech.vicenze.backend.repository.service;

import com.meyratech.vicenze.backend.model.Project;
import com.meyratech.vicenze.backend.model.User;
import com.meyratech.vicenze.backend.repository.dao.IProjectDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * ekocbiyik on 4/28/19
 */
@Service
public class ProjectServiceImpl implements FilterableCrudService<Project> {

    public static final String MODIFY_LOCKED_USER_NOT_PERMITTED = "User has been locked and cannot be modified or deleted";
    private static final String DELETING_SELF_NOT_PERMITTED = "You cannot delete your own account";
    private final IProjectDao projectDao;

    @Autowired
    public ProjectServiceImpl(IProjectDao projectDao) {
        this.projectDao = projectDao;
    }


    @Override
    public Page<Project> findAnyMatching(Optional<String> filter, Pageable pageable) {
        return null;
    }

    @Override
    public long countAnyMatching(Optional<String> filter) {
        return 0;
    }

    @Override
    public Project findById(Long id) {
        return projectDao.getOne(id);
    }

    @Override
    public JpaRepository<Project, Long> getRepository() {
        return projectDao;
    }

    @Override
    public Project createNew(User currentUser) {
        return null;
    }

    @Override
    public List<Project> findAll() {
        return projectDao.findAll();
    }

}
