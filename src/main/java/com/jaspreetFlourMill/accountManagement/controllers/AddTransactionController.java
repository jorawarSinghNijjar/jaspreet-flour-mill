package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.model.Customer;
import com.jaspreetFlourMill.accountManagement.model.Transaction;
import com.jaspreetFlourMill.accountManagement.util.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@FxmlView("/views/addTransaction.fxml")
public class AddTransactionController implements Initializable {

    @FXML
    public TextField customerIdInput;

    @FXML
    private TextField attaPickupQtyInput;

    @FXML
    private TextField grindingChargesInput;

    @FXML
    private TextField grindingChargesPaidInput;

    @FXML
    private TextField orderPickedByInput;

    @FXML
    private Label cashierNameLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cashierNameLabel.setText("Jaspreet Singh");
    }

    @FXML
    public void submitTransaction(ActionEvent event){
        int customerId = Integer.parseInt(customerIdInput.getText());
        double attaPickupQty = Double.parseDouble(attaPickupQtyInput.getText());
        double grindingChargesPaid = Double.parseDouble(grindingChargesPaidInput.getText());
        double grindingCharges = Double.parseDouble(grindingChargesInput.getText());
        String orderPickedBy = orderPickedByInput.getText();
        String cashierName = cashierNameLabel.getText();

        try{
            Customer customer = Customer.getCustomer(customerId);

            Transaction newTransaction = new Transaction(
                    customer,
                    attaPickupQty,
                    grindingCharges,
                    grindingChargesPaid,
                    orderPickedBy,
                    cashierName
            );

            System.out.println(newTransaction.toString());

            if(newTransaction != null){
                // POST request to register employee
                final String uri =  "http://localhost:8080/transactions/";
                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Transaction> req = new HttpEntity<>(newTransaction,httpHeaders);
                ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST,req,String.class);

                if(result != null){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Transaction submitted successfully");
                    alert.show();
                    customerIdInput.setText("");
                    attaPickupQtyInput.setText("");
                    grindingChargesInput.setText("");
                    grindingChargesPaidInput.setText("");
                    orderPickedByInput.setText("");
                }
            }

        }
        catch(Exception e){
            e.getMessage();
        }

    }

}
