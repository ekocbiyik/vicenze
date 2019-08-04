package com.meyratech.vicenze.backend.repository.dao;

import com.meyratech.vicenze.backend.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ekocbiyik on 04.08.2019
 */
@Repository
public interface IInvoiceDao extends JpaRepository<Invoice, Long> {

}
