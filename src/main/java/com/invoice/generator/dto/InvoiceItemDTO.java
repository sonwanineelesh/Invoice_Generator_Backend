package com.invoice.generator.dto;

public class InvoiceItemDTO {

    private Long id;
    private String name;
    private String description;
    private Double quantity;
    private Double rate;
    private Double gst;

    public InvoiceItemDTO() {
    }

    public InvoiceItemDTO(Long id, String name, String description, Double quantity, Double rate, Double gst) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.rate = rate;
        this.gst = gst;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Double getGst() {
        return gst;
    }

    public void setGst(Double gst) {
        this.gst = gst;
    }
}
