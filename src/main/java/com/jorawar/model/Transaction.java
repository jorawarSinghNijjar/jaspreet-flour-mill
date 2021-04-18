package com.jorawar.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

public class Transaction {
    private String customerId;
    private String transactionId;
    private String date;
    private double attaPickupQty;
    private double grindingChargesPaid;
    private double customerBalanceGrindingCharges;
    private double customerStoredAttaBalanceQty;
    private String orderPickedBy;
    private String cashierName;

    public Transaction(String customerId,double attaPickupQty, double grindingChargesPaid, String orderPickedBy, String cashierName) {
        this.customerId = customerId;
        this.attaPickupQty = attaPickupQty;
        this.grindingChargesPaid = grindingChargesPaid;
        this.orderPickedBy = orderPickedBy;
        this.cashierName = cashierName;
        UUID uuid = UUID.randomUUID();
        this.transactionId = uuid.toString();

        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss");
        this.date = formatter.format(dateTime);

        this.customerBalanceGrindingCharges = 100;
        this.customerStoredAttaBalanceQty = 300;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
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
}
