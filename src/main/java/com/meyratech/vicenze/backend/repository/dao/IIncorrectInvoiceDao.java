package com.meyratech.vicenze.backend.repository.dao;

import com.meyratech.vicenze.backend.model.IncorrectInvoice;
import com.meyratech.vicenze.backend.model.Invoice;
import com.meyratech.vicenze.backend.model.Project;
import com.meyratech.vicenze.backend.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ekocbiyik on 04.08.2019
 */
@Repository
public interface IIncorrectInvoiceDao extends JpaRepository<IncorrectInvoice, Long> {
}
