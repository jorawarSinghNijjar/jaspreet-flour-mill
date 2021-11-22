package com.jaspreetFlourMill.accountManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jaspreetFlourMill.accountManagement.util.AlertDialog;
import javafx.scene.control.Alert;
import org.springframework.http.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.jaspreetFlourMill.accountManagement.util.Rest.BASE_URI;


@JsonIgnoreProperties(ignoreUnknown = true)

public class User implements Serializable {

    private String id;
    private String password;
    private Role role;
    private String profileImgLocation;

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

    public String getProfileImgLocation() {
        return profileImgLocation;
    }

    public void setProfileImgLocation(String profileImgLocation) {
        this.profileImgLocation = profileImgLocation;
    }


    public static boolean save(User newUser) {
        try{
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
        }
        catch (Exception e){
            // User registration failed
            String errMessage = "User Registration failed for: " + newUser.getId();
            System.out.println(errMessage);
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error",errMessage,e.getMessage(), Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
            return false;
        }
        return false;
    }

    public static Optional<User> get(String id) {
        try{
            String uri = BASE_URI + "/users/" + id;
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<User> responseEntity = restTemplate.getForEntity(uri,User.class);

            if(responseEntity.getStatusCode() == HttpStatus.OK){
                User user = responseEntity.getBody();
                System.out.println("Fetched User : " + user.getId());
                return Optional.of(user);
            }
        }
        catch(HttpClientErrorException.NotFound e){
            System.out.println("User not found: " + id);
            return Optional.empty();
        }

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
            return Admin.get(user);
        }
        else if(user.role == Role.EMPLOYEE){
           return Employee.get(user);

        }
        return Optional.empty();
    }

    public static Optional<User> update(User user, String id) {
        try{
            String uri = BASE_URI + "/users/" + id;
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<User> req = new HttpEntity<>(user, httpHeaders);
            ResponseEntity<User> result = restTemplate.exchange(uri, HttpMethod.PUT, req, User.class);

            // User registration Successful
            if(result.getStatusCode() == HttpStatus.OK){
                User updatedUser =  result.getBody();
                System.out.println("User update successful for: " + updatedUser.getId());
                return Optional.of(updatedUser);
            }
        }
        catch (Exception e){
            // User registration failed
            String errMessage = "User update failed for: " + user.getId();
            System.out.println(errMessage);
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error",errMessage,e.getMessage(), Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
        }
        return Optional.empty();
    }
//
//    public static void delete(User user){
//        try{
//            System.out.println("Deleting User.....");
//            String uri = BASE_URI + "/users/" + user.getId();
//            RestTemplate restTemplate = new RestTemplate();
//            HttpHeaders httpHeaders = new HttpHeaders();
//            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
//
//            restTemplate.delete(uri);
//
//            System.out.println("Deleted user: " + user.getId());
//        }
//        catch (Exception e){
//            System.out.println("Error deleting user !");
//            // Information dialog
//            AlertDialog alertDialog = new AlertDialog("Error","Error deleting user !", e.getMessage(), Alert.AlertType.ERROR);
//            alertDialog.showErrorDialog(e);
//        }
//    }


    public static Optional<String> resetPassword(String emailId) {
        try{
            System.out.println("Resetting password ...." + emailId);
            String uri = BASE_URI + "/users/forgot-password";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            Map<String,String> data = new HashMap<>();
            data.put("emailId",emailId);

            HttpEntity<Map<String,String>> req = new HttpEntity<>(data, httpHeaders);
            ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST, req,String.class);

            // Employee registration Successful
            if(result.getStatusCode() == HttpStatus.ACCEPTED){
                System.out.println("Reset Token sent to : " + emailId);
                return Optional.of(result.getBody());
            }
        }
        catch(Exception e){
            // Employee registration failed
            String errMessage = "Reset password failed for: " + emailId;
            System.out.println(errMessage);
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error",errMessage,e.getMessage(), Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
            return Optional.empty();
        }
        return Optional.empty();
    }

    public static Optional<User> authorizeToken(String token) {
        try{
            System.out.println("Authorizing token ....");
            String uri = BASE_URI + "/users/reset-token";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            Map<String,String> data = new HashMap<>();
            data.put("reset-token",token);

            HttpEntity<Map<String,String>> req = new HttpEntity<>(data, httpHeaders);
            ResponseEntity<User> result = restTemplate.exchange(uri, HttpMethod.POST, req,User.class);

            // Employee registration Successful
            if(result.getStatusCode() == HttpStatus.ACCEPTED){
                System.out.println("Reset Token matched");
                User user = result.getBody();
                return Optional.of(user);
            }
        }
        catch(Exception e){
            // Employee registration failed
            String errMessage = "Reset token process failed !";
            System.out.println(errMessage);
            // Error dialog
            AlertDialog alertDialog = new AlertDialog("Error",errMessage,e.getMessage(), Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
            return Optional.empty();
        }
        return Optional.empty();
    }


}
