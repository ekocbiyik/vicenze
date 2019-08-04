package com.meyratech.vicenze.backend.model;

import org.springframework.data.annotation.Transient;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ekocbiyik on 12.05.2019
 */
@Entity
@Table(name = "t_invoice")
public class Invoice extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "vendor")
    private String vendor;

    @Column(name = "invoice_number")
    private String invoiceNumber; // fatura numarası

    @Column(name = "invoice_code")
    private String invoiceCode; // vergino şeklinde olan kısım

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "main_item", nullable = false)
    private String mainItem;

    @Column(name = "book", nullable = false)
    private String book;

    @Column(name = "transaction", nullable = false)
    private String transaction;

    @Size(max = 255)
    @Column(name = "explanation")
    private String explanation;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Transient
    private BigDecimal totalAmount;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
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

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getMainItem() {
        return mainItem;
    }

    public void setMainItem(String mainItem) {
        this.mainItem = mainItem;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public BigDecimal getTotalAmount() {
        return unitPrice.multiply(amount);
    }

}
