package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.StageReadyEvent;
import com.jaspreetFlourMill.accountManagement.model.Customer;
import com.jaspreetFlourMill.accountManagement.model.Employee;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationListener;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

@Component
public class RegisterCustomerController implements Initializable, ApplicationListener<StageReadyEvent> {

    private Stage stage;

    private FileChooser fileChooser;

    @FXML
    private Label idProofFileLabel;

    @FXML
    private TextField customerNameField;

    @FXML
    private TextField customerAddressField;

    @FXML
    private TextField customerPhoneNumberField;

    @FXML
    private TextField customerRationCardNoField;

    @FXML
    private DatePicker customerDOBField;

    @FXML
    private TextField customerAdhaarNoField;

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        stage = event.getStage();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
         fileChooser = new FileChooser();

    }

    @FXML
    public void selectIDProof(ActionEvent event) {
        try{
            File selectedFile = fileChooser.showOpenDialog(stage);
            if(selectedFile != null){
                idProofFileLabel.setText(selectedFile.getAbsolutePath());
            }
        }
        catch(Exception e){
            e.getMessage();
        }


    }

    @FXML
    public void registerCustomerSubmit(ActionEvent event){
        String customerName = customerNameField.getText();
        String customerAddress = customerAddressField.getText();
        String customerPhoneNumber = customerPhoneNumberField.getText();
        String customerRationCardNo = customerRationCardNoField.getText();
        LocalDate customerDOB = customerDOBField.getValue();
        String customerAdhaarNo = customerAdhaarNoField.getText();

        Customer newCustomer = new Customer(customerName,customerAddress,customerPhoneNumber,customerRationCardNo,
                customerDOB,customerAdhaarNo,idProofFileLabel.getText());

//        System.out.println(newCustomer.toString());

        if(newCustomer != null){
            // POST request to register employee
            final String uri =  "http://localhost:8080/customers/";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Customer> req = new HttpEntity<>(newCustomer,httpHeaders);
            ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST,req,String.class);
            System.out.println(result.getBody());
        }
    }


}
