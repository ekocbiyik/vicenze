package com.meyratech.vicenze.backend.repository.service;

import com.meyratech.vicenze.backend.model.Invoice;
import com.meyratech.vicenze.backend.model.Project;

import java.util.List;

/**
 * ekocbiyik on 03.08.2019
 */
public interface IInvoiceService {

    Invoice save(Invoice invoice);

    Invoice findById(Long id);

    List<Invoice> findAll();

}
