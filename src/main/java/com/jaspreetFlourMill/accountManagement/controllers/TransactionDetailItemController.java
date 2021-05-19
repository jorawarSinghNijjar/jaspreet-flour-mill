package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.StageReadyEvent;
import com.sun.javafx.print.PrintHelper;
import com.sun.javafx.print.Units;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
@FxmlView("/views/transactionDetailItem.fxml")
public class TransactionDetailItemController implements Initializable, ApplicationListener<StageReadyEvent> {
    private Stage stage;

    @FXML
    private HBox transactionDetailItem;
    @FXML
    private Label transactionIdLabel;

    @FXML
    private Label timeStampLabel;

    @FXML
    private Label attaPickupQtyLabel;

    @FXML
    private Label grindingChargesPaidLabel;

    @FXML
    private Label grindingBalanceLabel;

    @FXML
    private Label storedWheatBalanceLabel;

    @FXML
    private Label orderPickedByLabel;

    @FXML
    private Label cashierLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public  void setTransactionDetails(
            String transactionId, String time, double attaPickupQty,
            double grindingRate,
            double grindingChargesPaid, double grindingBalance,
            double storedWheatBalance, String orderPickedBy,
            String cashier
    ) {
        transactionIdLabel.setText(transactionId);
        timeStampLabel.setText(time);
        attaPickupQtyLabel.setText(String.valueOf(attaPickupQty));
        grindingChargesPaidLabel.setText(grindingChargesPaid + " @ Rs." + grindingRate +"/kg");
        grindingBalanceLabel.setText(String.valueOf(grindingBalance));
        storedWheatBalanceLabel.setText(String.valueOf(storedWheatBalance));
        orderPickedByLabel.setText(orderPickedBy);
        cashierLabel.setText(cashier);

    }

    @FXML
    public  void printTransaction(ActionEvent e){
        ObservableSet<Printer> printers = Printer.getAllPrinters();

        ListView listView = new ListView();

        for(Printer printer: printers){
            listView.getItems().add(printer.getName());
        }

        VBox vBox = new VBox(10);
        Label label = new Label("Printers");
        Button printBtn = new Button("Print");
        vBox.getChildren().addAll(label,listView,printBtn);
        vBox.setPrefSize(400,250);
        vBox.setStyle("-fx-padding: 10;");

        printBtn.setOnAction(printEvent -> {
            print(transactionDetailItem);
        });

        Scene scene = new Scene(vBox, 400,300);
        stage.setScene(scene);
        stage.show();
    }

    private void print(Node node){
        Printer printer = Printer.getDefaultPrinter();
        Paper paper = PrintHelper.createPaper("160X90", 160, 90, Units.MM);
        PageLayout pageLayout = printer.createPageLayout(
                paper, PageOrientation.LANDSCAPE, Printer.MarginType.DEFAULT
        );

        PrinterJob printerJob = PrinterJob.createPrinterJob(printer);

        boolean proceed = printerJob.showPageSetupDialog(stage);



        if(proceed){
            System.out.println("Creating a printing job.....");
            if(printerJob != null){
                boolean printed = printerJob.printPage(pageLayout,node);

                if(printed){
                    printerJob.endJob();
                }
                else{
                    System.out.println("Printing Failed......");
                }
            }
            else{
                System.out.println("Could not create a printing job");
            }
        }

    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        stage = event.getStage();
    }
}
