package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.model.Transaction;
import com.jaspreetFlourMill.accountManagement.util.AlertDialog;
import com.jaspreetFlourMill.accountManagement.util.Util;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@FxmlView("/views/transactionDetails.fxml")
public class TransactionDetailController implements Initializable {
    @FXML
    public AnchorPane transactionDetailContainerPane;

    @FXML
    public HBox transactionDetailTitleBar;

    @FXML
    public ScrollPane transactionDetailScrollPane;

    @FXML
    public VBox detailItemContainer;

    @FXML
    private Label transactionTimeHeading;

    @FXML
    private Label transactionFlourPickupQtyHeading;

    @FXML
    private Label transactionGrindingAmountHeading;

    @FXML
    private Label transactionGrindingAmountPaidHeading;

    @FXML
    private Label transactionGrindingBalanceAmountHeading;

    @FXML
    private Label transactionStoredWheatBalanceHeading;

    @FXML
    private Label transactionOrderPickedByHeading;

    @FXML
    private Label transactionCashierHeading;


    private List<FxControllerAndView<TransactionDetailItemController,Node>> transactionDetailItemCVList;
    private FxControllerAndView<TransactionDetailItemController,Node> transactionDetailItemCV;
    private final FxWeaver fxWeaver;


    public TransactionDetailController(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        transactionTimeHeading.setText("????????????" + "\n" + "Time");
        transactionFlourPickupQtyHeading.setText("????????? ??????????????????" + "\n" + "Flour Pickup Qty");
        transactionGrindingAmountHeading.setText("??????????????? ?????????" + "\n" + "Grinding Amount");
        transactionGrindingAmountPaidHeading.setText("????????? ????????? ????????????" + "\n" + "Amount Paid");
        transactionGrindingBalanceAmountHeading.setText("???????????? ?????????" + "\n" + "Unpaid Amount");
        transactionStoredWheatBalanceHeading.setText("???????????? ?????????"+ "\n" +"Account Wheat Balance");
        transactionOrderPickedByHeading.setText("?????????????????? ????????????"+ "\n" +"Order Picked By");
        transactionCashierHeading.setText("??????????????????" + "\n" + "Cashier");
    }

    public void renderTransactions(String customerId){
        transactionDetailItemCVList = new ArrayList<>(10);
        List<Transaction> transactions = new ArrayList<>(10);

        String transactionId = "";
        String time ="";
        double flourPickupQty=0.00;
        double grindingCharges=0.00;
        double grindingRate=0.00;
        double grindingChargesPaid=0.00;
        double grindingBalance=0.00;
        double storedWheatBalance=0.00;
        String orderPickedBy = "";
        String cashier ="";

        transactions = Transaction.getAll(customerId);

        if(transactions != null){
            for(int i=0; i<transactions.size(); i++){
                try {
//                System.out.println("--------------------->"+transactions.get(i).toString());
                    transactionDetailItemCV = fxWeaver.load(TransactionDetailItemController.class);
                    TransactionDetailItemController transactionDetailItemController =
                            transactionDetailItemCV.getController();

                    // Change of Date Time format from US to IND
                    String displayDate = Util.usToIndDateFormat(transactions.get(i).getDate());
                    String displayTime = Util.usToIndTimeFormat(transactions.get(i).getTime());

                    transactionId = transactions.get(i).getTransactionId();
                    time = displayDate +" " + displayTime;
                    flourPickupQty = transactions.get(i).getFlourPickupQty();
                    grindingCharges = transactions.get(i).getGrindingCharges();
                    grindingBalance = transactions.get(i).getCustomerBalanceGrindingCharges();
                    grindingRate = transactions.get(i).getGrindingRate();
                    grindingChargesPaid = transactions.get(i).getGrindingChargesPaid();
                    storedWheatBalance = transactions.get(i).getCustomerStoredFlourBalanceQty();
                    orderPickedBy = transactions.get(i).getOrderPickedBy();
                    cashier = transactions.get(i).getCashierName();

                    transactionDetailItemController.setTransactionDetails(
                            transactionId,
                            time,
                            flourPickupQty,
                            grindingCharges,
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
                    System.out.println("Transaction rendering failed............");
                    // Information dialog
                    AlertDialog alertDialog = new AlertDialog("Error",e.getCause().getMessage(),e.getMessage(), Alert.AlertType.ERROR);
                    alertDialog.showErrorDialog(e);
                    e.printStackTrace();
                }
            }
        }
        else {
            System.out.println("No transactions found !");
        }

    }

    public void clearTransactionDisplay(){
        detailItemContainer.getChildren().clear();
    }
}
