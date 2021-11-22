package com.jaspreetFlourMill.accountManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jaspreetFlourMill.accountManagement.util.AlertDialog;
import javafx.scene.control.Alert;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.jaspreetFlourMill.accountManagement.util.Rest.BASE_URI;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer implements Serializable {
    private Integer customerId;
    private String name;
    private String address;
    private String phoneNumber;
    private String rationCardNo;
    private String dob;
    private String adhaarNo;
    private String idProof;

    public Customer(String name, String address, String phoneNumber, String rationCardNo, LocalDate dob, String adhaarNo,
                    String idProof) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.rationCardNo = rationCardNo;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        this.dob = formatter.format(dob);
        this.adhaarNo = adhaarNo;
        this.idProof = idProof;
    }

    public Customer() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRationCardNo() {
        return rationCardNo;
    }

    public void setRationCardNo(String rationCardNo) {
        this.rationCardNo = rationCardNo;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAdhaarNo() {
        return adhaarNo;
    }

    public void setAdhaarNo(String adhaarNo) {
        this.adhaarNo = adhaarNo;
    }

    public String getIdProof() {
        return idProof;
    }

    public void setIdProof(String idProof) {
        this.idProof = idProof;
    }


    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public static Optional<Customer[]> getAllCustomers() {
        try {
            System.out.println("Fetching all customers....");
            String uri = BASE_URI + "/customers";
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Customer[]> responseEntity = restTemplate.getForEntity(uri, Customer[].class);

            // Find customers successful
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Customer[] customers = responseEntity.getBody();
                System.out.println("Fetched customers: " + customers.length);
                return Optional.of(customers);
            }
        } catch (HttpClientErrorException.NotFound e) {
            // Customers not found
            System.out.println("Customers not found !");
            return Optional.empty();
        }

        return Optional.empty();
    }

    public static Optional<Customer> get(String id) {
        try {
            System.out.println("Fetching customer ...." + id);
            String uri = BASE_URI + "/customers/" + id;
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Customer> responseEntity = restTemplate.getForEntity(uri, Customer.class);

            // Find customer successful
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Customer customer = responseEntity.getBody();
                System.out.println("Fetched Customer : " + customer.getCustomerId());
                return Optional.of(customer);
            }

        } catch (HttpClientErrorException.NotFound e) {
            // Customer not found
            System.out.println("Customer not found: " + id);
            return Optional.empty();
        }

        // Find customer failed
        System.out.println("Failed to fetch customer: " + id);
        return Optional.empty();
    }

    public static boolean save(Customer customer){
        try{
            System.out.println("Registering customer...." + customer.getCustomerId());
            String uri = BASE_URI + "/customers";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Customer> req = new HttpEntity<>(customer, httpHeaders);
            ResponseEntity<Customer> result = restTemplate.exchange(uri, HttpMethod.POST, req, Customer.class);

            // Admin creation Successful
            if (result.getStatusCode() == HttpStatus.CREATED) {
                Customer savedCustomer = result.getBody();
                System.out.println("Customer Registration successful for: " + savedCustomer.getCustomerId());
                return true;
            }
        }
        catch (Exception e){
            // Admin creation failed
            String errMessage = "Customer Registration failed for: " + customer.getCustomerId();
            System.out.println(errMessage);
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error",errMessage,e.getMessage(), Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
            return false;
        }

        return false;
    }

    public static Optional<Customer> update(Integer id, Customer customer){
        try{
            System.out.println("Updating Customer.....");
            String uri = BASE_URI + "/customers/" + id;
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Customer> req = new HttpEntity<>(customer, httpHeaders);
            ResponseEntity<Customer> responseEntity = restTemplate.exchange(uri, HttpMethod.PUT, req, Customer.class);

            if(responseEntity.getStatusCode() == HttpStatus.OK){
                System.out.println("Updated Customer : " + responseEntity.getBody().getCustomerId());
                return Optional.of(responseEntity.getBody());
            }
        }
        catch (Exception e){
            System.out.println("Error updating customer !");
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error","Error updating customer !", e.getMessage(), Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
        }
        return Optional.empty();
    }

    public static boolean delete(Integer id){
        try{
            System.out.println("Deleting Customer....." + id);
            String uri = BASE_URI + "/customers/" + id;
            RestTemplate restTemplate = new RestTemplate();

            restTemplate.delete(uri);

            System.out.println("Deleted Customer: " + id);

            return true;
        }
        catch (Exception e){
            System.out.println("Error deleting customer !");
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error","Error deleting customer !", e.getMessage(), Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
            return false;
        }
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", rationCardNo='" + rationCardNo + '\'' +
                ", dob='" + dob + '\'' +
                ", adhaarNo='" + adhaarNo + '\'' +
                ", idProof='" + idProof + '\'' +
                '}';
    }
}
