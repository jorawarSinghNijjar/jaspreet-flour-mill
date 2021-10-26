package com.jaspreetFlourMill.accountManagement.util;

import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.stage.Screen;
import org.joda.time.format.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


public class Util {
    public static double titleLabelHeight = Util.getScreenHeight() * 0.05;
    private static final String baseUri = "http://localhost:8080";

    public static String getBaseUri() {
        return baseUri;
    }

    public static Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }

    //Get today's date in string

    public static String getDateForToday(){
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return dateFormat.format(dateTime);
    }

    public static String getDateForYesterday(){
        LocalDateTime dateTime = LocalDateTime.now();
        dateTime = dateTime.minusDays(1);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return dateFormat.format(dateTime);
    }

    // DATE TIME format conversion

    public static String usToIndDateFormat(String inputDate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
        LocalDate date = LocalDate.parse(inputDate, formatter);
        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH);
        return outputFormat.format(date);
    }

    public static String usToIndTimeFormat(String inputTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.ENGLISH);
        LocalTime time = LocalTime.parse(inputTime, formatter);
        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("hh:mm:ss a", Locale.ENGLISH);
        return outputFormat.format(time);
    }


    // Rounds off to  2 decimal places
    public static double roundOff(double val){
        return Math.round(val * 100.0) / 100.0;
    }

    // Screen bounds Getter
    // Note: getVisualBounds() uncovers the taskbar
    public static double getScreenWidth(){
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        return screenBounds.getWidth();
    }

    public static double getScreenHeight(){
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        return screenBounds.getHeight();
    }

    public static double getContentAreaWidth(){
        return Util.getScreenWidth() * 0.85;
    }

    public static double getContentAreaHeight(){
        return Util.getScreenHeight();
    }

}
