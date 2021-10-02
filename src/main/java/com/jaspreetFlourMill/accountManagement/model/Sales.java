package com.jaspreetFlourMill.accountManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jaspreetFlourMill.accountManagement.util.Util;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;

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

    public Sales(){}

    public Sales(String date, Double totalWheatSold, Double totalGrindingCharges,
                 Double totalGrindingChargesPaid) {
        this.date = date;
        String[] dateStrArr = date.split("-");
        int[] dateIntArr = new int[3];
        for(int i=0; i<dateStrArr.length; i++){
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

    public static Sales[] getSalesForMonth(int month, int year){

        try{
            System.out.println("Retrieving sales for month: " + month + "-" + year );
            String uri = "http://localhost:8080/sales/" + "/monthly/"+month + "/" + year;
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Sales[]> response = restTemplate.getForEntity(uri,Sales[].class);
            Sales[] salesForMonth = response.getBody();
            return salesForMonth;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public static Sales[] getSalesForYear(int year){

        try{
            System.out.println("Retrieving sales for year: " + year );
            String uri = "http://localhost:8080/sales/" + "yearly/"+ year;
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Sales[]> response = restTemplate.getForEntity(uri,Sales[].class);
            Sales[] salesForYear = response.getBody();
            return salesForYear;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public static Sales getSalesForDate(String date){
        try{
            System.out.println("Retrieving sales for date: " + date);
            String uri = "http://localhost:8080/sales/" + date;
            RestTemplate restTemplate = new RestTemplate();
            Sales responseEntity = restTemplate.getForObject(uri,Sales.class);
//            System.out.println("Sales retrieved: "+ responseEntity);
            return responseEntity;
        }
       catch (Exception e){
            e.getMessage();
            return null;
       }
    }

    public static HttpStatus saveSales(Sales sales){
        try {
            System.out.println("Saving sale........");
            final String uri =  "http://localhost:8080/sales/";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Sales> req = new HttpEntity<>(sales,httpHeaders);
            ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST,req,String.class);

            return result.getStatusCode();
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public static String updateSales(String date,Sales sales) {
        try{
            System.out.println("Updating Sales..........");
            String uri = "http://localhost:8080/sales/" + date;
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Sales> req = new HttpEntity<>(sales,httpHeaders);
            ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.PUT,req,String.class);
            return responseEntity.getBody();
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public static void addWheatDeposit(double wheatDepositQty) {
        System.out.println("addWheatDeposit()");
        try {
            ResponseEntity<Stock> response = Stock.getStock();
            if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                // Needs to be changed
                System.out.println("Stock not found");
            } else {
                Stock stock = response.getBody();
                stock.addWheat(wheatDepositQty);
                Stock.updateStock(stock);
                Sales.updateWheatBalanceInSales(stock.getWheatBalance(), wheatDepositQty,true);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void deductWheatSold(double flourPickupQty) throws Exception{
        try {
            ResponseEntity<Stock> response = Stock.getStock();
            if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                System.out.println("Stock not found");
            } else {
                Stock stock = response.getBody();
                stock.deductWheat(flourPickupQty);
                Stock.updateStock(stock);
                Sales.updateWheatBalanceInSales(stock.getWheatBalance(),0.00,false);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }


    private static void updateWheatBalanceInSales(
            double wheatBalance,
            double wheatDepositQty,
            boolean wheatDeposit
            ){
        // Update stocks in Sales table

        Sales sales = Sales.getSalesForDate(Util.getDateForToday());
        if(sales != null){
            System.out.println("Updating total stored wheat balance in sales table...");
            sales.setTotalStoredWheatBalance(wheatBalance);

            if(wheatDeposit){
                System.out.println("Updating total wheat deposit in sales table...");
                sales.updateTotalWheatDeposited(wheatDepositQty);
            }
            // Update sales on backend
            Sales.updateSales(Util.getDateForToday(),sales);
        }
        else{
            System.out.println("Today's first update for total stored wheat balance...");
            Sales sale = new Sales(
                    Util.getDateForToday(),
                    0.00,
                    0.00,
                    0.00);
            sale.setTotalStoredWheatBalance(wheatBalance);
            if(wheatDeposit){
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
