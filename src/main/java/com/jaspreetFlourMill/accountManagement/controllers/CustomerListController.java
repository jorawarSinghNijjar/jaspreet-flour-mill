package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.model.Customer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;


import java.net.URL;
import java.util.ResourceBundle;


@Component
@FxmlView("/views/customerList.fxml")
public class CustomerListController implements Initializable {

    @FXML
    private VBox customerListBox;

    @FXML
    private TextField searchCustomerTextField;

    @FXML
    private HBox customerDetailsFromList;

    private Customer[] customers;

    private FxControllerAndView<CustomerDetailsController, Node> customerDetailsCV;

    private final FxWeaver fxWeaver;

    public CustomerListController(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.displayCustomers();
        searchCustomerTextField.textProperty().addListener((observableValue, oldVal, newVal) -> {
            customerListBox.getChildren().clear();
            for(Customer customer: customers){
                if(customer.getName().toLowerCase().contains(newVal.toLowerCase())){
                    Label customerLabel = new Label(customer.getName()
                            + "(ID-" + customer.getCustomerId() + " )" );
                    customerLabel.getStyleClass().add("list-item");
                    customerLabel.setMinWidth(250);
                    customerLabel.setOnMouseClicked(mouseEvent -> {
                        showCustomerDetails(customer.getCustomerId().toString());
                    });
                    customerListBox.getChildren().add(customerLabel);
                }
            }
        });

    }


    public void displayCustomers(){
        try {
            this.customers = Customer.getAllCustomers();
            for(Customer customer: customers){
                Label customerLabel = new Label(customer.getName()
                        + "(ID-" + customer.getCustomerId() + " )" );
                customerLabel.getStyleClass().add("list-item");
                customerLabel.setMinWidth(250);
                customerLabel.setOnMouseClicked(mouseEvent -> {
                    showCustomerDetails(customer.getCustomerId().toString());
                });
                customerListBox.getChildren().add(customerLabel);
            }
        }
        catch (Exception e){
            System.out.println("Failed to get All Customers !");
            System.out.println(e.getMessage());
        }
    }

    public void showCustomerDetails(String id){
        customerDetailsCV = fxWeaver.load(CustomerDetailsController.class);
        customerDetailsCV.getView().ifPresent(view -> {
            customerDetailsFromList.getChildren().clear();
            customerDetailsFromList.getChildren().add(view);
            customerDetailsFromList.getStyleClass().add("no-border");
            customerDetailsFromList.setAlignment(Pos.CENTER);
        });

        customerDetailsCV.getController().updateCustomerDetails(id);
    }

}
