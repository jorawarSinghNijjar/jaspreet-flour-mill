package com.jaspreetFlourMill.accountManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

import static com.jaspreetFlourMill.accountManagement.util.Rest.BASE_URI;


@JsonIgnoreProperties(ignoreUnknown = true)

public class User implements Serializable {

    private String id;
    private String password;
    private Role role;


    public User() {
    }

    public User(String userId, String password, Role role) {
        this.id = userId;
        this.password = password;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public static boolean register(User newUser) throws Exception {
        String uri = BASE_URI + "/users";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<User> req = new HttpEntity<>(newUser, httpHeaders);
        ResponseEntity<User> result = restTemplate.exchange(uri, HttpMethod.POST, req, User.class);

        // User registration Successful
        if(result.getStatusCode() == HttpStatus.CREATED){
            User savedUser =  result.getBody();
            System.out.println("User Registration successful for: " + savedUser.getId());
            return true;
        }

        // User registration failed
        System.out.println("User Registration failed for: " + newUser.getId());
        return false;
    }

    public static Optional<User> getUser(String id) throws Exception{
        String uri = BASE_URI + "/users/" + id;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<User> responseEntity = restTemplate.getForEntity(uri,User.class);

        // Find user successful
        if(responseEntity.getStatusCode() == HttpStatus.OK){
            User user = responseEntity.getBody();
            System.out.println("Fetched User : " + user.getId());
            return Optional.of(user);
        }

        // Find user failed
        System.out.println("Failed to fetch user: " + id);
        return Optional.empty();
    }

    public static boolean isRegistered() throws Exception {
        String uri = BASE_URI+ "/users";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<User[]> responseEntity = restTemplate.getForEntity(uri, User[].class);
        User[] usersArr = responseEntity.getBody();
        if(usersArr.length > 0){
            System.out.println("User is registered");
            return true;
        }
        System.out.println("No user registered!");
        return false;
    }

    public static Optional getUserDetails(User user) throws Exception{
        if(user.role == Role.ADMIN){
            return Admin.getAdmin(user);
        }
        else if(user.role == Role.EMPLOYEE){
           return Employee.getEmployee(user);

        }
        return Optional.empty();
    }
}
