package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.StageReadyEvent;
import com.jaspreetFlourMill.accountManagement.util.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;


@Component
@FxmlView("/views/dashboard-2.fxml")
public class ContentController implements  Initializable, ApplicationListener<StageReadyEvent> {
    private final FxWeaver fxWeaver;
    private Stage stage;

    @FXML
    private AnchorPane contentContainer;

    private AnchorPane addTransactionContainer;

    private AnchorPane customerDetailsContainer;

    private AnchorPane transactionDetailsContainer;

    @FXML
    private Circle avatarFrame;


    public ContentController(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        stage = event.getStage();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Image avatar = new Image("/images/avatar.png");
        avatarFrame.setFill(new ImagePattern(avatar));
    }

    @FXML
    public void showAddTransaction() {
        try {
            System.out.println("Show add transaction");
            //Load add transaction view
            Node addTransactionNode = (Node) FXMLLoader.load(
                    getClass().getResource("/views/addTransaction.fxml")
            );
            addTransactionContainer = new AnchorPane();

            addTransactionContainer.setPrefHeight(400);
            addTransactionContainer.setPrefWidth(568);
            addTransactionContainer.setLayoutX(0);
            addTransactionContainer.setLayoutY(0);
            addTransactionContainer.getChildren().add(addTransactionNode);

            //Load customer details view
            Node customerDetailsNode = (Node) FXMLLoader.load(
                    getClass().getResource("/views/customerDetails.fxml")
            );
            customerDetailsContainer = new AnchorPane();

            customerDetailsContainer.setPrefHeight(400);
            customerDetailsContainer.setPrefWidth(568);
            customerDetailsContainer.setLayoutX(568);
            customerDetailsContainer.setLayoutY(0);
            customerDetailsContainer.getChildren().add(customerDetailsNode);

            //Load transaction details view
            Node transactionDetailsNode = (Node) FXMLLoader.load(
                    getClass().getResource("/views/transactionDetails.fxml")
            );
            transactionDetailsContainer = new AnchorPane();
            transactionDetailsContainer.setPrefHeight(368);
            transactionDetailsContainer.setPrefWidth(1136);
            transactionDetailsContainer.setLayoutX(0);
            transactionDetailsContainer.setLayoutY(400);
            transactionDetailsContainer.getChildren().add(transactionDetailsNode);


            // Load all nodes on contentContainer
            contentContainer.getChildren().addAll(
                    addTransactionContainer,
                    customerDetailsContainer,
                    transactionDetailsContainer
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showRegisterCustomer() {
        try {
            contentContainer.getChildren().clear();
            //Load Register Customer View
            Node registerCustomerNode = (Node) FXMLLoader.load(
              getClass().getResource("/views/registerCustomer.fxml")
            );
            contentContainer.getChildren().add(registerCustomerNode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showHome(){
        try {
            contentContainer.getChildren().clear();
            //Load Home View
            Node homeNode = (Node) FXMLLoader.load(
                    getClass().getResource("/views/home.fxml")
            );
            contentContainer.getChildren().add(homeNode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showRegisterEmployee(){
        try {
            contentContainer.getChildren().clear();
            //Load Register Employee View
            Node employeeNode = (Node) FXMLLoader.load(
                    getClass().getResource("/views/registerEmployee.fxml")
            );
            contentContainer.getChildren().add(employeeNode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void signOut(){
        try {
            contentContainer.getChildren().clear();
            AuthController.currentSession.cleanSession();
            stage.setScene(new Scene(fxWeaver.loadView(AuthController.class),500,400));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
