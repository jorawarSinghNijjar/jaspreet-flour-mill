package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.StageInitializer;
import com.jaspreetFlourMill.accountManagement.StageReadyEvent;
import com.jaspreetFlourMill.accountManagement.model.User;
import com.jaspreetFlourMill.accountManagement.util.AlertDialog;
import com.jaspreetFlourMill.accountManagement.util.Util;
import com.sun.javafx.menu.MenuItemBase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Dimension2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PopupControl;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
@FxmlView("/views/forgotPassword.fxml")
public class ForgotPasswordController implements Initializable, ApplicationListener<StageReadyEvent> {
    private Stage stage;
    private final FxWeaver fxWeaver;

    @FXML
    private VBox forgotPasswordContainerVBox;
    @FXML
    private TextField forgotEmailIdField;
    @FXML
    private GridPane forgotPasswordGP;

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

    public ForgotPasswordController(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Grid Pane styling
        this.forgotPasswordGP.setAlignment(Pos.CENTER);
        double width = Util.getScreenWidth() / 2;
        double height = Util.getScreenHeight() / 1.5;
        this.forgotPasswordGP.setPrefWidth(width * 0.8);
        this.forgotPasswordGP.setPrefHeight(height * 0.5);
        this.forgotPasswordGP.setVgap(height * 0.08);
        this.forgotPasswordGP.setHgap(width * 0.04);

        List<ColumnConstraints> colConstList = this.forgotPasswordGP.getColumnConstraints();
        colConstList.get(0).setPercentWidth(40);
        colConstList.get(1).setPercentWidth(60);

        topButtonBar.setPrefWidth(width);
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

        backButton.setOnAction(backBtnEvent -> {
            Dimension2D dimension2D = Util.getCenterSceneDim(this.stage, 2, 1.5);
            Scene root = new Scene(fxWeaver.loadView(AuthController.class), dimension2D.getWidth(), dimension2D.getHeight());
            root.setFill(Color.TRANSPARENT);
            this.stage.setScene(root);
            this.stage.show();
        });

        // Submit form if Enter key is pressed
        forgotPasswordGP.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                handleResetPassword();
            }
        });
    }

    @FXML
    public void handleResetPassword(){
        String emailId = forgotEmailIdField.getText();

        User.resetPassword(emailId).ifPresentOrElse((msg) -> {
            System.out.println(msg);
            Dimension2D dimension2D = Util.getCenterSceneDim(stage,2,1.5);
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

    public void handleClose(ActionEvent actionEvent) {
        stage.close();
    }
}
