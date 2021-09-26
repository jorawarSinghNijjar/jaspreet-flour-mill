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

    public void setTotalWheatDeposited(Double totalWheatDeposited) {
        this.totalWheatDeposited = totalWheatDeposited;
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

        Stock stock = Stock.getStock();
        if(stock == null ){
            System.out.println("Stock is null");
            Stock newStock = new Stock();
            newStock.addWheat(wheatDepositQty);
            Stock.saveStock(newStock);
        }
        else{
            stock.addWheat(wheatDepositQty);
            Stock.updateStock(stock);

            // Update stocks in Sales table

            Sales sales = Sales.getSalesForDate(Util.getDateForToday());

            if(sales != null){
                System.out.println("Updating total stored wheat balance...");
                Double currentTotalStoredWheatBal = sales.getTotalStoredWheatBalance();
                currentTotalStoredWheatBal += wheatDepositQty;
                sales.setTotalStoredWheatBalance(currentTotalStoredWheatBal);
                Sales.updateSales(Util.getDateForToday(),sales);
                System.out.println("Total wheat stored: " + currentTotalStoredWheatBal);
            }
            else{
                System.out.println("Today's first update for total stored wheat balance...");
                Double currentTotalStoredWheatBal = stock.getWheatBalance();
                currentTotalStoredWheatBal += wheatDepositQty;
                Sales sale = new Sales(
                        Util.getDateForToday(),
                        0.00,
                        0.00,
                        0.00);
                sale.setTotalStoredWheatBalance(currentTotalStoredWheatBal);
                Sales.saveSales(sale);
            }
        }


//        else{
//            System.out.println("Today's first update for total stored wheat balance...");
//            Sales yesterdaySales = Sales.getSalesForDate(Util.getDateForYesterday());
//            if(yesterdaySales != null){
//                 Double currentTotalStoredWheatBal = 0.00;
//                if(yesterdaySales.getTotalStoredWheatBalance() == null){
//                    currentTotalStoredWheatBal = wheatDepositQty;
//                }else {
//                    currentTotalStoredWheatBal= yesterdaySales.getTotalStoredWheatBalance();
//                    currentTotalStoredWheatBal += wheatDepositQty;
//                }
//                yesterdaySales.setTotalStoredWheatBalance(currentTotalStoredWheatBal);
//                System.out.println(yesterdaySales.toString());
//                Sales.updateSales(Util.getDateForToday(), yesterdaySales);
//            }
//            else {
//                Sales sale = new Sales(
//                        Util.getDateForToday(),
//                        0.00,
//                        0.00,
//                        0.00);
//                sale.setTotalStoredWheatBalance(wheatDepositQty);
//                Sales.saveSales(sale);
//            }
//        }


    }

    public void deductWheatSold(double attaPickupQty) throws Exception{

        Stock stock = Stock.getStock();
        if(stock == null ){
            System.out.println("Stock is null");
        }
        else{
            stock.deductWheat(attaPickupQty);
            Stock.updateStock(stock);

            // Update Stock in sales table
            Sales sales = Sales.getSalesForDate(Util.getDateForToday());

            if(sales != null){
                System.out.println("Deducting total stored wheat balance...");
                Double currentTotalStoredWheatBal = sales.getTotalStoredWheatBalance();
                currentTotalStoredWheatBal -= attaPickupQty;
                sales.setTotalStoredWheatBalance(currentTotalStoredWheatBal);
                Sales.updateSales(Util.getDateForToday(),sales);
                System.out.println("Total wheat stored: " + currentTotalStoredWheatBal);
            }
            else{
                System.out.println("Today's first update for total stored wheat balance...");
                Double currentTotalStoredWheatBal = stock.getWheatBalance();
                currentTotalStoredWheatBal -= attaPickupQty;
                Sales sale = new Sales(
                        Util.getDateForToday(),
                        0.00,
                        0.00,
                        0.00);
                sale.setTotalStoredWheatBalance(currentTotalStoredWheatBal);
                Sales.saveSales(sale);
        }


//            Sales yesterdaySales = Sales.getSalesForDate(Util.getDateForYesterday());
//            if(yesterdaySales != null){
//                Double currentTotalStoredWheatBal = 0.00;
//                if(yesterdaySales.getTotalStoredWheatBalance() == null){
//                    System.out.println("Total Stored Wheat Balance is null");
//                    currentTotalStoredWheatBal = 0.00;
//                }else {
//                    currentTotalStoredWheatBal= yesterdaySales.getTotalStoredWheatBalance();
//                    currentTotalStoredWheatBal -= attaPickupQty;
//                }
//                yesterdaySales.setTotalStoredWheatBalance(currentTotalStoredWheatBal);
//                System.out.println(yesterdaySales.toString());
//                Sales.updateSales(Util.getDateForToday(), yesterdaySales);
//            }
//            else {
//                System.out.println("Company's stored wheat balance is 0");
//            }
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
