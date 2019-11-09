package com.meyratech.vicenze.backend.repository.dao;

import com.meyratech.vicenze.backend.model.IncorrectInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ekocbiyik on 04.08.2019
 */
@Repository
public interface IIncorrectInvoiceDao extends JpaRepository<IncorrectInvoice, Long> {

    List<IncorrectInvoice> findByOrderByIsActiveDescCreationDateDesc();

}
