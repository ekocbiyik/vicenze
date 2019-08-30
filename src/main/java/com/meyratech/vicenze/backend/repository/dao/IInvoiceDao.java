package com.meyratech.vicenze.backend.repository.dao;

import com.meyratech.vicenze.backend.model.Invoice;
import com.meyratech.vicenze.backend.model.Project;
import com.meyratech.vicenze.backend.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ekocbiyik on 04.08.2019
 */
@Repository
public interface IInvoiceDao extends JpaRepository<Invoice, Long> {

    @Query(value = "from Invoice i")
    List<Invoice> findTopInvoices(Pageable size);

    @Query(value = "from Invoice i where i.date >= :sDate and i.date <= :eDate")
    List<Invoice> getInvoicesByDate(@Param("sDate") LocalDateTime sDate, @Param("eDate") LocalDateTime eDate);

    @Query(value = "from Invoice i where i.project = :project and i.date >= :sDate and i.date <= :eDate")
    List<Invoice> getInvoicesByProjectAndDate(@Param("project") Project project, @Param("sDate") LocalDateTime sDate, @Param("eDate") LocalDateTime eDate);

    @Query(value = "from Invoice i where i.createdBy = :user and i.date >= :sDate and i.date <= :eDate")
    List<Invoice> getInvoicesByUserAndDate(@Param("user") User user, @Param("sDate") LocalDateTime sDate, @Param("eDate") LocalDateTime eDate);

    @Query(value = "from Invoice i where i.project = :project and i.createdBy = :user and i.date >= :sDate and i.date <= :eDate")
    List<Invoice> getInvoicesByProjectAndUserAndDate(@Param("project") Project project, @Param("user") User user, @Param("sDate") LocalDateTime sDate, @Param("eDate") LocalDateTime eDate);

}
