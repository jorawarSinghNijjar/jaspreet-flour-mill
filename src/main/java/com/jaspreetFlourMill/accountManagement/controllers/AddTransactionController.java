package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.StageInitializer;
import com.jaspreetFlourMill.accountManagement.StageReadyEvent;
import com.jaspreetFlourMill.accountManagement.model.*;
import com.jaspreetFlourMill.accountManagement.util.AlertDialog;
import com.jaspreetFlourMill.accountManagement.util.FormValidation;
import com.jaspreetFlourMill.accountManagement.util.Util;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.Printer;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationListener;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
@FxmlView("/views/addTransaction.fxml")
public class AddTransactionController implements Initializable, ApplicationListener<StageReadyEvent> {

    @FXML
    public GridPane addTransactionPageContainerGP;

    @FXML
    public AnchorPane customerDetailsGPCol;

    @FXML
    public AnchorPane addTransactionAnchorPaneContainer;

    @FXML
    public VBox addTransactionVBoxContainer;

    @FXML
    public GridPane addTransactionFormGridPane;

    @FXML
    public TextField customerIdInput;

    @FXML
    private TextField flourPickupQtyInput;

    @FXML
    private TextField grindingRateInput;

    @FXML
    private TextField grindingChargesInput;

    @FXML
    private TextField grindingChargesPaidInput;

    @FXML
    private TextField orderPickedByInput;

    @FXML
    private Label cashierNameLabel;

    @FXML
    private Label tfCustomerIdValidLabel;

    @FXML
    private Label tfFlourPickupQtyValidLabel;

    @FXML
    private Label tfGrindingRateValidLabel;

    @FXML
    private Label tfGrindingChargesPaidValidLabel;

    @FXML
    private Label tfOrderPickedByValidLabel;

    @FXML
    private Label tfCashierValidLabel;

    private boolean validForm;

    private String cashierName;

    private double grindingCharges;

    @FXML
    protected Button submitTransactionBtn;

    private Printer currentPrinter;

    private FxControllerAndView<CustomerDetailsController, Node> customerDetailsCV;

    private FxControllerAndView<TransactionDetailController, Node> transactionDetailsCV;

    private FxControllerAndView<TransactionDetailItemController, Node> transactionDetailItemCV;

    private FxControllerAndView<TransactionPrintPreviewController, Node> transactionPrintPreviewCV;

    private final FxWeaver fxWeaver;
    private Stage stage;
    private FormValidation addTransactionFormValidation;
    private Role currentUserRole;


    public AddTransactionController(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Authorization Check
        if (StageInitializer.authentication.isAuthenticated()) {
            System.out.println("Current Cashier - " + StageInitializer.authentication.getUser().getId());
            try {
                User signedInUser = StageInitializer.authentication.getUser();
                Employee employee = Employee.get(signedInUser).orElseThrow(() -> new UsernameNotFoundException("userId: " + signedInUser));
                cashierName = employee.getName();
            } catch (Exception e) {
                e.printStackTrace();
                // Information dialog
                AlertDialog alertDialog = new AlertDialog("Error", e.getCause().getMessage(), e.getMessage(), Alert.AlertType.ERROR);
                alertDialog.showErrorDialog(e);
            }
            currentUserRole = StageInitializer.authentication.getUser().getRole();
        }


        // Loading customerDetails.fxml
        customerDetailsCV = fxWeaver.load(CustomerDetailsController.class);
        // Loading transactionDetails.fxml
        transactionDetailsCV = fxWeaver.load(TransactionDetailController.class);
        transactionDetailItemCV = fxWeaver.load(TransactionDetailItemController.class);
        transactionPrintPreviewCV = fxWeaver.load(TransactionPrintPreviewController.class);

        // Form Validation
        addTransactionFormValidation = new FormValidation();
        addTransactionFormValidation.getFormFields().put("customer-id", false);
        addTransactionFormValidation.getFormFields().put("flour-pickup-qty", false);
        addTransactionFormValidation.getFormFields().put("grinding-rate", false);
//        addTransactionFormValidation.getFormFields().put("grinding-charges", false);
        addTransactionFormValidation.getFormFields().put("grinding-charges-paid", false);
        addTransactionFormValidation.getFormFields().put("order-picked-by", false);
        addTransactionFormValidation.getFormFields().put("cashier", false);


        cashierNameLabel.setText(cashierName);

        // Validate cashier name
        if(!cashierNameLabel.getText().isEmpty()){
            addTransactionFormValidation.getFormFields().put("cashier", true);
        }
        else{
            addTransactionFormValidation.getFormFields().put("cashier", false);
        }

        this.addEventListeners();

        // Input Change Listener for grinding rate input field
        grindingRateInput.textProperty().addListener((observableValue, oldValue, newValue) -> {
            String flourPickupQtyInputText = flourPickupQtyInput.getText();
            if (!flourPickupQtyInputText.isEmpty() && flourPickupQtyInputText != null) {
                if (!newValue.isEmpty() && newValue != null) {
                    grindingCharges = Double.parseDouble(flourPickupQtyInputText)
                            * Double.parseDouble(newValue);
                    grindingChargesInput.setText(String.valueOf(grindingCharges));
                }
            }

        });

        // Input Change Listener for grinding rate input field
        flourPickupQtyInput.textProperty().addListener((observableValue, oldValue, newValue) -> {
            String grindingRateInputText = grindingRateInput.getText();
            if (!grindingRateInputText.isEmpty() && grindingRateInputText != null) {
                if (!newValue.isEmpty() && newValue != null) {
                    grindingCharges = Double.parseDouble(grindingRateInputText)
                            * Double.parseDouble(newValue);
                    grindingChargesInput.setText(String.valueOf(grindingCharges));
                }
            }
        });

        // Submit form if Enter key is pressed
        addTransactionVBoxContainer.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                submitTransaction();
            }
        });
    }

    private void addEventListeners() {
        customerIdInput.textProperty().addListener((observableValue, oldVal, newVal) -> {
            boolean valid = FormValidation.isInteger(
                    newVal,
                    tfCustomerIdValidLabel
            ).isValid();
            addTransactionFormValidation.getFormFields().put("customer-id", valid);
            this.validateForm();

        });

        flourPickupQtyInput.textProperty().addListener((observableValue, oldVal, newVal) -> {
            boolean valid = FormValidation.isDouble(
                    newVal,
                    tfFlourPickupQtyValidLabel
            ).isValid();
            addTransactionFormValidation.getFormFields().put("flour-pickup-qty", valid);
            this.validateForm();

        });

        grindingRateInput.textProperty().addListener((observableValue, oldVal, newVal) -> {
            boolean valid = FormValidation.isDouble(
                    newVal,
                    tfGrindingRateValidLabel
            ).isValid();
            addTransactionFormValidation.getFormFields().put("grinding-rate", valid);
            this.validateForm();

        });

        grindingChargesPaidInput.textProperty().addListener((observableValue, oldVal, newVal) -> {
            boolean valid = FormValidation.isDouble(
                    newVal,
                    tfGrindingChargesPaidValidLabel
            ).isValid();
            addTransactionFormValidation.getFormFields().put("grinding-charges-paid", valid);
            this.validateForm();
        });

        orderPickedByInput.textProperty().addListener((observableValue, oldVal, newVal) -> {
            boolean valid = FormValidation.isName(
                    newVal,
                    tfOrderPickedByValidLabel
            ).isValid();
            addTransactionFormValidation.getFormFields().put("order-picked-by", valid);
            this.validateForm();
        });

    }

    private boolean validateForm() {
        addTransactionFormValidation.getFormFields().entrySet().forEach(entry->{
            System.out.println(entry.getKey() + " = " + entry.getValue());
        });
        if (addTransactionFormValidation.getFormFields().containsValue(false)) {
            submitTransactionBtn.setDisable(true);
            return false;
        } else {
            submitTransactionBtn.setDisable(false);
            return true;
        }
    }

    private void submitTransaction() {
        int customerId = Integer.parseInt(customerIdInput.getText());
        double flourPickupQty = Double.parseDouble(flourPickupQtyInput.getText());
        double grindingRate = Double.parseDouble(grindingRateInput.getText());
        double grindingChargesPaid = Double.parseDouble(grindingChargesPaidInput.getText());
        String orderPickedBy = orderPickedByInput.getText();

        if (!this.validateForm()) {
            return;
        }

        Customer.get(String.valueOf(customerId)).ifPresent(customer -> {
            System.out.println("Grinding Charges ---->" + grindingCharges);
            Transaction newTransaction = new Transaction(
                    customer,
                    flourPickupQty,
                    grindingRate,
                    grindingCharges,
                    grindingChargesPaid,
                    orderPickedBy,
                    cashierName
            );

            Transaction.save(newTransaction).ifPresent(savedTransaction -> {
                this.updateSales(
                        newTransaction.getDate(),
                        flourPickupQty,
                        grindingCharges,
                        grindingChargesPaid
                );

                // Confirmation dialog for printing the transaction
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation Dialog");
                alert.setHeaderText("Transaction Successful !");
                alert.setContentText("Do you want to print the transaction?");

                Optional<ButtonType> response = alert.showAndWait();
                if (response.get() == ButtonType.OK) {
                    // ... user chose OK
                    System.out.println("Printing Transaction...");
                    this.printTransaction(newTransaction.getTransactionId());

                } else {
                    // ... user chose CANCEL or closed the dialog
                    System.out.println("Transaction printing cancelled");
                }

                flourPickupQtyInput.setText("");
                grindingChargesInput.setText("");
                grindingChargesPaidInput.setText("");
                orderPickedByInput.setText("");
                grindingRateInput.setText("");

                customerDetailsCV.getController().updateCustomerDetails(String.valueOf(customerId));
                transactionDetailsCV.getController().clearTransactionDisplay();
                transactionDetailsCV.getController().renderTransactions(String.valueOf(customerId));
            });
        });
    }


    @FXML
    public void handleSubmitTransaction(ActionEvent event) {
        submitTransaction();
    }

    // Print Transaction
    public void printTransaction(String transactionId) {
        ObservableSet<Printer> printers = Printer.getAllPrinters();

        ListView<String> listView = new ListView();

        Label jobStatus = new Label();

        // Create the Status Box
        HBox jobStatusBox = new HBox(5, new Label("Job Status: "), jobStatus);
//        pageSetupBtn = new Button("Page Setup");

        for (Printer printer : printers) {
            listView.getItems().add(printer.getName());
        }

        // Change Listener to observe change in ListView to select a printer from the list
        listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> ov,
                                final String oldvalue, final String newvalue) {
                for (Printer printer : printers) {
                    if (printer.getName().matches(listView.getSelectionModel().getSelectedItem())) {
                        currentPrinter = printer;
                        System.out.println("Current Printer: " + currentPrinter.getName());
                    }
                }
            }
        });

        VBox vBox = new VBox(10);
        Label label = new Label("Printers");
        Button printBtn = new Button("Print");
        vBox.getChildren().addAll(label, listView, printBtn, jobStatusBox);
        vBox.setPrefSize(400, 250);
        vBox.setStyle("-fx-padding: 10;");

//        Node node = (Node)e.getSource();
//        Node selectedTransactionIdLabel = node.getParent().getChildrenUnmodifiable().get(0);
//        selectedTransactionId = ((Label)selectedTransactionIdLabel).getText();

        printBtn.setOnAction(PrintEvent -> {
            transactionPrintPreviewCV.getView().ifPresent(view -> {
                System.out.println(view);
                transactionDetailItemCV.getController().printSetup(view, stage, transactionId, currentPrinter);
            });
        });

        Scene scene = new Scene(vBox, 400, 300);

        // Submit form if Enter key is pressed
        listView.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                transactionPrintPreviewCV.getView().ifPresent(view -> {
                    System.out.println("Printing.......");
                    transactionDetailItemCV.getController().printSetup(view, stage, transactionId, currentPrinter);
                });
            }
        });

        this.stage.setScene(scene);
        this.stage.show();
    }

    private void updateSales(String date, Double flourPickupQty, Double grindingCharges, Double grindingChargesPaid) {

        // Get current Sales for this date
        Optional<Sales> salesOpt = Sales.getSalesForDate(date);

        if (salesOpt.isPresent()) {
            Sales sales = salesOpt.get();
            String currentDate = sales.getDate();
            Double currentTotalWheatSold = sales.getTotalWheatSold();
            Double currentTotalGrindingChargesPaid = sales.getTotalGrindingChargesPaid();
            Double currentTotalGrindingCharges = sales.getTotalGrindingCharges();

            // Update sales table for this date
            currentTotalWheatSold += flourPickupQty;
            currentTotalGrindingCharges += grindingCharges;
            currentTotalGrindingChargesPaid += grindingChargesPaid;

            sales.setTotalWheatSold(currentTotalWheatSold);
            sales.setTotalGrindingChargesPaid(currentTotalGrindingChargesPaid);
            sales.setTotalGrindingCharges(currentTotalGrindingCharges);

            Sales.updateSales(currentDate, sales);

            sales.deductWheatSold(flourPickupQty);
        } else {
            // Save new sale for the particular date
            Sales newSale = new Sales(date, flourPickupQty, grindingCharges, grindingChargesPaid);
            newSale.deductWheatSold(flourPickupQty);
            System.out.println("New Sale: " + newSale);
            Sales.saveSales(newSale);
        }

    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        this.stage = event.getStage();
    }
}
