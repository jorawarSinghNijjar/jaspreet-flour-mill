package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.StageReadyEvent;
import com.jaspreetFlourMill.accountManagement.model.Transaction;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


import java.net.URL;
import java.util.ResourceBundle;

@Component
@FxmlView("/views/transactionDetailItem.fxml")
public class TransactionDetailItemController implements Initializable, ApplicationListener<StageReadyEvent> {
    private Stage stage;

    private FxControllerAndView<TransactionPrintPreviewController,Node> transactionPrintPreviewCV;

    @FXML
    private HBox transactionDetailItem;
    @FXML
    private Label transactionIdLabel;

    @FXML
    private Label timeStampLabel;

    @FXML
    private Label flourPickupQtyLabel;

    @FXML
    private Label grindingChargesLabel;

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

    @FXML
    private Label jobStatus;

    private Printer currentPrinter;

//    private String selectedTransactionId;

    @FXML
    private Button pageSetupBtn;
    private final FxWeaver fxWeaver;

    public TransactionDetailItemController(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        transactionPrintPreviewCV = fxWeaver.load(TransactionPrintPreviewController.class);
    }

    public  void setTransactionDetails(
            String transactionId, String time, double flourPickupQty,
            double grindingCharges,
            double grindingRate,
            double grindingChargesPaid, double grindingBalance,
            double storedWheatBalance, String orderPickedBy,
            String cashier
    ) {
        transactionIdLabel.setText(transactionId);
        timeStampLabel.setText(time);
        flourPickupQtyLabel.setText(String.valueOf(flourPickupQty));
        grindingChargesLabel.setText(String.valueOf(grindingCharges));
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

        jobStatus = new Label();

        // Create the Status Box
        HBox jobStatusBox = new HBox(5, new Label("Job Status: "), jobStatus);
//        pageSetupBtn = new Button("Page Setup");

        for(Printer printer: printers){
            listView.getItems().add(printer.getName());
        }

        listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                for(Printer printer: printers){
                    if(printer.getName().matches(listView.getSelectionModel().getSelectedItem().toString())){
                        currentPrinter = printer;
                        System.out.println("Current Printer: " + currentPrinter.getName());
                    }
                }
            }
        });
        VBox vBox = new VBox(10);
        Label label = new Label("Printers");
        Button printBtn = new Button("Print");
        vBox.getChildren().addAll(label,listView,printBtn,jobStatusBox);
        vBox.setPrefSize(400,250);
        vBox.setStyle("-fx-padding: 10;");

        Node node = (Node)e.getSource();
        Node selectedTransactionIdLabel = node.getParent().getChildrenUnmodifiable().get(0);
        String selectedTransactionId = ((Label)selectedTransactionIdLabel).getText();

        printBtn.setOnAction(PrintEvent -> {
            transactionPrintPreviewCV.getView().ifPresent(view -> {
                System.out.println(view);
                printSetup(view,stage,selectedTransactionId,currentPrinter);
            });
        });

        Scene scene = new Scene(vBox, 400,300);
        stage.setScene(scene);
        stage.show();
    }

    public void printSetup(Node node, Stage owner,String selectedTransactionId, Printer currentPrinter)
    {
        PrinterJob job = PrinterJob.createPrinterJob();
        job.setPrinter(currentPrinter);
        Paper paper = job.getJobSettings().getPageLayout().getPaper();
        PageLayout pageLayout = currentPrinter.createPageLayout(
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
            this.printPreview(node,job,selectedTransactionId, currentPrinter);
        }

    }

    private boolean printPreview(Node node, PrinterJob job, String selectedTransactionId, Printer currentPrinter){
        TransactionPrintPreviewController transactionPrintPreviewController =
                transactionPrintPreviewCV.getController();
        Transaction transaction = null;
        try{
             transaction = Transaction.getTransaction(selectedTransactionId);
        }
        catch(Exception e){
            e.getMessage();
        }
        if(transaction != null & currentPrinter !=null){
            System.out.println("Previewing Transaction"
                    + transaction.getTransactionId()
                    +" before printing...");
            stage.setScene(new Scene(fxWeaver.loadView(TransactionPrintPreviewController.class),1056,400));

            stage.show();
            transactionPrintPreviewController.populateTransactionRow(transaction,currentPrinter);

            return  true;
        }
        return false;
    }

    private void createPrintPageNode(Node node){

    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        this.stage = event.getStage();
    }
}
