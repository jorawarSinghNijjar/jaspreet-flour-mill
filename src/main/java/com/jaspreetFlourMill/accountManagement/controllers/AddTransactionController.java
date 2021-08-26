package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.model.Customer;
import com.jaspreetFlourMill.accountManagement.model.Employee;
import com.jaspreetFlourMill.accountManagement.model.Sales;
import com.jaspreetFlourMill.accountManagement.model.Transaction;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@FxmlView("/views/addTransaction.fxml")
public class AddTransactionController implements Initializable {

    @FXML
    public TextField customerIdInput;

    @FXML
    private TextField attaPickupQtyInput;

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

    private String cashierName;

    private double grindingCharges;

    private FxControllerAndView<CustomerDetailsController, Node> customerDetailsCV;

    private FxControllerAndView<TransactionDetailController, Node> transactionDetailsCV;

    private final FxWeaver fxWeaver;

    public AddTransactionController(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customerDetailsCV = fxWeaver.load(CustomerDetailsController.class);
        transactionDetailsCV = fxWeaver.load(TransactionDetailController.class);

        cashierName = this.getEmployeeName(AuthController.currentSession.getUserId());
        cashierNameLabel.setText(cashierName);
        grindingRateInput.textProperty().addListener( (observableValue, oldValue, newValue) -> {
            String attaPickupQtyInputText = attaPickupQtyInput.getText();
            if( !attaPickupQtyInputText.isEmpty() && attaPickupQtyInputText !=null){
                if(!newValue.isEmpty() && newValue != null) {
                    grindingCharges = Double.parseDouble(attaPickupQtyInputText)
                            * Double.parseDouble(newValue);
                    grindingChargesInput.setText(String.valueOf(grindingCharges));
                }
            }

        });

        attaPickupQtyInput.textProperty().addListener( (observableValue, oldValue, newValue) -> {
            String grindingRateInputText = grindingRateInput.getText();
            if( !grindingRateInputText.isEmpty() && grindingRateInputText !=null) {
                if(!newValue.isEmpty() && newValue != null) {
                    grindingCharges = Double.parseDouble(grindingRateInputText)
                            * Double.parseDouble(newValue);
                    grindingChargesInput.setText(String.valueOf(grindingCharges));
                }
            }
        });
    }



    @FXML
    public void submitTransaction(ActionEvent event){
        int customerId = Integer.parseInt(customerIdInput.getText());
        double attaPickupQty = Double.parseDouble(attaPickupQtyInput.getText());
        double grindingRate = Double.parseDouble(grindingRateInput.getText());
        double grindingChargesPaid = Double.parseDouble(grindingChargesPaidInput.getText());
        String orderPickedBy = orderPickedByInput.getText();

        try{
            Customer customer = Customer.getCustomer(customerId);
            System.out.println("Grinding Charges ---->" + grindingCharges);
            Transaction newTransaction = new Transaction(
                    customer,
                    attaPickupQty,
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
                            attaPickupQty,
                            grindingCharges,
                            grindingChargesPaid
                    );

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Transaction submitted successfully");
                    alert.show();

                    attaPickupQtyInput.setText("");
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
                e.getMessage();
            }
        }
        System.out.println("Please enter a valid employee id");
        return "";
    }

    private void updateSales(String date, Double attaPickupQty, Double grindingCharges, Double grindingChargesPaid) throws Exception{
//        System.out.println("Inside update sales");
        // Get current Sales for this date
        Sales sales = Sales.getSalesForToday(date);
//        System.out.println("Sales retrieved: " + sales);

        if(sales != null){
            String currentDate = sales.getDate();
            Double currentTotalWheatSold = sales.getTotalWheatSold();
            Double currentTotalGrindingChargesPaid = sales.getTotalGrindingChargesPaid();
            Double currentTotalGrindingCharges = sales.getTotalGrindingCharges();

            // Update sales table for this date
            currentTotalWheatSold += attaPickupQty;
            currentTotalGrindingCharges += grindingCharges;
            currentTotalGrindingChargesPaid += grindingChargesPaid;

            Sales updatedSales = new Sales(currentDate,currentTotalWheatSold,currentTotalGrindingCharges,
                    currentTotalGrindingChargesPaid);

            String updateResult = Sales.updateSales(currentDate,updatedSales);

            System.out.println(updateResult);
        }
        else{
            Sales newSale = new Sales(date,attaPickupQty,grindingCharges,grindingChargesPaid);
            System.out.println("New Sale: " + newSale);
            Sales.saveSales(newSale);
        }

    }

}
