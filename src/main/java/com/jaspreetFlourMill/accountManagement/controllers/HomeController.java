package com.jaspreetFlourMill.accountManagement.controllers;

import animatefx.animation.FadeInLeft;
import animatefx.animation.FadeInRight;
import animatefx.animation.FadeOutLeft;
import animatefx.animation.FadeOutRight;
import com.jaspreetFlourMill.accountManagement.model.MonthlySales;
import com.jaspreetFlourMill.accountManagement.model.Sales;
import com.jaspreetFlourMill.accountManagement.util.AlertDialog;
import com.jaspreetFlourMill.accountManagement.util.Util;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons;
import jiconfont.javafx.IconFontFX;
import jiconfont.javafx.IconNode;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@FxmlView("/views/home.fxml")
public class HomeController implements Initializable {

    @FXML
    protected AnchorPane homeContainer;

    @FXML
    protected VBox homeVBoxContainer;

    @FXML
    protected HBox homeHBoxContainer;

    @FXML
    private Label wheatSoldDisplay;

    @FXML
    private Label wheatBalanceDisplay;

    @FXML
    private Label wheatDepositDisplay;

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

    @FXML
    protected Label leftArrow;

    @FXML
    protected Label rightArrow;

    @FXML
    protected GridPane lineChartGridPane;

    @FXML
    protected AnchorPane lineChartContainer;

    private NumberAxis salesQtyXAxis;
    private NumberAxis salesQtyYAxis;

    private XYChart.Series wheatSalesSeries;

    private XYChart.Series wheatBalanceSeries;

    private XYChart.Series wheatDepositSeries;

    private XYChart.Series grindingChargesPaidSeries;

    private XYChart.Series grindingChargesSeries;

    protected LineChart<Number,Number> salesQtyChart;

    protected LineChart<Number, Number> salesAmtChart;

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


    private boolean salesMonthIsSelected;

    private boolean salesYearForMonthIsSelected;

    int displayChartIndex = 0;


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

        // Arrow Icons
        IconFontFX.register(GoogleMaterialDesignIcons.getIconFont());
        IconNode chevronRight = new IconNode(GoogleMaterialDesignIcons.CHEVRON_RIGHT);
        chevronRight.setIconSize(32);
        chevronRight.setStyle("-fx-font-size: 90px;");

        IconNode chevronLeft = new IconNode(GoogleMaterialDesignIcons.CHEVRON_LEFT);
        chevronLeft.setIconSize(32);
        chevronLeft.setStyle("-fx-font-size: 90px;");

        leftArrow.setGraphic(chevronLeft);
        rightArrow.setGraphic(chevronRight);

        leftArrow.setTranslateY(-50);
        rightArrow.setTranslateY(-50);

        // For animation
        salesQtyChart.setOpacity(0);

        List<LineChart> charts = new ArrayList<>();
        charts.add(salesAmtChart);
        charts.add(salesQtyChart);

        rightArrow.setOnMouseClicked((e) -> {
            new FadeOutRight(charts.get(displayChartIndex)).play();
            displayChartIndex++;
            if(displayChartIndex > (charts.size()-1)){
                displayChartIndex = 0;
            }
            new FadeInLeft(charts.get(displayChartIndex)).play();
        });

        leftArrow.setOnMouseClicked((e) -> {
            new FadeOutLeft(charts.get(displayChartIndex)).play();
            displayChartIndex++;
            if(displayChartIndex > (charts.size()-1)){
                displayChartIndex = 0;
            }
            new FadeInRight(charts.get(displayChartIndex)).play();
        });


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
        System.out.println(currentDay);
            salesQtyChart.getData().clear();
            salesAmtChart.getData().clear();
            this.initializeSalesQtySeries();
            this.initializeSalesAmtSeries();
            Sales.getSalesForDate(currentDay).ifPresent( salesToday -> {
                if(salesToday!=null){
                    wheatSoldDisplay.setText(salesToday.getTotalWheatSold().toString());
                    wheatBalanceDisplay.setText(salesToday.getTotalStoredWheatBalance().toString());
                    wheatDepositDisplay.setText(salesToday.getTotalWheatDeposited().toString());
                    grindingChargesPaidDisplay.setText(salesToday.getTotalGrindingChargesPaid().toString());
                    grindingChargesDisplay.setText(salesToday.getTotalGrindingCharges().toString());
                }
                else{
                    System.out.println("No sales made today");
                    wheatSoldDisplay.setText("0.00");
                    grindingChargesPaidDisplay.setText("0.00");
                    grindingChargesDisplay.setText("0.00");
                }
            });
    }

    // Display Sales for month
    private void displaySalesForMonth(int month,int year){
        System.out.println("displaySalesForMonth()");

            salesQtyChart.getData().clear();
            salesAmtChart.getData().clear();
            this.initializeSalesQtySeries();
            this.initializeSalesAmtSeries();
            Sales.getSalesForMonth(month,year).ifPresent(salesForMonth -> {
                if(salesForMonth != null && salesForMonth.length !=0){
                    double monthlyWheatSold = 0.00;
                    double monthlyGrindingAmountReceived =0.00;
                    double monthlyGrindingAmount = 0.00;

                    for(Sales sale: salesForMonth){
                        monthlyWheatSold+=sale.getTotalWheatSold();
                        monthlyGrindingAmount+=sale.getTotalGrindingCharges();
                        monthlyGrindingAmountReceived+=sale.getTotalGrindingChargesPaid();
                        System.out.println(sale);
                    }

                    // Round off to 2 decimal places
                    monthlyWheatSold = Util.roundOff(monthlyWheatSold);
                    monthlyGrindingAmount = Util.roundOff(monthlyGrindingAmount);
                    monthlyGrindingAmountReceived = Util.roundOff(monthlyGrindingAmountReceived);

                    wheatSoldDisplay.setText(String.valueOf(monthlyWheatSold));
                    grindingChargesPaidDisplay.setText(String.valueOf(monthlyGrindingAmountReceived));
                    grindingChargesDisplay.setText(String.valueOf(monthlyGrindingAmount));

                    this.populateDailyDataToChart(wheatSalesSeries,salesForMonth,"MONTH");
                    this.populateDailyDataToChart(wheatBalanceSeries,salesForMonth,"MONTH");
                    this.populateDailyDataToChart(wheatDepositSeries,salesForMonth,"MONTH");
                    this.populateDailyDataToChart(grindingChargesSeries,salesForMonth,"MONTH");
                    this.populateDailyDataToChart(grindingChargesPaidSeries,salesForMonth,"MONTH");

                }
                else{
                    System.out.println("No sales found in month: " + month);
                    wheatSoldDisplay.setText("0.00");
                    grindingChargesPaidDisplay.setText("0.00");
                    grindingChargesDisplay.setText("0.00");
                }
            });
    }
    // Display Sales for year
    private void displaySalesforYear(int year){

            salesQtyChart.getData().clear();
            salesAmtChart.getData().clear();
            this.initializeSalesQtySeries();
            this.initializeSalesAmtSeries();

            Map<Integer,MonthlySales> monthlySales = new HashMap<>();
            for(int i=1; i <= 12; i++){

                Sales[] salesForMonth = Sales.getSalesForMonth(i,year).orElseThrow();

                monthlySales.put(i,new MonthlySales(i,salesForMonth));
            }

            Sales.getSalesForYear(year).ifPresentOrElse(salesForYear -> {
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

                    this.populateMonthlyDataToChart(wheatSalesSeries,monthlySales,"YEAR");
                    this.populateMonthlyDataToChart(wheatDepositSeries,monthlySales,"YEAR");
                    this.populateMonthlyDataToChart(grindingChargesSeries,monthlySales,"YEAR");
                    this.populateMonthlyDataToChart(grindingChargesPaidSeries,monthlySales,"YEAR");
                }

            }, () ->{
                System.out.println("No sales found in year: " + year);
                wheatSoldDisplay.setText("0.00");
                grindingChargesPaidDisplay.setText("0.00");
                grindingChargesDisplay.setText("0.00");
            });
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
    private boolean populateDailyDataToChart(XYChart.Series series, Sales[] sales, String xAxisType){
        String seriesName = series.getName();
        if(sales!=null && sales.length!=0){
//            System.out.println(sales[0].getDay() + "-" +sales[0].getMonth()
//                    + "-"+sales[0].getYear() + " Total wheat sold : "
//                    + sales[0].getTotalWheatSold());
            double data = 0.00;

            for(int i=0; i < sales.length; i++){
                switch (seriesName){
                    case "Wheat Sold":
                        data = sales[i].getTotalWheatSold();
                        break;
                    case "Wheat Balance":
                        data = sales[i].getTotalStoredWheatBalance();
                        break;
                    case "Wheat Deposit":
                        data = sales[i].getTotalWheatDeposited();
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

                series.getData().add(new XYChart.Data(dayForSale,data));
            }
            return true;
        }
        System.out.println("No sales for this month or year");
        salesQtyChart.getData().clear();
        salesAmtChart.getData().clear();
        return false;

    }

    private boolean populateMonthlyDataToChart(XYChart.Series series, Map<Integer,MonthlySales> salesForMonth,
                                               String xAxisType){
        String seriesName = series.getName();
        if(!salesForMonth.isEmpty()){
            double data = 0.00;

            for (Map.Entry<Integer, MonthlySales> entry : salesForMonth.entrySet()) {
                int month = entry.getKey();
                switch (seriesName){
                    case "Wheat Sold":
                        data = entry.getValue().getTotalWheatSold();
                        break;
                    case "Wheat Deposit":
                        data = entry.getValue().getTotalWheatDeposited();
                        break;
                    case "Grinding Charges Paid":
                        data = entry.getValue().getTotalGrindingAmountReceived();
                        break;
                    case "Grinding Charges":
                        data = entry.getValue().getTotalGrindingAmount();
                        break;
                    default:
                        System.out.println("No series found for: "+ seriesName);
                }
                series.getData().add(new XYChart.Data(month,data));
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
//        salesQtyChart.setPrefSize(lineChartContainer.getPrefWidth(),lineChartContainer.getHeight());
        lineChartContainer.getChildren().add(salesQtyChart);
//        salesQtyChart.setLayoutX(50);
//        salesQtyChart.setLayoutY(200);
//        homeContainer.getChildren().add(salesQtyChart);

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
//        salesAmtChart.setPrefSize(lineChartContainer.getPrefWidth(),lineChartContainer.getHeight());
        lineChartContainer.getChildren().add(salesAmtChart);
//        salesAmtChart.setLayoutX(50);
//        salesAmtChart.setLayoutY(200);
//        homeContainer.getChildren().add(salesAmtChart);

    }


    private void initializeSalesQtySeries(){
        //Defining series
        wheatSalesSeries = new XYChart.Series();
        wheatBalanceSeries = new XYChart.Series();
        wheatDepositSeries = new XYChart.Series();

        wheatSalesSeries.setName("Wheat Sold");
        wheatBalanceSeries.setName("Wheat Balance");
        wheatDepositSeries.setName("Wheat Deposited");

        salesQtyChart.getData().add(wheatSalesSeries);
        salesQtyChart.getData().add(wheatBalanceSeries);
        salesQtyChart.getData().add(wheatDepositSeries);

    }

    private void initializeSalesAmtSeries(){
        //Defining series
        grindingChargesPaidSeries = new XYChart.Series();
        grindingChargesPaidSeries.setName("Grinding Charges Received");
        grindingChargesSeries = new XYChart.Series();
        grindingChargesSeries.setName("Grinding Charges Total");

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
