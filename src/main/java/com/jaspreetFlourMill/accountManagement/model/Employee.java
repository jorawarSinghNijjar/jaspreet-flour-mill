package com.jaspreetFlourMill.accountManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jaspreetFlourMill.accountManagement.util.AlertDialog;
import javafx.scene.control.Alert;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static com.jaspreetFlourMill.accountManagement.util.Rest.BASE_URI;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Employee implements Serializable {

    private User user;
    private String id;
    private String name;
    private String emailId;
    private String password;
    private String contactNumber;
    private String address;
    private String jobDesignation;
    private String dob;

    public Employee(){

    }

    public Employee(User user,String name, String emailId, String contactNumber, String address, String jobDesignation, LocalDate dob) {
        this.user = user;
        this.name = name;
        this.emailId = emailId;
        this.contactNumber = contactNumber;
        this.address = address;
        this.jobDesignation = jobDesignation;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        this.dob = formatter.format(dob);

//        this.id = name.substring(0,3) + "00" + dob.getMonthValue();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

       public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getJobDesignation() {
        return jobDesignation;
    }

    public void setJobDesignation(String jobDesignation) {
        this.jobDesignation = jobDesignation;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", address='" + address + '\'' +
                ", jobDesignation='" + jobDesignation + '\'' +
                ", dob='" + dob + '\'' +
                '}';
    }

    // POST Employee to API
    public static boolean register(Employee newEmployee) throws Exception {
        try{
            System.out.println("Registering employee ...." + newEmployee.getName());
            String uri = BASE_URI + "/employees";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Employee> req = new HttpEntity<>(newEmployee, httpHeaders);
            ResponseEntity<Employee> result = restTemplate.exchange(uri, HttpMethod.POST, req, Employee.class);

            // Employee registration Successful
            if(result.getStatusCode() == HttpStatus.CREATED){
                System.out.println("Employee Registration successful for: " + result.getBody().getId());
                return true;
            }
        }
        catch(Exception e){
            // Employee registration failed
            String errMessage = "Employee Registration failed for: " + newEmployee.getId();
            System.out.println(errMessage);
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error",errMessage,e.getMessage(), Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
            return false;
        }
        return false;
    }

    // GET Employee
    public static Optional<Employee> getEmployee(User user) {
        try{
            System.out.println("Fetching employee ...." + user.getId());
            String uri = BASE_URI + "/employees/" + user.getId();
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Employee> responseEntity = restTemplate.getForEntity(uri,Employee.class);

            // Find customer successful
            if(responseEntity.getStatusCode() == HttpStatus.OK){
                Employee employee = responseEntity.getBody();
                System.out.println("Fetched Employee : " + employee.getId());
                return Optional.of(employee);
            }

        }
        catch (HttpClientErrorException.NotFound e){
            System.out.println("Employee not found: " + user.getId());
            return Optional.empty();
        }

        return Optional.empty();

    }
}
