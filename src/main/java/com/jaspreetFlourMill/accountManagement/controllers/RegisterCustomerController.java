package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.StageReadyEvent;
import com.jaspreetFlourMill.accountManagement.model.Customer;
import com.jaspreetFlourMill.accountManagement.util.FormValidation;
import com.jaspreetFlourMill.accountManagement.util.Util;
import com.jaspreetFlourMill.accountManagement.util.ValidatedResponse;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
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
import java.util.Map;
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
    private Label customerNameValidLabel;

    @FXML
    private TextField customerAddressField;

    @FXML
    private Label customerAddressValidLabel;

    @FXML
    private TextField customerPhoneNumberField;

    @FXML
    private Label customerPhoneNumberValidLabel;

    @FXML
    private TextField customerRationCardNoField;

    @FXML
    private Label customerRationNumberValidLabel;

    @FXML
    private DatePicker customerDOBField;

    @FXML
    private Label customerDobValidLabel;

    @FXML
    private TextField customerAdhaarNoField;

    @FXML
    private Label customerAdhaarNumberValidLabel;

    @FXML
    private Label customerIdProofValidLabel;

    @FXML
    private Label customerRegisterAlertMsg;

    @FXML
    private Button registerCustomerBtn;

    private Label valResponseLabel;

    private FormValidation customerFormValidation;

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

        registerCustomerBtn.setDisable(true);

        validForm = false;

        customerFormValidation = new FormValidation();
        customerFormValidation.getFormFields().put("name",false);
        customerFormValidation.getFormFields().put("address",false);
        customerFormValidation.getFormFields().put("phone-number",false);
        customerFormValidation.getFormFields().put("ration-card-number",false);
        customerFormValidation.getFormFields().put("dob",false);
        customerFormValidation.getFormFields().put("adhaar-number",false);
        customerFormValidation.getFormFields().put("id-proof",false);

        this.addEventListeners();

    }

    @FXML
    public void selectIDProof(ActionEvent event) {
        try{
            File selectedFile = fileChooser.showOpenDialog(stage);
            if(selectedFile != null){
                idProofFileLabel.setText(selectedFile.getAbsolutePath());
                boolean valid = FormValidation.isIdProof(
                        selectedFile,
                        customerIdProofValidLabel
                ).isValid();
                customerFormValidation.getFormFields().put("id-proof",valid);
            }
        }
        catch(Exception e){
            e.getMessage();
        }


    }

    @FXML
    public void registerCustomerSubmit(ActionEvent event){
        if(!this.validateForm()){
            return;
        }

        String customerName = customerNameField.getText();
        String customerAddress = customerAddressField.getText();
        String customerPhoneNumber = customerPhoneNumberField.getText();
        String customerRationCardNo = customerRationCardNoField.getText();
        LocalDate customerDOB = customerDOBField.getValue();
        String customerAdhaarNo = customerAdhaarNoField.getText();

        Customer newCustomer = new Customer(customerName,customerAddress,customerPhoneNumber,customerRationCardNo,
                customerDOB,customerAdhaarNo,idProofFileLabel.getText());

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
            boolean validName = FormValidation.isName(
                    customerNameField.getText(),
                    customerNameValidLabel
            ).isValid();
            customerFormValidation.getFormFields().put("name",validName);
            this.validateForm();

        });

        customerPhoneNumberField.textProperty().addListener((observableValue, s, t1) -> {
            boolean validPhoneNumber = FormValidation.isPhoneNumber(
                    customerPhoneNumberField.getText(),
                    customerPhoneNumberValidLabel
            ).isValid();
            customerFormValidation.getFormFields().put("phone-number",validPhoneNumber);
            this.validateForm();
        });

        customerAdhaarNoField.textProperty().addListener((observableValue, s, t1) -> {
            boolean validAdhaarNumber = FormValidation.isAdhaarNumber(
                    customerAdhaarNoField.getText(),
                    customerAdhaarNumberValidLabel
                    ).isValid();
            customerFormValidation.getFormFields().put("adhaar-number",validAdhaarNumber);
            this.validateForm();
        });

        customerRationCardNoField.textProperty().addListener((observableValue, s, t1) -> {
            boolean validRationNumber = FormValidation.isRationCardNumber(
                    customerRationCardNoField.getText(),
                    customerRationNumberValidLabel
            ).isValid();
            customerFormValidation.getFormFields().put("ration-card-number",validRationNumber);
            this.validateForm();
        });

        customerDOBField.valueProperty().addListener((observableValue, localDate, t1) -> {
            boolean validDob = FormValidation.isDob(
                    customerDOBField.getValue(),
                    customerDobValidLabel
            ).isValid();
            customerFormValidation.getFormFields().put("dob",validDob);
            this.validateForm();
        });

        customerAddressField.textProperty().addListener((observableValue, localDate, t1) -> {
            boolean validAddress = FormValidation.isAddress(
                    customerAddressField.getText(),
                    customerAddressValidLabel
            ).isValid();
            customerFormValidation.getFormFields().put("address",validAddress);
            this.validateForm();
        });
    }

    private boolean validateForm(){
        if(customerFormValidation.getFormFields().containsValue(false)){
            customerRegisterAlertMsg.setText("Please fill the form correctly");
            customerRegisterAlertMsg.getStyleClass().add("validate-err");
            registerCustomerBtn.setDisable(true);
            return false;
        }
        else{
            customerRegisterAlertMsg.setText("");
            customerRegisterAlertMsg.getStyleClass().clear();
            registerCustomerBtn.setDisable(false);
            return true;
        }
    }

}
