package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.model.Customer;
import com.jaspreetFlourMill.accountManagement.model.CustomerAccount;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@FxmlView("/views/depositWheat.fxml")
public class DepositWheatController implements Initializable {
    @FXML
    private TextField customerIdInputDepositPage;

    @FXML
    private TextField wheatDepositQtyInput;

    @FXML
    private TextField wheatProcessingDeductionQtyInput;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    public void submitWheatDeposit(){
        Integer customerId = Integer.parseInt(customerIdInputDepositPage.getText());
        Double wheatDepositQty = Double.parseDouble(wheatDepositQtyInput.getText());
        Double wheatProcessingDeductionQty = Double.parseDouble(wheatProcessingDeductionQtyInput.getText());

        try{
            Customer customer = Customer.getCustomer(customerId);

            CustomerAccount newCustomerAccount = new CustomerAccount(customer,wheatDepositQty,
                    wheatProcessingDeductionQty);

            System.out.println(newCustomerAccount.toString());

            if(newCustomerAccount != null){
                // POST request to register employee
                final String uri =  "http://localhost:8080/customer-accounts/";
                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<CustomerAccount> req = new HttpEntity<>(newCustomerAccount,httpHeaders);
                ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST,req,String.class);

                if(result != null){
                    System.out.println(result.getBody());
                    ContentController.navigationHandler.handleShowHome();
                }
            }
        }
        catch(Exception e){
            e.getMessage();
        }

    }
}
