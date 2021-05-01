package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.model.Customer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


import java.io.FileInputStream;
import java.net.URL;
import java.util.ResourceBundle;

@Component
@FxmlView("/views/customerDetails.fxml")
public class CustomerDetailsController implements Initializable {

//    @FXML
//    private AnchorPane customeDetailsPane;

    @FXML
    private Label customerIdDisplay;

    @FXML
    private Label customerAddress;

    @FXML
    private Label customerPhoneNumber;

    @FXML
    private Label customerRationCardNo;

    @FXML
    private ImageView customerIdProofImage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void updateCustomerDetails(String id){
        try{
            Customer updatedCustomer = this.getCustomer(id);

            customerIdDisplay.setText(updatedCustomer.getCustomerId().toString());
            customerAddress.setText(updatedCustomer.getAddress());
            customerPhoneNumber.setText(updatedCustomer.getPhoneNumber());
            customerRationCardNo.setText(updatedCustomer.getRationCardNo());
            customerIdProofImage.setImage(new Image(new FileInputStream(updatedCustomer.getIdProof())));
        }
        catch(Exception e){
            e.getMessage();
        }
    }

    public Customer getCustomer(String id) throws Exception{
        String uri =  "http://localhost:8080/customers/"+ id;
        RestTemplate restTemplate = new RestTemplate();
        Customer responseEntity = restTemplate.getForObject(uri,Customer.class);

        return responseEntity;
    }
}
