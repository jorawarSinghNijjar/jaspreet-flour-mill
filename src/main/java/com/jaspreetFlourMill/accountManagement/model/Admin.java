package com.jaspreetFlourMill.accountManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jaspreetFlourMill.accountManagement.StageInitializer;
import com.jaspreetFlourMill.accountManagement.util.AlertDialog;
import com.jaspreetFlourMill.accountManagement.util.Rest;
import com.jaspreetFlourMill.accountManagement.util.Util;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.io.Serializable;
import java.net.ConnectException;
import java.util.Optional;

import static com.jaspreetFlourMill.accountManagement.util.Rest.BASE_URI;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Admin implements Serializable {

    private User user;
    private String id;
    private String emailId;

    public Admin(User user, String emailId) {
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
    public static boolean save(Admin newAdmin) {
        try{
            System.out.println("Registering admin...." + newAdmin.getUser().getId());
            String uri = BASE_URI + "/admins";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Admin> req = new HttpEntity<>(newAdmin, httpHeaders);
            ResponseEntity<Admin> result = restTemplate.exchange(uri, HttpMethod.POST, req, Admin.class);

            // Admin creation Successful
            if (result.getStatusCode() == HttpStatus.CREATED) {
                Admin savedAdmin = result.getBody();
                System.out.println("Admin Registration successful for: " + savedAdmin.getId());
                return true;
            }
        }
        catch (Exception e){
            // Admin creation failed
            String errMessage = "Admin Registration failed for: " + newAdmin.getUser().getId();
            System.out.println(errMessage);
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error",errMessage,e.getMessage(), Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
            return false;
        }

        return false;
    }

    public static Optional<Admin> get(User user) {
        try {
            System.out.println("Fetching admin ...." + user.getId());
            String uri = BASE_URI + "/admins/" + user.getId();
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Admin> responseEntity = restTemplate.getForEntity(uri, Admin.class);

            // Admin fetched successfully
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                System.out.println("Found Admin with id: " + responseEntity.getBody().getId());
                return Optional.of(responseEntity.getBody());
            }
        } catch (HttpClientErrorException.NotFound e) {
            // User not found
            System.out.println("User not found: " + user.getId());
            return Optional.empty();
        }

        return Optional.empty();

    }

    public static boolean isRegistered() {
        try{
            System.out.println("Checking if an admin is registered or not.....");
            String uri = BASE_URI + "/admins";
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Admin[]> responseEntity = restTemplate.getForEntity(uri, Admin[].class);
            Admin[] adminsArr = responseEntity.getBody();
            if (adminsArr.length > 0) {
                System.out.println("Admin is registered");
                return true;
            }
            System.out.println("No admin registered!");
            return false;
        }
        catch (ResourceAccessException e){
            System.out.println("Error connecting to server!");
            String headerText = "Server is not up and running, start the server before launching the client";
            String contentText = "Try connecting again";
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Info",headerText,contentText, Alert.AlertType.INFORMATION);
            alertDialog.showInformationDialog();
            Platform.exit();
            return false;
        }
        catch (Exception e){
            System.out.println("Error checking admin is registered !");
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error","Error checking admin is registered !",e.getMessage(), Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
            return false;
        }
    }


    public static Optional<Admin> update(User user, Admin admin){
        try{
            System.out.println("Updating Admin.....");
            String uri = BASE_URI + "/admins/" + user.getId();
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Admin> req = new HttpEntity<>(admin, httpHeaders);
            ResponseEntity<Admin> responseEntity = restTemplate.exchange(uri, HttpMethod.PUT, req, Admin.class);

            if(responseEntity.getStatusCode() == HttpStatus.OK){
                System.out.println("Updated Admin : " + responseEntity.getBody().getId());
                return Optional.of(responseEntity.getBody());
            }
        }
        catch (Exception e){
            System.out.println("Error updating admin !");
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error","Error updating admin !", e.getMessage(), Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
        }
        return Optional.empty();
    }

    public static void delete(User user){
        try{
            System.out.println("Deleting Admin.....");
            String uri = BASE_URI + "/admins/" + user.getId();
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            restTemplate.delete(uri);

            System.out.println("Deleted Admin: " + user.getId());
        }
        catch (Exception e){
            System.out.println("Error deleting admin !");
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error","Error deleting admin !", e.getMessage(), Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
        }

//        User.delete(user);
    }
}
