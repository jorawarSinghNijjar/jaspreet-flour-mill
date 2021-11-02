package com.jaspreetFlourMill.accountManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Employee implements Serializable {

    private User user;
    private String id;
    private String name;
    private String password;
    private String contactNumber;
    private String address;
    private String jobDesignation;
    private String dob;

    public Employee(){

    }

    public Employee(User user,String name, String contactNumber, String address, String jobDesignation, LocalDate dob) {
        this.user = user;
        this.name = name;
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

    public static HttpStatus register(Employee newEmployee) throws Exception {
        String uri = "http://localhost:8080/employees/";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Employee> req = new HttpEntity<>(newEmployee, httpHeaders);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST, req, String.class);

        return result.getStatusCode();
    }
}
