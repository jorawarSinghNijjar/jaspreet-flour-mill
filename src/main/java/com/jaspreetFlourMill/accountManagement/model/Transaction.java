package com.jaspreetFlourMill.accountManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jaspreetFlourMill.accountManagement.util.AlertDialog;
import javafx.scene.control.Alert;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.jaspreetFlourMill.accountManagement.util.Rest.BASE_URI;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction implements Serializable {
//    private Integer customerId;
    private Customer customer;
    private String transactionId;
    private String date;
    private String time;
    private double FlourPickupQty;
    private double grindingRate;
    private double grindingCharges;
    private double grindingChargesPaid;
    private double customerBalanceGrindingCharges;
    private double customerStoredFlourBalanceQty;
    private String orderPickedBy;
    private String cashierName;

    public Transaction(Customer customer, double FlourPickupQty, double grindingRate, double grindingCharges,
                       double grindingChargesPaid,
                       String orderPickedBy,
                       String cashierName) {
        this.customer = customer;
        this.FlourPickupQty = FlourPickupQty;
        this.grindingRate = grindingRate;
        this.grindingCharges = grindingCharges;
        this.grindingChargesPaid = grindingChargesPaid;
        this.orderPickedBy = orderPickedBy;
        this.cashierName = cashierName;
        UUID uuid = UUID.randomUUID();
        this.transactionId = uuid.toString();

        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        this.date = dateFormat.format(dateTime);
        this.time = timeFormat.format(dateTime);

        this.customerBalanceGrindingCharges = grindingCharges - grindingChargesPaid;

        // Update Transaction Details in Database
        try{
            CustomerAccount customerAccount = CustomerAccount.get(customer.getCustomerId()).orElseThrow();

            // Update customerBalanceGrindingCharges to database
            double currentGrindingChargesBalance = customerAccount.getGrindingChargesBalance();

            double updatedGrindingChargesBalance =
                    currentGrindingChargesBalance + customerBalanceGrindingCharges;

            customerAccount.setGrindingChargesBalance(updatedGrindingChargesBalance);

            // Add previous customerBalance Grinding Charges

            this.customerBalanceGrindingCharges = updatedGrindingChargesBalance;

            double currentStoredWheatBalance = customerAccount.getCurrentWheatBalance();
            customerAccount.setCurrentWheatBalance(currentStoredWheatBalance- FlourPickupQty);

            this.customerStoredFlourBalanceQty = currentStoredWheatBalance- FlourPickupQty;

            customerAccount.setGrindingRate(this.grindingRate);

            CustomerAccount.update(customer.getCustomerId(),customerAccount);
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Transaction detail update failed !!!");
        }


    }

    public Transaction(){

    }



    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getFlourPickupQty() {
        return FlourPickupQty;
    }

    public void setFlourPickupQty(double flourPickupQty) {
        this.FlourPickupQty = flourPickupQty;
    }

    public double getGrindingChargesPaid() {
        return grindingChargesPaid;
    }

    public void setGrindingChargesPaid(double grindingChargesPaid) {
        this.grindingChargesPaid = grindingChargesPaid;
    }

    public double getCustomerBalanceGrindingCharges() {
        return customerBalanceGrindingCharges;
    }

    public void setCustomerBalanceGrindingCharges(double customerBalanceGrindingCharges) {
        this.customerBalanceGrindingCharges = customerBalanceGrindingCharges;
    }

    public double getCustomerStoredFlourBalanceQty() {
        return customerStoredFlourBalanceQty;
    }

    public void setCustomerStoredFlourBalanceQty(double customerStoredFlourBalanceQty) {
        this.customerStoredFlourBalanceQty = customerStoredFlourBalanceQty;
    }

    public String getOrderPickedBy() {
        return orderPickedBy;
    }

    public void setOrderPickedBy(String orderPickedBy) {
        this.orderPickedBy = orderPickedBy;
    }

    public String getCashierName() {
        return cashierName;
    }

    public void setCashierName(String cashierName) {
        this.cashierName = cashierName;
    }

    public double getGrindingRate() {
        return grindingRate;
    }

    public void setGrindingRate(double grindingRate) {
        this.grindingRate = grindingRate;
    }
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getGrindingCharges() {
        return grindingCharges;
    }

    public void setGrindingCharges(double grindingCharges) {
        this.grindingCharges = grindingCharges;
    }


    @Override
    public String toString() {
        return "Transaction{" +
                "customerId=" + customer.getCustomerId() +
                ", transactionId='" + transactionId + '\'' +
                ", date='" + date + '\'' +
                ", flourPickupQty=" + FlourPickupQty +
                ", grindingCharges=" + grindingCharges +
                ", grindingChargesPaid=" + grindingChargesPaid +
                ", customerBalanceGrindingCharges=" + customerBalanceGrindingCharges +
                ", customerStoredFlourBalanceQty=" + customerStoredFlourBalanceQty +
                ", orderPickedBy='" + orderPickedBy + '\'' +
                ", cashierName='" + cashierName + '\'' +
                '}';
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    // GET ALL

    public static List<Transaction> getAll(String customerId) {
        try {
            String uri = BASE_URI + "/transactions/query?customerId=" + customerId;
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Transaction[]> responseEntity = restTemplate.getForEntity(uri, Transaction[].class);

            if(responseEntity.getStatusCode() == HttpStatus.OK){
                List<Transaction> transactions = new ArrayList<>();
                for (Transaction transaction : responseEntity.getBody()) {
                    transactions.add(transaction);
                }

                return transactions;
            }
        }
        catch (HttpClientErrorException.NotFound e){
            System.out.println("Customer does not exist: " + customerId);
            return null;
        }
        catch (Exception e){
            String errMessage = "Failed to get transactions for : " + customerId;
            System.out.println(errMessage);
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error",errMessage,e.getMessage(), Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
            return null;
        }
        return null;
    }

    // GET transaction by id
    public static Optional<Transaction> get(String id) {
        try{
            String uri = BASE_URI  + "/transactions/" + id;
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Transaction> responseEntity = restTemplate.getForEntity(uri,Transaction.class);

            // Find transaction successful
            if(responseEntity.getStatusCode() == HttpStatus.OK){
                Transaction transaction = responseEntity.getBody();
                System.out.println("Fetched Transaction : " + transaction.getTransactionId());
                return Optional.of(transaction);
            }
        }
        catch (HttpClientErrorException.NotFound e){
            System.out.println("Transaction not found: " + id);
            return Optional.empty();
        }

        // Find transaction failed
        System.out.println("Failed to fetch transaction: " + id);
        return Optional.empty();
    }

    // POST transaction
    public static Optional<Transaction> save(Transaction newTransaction) {
        try {
            String uri = BASE_URI  + "/transactions/";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Transaction> req = new HttpEntity<>(newTransaction,httpHeaders);
            ResponseEntity<Transaction> responseEntity = restTemplate.exchange(uri, HttpMethod.POST,req,Transaction.class);

            if(responseEntity.getStatusCode() == HttpStatus.CREATED){
                Transaction savedTransaction = responseEntity.getBody();
                System.out.println("Saved Transaction : " + savedTransaction.getTransactionId());
                return Optional.of(savedTransaction);
            }
        }
        catch (Exception e){
            String errMessage = "Failed to save transaction : " + newTransaction.getTransactionId();
            System.out.println(errMessage);
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error",errMessage,e.getMessage(), Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
            return Optional.empty();
        }
        return Optional.empty();
    }

    public static Optional<Transaction> update(String id, Transaction transaction){
        try{
            System.out.println("Updating Transaction.....");
            String uri = BASE_URI + "/transactions/" + id;
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Transaction> req = new HttpEntity<>(transaction, httpHeaders);
            ResponseEntity<Transaction> responseEntity = restTemplate.exchange(uri, HttpMethod.PUT, req, Transaction.class);

            if(responseEntity.getStatusCode() == HttpStatus.OK){
                System.out.println("Updated Transaction : " + responseEntity.getBody().getTransactionId());
                return Optional.of(responseEntity.getBody());
            }
        }
        catch (Exception e){
            System.out.println("Error updating transaction !");
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error","Error updating transaction !", e.getMessage(), Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
        }
        return Optional.empty();
    }

    public static void delete(String id){
        try{
            System.out.println("Deleting Transaction.....");
            String uri = BASE_URI + "/transactions/" + id;
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            restTemplate.delete(uri);

            System.out.println("Deleted Transaction: " + id);
        }
        catch (Exception e){
            System.out.println("Error deleting transaction !");
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error","Error deleting transaction !", e.getMessage(), Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
        }
    }
}
