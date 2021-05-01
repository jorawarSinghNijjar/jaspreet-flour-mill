package com.jaspreetFlourMill.accountManagement.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@FxmlView("/views/addTransaction.fxml")
public class AddTransactionController implements Initializable {

    @FXML
    public TextField customerIdInput;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }



}
