package com.meyratech.vicenze.backend.repository.dao;

import com.meyratech.vicenze.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface IUserDao extends JpaRepository<User, Long> {

    User findByEmailIgnoreCase(String email);

    @Modifying
    @Query("update User u set u.lastLogin = :lastLogin where u.email = :email")
    void setLastLogin(@Param("email") String email, @Param("lastLogin") LocalDateTime lastLogin);

}
