package com.meyratech.vicenze.backend.repository.service;

import com.meyratech.vicenze.backend.model.Invoice;
import com.meyratech.vicenze.backend.model.Project;
import com.meyratech.vicenze.backend.model.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ekocbiyik on 03.08.2019
 */
public interface IInvoiceService {

    Invoice save(Invoice invoice);

    Invoice findById(Long id);

    List<Invoice> findAll();

    List<Invoice> getLastInvoices(int count);

    List<Invoice> getInvoicesByDate(LocalDateTime sDate, LocalDateTime eDate);

    List<Invoice> getInvoicesByProjectAndDate(Project project, LocalDateTime sDate, LocalDateTime eDate);

    List<Invoice> getInvoicesByUserAndDate(User user, LocalDateTime sDate, LocalDateTime eDate);

    List<Invoice> getInvoicesByProjectAndUserAndDate(Project project, User user, LocalDateTime sDate, LocalDateTime eDate);

    List<String> getAllVendorList();
}
