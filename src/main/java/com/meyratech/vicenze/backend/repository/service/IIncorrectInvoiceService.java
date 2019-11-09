package com.meyratech.vicenze.backend.repository.service;

import com.meyratech.vicenze.backend.model.IncorrectInvoice;

import java.util.List;

/**
 * ekocbiyik on 03.08.2019
 */
public interface IIncorrectInvoiceService {

    IncorrectInvoice save(IncorrectInvoice incorrectInvoice);

    IncorrectInvoice findById(Long id);

    List<IncorrectInvoice> findAll();

}
