package com.meyratech.vicenze.backend.repository.service;

import com.meyratech.vicenze.backend.model.Invoice;
import com.meyratech.vicenze.backend.repository.dao.IInvoiceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ekocbiyik on 4/28/19
 */
@Service
public class InvoiceServiceImpl implements IInvoiceService {

    @Autowired
    private IInvoiceDao invoiceDao;

    @Transactional
    @Override
    public Invoice save(Invoice invoice) {
        return invoiceDao.save(invoice);
    }

    @Transactional
    @Override
    public Invoice findById(Long id) {
        return invoiceDao.findById(id).get();
    }

    @Transactional
    @Override
    public List<Invoice> findAll() {
        return invoiceDao.findAll();
    }
}
