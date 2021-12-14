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
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons;
import jiconfont.javafx.IconFontFX;
import jiconfont.javafx.IconNode;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Component
@FxmlView("/views/resetToken.fxml")
public class ResetTokenController implements Initializable, ApplicationListener<StageReadyEvent> {
    private Stage stage;
    private final FxWeaver fxWeaver;

    @FXML
    private TextField resetTokenInputField;
    @FXML
    private GridPane resetTokenGP;
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

    public ResetTokenController(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Grid Pane styling
        this.resetTokenGP.setAlignment(Pos.CENTER);
        double width = Util.getScreenWidth() / 3.5;
        double height = Util.getScreenHeight() / 2.5;
        this.resetTokenGP.setPrefWidth(width * 0.8);
        this.resetTokenGP.setPrefHeight(height * 0.5);
        this.resetTokenGP.setVgap(height * 0.08);
        this.resetTokenGP.setHgap(width * 0.04);

        List<ColumnConstraints> colConstList = this.resetTokenGP.getColumnConstraints();
        colConstList.get(0).setPercentWidth(40);
        colConstList.get(1).setPercentWidth(60);

        topButtonBar.setPrefWidth(width);
        closeButtonContainerHBox.setPrefWidth(topButtonBar.getPrefWidth() * 0.50);
        backButtonContainerHBox.setPrefWidth(topButtonBar.getPrefWidth() * 0.50);

        IconFontFX.register(GoogleMaterialDesignIcons.getIconFont());

        IconNode closeIcon = new IconNode(GoogleMaterialDesignIcons.CLOSE);
        closeIcon.setIconSize(24);
        closeIcon.setFill(Color.valueOf("#272635"));

        closeButton.setGraphic(closeIcon);

        IconNode backIcon = new IconNode(GoogleMaterialDesignIcons.ARROW_BACK);
        backIcon.setIconSize(24);
        backIcon.setFill(Color.valueOf("#272635"));

        backButton.setGraphic(backIcon);

        backButton.setOnAction(backBtnEvent -> {
            Dimension2D dimension2D = Util.getCenterSceneDim(this.stage, 3.5, 2.5);
            Scene root = new Scene(fxWeaver.loadView(ForgotPasswordController.class), dimension2D.getWidth(), dimension2D.getHeight());
            root.setFill(Color.TRANSPARENT);
            this.stage.setScene(root);
            this.stage.show();
        });

        // Submit form if Enter key is pressed
        resetTokenGP.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER){
                this.handleResetTokenSubmit();
            }
        });

    }

    @FXML
    public void handleResetTokenSubmit(){
        String resetTokenInput = resetTokenInputField.getText();

        User.authorizeToken(resetTokenInput).ifPresentOrElse(user -> {

            StageInitializer.authentication.setUser(user);

            Dimension2D dimension2D = Util.getCenterSceneDim(stage,2.5,2.5);
            stage.setScene(new Scene(fxWeaver.loadView(NewPasswordController.class),dimension2D.getWidth(),dimension2D.getHeight()));
            stage.show();

        },() -> {
            // Info dialog
            AlertDialog alertDialog = new AlertDialog("Info","Invalid Token","Please enter a valid token sent on your email", Alert.AlertType.INFORMATION);
            alertDialog.showInformationDialog();
        });
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        stage = event.getStage();
    }

    public void handleClose(ActionEvent actionEvent) {
        stage.close();
    }
}
