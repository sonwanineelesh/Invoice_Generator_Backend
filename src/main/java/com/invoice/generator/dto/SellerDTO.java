package com.invoice.generator.dto;

public class SellerDTO {
    private String name;
    private String address;
    private String phone;
    private String email;
    private String gstin;
    private String pan;

    public SellerDTO() {
    }

    public SellerDTO(String name, String address, String phone, String email, String gstin, String pan) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.gstin = gstin;
        this.pan = pan;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGstin() {
        return gstin;
    }

    public void setGstin(String gstin) {
        this.gstin = gstin;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }
}
