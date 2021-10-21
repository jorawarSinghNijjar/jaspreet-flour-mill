package com.jaspreetFlourMill.accountManagement.model;

public class MonthlySales {
    private int month;
    private double totalWheatSold;
    private double totalWheatDeposited;
    private double totalGrindingAmount;
    private double totalGrindingAmountReceived;

    public MonthlySales(int month, Sales[] salesForMonth) {
        this.month = month;
        this.totalWheatSold = 0;
        this.totalWheatDeposited = 0;
        this.totalGrindingAmount = 0;
        this.totalGrindingAmountReceived = 0;

        if(salesForMonth != null && salesForMonth.length !=0) {
            for (Sales sale : salesForMonth) {
                totalWheatSold += sale.getTotalWheatSold();
                totalWheatDeposited += sale.getTotalWheatDeposited();
                totalGrindingAmount += sale.getTotalGrindingCharges();
                totalGrindingAmountReceived += sale.getTotalGrindingChargesPaid();
            }
        }
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public double getTotalWheatSold() {
        return totalWheatSold;
    }

    public void setTotalWheatSold(double totalWheatSold) {
        this.totalWheatSold = totalWheatSold;
    }

    public double getTotalGrindingAmount() {
        return totalGrindingAmount;
    }

    public void setTotalGrindingAmount(double totalGrindingAmount) {
        this.totalGrindingAmount = totalGrindingAmount;
    }

    public double getTotalGrindingAmountReceived() {
        return totalGrindingAmountReceived;
    }

    public void setTotalGrindingAmountReceived(double totalGrindingAmountReceived) {
        this.totalGrindingAmountReceived = totalGrindingAmountReceived;
    }
    public double getTotalWheatDeposited() {
        return totalWheatDeposited;
    }

    public void setTotalWheatDeposited(double totalWheatDeposited) {
        this.totalWheatDeposited = totalWheatDeposited;
    }

}
