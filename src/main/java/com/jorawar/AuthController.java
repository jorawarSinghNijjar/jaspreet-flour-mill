package com.jorawar;

import java.io.IOException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class AuthController{
    private static Scene scene;

    @FXML
    public void handleLogin(ActionEvent event) throws IOException{
        System.out.println("Login Successfull");
        FXMLLoader loader = new FXMLLoader(App.class.getResource("dashboard.fxml"));
        App.getScene().setRoot(loader.load());
    }
}
