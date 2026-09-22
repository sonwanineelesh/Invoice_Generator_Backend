package com.invoice.generator.dto;

public class CustomerDTO {
    private String name;
    private String company;
    private String address;
    private String phone;
    private String email;
    private String gstin;

    public CustomerDTO() {
    }

    public CustomerDTO(String name, String company, String address, String phone, String email, String gstin) {
        this.name = name;
        this.company = company;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.gstin = gstin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
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
}
