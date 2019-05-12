package com.meyratech.vicenze.backend.model;

import org.springframework.data.annotation.Transient;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ekocbiyik on 12.05.2019
 */
@Entity
@Table(name = "t_invoice")
public class Invoice extends AbstractEntity {

    @Column(name = "invoice_number")
    private String invoiceNumber; // fatura numarası

    @Column(name = "invoice_code")
    private String invoiceCode; // vergino şeklinde olan kısım

    @Column(name = "vendor")
    private String vendor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "unit_prize", nullable = false)
    private BigDecimal unitPrize;

    @Column(name = "given_date", nullable = false)
    private LocalDateTime givenDate;

    @Transient
    private BigDecimal total;

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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getUnitPrize() {
        return unitPrize;
    }

    public void setUnitPrize(BigDecimal unitPrize) {
        this.unitPrize = unitPrize;
    }

    public LocalDateTime getGivenDate() {
        return givenDate;
    }

    public void setGivenDate(LocalDateTime givenDate) {
        this.givenDate = givenDate;
    }

    public BigDecimal getTotal() {
        return unitPrize.multiply(amount);
    }

    public void setTotal(BigDecimal total) {
        // total oto. olarak hesaplacak. DB de yer tutmasın
    }
}
