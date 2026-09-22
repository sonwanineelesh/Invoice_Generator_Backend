package com.invoice.generator;

import com.invoice.generator.dto.InvoiceDTO;
import com.invoice.generator.dto.InvoiceItemDTO;
import com.invoice.generator.dto.SellerDTO;
import com.invoice.generator.dto.CustomerDTO;
import com.invoice.generator.service.InvoiceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class InvoiceServiceTest {

    @Autowired
    private InvoiceService invoiceService;

    @Test
    void testSaveAndFetchInvoice() {
        InvoiceDTO dto = new InvoiceDTO();
        dto.setInvoiceNumber("TEST-INV-001");
        dto.setInvoiceDate("2026-09-23");
        dto.setDueDate("2026-09-23");

        SellerDTO seller = new SellerDTO("Test Seller", "123 Street", "9999999999", "seller@test.com", "GSTIN1", "PAN1");
        dto.setSeller(seller);

        CustomerDTO customer = new CustomerDTO("Test Customer", "Cust Corp", "456 Street", "8888888888", "cust@test.com", "GSTIN2");
        dto.setCustomer(customer);

        InvoiceItemDTO item = new InvoiceItemDTO(null, "Consulting", "Software dev", 2.0, 5000.0, 18.0);
        dto.getItems().add(item);

        InvoiceDTO saved = invoiceService.saveOrUpdateInvoice(dto);
        assertNotNull(saved);
        assertNotNull(saved.getId());

        List<InvoiceDTO> list = invoiceService.getAllInvoices(null);
        assertFalse(list.isEmpty());

        String nextNumber = invoiceService.generateNextInvoiceNumber();
        assertNotNull(nextNumber);
    }
}
