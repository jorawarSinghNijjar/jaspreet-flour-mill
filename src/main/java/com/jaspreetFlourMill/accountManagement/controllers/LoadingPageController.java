package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.StageReadyEvent;
import com.jaspreetFlourMill.accountManagement.model.Admin;
import com.jaspreetFlourMill.accountManagement.model.Connection;
import com.jaspreetFlourMill.accountManagement.util.AlertDialog;
import com.jaspreetFlourMill.accountManagement.util.Util;
import javafx.application.Preloader;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Dimension2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

@Component
@FxmlView("/views/loadingPage.fxml")
public class LoadingPageController  implements Initializable, ApplicationListener<StageReadyEvent> {
    private int retryCount;

    private Stage stage;

    private final FxWeaver fxWeaver;
    private FxControllerAndView<RegisterAdminController, Node> registerAdminCV;

    public LoadingPageController(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
        this.retryCount = 0;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

//    public void proceedToAuth(){
//        Connection connection = this.getConnection();
//
//        if(connection.isConnected()){
//            if(Admin.isRegistered()){
//                Dimension2D dimension2D = Util.getCenterSceneDim(this.stage,2,1.5);
//                Scene root = new Scene(fxWeaver.loadView(AuthController.class),dimension2D.getWidth(),dimension2D.getHeight());
//                root.setFill(Color.TRANSPARENT);
//                this.stage.setScene(root);
//            }
//            else {
//                System.out.println("Redirecting to registration page....");
//                Dimension2D dimension2D = Util.getCenterSceneDim(this.stage,1,1);
//                registerAdminCV = fxWeaver.load(RegisterAdminController.class);
//                registerAdminCV.getController().setLayout(dimension2D);
//                registerAdminCV.getView().ifPresent(view -> {
//                    this.stage.setScene(new Scene((Parent)(view),dimension2D.getWidth(),dimension2D.getHeight()));
//                });
//            }
//            this.stage.show();
//        }
//    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        this.stage = event.getStage();
    }

    public Connection getConnection(){
        Connection connection = new Connection();
        connection.connect();

        if(connection.isConnected()){
            return connection;
        }
        else {
            if(retryCount > 60){
                // Information dialog
                AlertDialog alertDialog = new AlertDialog("Error","Connection Timed Out: Error Connecting ...","Try relaunching ther application! Exiting....", Alert.AlertType.ERROR);
                alertDialog.showInformationDialog();
                stage.close();
                return null;
            }
            else{
                try {
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println("Retrying to connect.....");
                    System.out.println("Retry Count: " + retryCount);
                    retryCount++;
                    return getConnection();
                }
                catch (NullPointerException e){
                    System.out.println(e.getMessage());
                }
                catch (Exception e){
                    System.out.println(e.getMessage());
                }
                return null;
            }
        }
    }
}
