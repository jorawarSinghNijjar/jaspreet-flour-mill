package com.jaspreetFlourMill.accountManagement;

import com.jaspreetFlourMill.accountManagement.controllers.AuthController;
import com.jaspreetFlourMill.accountManagement.controllers.ContentController;
import com.jaspreetFlourMill.accountManagement.controllers.RegisterAdminController;
import com.jaspreetFlourMill.accountManagement.model.Admin;
import com.jaspreetFlourMill.accountManagement.util.AlertDialog;
import com.jaspreetFlourMill.accountManagement.util.Authentication;
import com.jaspreetFlourMill.accountManagement.util.Util;
import javafx.geometry.Dimension2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StageInitializer implements ApplicationListener<StageReadyEvent> {

    private final FxWeaver fxWeaver;

    private Stage stage;

    public static Authentication authentication;

    private FxControllerAndView<RegisterAdminController, Node> registerAdminCV;

    public StageInitializer(FxWeaver fxWeaver){
        this.fxWeaver = fxWeaver;
        this.authentication = new Authentication();
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event){

        stage = event.getStage();
        stage.initStyle(StageStyle.UNDECORATED);

//        try {
            if(Admin.isRegistered()){
                Dimension2D dimension2D = Util.getCenterSceneDim(stage,3.5,2.5);
                stage.setScene(new Scene(fxWeaver.loadView(AuthController.class),dimension2D.getWidth(),dimension2D.getHeight()));
            }
            else {
                System.out.println("Redirecting to registration page....");
                Dimension2D dimension2D = Util.getCenterSceneDim(stage,2.5,2.5);
                registerAdminCV = fxWeaver.load(RegisterAdminController.class);
                registerAdminCV.getController().setLayout(dimension2D);
                registerAdminCV.getView().ifPresent(view -> {
                    stage.setScene(new Scene((Parent)(view),dimension2D.getWidth(),dimension2D.getHeight()));
                });
            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            // Information dialog
//            AlertDialog alertDialog = new AlertDialog("Error",e.getCause().getMessage(),e.getMessage(),Alert.AlertType.ERROR);
//            alertDialog.showErrorDialog(e);
//        }

        stage.show();

    }

}
