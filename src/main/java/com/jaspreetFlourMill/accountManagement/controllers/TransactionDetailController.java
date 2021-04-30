package com.jaspreetFlourMill.accountManagement.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
@FxmlView("/views/transactionDetails.fxml")
public class TransactionDetailController implements Initializable {

    @FXML
    private VBox detailItemContainer;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Node[] nodes = new Node[20];

        for(int i=0; i < nodes.length; i++){
            try {
                nodes[i] = (Node)FXMLLoader.load(getClass().getResource("/views/transactionDetailItem.fxml"));
                detailItemContainer.getChildren().add(nodes[i]);
            }
            catch (IOException e){
                e.getCause();
            }
        }
    }
}
