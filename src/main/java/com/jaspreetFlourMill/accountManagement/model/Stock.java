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
        this.id = 1;
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

//    public static boolean initializeStock(){
//        System.out.println("Initializing the company stock..");
//        try {
//            ResponseEntity<Stock> resultFromGet = Stock.getStock();
//            if(resultFromGet.getStatusCode() == HttpStatus.NOT_FOUND){
//                ResponseEntity<String> resultFromSave = Stock.saveStock(new Stock());
//
//                if(resultFromSave.getStatusCode() == HttpStatus.OK){
//                    System.out.println("Stock initialized");
//                    return true;
//                }
//            }
//        }
//        catch (Exception e){
//            System.out.println("Initialization failed with error!!");
//            e.printStackTrace();
//        }
//        return false;
//    }

    public static ResponseEntity<String> saveStock(Stock stock){
        try {
            System.out.println("Saving stock........");
            final String uri =  "http://localhost:8080/stock/";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Stock> req = new HttpEntity<>(stock,httpHeaders);
            ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST,req,String.class);

            return result;
        }
        catch (Exception e){
           e.printStackTrace();
            return null;
        }

    }


    public static String updateStock(Stock stock){
        try{
            System.out.println("Updating Stock..........");
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

    public static ResponseEntity<Stock> getStock(){
        try {
            String uri = "http://localhost:8080/stock/get";
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Stock> responseEntity = restTemplate.getForEntity(uri,Stock.class);
            return responseEntity;
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
