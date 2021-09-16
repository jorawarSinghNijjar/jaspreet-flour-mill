package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.model.Customer;
import com.jaspreetFlourMill.accountManagement.model.Employee;
import com.jaspreetFlourMill.accountManagement.util.FormValidation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

@Component
@FxmlView("/views/registerEmployee.fxml")
public class RegisterEmployeeController implements Initializable {

    @FXML
    private TextField employeeNameField;

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
    private Label employeeNameValidLabel;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        registerEmployeeBtn.setDisable(true);

        employeeFormValidation = new FormValidation();
        employeeFormValidation.getFormFields().put("name",false);
        employeeFormValidation.getFormFields().put("password",false);
        employeeFormValidation.getFormFields().put("conf-password",false);
        employeeFormValidation.getFormFields().put("phone-number",false);
        employeeFormValidation.getFormFields().put("address",false);
        employeeFormValidation.getFormFields().put("job-designation",false);
        employeeFormValidation.getFormFields().put("dob",false);

        this.addEventListeners();

    }

    private void addEventListeners() {
        employeeNameField.textProperty().addListener((observableValue, oldVal, newVal) -> {
            boolean validName = FormValidation.isName(
                    newVal,
                    employeeNameValidLabel
            ).isValid();
            employeeFormValidation.getFormFields().put("name",validName);
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


    public void submitRegisterEmployee(ActionEvent e){
        String name = employeeNameField.getText();
        String password = employeePasswordField.getText();
        String contactNo = employeeContactNoField.getText();
        String address = employeeAddressField.getText();
        String jobDesignation = employeeJobDesignationField.getText();
        LocalDate dob = employeeDOBField.getValue();

        if(!this.validateForm()){
            return;
        }
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = bCryptPasswordEncoder.encode(password);
            Employee newEmployee = new Employee(name,encodedPassword,contactNo,address,jobDesignation,dob);

            if(newEmployee != null){
                // POST request to register employee
                final String uri =  "http://localhost:8080/employees/";
                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Employee> req = new HttpEntity<>(newEmployee,httpHeaders);
                ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST,req,String.class);

                if(result != null){
                    System.out.println(result.getBody());
                    ContentController.navigationHandler.handleShowHome();
                }
            }


    }
}
