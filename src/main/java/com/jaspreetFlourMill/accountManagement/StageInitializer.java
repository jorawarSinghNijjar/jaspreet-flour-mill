package com.jaspreetFlourMill.accountManagement;

import com.jaspreetFlourMill.accountManagement.controllers.AuthController;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StageInitializer implements ApplicationListener<StageReadyEvent> {

    private final FxWeaver fxWeaver;

    private Stage stage;

    public StageInitializer(FxWeaver fxWeaver){
        this.fxWeaver = fxWeaver;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event){

        stage = event.getStage();
//        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(new Scene(fxWeaver.loadView(AuthController.class),500,400));
        stage.show();

    }

}
