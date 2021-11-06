package com.jaspreetFlourMill.accountManagement.util;

import com.jaspreetFlourMill.accountManagement.controllers.ContentController;
import com.jaspreetFlourMill.accountManagement.model.Role;
import com.jaspreetFlourMill.accountManagement.model.User;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Authentication {

    private boolean isAuthenticated;

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

    private void setUser(User user) {
        this.user = user;
    }

    public boolean login(String userId, String password) throws Exception {
        //GET request to get employee with userId
        if (userId == "" || userId == null) {
            System.out.println("Please enter User Id. Field is empty!");
            return false;
        }

        try {
            // Check if user is an ADMIN or EMPLOYEE
            System.out.println("Signing in ......");
            User user = User.getUser(userId).orElseThrow();
            System.out.println("Authenticating ......" + user.getId());

            String encodedPassword = user.getPassword();
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            boolean validPassword = bCryptPasswordEncoder.matches(password, encodedPassword);

            if (!validPassword) {
                // Information dialog
                AlertDialog alertDialog = new AlertDialog("Error", "Invalid username or password", "Check your credentials", Alert.AlertType.INFORMATION);
                alertDialog.showInformationDialog();
                return false;
            }

            this.setUser(user);
            this.setAuthenticated(true);
            System.out.println("Welcome, " + user.getId());
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error", e.getCause().getMessage(), e.getMessage(), Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
            return false;
        }
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
