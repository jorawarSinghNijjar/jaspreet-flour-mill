package com.jaspreetFlourMill.accountManagement;

import com.jaspreetFlourMill.accountManagement.controllers.AuthController;
import com.jaspreetFlourMill.accountManagement.util.Util;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
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
        stage.initStyle(StageStyle.UNDECORATED);
        double width = Util.getScreenWidth() / 3.5;
        double height = Util.getScreenHeight() / 2.5;
        stage.setX((Util.getScreenWidth() - width) / 2);
        stage.setY((Util.getScreenHeight() - height) / 2);
        stage.setScene(new Scene(fxWeaver.loadView(AuthController.class),width,height));
        stage.show();

    }

}
