package com.jaspreetFlourMill.accountManagement.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jaspreetFlourMill.accountManagement.util.AlertDialog;
import com.jaspreetFlourMill.accountManagement.util.Util;
import javafx.scene.control.Alert;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.sql.SQLOutput;
import java.util.Optional;

import static com.jaspreetFlourMill.accountManagement.util.Rest.BASE_URI;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Sales implements Serializable {

    private String date;

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    private int day;

    private int month;

    private int year;

    private Double totalStoredWheatBalance;

    private Double totalWheatDeposited;

    private Double totalWheatSold;

    private Double totalGrindingCharges;

    private Double totalGrindingChargesPaid;

    public Sales() {
    }

    public Sales(String date, Double totalWheatSold, Double totalGrindingCharges,
                 Double totalGrindingChargesPaid) {
        this.date = date;
        String[] dateStrArr = date.split("-");
        int[] dateIntArr = new int[3];
        for (int i = 0; i < dateStrArr.length; i++) {
            dateIntArr[i] = Integer.parseInt(dateStrArr[i]);
        }
        this.day = dateIntArr[2];
        this.month = dateIntArr[1];
        this.year = dateIntArr[0];
        System.out.println("Date: " + date);
        this.totalWheatSold = totalWheatSold;
        this.totalGrindingCharges = totalGrindingCharges;
        this.totalGrindingChargesPaid = totalGrindingChargesPaid;
        this.totalWheatDeposited = 0.00;
        this.totalStoredWheatBalance = 0.00;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getTotalWheatSold() {
        return totalWheatSold;
    }

    public void setTotalWheatSold(Double totalWheatSold) {
        this.totalWheatSold = totalWheatSold;
    }

    public Double getTotalGrindingCharges() {
        return totalGrindingCharges;
    }

    public void setTotalGrindingCharges(Double totalGrindingCharges) {
        this.totalGrindingCharges = totalGrindingCharges;
    }

    public Double getTotalGrindingChargesPaid() {
        return totalGrindingChargesPaid;
    }

    public void setTotalGrindingChargesPaid(Double totalGrindingChargesPaid) {
        this.totalGrindingChargesPaid = totalGrindingChargesPaid;
    }

    public Double getTotalStoredWheatBalance() {
        return totalStoredWheatBalance;
    }

    public void setTotalStoredWheatBalance(Double totalStoredWheatBalance) {
        this.totalStoredWheatBalance = totalStoredWheatBalance;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Double getTotalWheatDeposited() {
        return totalWheatDeposited;
    }

    public void updateTotalWheatDeposited(Double totalWheatDeposited) {
        this.totalWheatDeposited += totalWheatDeposited;
    }

    // GET Sales for a particular month and year
    public static Optional<Sales[]> getSalesForMonth(int month, int year) {
        try{
            System.out.println("Fetching sales for month: " + month + "-" + year);

            String uri = BASE_URI + "/sales/" + "/monthly/" + month + "/" + year;
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Sales[]> response = restTemplate.getForEntity(uri, Sales[].class);

            // Fetching Sales successful
            if (response.getStatusCode() == HttpStatus.OK) {
                Sales[] salesForMonth = response.getBody();
                System.out.println("Fetched sales: " + salesForMonth.length);
                return Optional.of(salesForMonth);
            }
        }
        catch (HttpClientErrorException.NotFound e){
            // No sales found
            System.out.println("No sales found for month: " + month + "-" + year);
            return Optional.empty();
        }
        catch (Exception e){
            // Fetching Sales failed
            System.out.println("Fetching sales failed for month: " + month + "-" + year);
            return Optional.empty();
        }
        return Optional.empty();
    }

    // GET Sales for a particular year
    public static Optional<Sales[]> getSalesForYear(int year) {
        try{
            System.out.println("Fetching sales for year: " + year);
            String uri = BASE_URI + "/sales/" + "yearly/" + year;
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Sales[]> response = restTemplate.getForEntity(uri, Sales[].class);

            // Fetching Sales successful
            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Fetching sales successful for year: " + year);
                Sales[] salesForYear = response.getBody();
                return Optional.of(salesForYear);
            }
        }
        catch (HttpClientErrorException.NotFound e){
            // No sales found
            System.out.println("No sales found for year: " + year);
            return Optional.empty();
        }
        catch (Exception e){
            // Fetching Sales failed
            System.out.println("Fetching sales failed for year: " + year);
            return Optional.empty();
        }
        return Optional.empty();




    }

    // GET Sales for a particular date
    public static Optional<Sales> getSalesForDate(String date) {
        try {
            System.out.println("Fetching sales for date: " + date);
            String uri = BASE_URI + "/sales/" + date;
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Sales> responseEntity = restTemplate.getForEntity(uri, Sales.class);

            // Fetching Sales successful
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                System.out.println("Fetching sales successful for date: " + date);
                Sales sales = responseEntity.getBody();
                return Optional.of(sales);
            }
        } catch (HttpClientErrorException.NotFound e) {
            // Sales not found
            System.out.println("Sales not found for: " + date);
            return Optional.empty();
        }
        return Optional.empty();
    }

    // POST Sales
    public static boolean saveSales(Sales sales) {
        try {
            System.out.println("Saving sale ........");
            final String uri = BASE_URI + "/sales/";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Sales> req = new HttpEntity<>(sales, httpHeaders);
            ResponseEntity<Sales> result = restTemplate.exchange(uri, HttpMethod.POST, req, Sales.class);

            // Saving Sale Successful
            if (result.getStatusCode() == HttpStatus.CREATED) {
                System.out.println("Saving Sale successful for: " + sales.getDate());
                return true;
            }

        } catch (Exception e) {
            // Employee registration failed
            String errMessage = "Saving sale failed for: " + sales.getDate();
            System.out.println(errMessage);
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error", errMessage, e.getMessage(), Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
            return false;
        }
        return false;
    }

    // UPDATE Sales
    public static boolean updateSales(String date, Sales sales) {
        try {
            System.out.println("Updating Sale..........");
            String uri = BASE_URI + "/sales/" + date;
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Sales> req = new HttpEntity<>(sales, httpHeaders);
            ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.PUT, req, String.class);
            // Updating Sale Successful
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                System.out.println("Updating Sale successful for: " + sales.getDate());
                return true;
            }
        } catch (Exception e) {
            // Employee registration failed
            String errMessage = "Updating Sale failed for: " + sales.getDate();
            System.out.println(errMessage);
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error", errMessage, e.getMessage(), Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
            return false;
        }
        return false;
    }

    // Add Wheat amount to Sales and Stock table
    public static void addWheatDeposit(double wheatDepositQty) {
        System.out.println("Adding wheat amount - " + wheatDepositQty + " to wheatBalance....");
        Stock.getStock().ifPresent(stock -> {
            stock.addWheat(wheatDepositQty);
            Stock.updateStock(stock);
            Sales.updateWheatBalanceInSales(stock.getWheatBalance(), wheatDepositQty, true);
        });
    }

    // Deduct Wheat amount from Sales and Stock table
    public void deductWheatSold(double flourPickupQty) {
        System.out.println("Deducting wheat amount - " + flourPickupQty + " from wheatBalance....");
        Stock.getStock().ifPresent(stock -> {
            stock.deductWheat(flourPickupQty);
            Stock.updateStock(stock);
            Sales.updateWheatBalanceInSales(stock.getWheatBalance(), 0.00, false);
        });


    }

    // UPDATE Wheat Balance in Sales Table
    private static void updateWheatBalanceInSales(
            double wheatBalance,
            double wheatDepositQty,
            boolean wheatDeposit
    ) {
        try {
            // Update stocks in Sales table
            Optional<Sales> sales = Sales.getSalesForDate(Util.getDateForToday());
            if (sales.isPresent()) {
                System.out.println("Updating total stored wheat balance in sales table...");
                sales.get().setTotalStoredWheatBalance(wheatBalance);

                if (wheatDeposit) {
                    System.out.println("Updating total wheat deposit in sales table...");
                    sales.get().updateTotalWheatDeposited(wheatDepositQty);
                }
                // Update sales on backend
                Sales.updateSales(Util.getDateForToday(), sales.get());
            }
        } catch (HttpClientErrorException.NotFound e) {
            System.out.println("Today's first update for total stored wheat balance...");
            Sales sale = new Sales(
                    Util.getDateForToday(),
                    0.00,
                    0.00,
                    0.00);
            sale.setTotalStoredWheatBalance(wheatBalance);
            if (wheatDeposit) {
                sale.updateTotalWheatDeposited(wheatDepositQty);
            }
            Sales.saveSales(sale);
        }
    }


    @Override
    public String toString() {
        return "Sales{" +
                "date='" + date + '\'' +
                ", day=" + day +
                ", month=" + month +
                ", year=" + year +
                ", totalStoredWheatBalance=" + totalStoredWheatBalance +
                ", totalWheatDeposited=" + totalWheatDeposited +
                ", totalWheatSold=" + totalWheatSold +
                ", totalGrindingCharges=" + totalGrindingCharges +
                ", totalGrindingChargesPaid=" + totalGrindingChargesPaid +
                '}';
    }
}
