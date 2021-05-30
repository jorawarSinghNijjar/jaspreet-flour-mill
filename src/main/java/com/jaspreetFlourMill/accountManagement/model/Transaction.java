package com.jaspreetFlourMill.accountManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sun.javafx.beans.IDProperty;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction implements Serializable {
//    private Integer customerId;
    private Customer customer;
    private String transactionId;
    private String date;
    private double attaPickupQty;
    private double grindingRate;
    private double grindingCharges;
    private double grindingChargesPaid;
    private double customerBalanceGrindingCharges;
    private double customerStoredAttaBalanceQty;
    private String orderPickedBy;
    private String cashierName;

    public Transaction(Customer customer,double attaPickupQty,double grindingRate,double grindingCharges,
                       double grindingChargesPaid,
                       String orderPickedBy,
                       String cashierName) {
        this.customer = customer;
        this.attaPickupQty = attaPickupQty;
        this.grindingRate = grindingRate;
        this.grindingCharges = grindingCharges;
        this.grindingChargesPaid = grindingChargesPaid;
        this.orderPickedBy = orderPickedBy;
        this.cashierName = cashierName;
        UUID uuid = UUID.randomUUID();
        this.transactionId = uuid.toString();

        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss");
        this.date = formatter.format(dateTime);

        this.customerBalanceGrindingCharges = grindingCharges - grindingChargesPaid;

        // Update Database
        try{
            CustomerAccount customerAccount = CustomerAccount.getCustomerAccount(customer.getCustomerId());

            // Update customerBalanceGrindingCharges to database
            double currentGrindingChargesBalance = customerAccount.getGrindingChargesBalance();

            double updatedGrindingChargesBalance =
                    currentGrindingChargesBalance + customerBalanceGrindingCharges;

            customerAccount.setGrindingChargesBalance(updatedGrindingChargesBalance);

            // Add previous customerBalance Grinding Charges

            this.customerBalanceGrindingCharges = updatedGrindingChargesBalance;

            double currentStoredWheatBalance = customerAccount.getCurrentWheatBalance();
            customerAccount.setCurrentWheatBalance(currentStoredWheatBalance-attaPickupQty);

            this.customerStoredAttaBalanceQty = currentStoredWheatBalance-attaPickupQty;

            customerAccount.setGrindingRate(this.grindingRate);

            CustomerAccount.updateCustomerAccount(customer.getCustomerId(),customerAccount);
        }
        catch(Exception e){
            e.getMessage();
        }


    }

    public Transaction(){

    }



    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getAttaPickupQty() {
        return attaPickupQty;
    }

    public void setAttaPickupQty(double attaPickupQty) {
        this.attaPickupQty = attaPickupQty;
    }

    public double getGrindingChargesPaid() {
        return grindingChargesPaid;
    }

    public void setGrindingChargesPaid(double grindingChargesPaid) {
        this.grindingChargesPaid = grindingChargesPaid;
    }

    public double getCustomerBalanceGrindingCharges() {
        return customerBalanceGrindingCharges;
    }

    public void setCustomerBalanceGrindingCharges(double customerBalanceGrindingCharges) {
        this.customerBalanceGrindingCharges = customerBalanceGrindingCharges;
    }

    public double getCustomerStoredAttaBalanceQty() {
        return customerStoredAttaBalanceQty;
    }

    public void setCustomerStoredAttaBalanceQty(double customerStoredAttaBalanceQty) {
        this.customerStoredAttaBalanceQty = customerStoredAttaBalanceQty;
    }

    public String getOrderPickedBy() {
        return orderPickedBy;
    }

    public void setOrderPickedBy(String orderPickedBy) {
        this.orderPickedBy = orderPickedBy;
    }

    public String getCashierName() {
        return cashierName;
    }

    public void setCashierName(String cashierName) {
        this.cashierName = cashierName;
    }

    public double getGrindingRate() {
        return grindingRate;
    }

    public void setGrindingRate(double grindingRate) {
        this.grindingRate = grindingRate;
    }


    @Override
    public String toString() {
        return "Transaction{" +
                "customerId=" + customer.getCustomerId() +
                ", transactionId='" + transactionId + '\'' +
                ", date='" + date + '\'' +
                ", attaPickupQty=" + attaPickupQty +
                ", grindingCharges=" + grindingCharges +
                ", grindingChargesPaid=" + grindingChargesPaid +
                ", customerBalanceGrindingCharges=" + customerBalanceGrindingCharges +
                ", customerStoredAttaBalanceQty=" + customerStoredAttaBalanceQty +
                ", orderPickedBy='" + orderPickedBy + '\'' +
                ", cashierName='" + cashierName + '\'' +
                '}';
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
