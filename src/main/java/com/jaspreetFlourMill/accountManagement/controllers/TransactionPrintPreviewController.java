package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.StageReadyEvent;
import com.jaspreetFlourMill.accountManagement.model.CustomerAccount;
import com.jaspreetFlourMill.accountManagement.model.Transaction;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Component
@FxmlView("/views/transactionPrintPage.fxml")
public class TransactionPrintPreviewController implements Initializable, ApplicationListener<StageReadyEvent> {
    @FXML
    private AnchorPane transactionPrintPreviewContainer;

    private Stage stage;

    @FXML
    private VBox printPageVBox;

    @FXML
    private Button backToHomeBtn;

    private int currentPrintRow;

    private static final int printRowsMax =  6;
    private boolean nextPage;
    private static final double columnWidth =  132;
    private static final double columnHeight = 60;
    private int customerId;
    private final FxWeaver fxWeaver;

    public TransactionPrintPreviewController(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
            nextPage = false;
//            backToHomeBtn = new Button("Back");
//            backToHomeBtn.setOnAction(ActionEvent -> {
//                stage.setScene(new Scene(fxWeaver.loadView(ContentController.class),1366,768));
//                stage.setX(0);
//                stage.setY(0);
//                stage.show();
//            });
//            transactionPrintPreviewContainer.getChildren().add(backToHomeBtn);
        transactionPrintPreviewContainer.setStyle("-fx-background-color:white;");

        printPageVBox = new VBox();
        printPageVBox.setPrefSize(
                transactionPrintPreviewContainer.getPrefWidth()
                ,transactionPrintPreviewContainer.getPrefHeight()
        );
        double vBoxHeight = printPageVBox.getPrefHeight();
        double heightSum = 0;
        while(heightSum <= vBoxHeight){
            HBox hBox = new HBox();
            hBox.setPrefWidth(columnWidth);
            hBox.setPrefHeight(columnHeight);
//            hBox.setStyle("-fx-border-style: hidden solid solid hidden;" +
//                    "-fx-border-color: grey;");
            printPageVBox.getChildren().add(hBox);
            heightSum += columnHeight;
        }

        transactionPrintPreviewContainer.getChildren().add(printPageVBox);
    }

    public boolean populateTransactionRow(Transaction transaction,Printer printer){
        System.out.println("Printing row :" + currentPrintRow);

        try{
            customerId = transaction.getCustomer().getCustomerId();
            currentPrintRow = CustomerAccount.getCustomerAccount(customerId).getRowsPrinted();
            currentPrintRow++;
            if(currentPrintRow > printRowsMax){
                nextPage = true;
            }
        }
        catch (Exception e){
            e.getMessage();
        }


        HBox printRow = new HBox();
        printRow.setPrefWidth(1056);
        printRow.setPrefHeight(50);
        printRow.setSpacing(5);
        printRow.setStyle("-fx-border-style: hidden hidden solid hidden; " +
                "-fx-border-color: grey;");
        Label transactionIdLabel = new Label(transaction.getTransactionId());
        Label timeLabel = new Label(transaction.getTime());
        Label flourPickupQtyLabel = new Label(String.valueOf(transaction.getFlourPickupQty()));
        Label grindingChargesLabel = new Label(String.valueOf(transaction.getGrindingCharges()));
        Label  grindingChargesPaidLabel= new Label(String.valueOf(transaction.getGrindingChargesPaid()));
        Label customerBalanceGrindingLabel = new Label(String.valueOf(transaction.getCustomerBalanceGrindingCharges()));
        Label customerStoredWheatBalanceLabel = new Label(String.valueOf(transaction.getCustomerStoredFlourBalanceQty()));
        Label orderPickedByLabel = new Label(String.valueOf(transaction.getOrderPickedBy()));

            List<Label> labels = new ArrayList<>();
            labels.add(transactionIdLabel);
            labels.add(timeLabel);
            labels.add(flourPickupQtyLabel);
            labels.add(grindingChargesLabel);
            labels.add(grindingChargesPaidLabel);
            labels.add(customerBalanceGrindingLabel);
            labels.add(customerStoredWheatBalanceLabel);
            labels.add(orderPickedByLabel);

            this.setRowSettings(labels, columnWidth, columnHeight,"data");

        printRow.getChildren().addAll(
                transactionIdLabel,
                timeLabel,
                flourPickupQtyLabel,
                grindingChargesLabel,
                grindingChargesPaidLabel,
                customerBalanceGrindingLabel,
                customerStoredWheatBalanceLabel,
                orderPickedByLabel);

        try{
            HBox hboxContainer = (HBox)(printPageVBox.getChildren().get(currentPrintRow));
            if(hboxContainer != null){
                hboxContainer.getChildren().add(printRow);
            }

            if(currentPrintRow == 1){
                this.printColumnHeaders();
            }

            this.printSetup(transactionPrintPreviewContainer,stage,printer);

            return true;
        }
        catch (IndexOutOfBoundsException e){
            e.getMessage();
            System.out.println("Print row number has exceeded the limit of the page OR Turn over the page");
            return false;
        }

    }

//    private boolean checkPageCompletion(){
//        currentPrintRow++;
//        if(currentPrintRow > printRowsMax){
//            currentPrintRow = 1;
//            System.out.println("Insert new page.....");
//            return true;
//        }
//        return false;
//    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        stage = event.getStage();
    }

    private void setRowSettings(List<Label> labels, double width, double height,String type){
        for(Label label: labels){
            label.setPrefHeight(height);
            label.setPrefWidth(width);
            label.setMaxWidth(width);
            label.setMaxHeight(height);
            label.setAlignment(Pos.CENTER);
            label.setTextAlignment(TextAlignment.CENTER);
            label.setContentDisplay(ContentDisplay.CENTER);
            label.setWrapText(true);
//            label.setStyle("-fx-border-style: hidden solid hidden hidden; " +
//                    "-fx-border-color: grey;");
            if(type == "colHeader"){
                label.setStyle("-fx-font-weight: bold;");
            }
        }
    }

    private void printColumnHeaders(){
        Label transactionIdLabel = new Label("Transaction ID");
        Label timeLabel = new Label("Time");
        Label flourPickupQtyLabel = new Label("Flour Pickup Qty");
        Label grindingChargesLabel = new Label("Grinding Charges");
        Label  grindingChargesPaidLabel= new Label("Grinding Charges Paid");
        Label customerBalanceGrindingLabel = new Label("Customer Balance Grinding");
        Label customerStoredWheatBalanceLabel = new Label("Customer Stored Wheat Balance");
        Label orderPickedByLabel = new Label("Order Picked By");


        List<Label> labels = new ArrayList<>();
        labels.add(transactionIdLabel);
        labels.add(timeLabel);
        labels.add(flourPickupQtyLabel);
        labels.add(grindingChargesLabel);
        labels.add(grindingChargesPaidLabel);
        labels.add(customerBalanceGrindingLabel);
        labels.add(customerStoredWheatBalanceLabel);
        labels.add(orderPickedByLabel);

        this.setRowSettings(labels, columnWidth, columnHeight, "colHeader");

        HBox printRow = new HBox();
        printRow.setPrefWidth(1056);
        printRow.setPrefHeight(50);
        printRow.setSpacing(5);
        printRow.setStyle("-fx-border-style: hidden hidden solid hidden; " +
                "-fx-border-color: grey;");
        printRow.getChildren().addAll(
                transactionIdLabel,
                timeLabel,
                flourPickupQtyLabel,
                grindingChargesLabel,
                grindingChargesPaidLabel,
                customerBalanceGrindingLabel,
                customerStoredWheatBalanceLabel,
                orderPickedByLabel
        );

        HBox colHeaderHBoxContainer = (HBox)(printPageVBox.getChildren().get(0));
        colHeaderHBoxContainer.getChildren().add(printRow);

    }

    private void printSetup(Node node, Stage owner,Printer printer)
    {
        PrinterJob job = PrinterJob.createPrinterJob();
        job.setPrinter(printer);
        Paper paper = job.getJobSettings().getPageLayout().getPaper();
        PageLayout pageLayout = printer.createPageLayout(
                paper,PageOrientation.LANDSCAPE,Printer.MarginType.DEFAULT
        );
        System.out.println("PageLayout: " + pageLayout);
        job.getJobSettings().setPageLayout(pageLayout);



        // Printable area
        double pWidth = pageLayout.getPrintableWidth();
        double pHeight = pageLayout.getPrintableHeight();
        System.out.println("Printable area is " + pWidth + " width and "
                + pHeight + " height.");

        // Node's (Image) dimensions
        double nWidth = node.getBoundsInParent().getWidth();
        double nHeight = node.getBoundsInParent().getHeight();
        System.out.println("Node's dimensions are " + nWidth + " width and "
                + nHeight + " height");

        // How much space is left? Or is the image to big?
        double widthLeft = pWidth - nWidth;
        double heightLeft = pHeight - nHeight;
        System.out.println("Width left: " + widthLeft
                + " height left: " + heightLeft);

        // scale the image to fit the page in width, height or both
        double scale = 0;

        if (widthLeft < heightLeft) {
            scale = pWidth / nWidth;
        } else {
            scale = pHeight / nHeight;
        }

        // preserve ratio (both values are the same)
        node.getTransforms().add(new Scale(scale, scale));

        // after scale you can check the size fit in the printable area
        double newWidth = node.getBoundsInParent().getWidth();
        double newHeight = node.getBoundsInParent().getHeight();
        System.out.println("New Node's dimensions: " + newWidth
                + " width " + newHeight + " height");


        if (job == null) {
            return;
        }

        boolean proceed = job.showPageSetupDialog(owner);

        if(proceed){
            print(job,node);
        }
    }


    private void print(PrinterJob job, Node node)
    {
        // Set the Job Status Message
//        jobStatus.textProperty().bind(job.jobStatusProperty().asString());

        // Print the node
        boolean printed = job.printPage(node);

        if (printed)
        {
            try {
                CustomerAccount.updatePrintedRow(customerId,nextPage);
            }
            catch (Exception e){
                e.getMessage();
            }

            job.endJob();

            stage.setScene(new Scene(fxWeaver.loadView(ContentController.class),1366,768));
            stage.setX(0);
            stage.setY(0);
            stage.show();
        }
        else{
            System.out.println("Printing failed....");
            stage.setScene(new Scene(fxWeaver.loadView(ContentController.class),1366,768));
            stage.setX(0);
            stage.setY(0);
            stage.show();
        }
    }

}
