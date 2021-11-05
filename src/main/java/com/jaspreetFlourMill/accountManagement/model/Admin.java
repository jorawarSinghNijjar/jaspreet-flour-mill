package com.jaspreetFlourMill.accountManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jaspreetFlourMill.accountManagement.controllers.ContentController;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Admin implements Serializable {

    private String adminId;
    private String password;
    private String emailId;

    public Admin(String adminId, String password, String emailId) {
        this.adminId = adminId;
        this.password = password;
        this.emailId = emailId;
    }

    public Admin() {
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public static HttpStatus register(Admin newAdmin) throws Exception {
        String uri = "http://localhost:8080/admin/";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Admin> req = new HttpEntity<>(newAdmin, httpHeaders);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST, req, String.class);

       return result.getStatusCode();
    }

    public static HttpStatus getAdmin(String id) throws Exception{
        String uri = "http://localhost:8080/admin/" + id;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Admin> responseEntity = restTemplate.getForEntity(uri,Admin.class);
        return responseEntity.getStatusCode();
    }

    public static boolean isRegistered() throws Exception {
        String uri = "http://localhost:8080/admin/";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Admin[]> responseEntity = restTemplate.getForEntity(uri, Admin[].class);
        Admin[] adminsArr = responseEntity.getBody();
        if(adminsArr.length > 0){
            System.out.println("Admin is registered");
            return true;
        }
        System.out.println("No admin registered!");
        return false;
    }
}
