package com.jaspreetFlourMill.accountManagement.util;

import javafx.geometry.Dimension2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.joda.time.format.DateTimeFormat;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static javafx.scene.control.Alert.AlertType.INFORMATION;


public class Util {
    public static double titleLabelHeight = Util.getScreenHeight() * 0.05;

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

    // String ("dd MMM yyyy") to LocalDate

    public static LocalDate stringToLocalDate(String strDate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        return LocalDate.parse(strDate, formatter);
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

    public static Dimension2D getCenterSceneDim(Stage stage, double widthScale, double heightScale){
        double width = Util.getScreenWidth() / widthScale;
        double height = Util.getScreenHeight() / heightScale;
        stage.setX((Util.getScreenWidth() - width) / 2);
        stage.setY((Util.getScreenHeight() - height) / 2);
        return new Dimension2D(width,height);
    }

    public static boolean fieldIsEmpty(String string, String fieldName){
        if(string.trim().length() == 0){
            // Info Dialog
            AlertDialog alertDialog = new AlertDialog("INFO", String.format("Invalid %s", fieldName), String.format("Please enter %s. %s Field is empty!", fieldName),INFORMATION);
            alertDialog.showInformationDialog();
            return true;
        }
        return false;
    }

}
