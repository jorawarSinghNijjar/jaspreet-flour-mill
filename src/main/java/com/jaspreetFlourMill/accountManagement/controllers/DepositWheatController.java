package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.model.Customer;
import com.jaspreetFlourMill.accountManagement.model.CustomerAccount;
import com.jaspreetFlourMill.accountManagement.model.Sales;
import com.jaspreetFlourMill.accountManagement.util.AlertDialog;
import com.jaspreetFlourMill.accountManagement.util.FormValidation;
import com.jaspreetFlourMill.accountManagement.util.Util;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
@FxmlView("/views/depositWheat.fxml")
public class DepositWheatController implements Initializable {
    @FXML
    public GridPane wheatDepositFormGP;

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

            Optional<Customer> customer = Customer.get(String.valueOf(customerId));

            if(customer.isPresent()){
                Optional<CustomerAccount> fetchedCustomerAccount = CustomerAccount.get(customerId);

                if(fetchedCustomerAccount.isPresent()){
                    // Add wheat balance to customer account
                    fetchedCustomerAccount.get().addWheatToAccount(wheatDepositQty,wheatProcessingDeductionQty);
                    // PUT request UPDATE customer account
                    CustomerAccount.update(customerId,fetchedCustomerAccount.get());
                    // add wheat to total wheat balance of company
                    Sales.addWheatDeposit(wheatDepositQty);

                    ContentController.navigationHandler.handleShowHome();
                }
                else
                {
                    CustomerAccount newCustomerAccount = new CustomerAccount(customer.get(),wheatDepositQty,
                            wheatProcessingDeductionQty);

                    if(CustomerAccount.save(newCustomerAccount)){
                        // add wheat to total wheat balance of company
                        Sales.addWheatDeposit(wheatDepositQty);
                        ContentController.navigationHandler.handleShowHome();
                    }
                }
            }
    }

    public void setCurrentCustomerId(Integer id){
        customerIdInputDepositPage.setText(String.valueOf(id));
    }

}
