package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.StageReadyEvent;
import com.jaspreetFlourMill.accountManagement.util.NavigationHandler;
import com.jaspreetFlourMill.accountManagement.util.UserSession;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Callback;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


@Component
@FxmlView("/views/dashboard-2.fxml")
public class ContentController implements  Initializable, ApplicationListener<StageReadyEvent> {
    private final FxWeaver fxWeaver;

    private Stage stage;

    @FXML
    private AnchorPane contentContainer;

    private AnchorPane addTransactionContainer;

    private AnchorPane customerDetailsContainer;

    private AnchorPane transactionDetailsContainer;

    @FXML
    private Circle avatarFrame;

    private FxControllerAndView<AddTransactionController,Node> addTransactionCV;

    private FxControllerAndView<CustomerDetailsController,Node> customerDetailsCV;

    private FxControllerAndView<RegisterCustomerController,Node> registerCustomerControllerCV;

    public static NavigationHandler navigationHandler;

    public ContentController(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        stage = event.getStage();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Image avatar = new Image("/images/avatar.png");
        avatarFrame.setFill(new ImagePattern(avatar));

        showHome();

        navigationHandler = new NavigationHandler() {
            @Override
            public void handleShowHome() {
                showHome();
            }
        };

    }

    @FXML
    public void showHome(){
        try {
            contentContainer.getChildren().clear();
//            //Load Home View
//            Node homeNode = (Node) FXMLLoader.load(
//                    getClass().getResource("/views/home.fxml")
//            );
            //defining the axes
            final NumberAxis xAxis = new NumberAxis();
            final NumberAxis yAxis = new NumberAxis();
            xAxis.setLabel("Number of Month");
            yAxis.setLabel("Sales (in quintal)");
            //creating the chart
            final LineChart<Number,Number> lineChart =
                    new LineChart<>(xAxis,yAxis);

            lineChart.setTitle("Account Monitoring");
            //defining a series
            XYChart.Series series = new XYChart.Series();
            series.setName("Sales");
            //populating the series with data
            series.getData().add(new XYChart.Data(1, 23));
            series.getData().add(new XYChart.Data(2, 14));
            series.getData().add(new XYChart.Data(3, 15));
            series.getData().add(new XYChart.Data(4, 24));
            series.getData().add(new XYChart.Data(5, 34));
            series.getData().add(new XYChart.Data(6, 36));
            series.getData().add(new XYChart.Data(7, 22));
            series.getData().add(new XYChart.Data(8, 45));
            series.getData().add(new XYChart.Data(9, 43));
            series.getData().add(new XYChart.Data(10, 17));
            series.getData().add(new XYChart.Data(11, 29));
            series.getData().add(new XYChart.Data(12, 25));

            lineChart.getData().add(series);

            contentContainer.getChildren().add(lineChart);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showAddTransaction() {
        try {
            contentContainer.getChildren().clear();
            System.out.println("Show add transaction");
            //Load add transaction view

            addTransactionCV = fxWeaver.load(AddTransactionController.class);
            customerDetailsCV = fxWeaver.load(CustomerDetailsController.class);

            AddTransactionController addTransactionController = addTransactionCV.getController();

            addTransactionController.customerIdInput.textProperty().addListener((
                    (observableValue, oldValue, newValue) -> {
                customerDetailsCV.getController().updateCustomerDetails(newValue);
            }
            ));

            addTransactionContainer = new AnchorPane();

            addTransactionContainer.setPrefHeight(400);
            addTransactionContainer.setPrefWidth(568);
            addTransactionContainer.setLayoutX(0);
            addTransactionContainer.setLayoutY(0);
            addTransactionCV.getView().ifPresent(view ->{
                addTransactionContainer.getChildren().add(view);
            });

//            //Load customer details view

            customerDetailsContainer = new AnchorPane();

            customerDetailsContainer.setPrefHeight(400);
            customerDetailsContainer.setPrefWidth(568);
            customerDetailsContainer.setLayoutX(568);
            customerDetailsContainer.setLayoutY(0);

            customerDetailsCV.getView().ifPresent(view ->{
                customerDetailsContainer.getChildren().add(view);
            });


            //Load transaction details view
            Node transactionDetailsNode = (Node) FXMLLoader.load(
                    getClass().getResource("/views/transactionDetails.fxml")
            );

            transactionDetailsContainer = new AnchorPane();
            transactionDetailsContainer.setPrefHeight(368);
            transactionDetailsContainer.setPrefWidth(1136);
            transactionDetailsContainer.setLayoutX(0);
            transactionDetailsContainer.setLayoutY(400);

            transactionDetailsContainer.getChildren().add(transactionDetailsNode);


            // Load all nodes on contentContainer
            contentContainer.getChildren().addAll(
                    addTransactionContainer,
                    customerDetailsContainer,
                    transactionDetailsContainer
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showDepositWheat(){
        try{
            contentContainer.getChildren().clear();
            //Load Deposit Wheat View
            Node depositWheatNode = (Node) FXMLLoader.load(
                    getClass().getResource("/views/depositWheat.fxml")
            );
            contentContainer.getChildren().add(depositWheatNode);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }


    @FXML
    public void showRegisterCustomer() {
        try {
            contentContainer.getChildren().clear();
            //Load Register Customer View
//            Node registerCustomerNode = (Node) FXMLLoader.load(
//              getClass().getResource("/views/registerCustomer.fxml")
//            );
            registerCustomerControllerCV = fxWeaver.load(RegisterCustomerController.class);
            registerCustomerControllerCV.getView().ifPresent(view ->{
                contentContainer.getChildren().add(view);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @FXML
    public void showRegisterEmployee(){
        try {
            contentContainer.getChildren().clear();
            //Load Register Employee View
            Node employeeNode = (Node) FXMLLoader.load(
                    getClass().getResource("/views/registerEmployee.fxml")
            );
            contentContainer.getChildren().add(employeeNode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void signOut(){
        try {
            contentContainer.getChildren().clear();
            AuthController.currentSession.cleanSession();
            stage.setScene(new Scene(fxWeaver.loadView(AuthController.class),500,400));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
