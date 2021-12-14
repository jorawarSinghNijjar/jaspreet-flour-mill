package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.model.Customer;
import com.jaspreetFlourMill.accountManagement.model.CustomerAccount;

import com.jaspreetFlourMill.accountManagement.util.AlertDialog;
import com.jaspreetFlourMill.accountManagement.util.Util;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


import java.io.FileInputStream;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
@FxmlView("/views/customerDetails.fxml")
public class CustomerDetailsController implements Initializable {

    @FXML
    public AnchorPane customerDetailAnchorPane;

    @FXML
    public VBox customerDetailVBox;

    @FXML
    public GridPane customerDetailGridPane;

    @FXML
    public HBox imageContainerHBox;

    private FxControllerAndView<ModalImageViewController, Node> modalImageViewCV;

    private FxControllerAndView<ContentController, Node> contentControllerCV;

    @FXML
    private Label customerIdDisplay;

    @FXML
    private Label customerAddress;

    @FXML
    private Label customerPhoneNumber;

    @FXML
    private Label customerRationCardNo;

    @FXML
    private Label customerAdhaarNo;

    @FXML
    public ImageView customerIdProofImage;

    @FXML
    private Label wheatQtyStored;

    @FXML
    private Label qtyDeduction;

    @FXML
    private Label initialWheatQty;

    @FXML
    private Label currentWheatBalance;

    @FXML
    private Label totalGrindingChargesBalance;

    @FXML
    private Label grindingRate;



    public String idProofImageUri;

    private final FxWeaver fxWeaver;


    public CustomerDetailsController(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customerDetailVBox.setVisible(false);

    }

    public void updateCustomerDetails(String id) {
        // Find customer
        Customer.get(id).ifPresentOrElse((updatedCustomer) -> {
            customerIdDisplay.setText(updatedCustomer.getCustomerId().toString());
            customerAddress.setText(updatedCustomer.getAddress());
            customerPhoneNumber.setText(updatedCustomer.getPhoneNumber());
            customerRationCardNo.setText(updatedCustomer.getRationCardNo());
            customerAdhaarNo.setText(updatedCustomer.getAdhaarNo());
            try {
                customerIdProofImage.setImage(new Image(new FileInputStream(updatedCustomer.getIdProof())));
                this.idProofImageUri = updatedCustomer.getIdProof();
            } catch (Exception e) {
                // Information dialog
                AlertDialog alertDialog = new AlertDialog("Error", "Error reading updating input file", e.getMessage(), Alert.AlertType.ERROR);
                alertDialog.showErrorDialog(e);
            }

            wheatQtyStored.setText("XXX kg");
            qtyDeduction.setText("XXX kg");
            initialWheatQty.setText("XXX kg");
            currentWheatBalance.setText("\u20B9 XXX");
            grindingRate.setText("\u20B9 XXX");
            totalGrindingChargesBalance.setText("\u20B9 XXX");

            customerDetailVBox.setVisible(true);

            // If customer is found, find customer account and update it to view
            CustomerAccount.get(updatedCustomer.getCustomerId()).ifPresent((updatedCustomerAccount) -> {

                String wheatQtyStoredDisplay = updatedCustomerAccount.getWheatDepositQty() + " kg";
                String qtyDeductionDisplay = updatedCustomerAccount.getWheatProcessingDeductionQty() + " kg";
                String initialWheatQtyDisplay = updatedCustomerAccount.getInitialWheatQty() + " kg";
                String currentWheatBalanceDisplay = updatedCustomerAccount.getCurrentWheatBalance() + " kg";
                String grindingRateDisplay = updatedCustomerAccount.getGrindingRate() + " kg";
                String totalGrindingChargesBalanceDisplay = "\u20B9 " + updatedCustomerAccount.getGrindingChargesBalance();

                wheatQtyStored.setText(wheatQtyStoredDisplay);
                qtyDeduction.setText(qtyDeductionDisplay);
                initialWheatQty.setText(initialWheatQtyDisplay);
                currentWheatBalance.setText(currentWheatBalanceDisplay);
                grindingRate.setText(grindingRateDisplay);
                totalGrindingChargesBalance.setText(totalGrindingChargesBalanceDisplay);

                customerDetailVBox.setVisible(true);
            });
        }, () -> {
            customerDetailVBox.setVisible(false);
        });


    }

}
