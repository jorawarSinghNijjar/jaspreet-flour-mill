package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.model.Employee;
import com.jaspreetFlourMill.accountManagement.model.Transaction;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Callback;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@FxmlView("/views/dashboard.fxml")

public class DashboardController {
    @FXML
    private ListView<String> menu;

    @FXML
    private BorderPane dashboard;

    @FXML
    public void initialize() {
        populateMenu();
        observeSelectedMenuItemView();
    }

    public void populateMenu() {
        ObservableList<String> menuItems = FXCollections.observableArrayList(
                "Home","Add Transaction", "View Transaction", "Settings"
        );
        menu.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> stringListView) {
                return new ListCell<>(){
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item);
                        setStyle("-fx-control-inner-background: #272635; -fx-font-weight: bold;");
                    }
                };
            }
        });
        menu.setItems(menuItems);

    }

    private void observeSelectedMenuItemView(){
        menu.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue != null){
                String selectMenuItem = menu.getSelectionModel().getSelectedItem();
                showSelectedMenuItemView(selectMenuItem);
            }
        });
    }

    private void showSelectedMenuItemView(String selectedMenuItem){
        System.out.println(selectedMenuItem);
        switch (selectedMenuItem){
            case "Add Transaction":
                showAddTransaction();
                break;
            case "View Transaction":
                showViewTransaction();
                break;
            case "Settings":
                showSettings();
                break;
            case "Home":
//                showHome();
                break;
            default:
                System.out.println("Nothing selected");
        }
    }

    private void showAddTransaction(){
//        selectedMenuItemView.getChildren().clear();
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(20);
        gridPane.setVgap(35);

        Text heading = new Text("Enter transaction details");

        Label customerIdLabel = new Label("Customer ID:");
        TextField customerIdInput = new TextField();
        Label attaPickUpQtyLabel = new Label("Atta Pickup Qty:");
        TextField attaPickUpQtyInput = new TextField();
        Label grindingChargesPaidLabel = new Label("Grinding Charges Paid:");
        TextField grindingChargesPaidInput = new TextField();
        Label orderPickedByLabel = new Label("Order Picked By:");
        TextField orderPickedByInput = new TextField();
        Label cashierNameLabel = new Label("Cashier:");
        Label cashierName = new Label();
        cashierName.setText("Santokh");

        Button submitBtn = new Button("Submit");
        submitBtn.getStyleClass().add("secondary-btn");
        submitBtn.setPrefWidth(250);

        gridPane.add(heading,0,0,2,1);
        gridPane.add(customerIdLabel,0,1);
        gridPane.add(attaPickUpQtyLabel,0,2);
        gridPane.add(grindingChargesPaidLabel,0,3);
        gridPane.add(orderPickedByLabel,0,4);
        gridPane.add(cashierNameLabel,0,5);

        gridPane.add(customerIdInput,1,1);
        gridPane.add(attaPickUpQtyInput,1,2);
        gridPane.add(grindingChargesPaidInput,1,3);
        gridPane.add(orderPickedByInput,1,4);
        gridPane.add(cashierName,1,5);

        gridPane.add(submitBtn,0,7,2,1);

        dashboard.setCenter(gridPane);


    }

    private void showViewTransaction(){

        TableView tableView = new TableView();

        tableView.setColumnResizePolicy((Callback<TableView.ResizeFeatures, Boolean>) resizeFeatures -> true);

        TableColumn<Transaction, String> transactionIdColumn = new TableColumn<>("Transaction ID");
        transactionIdColumn.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        transactionIdColumn.setPrefWidth(100);

        TableColumn<Transaction, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Transaction, Double> attaPickupQtyColumn = new TableColumn<>("Atta Pickup Qty");
        attaPickupQtyColumn.setCellValueFactory(new PropertyValueFactory<>("attaPickupQty"));

        TableColumn<Transaction, Double> grindingChargesPaidColumn = new TableColumn<>("Grinding Charges Paid");
        grindingChargesPaidColumn.setCellValueFactory(new PropertyValueFactory<>("grindingChargesPaid"));

        TableColumn<Transaction, Double> customerBalanceGrindingChargesColumn = new TableColumn<>("Balance Grinding Charges");
        customerBalanceGrindingChargesColumn.setCellValueFactory(new PropertyValueFactory<>("customerBalanceGrindingCharges"));

        TableColumn<Transaction, Double> customerStoredAttaBalanceQtyColumn = new TableColumn<>("Stored Atta Balance Qty");
        customerStoredAttaBalanceQtyColumn.setCellValueFactory(new PropertyValueFactory<>("customerStoredAttaBalanceQty"));

        TableColumn<Transaction, String> orderPickedByColumn = new TableColumn<>("Order Picked By");
        orderPickedByColumn.setCellValueFactory(new PropertyValueFactory<>("orderPickedBy"));

        TableColumn<Transaction, String> cashierNameColumn = new TableColumn<>("Cashier");
        cashierNameColumn.setCellValueFactory(new PropertyValueFactory<>("cashierName"));

        tableView.getColumns().add(transactionIdColumn);
        tableView.getColumns().add(dateColumn);
        tableView.getColumns().add(attaPickupQtyColumn);
        tableView.getColumns().add(grindingChargesPaidColumn);
        tableView.getColumns().add(customerBalanceGrindingChargesColumn);
        tableView.getColumns().add(customerStoredAttaBalanceQtyColumn);
        tableView.getColumns().add(orderPickedByColumn);
        tableView.getColumns().add(cashierNameColumn);

        tableView.getItems().add(
                new Transaction(
                        "12",
                        25,
                        40,
                        "Rahul",
                        "Santokh"));
        tableView.getItems().add(
                new Transaction(
                        "13",
                        13,
                        30,
                        "Parmeet",
                        "Santokh"));

        Text headingText = new Text("Transaction Details for Customer Name");
        headingText.getStyleClass().addAll("h2");

        HBox titleBar = new HBox();
        titleBar.getChildren().add(headingText);
        titleBar.getStyleClass().add("p-3");
        titleBar.setAlignment(Pos.CENTER);
        VBox vBox = new VBox(titleBar,tableView);
        VBox.setVgrow(tableView,Priority.ALWAYS);
        dashboard.setCenter(vBox);

    }

    public void showSettings(){
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(20);
        gridPane.setVgap(35);

        Text headingText = new Text("Settings");
        headingText.getStyleClass().addAll("h2");

        HBox titleBar = new HBox();
        titleBar.getChildren().add(headingText);
        titleBar.getStyleClass().add("p-3");
        titleBar.setAlignment(Pos.CENTER);

        Button registerEmployeeBtn = new Button("Register Employee");

        registerEmployeeBtn.setOnAction(
                actionEvent -> showRegisterEmployeeView()
        );

        gridPane.add(titleBar, 0,0,2,1);
        gridPane.add(registerEmployeeBtn,0,1,2,1);

        dashboard.setCenter(gridPane);
    }

    public void showRegisterEmployeeView(){
        GridPane addEmployeeView = new GridPane();
        addEmployeeView.setAlignment(Pos.CENTER);
        addEmployeeView.setHgap(20);
        addEmployeeView.setVgap(30);

        Text heading = new Text("Enter Employee Details");

        Label employeeNameLabel = new Label("Employee Name:");
        TextField employeeNameInput = new TextField();
        Label employeeUsernameLabel = new Label("Username:");
        TextField employeeUsernameInput = new TextField();
        Label employeePasswordLabel = new Label("Password:");
        PasswordField employeePasswordInput = new PasswordField();
        Label employeeContactNumberLabel = new Label("Contact Number");
        TextField employeeContactNumberInput = new TextField();
        Label employeeAddressLabel = new Label("Address:");
        TextField employeeAddressInput = new TextField();
        Label employeeDesignationLabel = new Label("Job Designation:");
        TextField employeeDesignationInput = new TextField();
        Label employeeDOBLabel = new Label("Date of Birth: ");
        DatePicker employeeDOBInput = new DatePicker();

        addEmployeeView.add(heading,0,0,2,1);
        addEmployeeView.add(employeeNameLabel,0,1);
        addEmployeeView.add(employeeUsernameLabel,0,2);
        addEmployeeView.add(employeePasswordLabel,0,3);
        addEmployeeView.add(employeeContactNumberLabel,0,4);
        addEmployeeView.add(employeeAddressLabel,0,5);
        addEmployeeView.add(employeeDesignationLabel,0,6);
        addEmployeeView.add(employeeDOBLabel,0,7);

        addEmployeeView.add(employeeNameInput,1,1);
        addEmployeeView.add(employeeUsernameInput,1,2);
        addEmployeeView.add(employeePasswordInput,1,3);
        addEmployeeView.add(employeeContactNumberInput,1,4);
        addEmployeeView.add(employeeAddressInput,1,5);
        addEmployeeView.add(employeeDesignationInput,1,6);
        addEmployeeView.add(employeeDOBInput,1,7);

        Button registerBtn = new Button("Register");
        registerBtn.getStyleClass().add("secondary-btn");
        registerBtn.setPrefWidth(250);

        registerBtn.setOnAction(actionEvent -> {
            String name = employeeNameInput.getText();
            String username = employeeUsernameInput.getText();
            String password = employeePasswordInput.getText();
            String contactNumber = employeeContactNumberInput.getText();
            String address = employeeAddressInput.getText();
            String jobDesignation = employeeDesignationInput.getText();
            LocalDate dob = employeeDOBInput.getValue();

            Employee employee = new Employee(name,username,password,contactNumber,address,jobDesignation,dob);

        });

        addEmployeeView.add(registerBtn,0,9,2,1);

        Button backBtn = new Button("Back");
        backBtn.setOnAction(
                actionEvent -> showSettings()
        );

        addEmployeeView.add(backBtn,0,8,2,1);

        dashboard.setCenter(addEmployeeView);
    }




}
