package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.model.Customer;
import com.jaspreetFlourMill.accountManagement.model.Transaction;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Component
@FxmlView("/views/transactionDetails.fxml")
public class TransactionDetailController implements Initializable {

    @FXML
    private VBox detailItemContainer;

    private List<FxControllerAndView<TransactionDetailItemController,Node>> transactionDetailItemCVList;
    private FxControllerAndView<TransactionDetailItemController,Node> transactionDetailItemCV;
    private final FxWeaver fxWeaver;

    public TransactionDetailController(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void renderTransactions(String customerId){
        transactionDetailItemCVList = new ArrayList<>(10);
        List<Transaction> transactions = new ArrayList<>(10);

        String transactionId = "";
        String time ="";
        double attaPickupQty=0.00;
        double grindingRate=0.00;
        double grindingChargesPaid=0.00;
        double grindingBalance=0.00;
        double storedWheatBalance=0.00;
        String orderPickedBy = "";
        String cashier ="";

        try{
            transactions = this.getTransactions(customerId);
        }
        catch(Exception e){
            e.getMessage();
        }


        for(int i=0; i<transactions.size(); i++){
            try {
                transactionDetailItemCV = fxWeaver.load(TransactionDetailItemController.class);
                TransactionDetailItemController transactionDetailItemController =
                        transactionDetailItemCV.getController();

                transactionId = transactions.get(i).getTransactionId();
                time = transactions.get(i).getDate();
                attaPickupQty = transactions.get(i).getAttaPickupQty();
                grindingBalance = transactions.get(i).getCustomerBalanceGrindingCharges();
                grindingRate = transactions.get(i).getGrindingRate();
                grindingChargesPaid = transactions.get(i).getGrindingChargesPaid();
                storedWheatBalance = transactions.get(i).getCustomerStoredAttaBalanceQty();
                orderPickedBy = transactions.get(i).getOrderPickedBy();
                cashier = transactions.get(i).getCashierName();

                transactionDetailItemController.setTransactionDetails(
                        transactionId,
                        time,
                        attaPickupQty,
                        grindingRate,
                        grindingChargesPaid,
                        grindingBalance,
                        storedWheatBalance,
                        orderPickedBy,
                        cashier
                );
                transactionDetailItemCVList.add(transactionDetailItemCV);
                transactionDetailItemCV.getView().ifPresent(view -> {
                    detailItemContainer.getChildren().add(view);
                });
            }
            catch (Exception e){
                e.getMessage();
            }
        }

    }

    public List<Transaction> getTransactions(String customerId) throws Exception{
        String uri =  "http://localhost:8080/transactions/query?customerId="+customerId;
        RestTemplate restTemplate = new RestTemplate();
        Transaction[] responseEntity = restTemplate.getForObject(uri,Transaction[].class);

        List<Transaction> transactions = new ArrayList<>();
        for(Transaction transaction: responseEntity){
            System.out.println(transaction.getTransactionId());
            transactions.add(transaction);
        }

        return transactions;

    }

    public void clearTransactionDisplay(){
        detailItemContainer.getChildren().clear();
    }
}
