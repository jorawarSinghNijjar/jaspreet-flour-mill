package com.jaspreetFlourMill.accountManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;


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

    public static HttpStatus register(User newUser) throws Exception {
        String uri = "http://localhost:8080/user/";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<User> req = new HttpEntity<>(newUser, httpHeaders);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST, req, String.class);

        return result.getStatusCode();
    }

    public static ResponseEntity<User> getUser(String id) throws Exception{
        String uri = "http://localhost:8080/user/" + id;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<User> responseEntity = restTemplate.getForEntity(uri,User.class);
        return responseEntity;
    }

    public static boolean isRegistered() throws Exception {
        String uri = "http://localhost:8080/user/";
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

    public static Optional<ResponseEntity<?>> getUserDetails(User user) throws Exception{
        if(user.role == Role.ADMIN){
            ResponseEntity<Admin> responseEntity = Admin.getAdmin(user);
            return Optional.ofNullable(responseEntity);
        }
        else if(user.role == Role.EMPLOYEE){
            ResponseEntity<Employee> responseEntity = Employee.getEmployee(user);
            return Optional.ofNullable(responseEntity);
        }
        return Optional.empty();
    }
}
