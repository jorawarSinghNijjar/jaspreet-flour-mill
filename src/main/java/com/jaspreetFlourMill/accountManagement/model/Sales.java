package com.jaspreetFlourMill.accountManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jaspreetFlourMill.accountManagement.util.Util;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static Sales getSalesForToday(String date){
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
        System.out.println("Saving sale........");
        final String uri =  "http://localhost:8080/sales/";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Sales> req = new HttpEntity<>(sales,httpHeaders);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST,req,String.class);

        return result.getStatusCode();
    }

    public static String updateSales(String date,Sales sales) throws Exception{
        System.out.println("Updating Sales..........");
        String uri = "http://localhost:8080/sales/" + date;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Sales> req = new HttpEntity<>(sales,httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.PUT,req,String.class);
        return responseEntity.getBody();
    }
}
