package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.model.Customer;
import com.jaspreetFlourMill.accountManagement.model.CustomerAccount;
import com.jaspreetFlourMill.accountManagement.util.AlertDialog;
import com.jaspreetFlourMill.accountManagement.util.Util;
import com.sun.javafx.menu.MenuItemBase;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;


import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;


@Component
@FxmlView("/views/customerList.fxml")
public class CustomerListController implements Initializable {

    @FXML
    public HBox customerListContainerHBox;

    @FXML
    public VBox customerListContainerVBox;

    @FXML
    public VBox customerListBox;

    @FXML
    public ScrollPane customerListSP;

    @FXML
    public TextField searchCustomerTextField;

    @FXML
    public VBox customerDetailsFromList;

    private Customer[] customers;

    private FxControllerAndView<CustomerDetailsController, Node> customerDetailsCV;

    private final FxWeaver fxWeaver;


    public CustomerListController(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Customer.getAllCustomers().ifPresent(customers -> {
            this.displayCustomers(customers);
            customerDetailsFromList.setVisible(false);
            searchCustomerTextField.textProperty().addListener((observableValue, oldVal, newVal) -> {
                customerListBox.getChildren().clear();
                for (Customer customer : customers) {
                    if (customer.getName().toLowerCase().contains(newVal.toLowerCase())) {
                        Label customerLabel = new Label(customer.getName()
                                + "(ID-" + customer.getCustomerId() + ")");

                        customerLabel.setMinWidth(250);
                        customerLabel.setPrefWidth(customerListSP.getPrefWidth());
                        customerLabel.setMaxWidth(Double.MAX_VALUE);
                        customerLabel.getStyleClass().add("list-item");
                        customerLabel.setOnMouseClicked(mouseEvent -> {
                            showCustomerDetails(customer);
                        });
                        customerListBox.getChildren().add(customerLabel);
                    }
                }
            });
        });
    }


    public void displayCustomers(Customer[] customerArr) {

        for (Customer customer : customerArr) {
            Label customerLabel = new Label(customer.getName()
                    + "(ID-" + customer.getCustomerId() + " )");

            customerLabel.setMinWidth(250);
            customerLabel.setPrefWidth(customerListSP.getPrefWidth());
            customerLabel.setMaxWidth(Double.MAX_VALUE);
//                customerLabel.setPrefHeight(400);
            customerLabel.getStyleClass().add("list-item");
            customerLabel.setOnMouseClicked(mouseEvent -> {
                showCustomerDetails(customer);
            });
            customerListBox.getChildren().add(customerLabel);
        }

    }

    public void showCustomerDetails(Customer customer) {
        customerDetailsCV = fxWeaver.load(CustomerDetailsController.class);
        customerDetailsCV.getView().ifPresent(view -> {
            customerDetailsFromList.getChildren().clear();
            customerDetailsFromList.getChildren().add(view);
            customerDetailsFromList.setVisible(true);
            customerDetailsFromList.setAlignment(Pos.TOP_CENTER);

            Button depositWheatBtn = new Button("Deposit Wheat");
            depositWheatBtn.getStyleClass().add("secondary-btn");

            Button editBtn = new Button("Edit");
            editBtn.getStyleClass().add("tertiary-btn");
            Button deleteAccountBtn = new Button("Delete Account");
            deleteAccountBtn.getStyleClass().add("danger-btn");
//            Button deleteCustomerBtn = new Button("Delete Customer");
//            deleteCustomerBtn.getStyleClass().add("danger-btn");
            Button lostPassbookBtn = new Button("Passbook Lost");
            lostPassbookBtn.getStyleClass().add("alert-btn");

            // Deposit wheat
            depositWheatBtn.setOnAction(actionEvent -> {
                ContentController.navigationHandler.handleShowWheatDeposit(customer.getCustomerId());
            });

            // Edit Customer
            editBtn.setOnAction(actionEvent -> {
                ContentController.navigationHandler.handleEditCustomer(customer);
            });

            // Delete Customer Account
            deleteAccountBtn.setOnAction(actionEvent -> {
                // Confirmation dialog for deleting the customer account
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation to delete account");
                alert.setHeaderText("Delete Customer Account !");
                alert.setContentText("Do you want to delete customer account?");

                Optional<ButtonType> response = alert.showAndWait();
                if (response.get() == ButtonType.OK) {
                    // ... user chose OK
                    if(CustomerAccount.delete(customer)){
                        ContentController.navigationHandler.handleShowCustomers();
                    }

                } else {
                    // ... user chose CANCEL or closed the dialog
                    System.out.println("Delete customer account cancelled");
                }

            });

            // Lost Passbook
            lostPassbookBtn.setOnAction(actionEvent -> {

            });

            HBox btnBox = new HBox(depositWheatBtn, editBtn, deleteAccountBtn, lostPassbookBtn);
            btnBox.setPrefWidth(customerDetailsFromList.getWidth());
            btnBox.setSpacing(customerDetailsFromList.getWidth() * 0.02);
            btnBox.setAlignment(Pos.CENTER);

            customerDetailsFromList.getChildren().add(btnBox);
        });

        customerDetailsCV.getController().updateCustomerDetails(String.valueOf(customer.getCustomerId()));
    }

}
