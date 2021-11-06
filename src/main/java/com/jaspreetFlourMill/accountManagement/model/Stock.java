package com.jaspreetFlourMill.accountManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.naming.directory.SearchResult;
import java.io.Serializable;
import java.util.Optional;

import static com.jaspreetFlourMill.accountManagement.util.Rest.BASE_URI;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Stock implements Serializable {

    private Integer id;

    private Double wheatBalance;

    public Stock() {
        this.id = 1;
        this.wheatBalance = 0.00;
    }

    public void addWheat(double wheatQty) {
        this.wheatBalance += wheatQty;
    }

    public void deductWheat(double wheatQty) {
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

    // Save stock
    public static Optional<Stock> saveStock(Stock stock) throws Exception{
        System.out.println("Saving stock........");
        final String uri = BASE_URI + "/stocks/";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Stock> req = new HttpEntity<>(stock, httpHeaders);
        ResponseEntity<Stock> result = restTemplate.exchange(uri, HttpMethod.POST, req, Stock.class);

        // Saving stock successful
        if (result.getStatusCode() == HttpStatus.CREATED) {
            Stock savedStock = result.getBody();
            System.out.println("Saved stock, Current Wheat Balance: " + savedStock.getWheatBalance());
            return Optional.of(savedStock);
        }

        // Saving stock unsuccessful
        System.out.println("Failed to save stock, Current Wheat Balance: " + stock.getWheatBalance());
        return Optional.empty();
    }

    // UPDATE Stock
    public static Optional<Stock> updateStock(Stock stock) throws Exception{

        System.out.println("Updating Stock..........");
        String uri = BASE_URI + "/stocks/update";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Stock> req = new HttpEntity<>(stock, httpHeaders);
        ResponseEntity<Stock> responseEntity = restTemplate.exchange(uri, HttpMethod.PUT, req, Stock.class);

        // Updating stock successful
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            Stock updatedStock = responseEntity.getBody();
            System.out.println("Updated stock, Current Wheat Balance: " + updatedStock.getWheatBalance());
            return Optional.of(updatedStock);
        }

        // Updating stock unsuccessful
        System.out.println("Failed to update stock, Current Wheat Balance: " + stock.getWheatBalance());
        return Optional.empty();


    }

    // GET stock
    public static Optional<Stock> getStock() throws Exception{
        String uri = BASE_URI + "/stocks/get";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Stock> responseEntity = restTemplate.getForEntity(uri, Stock.class);

        // Fetching stock successful
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            Stock stock = responseEntity.getBody();
            System.out.println("Fetched stock, Current Wheat Balance: " + stock.getWheatBalance());
            return Optional.of(stock);
        }

        // Fetching stock unsuccessful
        System.out.println("Failed to fetch stock !!!");
        return Optional.empty();
    }

    @Override
    public String toString() {
        return "Stock{" +
                "id=" + id +
                ", wheatBalance=" + wheatBalance +
                '}';
    }
}
