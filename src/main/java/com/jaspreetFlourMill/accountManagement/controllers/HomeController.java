package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.model.Sales;
import com.jaspreetFlourMill.accountManagement.model.SalesToday;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("Sales");
        //creating the chart
        final LineChart<Number,Number> lineChart =
                new LineChart<>(xAxis,yAxis);

        lineChart.setTitle("Account Monitoring");
        //defining a series
        XYChart.Series wheatSalesSeries = new XYChart.Series();
        wheatSalesSeries.setName("Wheat");
        XYChart.Series grindingChargesPaidSeries = new XYChart.Series();
        grindingChargesPaidSeries.setName("Grinding Charges Paid");
        XYChart.Series grindingChargesSeries = new XYChart.Series();
        grindingChargesSeries.setName("Grinding Charges");

        // Get the number of days in that month
        LocalDate today = LocalDate.now();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String todayString = dateFormat.format(today);
        int day = today.getDayOfMonth();
        int month = today.getMonthValue();
        int year = today.getYear();
        YearMonth yearMonthObject = YearMonth.of(year, month);
        int daysInMonth = yearMonthObject.lengthOfMonth();

        // Monthly sales get request
        Sales[] salesForMonth = Sales.getSalesForMonth(
                Integer.toString(month),Integer.toString(year)
        );
        double monthlyWheatSold = 0.00;
        double monthlyGrindingAmountReceived =0.00;
        double monthlyGrindingAmount = 0.00;

        for(int i=0; i < salesForMonth.length; i++ ){
            monthlyWheatSold += salesForMonth[i].getTotalWheatSold();
            monthlyGrindingAmountReceived += salesForMonth[i].getTotalGrindingChargesPaid();
            monthlyGrindingAmount += salesForMonth[i].getTotalGrindingCharges();
        }

        //populating the series with data
        for(int i=0; i < salesForMonth.length; i++){
            double totalWheatSold = salesForMonth[i].getTotalWheatSold();
            double grindingChargesPaid = salesForMonth[i].getTotalGrindingChargesPaid();
            double grindingCharges = salesForMonth[i].getTotalGrindingCharges();
            int dayForSale = salesForMonth[i].getDay();
            wheatSalesSeries.getData().add(new XYChart.Data(dayForSale,totalWheatSold));
            grindingChargesPaidSeries.getData().add(new XYChart.Data(dayForSale,grindingChargesPaid));
            grindingChargesSeries.getData().add(new XYChart.Data(dayForSale,grindingCharges));
        }

        lineChart.getData().add(wheatSalesSeries);
        lineChart.getData().add(grindingChargesPaidSeries);
        lineChart.getData().add(grindingChargesSeries);


        homeContainer.getChildren().add(lineChart);

        try{
            Sales salesToday = Sales.getSalesForToday(todayString);
            System.out.println("Sales today: " + salesToday.getTotalWheatSold());
            wheatSoldDisplay.setText(salesToday.getTotalWheatSold().toString());
            grindingChargesPaidDisplay.setText(salesToday.getTotalGrindingChargesPaid().toString());
            grindingChargesDisplay.setText(salesToday.getTotalGrindingCharges().toString());
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

}
