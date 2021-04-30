package com.jaspreetFlourMill.accountManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer implements Serializable {
    private Integer customerId;
    private String name;
    private String address;
    private String phoneNumber;
    private String rationCardNo;
    private String dob;
    private String adhaarNo;
    private String idProof;

    public Customer(String name, String address, String phoneNumber, String rationCardNo, LocalDate dob, String adhaarNo,
                    String idProof) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.rationCardNo = rationCardNo;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        this.dob = formatter.format(dob);
        this.adhaarNo = adhaarNo;
        this.idProof = idProof;
    }

    public Customer(){

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRationCardNo() {
        return rationCardNo;
    }

    public void setRationCardNo(String rationCardNo) {
        this.rationCardNo = rationCardNo;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAdhaarNo() {
        return adhaarNo;
    }

    public void setAdhaarNo(String adhaarNo) {
        this.adhaarNo = adhaarNo;
    }

    public String getIdProof() {
        return idProof;
    }

    public void setIdProof(String idProof) {
        this.idProof = idProof;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", rationCardNo='" + rationCardNo + '\'' +
                ", dob='" + dob + '\'' +
                ", adhaarNo='" + adhaarNo + '\'' +
                ", idProof=" + idProof +
                '}';
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
}
