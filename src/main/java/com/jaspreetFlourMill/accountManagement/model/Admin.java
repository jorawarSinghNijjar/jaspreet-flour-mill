package com.jaspreetFlourMill.accountManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jaspreetFlourMill.accountManagement.util.Rest;
import com.jaspreetFlourMill.accountManagement.util.Util;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import javax.swing.text.html.Option;
import java.io.Serializable;
import java.util.Optional;

import static com.jaspreetFlourMill.accountManagement.util.Rest.BASE_URI;

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

    // Register new Admin (only 1 admin allowed for now)
    public static boolean register(Admin newAdmin) throws Exception {
        System.out.println("Registering admin...." + newAdmin.getUser().getId());
        String uri = BASE_URI + "/admins";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Admin> req = new HttpEntity<>(newAdmin, httpHeaders);
        ResponseEntity<Admin> result = restTemplate.exchange(uri, HttpMethod.POST, req, Admin.class);

        // Admin creation Successful
        if(result.getStatusCode() == HttpStatus.CREATED){
            Admin savedAdmin = result.getBody();
            System.out.println("Admin Registration successful for: " + savedAdmin.getId());
            return true;
        }

        // Admin creation failed
        System.out.println("Admin Registration failed for: " + newAdmin.getUser().getId());
        return false;
    }

    public static Optional<Admin> getAdmin(User user) throws Exception{
        System.out.println("Fetching admin ...." + user.getId());
        String uri = BASE_URI + "/admins/" + user.getId();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Admin> responseEntity = restTemplate.getForEntity(uri,Admin.class);

        // Admin fetched successfully
        if(responseEntity.getStatusCode() == HttpStatus.OK){
            System.out.println("Found Admin with id: " + responseEntity.getBody().getId());
            return Optional.of(responseEntity.getBody());
        }

        // User not found
        System.out.println("User not found: " + user.getId());
        return Optional.empty();
    }

    public static boolean isRegistered() throws Exception {
        System.out.println("Checking if an admin is registered or not.....");
        String uri = BASE_URI + "/admins";
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
