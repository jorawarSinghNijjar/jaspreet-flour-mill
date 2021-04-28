package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.StageReadyEvent;
import com.jaspreetFlourMill.accountManagement.model.Employee;
import com.jaspreetFlourMill.accountManagement.util.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationListener;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@FxmlView("/views/loginForm.fxml")
public class AuthController implements ApplicationListener<StageReadyEvent> {
    public static UserSession currentSession;

    @FXML
    private TextField userIdField;

    @FXML
    private TextField passwordField;

    private  final FxWeaver fxWeaver;
    private Stage stage;

    public AuthController(FxWeaver fxWeaver){
        this.fxWeaver = fxWeaver;
    }

    @FXML
    private void initialize(){

    }
    @FXML
    public void handleLogin(ActionEvent event) {
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
                stage.setScene(new Scene(fxWeaver.loadView(DashboardController.class),1300,700));
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
            System.out.println(e.getCause());
            return;
        }


        stage.setScene(new Scene(fxWeaver.loadView(DashboardController.class),1300,700));
        stage.show();
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        stage = event.getStage();
    }


}
