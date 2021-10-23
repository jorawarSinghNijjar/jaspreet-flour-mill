package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.StageReadyEvent;
import com.jaspreetFlourMill.accountManagement.model.Employee;
import com.jaspreetFlourMill.accountManagement.model.Sales;
import com.jaspreetFlourMill.accountManagement.model.Stock;
import com.jaspreetFlourMill.accountManagement.util.UserSession;
import com.jaspreetFlourMill.accountManagement.util.Util;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
import org.springframework.context.ApplicationListener;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@FxmlView("/views/loginForm.fxml")
public class AuthController implements Initializable,ApplicationListener<StageReadyEvent> {
    public static UserSession currentSession;
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
    private GridPane loginFormGridPane;

    @FXML VBox loginFormVBox;

    private  final FxWeaver fxWeaver;
    private Stage stage;

    public AuthController(FxWeaver fxWeaver){
        this.fxWeaver = fxWeaver;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // VBox styling
        double vBoxHeight = this.loginFormVBox.getPrefHeight();
        double vBoxWidth = this.loginFormVBox.getPrefWidth();
        double vSpacing = vBoxHeight * 0.08;
        this.loginFormVBox.setSpacing(vSpacing);

        // Grid Pane styling
        this.loginFormGridPane.setAlignment(Pos.CENTER);
        this.loginFormGridPane.setPrefWidth(vBoxWidth * 0.8);
        this.loginFormGridPane.setPrefHeight(vBoxHeight * 0.5);

    }


    @FXML
    public void handleLoginKeyPress(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER){
            login();
        }
    }

    @FXML
    public void handleLogin(ActionEvent event) {
       login();
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        stage = event.getStage();
    }

    public void login(){
        String userId = userIdField.getText();
        String password = passwordField.getText();

        //GET request to get employee with userId
        if(userId == "" || userId == null){
            System.out.println("Please enter User Id. Field is empty!");
            return;
        }
        try{

            if(userId.equals("admin") && password.equals("admin")){
                currentSession = UserSession.getInstance(userId, UserSession.UserType.ADMIN);
                stage.setScene(new Scene(fxWeaver.loadView(ContentController.class), Util.getScreenWidth(),Util.getScreenHeight()));
                stage.setX(0);
                stage.setY(0);
                stage.setMaximized(true);
                stage.show();
                return;
            }

            final String uri =  "http://localhost:8080/employees/"+ userId;
            RestTemplate restTemplate = new RestTemplate();

            Employee responseEntity = restTemplate.getForObject(uri,Employee.class);

            String encodedPassword = responseEntity.getPassword();

            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            boolean validPassword = bCryptPasswordEncoder.matches(password,encodedPassword);

            if(!validPassword) {
                System.out.println("Invalid Password");
                return;
            }
            else{
                currentSession = UserSession.getInstance(userId, UserSession.UserType.EMPLOYEE);
            }
        }
        catch(Exception e){
            e.printStackTrace();
            return;
        }


        stage.setScene(new Scene(fxWeaver.loadView(ContentController.class),Util.getScreenWidth(),Util.getScreenHeight()));
        stage.setX(0);
        stage.setY(0);
        stage.setMaximized(true);
        stage.show();
    }



}
