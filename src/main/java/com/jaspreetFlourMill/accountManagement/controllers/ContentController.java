package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.StageReadyEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;


@Component
@FxmlView("/views/dashboard-2.fxml")
public class ContentController implements ApplicationListener<StageReadyEvent>, Initializable {
    private  final FxWeaver fxWeaver;
    private Stage stage;

    @FXML
    private AnchorPane addTransactionContainer;

    @FXML
    private AnchorPane customerDetailsContainer;

    @FXML
    private AnchorPane transactionDetailsContainer;

    @FXML
    private Circle avatarFrame;


    public ContentController(FxWeaver fxWeaver){
        this.fxWeaver = fxWeaver;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        stage = event.getStage();
    }

    @FXML
    public void showAddTransaction() {
        try {
            //Load add transaction view
            Node addTransactionNode = (Node) FXMLLoader.load(getClass().getResource("/views/addTransaction.fxml"));
            addTransactionContainer.getChildren().add(addTransactionNode);

            //Load customer details view
            Node customerDetailsNode = (Node) FXMLLoader.load(getClass().getResource("/views/customerDetails.fxml"));
            customerDetailsContainer.getChildren().add(customerDetailsNode);

            //Load transaction details view
            Node transactionDetailsNode = (Node) FXMLLoader.load(getClass().getResource("/views/transactionDetails.fxml"));
            transactionDetailsContainer.getChildren().add(transactionDetailsNode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showRegisterCustomer() {
        try {
            //Load add transaction view
            Node showRegisterCustomerNode = (Node) FXMLLoader.load(getClass().getResource("/views/registerCustomer.fxml"));
            addTransactionContainer.getChildren().add(showRegisterCustomerNode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Image avatar = new Image("/images/avatar.png");
        avatarFrame.setFill(new ImagePattern(avatar));
    }
}
