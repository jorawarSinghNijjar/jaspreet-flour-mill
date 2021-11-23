package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.StageReadyEvent;
import com.jaspreetFlourMill.accountManagement.model.Customer;
import com.jaspreetFlourMill.accountManagement.util.AlertDialog;
import com.jaspreetFlourMill.accountManagement.util.FormValidation;

import com.jaspreetFlourMill.accountManagement.util.Util;
import com.jaspreetFlourMill.accountManagement.util.ValidatedResponse;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
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
import java.util.List;
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
    private VBox registerCustomerVBoxContainer;

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

    public String formType;

    private Customer currentCustomer;


    public RegisterCustomerController(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        stage = event.getStage();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Layout
        registerCustomerVBoxContainer.setPrefWidth(Util.getContentAreaWidth());
        registerCustomerVBoxContainer.setPrefHeight(Util.getContentAreaHeight());
        registerCustomerVBoxContainer.setSpacing(registerCustomerVBoxContainer.getPrefHeight() * 0.08);

        customerDetailFormGrid.setPrefWidth(registerCustomerVBoxContainer.getPrefWidth() * 0.70);
        customerDetailFormGrid.setPrefHeight(registerCustomerVBoxContainer.getPrefHeight() * 0.50);
        customerDetailFormGrid.setHgap(customerDetailFormGrid.getPrefWidth() * 0.02);
        customerDetailFormGrid.setVgap(customerDetailFormGrid.getPrefHeight() * 0.04);

        List<ColumnConstraints> colConstList = customerDetailFormGrid.getColumnConstraints();
        colConstList.get(0).setPercentWidth(15);
        colConstList.get(1).setPercentWidth(40);
        colConstList.get(2).setPercentWidth(30);

        customerDOBField.setPrefWidth(Double.MAX_VALUE);

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

    public void populateFields(Customer customer) {
        customerNameField.setText(customer.getName());
        customerAddressField.setText(customer.getAddress());
        customerPhoneNumberField.setText(customer.getPhoneNumber());
        customerRationCardNoField.setText(customer.getRationCardNo());
//        customerDOBField.setValue(customer.getDob());
        customerAdhaarNoField.setText(customer.getAdhaarNo());
        idProofFileLabel.setText(customer.getIdProof());

        registerCustomerBtn.setText("Update Customer");

        currentCustomer = customer;
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
                this.validateForm();
            }
        }
        catch(Exception e){
            e.getMessage();
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error",e.getCause().getMessage(),e.getMessage(), Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
        }


    }

    private void registerCustomer(){
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
            if(this.formType == "REGISTER") {
                Customer.save(newCustomer).ifPresent(savedCustomer -> {
                    ContentController.navigationHandler.handleShowWheatDeposit(savedCustomer.getCustomerId());
                });

            }
            else if(this.formType == "EDIT"){
                Customer.update(currentCustomer.getCustomerId(),newCustomer).ifPresent(updatedCustomer -> {
                    ContentController.navigationHandler.handleShowWheatDeposit(updatedCustomer.getCustomerId());
                });
            }
            else {
                System.out.println("form type is empty");
            }
        }
    }

    @FXML
    public void handleRegisterCustomerSubmit(ActionEvent event){
        registerCustomer();
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

        customerAddressField.textProperty().addListener((observableValue, oldVal, newVal) -> {
            boolean validAddress = FormValidation.isAddress(
                    customerAddressField.getText(),
                    customerAddressValidLabel
            ).isValid();
            customerFormValidation.getFormFields().put("address",validAddress);
            this.validateForm();
        });

        // Submit form if Enter key is pressed
        this.stage.getScene().setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER){
                registerCustomer();
            }
        });
    }

    private boolean validateForm(){
        if(customerFormValidation.getFormFields().containsValue(false)){
            registerCustomerBtn.setDisable(true);
            return false;
        }
        else{
            registerCustomerBtn.setDisable(false);
            return true;
        }
    }

}
