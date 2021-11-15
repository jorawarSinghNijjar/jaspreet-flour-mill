package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.StageInitializer;
import com.jaspreetFlourMill.accountManagement.StageReadyEvent;
import com.jaspreetFlourMill.accountManagement.model.User;
import com.jaspreetFlourMill.accountManagement.util.AlertDialog;
import com.jaspreetFlourMill.accountManagement.util.Util;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Dimension2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Component
@FxmlView("/views/forgotPassword.fxml")
public class ForgotPasswordController implements Initializable, ApplicationListener<StageReadyEvent> {
    private Stage stage;
    private final FxWeaver fxWeaver;

    @FXML
    private TextField forgotEmailIdField;
    @FXML
    private GridPane forgotPasswordGP;

    public ForgotPasswordController(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Grid Pane styling
        this.forgotPasswordGP.setAlignment(Pos.CENTER);
        double width = Util.getScreenWidth() / 3.5;
        double height = Util.getScreenHeight() / 2.5;
        this.forgotPasswordGP.setPrefWidth(width * 0.8);
        this.forgotPasswordGP.setPrefHeight(height * 0.5);
        this.forgotPasswordGP.setVgap(height * 0.08);
        this.forgotPasswordGP.setHgap(width * 0.04);

        List<ColumnConstraints> colConstList = this.forgotPasswordGP.getColumnConstraints();
        colConstList.get(0).setPercentWidth(40);
        colConstList.get(1).setPercentWidth(60);
    }

    @FXML
    public void handleResetPassword(ActionEvent actionEvent){
        String emailId = forgotEmailIdField.getText();
        User.resetPassword(emailId).ifPresentOrElse((msg) -> {
            System.out.println(msg);
            Dimension2D dimension2D = Util.getCenterSceneDim(stage,3.5,2.5);
            stage.setScene(new Scene(fxWeaver.loadView(ResetTokenController.class),dimension2D.getWidth(),dimension2D.getHeight()));
            stage.show();
        },() -> {
            // Info dialog
            AlertDialog alertDialog = new AlertDialog("Error","Invalid Email","Please enter a valid email", Alert.AlertType.INFORMATION);
            alertDialog.showInformationDialog();
        });
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        stage = event.getStage();
    }
}
