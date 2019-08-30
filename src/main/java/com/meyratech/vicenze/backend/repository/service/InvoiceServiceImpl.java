package com.meyratech.vicenze.backend.repository.service;

import com.meyratech.vicenze.backend.model.Invoice;
import com.meyratech.vicenze.backend.model.Project;
import com.meyratech.vicenze.backend.model.User;
import com.meyratech.vicenze.backend.repository.dao.IInvoiceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Transactional
    @Override
    public List<Invoice> getLastInvoices(int count) {
        return invoiceDao.findTopInvoices(new PageRequest(0, count, Sort.Direction.DESC, "id"));
    }

    @Transactional
    @Override
    public List<Invoice> getInvoicesByDate(LocalDateTime sDate, LocalDateTime eDate) {
        return invoiceDao.getInvoicesByDate(sDate, eDate);
    }

    @Transactional
    @Override
    public List<Invoice> getInvoicesByProjectAndDate(Project project, LocalDateTime sDate, LocalDateTime eDate) {
        return invoiceDao.getInvoicesByProjectAndDate(project, sDate, eDate);
    }

    @Transactional
    @Override
    public List<Invoice> getInvoicesByUserAndDate(User user, LocalDateTime sDate, LocalDateTime eDate) {
        return invoiceDao.getInvoicesByUserAndDate(user, sDate, eDate);
    }

    @Transactional
    @Override
    public List<Invoice> getInvoicesByProjectAndUserAndDate(Project project, User user, LocalDateTime sDate, LocalDateTime eDate) {
        return invoiceDao.getInvoicesByProjectAndUserAndDate(project, user, sDate, eDate);
    }
}
