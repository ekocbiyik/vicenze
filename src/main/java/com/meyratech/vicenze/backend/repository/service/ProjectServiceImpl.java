package com.meyratech.vicenze.backend.repository.service;

import com.meyratech.vicenze.backend.model.Project;
import com.meyratech.vicenze.backend.repository.dao.IProjectDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ekocbiyik on 4/28/19
 */
@Service
public class ProjectServiceImpl implements IProjectService {

    private final IProjectDao projectDao;

    @Autowired
    public ProjectServiceImpl(IProjectDao projectDao) {
        this.projectDao = projectDao;
    }

    @Override
    public Project save(Project project) {
        return projectDao.save(project);
    }

    @Override
    public Project findById(Long id) {
        return projectDao.findById(id).get();
    }

    @Override
    public List<Project> findAll() {
        return projectDao.findAll();
    }
}
