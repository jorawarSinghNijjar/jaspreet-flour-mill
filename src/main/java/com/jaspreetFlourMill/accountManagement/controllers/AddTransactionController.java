package com.jaspreetFlourMill.accountManagement.controllers;

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
import org.springframework.stereotype.Component;

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
        double attaPickupQty = Double.parseDouble(attaPickupQtyInput.getText());
        double grindingChargesPaid = Double.parseDouble(grindingChargesPaidInput.getText());
        double grindingCharges = Double.parseDouble(grindingChargesInput.getText());
        String orderPickedBy = orderPickedByInput.getText();
        String cashierName = cashierNameLabel.getText();

        Transaction newTransaction = new Transaction(
                2,
                attaPickupQty,
                grindingCharges,
                grindingChargesPaid,
                orderPickedBy,
                cashierName
        );

        System.out.println(newTransaction.toString());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Transaction submitted successfully");
        alert.show();
    }

}
