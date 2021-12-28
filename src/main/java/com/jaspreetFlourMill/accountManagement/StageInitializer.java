package com.jaspreetFlourMill.accountManagement;

import com.jaspreetFlourMill.accountManagement.controllers.AuthController;
import com.jaspreetFlourMill.accountManagement.controllers.ContentController;
import com.jaspreetFlourMill.accountManagement.controllers.LoadingPageController;
import com.jaspreetFlourMill.accountManagement.controllers.RegisterAdminController;
import com.jaspreetFlourMill.accountManagement.model.Admin;
import com.jaspreetFlourMill.accountManagement.model.Connection;
import com.jaspreetFlourMill.accountManagement.util.AlertDialog;
import com.jaspreetFlourMill.accountManagement.util.Authentication;
import com.jaspreetFlourMill.accountManagement.util.Util;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

@Component
public class StageInitializer implements ApplicationListener<StageReadyEvent> {
    private int retryCount;

    private final FxWeaver fxWeaver;

    private Stage stage;

    public static Authentication authentication;

    private FxControllerAndView<RegisterAdminController, Node> registerAdminCV;

    private FxControllerAndView<LoadingPageController, Node> loadingPageCV;

    public StageInitializer(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
        this.authentication = new Authentication();
        this.retryCount = 0;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {

        stage = event.getStage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initStyle(StageStyle.TRANSPARENT);

        Dimension2D dimension2DLoading = Util.getCenterSceneDim(stage, 2, 1.5);
        loadingPageCV = fxWeaver.load(LoadingPageController.class);
        loadingPageCV.getView().ifPresent(view -> {
            Scene loadScene = new Scene(fxWeaver.loadView(LoadingPageController.class), dimension2DLoading.getWidth(), dimension2DLoading.getHeight());
            loadScene.setFill(Color.TRANSPARENT);
            stage.setScene(loadScene);

            Task<Void> sleeper = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                    return null;
                }
            };
            sleeper.setOnSucceeded(event1 -> {
                proceedToAuth();
            });
            new Thread(sleeper).start();

        });
        stage.show();
    }

    public void proceedToAuth() {
        Connection connection = this.getConnection();

        if (connection.isConnected()) {
            if (Admin.isRegistered()) {
                Dimension2D dimension2D = Util.getCenterSceneDim(this.stage, 2, 1.5);
                Scene root = new Scene(fxWeaver.loadView(AuthController.class), dimension2D.getWidth(), dimension2D.getHeight());
                root.setFill(Color.TRANSPARENT);
                this.stage.setScene(root);
                this.stage.show();
            } else {
                System.out.println("Redirecting to registration page....");
                Dimension2D dimension2D = Util.getCenterSceneDim(this.stage, 1, 1);
                registerAdminCV = fxWeaver.load(RegisterAdminController.class);
                registerAdminCV.getController().setLayout(dimension2D);
                registerAdminCV.getView().ifPresent(view -> {
                    this.stage.setScene(new Scene((Parent) (view), dimension2D.getWidth(), dimension2D.getHeight()));
                    this.stage.show();
                });
            }
        }
    }

    public Connection getConnection() {
        Connection connection = new Connection();
        connection.connect();

        if (connection.isConnected()) {
            return connection;
        } else {
            if (retryCount > 60) {
                // Information dialog
                AlertDialog alertDialog = new AlertDialog("Error", "Connection Timed Out: Error Connecting ...", "Try relaunching ther application! Exiting....", Alert.AlertType.ERROR);
                alertDialog.showInformationDialog();
                stage.close();
                return null;
            } else {
                try {
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println("Retrying to connect.....");
                    System.out.println("Retry Count: " + retryCount);
                    retryCount++;
                    return getConnection();
                } catch (NullPointerException e) {
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                return null;
            }
        }
    }

}
