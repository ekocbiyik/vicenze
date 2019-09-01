package com.meyratech.vicenze.backend.repository.service;

import com.meyratech.vicenze.backend.model.IncorrectInvoice;
import com.meyratech.vicenze.backend.model.Invoice;
import com.meyratech.vicenze.backend.model.Project;
import com.meyratech.vicenze.backend.model.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ekocbiyik on 03.08.2019
 */
public interface IIncorrectInvoiceService {

    IncorrectInvoice save(IncorrectInvoice incorrectInvoice);

    IncorrectInvoice findById(Long id);

    List<IncorrectInvoice> findAll();

}
