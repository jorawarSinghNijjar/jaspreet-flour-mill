package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.StageReadyEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@FxmlView("/views/loginForm.fxml")
public class AuthController implements ApplicationListener<StageReadyEvent> {

    private  final FxWeaver fxWeaver;
    private Stage stage;
    public AuthController(FxWeaver fxWeaver){
        this.fxWeaver = fxWeaver;
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        stage.setScene(new Scene(fxWeaver.loadView(DashboardController.class),1300,700));
        stage.show();
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        stage = event.getStage();
    }


}
