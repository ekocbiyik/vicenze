package com.meyratech.vicenze.backend.repository.service;

import com.meyratech.vicenze.backend.model.Project;

import java.util.List;

/**
 * ekocbiyik on 03.08.2019
 */
public interface IProjectService {

    Project save(Project project);

    Project findById(Long id);

    Project findByProjectName(String projectName);

    List<Project> findAll();

    List<Project> findAllActiveProjects();

}
