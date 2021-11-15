package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.StageInitializer;
import com.jaspreetFlourMill.accountManagement.StageReadyEvent;
import com.jaspreetFlourMill.accountManagement.model.User;
import com.jaspreetFlourMill.accountManagement.util.FormValidation;
import com.jaspreetFlourMill.accountManagement.util.Util;
import com.sun.javafx.menu.MenuItemBase;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Dimension2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Component
@FxmlView("/views/newPassword.fxml")
public class NewPasswordController implements Initializable, ApplicationListener<StageReadyEvent> {

    private final FxWeaver fxWeaver;
    private FormValidation resetPasswordFormValidation;
    private Stage stage;

    @FXML
    private GridPane setNewPasswordGP;
    @FXML
    private TextField newPasswordInputField;
    @FXML
    private TextField newConfPasswordInputField;
    @FXML
    private Button newPasswordSubmitButton;
    @FXML
    private Label newPasswordValidLabel;
    @FXML
    private Label newConfPasswordValidLabel;

    public NewPasswordController(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Grid Pane styling
        this.setNewPasswordGP.setAlignment(Pos.CENTER);
        double width = Util.getScreenWidth() / 3;
        double height = Util.getScreenHeight() / 2.5;
        this.setNewPasswordGP.setPrefWidth(width * 0.8);
        this.setNewPasswordGP.setPrefHeight(height * 0.5);
        this.setNewPasswordGP.setVgap(height * 0.08);
        this.setNewPasswordGP.setHgap(width * 0.04);

        List<ColumnConstraints> colConstList = this.setNewPasswordGP.getColumnConstraints();
        colConstList.get(0).setPercentWidth(30);
        colConstList.get(1).setPercentWidth(30);
        colConstList.get(1).setPercentWidth(60);

        resetPasswordFormValidation = new FormValidation();
        resetPasswordFormValidation.getFormFields().put("password",false);
        resetPasswordFormValidation.getFormFields().put("conf-password",false);

        this.addEventListeners();
    }

    private void addEventListeners() {
        newPasswordInputField.textProperty().addListener((observableValue, oldVal, newVal) -> {
            boolean valid = FormValidation.isPassword(
                    newVal,
                    newPasswordValidLabel
            ).isValid();
            resetPasswordFormValidation.getFormFields().put("password",valid);
            this.validateForm();
        });

        newConfPasswordInputField.textProperty().addListener((observableValue, oldVal, newVal) -> {
            boolean valid = FormValidation.isConfPassword(
                    newPasswordInputField.getText(),
                    newVal,
                    newConfPasswordValidLabel
            ).isValid();
            resetPasswordFormValidation.getFormFields().put("conf-password",valid);
            this.validateForm();
        });

        // Submit form if Enter key is pressed
        stage.getScene().setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER && this.validateForm()){
                this.handleNewPasswordSubmit();
            }
        });

    }

    @FXML
    public boolean handleNewPasswordSubmit(){
        String password = newPasswordInputField.getText();

        if(!this.validateForm()){
            return false;
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = bCryptPasswordEncoder.encode(password);

        User user = StageInitializer.authentication.getUser();

        user.setPassword(encodedPassword);

        User.updateUser(user, user.getId()).ifPresent(updatedUser -> {
            Dimension2D dimension2D = Util.getCenterSceneDim(stage,3.5,2.5);
            stage.setScene(new Scene(fxWeaver.loadView(AuthController.class),dimension2D.getWidth(),dimension2D.getHeight()));
        });

        return true;
    }

    private boolean validateForm(){
        if(resetPasswordFormValidation.getFormFields().containsValue(false)){
            newPasswordSubmitButton.setDisable(true);
            return false;
        }
        else{
            newPasswordSubmitButton.setDisable(false);
            return true;
        }
    }
    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        stage = event.getStage();
    }
}
