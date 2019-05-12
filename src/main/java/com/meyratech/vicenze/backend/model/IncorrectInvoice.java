package com.meyratech.vicenze.backend.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * ekocbiyik on 12.05.2019
 */
@Entity
@Table(name = "t_incorrect_invoice")
public class IncorrectInvoice extends AbstractEntity {

    @OneToOne
    private Invoice invoice;

    @Column(name = "invoice_number")
    private String invoiceNumber; // fatura numarası

    @Column(name = "invoice_code")
    private String invoiceCode; // vergino şeklinde olan kısım

    @Column(name = "vendor")
    private String vendor;

    @Column(name = "description")
    private String description;

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getInvoiceCode() {
        return invoiceCode;
    }

    public void setInvoiceCode(String invoiceCode) {
        this.invoiceCode = invoiceCode;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
