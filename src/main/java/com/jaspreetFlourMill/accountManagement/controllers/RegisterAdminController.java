package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.StageInitializer;
import com.jaspreetFlourMill.accountManagement.StageReadyEvent;
import com.jaspreetFlourMill.accountManagement.model.Admin;
import com.jaspreetFlourMill.accountManagement.model.Role;
import com.jaspreetFlourMill.accountManagement.model.User;
import com.jaspreetFlourMill.accountManagement.util.AlertDialog;
import com.jaspreetFlourMill.accountManagement.util.FormValidation;
import com.jaspreetFlourMill.accountManagement.util.Util;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Dimension2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons;
import jiconfont.javafx.IconFontFX;
import jiconfont.javafx.IconNode;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationListener;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;


import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Component
@FxmlView("/views/registerAdmin.fxml")
public class RegisterAdminController implements Initializable, ApplicationListener<StageReadyEvent> {
    private Stage stage;

    @FXML
    private VBox adminRegisterContainerVBox;
    @FXML
    private GridPane adminRegisterFormGP;
    @FXML
    private TextField adminIdInputField;
    @FXML
    private TextField adminNameInputField;
    @FXML
    private TextField adminLicenseKeyInputField;
    @FXML
    private PasswordField adminPasswordInputField;
    @FXML
    private PasswordField adminConfPasswordInputField;
    @FXML
    private TextField adminEmailIdInputField;
    @FXML
    private Label adminIdInputValidLabel;
    @FXML
    private Label adminPasswordInputValidLabel;
    @FXML
    private Label adminConfPasswordInputValidLabel;
    @FXML
    private Label adminEmailIdInputValidLabel;
    @FXML
    private Label adminNameValidLabel;
    @FXML
    private Label adminLicenseKeyValidLabel;
    @FXML
    private Button adminRegisterBtn;
    @FXML
    private HBox closeButtonContainerHBox;
    @FXML
    private Button closeButton;
    @FXML
    private Button backButton;
    @FXML
    private HBox topButtonBar;
    @FXML
    private HBox backButtonContainerHBox;

    private FormValidation adminFormValidation;

    private final FxWeaver fxWeaver;


    public RegisterAdminController(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        adminRegisterBtn.setDisable(true);

        adminFormValidation = new FormValidation();
        adminFormValidation.getFormFields().put("admin-id",false);
        adminFormValidation.getFormFields().put("password",false);
        adminFormValidation.getFormFields().put("conf-password",false);
        adminFormValidation.getFormFields().put("email-id",false);
        adminFormValidation.getFormFields().put("name",false);
        adminFormValidation.getFormFields().put("license-key",false);

        this.addEventListeners();
    }

    private void addEventListeners() {

        adminIdInputField.textProperty().addListener((observableValue, oldVal, newVal) -> {
            boolean valid = FormValidation.isUsername(
                    newVal,
                    adminIdInputValidLabel
            ).isValid();
            adminFormValidation.getFormFields().put("admin-id",valid);
            this.validateForm();
        });

        adminNameInputField.textProperty().addListener((observableValue, oldVal, newVal) -> {
            boolean valid = FormValidation.isName(
                    newVal,
                    adminNameValidLabel
            ).isValid();
            adminFormValidation.getFormFields().put("name", valid);
            this.validateForm();
        });

        adminPasswordInputField.textProperty().addListener((observableValue, oldVal, newVal) -> {
            boolean valid = FormValidation.isPassword(
                    newVal,
                    adminPasswordInputValidLabel
            ).isValid();
            adminFormValidation.getFormFields().put("password",valid);
            this.validateForm();
        });

        adminConfPasswordInputField.textProperty().addListener((observableValue, oldVal, newVal) -> {
            boolean valid = FormValidation.isConfPassword(
                    adminPasswordInputField.getText(),
                    newVal,
                    adminConfPasswordInputValidLabel
            ).isValid();
            adminFormValidation.getFormFields().put("conf-password",valid);
            this.validateForm();
        });

        adminEmailIdInputField.textProperty().addListener((observableValue, oldVal, newVal) -> {
            boolean valid = FormValidation.isEmailId(
                    newVal,
                    adminEmailIdInputValidLabel
            ).isValid();
            adminFormValidation.getFormFields().put("email-id",valid);
            this.validateForm();
        });

        adminLicenseKeyInputField.textProperty().addListener((observableValue, oldVal, newVal) -> {
            boolean valid = FormValidation.isLicenseKey(
                    newVal,
                    adminLicenseKeyValidLabel
            ).isValid();
            adminFormValidation.getFormFields().put("license-key",valid);
            this.validateForm();
        });

        // Submit form if Enter key is pressed
        adminRegisterFormGP.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER && this.validateForm()){
                this.loadLoginView();
            }
        });

        backButton.setOnAction((e) -> {
            this.loadDashboard();
        });
    }

    private boolean validateForm(){
        if(adminFormValidation.getFormFields().containsValue(false)){
            adminRegisterBtn.setDisable(true);
            return false;
        }
        else{
            adminRegisterBtn.setDisable(false);
            return true;
        }
    }

    private void resizeValidationLabels(){
        adminIdInputValidLabel.setStyle("-fx-font-size: 9px;");
        adminPasswordInputValidLabel.setStyle(" -fx-font-size: 9px;");
        adminConfPasswordInputValidLabel.setStyle(" -fx-font-size: 9px;");
        adminEmailIdInputValidLabel.setStyle(" -fx-font-size: 9px;");
    }

//    private void maximizeValidationLabels(){
//        double width = Util.getScreenWidth() * 0.5;
//        stage.setWidth(Util.getScreenWidth() * 0.7);
//        adminIdInputValidLabel.setPrefWidth(width);
//        adminPasswordInputValidLabel.setPrefWidth(width);
//        adminConfPasswordInputValidLabel.setPrefWidth(width);
//        adminEmailIdInputValidLabel.setPrefWidth(width);
//    }

    public void setLayout(Dimension2D stageDim){
        // Layout Settings

        adminRegisterContainerVBox.setPrefWidth(stageDim.getWidth());
        adminRegisterContainerVBox.setPrefHeight(stageDim.getHeight());


        adminRegisterFormGP.setPrefWidth(adminRegisterContainerVBox.getPrefWidth() * 0.8);
        adminRegisterFormGP.setPrefHeight(adminRegisterContainerVBox.getPrefHeight() * 0.8);

        adminRegisterFormGP.setHgap(adminRegisterContainerVBox.getPrefWidth() * 0.02);
        adminRegisterFormGP.setVgap(adminRegisterContainerVBox.getPrefHeight() * 0.06);

        List<ColumnConstraints> colConstList = adminRegisterFormGP.getColumnConstraints();
        colConstList.get(0).setPercentWidth(30);
        colConstList.get(1).setPercentWidth(30);
        colConstList.get(2).setPercentWidth(40);

        this.resizeValidationLabels();

        List<RowConstraints> rowConstraints = adminRegisterFormGP.getRowConstraints();
        rowConstraints.get(0).setPercentHeight(12.5);
        rowConstraints.get(1).setPercentHeight(12.5);
        rowConstraints.get(2).setPercentHeight(12.5);
        rowConstraints.get(3).setPercentHeight(12.5);
        rowConstraints.get(4).setPercentHeight(12.5);
        rowConstraints.get(5).setPercentHeight(12.5);
        rowConstraints.get(6).setPercentHeight(12.5);
        rowConstraints.get(7).setPercentHeight(12.5);

        topButtonBar.setPrefWidth(adminRegisterContainerVBox.getPrefWidth());
        closeButtonContainerHBox.setPrefWidth(topButtonBar.getPrefWidth() * 0.50);
        backButtonContainerHBox.setPrefWidth(topButtonBar.getPrefWidth() * 0.50);

        IconFontFX.register(GoogleMaterialDesignIcons.getIconFont());

        IconNode closeIcon = new IconNode(GoogleMaterialDesignIcons.CLOSE);
        closeIcon.setIconSize(24);
        closeIcon.setFill(Color.valueOf("#272635"));

        IconNode backIcon = new IconNode(GoogleMaterialDesignIcons.ARROW_BACK);
        backIcon.setIconSize(24);
        backIcon.setFill(Color.valueOf("#272635"));

        closeButton.setGraphic(closeIcon);
        backButton.setGraphic(backIcon);


    }

    private boolean registerAdmin(){
        String userId = adminIdInputField.getText();
        String password = adminPasswordInputField.getText();
        String confPassword = adminConfPasswordInputField.getText();
        String emailId = adminEmailIdInputField.getText();

        if(!this.validateForm()){
            return false;
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = bCryptPasswordEncoder.encode(password);
        User newUser = new User(userId,encodedPassword, Role.ADMIN);

        if(newUser != null) {
            // POST request to register employee
                // User registration
                if (User.save(newUser)) {
                    // Admin registration
                    Admin newAdmin = new Admin(newUser,emailId);
                    if(Admin.save(newAdmin)){
                        return true;
                    }
                }
                else{
                    return false;
                }
        }
        return true;
    }

    @FXML
    private void handleRegisterAdmin(ActionEvent e){
        this.loadLoginView();
    }

    private void loadDashboard(){
        System.out.println("Loading Dashboard ....");
        stage.setScene(new Scene(fxWeaver.loadView(ContentController.class), Util.getScreenWidth(), Util.getScreenHeight()));
        stage.setX(0);
        stage.setY(0);
        stage.setMaximized(true);
        stage.show();
    }

    private void loadLoginView(){
        if(this.registerAdmin()){
            if(StageInitializer.authentication.isAuthenticated()){
                this.loadDashboard();
//                System.out.println("Loading Dashboard ....");
//                stage.setScene(new Scene(fxWeaver.loadView(ContentController.class), Util.getScreenWidth(), Util.getScreenHeight()));
//                stage.setX(0);
//                stage.setY(0);
//                stage.setMaximized(true);
//                stage.show();
            }
            else {
                Dimension2D dimension2D = Util.getCenterSceneDim(stage,2.5,2.5);
                this.stage.setScene(new Scene(fxWeaver.loadView(AuthController.class), dimension2D.getWidth(),dimension2D.getHeight()));
                this.stage.show();
            }
        }
        else{
            // Admin creation failed
            String errMessage = "Unable to register !";
            System.out.println(errMessage);
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error",errMessage,"Something went wrong !", Alert.AlertType.ERROR);
            alertDialog.showInformationDialog();
        }
    }

    @Override
    public void onApplicationEvent(StageReadyEvent stageReadyEvent) {
        this.stage = stageReadyEvent.getStage();
    }

    public void handleClose(ActionEvent actionEvent) {
        stage.close();
    }
}
