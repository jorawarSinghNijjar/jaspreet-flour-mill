package com.jaspreetFlourMill.accountManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SalesToday {
    private Double wheatSold;
    private Double grindingChargesPaid;
    private Double grindingCharges;
    private String date;

    public SalesToday() {
    }

    public SalesToday(Double wheatSold, Double grindingChargesPaid, Double grindingCharges, String date) {
        this.wheatSold = wheatSold;
        this.grindingChargesPaid = grindingChargesPaid;
        this.grindingCharges = grindingCharges;
        this.date = date;
    }

    public Double getWheatSold() {
        return wheatSold;
    }

    public void setWheatSold(Double wheatSold) {
        this.wheatSold = wheatSold;
    }

    public Double getGrindingChargesPaid() {
        return grindingChargesPaid;
    }

    public void setGrindingChargesPaid(Double grindingChargesPaid) {
        this.grindingChargesPaid = grindingChargesPaid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getGrindingCharges() {
        return grindingCharges;
    }

    public void setGrindingCharges(Double grindingCharges) {
        this.grindingCharges = grindingCharges;
    }
}
