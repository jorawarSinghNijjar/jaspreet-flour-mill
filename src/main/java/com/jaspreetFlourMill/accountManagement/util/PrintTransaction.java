package com.jaspreetFlourMill.accountManagement.util;

import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.util.List;

public class PrintTransaction {
    private Printer currentPrinter;
    private List<Printer> printerList;
    private String transactionId;
    private Node printNode;
    private Stage owner;

    public PrintTransaction(
            Printer currentPrinter,
            List<Printer> printerList,
            String transactionId,
            Node printNode,
            Stage owner
    ) {
        this.currentPrinter = currentPrinter;
        this.printerList = printerList;
        this.transactionId = transactionId;
        this.printNode = printNode;
        this.owner = owner;
    }

    private boolean printSetup(){
        boolean response = false;
        PrinterJob job = PrinterJob.createPrinterJob();
        job.setPrinter(currentPrinter);
        Paper paper = job.getJobSettings().getPageLayout().getPaper();
        PageLayout pageLayout = currentPrinter.createPageLayout(
                paper, PageOrientation.LANDSCAPE,Printer.MarginType.DEFAULT
        );
        System.out.println("PageLayout: " + pageLayout);
        job.getJobSettings().setPageLayout(pageLayout);



        // Printable area
        double pWidth = pageLayout.getPrintableWidth();
        double pHeight = pageLayout.getPrintableHeight();
        System.out.println("Printable area is " + pWidth + " width and "
                + pHeight + " height.");

        // Node's (Image) dimensions
        double nWidth = printNode.getBoundsInParent().getWidth();
        double nHeight = printNode.getBoundsInParent().getHeight();
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
        printNode.getTransforms().add(new Scale(scale, scale));

        // after scale you can check the size fit in the printable area
        double newWidth = printNode.getBoundsInParent().getWidth();
        double newHeight = printNode.getBoundsInParent().getHeight();
        System.out.println("New Node's dimensions: " + newWidth
                + " width " + newHeight + " height");


        if (job == null) {
            response = false;
        }

        boolean proceed = job.showPageSetupDialog(owner);
        if(proceed){
//            this.printPreview(printNode,job);
            response = true;
        }

        return response;
    }



    public Printer getCurrentPrinter() {
        return currentPrinter;
    }

    public void setCurrentPrinter(Printer currentPrinter) {
        this.currentPrinter = currentPrinter;
    }

    public List<Printer> getPrinterList() {
        return printerList;
    }

    public void setPrinterList(List<Printer> printerList) {
        this.printerList = printerList;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Node getPrintNode() {
        return printNode;
    }

    public void setPrintNode(Node printNode) {
        this.printNode = printNode;
    }

    public Stage getOwner() {
        return owner;
    }

    public void setOwner(Stage owner) {
        this.owner = owner;
    }
}
