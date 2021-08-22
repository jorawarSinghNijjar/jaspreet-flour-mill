package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.model.Sales;
import com.jaspreetFlourMill.accountManagement.model.SalesToday;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private NumberAxis xAxis;
    private NumberAxis yAxis;

    private XYChart.Series wheatSalesSeries;

    private XYChart.Series grindingChargesPaidSeries;

    private XYChart.Series grindingChargesSeries;

    private LineChart<Number,Number> lineChart;

    private int currentDisplaySalesMonth;

    private int currentDisplayYearForSalesMonth;

    private int currentDisplaySalesYear;

    private LocalDate todayDate;

    private int currentDay;

    private int currentMonth;

    private int currentYear;

    private int daysInCurrentMonth;

    private boolean salesMonthIsSelected;


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
        this.initializeLineChart();

        // Hide monthly and yearly comboBox
        this.setSalesMonthComboBoxVisibility(false);
        this.setSalesYearComboBoxVisibility(false);
        this.setSalesYearforMonthComboBoxVisibility(false);

        salesMonthIsSelected = false;

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
                            xAxis.setUpperBound(31);
                            break;

                        case "Yearly":
                            System.out.println("Showing sales report for year");
                            this.setSalesMonthComboBoxVisibility(false);
                            this.setSalesYearforMonthComboBoxVisibility(false);
                            this.setSalesYearComboBoxVisibility(true);
                            this.displaySalesforYear(currentYear);
                            xAxis.setLabel("Year");
                            xAxis.setUpperBound(12);
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
                    });

        salesYearforMonthComboBox.getSelectionModel().selectedItemProperty()
                .addListener((options,oldValue,newValue) ->{
                    currentDisplayYearForSalesMonth = Integer.parseInt(newValue.toString());
                    if(salesMonthIsSelected){
                        this.displaySalesForMonth(
                                currentDisplaySalesMonth, currentDisplayYearForSalesMonth
                        );
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.MONTH,currentDisplaySalesMonth-1);
                        xAxis.setLabel(
                                cal.getDisplayName(Calendar.MONTH,Calendar.LONG, Locale.getDefault())
                        );
                    }
                    else {
                        System.out.println("Please select year for the month");
                    }

                });


        salesYearComboBox.getSelectionModel().selectedItemProperty().
                addListener((options,oldValue,newValue) ->{
            currentDisplaySalesYear = Integer.parseInt(newValue.toString());
            this.displaySalesforYear(currentDisplaySalesYear);
        });



//        // Monthly sales get request
//        Sales[] salesForMonth = Sales.getSalesForMonth(
//                Integer.toString(month),Integer.toString(year)
//        );


//        for(int i=0; i < salesForMonth.length; i++ ){
//            monthlyWheatSold += salesForMonth[i].getTotalWheatSold();
//            monthlyGrindingAmountReceived += salesForMonth[i].getTotalGrindingChargesPaid();
//            monthlyGrindingAmount += salesForMonth[i].getTotalGrindingCharges();
//        }
//
//        //populating the series with data
//        for(int i=0; i < salesForMonth.length; i++){
//            double totalWheatSold = salesForMonth[i].getTotalWheatSold();
//            double grindingChargesPaid = salesForMonth[i].getTotalGrindingChargesPaid();
//            double grindingCharges = salesForMonth[i].getTotalGrindingCharges();
//            int dayForSale = salesForMonth[i].getDay();
//            wheatSalesSeries.getData().add(new XYChart.Data(dayForSale,totalWheatSold));
//            grindingChargesPaidSeries.getData().add(new XYChart.Data(dayForSale,grindingChargesPaid));
//            grindingChargesSeries.getData().add(new XYChart.Data(dayForSale,grindingCharges));
//        }

    }

    // Display Sales for today
    private void displaySalesForToday(String currentDay){
        try{
            lineChart.getData().clear();
            this.initializeSeries();
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
        try {
            lineChart.getData().clear();
            this.initializeSeries();
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
        try {
            lineChart.getData().clear();
            this.initializeSeries();
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

                wheatSoldDisplay.setText(String.valueOf(yearlyWheatSold));
                grindingChargesPaidDisplay.setText(String.valueOf(yearlyGrindingAmountReceived));
                grindingChargesDisplay.setText(String.valueOf(yearlyGrindingAmount));

                this.populateDataToChart(wheatSalesSeries,salesForYear,"YEAR");
                this.populateDataToChart(grindingChargesSeries,salesForYear,"YEAR");
                this.populateDataToChart(grindingChargesPaidSeries,salesForYear,"YEARLY");
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
        lineChart.getData().clear();
        return false;

    }

    private void initializeLineChart(){
// Initializing Line Graph
        xAxis = new NumberAxis();
        yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("Sales");

        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(30);
        xAxis.setTickUnit(1);
        xAxis.setMinorTickVisible(false);

        //Creating the chart
        lineChart = new LineChart<Number,Number>(xAxis,yAxis);

        lineChart.setTitle("Account Monitoring");

        this.initializeSeries();
        lineChart.setPrefSize(800,400);
        lineChart.setLayoutX(200);
        lineChart.setLayoutY(250);
        homeContainer.getChildren().add(lineChart);

    }

    private void initializeSeries(){
        //Defining a series
        wheatSalesSeries = new XYChart.Series();
        wheatSalesSeries.setName("Wheat Sold");
        grindingChargesPaidSeries = new XYChart.Series();
        grindingChargesPaidSeries.setName("Grinding Charges Paid");
        grindingChargesSeries = new XYChart.Series();
        grindingChargesSeries.setName("Grinding Charges");

        lineChart.getData().add(wheatSalesSeries);
        lineChart.getData().add(grindingChargesPaidSeries);
        lineChart.getData().add(grindingChargesSeries);

    }

    private void printTotalWheatSold(Sales[] sales){
        System.out.println("Total wheat sold in month:  " + sales[0].getMonth());
        for(Sales sale: sales){
            System.out.println(sale.getDate()+ " : " + sale.getTotalWheatSold());
        }
    }


}
