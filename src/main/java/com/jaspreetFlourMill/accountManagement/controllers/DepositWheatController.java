package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.model.Customer;
import com.jaspreetFlourMill.accountManagement.model.CustomerAccount;
import com.jaspreetFlourMill.accountManagement.model.Sales;
import com.jaspreetFlourMill.accountManagement.util.FormValidation;
import com.jaspreetFlourMill.accountManagement.util.Util;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

    @FXML
    private Label customerIdValidLabel;

    @FXML
    private Label wheatDepositQtyValidLabel;

    @FXML
    private Label wheatDedQtyValidLabel;

    @FXML
    private Button wheatDepositSubmitBtn;

    private FormValidation wheatDepositFormValidation;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        wheatDepositSubmitBtn.setDisable(true);

        wheatDepositFormValidation = new FormValidation();
        wheatDepositFormValidation.getFormFields().put("customer-id", false);
        wheatDepositFormValidation.getFormFields().put("wheat-deposit-qty",false);
        wheatDepositFormValidation.getFormFields().put("wheat-ded-qty",false);

        this.addEventListeners();
    }

    private void addEventListeners() {
        customerIdInputDepositPage.textProperty().addListener((observableValue, oldVal, newVal) -> {
            boolean validCustomerId = FormValidation.isInteger(
                    newVal,
                    customerIdValidLabel
            ).isValid();
            wheatDepositFormValidation.getFormFields().put("customer-id",validCustomerId);
            this.validateForm();
        });

        wheatDepositQtyInput.textProperty().addListener((observableValue, oldVal, newVal) -> {
            boolean validDepositQty = FormValidation.isDouble(
                    newVal,
                    wheatDepositQtyValidLabel
            ).isValid();
            wheatDepositFormValidation.getFormFields().put("wheat-deposit-qty",validDepositQty);
            this.validateForm();
        });

        wheatProcessingDeductionQtyInput.textProperty().addListener((observableValue, oldVal, newVal) -> {
            boolean validDedQty = FormValidation.isDouble(
                    newVal,
                    wheatDedQtyValidLabel
            ).isValid();
            wheatDepositFormValidation.getFormFields().put("wheat-ded-qty",validDedQty);
            this.validateForm();
        });

    }

    private boolean validateForm() {
        if(wheatDepositFormValidation.getFormFields().containsValue(false)){
            wheatDepositSubmitBtn.setDisable(true);
            return false;
        }
        else{
            wheatDepositSubmitBtn.setDisable(false);
            return true;
        }
    }


    @FXML
    public void submitWheatDeposit(){
        Integer customerId = Integer.parseInt(customerIdInputDepositPage.getText());
        Double wheatDepositQty = Double.parseDouble(wheatDepositQtyInput.getText());
        Double wheatProcessingDeductionQty = Double.parseDouble(wheatProcessingDeductionQtyInput.getText());

        try{
            Customer customer = Customer.getCustomer(customerId);

            CustomerAccount fetchedCustomerAccount = CustomerAccount.getCustomerAccount(customerId);

            if(fetchedCustomerAccount != null){
                System.out.println("Updating Customer Account --> "
                        + fetchedCustomerAccount.getCustomerAccountId());

                fetchedCustomerAccount.addWheatToAccount(wheatDepositQty,wheatProcessingDeductionQty);
                CustomerAccount.updateCustomerAccount(customerId,fetchedCustomerAccount);
                // add wheat to total wheat balance of company
                Sales.addWheatDeposit(wheatDepositQty);

                System.out.println("\n Customer Account Update Successful");
                ContentController.navigationHandler.handleShowHome();
            }
            else
                {
                CustomerAccount newCustomerAccount = new CustomerAccount(customer,wheatDepositQty,
                        wheatProcessingDeductionQty);
                System.out.println("Updating Customer Account for" + newCustomerAccount.getCustomer().getName());
                    System.out.println(newCustomerAccount.toString());
                if(newCustomerAccount != null){
                    // POST request to register customer account
                    final String uri =  "http://localhost:8080/customer-accounts/";
                    RestTemplate restTemplate = new RestTemplate();

                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);

                    HttpEntity<CustomerAccount> req = new HttpEntity<>(newCustomerAccount,httpHeaders);
                    ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST,req,String.class);

                    if(result.getStatusCode() == HttpStatus.OK){
                        System.out.println("Customer Acccount Created Successfully "+result.getBody());
                        // add wheat to total wheat balance of company
                        Sales.addWheatDeposit(wheatDepositQty);

                        ContentController.navigationHandler.handleShowHome();
                    }
                    else {
                        System.out.println("Customer Account creation failed");
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

}
