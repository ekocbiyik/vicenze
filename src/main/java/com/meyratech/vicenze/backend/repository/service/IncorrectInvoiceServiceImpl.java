package com.meyratech.vicenze.backend.repository.service;

import com.meyratech.vicenze.backend.model.IncorrectInvoice;
import com.meyratech.vicenze.backend.repository.dao.IIncorrectInvoiceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ekocbiyik on 4/28/19
 */
@Service
public class IncorrectInvoiceServiceImpl implements IIncorrectInvoiceService {

    @Autowired
    private IIncorrectInvoiceDao incorrectInvoiceDao;

    @Transactional
    @Override
    public IncorrectInvoice save(IncorrectInvoice incorrectInvoice) {
        return incorrectInvoiceDao.save(incorrectInvoice);
    }

    @Transactional
    @Override
    public IncorrectInvoice findById(Long id) {
        return incorrectInvoiceDao.findById(id).get();
    }

    @Transactional
    @Override
    public List<IncorrectInvoice> findAll() {
        return incorrectInvoiceDao.findByOrderByIsActiveDescCreationDateDesc();
    }
}
