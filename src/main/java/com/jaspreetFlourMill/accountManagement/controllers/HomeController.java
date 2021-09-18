package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.model.Sales;
import com.jaspreetFlourMill.accountManagement.util.Util;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@FxmlView("/views/home.fxml")
public class HomeController implements Initializable {

    @FXML
    private AnchorPane homeContainer;

    @FXML
    private Label wheatSoldDisplay;

    @FXML
    private Label grindingChargesPaidDisplay;

    @FXML
    private Label grindingChargesDisplay;

    @FXML
    private ComboBox salesSummaryTypeComboBox;

    @FXML
    private ComboBox salesMonthComboBox;

    @FXML
    private ComboBox salesYearforMonthComboBox;

    @FXML
    private ComboBox salesYearComboBox;

    private NumberAxis salesQtyXAxis;
    private NumberAxis salesQtyYAxis;

    private XYChart.Series wheatSalesSeries;

    private XYChart.Series grindingChargesPaidSeries;

    private XYChart.Series grindingChargesSeries;

    private LineChart<Number,Number> salesQtyChart;

    private int currentDisplaySalesMonth;

    private int currentDisplayYearForSalesMonth;

    private int currentDisplaySalesYear;

    private LocalDate todayDate;

    private int currentDay;

    private int currentMonth;

    private int currentYear;

    private int daysInCurrentMonth;

    private NumberAxis salesAmtXAxis;
    private NumberAxis salesAmtYAxis;
    private LineChart<Number, Number> salesAmtChart;

    private boolean salesMonthIsSelected;

    private boolean salesYearForMonthIsSelected;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Get the number of days in that month
        todayDate = LocalDate.now();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String currentDayStr = dateFormat.format(todayDate);
        currentDay = todayDate.getDayOfMonth();
        currentMonth = todayDate.getMonthValue();
        currentYear = todayDate.getYear();
        YearMonth yearMonthObject = YearMonth.of(currentYear, currentMonth);
        daysInCurrentMonth = yearMonthObject.lengthOfMonth();

        currentDisplaySalesMonth = currentMonth;

        // Initialize Line Chart
        this.initializeSalesQtyLineChart();
        this.initializeSalesAmtLineChart();

        // Hide monthly and yearly comboBox
        this.setSalesMonthComboBoxVisibility(false);
        this.setSalesYearComboBoxVisibility(false);
        this.setSalesYearforMonthComboBoxVisibility(false);

        salesMonthIsSelected = false;
        salesYearForMonthIsSelected = false;

        // Months add list
        ObservableList<Integer> months =
                FXCollections.observableArrayList(
                      1,2,3,4,5,6,7,8,9,10,11,12
                );
        salesMonthComboBox.getItems().addAll(months);

        // Years add list
        salesYearComboBox.getItems().addAll(this.getYears(2000));
        salesYearforMonthComboBox.getItems().addAll(this.getYears(2000));

        // Sales summary options (today, monthly, yearly)
        ObservableList<String> salesSummaryTypeOptions =
                FXCollections.observableArrayList(
                        "Today",
                        "Monthly",
                        "Yearly"
                );
        salesSummaryTypeComboBox.getItems().addAll(salesSummaryTypeOptions);

        // Event Listeners

        salesSummaryTypeComboBox.getSelectionModel().selectedItemProperty()
                .addListener((options,oldValue,newValue) ->{
                    switch (newValue.toString()){
                        case "Today":
                            System.out.println("Showing sales report for today");
                            this.setSalesMonthComboBoxVisibility(false);
                            this.setSalesYearComboBoxVisibility(false);
                            this.setSalesYearforMonthComboBoxVisibility(false);
                            this.displaySalesForToday(currentDayStr);
                            this.displaySalesForMonth(currentMonth,currentYear);

                            break;
                        case "Monthly":
                            System.out.println("Showing sales report for month");
                            this.setSalesMonthComboBoxVisibility(true);
                            this.setSalesYearComboBoxVisibility(false);
                            this.setSalesYearforMonthComboBoxVisibility(true);
                            this.displaySalesForMonth(currentMonth,currentYear);
                            salesQtyXAxis.setUpperBound(31);
                            salesAmtXAxis.setUpperBound(31);
                            break;

                        case "Yearly":
                            System.out.println("Showing sales report for year");
                            this.setSalesMonthComboBoxVisibility(false);
                            this.setSalesYearforMonthComboBoxVisibility(false);
                            this.setSalesYearComboBoxVisibility(true);
                            this.displaySalesforYear(currentYear);
                            salesQtyXAxis.setLabel("Year");
                            salesQtyXAxis.setUpperBound(12);
                            salesAmtXAxis.setLabel("Year");
                            salesAmtXAxis.setUpperBound(12);
                            break;

                        default:
                            System.out.println("Please select a valid option");
                    }
        });

        // Get month input from ComboBox

        salesMonthComboBox.getSelectionModel().selectedItemProperty()
                    .addListener((options,oldValue,newValue) ->{
                        currentDisplaySalesMonth = Integer.parseInt(newValue.toString());
                        salesMonthIsSelected = true;

                        if(salesYearForMonthIsSelected){
                            this.displaySalesForMonth(
                                    currentDisplaySalesMonth, currentDisplayYearForSalesMonth
                            );
                            Calendar cal = Calendar.getInstance();
                            cal.set(Calendar.MONTH,currentDisplaySalesMonth-1);
                            salesQtyXAxis.setLabel(
                                    cal.getDisplayName(Calendar.MONTH,Calendar.LONG, Locale.getDefault())
                            );
                            salesAmtXAxis.setLabel(
                                    cal.getDisplayName(Calendar.MONTH,Calendar.LONG, Locale.getDefault())
                            );
                        }
                        else {
                            System.out.println("Please select year for the month");
                        }

                    });

        salesYearforMonthComboBox.getSelectionModel().selectedItemProperty()
                .addListener((options,oldValue,newValue) ->{
                    currentDisplayYearForSalesMonth = Integer.parseInt(newValue.toString());
                    salesYearForMonthIsSelected = true;

                    if(salesMonthIsSelected){
                        this.displaySalesForMonth(
                                currentDisplaySalesMonth, currentDisplayYearForSalesMonth
                        );
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.MONTH,currentDisplaySalesMonth-1);
                        salesQtyXAxis.setLabel(
                                cal.getDisplayName(Calendar.MONTH,Calendar.LONG, Locale.getDefault())
                        );
                        salesAmtXAxis.setLabel(
                                cal.getDisplayName(Calendar.MONTH,Calendar.LONG, Locale.getDefault())
                        );
                    }
                    else {
                        System.out.println("Please select a month");
                    }

                });


        salesYearComboBox.getSelectionModel().selectedItemProperty().
                addListener((options,oldValue,newValue) ->{
            currentDisplaySalesYear = Integer.parseInt(newValue.toString());
            this.displaySalesforYear(currentDisplaySalesYear);
        });

    }


    // Display Sales for today
    private void displaySalesForToday(String currentDay){
        try{
            salesQtyChart.getData().clear();
            salesAmtChart.getData().clear();
            this.initializeSalesQtySeries();
            this.initializeSalesAmtSeries();
            Sales salesToday = Sales.getSalesForToday(currentDay);

            if(salesToday!=null){
                wheatSoldDisplay.setText(salesToday.getTotalWheatSold().toString());
                grindingChargesPaidDisplay.setText(salesToday.getTotalGrindingChargesPaid().toString());
                grindingChargesDisplay.setText(salesToday.getTotalGrindingCharges().toString());
            }
            else{
                System.out.println("No sales made today");
                wheatSoldDisplay.setText("0.00");
                grindingChargesPaidDisplay.setText("0.00");
                grindingChargesDisplay.setText("0.00");
            }

        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    // Display Sales for month
    private void displaySalesForMonth(int month,int year){
        System.out.println("displaySalesForMonth()");
        try {
            salesQtyChart.getData().clear();
            salesAmtChart.getData().clear();
            this.initializeSalesQtySeries();
            this.initializeSalesAmtSeries();
            Sales[] salesForMonth = Sales.getSalesForMonth(month,year);
//            this.printTotalWheatSold(salesForMonth);

            if(salesForMonth != null && salesForMonth.length !=0){
                double monthlyWheatSold = 0.00;
                double monthlyGrindingAmountReceived =0.00;
                double monthlyGrindingAmount = 0.00;

                for(Sales sale: salesForMonth){
                    monthlyWheatSold+=sale.getTotalWheatSold();
                    monthlyGrindingAmount+=sale.getTotalGrindingCharges();
                    monthlyGrindingAmountReceived+=sale.getTotalGrindingChargesPaid();
                }

                // Round off to 2 decimal places
                monthlyWheatSold = Util.roundOff(monthlyWheatSold);
                monthlyGrindingAmount = Util.roundOff(monthlyGrindingAmount);
                monthlyGrindingAmountReceived = Util.roundOff(monthlyGrindingAmountReceived);

                wheatSoldDisplay.setText(String.valueOf(monthlyWheatSold));
                grindingChargesPaidDisplay.setText(String.valueOf(monthlyGrindingAmountReceived));
                grindingChargesDisplay.setText(String.valueOf(monthlyGrindingAmount));

                this.populateDataToChart(wheatSalesSeries,salesForMonth,"MONTH");
                this.populateDataToChart(grindingChargesSeries,salesForMonth,"MONTH");
                this.populateDataToChart(grindingChargesPaidSeries,salesForMonth,"MONTH");
            }
            else{
                System.out.println("No sales found in month: " + month);
                wheatSoldDisplay.setText("0.00");
                grindingChargesPaidDisplay.setText("0.00");
                grindingChargesDisplay.setText("0.00");
            }

        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

    }
    // Display Sales for year
    private void displaySalesforYear(int year){
        System.out.println("displaySalesForYear()");
        try {
            salesQtyChart.getData().clear();
            salesAmtChart.getData().clear();
            this.initializeSalesQtySeries();
            this.initializeSalesAmtSeries();
            Sales[] salesForYear = Sales.getSalesForYear(year);

            if(salesForYear != null && salesForYear.length !=0){

                double yearlyWheatSold = 0.00;
                double yearlyGrindingAmountReceived =0.00;
                double yearlyGrindingAmount = 0.00;

                for(Sales sale: salesForYear){
                    yearlyWheatSold+=sale.getTotalWheatSold();
                    yearlyGrindingAmount+=sale.getTotalGrindingCharges();
                    yearlyGrindingAmountReceived+=sale.getTotalGrindingChargesPaid();
                }

                // Round off to 2 decimal places
                yearlyWheatSold = Util.roundOff(yearlyWheatSold);
                yearlyGrindingAmount = Util.roundOff(yearlyGrindingAmount);
                yearlyGrindingAmountReceived = Util.roundOff(yearlyGrindingAmountReceived);

                wheatSoldDisplay.setText(String.valueOf(yearlyWheatSold));
                grindingChargesPaidDisplay.setText(String.valueOf(yearlyGrindingAmountReceived));
                grindingChargesDisplay.setText(String.valueOf(yearlyGrindingAmount));

                this.populateDataToChart(wheatSalesSeries,salesForYear,"YEAR");
                this.populateDataToChart(grindingChargesSeries,salesForYear,"YEAR");
                this.populateDataToChart(grindingChargesPaidSeries,salesForYear,"YEAR");
            }
            else{
                System.out.println("No sales found in year: " + year);
                wheatSoldDisplay.setText("0.00");
                grindingChargesPaidDisplay.setText("0.00");
                grindingChargesDisplay.setText("0.00");
            }

        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    // Toggles comboBox visibility
    private void setSalesYearComboBoxVisibility(boolean visible){
        salesYearComboBox.setVisible(visible);
        salesYearComboBox.setManaged(visible);
    }

    private void setSalesMonthComboBoxVisibility(boolean visible){
        salesMonthComboBox.setVisible(visible);
        salesMonthComboBox.setManaged(visible);
    }

    private void setSalesYearforMonthComboBoxVisibility(boolean visible){
        salesYearforMonthComboBox.setVisible(visible);
        salesYearforMonthComboBox.setManaged(visible);
    }

    // Get years from 1900 to current year
    private ObservableList<Integer> getYears(int startYear){
        LocalDate today = LocalDate.now();
        int endYear = today.getYear();

        ObservableList<Integer> yearsList = FXCollections.observableArrayList();
        while(startYear <= endYear){
            yearsList.add(startYear);
            startYear++;
        }

        return yearsList;
    }

    // Method to populate data to Line Chart
    private boolean populateDataToChart(XYChart.Series series, Sales[] sales, String xAxisType){
        String seriesName = series.getName();
        if(sales!=null && sales.length!=0){
            System.out.println(sales[0].getDay() + "-" +sales[0].getMonth()
                    + "-"+sales[0].getYear() + " Total wheat sold : "
                    + sales[0].getTotalWheatSold());
            double data = 0.00;

            for(int i=0; i < sales.length; i++){
                switch (seriesName){
                    case "Wheat Sold":

                        data = sales[i].getTotalWheatSold();
                        break;
                    case "Grinding Charges Paid":
                        data = sales[i].getTotalGrindingChargesPaid();
                        break;
                    case "Grinding Charges":
                        data = sales[i].getTotalGrindingCharges();
                        break;
                    default:
                        System.out.println("No series found for: "+seriesName);
                }
                int dayForSale = sales[i].getDay();
                int monthForSale = sales[i].getMonth();

                switch (xAxisType){
                    case "MONTH" :
                        series.getData().add(new XYChart.Data(dayForSale,data));
                        break;

                    case "YEAR":
                        series.getData().add(new XYChart.Data(monthForSale,data));
                        break;
                    default:
                        System.out.println("No X-Axis type found");
                }


            }
            return true;
        }
        System.out.println("No sales for this month or year");
        salesQtyChart.getData().clear();
        salesAmtChart.getData().clear();
        return false;

    }

    private void initializeSalesQtyLineChart(){
// Initializing Line Graph
        salesQtyXAxis = new NumberAxis();
        salesQtyYAxis = new NumberAxis();
        salesQtyXAxis.setLabel("Date");
        salesQtyYAxis.setLabel("Quantity");

        salesQtyXAxis.setAutoRanging(false);
        salesQtyXAxis.setLowerBound(0);
        salesQtyXAxis.setUpperBound(30);
        salesQtyXAxis.setTickUnit(1);
        salesQtyXAxis.setMinorTickVisible(false);

        //Creating the chart
        salesQtyChart = new LineChart<Number,Number>(salesQtyXAxis, salesQtyYAxis);

        salesQtyChart.setTitle("Sales By Quantity");

        this.initializeSalesQtySeries();
        salesQtyChart.setPrefSize(400,400);
        salesQtyChart.setLayoutX(50);
        salesQtyChart.setLayoutY(250);
        homeContainer.getChildren().add(salesQtyChart);

    }

    private void initializeSalesAmtLineChart() {
        // Initializing Line Graph
        salesAmtXAxis = new NumberAxis();
        salesAmtYAxis = new NumberAxis();
        salesAmtXAxis.setLabel("Date");
        salesAmtYAxis.setLabel("Amount");

        salesAmtXAxis.setAutoRanging(false);
        salesAmtXAxis.setLowerBound(0);
        salesAmtXAxis.setUpperBound(30);
        salesAmtXAxis.setTickUnit(1);
        salesAmtXAxis.setMinorTickVisible(false);

        //Creating the chart
        salesAmtChart = new LineChart<Number,Number>(salesAmtXAxis, salesAmtYAxis);

        salesAmtChart.setTitle("Sales By Amount");

        this.initializeSalesAmtSeries();
        salesAmtChart.setPrefSize(400,400);
        salesAmtChart.setLayoutX(500);
        salesAmtChart.setLayoutY(250);
        homeContainer.getChildren().add(salesAmtChart);

    }


    private void initializeSalesQtySeries(){
        //Defining a series
        wheatSalesSeries = new XYChart.Series();
        wheatSalesSeries.setName("Wheat Sold");
//        grindingChargesPaidSeries = new XYChart.Series();
//        grindingChargesPaidSeries.setName("Grinding Charges Paid");
//        grindingChargesSeries = new XYChart.Series();
//        grindingChargesSeries.setName("Grinding Charges");

        salesQtyChart.getData().add(wheatSalesSeries);
//        salesQtyChart.getData().add(grindingChargesPaidSeries);
//        salesQtyChart.getData().add(grindingChargesSeries);

    }

    private void initializeSalesAmtSeries(){
        //Defining a series
        grindingChargesPaidSeries = new XYChart.Series();
        grindingChargesPaidSeries.setName("Grinding Charges Paid");
        grindingChargesSeries = new XYChart.Series();
        grindingChargesSeries.setName("Grinding Charges");

        salesAmtChart.getData().add(grindingChargesPaidSeries);
        salesAmtChart.getData().add(grindingChargesSeries);

    }



    private void printTotalWheatSold(Sales[] sales){
        System.out.println("Total wheat sold in month:  " + sales[0].getMonth());
        for(Sales sale: sales){
            System.out.println(sale.getDate()+ " : " + sale.getTotalWheatSold());
        }
    }


}
