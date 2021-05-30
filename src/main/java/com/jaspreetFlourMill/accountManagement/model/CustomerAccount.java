package com.jaspreetFlourMill.accountManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerAccount implements Serializable {
    private Integer customerAccountId;
//    private Integer customerId;
    private Customer customer;
    private double wheatDepositQty;
    private double wheatProcessingDeductionQty;
    private double initialWheatQty;
    private double currentWheatBalance;
    private double grindingChargesBalance;
    private double grindingRate;
    private String startDate;

    public CustomerAccount(Customer customer, double wheatDepositQty, double wheatProcessingDeductionQty) {
        this.customer = customer;
        this.wheatDepositQty = wheatDepositQty;
        this.wheatProcessingDeductionQty = wheatProcessingDeductionQty;
        this.initialWheatQty = wheatDepositQty - wheatProcessingDeductionQty;
        this.currentWheatBalance = initialWheatQty;
        this.grindingChargesBalance = 0;
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss");
        this.startDate = formatter.format(dateTime);
    }

    public void setWheatProcessingDeductionQty(double wheatProcessingDeductionQty) {
        this.wheatProcessingDeductionQty = wheatProcessingDeductionQty;
    }

    public void setGrindingChargesBalance(double grindingChargesBalance) {
        this.grindingChargesBalance = grindingChargesBalance;
    }

    public CustomerAccount(){

    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }


    public double getWheatDepositQty() {
        return wheatDepositQty;
    }

    public void setWheatDepositQty(double wheatDepositQty) {
        this.wheatDepositQty = wheatDepositQty;
    }

    public double getWheatProcessingDeductionQty() {
        return wheatProcessingDeductionQty;
    }

    public double getInitialWheatQty() {
        return initialWheatQty;
    }

    public void setInitialWheatQty(double initialWheatQty) {
        this.initialWheatQty = initialWheatQty;
    }

    public double getCurrentWheatBalance() {
        return currentWheatBalance;
    }

    public void setCurrentWheatBalance(double currentWheatBalance) {
        this.currentWheatBalance = currentWheatBalance;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public Integer getCustomerAccountId() {
        return customerAccountId;
    }

    public void setCustomerAccountId(Integer customerAccountId) {
        this.customerAccountId = customerAccountId;
    }

    public double getGrindingRate() {
        return grindingRate;
    }

    public void setGrindingRate(double grindingRate) {
        this.grindingRate = grindingRate;
    }

    public double getGrindingChargesBalance() {
        return grindingChargesBalance;
    }

    public void addWheatToAccount(double wheatDepositQty, double wheatProcessingDeductionQty){
        this.wheatDepositQty += wheatDepositQty;
        this.initialWheatQty += (wheatDepositQty-wheatProcessingDeductionQty);
        this.currentWheatBalance = this.initialWheatQty;
        this.wheatProcessingDeductionQty += wheatProcessingDeductionQty;
    }

    // Get Customer account by id from DB

    public static CustomerAccount getCustomerAccount(Integer id) throws Exception{
        String uri = "http://localhost:8080/customer-accounts/" + id;
        RestTemplate restTemplate = new RestTemplate();
        CustomerAccount responseEntity = restTemplate.getForObject(uri,CustomerAccount.class);
        return responseEntity;
    }

    public static String updateCustomerAccount(Integer id,CustomerAccount customerAccount) throws Exception{
        String uri = "http://localhost:8080/customer-accounts/" + id;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CustomerAccount> req = new HttpEntity<>(customerAccount,httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.PUT,req,String.class);
        return responseEntity.getBody();
    }

    @Override
    public String toString() {
        return "CustomerAccount{" +
                "customerAccountId=" + customerAccountId +
                ", customer=" + customer +
                ", wheatDepositQty=" + wheatDepositQty +
                ", wheatProcessingDeductionQty=" + wheatProcessingDeductionQty +
                ", initialWheatQty=" + initialWheatQty +
                ", currentWheatBalance=" + currentWheatBalance +
                ", grindingChargesBalance=" + grindingChargesBalance +
                ", startDate='" + startDate + '\'' +
                '}';
    }
}
