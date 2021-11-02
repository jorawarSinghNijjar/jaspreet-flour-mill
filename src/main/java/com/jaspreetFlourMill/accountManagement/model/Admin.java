package com.jaspreetFlourMill.accountManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Admin implements Serializable {

    private User user;
    private String id;
    private String emailId;

    public Admin(User user,String emailId) {
        this.user = user;
        this.emailId = emailId;
    }

    public Admin() {
    }

    public String getId() {
        return id;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
