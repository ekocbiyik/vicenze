package com.meyratech.vicenze.backend.repository.dao;

import com.meyratech.vicenze.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserDao extends JpaRepository<User, Long> {

    User findByEmailIgnoreCase(String email);

    Page<User> findBy(Pageable pageable);

    Page<User> findByEmailLikeIgnoreCaseOrFirstNameLikeIgnoreCaseOrLastNameLikeIgnoreCaseOrRoleLikeIgnoreCase(String emailLike, String firstNameLike, String lastNameLike, String roleLike, Pageable pageable);

    long countByEmailLikeIgnoreCaseOrFirstNameLikeIgnoreCaseOrLastNameLikeIgnoreCaseOrRoleLikeIgnoreCase(String emailLike, String firstNameLike, String lastNameLike, String roleLike);
}
