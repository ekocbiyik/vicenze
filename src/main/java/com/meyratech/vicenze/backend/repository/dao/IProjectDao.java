package com.meyratech.vicenze.backend.repository.dao;

import com.meyratech.vicenze.backend.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ekocbiyik on 4/28/19
 */
@Repository
public interface IProjectDao extends JpaRepository<Project, Long> {

}
