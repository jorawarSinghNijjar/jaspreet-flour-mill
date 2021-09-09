package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.StageReadyEvent;
import com.jaspreetFlourMill.accountManagement.model.Customer;
import com.jaspreetFlourMill.accountManagement.util.FormValidation;
import com.jaspreetFlourMill.accountManagement.util.ValidatedResponse;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons;
import jiconfont.javafx.IconFontFX;
import jiconfont.javafx.IconNode;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationListener;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

@Component
@FxmlView("/views/registerCustomer.fxml")
public class RegisterCustomerController implements Initializable, ApplicationListener<StageReadyEvent> {

    private FxControllerAndView<ContentController, Node> contentControllerCV;

    private final FxWeaver fxWeaver;

    private Stage stage;

    private FileChooser fileChooser;

    @FXML
    private GridPane customerDetailFormGrid;

    @FXML
    private Label idProofFileLabel;

    @FXML
    private TextField customerNameField;

    @FXML
    private TextField customerAddressField;

    @FXML
    private TextField customerPhoneNumberField;

    @FXML
    private TextField customerRationCardNoField;

    @FXML
    private DatePicker customerDOBField;

    @FXML
    private TextField customerAdhaarNoField;

    private Label valResponseLabel;

    private boolean validForm;

    public RegisterCustomerController(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        stage = event.getStage();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
         fileChooser = new FileChooser();
        IconFontFX.register(GoogleMaterialDesignIcons.getIconFont());
        valResponseLabel =  new Label();
        customerDetailFormGrid.add(valResponseLabel,2,0,1,1);
        validForm = false;
        this.addEventListeners();
    }

    @FXML
    public void selectIDProof(ActionEvent event) {
        try{
            File selectedFile = fileChooser.showOpenDialog(stage);
            if(selectedFile != null){
                idProofFileLabel.setText(selectedFile.getAbsolutePath());
            }
        }
        catch(Exception e){
            e.getMessage();
        }


    }

    @FXML
    public void registerCustomerSubmit(ActionEvent event){
        String customerName = customerNameField.getText();
        String customerAddress = customerAddressField.getText();
        String customerPhoneNumber = customerPhoneNumberField.getText();
        String customerRationCardNo = customerRationCardNoField.getText();
        LocalDate customerDOB = customerDOBField.getValue();
        String customerAdhaarNo = customerAdhaarNoField.getText();

//        boolean validForm = this.validate();

        if(!validForm){
            return;
        }

        Customer newCustomer = new Customer(customerName,customerAddress,customerPhoneNumber,customerRationCardNo,
                customerDOB,customerAdhaarNo,idProofFileLabel.getText());

//        System.out.println(newCustomer.toString());

        if(newCustomer != null){
            // POST request to register employee
            final String uri =  "http://localhost:8080/customers/";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Customer> req = new HttpEntity<>(newCustomer,httpHeaders);
            ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST,req,String.class);

            if(result != null){
                System.out.println(result.getBody());
                ContentController.navigationHandler.handleShowHome();
            }
        }
    }

    private void addEventListeners(){
        customerNameField.textProperty().addListener((observableValue, s, t1) -> {
            validForm = this.validate();
        });
    }

    private void removeEventListeners(){
//        customerNameField.textProperty().removeListener();
    }

    private boolean validate(){
        ValidatedResponse customerNameValResp = FormValidation.isName(
                customerNameField.getText(),
                valResponseLabel
        );

        return false;
    }

}
