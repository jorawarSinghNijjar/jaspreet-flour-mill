package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.StageReadyEvent;
import com.jaspreetFlourMill.accountManagement.model.Customer;
import com.jaspreetFlourMill.accountManagement.model.Employee;
import com.jaspreetFlourMill.accountManagement.model.Sales;
import com.jaspreetFlourMill.accountManagement.model.Transaction;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationListener;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
@FxmlView("/views/addTransaction.fxml")
public class AddTransactionController implements Initializable, ApplicationListener<StageReadyEvent> {

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
    public Label customerIdInputValidLabel;

    private String cashierName;

    private double grindingCharges;

    private Printer currentPrinter;

    private FxControllerAndView<CustomerDetailsController, Node> customerDetailsCV;

    private FxControllerAndView<TransactionDetailController, Node> transactionDetailsCV;

    private FxControllerAndView<TransactionDetailItemController,Node> transactionDetailItemCV;

    private FxControllerAndView<TransactionPrintPreviewController,Node> transactionPrintPreviewCV;

    private final FxWeaver fxWeaver;
    private Stage stage;


    public AddTransactionController(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customerDetailsCV = fxWeaver.load(CustomerDetailsController.class);
        transactionDetailsCV = fxWeaver.load(TransactionDetailController.class);
        transactionDetailItemCV = fxWeaver.load(TransactionDetailItemController.class);
        transactionPrintPreviewCV = fxWeaver.load(TransactionPrintPreviewController.class);

        cashierName = this.getEmployeeName(AuthController.currentSession.getUserId());
        cashierNameLabel.setText(cashierName);

        // Input Change Listener for grinding rate input field
        grindingRateInput.textProperty().addListener( (observableValue, oldValue, newValue) -> {
            String flourPickupQtyInputText = flourPickupQtyInput.getText();
            if( !flourPickupQtyInputText.isEmpty() && flourPickupQtyInputText !=null){
                if(!newValue.isEmpty() && newValue != null) {
                    grindingCharges = Double.parseDouble(flourPickupQtyInputText)
                            * Double.parseDouble(newValue);
                    grindingChargesInput.setText(String.valueOf(grindingCharges));
                }
            }

        });

        // Input Change Listener for grinding rate input field
        flourPickupQtyInput.textProperty().addListener( (observableValue, oldValue, newValue) -> {
            String grindingRateInputText = grindingRateInput.getText();
            if( !grindingRateInputText.isEmpty() && grindingRateInputText !=null) {
                if(!newValue.isEmpty() && newValue != null) {
                    grindingCharges = Double.parseDouble(grindingRateInputText)
                            * Double.parseDouble(newValue);
                    grindingChargesInput.setText(String.valueOf(grindingCharges));
                }
            }
        });

        // Submit form if Enter key is pressed
        this.stage.getScene().setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER){
                submitTransaction();
            }
        });
    }

    private void submitTransaction() {
        int customerId = Integer.parseInt(customerIdInput.getText());
        double flourPickupQty = Double.parseDouble(flourPickupQtyInput.getText());
        double grindingRate = Double.parseDouble(grindingRateInput.getText());
        double grindingChargesPaid = Double.parseDouble(grindingChargesPaidInput.getText());
        String orderPickedBy = orderPickedByInput.getText();

        try{
            Customer customer = Customer.getCustomer(customerId);
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

//            System.out.println("New transaction: " + newTransaction.toString());

            if(newTransaction != null){
                // POST request to register employee
                final String uri =  "http://localhost:8080/transactions/";
                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Transaction> req = new HttpEntity<>(newTransaction,httpHeaders);
                ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST,req,String.class);
//                System.out.println(result);
                if(result != null){
//                    System.out.println("Before calling update sales....");
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
                    if (response.get() == ButtonType.OK){
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
                }
            }

        }
        catch(Exception e){
            e.getMessage();
        }
    }


    @FXML
    public void handleSubmitTransaction(ActionEvent event){
        submitTransaction();
    }

    // Print Transaction
    public void printTransaction(String transactionId){
        ObservableSet<Printer> printers = Printer.getAllPrinters();

        ListView<String> listView = new ListView();

        Label jobStatus = new Label();

        // Create the Status Box
        HBox jobStatusBox = new HBox(5, new Label("Job Status: "), jobStatus);
//        pageSetupBtn = new Button("Page Setup");

        for(Printer printer: printers){
            listView.getItems().add(printer.getName());
        }

        // Change Listener to observe change in ListView to select a printer from the list
        listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>()
        {
            public void changed(ObservableValue<? extends String> ov,
                                final String oldvalue, final String newvalue)
            {
                for(Printer printer: printers){
                    if(printer.getName().matches(listView.getSelectionModel().getSelectedItem())){
                        currentPrinter = printer;
                        System.out.println("Current Printer: " + currentPrinter.getName());
                    }
                }
            }});

        VBox vBox = new VBox(10);
        Label label = new Label("Printers");
        Button printBtn = new Button("Print");
        vBox.getChildren().addAll(label,listView,printBtn,jobStatusBox);
        vBox.setPrefSize(400,250);
        vBox.setStyle("-fx-padding: 10;");

//        Node node = (Node)e.getSource();
//        Node selectedTransactionIdLabel = node.getParent().getChildrenUnmodifiable().get(0);
//        selectedTransactionId = ((Label)selectedTransactionIdLabel).getText();

        printBtn.setOnAction(PrintEvent -> {
            transactionPrintPreviewCV.getView().ifPresent(view -> {
                System.out.println(view);
                transactionDetailItemCV.getController().printSetup(view,stage,transactionId,currentPrinter);
            });
        });

        Scene scene = new Scene(vBox, 400,300);

        // Submit form if Enter key is pressed
        listView.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER){
                transactionPrintPreviewCV.getView().ifPresent(view -> {
                    System.out.println("Printing.......");
                    transactionDetailItemCV.getController().printSetup(view,stage,transactionId,currentPrinter);
                });
            }
        });

        this.stage.setScene(scene);
        this.stage.show();
    }


    private String getEmployeeName(String employeeId){
        if(employeeId != null || !employeeId.isEmpty()){
            try {
                String uri = "http://localhost:8080/employees/" + employeeId;
                RestTemplate restTemplate = new RestTemplate();
                Employee responseEntity = restTemplate.getForObject(uri,Employee.class);
                return responseEntity.getName();
            }
            catch(Exception e){
                System.out.println("Failed to retrieve employee name");
                e.printStackTrace();
            }
        }
        System.out.println("Please enter a valid employee id");
        return "";
    }

    private void updateSales(String date, Double flourPickupQty, Double grindingCharges, Double grindingChargesPaid) throws Exception{
//        System.out.println("Inside update sales");
        // Get current Sales for this date
        Sales sales = Sales.getSalesForDate(date);
//        System.out.println("Sales retrieved: " + sales);

        if(sales != null){
            String currentDate = sales.getDate();
            Double currentTotalWheatSold = sales.getTotalWheatSold();
            Double currentTotalGrindingChargesPaid = sales.getTotalGrindingChargesPaid();
            Double currentTotalGrindingCharges = sales.getTotalGrindingCharges();

            // Update sales table for this date
            currentTotalWheatSold += flourPickupQty;
            currentTotalGrindingCharges += grindingCharges;
            currentTotalGrindingChargesPaid += grindingChargesPaid;


//            Sales updatedSales = new Sales(currentDate,currentTotalWheatSold,currentTotalGrindingCharges,
//                    currentTotalGrindingChargesPaid);
            sales.setTotalWheatSold(currentTotalWheatSold);
            sales.setTotalGrindingChargesPaid(currentTotalGrindingChargesPaid);
            sales.setTotalGrindingCharges(currentTotalGrindingCharges);

            Sales.updateSales(currentDate,sales);

            sales.deductWheatSold(flourPickupQty);
        }
        else{
            Sales newSale = new Sales(date,flourPickupQty,grindingCharges,grindingChargesPaid);
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
