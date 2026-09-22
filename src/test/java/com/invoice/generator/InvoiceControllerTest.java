package com.invoice.generator;

import com.invoice.generator.dto.InvoiceDTO;
import com.invoice.generator.dto.InvoiceItemDTO;
import com.invoice.generator.dto.SellerDTO;
import com.invoice.generator.dto.CustomerDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testHealth() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/health"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateInvoiceCorsAndEndpoint() throws Exception {
        InvoiceDTO dto = new InvoiceDTO();
        dto.setInvoiceNumber("INV-TEST-CORS");
        dto.setInvoiceDate("2026-09-23");
        dto.setDueDate("2026-10-07");
        dto.setSeller(new SellerDTO("Seller Inc", "Addr 1", "123", "s@test.com", "G1", "P1"));
        dto.setCustomer(new CustomerDTO("Customer Corp", "Comp", "Addr 2", "456", "c@test.com", "G2"));
        dto.getItems().add(new InvoiceItemDTO(null, "Item 1", "Desc 1", 1.0, 100.0, 18.0));

        mockMvc.perform(post("/api/invoices")
                .header("Origin", "http://localhost:5173")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}
