package com.invoice.generator.service;

import com.invoice.generator.dto.*;
import com.invoice.generator.model.Invoice;
import com.invoice.generator.model.InvoiceItem;
import com.invoice.generator.repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Transactional
    public InvoiceDTO saveOrUpdateInvoice(InvoiceDTO dto) {
        Invoice invoice;

        if (dto.getId() != null) {
            invoice = invoiceRepository.findById(dto.getId())
                    .orElse(new Invoice());
        } else if (dto.getInvoiceNumber() != null && !dto.getInvoiceNumber().trim().isEmpty()) {
            Optional<Invoice> existing = invoiceRepository.findByInvoiceNumber(dto.getInvoiceNumber().trim());
            invoice = existing.orElseGet(Invoice::new);
        } else {
            invoice = new Invoice();
        }

        if (dto.getInvoiceNumber() == null || dto.getInvoiceNumber().trim().isEmpty()) {
            dto.setInvoiceNumber(generateNextInvoiceNumber());
        }

        invoice.setInvoiceNumber(dto.getInvoiceNumber().trim());
        invoice.setInvoiceDate(dto.getInvoiceDate());
        invoice.setDueDate(dto.getDueDate());

        // Seller
        if (dto.getSeller() != null) {
            invoice.setSellerName(dto.getSeller().getName());
            invoice.setSellerAddress(dto.getSeller().getAddress());
            invoice.setSellerPhone(dto.getSeller().getPhone());
            invoice.setSellerEmail(dto.getSeller().getEmail());
            invoice.setSellerGstin(dto.getSeller().getGstin());
            invoice.setSellerPan(dto.getSeller().getPan());
        }

        // Customer
        if (dto.getCustomer() != null) {
            invoice.setCustomerName(dto.getCustomer().getName());
            invoice.setCustomerCompany(dto.getCustomer().getCompany());
            invoice.setCustomerAddress(dto.getCustomer().getAddress());
            invoice.setCustomerPhone(dto.getCustomer().getPhone());
            invoice.setCustomerEmail(dto.getCustomer().getEmail());
            invoice.setCustomerGstin(dto.getCustomer().getGstin());
        }

        invoice.setPlaceOfSupply(dto.getPlaceOfSupply());
        invoice.setTaxType(dto.getTaxType() != null ? dto.getTaxType() : "intra");

        invoice.setDiscount(dto.getDiscount() != null ? Math.max(dto.getDiscount(), 0.0) : 0.0);
        invoice.setShipping(dto.getShipping() != null ? Math.max(dto.getShipping(), 0.0) : 0.0);
        invoice.setAmountPaid(dto.getAmountPaid() != null ? Math.max(dto.getAmountPaid(), 0.0) : 0.0);

        invoice.setNotes(dto.getNotes());
        invoice.setTerms(dto.getTerms());

        // Process Items & Calculations
        double subtotal = 0.0;
        invoice.clearItems();
        if (dto.getItems() != null) {
            for (InvoiceItemDTO itemDto : dto.getItems()) {
                InvoiceItem item = new InvoiceItem();
                item.setName(itemDto.getName());
                item.setDescription(itemDto.getDescription());
                item.setQuantity(itemDto.getQuantity() != null ? itemDto.getQuantity() : 0.0);
                item.setRate(itemDto.getRate() != null ? itemDto.getRate() : 0.0);
                item.setGst(itemDto.getGst() != null ? itemDto.getGst() : 18.0);

                subtotal += (item.getQuantity() * item.getRate());
                invoice.addItem(item);
            }
        }

        // Financial Calculations
        double discount = Math.min(invoice.getDiscount(), subtotal);
        double taxableAmount = subtotal - discount;

        double cgst = 0.0;
        double sgst = 0.0;
        double igst = 0.0;

        for (InvoiceItem item : invoice.getItems()) {
            double itemAmount = item.getQuantity() * item.getRate();
            double itemDiscount = subtotal > 0 ? (itemAmount / subtotal) * discount : 0.0;
            double itemTaxableAmount = itemAmount - itemDiscount;
            double itemGst = itemTaxableAmount * (item.getGst() / 100.0);

            if ("intra".equalsIgnoreCase(invoice.getTaxType())) {
                cgst += itemGst / 2.0;
                sgst += itemGst / 2.0;
            } else {
                igst += itemGst;
            }
        }

        double total = taxableAmount + cgst + sgst + igst + invoice.getShipping();
        double balance = Math.max(total - invoice.getAmountPaid(), 0.0);

        invoice.setSubtotal(subtotal);
        invoice.setTaxableAmount(taxableAmount);
        invoice.setCgst(cgst);
        invoice.setSgst(sgst);
        invoice.setIgst(igst);
        invoice.setTotal(total);
        invoice.setBalance(balance);

        Invoice saved = invoiceRepository.save(invoice);
        return convertToDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<InvoiceDTO> getAllInvoices(String query) {
        List<Invoice> invoices;
        if (query != null && !query.trim().isEmpty()) {
            invoices = invoiceRepository.searchInvoices(query.trim());
        } else {
            invoices = invoiceRepository.findAllByOrderByIdDesc();
        }

        List<InvoiceDTO> dtoList = new ArrayList<>();
        for (Invoice inv : invoices) {
            dtoList.add(convertToDTO(inv));
        }
        return dtoList;
    }

    @Transactional(readOnly = true)
    public Optional<InvoiceDTO> getInvoiceById(Long id) {
        return invoiceRepository.findById(id).map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Optional<InvoiceDTO> getInvoiceByNumber(String invoiceNumber) {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber).map(this::convertToDTO);
    }

    @Transactional
    public void deleteInvoice(Long id) {
        invoiceRepository.deleteById(id);
    }

    public String generateNextInvoiceNumber() {
        int currentYear = LocalDate.now().getYear();
        long count = invoiceRepository.count();
        long next = 1001 + count;
        return String.format("INV-%d-%04d", currentYear, next);
    }

    private InvoiceDTO convertToDTO(Invoice invoice) {
        InvoiceDTO dto = new InvoiceDTO();
        dto.setId(invoice.getId());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setInvoiceDate(invoice.getInvoiceDate());
        dto.setDueDate(invoice.getDueDate());

        SellerDTO seller = new SellerDTO(
                invoice.getSellerName(),
                invoice.getSellerAddress(),
                invoice.getSellerPhone(),
                invoice.getSellerEmail(),
                invoice.getSellerGstin(),
                invoice.getSellerPan()
        );
        dto.setSeller(seller);

        CustomerDTO customer = new CustomerDTO(
                invoice.getCustomerName(),
                invoice.getCustomerCompany(),
                invoice.getCustomerAddress(),
                invoice.getCustomerPhone(),
                invoice.getCustomerEmail(),
                invoice.getCustomerGstin()
        );
        dto.setCustomer(customer);

        dto.setPlaceOfSupply(invoice.getPlaceOfSupply());
        dto.setTaxType(invoice.getTaxType());

        dto.setDiscount(invoice.getDiscount());
        dto.setShipping(invoice.getShipping());
        dto.setAmountPaid(invoice.getAmountPaid());

        dto.setSubtotal(invoice.getSubtotal());
        dto.setTaxableAmount(invoice.getTaxableAmount());
        dto.setCgst(invoice.getCgst());
        dto.setSgst(invoice.getSgst());
        dto.setIgst(invoice.getIgst());
        dto.setTotal(invoice.getTotal());
        dto.setBalance(invoice.getBalance());

        dto.setNotes(invoice.getNotes());
        dto.setTerms(invoice.getTerms());

        if (invoice.getCreatedAt() != null) {
            dto.setCreatedAt(invoice.getCreatedAt().toString());
        }
        if (invoice.getUpdatedAt() != null) {
            dto.setUpdatedAt(invoice.getUpdatedAt().toString());
        }

        List<InvoiceItemDTO> itemDtos = new ArrayList<>();
        if (invoice.getItems() != null) {
            for (InvoiceItem item : invoice.getItems()) {
                itemDtos.add(new InvoiceItemDTO(
                        item.getId(),
                        item.getName(),
                        item.getDescription(),
                        item.getQuantity(),
                        item.getRate(),
                        item.getGst()
                ));
            }
        }
        dto.setItems(itemDtos);

        return dto;
    }
}
