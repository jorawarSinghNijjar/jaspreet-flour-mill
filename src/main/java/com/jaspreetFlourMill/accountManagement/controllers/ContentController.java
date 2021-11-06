package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.StageInitializer;
import com.jaspreetFlourMill.accountManagement.StageReadyEvent;
import com.jaspreetFlourMill.accountManagement.model.Role;
import com.jaspreetFlourMill.accountManagement.model.User;
import com.jaspreetFlourMill.accountManagement.util.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
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
import java.util.List;
import java.util.ResourceBundle;
import java.util.Stack;


@Component
@FxmlView("/views/dashboard-2.fxml")
public class ContentController implements Initializable, ApplicationListener<StageReadyEvent> {
    private final FxWeaver fxWeaver;

    private Stage stage;

    private Role currentUserRole;

    @FXML
    private AnchorPane contentContainer;

    private AnchorPane addTransactionContainer;

    private AnchorPane customerDetailsContainer;

    private AnchorPane transactionDetailsContainer;

    @FXML
    private Label contentAreaTitleLabel;

    @FXML
    private Circle avatarFrame;

    private FxControllerAndView<CustomerListController, Node> customerListCV;

    private FxControllerAndView<AddTransactionController, Node> addTransactionCV;

    private FxControllerAndView<CustomerDetailsController, Node> customerDetailsCV;

    private FxControllerAndView<RegisterCustomerController, Node> registerCustomerControllerCV;

    private FxControllerAndView<TransactionDetailController, Node> transactionDetailsCV;

    private FxControllerAndView<DepositWheatController, Node> depositWheatCV;

    private FxControllerAndView<RegisterEmployeeController, Node> registerEmployeeCV;

    private FxControllerAndView<HomeController, Node> homeCV;


    public static NavigationHandler navigationHandler;

    @FXML
    private Label usernameLabel;

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

    @FXML
    private HBox titleHBox;

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

        // Authorization Check
        if(StageInitializer.authentication.isAuthenticated()){
            usernameLabel.setText(StageInitializer.authentication.getUser().getId() + "\n" + StageInitializer.authentication.getUser().getRole());
            currentUserRole = StageInitializer.authentication.getUser().getRole();
        }

        // Laying out the dashboard

        // Side menu = 15 % screen
        // Dashboard = 85% screen
        baseContainer.setPrefWidth(Util.getScreenWidth());
        baseContainer.setPrefHeight(Util.getScreenHeight());

        sideMenuBox.setPrefHeight(baseContainer.getPrefHeight());
        sideMenuBox.setPrefWidth(baseContainer.getPrefWidth() * 0.15);

        contentContainer.setPrefWidth(baseContainer.getPrefWidth() * 0.85);
        contentContainer.setLayoutX(sideMenuBox.getPrefWidth());

        titleHBox.setPrefWidth(contentContainer.getPrefWidth());
        titleHBox.setPrefHeight(baseContainer.getPrefHeight() * 0.05);
        titleHBox.setLayoutX(sideMenuBox.getPrefWidth());

        contentContainer.setPrefHeight(baseContainer.getPrefHeight() - (baseContainer.getPrefHeight() * 0.05));
        contentContainer.setLayoutY(titleHBox.getPrefHeight());


        contentAreaTitleLabel.setPrefWidth(titleHBox.getPrefWidth());

        Image avatar = new Image("/images/avatar.png");
        avatarFrame.setFill(new ImagePattern(avatar));

//        Hide not admin content
        if (currentUserRole == Role.EMPLOYEE) {
            sideMenuBox.getChildren().remove(registerEmployeeButton);
            sideMenuBox.getChildren().remove(homeButton);
            showAddTransaction();
        } else if(currentUserRole == Role.ADMIN){
            sideMenuBox.getChildren().remove(addTransactionButton);
            sideMenuBox.getChildren().remove(wheatDepositButton);
            sideMenuBox.getChildren().remove(registerCustomerButton);
            showHome();
        }else {
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error","Unknown User Role","User role not set !",Alert.AlertType.INFORMATION);
            alertDialog.showInformationDialog();
        }



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
    public void showHome() {
        contentAreaTitleLabel.setText("Sales Summary");
        contentContainer.getChildren().clear();

        homeCV = fxWeaver.load(HomeController.class);

        homeCV.getView().ifPresent(view -> {
            // Layout
            homeCV.getController().homeContainer.setPrefWidth(contentContainer.getPrefWidth());
            homeCV.getController().homeContainer.setPrefHeight(contentContainer.getPrefHeight());

            homeCV.getController().homeVBoxContainer.setPrefWidth(homeCV.getController().homeContainer.getPrefWidth());
            homeCV.getController().homeVBoxContainer.setPrefHeight(homeCV.getController().homeContainer.getPrefHeight());
            homeCV.getController().homeVBoxContainer.setSpacing(homeCV.getController().homeContainer.getPrefHeight() * 0.08);

            double hBoxSpacing = homeCV.getController().homeHBoxContainer.getPrefWidth();
            homeCV.getController().homeHBoxContainer.setSpacing(hBoxSpacing * 0.08);
            homeCV.getController().homeHBoxContainer.setPrefHeight(homeCV.getController().homeVBoxContainer.getHeight() * 0.30);

            homeCV.getController().lineChartContainer.setPrefWidth(homeCV.getController().homeVBoxContainer.getPrefWidth() * 0.85);
            homeCV.getController().lineChartContainer.setPrefHeight(homeCV.getController().homeVBoxContainer.getPrefHeight() * 0.60);

            homeCV.getController().salesAmtChart.setPrefSize(
                    homeCV.getController().lineChartContainer.getPrefWidth(),
                    homeCV.getController().lineChartContainer.getPrefHeight() * 0.90
            );
            homeCV.getController().salesQtyChart.setPrefSize(
                    homeCV.getController().lineChartContainer.getPrefWidth(),
                    homeCV.getController().lineChartContainer.getPrefHeight() * 0.90
            );

            homeCV.getController().leftArrow.setPrefWidth(homeCV.getController().lineChartGridPane.getCellBounds(0, 0).getWidth());

            contentContainer.getChildren().add(view);
        });
    }

    @FXML
    public void showCustomers() {
        contentAreaTitleLabel.setText("Search Customers By Name");
        contentContainer.getChildren().clear();
        customerListCV = fxWeaver.load(CustomerListController.class);

        customerListCV.getView().ifPresent(view -> {
            // Layout
            customerListCV.getController().customerListContainerAP.setPrefWidth(contentContainer.getPrefWidth());
            customerListCV.getController().customerListContainerAP.setPrefHeight(contentContainer.getPrefHeight());

            customerListCV.getController().customerListContainerGP.setPrefWidth(contentContainer.getPrefWidth());
            customerListCV.getController().customerListContainerGP.setPrefHeight(contentContainer.getPrefHeight());
            customerListCV.getController().customerListContainerGP.setHgap(contentContainer.getPrefWidth() * 0.01);
            customerListCV.getController().customerListContainerGP.setVgap(contentContainer.getPrefHeight() * 0.01);

            List<ColumnConstraints> colConstList = customerListCV.getController().customerListContainerGP.getColumnConstraints();
            colConstList.get(0).setPercentWidth(30);
            colConstList.get(1).setPercentWidth(70);

            List<RowConstraints> rowConstConstList = customerListCV.getController().customerListContainerGP.getRowConstraints();
            rowConstConstList.get(0).setPercentHeight(10);
            rowConstConstList.get(1).setPercentHeight(90);


            contentContainer.getChildren().add(view);
        });
    }

    @FXML
    public void showAddTransaction() {
        try {
            contentAreaTitleLabel.setText("Transactions");
            contentContainer.getChildren().clear();
            System.out.println("Show add transaction");
            //Load add transaction view

            addTransactionCV = fxWeaver.load(AddTransactionController.class);
            customerDetailsCV = fxWeaver.load(CustomerDetailsController.class);
            transactionDetailsCV = fxWeaver.load(TransactionDetailController.class);

            AddTransactionController addTransactionController = addTransactionCV.getController();

            addTransactionController.customerIdInput.textProperty().addListener((
                    (observableValue, oldValue, newValue) -> {
                        if(!newValue.isEmpty()){
                            customerDetailsCV.getController().updateCustomerDetails(newValue);
                            transactionDetailsCV.getController().clearTransactionDisplay();
                            transactionDetailsCV.getController().renderTransactions(newValue);
                        }
                    }
            ));


            customerDetailsCV.getController().customerIdProofImage.setOnMouseEntered(e -> {
                ImageView modalImage = new ImageView();
                try {
                    modalImage.setImage(new Image(
                            new FileInputStream(customerDetailsCV.getController().idProofImageUri)
                    ));
                    modalImage.setFitWidth(500);
                    modalImage.setFitHeight(350);
                    this.showImageModal(modalImage);
                } catch (Exception exception) {
                    exception.getMessage();
                    // Information dialog
                    AlertDialog alertDialog = new AlertDialog("Error",exception.getCause().getMessage(),exception.getMessage(), Alert.AlertType.ERROR);
                    alertDialog.showErrorDialog(exception);
                }

            });

            addTransactionContainer = new AnchorPane();
            addTransactionContainer.setPrefHeight(contentContainer.getPrefHeight() * 0.5);
            addTransactionContainer.setPrefWidth(contentContainer.getPrefWidth() * 0.5);
            addTransactionContainer.setLayoutX(0);
            addTransactionContainer.setLayoutY(titleHBox.getPrefHeight());
            addTransactionCV.getView().ifPresent(view -> {

                addTransactionCV.getController().addTransactionVBoxContainer.setPrefWidth(addTransactionContainer.getPrefWidth());
                addTransactionCV.getController().addTransactionVBoxContainer.setPrefHeight(addTransactionContainer.getPrefHeight());

                addTransactionCV.getController().addTransactionFormGridPane.setPrefWidth(addTransactionContainer.getPrefWidth());
                addTransactionCV.getController().addTransactionFormGridPane.setHgap(addTransactionContainer.getPrefWidth() * 0.05);
                addTransactionCV.getController().addTransactionFormGridPane.setVgap(addTransactionContainer.getPrefHeight() * 0.07);

                List<ColumnConstraints> colConstList = addTransactionCV.getController().addTransactionFormGridPane.getColumnConstraints();
                colConstList.get(0).setPercentWidth(25);
                colConstList.get(1).setPercentWidth(25);
                colConstList.get(2).setPercentWidth(50);

                addTransactionCV.getController().submitTransactionBtn.setPrefWidth(Double.MAX_VALUE);

                addTransactionContainer.getChildren().add(view);
            });

//            //Load customer details view

            customerDetailsContainer = new AnchorPane();
            customerDetailsContainer.setPrefHeight(contentContainer.getPrefHeight() * 0.5);
            customerDetailsContainer.setPrefWidth(contentContainer.getPrefWidth() * 0.5);
            customerDetailsContainer.setLayoutX(addTransactionContainer.getPrefWidth());
            customerDetailsContainer.setLayoutY(titleHBox.getPrefHeight());

            customerDetailsCV.getView().ifPresent(view -> {
                // Layout

                customerDetailsCV.getController().customerDetailVBox.setPrefWidth(customerDetailsContainer.getPrefWidth());
                customerDetailsCV.getController().customerDetailVBox.setPrefHeight(customerDetailsContainer.getPrefHeight());

                customerDetailsCV.getController().customerDetailGridPane.setHgap(customerDetailsContainer.getPrefWidth() * 0.02);
                customerDetailsCV.getController().customerDetailGridPane.setVgap(customerDetailsContainer.getPrefHeight() * 0.02);

                List<ColumnConstraints> colConstList = customerDetailsCV.getController().customerDetailGridPane.getColumnConstraints();
                colConstList.get(0).setPercentWidth(30);
                colConstList.get(1).setPercentWidth(15);
                colConstList.get(2).setPercentWidth(25);
                colConstList.get(3).setPercentWidth(30);

                customerDetailsContainer.getChildren().add(view);
            });

            //Load transaction details view

            transactionDetailsContainer = new AnchorPane();
            transactionDetailsContainer.setPrefHeight(contentContainer.getPrefHeight() * 0.5);
            transactionDetailsContainer.setPrefWidth(contentContainer.getPrefWidth());
            transactionDetailsContainer.setLayoutX(0);
            transactionDetailsContainer.setLayoutY(addTransactionContainer.getPrefHeight() + titleHBox.getPrefHeight());

            transactionDetailsCV.getView().ifPresent(view -> {
                // Layout
                transactionDetailsCV.getController().transactionDetailContainerPane.setPrefWidth(transactionDetailsContainer.getPrefWidth());
                transactionDetailsCV.getController().transactionDetailContainerPane.setPrefHeight(transactionDetailsContainer.getPrefHeight() * 0.5);

                transactionDetailsCV.getController().transactionDetailTitleBar.setPrefWidth(contentContainer.getPrefWidth());
                transactionDetailsCV.getController().transactionDetailTitleBar.setPrefHeight(transactionDetailsContainer.getPrefHeight() * 0.15);
                transactionDetailsCV.getController().transactionDetailTitleBar.setSpacing(transactionDetailsContainer.getPrefHeight() * 0.02);

                transactionDetailsCV.getController().transactionDetailScrollPane.setPrefWidth(transactionDetailsContainer.getPrefWidth());
                transactionDetailsCV.getController().transactionDetailScrollPane.setPrefHeight(
                        transactionDetailsContainer.getPrefHeight() - transactionDetailsCV.getController().transactionDetailTitleBar.getPrefHeight());
                transactionDetailsCV.getController().transactionDetailScrollPane.setLayoutY(transactionDetailsCV.getController().transactionDetailTitleBar.getPrefHeight());

                transactionDetailsCV.getController().detailItemContainer.setPrefWidth(transactionDetailsContainer.getPrefWidth());
                transactionDetailsCV.getController().detailItemContainer.setPrefHeight(transactionDetailsCV.getController().transactionDetailScrollPane.getHeight());

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
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error",e.getCause().getMessage(),e.getMessage(),Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
        }
    }

    @FXML
    public void showDepositWheat() {

        try {
            contentAreaTitleLabel.setText("Wheat Deposit Form");
            contentContainer.getChildren().clear();
            //Load Deposit Wheat View
            depositWheatCV = fxWeaver.load(DepositWheatController.class);

            depositWheatCV.getView().ifPresent(view -> {
                // Layout
                depositWheatCV.getController().wheatDepositFormGP.setPrefWidth(contentContainer.getPrefWidth() * 0.5);
                depositWheatCV.getController().wheatDepositFormGP.setPrefHeight(contentContainer.getPrefHeight() * 0.5);
                depositWheatCV.getController().wheatDepositFormGP.setHgap(depositWheatCV.getController().wheatDepositFormGP.getPrefWidth() * 0.02);
                depositWheatCV.getController().wheatDepositFormGP.setVgap(depositWheatCV.getController().wheatDepositFormGP.getPrefHeight() * 0.01);

                List<ColumnConstraints> colConstList = depositWheatCV.getController().wheatDepositFormGP.getColumnConstraints();
                colConstList.get(0).setPercentWidth(40);
                colConstList.get(1).setPercentWidth(20);
                colConstList.get(2).setPercentWidth(40);

                contentContainer.getChildren().add(view);
            });

        } catch (Exception e) {
            e.printStackTrace();
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error",e.getCause().getMessage(),e.getMessage(),Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
        }
    }


    @FXML
    public void showRegisterCustomer() {

        try {
            contentAreaTitleLabel.setText("Customer Registration Form");
            contentContainer.getChildren().clear();
            registerCustomerControllerCV = fxWeaver.load(RegisterCustomerController.class);
            registerCustomerControllerCV.getView().ifPresent(view -> {
                contentContainer.getChildren().add(view);
            });

        } catch (Exception e) {
            e.printStackTrace();
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error",e.getCause().getMessage(),e.getMessage(),Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
        }
    }


    @FXML
    public void showRegisterEmployee() {
        try {
            contentAreaTitleLabel.setText("Employee Registration Form");
            contentContainer.getChildren().clear();
            //Load Register Employee View
            registerEmployeeCV = fxWeaver.load(RegisterEmployeeController.class);

            registerEmployeeCV.getView().ifPresent(view -> {
                contentContainer.getChildren().add(view);
            });


        } catch (Exception e) {
            e.printStackTrace();
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error",e.getCause().getMessage(),e.getMessage(),Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
        }
    }

    @FXML
    public void signOut() {
        try {
            contentContainer.getChildren().clear();

            // Dashboard size setting
            double width = Util.getScreenWidth() / 3.5;
            double height = Util.getScreenHeight() / 2.5;
            stage.setX((Util.getScreenWidth() - width) / 2);
            stage.setY((Util.getScreenHeight() - height) / 2);
            stage.setScene(new Scene(fxWeaver.loadView(AuthController.class), width, height));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error",e.getCause().getMessage(),e.getMessage(),Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
        }
    }

    public void showImageModal(Node node) {
        if (modalMounted) {
            return;
        }
        VBox modal = new VBox();
        modal.setMinWidth(500);
        modal.setMinHeight(400);
        modal.setMaxWidth(600);
        modal.setMaxHeight(400);
        modal.setLayoutX((Util.getScreenWidth() - modal.getMaxWidth()) / 2);
        modal.setLayoutY((Util.getScreenHeight() - modal.getMaxHeight()) / 2);
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
