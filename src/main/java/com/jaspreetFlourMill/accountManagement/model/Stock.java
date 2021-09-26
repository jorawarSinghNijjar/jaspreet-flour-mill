package com.jaspreetFlourMill.accountManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.naming.directory.SearchResult;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Stock implements Serializable {

    private  Integer id;

    private Double wheatBalance;

    public Stock() {
        this.wheatBalance = 0.00;
    }

    public void addWheat(double wheatQty){
        this.wheatBalance += wheatQty;
    }

    public void deductWheat(double wheatQty){
        this.wheatBalance -= wheatQty;
    }

    public Double getWheatBalance() {
        return wheatBalance;
    }

    public void setWheatBalance(Double wheatBalance) {
        this.wheatBalance = wheatBalance;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public static HttpStatus saveStock(Stock stock){
        try {
            System.out.println("Saving sale........");
            final String uri =  "http://localhost:8080/stock/";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Stock> req = new HttpEntity<>(stock,httpHeaders);
            ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST,req,String.class);

            return result.getStatusCode();
        }
        catch (Exception e){
           e.printStackTrace();
            return null;
        }

    }


    public static String updateStock(Stock stock){
        try{
            System.out.println("Updating Sales..........");
            String uri = "http://localhost:8080/stock/update";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Stock> req = new HttpEntity<>(stock,httpHeaders);
            ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.PUT,req,String.class);
            return responseEntity.getBody();
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public static Stock getStock() {
        try {
            String uri = "http://localhost:8080/stock/get";
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Stock> responseEntity = restTemplate.getForEntity(uri,Stock.class);
            return responseEntity.getBody();
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public String toString() {
        return "Stock{" +
                "id=" + id +
                ", wheatBalance=" + wheatBalance +
                '}';
    }
}
