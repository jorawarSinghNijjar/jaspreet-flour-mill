package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.StageReadyEvent;
import com.jaspreetFlourMill.accountManagement.model.CustomerAccount;
import com.jaspreetFlourMill.accountManagement.model.Transaction;
import com.jaspreetFlourMill.accountManagement.util.AlertDialog;
import com.jaspreetFlourMill.accountManagement.util.Util;
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

    private static final int printRowsMax = 6;
    private boolean nextPage;
    private static final double columnWidth = 132;
    private static final double columnHeight = 80;
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
                , transactionPrintPreviewContainer.getPrefHeight()
        );
        double vBoxHeight = printPageVBox.getPrefHeight();
        double heightSum = 0;
        while (heightSum <= vBoxHeight) {
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

    public boolean populateTransactionRow(Transaction transaction, Printer printer) {
        System.out.println("Printing row :" + currentPrintRow);

        try {
            customerId = transaction.getCustomer().getCustomerId();
            CustomerAccount customerAccount = CustomerAccount.get(customerId).orElseThrow();
            currentPrintRow = customerAccount.getRowsPrinted();
            currentPrintRow++;
            if (currentPrintRow > printRowsMax) {
                nextPage = true;
            }
        } catch (Exception e) {
            e.getMessage();
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error",e.getCause().getMessage(),e.getMessage(),Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
        }


        HBox printRow = new HBox();
        printRow.setPrefWidth(1056);
        printRow.setPrefHeight(50);
        printRow.setSpacing(5);
        printRow.setStyle("-fx-border-style: hidden hidden solid hidden; " +
                "-fx-border-color: grey;");
//        Label transactionIdLabel = new Label(transaction.getTransactionId());
//        Label sNo = new Label();

        // Change of Date Time format from US to IND
        String displayDate = Util.usToIndDateFormat(transaction.getDate());
        String displayTime = Util.usToIndTimeFormat(transaction.getTime());

        Label timeLabel = new Label(displayDate + "\n" + displayTime);

        String flourPickupQty = String.valueOf(transaction.getFlourPickupQty());
        String grindingCharges = String.valueOf(transaction.getGrindingCharges());
        String grindingChargesPaid = String.valueOf(transaction.getGrindingChargesPaid());
        String customerBalanceGrinding = String.valueOf(transaction.getCustomerBalanceGrindingCharges());
        String customerStoredWheatBalance = String.valueOf(transaction.getCustomerStoredFlourBalanceQty());

        Label flourPickupQtyLabel = new Label(flourPickupQty + " kg");
        Label grindingChargesLabel = new Label("₹ " + grindingCharges);
        Label grindingChargesPaidLabel = new Label("₹ " + grindingChargesPaid);
        Label customerBalanceGrindingLabel = new Label("₹ " + customerBalanceGrinding);
        Label customerStoredWheatBalanceLabel = new Label(customerStoredWheatBalance + " kg");
        Label orderPickedByLabel = new Label(transaction.getOrderPickedBy());

        List<Label> labels = new ArrayList<>();
//            labels.add(transactionIdLabel);
        labels.add(timeLabel);
        labels.add(flourPickupQtyLabel);
        labels.add(grindingChargesLabel);
        labels.add(grindingChargesPaidLabel);
        labels.add(customerBalanceGrindingLabel);
        labels.add(customerStoredWheatBalanceLabel);
        labels.add(orderPickedByLabel);

        this.setRowSettings(labels, columnWidth, columnHeight, "data");

        printRow.getChildren().addAll(
//                transactionIdLabel,
                timeLabel,
                flourPickupQtyLabel,
                grindingChargesLabel,
                grindingChargesPaidLabel,
                customerBalanceGrindingLabel,
                customerStoredWheatBalanceLabel,
                orderPickedByLabel);

        try {
            HBox hboxContainer = (HBox) (printPageVBox.getChildren().get(currentPrintRow));
            if (hboxContainer != null) {
                hboxContainer.getChildren().add(printRow);
            }

            if (currentPrintRow == 1) {
                this.printColumnHeaders();
            }

            this.printSetup(transactionPrintPreviewContainer, stage, printer);

            return true;
        } catch (IndexOutOfBoundsException e) {
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

    private void setRowSettings(List<Label> labels, double width, double height, String type) {
        for (Label label : labels) {
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
            if (type == "colHeader") {
                label.setStyle("-fx-font-weight: bold;");
            }
        }
    }

    private void printColumnHeaders() {
//        Label transactionIdLabel = new Label("Transaction ID");
//        Label sNo = new Label("S.No.");
        Label timeLabel = new Label("ਸਮਾਂ" + "\n" + "Time");
        Label flourPickupQtyLabel = new Label("ਆਟਾ ਪ੍ਰਾਪਤ" + "\n" + "Flour Pickup Qty");
        Label grindingChargesLabel = new Label("ਪੀਸਾਈ ਰਕਮ" + "\n" + "Grinding Amount");
        Label grindingChargesPaidLabel = new Label("ਰਕਮ ਅਦਾ ਕੀਤੀ" + "\n" + "Amount Paid");
        Label customerBalanceGrindingLabel = new Label("ਬਾਕੀ ਰਕਮ" + "\n" + "Unpaid Amount");
        Label customerStoredWheatBalanceLabel = new Label("ਬਾਕੀ ਕਣਕ"+ "\n" +"Account Wheat Balance");
        Label orderPickedByLabel = new Label("ਪ੍ਰਾਪਤ ਕਰਤਾ"+ "\n" +"Order Picked By");


        List<Label> labels = new ArrayList<>();
//        labels.add(transactionIdLabel);
//        labels.add(sNo);
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
//                transactionIdLabel,
                timeLabel,
                flourPickupQtyLabel,
                grindingChargesLabel,
                grindingChargesPaidLabel,
                customerBalanceGrindingLabel,
                customerStoredWheatBalanceLabel,
                orderPickedByLabel
        );

        HBox colHeaderHBoxContainer = (HBox) (printPageVBox.getChildren().get(0));
        colHeaderHBoxContainer.getChildren().add(printRow);

    }

    private void printSetup(Node node, Stage owner, Printer printer) {
        PrinterJob job = PrinterJob.createPrinterJob();
        job.setPrinter(printer);
        Paper paper = job.getJobSettings().getPageLayout().getPaper();
        PageLayout pageLayout = printer.createPageLayout(
                paper, PageOrientation.LANDSCAPE, Printer.MarginType.DEFAULT
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

        if (proceed) {
            print(job, node);
        }
    }


    private void print(PrinterJob job, Node node) {
        // Set the Job Status Message

        // Print the node
        boolean printed = job.printPage(node);

        if (printed) {
            try {
                CustomerAccount.updatePrintedRow(customerId, nextPage);
            } catch (Exception e) {
                e.getMessage();
            }

            job.endJob();
            System.out.println("Loading Dashboard ....");
            stage.setScene(new Scene(fxWeaver.loadView(ContentController.class), Util.getScreenWidth(), Util.getScreenHeight()));
            stage.setX(0);
            stage.setY(0);
            stage.setMaximized(true);
            stage.show();
        } else {
            stage.setScene(new Scene(fxWeaver.loadView(ContentController.class), Util.getScreenWidth(), Util.getScreenHeight()));
            stage.setX(0);
            stage.setY(0);
            stage.setMaximized(true);
            stage.show();
            // Error dialog
            AlertDialog alertDialog = new AlertDialog("Error","Printing failed !!!", "Something went wrong! Try again or contact customer support", Alert.AlertType.ERROR);
            alertDialog.showInformationDialog();
        }
    }

}
