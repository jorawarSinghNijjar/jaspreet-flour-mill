package com.jaspreetFlourMill.accountManagement.util;

import com.jaspreetFlourMill.accountManagement.controllers.ContentController;
import com.jaspreetFlourMill.accountManagement.model.Role;
import com.jaspreetFlourMill.accountManagement.model.User;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static javafx.scene.control.Alert.AlertType.INFORMATION;

public class Authentication {

    private boolean isAuthenticated;

    private String resetToken;

    private User user;

    public Authentication() {

    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    private void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public boolean login(String userId, String password) throws Exception {
        //GET request to get employee with userId

        if(userId == null){
            throw new NullPointerException("User id is null !");
        }

        if(password == null){
            throw new NullPointerException("Password is null !");
        }

        if (userId.trim().length() == 0) {
            System.out.println("Please enter User Id. User id Field is empty!");
            // Info Dialog
            AlertDialog alertDialog = new AlertDialog("INFO", "Invalid User Id", "Please enter User Id. User id Field is empty!",INFORMATION);
            alertDialog.showInformationDialog();
            return false;
        }

        if (password.trim().length() == 0) {
            System.out.println("Please enter password. Password Field is empty!");
            // Info Dialog
            AlertDialog alertDialog = new AlertDialog("INFO", "Invalid password", "Please enter password. Password Field is empty!",INFORMATION);
            alertDialog.showInformationDialog();
            return false;
        }

        // Check if user is an ADMIN or EMPLOYEE
        System.out.println("Signing in ......");
        User user = User.getUser(userId).orElseThrow();
        System.out.println("Authenticating ......" + user.getId());

        String encodedPassword = user.getPassword();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        boolean validPassword = bCryptPasswordEncoder.matches(password, encodedPassword);

        if (!validPassword) {
            // Info Dialog
            AlertDialog alertDialog = new AlertDialog("INFO", "Invalid Credentials", "Wrong username or password",INFORMATION);
            alertDialog.showInformationDialog();
            return false;
        }

        this.setUser(user);
        this.setAuthenticated(true);
        System.out.println("Welcome, " + user.getId());

        return true;
    }

    public boolean logout() {
        if (!this.isAuthenticated) {
            return false;
        }
        this.setUser(null);
        this.setAuthenticated(false);

        return true;
    }
}
