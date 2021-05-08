package com.jaspreetFlourMill.accountManagement.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.net.URL;
import java.util.ResourceBundle;

@Component
@FxmlView("/views/modalImageView.fxml")
public class ModalImageViewController implements Initializable {
    @FXML
    private ImageView zoomedIDProofImage;

    @FXML
    private HBox zoomHBox;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        zoomHBox.setStyle("-fx-background-color: rgba(222,242,241,.7) ;" +
                " -fx-background-radius: 10;" +
                " -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");
    }

    public void setZoomedIDProofImage(String imageUri){
        try{
            zoomedIDProofImage.setImage(new Image(new FileInputStream(imageUri)));
        }catch (Exception e){
            e.getMessage();
        }

    }
}
