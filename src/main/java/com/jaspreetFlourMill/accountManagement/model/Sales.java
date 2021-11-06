package com.jaspreetFlourMill.accountManagement.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jaspreetFlourMill.accountManagement.util.Util;
import org.springframework.http.*;
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
    public static Optional<Sales[]> getSalesForMonth(int month, int year) throws Exception {
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

        // Fetching Sales failed
        System.out.println("Fetching sales failed for month: " + month + "-" + year);
        return Optional.empty();
    }

    // GET Sales for a particular year
    public static Optional<Sales[]> getSalesForYear(int year) throws Exception {
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

        // Fetching Sales failed
        System.out.println("Fetching sales failed for year: " + year);
        return Optional.empty();

    }

    // GET Sales for a particular date
    public static Optional<Sales> getSalesForDate(String date) throws Exception {
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

        // Fetching Sales failed
        System.out.println("Fetching sales failed for date: " + date);
        return Optional.empty();
    }

    // POST Sales
    public static boolean saveSales(Sales sales) throws Exception {
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

        // Saving Sale failed
        System.out.println("Saving Sale failed for: " + sales.getDate());
        return false;
    }

    // UPDATE Sales
    public static boolean updateSales(String date, Sales sales) throws Exception {

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

        // Updating Sale failed
        System.out.println("Updating Sale failed for: " + sales.getDate());
        return false;

    }

    // Add Wheat amount to Sales and Stock table
    public static void addWheatDeposit(double wheatDepositQty) throws Exception {
        System.out.println("Adding wheat amount - " + wheatDepositQty + " to wheatBalance....");
        Stock stock = Stock.getStock().orElseThrow();
        stock.addWheat(wheatDepositQty);
        Stock.updateStock(stock);
        Sales.updateWheatBalanceInSales(stock.getWheatBalance(), wheatDepositQty, true);
    }

    // Deduct Wheat amount from Sales and Stock table
    public void deductWheatSold(double flourPickupQty) throws Exception {
        System.out.println("Deducting wheat amount - " + flourPickupQty + " from wheatBalance....");
        Stock stock = Stock.getStock().orElseThrow();
        stock.deductWheat(flourPickupQty);
        Stock.updateStock(stock);
        Sales.updateWheatBalanceInSales(stock.getWheatBalance(), 0.00, false);
    }

    // UPDATE Wheat Balance in Sales Table
    private static void updateWheatBalanceInSales(
            double wheatBalance,
            double wheatDepositQty,
            boolean wheatDeposit
    ) throws Exception {

        // Update stocks in Sales table
        Sales sales = Sales.getSalesForDate(Util.getDateForToday()).orElseThrow();
        if (sales != null) {
            System.out.println("Updating total stored wheat balance in sales table...");
            sales.setTotalStoredWheatBalance(wheatBalance);

            if (wheatDeposit) {
                System.out.println("Updating total wheat deposit in sales table...");
                sales.updateTotalWheatDeposited(wheatDepositQty);
            }
            // Update sales on backend
            Sales.updateSales(Util.getDateForToday(), sales);
        } else {
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
