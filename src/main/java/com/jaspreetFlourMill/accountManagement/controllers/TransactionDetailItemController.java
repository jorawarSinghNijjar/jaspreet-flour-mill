package com.jaspreetFlourMill.accountManagement.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;


import java.net.URL;
import java.util.ResourceBundle;

@Component
@FxmlView("/views/transactionDetailItem.fxml")
public class TransactionDetailItemController implements Initializable {
    @FXML
    private Label transactionIdLabel;

    @FXML
    private Label timeStampLabel;

    @FXML
    private Label attaPickupQtyLabel;

    @FXML
    private Label grindingChargesPaidLabel;

    @FXML
    private Label grindingBalanceLabel;

    @FXML
    private Label storedWheatBalanceLabel;

    @FXML
    private Label orderPickedByLabel;

    @FXML
    private Label cashierLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public  void setTransactionDetails(
            String transactionId, String time, double attaPickupQty,
            double grindingChargesPaid, double grindingBalance,
            double storedWheatBalance, String orderPickedBy,
            String cashier
    ) {
        transactionIdLabel.setText(transactionId);
        timeStampLabel.setText(time);
        attaPickupQtyLabel.setText(String.valueOf(attaPickupQty));
        grindingChargesPaidLabel.setText(String.valueOf(grindingChargesPaid));
        grindingBalanceLabel.setText(String.valueOf(grindingBalance));
        storedWheatBalanceLabel.setText(String.valueOf(storedWheatBalance));
        orderPickedByLabel.setText(orderPickedBy);
        cashierLabel.setText(cashier);

    }

}
