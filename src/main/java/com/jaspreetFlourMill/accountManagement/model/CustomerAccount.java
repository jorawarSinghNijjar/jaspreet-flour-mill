package com.jaspreetFlourMill.accountManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jaspreetFlourMill.accountManagement.controllers.ContentController;
import com.jaspreetFlourMill.accountManagement.util.AlertDialog;
import com.jaspreetFlourMill.accountManagement.util.Util;
import javafx.scene.control.Alert;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static com.jaspreetFlourMill.accountManagement.util.Rest.BASE_URI;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerAccount implements Serializable {
    private Integer customerAccountId;
    //    private Integer customerId;
    private Customer customer;
    private double wheatDepositQty;
    private double wheatProcessingDeductionQty;
    private double initialWheatQty;
    private double currentWheatBalance;
    private double grindingChargesBalance;
    private double grindingRate;
    private String startDate;
    private int rowsPrinted;

    public CustomerAccount(Customer customer, double wheatDepositQty, double wheatProcessingDeductionQty) {
        this.customer = customer;
        this.wheatDepositQty = wheatDepositQty;
        this.wheatProcessingDeductionQty = wheatProcessingDeductionQty;
        this.initialWheatQty = wheatDepositQty - wheatProcessingDeductionQty;
        this.currentWheatBalance = initialWheatQty;
        this.grindingChargesBalance = 0;
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        this.startDate = formatter.format(dateTime);
        this.rowsPrinted = 0;
    }

    public int getRowsPrinted() {
        return rowsPrinted;
    }

    public void setRowsPrinted(int rowsPrinted) {
        this.rowsPrinted = rowsPrinted;
    }

    public void incrementRow() {
        this.rowsPrinted++;
    }

    public void printNextPage() {
        this.rowsPrinted = 0;
    }

    public void setWheatProcessingDeductionQty(double wheatProcessingDeductionQty) {
        this.wheatProcessingDeductionQty = wheatProcessingDeductionQty;
    }

    public void setGrindingChargesBalance(double grindingChargesBalance) {
        this.grindingChargesBalance = grindingChargesBalance;
    }

    public CustomerAccount() {

    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }


    public double getWheatDepositQty() {
        return wheatDepositQty;
    }

    public void setWheatDepositQty(double wheatDepositQty) {
        this.wheatDepositQty = wheatDepositQty;
    }

    public double getWheatProcessingDeductionQty() {
        return wheatProcessingDeductionQty;
    }

    public double getInitialWheatQty() {
        return initialWheatQty;
    }

    public void setInitialWheatQty(double initialWheatQty) {
        this.initialWheatQty = initialWheatQty;
    }

    public double getCurrentWheatBalance() {
        return currentWheatBalance;
    }

    public void setCurrentWheatBalance(double currentWheatBalance) {
        this.currentWheatBalance = currentWheatBalance;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public Integer getCustomerAccountId() {
        return customerAccountId;
    }

    public void setCustomerAccountId(Integer customerAccountId) {
        this.customerAccountId = customerAccountId;
    }

    public double getGrindingRate() {
        return grindingRate;
    }

    public void setGrindingRate(double grindingRate) {
        this.grindingRate = grindingRate;
    }

    public double getGrindingChargesBalance() {
        return grindingChargesBalance;
    }

    public void addWheatToAccount(double wheatDepositQty, double wheatProcessingDeductionQty) {
        this.wheatDepositQty = 0.00;
        this.wheatDepositQty += this.currentWheatBalance;
        this.wheatDepositQty += wheatDepositQty;
        this.initialWheatQty = (this.wheatDepositQty - wheatProcessingDeductionQty);
        this.currentWheatBalance = this.initialWheatQty;
        this.wheatProcessingDeductionQty = wheatProcessingDeductionQty;
    }

    // POST request to create customer account
    public static boolean save(CustomerAccount newCustomerAccount) {
        try{
            System.out.println("Registering customer account .... ");
            String uri = BASE_URI + "/customer-accounts";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<CustomerAccount> req = new HttpEntity<>(newCustomerAccount, httpHeaders);
            ResponseEntity<CustomerAccount> result = restTemplate.exchange(uri, HttpMethod.POST, req, CustomerAccount.class);

            if (result.getStatusCode() == HttpStatus.CREATED) {
                CustomerAccount savedCustomerAccount = result.getBody();
                System.out.println("Customer Account created Successfully for : " + savedCustomerAccount);
                return true;
            }
        }
        catch (Exception e){
            String errMessage = "Customer Account creation failed for id: " + newCustomerAccount.getCustomer().getCustomerId();
            System.out.println(errMessage);
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error",errMessage,e.getMessage(), Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
            return false;
        }
        return false;
    }

    // GET Customer Account by id from API
    public static Optional<CustomerAccount> get(Integer id) {
        System.out.println("Fetching customer account ...." + id);
        try {
            String uri = BASE_URI + "/customer-accounts/" + id;
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<CustomerAccount> responseEntity = restTemplate.getForEntity(uri, CustomerAccount.class);

            // Find customer account successful
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                CustomerAccount customerAccount = responseEntity.getBody();
                System.out.println("Fetched Customer Account : " + customerAccount.getCustomer().getCustomerId());
                return Optional.of(customerAccount);
            }  else if(responseEntity.getStatusCode() == HttpStatus.NOT_FOUND){
                // Customer Account not found
                System.out.println("Customer Account not found: " + id);
                return Optional.empty();
            }
        } catch (HttpClientErrorException.NotFound e) {
            System.out.println("Customer Account does not exist: " + id);
            return Optional.empty();
        }
        // Find customer account failed
        System.out.println("Failed to fetch customer account: " + id);
        return Optional.empty();
    }

    // UPDATE Customer Account using customerId and current customer account details
    public static boolean update(Integer customerId, CustomerAccount customerAccount) {
        try {
            System.out.println("Updating customer account ...." + customerId);
            String uri = BASE_URI + "/customer-accounts/" + customerId;
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<CustomerAccount> req = new HttpEntity<>(customerAccount, httpHeaders);
            ResponseEntity<CustomerAccount> responseEntity = restTemplate.exchange(uri, HttpMethod.PUT, req, CustomerAccount.class);

            // Update customer account successful
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                CustomerAccount updatedCustomerAccount = responseEntity.getBody();
                System.out.println("Updated Customer Account : " + updatedCustomerAccount.getCustomer().getCustomerId());
                return true;
            }
        }
        catch (Exception e){
            String errMessage = "Failed to update customer account : " + customerId;
            System.out.println(errMessage);
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error",errMessage,e.getMessage(), Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
            return false;
        }

        // Update customer account failed
        System.out.println("Failed to fetch customer account: " + customerId);
        return false;
    }

    public static boolean delete(Customer customer){
        try{
            System.out.println("Deleting Customer Account....." + customer.getCustomerId());
            String uri = BASE_URI + "/customer-accounts/" + customer.getCustomerId();
            RestTemplate restTemplate = new RestTemplate();

            restTemplate.delete(uri);

            System.out.println("Deleted Customer Account: " + customer.getCustomerId());

            return true;
        }
        catch (Exception e){
            System.out.println("Error deleting customer account !");
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error","Error deleting customer account!", e.getMessage(), Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
            return false;
        }
    }

    public static boolean updatePrintedRow(Integer id, boolean nextPage) {
        try{
            System.out.println("Updating printed row .... for customer account: " + id);
            CustomerAccount customerAccount = CustomerAccount.get(id).orElseThrow();
            if (nextPage) {
                customerAccount.printNextPage();
            }
            customerAccount.incrementRow();

            String uri = BASE_URI + "/customer-accounts/" + id;
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<CustomerAccount> req = new HttpEntity<>(customerAccount, httpHeaders);
            ResponseEntity<CustomerAccount> responseEntity = restTemplate.exchange(uri, HttpMethod.PUT, req, CustomerAccount.class);

            // Update printed row successful
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                CustomerAccount updatedCustomerAccount = responseEntity.getBody();
                System.out.println("Rows printed : " + updatedCustomerAccount.getRowsPrinted());
                return true;
            }
        }catch (Exception e){
            String errMessage = "Failed to update printed rows for customer account: " + id;
            System.out.println(errMessage);
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error",errMessage,e.getMessage(), Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
            return false;
        }

        return false;
    }

    @Override
    public String toString() {
        return "CustomerAccount{" +
                "customerAccountId=" + customerAccountId +
                ", customer=" + customer +
                ", wheatDepositQty=" + wheatDepositQty +
                ", wheatProcessingDeductionQty=" + wheatProcessingDeductionQty +
                ", initialWheatQty=" + initialWheatQty +
                ", currentWheatBalance=" + currentWheatBalance +
                ", grindingChargesBalance=" + grindingChargesBalance +
                ", startDate='" + startDate + '\'' +
                '}';
    }
}
