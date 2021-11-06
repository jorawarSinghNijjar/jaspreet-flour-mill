package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.StageInitializer;
import com.jaspreetFlourMill.accountManagement.StageReadyEvent;


import com.jaspreetFlourMill.accountManagement.util.AlertDialog;
import com.jaspreetFlourMill.accountManagement.util.FormValidation;
import com.jaspreetFlourMill.accountManagement.util.Util;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.GestureEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.http.*;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Component
@FxmlView("/views/loginForm.fxml")
public class AuthController implements Initializable, ApplicationListener<StageReadyEvent> {
//
//    @FXML
//    private Label loginUsernameLabel;

    @FXML
    private TextField userIdField;

    @FXML
    private TextField passwordField;
//
//    @FXML
//    private AnchorPane loginFormContainer;

    @FXML
    public GridPane loginFormGridPane;

    @FXML
    public VBox loginFormVBox;

    private final FxWeaver fxWeaver;
    private Stage stage;
    private FormValidation loginFormValidation;

    public AuthController(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Grid Pane styling
        this.loginFormGridPane.setAlignment(Pos.CENTER);
        double width = Util.getScreenWidth() / 3.5;
        double height = Util.getScreenHeight() / 2.5;
        this.loginFormGridPane.setPrefWidth(width * 0.8);
        this.loginFormGridPane.setPrefHeight(height * 0.5);
        this.loginFormGridPane.setVgap(height * 0.08);
        this.loginFormGridPane.setHgap(width * 0.04);

        List<ColumnConstraints> colConstList = this.loginFormGridPane.getColumnConstraints();
        colConstList.get(0).setPercentWidth(40);
        colConstList.get(1).setPercentWidth(60);

    }


    @FXML
    public void handleLoginKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            login();
        }
    }

    @FXML
    public void handleLogin(ActionEvent event) { login(); }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        stage = event.getStage();
    }

    public boolean login(){
        String userId = userIdField.getText();
        String password = passwordField.getText();
        try {
            if(!StageInitializer.authentication.login(userId,password)){
                return false;
            }
            System.out.println("Loading Dashboard ....");
            stage.setScene(new Scene(fxWeaver.loadView(ContentController.class), Util.getScreenWidth(), Util.getScreenHeight()));
            stage.setX(0);
            stage.setY(0);
            stage.setMaximized(true);
            stage.show();
            return true;
        }
        catch (Exception e){
            // Error dialog
            AlertDialog alertDialog = new AlertDialog("Error","Unable to Login !",e.getLocalizedMessage(), Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
            return false;
        }
    }

}
