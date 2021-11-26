package com.jaspreetFlourMill.accountManagement.controllers;

import ch.qos.logback.core.joran.event.BodyEvent;
import com.jaspreetFlourMill.accountManagement.StageReadyEvent;
import com.jaspreetFlourMill.accountManagement.model.*;
import com.jaspreetFlourMill.accountManagement.util.AlertDialog;
import com.jaspreetFlourMill.accountManagement.util.FormValidation;
import com.jaspreetFlourMill.accountManagement.util.Util;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationListener;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

@Component
@FxmlView("/views/registerEmployee.fxml")
public class RegisterEmployeeController implements Initializable, ApplicationListener<StageReadyEvent> {

    @FXML
    private VBox registerEmployeeVBoxContainer;

    @FXML
    private GridPane registerEmployeeGridPane;

    @FXML
    private Label registerEmployeeTitleLabel;

    @FXML
    private TextField employeeUserIdField;

    @FXML
    private TextField employeeNameField;

    @FXML
    private TextField employeeEmailIdField;

    @FXML
    private TextField employeePasswordField;

    @FXML
    private TextField employeeConfirmPasswordField;

    @FXML
    private TextField employeeContactNoField;

    @FXML
    private TextField employeeAddressField;

    @FXML
    private DatePicker employeeDOBField;

    @FXML
    private TextField employeeJobDesignationField;

    @FXML
    private Button registerEmployeeBtn;

    private FormValidation employeeFormValidation;

    @FXML
    private Label employeeUserIdValidLabel;

    @FXML
    private Label employeeNameValidLabel;

    @FXML
    private Label employeeEmailIdValidLabel;

    @FXML
    private Label employeeAddressValidLabel;

    @FXML
    private Label employeePhoneNumberValidLabel;

    @FXML
    private Label employeeDesignationValidLabel;

    @FXML
    private Label employeeDobValidLabel;

    @FXML
    private Label employeePasswordValidLabel;

    @FXML
    private Label employeeConfPasswordValidLabel;
    private Stage stage;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        registerEmployeeVBoxContainer.setPrefWidth(Util.getContentAreaWidth());
        registerEmployeeVBoxContainer.setPrefHeight(Util.getContentAreaHeight());
        registerEmployeeVBoxContainer.setSpacing(registerEmployeeVBoxContainer.getPrefHeight() * 0.08);

        registerEmployeeGridPane.setPrefWidth(registerEmployeeVBoxContainer.getPrefWidth() * 0.70);
        System.out.println(registerEmployeeGridPane.getPrefWidth());
        registerEmployeeGridPane.setPrefHeight(registerEmployeeVBoxContainer.getPrefHeight() * 0.50);
        registerEmployeeGridPane.setHgap(registerEmployeeGridPane.getPrefWidth() * 0.02);
        registerEmployeeGridPane.setVgap(registerEmployeeGridPane.getPrefHeight() * 0.04);

//        registerEmployeeBtn.setPrefWidth(registerEmployeeVBoxContainer.getPrefWidth() * 0.55 );

        List<ColumnConstraints> colConstList = registerEmployeeGridPane.getColumnConstraints();
        colConstList.get(0).setPercentWidth(15);
        colConstList.get(1).setPercentWidth(40);
        colConstList.get(2).setPercentWidth(30);

        employeeDOBField.setPrefWidth(Double.MAX_VALUE);

        registerEmployeeBtn.setDisable(true);

        employeeFormValidation = new FormValidation();
        employeeFormValidation.getFormFields().put("user-id",false);
        employeeFormValidation.getFormFields().put("name",false);
        employeeFormValidation.getFormFields().put("email-id",false);
        employeeFormValidation.getFormFields().put("password",false);
        employeeFormValidation.getFormFields().put("conf-password",false);
        employeeFormValidation.getFormFields().put("phone-number",false);
        employeeFormValidation.getFormFields().put("address",false);
        employeeFormValidation.getFormFields().put("job-designation",false);
        employeeFormValidation.getFormFields().put("dob",false);

        this.addEventListeners();

    }

    private void addEventListeners() {
        employeeUserIdField.textProperty().addListener((observableValue, oldVal, newVal) -> {
            boolean valid = FormValidation.isUsername(
                    newVal,
                    employeeUserIdValidLabel
            ).isValid();
            employeeFormValidation.getFormFields().put("user-id",valid);
            this.validateForm();

        });

        employeeNameField.textProperty().addListener((observableValue, oldVal, newVal) -> {
            boolean validName = FormValidation.isName(
                    newVal,
                    employeeNameValidLabel
            ).isValid();
            employeeFormValidation.getFormFields().put("name",validName);
            this.validateForm();

        });

        employeeEmailIdField.textProperty().addListener((observableValue, oldVal, newVal) -> {
            boolean validName = FormValidation.isEmailId(
                    newVal,
                    employeeEmailIdValidLabel
            ).isValid();
            employeeFormValidation.getFormFields().put("email-id",validName);
            this.validateForm();

        });

        employeeContactNoField.textProperty().addListener((observableValue, oldVal, newVal) -> {
            boolean validPhoneNumber = FormValidation.isPhoneNumber(
                    newVal,
                    employeePhoneNumberValidLabel
            ).isValid();
            employeeFormValidation.getFormFields().put("phone-number",validPhoneNumber);
            this.validateForm();

        });

        employeeAddressField.textProperty().addListener((observableValue, oldVal, newVal) -> {
            boolean validAddress = FormValidation.isAddress(
                   newVal,
                    employeeAddressValidLabel
            ).isValid();
            employeeFormValidation.getFormFields().put("address",validAddress);
            this.validateForm();

        });

        employeeJobDesignationField.textProperty().addListener((observableValue, oldVal, newVal) -> {
            boolean validDesignation = FormValidation.isName(
                    newVal,
                    employeeDesignationValidLabel
            ).isValid();
            employeeFormValidation.getFormFields().put("job-designation",validDesignation);
            this.validateForm();
        });

        employeeDOBField.valueProperty().addListener((observableValue, localDate, newVal) -> {
            boolean validDob = FormValidation.isDob(
                    newVal,
                    employeeDobValidLabel
            ).isValid();
            employeeFormValidation.getFormFields().put("dob",validDob);
            this.validateForm();
        });

        employeePasswordField.textProperty().addListener((observableValue, oldVal, newVal) -> {
            boolean validPassword = FormValidation.isPassword(
                    newVal,
                    employeePasswordValidLabel
            ).isValid();
            employeeFormValidation.getFormFields().put("password",validPassword);
            this.validateForm();
        });

        employeeConfirmPasswordField.textProperty().addListener((observableValue, oldVal, newVal) -> {
            boolean validConfPassword = FormValidation.isConfPassword(
                    employeePasswordField.getText(),
                    newVal,
                    employeeConfPasswordValidLabel).isValid();
            employeeFormValidation.getFormFields().put("conf-password",validConfPassword);
            this.validateForm();
        });

        // Submit form if Enter key is pressed
        registerEmployeeVBoxContainer.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER && this.validateForm()){
                this.registerEmployee();
            }
        });

    }

    private boolean validateForm() {
        if(employeeFormValidation.getFormFields().containsValue(false)){
            registerEmployeeBtn.setDisable(true);
            return false;
        }
        else{
            registerEmployeeBtn.setDisable(false);
            return true;
        }
    }


    public void handleRegisterEmployeeSubmit(ActionEvent e){
        this.registerEmployee();
    }

    private boolean registerEmployee(){
        // Temporary Id Usage
        String userId = employeeUserIdField.getText();
        String name = employeeNameField.getText();
        String emailId = employeeEmailIdField.getText();
        String password = employeePasswordField.getText();
        String contactNo = employeeContactNoField.getText();
        String address = employeeAddressField.getText();
        String jobDesignation = employeeJobDesignationField.getText();
        LocalDate dob = employeeDOBField.getValue();

        if(!this.validateForm()){
            return false;
        }
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = bCryptPasswordEncoder.encode(password);
        User newUser = new User(userId,encodedPassword, Role.EMPLOYEE);



        if(newUser != null){

            // POST request to register employee
            try {
                // User registration
                if (User.save(newUser)) {
                    // Employee registration
                    Employee newEmployee = new Employee(newUser,name, emailId, contactNo,address,jobDesignation,dob);
                    if (Employee.save(newEmployee)) {
                        ContentController.navigationHandler.handleShowHome();
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error: registration failed !!");
                // Information dialog
                AlertDialog alertDialog = new AlertDialog("Error",e.getCause().getMessage(),e.getMessage(), Alert.AlertType.ERROR);
                alertDialog.showErrorDialog(e);
                return false;
            }
        }
        return false;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent stageReadyEvent) {
        this.stage = stageReadyEvent.getStage();
    }
}
