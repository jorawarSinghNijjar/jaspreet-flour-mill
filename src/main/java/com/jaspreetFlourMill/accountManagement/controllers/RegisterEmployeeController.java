package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.model.Customer;
import com.jaspreetFlourMill.accountManagement.model.Employee;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import net.rgielen.fxweaver.core.FxmlView;
//import net.synedra.validatorfx.Validator;
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

    private boolean validForm = true;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void submitRegisterEmployee(ActionEvent e){
        String name = employeeNameField.getText();
        String password = employeePasswordField.getText();
        String confirmPassword = employeeConfirmPasswordField.getText();
        String contactNo = employeeContactNoField.getText();
        String address = employeeAddressField.getText();
        String jobDesignation = employeeJobDesignationField.getText();
        LocalDate dob = employeeDOBField.getValue();

        if(!password.equals(confirmPassword)){
            validForm = false;
        }

        if(validForm){
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
}
