package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.StageInitializer;
import com.jaspreetFlourMill.accountManagement.StageReadyEvent;
import com.jaspreetFlourMill.accountManagement.model.Customer;
import com.jaspreetFlourMill.accountManagement.model.Employee;
import com.jaspreetFlourMill.accountManagement.model.Role;
import com.jaspreetFlourMill.accountManagement.model.User;
import com.jaspreetFlourMill.accountManagement.util.*;
import com.sun.javafx.menu.MenuItemBase;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Dimension2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons;
import jiconfont.javafx.IconFontFX;
import jiconfont.javafx.IconNode;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;


@Component
@FxmlView("/views/dashboard-2.fxml")
public class ContentController implements Initializable, ApplicationListener<StageReadyEvent> {
    private final FxWeaver fxWeaver;

    private Stage stage;

    private Role currentUserRole;

    @FXML
    private Pane contentContainer;
    @FXML
    private AnchorPane addTransactionContainer;
    @FXML
    private AnchorPane customerDetailsContainer;
    @FXML
    private AnchorPane transactionDetailsContainer;

    @FXML
    private Label contentAreaTitleLabel;

    @FXML
    private Circle avatarFrame;

    @FXML
    private Button changeImageBtn;

    @FXML
    private StackPane imageStackPaneContainer;

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
    private Button employeesButton;

    @FXML
    private Button wheatDepositButton;

    @FXML
    private Button registerCustomerButton;

    @FXML
    private Button registerEmployeeButton;

    @FXML
    private Button addAdminButton;

    @FXML
    private Button signOutButton;

    @FXML
    private VBox sideMenuBox;

    @FXML
    private AnchorPane baseContainer;

    @FXML
    private HBox titleHBox;

    @FXML
    private HBox closeButtonContainerHBox;

    @FXML
    private Button closeButton;

    private boolean modalMounted = false;

    private FileChooser fileChooser;

    private Image avatar;
    private FxControllerAndView<RegisterAdminController, Node> registerAdminCV;
    private FxControllerAndView<EmployeesListController, Node> employeeListCV;
    private FxControllerAndView<RegisterEmployeeController, Node> registerEmployeeControllerCV;



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

        contentAreaTitleLabel.setPrefWidth(titleHBox.getPrefWidth());
        contentAreaTitleLabel.setPrefHeight(titleHBox.getPrefHeight());

        contentContainer.setPrefHeight(baseContainer.getPrefHeight() - (baseContainer.getPrefHeight() * 0.05));
        contentContainer.setLayoutY(contentAreaTitleLabel.getPrefHeight() * 1.5);

//        closeButtonContainerHBox.setPrefWidth(contentContainer.getPrefWidth());

        IconFontFX.register(GoogleMaterialDesignIcons.getIconFont());

        IconNode closeIcon = new IconNode(GoogleMaterialDesignIcons.CLOSE);
        closeIcon.setIconSize(24);
        closeIcon.setFill(Color.valueOf("#272635"));

        closeButton.setGraphic(closeIcon);


        // Profile Image Layout
        User user = StageInitializer.authentication.getUser();
        avatar = new Image("images/avatar.png");

        if(user.getProfileImgLocation() != null){
            avatar = new Image(user.getProfileImgLocation());
        }

        avatarFrame.setFill(new ImagePattern(avatar));
        changeImageBtn.setVisible(false);
        changeImageBtn.setManaged(false);

        // On hover effects
        ChangeListener<Boolean> imageHoverListener = (observableValue, oldVal, newVal) -> {
            if(newVal){
                System.out.println("in...");
                ColorAdjust colorAdjust = new ColorAdjust();
                colorAdjust.setBrightness(-0.4);
                avatarFrame.setEffect(colorAdjust);
                changeImageBtn.setVisible(true);
                changeImageBtn.setManaged(true);
            }
            else {
                System.out.println("out...");
                ColorAdjust colorAdjust = new ColorAdjust();
                colorAdjust.setBrightness(0.0);
                avatarFrame.setEffect(colorAdjust);
                changeImageBtn.setVisible(false);
                changeImageBtn.setManaged(false);
            }
        };

        avatarFrame.hoverProperty().addListener(imageHoverListener);

        // On click handler to handle changing the image
        fileChooser = new FileChooser();

        avatarFrame.setOnMouseClicked(mouseEvent -> {
            try{
                File selectedFile = fileChooser.showOpenDialog(stage);
                if(selectedFile != null){
                    String newProfileImageLocation = selectedFile.getAbsolutePath();
                    boolean valid = FormValidation.isImage(selectedFile);
                    if(valid){
                        user.setProfileImgLocation(newProfileImageLocation);
                        User.update(user.getId(),user).ifPresentOrElse(
                                (updatedUser) -> {
                            avatarFrame.setFill(new ImagePattern(new Image(updatedUser.getProfileImgLocation())));
                        },
                                () ->{
                                    // Information dialog
                                    String msg = "Changing Image Unsuccessful. Try Again! ";
                                    AlertDialog alertDialog = new AlertDialog("Info","Something went wrong",msg, Alert.AlertType.INFORMATION);
                                    alertDialog.showInformationDialog();
                        });
                    }
                    else{
                        // Information dialog
                        String msg = "Only jpeg /jpg / png are allowed";
                        AlertDialog alertDialog = new AlertDialog("Info","Invalid File Type",msg, Alert.AlertType.INFORMATION);
                        alertDialog.showInformationDialog();
                    }
                }
                else {
                    System.out.println("No file selected");
                }
            }
            catch(Exception e){
                e.getMessage();
                // Information dialog
                AlertDialog alertDialog = new AlertDialog("Error",e.getCause().getMessage(),e.getMessage(), Alert.AlertType.ERROR);
                alertDialog.showErrorDialog(e);
            }
        });

//        Hide not admin content
        if (currentUserRole == Role.EMPLOYEE) {
            sideMenuBox.getChildren().remove(registerEmployeeButton);
            sideMenuBox.getChildren().remove(homeButton);
            sideMenuBox.getChildren().remove(addAdminButton);
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

            @Override
            public void handleShowWheatDeposit(Integer id) {
                showDepositWheat(id);
            }

            @Override
            public void handleShowCustomers() {
                showCustomers();
            }

            @Override
            public void handleShowAddTransaction() {
                showAddTransaction();
            }

            @Override
            public void handleRegisterCustomer() {
                showRegisterCustomer();
            }

            @Override
            public void handleEditCustomer(Customer customer) {
                showEditCustomer(customer);
            }

            @Override
            public void handleEditEmployee(Employee employee) {
                showEditEmployee(employee);
            }

            @Override
            public void handleShowEmployees() {
                showEmployees();
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

        IconNode employeeDetailsIcon = new IconNode(GoogleMaterialDesignIcons.ASSIGNMENT_IND);
        employeeDetailsIcon.setIconSize(18);
        employeeDetailsIcon.setFill(Color.WHITE);

        IconNode plusSquare = new IconNode(FontAwesome.PLUS_SQUARE);
        plusSquare.setIconSize(18);
        plusSquare.setFill(Color.WHITE);

        IconNode customerCard = new IconNode(FontAwesome.ADDRESS_CARD_O);
        customerCard.setIconSize(18);
        customerCard.setFill(Color.WHITE);

        IconNode employeeAdd = new IconNode(FontAwesome.USER_PLUS);
        employeeAdd.setIconSize(18);
        employeeAdd.setFill(Color.WHITE);

        IconNode adminAdd = new IconNode(FontAwesome.USER_PLUS);
        adminAdd.setIconSize(18);
        adminAdd.setFill(Color.WHITE);

        IconNode unlock = new IconNode(FontAwesome.UNLOCK);
        unlock.setIconSize(18);
        unlock.setFill(Color.WHITE);

        homeButton.setGraphic(homeIcon);
        registerCustomerButton.setGraphic(customerCard);
        registerEmployeeButton.setGraphic(employeeAdd);
        addAdminButton.setGraphic(adminAdd);
        signOutButton.setGraphic(unlock);
        addTransactionButton.setGraphic(assignmentIcon);
        wheatDepositButton.setGraphic(plusSquare);
        customersButton.setGraphic(groupIcon);
        employeesButton.setGraphic(employeeDetailsIcon);

        wheatDepositButton.setOnAction(actionEvent -> navigationHandler.handleShowWheatDeposit(-1));

    }

    @FXML
    public void showHome() {
        if(!StageInitializer.authentication.isAuthenticated() || StageInitializer.authentication.getUser().getRole() != Role.ADMIN){
            navigationHandler.handleShowAddTransaction();
        }
        contentAreaTitleLabel.setText("Sales Summary".toUpperCase());
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
        contentAreaTitleLabel.setText("Search Customers By Name".toUpperCase());
        contentContainer.getChildren().clear();
        customerListCV = fxWeaver.load(CustomerListController.class);

        customerListCV.getView().ifPresent(view -> {
            // Layout
            customerListCV.getController().customerListContainerHBox.setPrefWidth(contentContainer.getPrefWidth());
            customerListCV.getController().customerListContainerHBox.setPrefHeight(contentContainer.getPrefHeight());

            customerListCV.getController().customerListContainerHBox.setSpacing(contentContainer.getPrefWidth() * 0.1);

            customerListCV.getController().customerListContainerVBox.setPrefWidth(contentContainer.getPrefWidth() * 0.4);
            customerListCV.getController().customerListContainerVBox.setPrefHeight(contentContainer.getPrefHeight());
            customerListCV.getController().customerListContainerVBox.setMaxHeight(contentContainer.getPrefHeight());
            customerListCV.getController().customerListContainerVBox.setMinHeight(contentContainer.getPrefHeight() * 0.75);

            customerListCV.getController().customerListContainerVBox.setSpacing(contentContainer.getPrefHeight() * 0.05);

            customerListCV.getController().customerDetailsFromList.setPrefWidth(contentContainer.getPrefWidth() * 0.4);
            customerListCV.getController().customerDetailsFromList.setPrefHeight(contentContainer.getPrefWidth() * 0.3);
            customerListCV.getController().customerDetailsFromList.setMaxHeight(contentContainer.getPrefWidth() * 0.3);

            contentContainer.getChildren().add(view);
        });
    }

    @FXML
    private void showEmployees() {
        contentAreaTitleLabel.setText("Search Employees By Name".toUpperCase());
        contentContainer.getChildren().clear();
        employeeListCV = fxWeaver.load(EmployeesListController.class);

        employeeListCV.getView().ifPresent(view -> {
            // Layout
            employeeListCV.getController().employeeListContainerHBox.setPrefWidth(contentContainer.getPrefWidth());
            employeeListCV.getController().employeeListContainerHBox.setPrefHeight(contentContainer.getPrefHeight());

            employeeListCV.getController().employeeListContainerHBox.setSpacing(contentContainer.getPrefWidth() * 0.1);

            employeeListCV.getController().employeeListContainerVBox.setPrefWidth(contentContainer.getPrefWidth() * 0.4);
            employeeListCV.getController().employeeListContainerVBox.setPrefHeight(contentContainer.getPrefHeight());
            employeeListCV.getController().employeeListContainerVBox.setMaxHeight(contentContainer.getPrefHeight());
            employeeListCV.getController().employeeListContainerVBox.setMinHeight(contentContainer.getPrefHeight() * 0.75);

            employeeListCV.getController().employeeListContainerVBox.setSpacing(contentContainer.getPrefHeight() * 0.05);

            employeeListCV.getController().employeeDetailsFromList.setPrefWidth(contentContainer.getPrefWidth() * 0.4);
            employeeListCV.getController().employeeDetailsFromList.setPrefHeight(contentContainer.getPrefWidth() * 0.3);
            employeeListCV.getController().employeeDetailsFromList.setMaxHeight(contentContainer.getPrefWidth() * 0.3);

            contentContainer.getChildren().add(view);
        });
    }

    @FXML
    public void showAddTransaction() {
        try {
            contentAreaTitleLabel.setText("Transactions".toUpperCase());
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
            addTransactionContainer.setPrefWidth(contentContainer.getPrefWidth() * 0.45);
            addTransactionContainer.setLayoutX(0);
            addTransactionContainer.setLayoutY(titleHBox.getPrefHeight());
            addTransactionCV.getView().ifPresent(view -> {

                addTransactionCV.getController().addTransactionVBoxContainer.setPrefWidth(addTransactionContainer.getPrefWidth());
                addTransactionCV.getController().addTransactionVBoxContainer.setPrefHeight(addTransactionContainer.getPrefHeight());

                addTransactionCV.getController().addTransactionFormGridPane.setPrefWidth(addTransactionContainer.getPrefWidth());
                addTransactionCV.getController().addTransactionFormGridPane.setHgap(addTransactionContainer.getPrefWidth() * 0.05);
                addTransactionCV.getController().addTransactionFormGridPane.setVgap(addTransactionContainer.getPrefHeight() * 0.07);

                List<ColumnConstraints> colConstList = addTransactionCV.getController().addTransactionFormGridPane.getColumnConstraints();
                colConstList.get(0).setPercentWidth(35);
                colConstList.get(1).setPercentWidth(25);
                colConstList.get(2).setPercentWidth(40);

                addTransactionCV.getController().submitTransactionBtn.setPrefWidth(Double.MAX_VALUE);

                addTransactionContainer.getChildren().add(view);
            });

//            //Load customer details view

            customerDetailsContainer = new AnchorPane();
            customerDetailsContainer.setPrefHeight(contentContainer.getPrefHeight() * 0.5);
            customerDetailsContainer.setPrefWidth(contentContainer.getPrefWidth() * 0.55);
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
            transactionDetailsContainer.setLayoutY(addTransactionContainer.getPrefHeight());

            transactionDetailsCV.getView().ifPresent(view -> {
                // Layout
                transactionDetailsCV.getController().transactionDetailContainerPane.setPrefWidth(transactionDetailsContainer.getPrefWidth());
                transactionDetailsCV.getController().transactionDetailContainerPane.setPrefHeight(transactionDetailsContainer.getPrefHeight() * 0.5);

                transactionDetailsCV.getController().transactionDetailTitleBar.setPrefWidth(contentContainer.getPrefWidth());
                transactionDetailsCV.getController().transactionDetailTitleBar.setPrefHeight(transactionDetailsContainer.getPrefHeight() * 0.20);
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
    public void showDepositWheat(Integer id) {

        try {
            contentAreaTitleLabel.setText("Wheat Deposit Form".toUpperCase());
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

                depositWheatCV.getController().setCurrentCustomerId(id);

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
            contentAreaTitleLabel.setText("Customer Registration Form".toUpperCase());
            contentContainer.getChildren().clear();
            registerCustomerControllerCV = fxWeaver.load(RegisterCustomerController.class);
            registerCustomerControllerCV.getView().ifPresent(view -> {
                registerCustomerControllerCV.getController().formType = "REGISTER";
                contentContainer.getChildren().add(view);
            });

        } catch (Exception e) {
            e.printStackTrace();
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error",e.getCause().getMessage(),e.getMessage(),Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
        }
    }

    private void showEditCustomer(Customer customer) {
        contentAreaTitleLabel.setText("Customer Edit Form".toUpperCase());
        contentContainer.getChildren().clear();
        registerCustomerControllerCV = fxWeaver.load(RegisterCustomerController.class);
        registerCustomerControllerCV.getView().ifPresent(view -> {
            registerCustomerControllerCV.getController().formType = "EDIT";
            registerCustomerControllerCV.getController().populateFields(customer);
            contentContainer.getChildren().add(view);
        });
    }

    private void showEditEmployee(Employee employee) {
        contentAreaTitleLabel.setText("Employee Edit Form".toUpperCase());
        contentContainer.getChildren().clear();
        registerEmployeeControllerCV = fxWeaver.load(RegisterEmployeeController.class);
        registerEmployeeControllerCV.getView().ifPresent(view -> {
            registerEmployeeControllerCV.getController().formType = "EDIT";
            registerEmployeeControllerCV.getController().populateFields(employee);
            contentContainer.getChildren().add(view);
        });
    }


    @FXML
    public void showRegisterEmployee() {
        try {
            contentAreaTitleLabel.setText("Employee Registration Form".toUpperCase());
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
    public void showRegisterAdmin(){
        try {
            System.out.println("Redirecting to registration page....");
            Dimension2D dimension2D = Util.getCenterSceneDim(stage,2.5,1.5);
            registerAdminCV = fxWeaver.load(RegisterAdminController.class);
            registerAdminCV.getController().setLayout(dimension2D);
            registerAdminCV.getView().ifPresent(view -> {
                stage.setScene(new Scene((Parent)(view),dimension2D.getWidth(),dimension2D.getHeight()));
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


    public void handleClose(ActionEvent actionEvent) {
        stage.close();
    }
}
