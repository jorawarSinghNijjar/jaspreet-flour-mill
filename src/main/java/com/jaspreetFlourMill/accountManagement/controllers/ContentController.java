package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.StageReadyEvent;
import com.jaspreetFlourMill.accountManagement.util.NavigationHandler;
import com.jaspreetFlourMill.accountManagement.util.UserSession;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Callback;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons;
import jiconfont.javafx.IconFontFX;
import jiconfont.javafx.IconNode;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Stack;


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

    private FxControllerAndView<CustomerListController,Node> customerListCV;

    private FxControllerAndView<AddTransactionController,Node> addTransactionCV;

    private FxControllerAndView<CustomerDetailsController,Node> customerDetailsCV;

    private FxControllerAndView<RegisterCustomerController,Node> registerCustomerControllerCV;

    private FxControllerAndView<TransactionDetailController, Node> transactionDetailsCV;

    private FxControllerAndView<DepositWheatController, Node> depositWheatCV;

    private FxControllerAndView<RegisterEmployeeController, Node> registerEmployeeCV;

    private FxControllerAndView<HomeController, Node> homeCV;


    public static NavigationHandler navigationHandler;

    @FXML
    private Button homeButton;

    @FXML
    private Button addTransactionButton;

    @FXML
    private Button customersButton;

    @FXML
    private Button wheatDepositButton;

    @FXML
    private Button registerCustomerButton;

    @FXML
    private Button registerEmployeeButton;

    @FXML
    private Button signOutButton;

    @FXML
    private VBox sideMenuBox;

    @FXML
    private AnchorPane baseContainer;

    private boolean modalMounted = false;



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

        //Hide not admin content
        if(!AuthController.currentSession.getUserType().equals(UserSession.UserType.ADMIN)){
            sideMenuBox.getChildren().remove(registerEmployeeButton);
        }

        showHome();

        navigationHandler = new NavigationHandler() {
            @Override
            public void handleShowHome() {
                showHome();
            }
        };

        // Icons
        IconFontFX.register(GoogleMaterialDesignIcons.getIconFont());
        IconFontFX.register(FontAwesome.getIconFont());

        IconNode homeIcon = new IconNode(GoogleMaterialDesignIcons.HOME);
        homeIcon.setIconSize(18);
        homeIcon.setFill(Color.WHITE);

        IconNode groupIcon = new IconNode(GoogleMaterialDesignIcons.GROUP);
        groupIcon.setIconSize(18);
        groupIcon.setFill(Color.WHITE);

        IconNode assignmentIcon = new IconNode(GoogleMaterialDesignIcons.ASSIGNMENT);
        assignmentIcon.setIconSize(18);
        assignmentIcon.setFill(Color.WHITE);

        IconNode plusSquare = new IconNode(FontAwesome.PLUS_SQUARE);
        plusSquare.setIconSize(18);
        plusSquare.setFill(Color.WHITE);

        IconNode customerCard = new IconNode(FontAwesome.ADDRESS_CARD_O);
        customerCard.setIconSize(18);
        customerCard.setFill(Color.WHITE);

        IconNode employeeAdd = new IconNode(FontAwesome.USER_PLUS);
        employeeAdd.setIconSize(18);
        employeeAdd.setFill(Color.WHITE);

        IconNode unlock = new IconNode(FontAwesome.UNLOCK);
        unlock.setIconSize(18);
        unlock.setFill(Color.WHITE);

        homeButton.setGraphic(homeIcon);
        registerCustomerButton.setGraphic(customerCard);
        registerEmployeeButton.setGraphic(employeeAdd);
        signOutButton.setGraphic(unlock);
        addTransactionButton.setGraphic(assignmentIcon);
        wheatDepositButton.setGraphic(plusSquare);
        customersButton.setGraphic(groupIcon);

    }

    @FXML
    public void showHome(){
        contentContainer.getChildren().clear();
        homeCV = fxWeaver.load(HomeController.class);

        homeCV.getView().ifPresent(view -> {
            contentContainer.getChildren().add(view);
        });
    }

    @FXML
    public void showCustomers(){
        contentContainer.getChildren().clear();
        customerListCV = fxWeaver.load(CustomerListController.class);

        customerListCV.getView().ifPresent(view -> {
            contentContainer.getChildren().add(view);
        });
    }

    @FXML
    public void showAddTransaction() {
        try {
            contentContainer.getChildren().clear();
            System.out.println("Show add transaction");
            //Load add transaction view

            addTransactionCV = fxWeaver.load(AddTransactionController.class);
            customerDetailsCV = fxWeaver.load(CustomerDetailsController.class);
            transactionDetailsCV = fxWeaver.load(TransactionDetailController.class);

            AddTransactionController addTransactionController = addTransactionCV.getController();

            addTransactionController.customerIdInput.textProperty().addListener((
                    (observableValue, oldValue, newValue) -> {
                customerDetailsCV.getController().updateCustomerDetails(newValue);
                transactionDetailsCV.getController().clearTransactionDisplay();
                transactionDetailsCV.getController().renderTransactions(newValue);
            }
            ));


            customerDetailsCV.getController().customerIdProofImage.setOnMouseEntered(e -> {
                ImageView modalImage = new ImageView();
                try{
                    modalImage.setImage(new Image(
                            new FileInputStream(customerDetailsCV.getController().idProofImageUri)
                    ));
                    modalImage.setFitWidth(500);
                    modalImage.setFitHeight(350);
                    this.showImageModal(modalImage);
                }
                catch(Exception exception){
                    exception.getMessage();
                }

            });

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

//
//            //Load transaction details view
//            Node transactionDetailsNode = (Node) FXMLLoader.load(
//                    getClass().getResource("/views/transactionDetails.fxml")
//            );

            transactionDetailsContainer = new AnchorPane();
            transactionDetailsContainer.setPrefHeight(368);
            transactionDetailsContainer.setPrefWidth(1136);
            transactionDetailsContainer.setLayoutX(0);
            transactionDetailsContainer.setLayoutY(400);

            transactionDetailsCV.getView().ifPresent(view -> {
                transactionDetailsContainer.getChildren().add(view);

            });



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
            depositWheatCV = fxWeaver.load(DepositWheatController.class);
//            Node depositWheatNode = (Node) FXMLLoader.load(
//                    getClass().getResource("/views/depositWheat.fxml")
//            );

            depositWheatCV.getView().ifPresent(view -> {
                contentContainer.getChildren().add(view);
            });

        }
        catch(Exception e){
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
            registerEmployeeCV = fxWeaver.load(RegisterEmployeeController.class);
//            Node employeeNode = (Node) FXMLLoader.load(
//                    getClass().getResource("/views/registerEmployee.fxml")
//            );
            registerEmployeeCV.getView().ifPresent(view -> {
                contentContainer.getChildren().add(view);
            });


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

    public void showImageModal(Node node){
        if(modalMounted){
            return;
        }
        VBox modal = new VBox();
        modal.setMinWidth(500);
        modal.setMinHeight(400);
        modal.setMaxWidth(600);
        modal.setMaxHeight(400);
        modal.setLayoutX(400);
        modal.setLayoutY(200);
        modal.setStyle("-fx-background-color: rgb(222,242,241) ;" +
                " -fx-background-radius: 10;" +
                " -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");
        modal.setAlignment(Pos.TOP_CENTER);

        IconNode cancelIcon = new IconNode(FontAwesome.TIMES);
        cancelIcon.setIconSize(20);
        cancelIcon.setFill(Color.BLACK);
        Button closeButton = new Button();
        closeButton.getStyleClass().add("transparent-btn");
        closeButton.setPrefWidth(30);
        closeButton.setPrefHeight(30);
        closeButton.setAlignment(Pos.TOP_RIGHT);
        closeButton.setLayoutX(550);
        closeButton.setLayoutY(10);
        closeButton.setGraphic(cancelIcon);

        closeButton.setOnAction(e -> {
            baseContainer.getChildren().remove(modal);
            modalMounted = false;
        });

        HBox hBox = new HBox();
        hBox.getChildren().add(closeButton);
        hBox.setAlignment(Pos.TOP_RIGHT);
        hBox.setPrefWidth(600);
        modal.getChildren().add(hBox);
        modal.getChildren().add(node);
        baseContainer.getChildren().add(modal);
        modalMounted = true;
    }


}
