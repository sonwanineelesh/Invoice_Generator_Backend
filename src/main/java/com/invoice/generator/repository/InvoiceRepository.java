package com.invoice.generator.repository;

import com.invoice.generator.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    boolean existsByInvoiceNumber(String invoiceNumber);

    @Query("SELECT DISTINCT i FROM Invoice i LEFT JOIN FETCH i.items ORDER BY i.id DESC")
    List<Invoice> findAllByOrderByIdDesc();

    @Query("SELECT DISTINCT i FROM Invoice i LEFT JOIN FETCH i.items WHERE " +
           "(i.invoiceNumber IS NOT NULL AND LOWER(i.invoiceNumber) LIKE LOWER(CONCAT('%', :query, '%'))) OR " +
           "(i.customerName IS NOT NULL AND LOWER(i.customerName) LIKE LOWER(CONCAT('%', :query, '%'))) OR " +
           "(i.sellerName IS NOT NULL AND LOWER(i.sellerName) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY i.id DESC")
    List<Invoice> searchInvoices(@Param("query") String query);
}
