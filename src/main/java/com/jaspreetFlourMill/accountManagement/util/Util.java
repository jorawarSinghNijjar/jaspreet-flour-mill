package com.jaspreetFlourMill.accountManagement.util;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Util {
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

    //Get's today's date in string

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


    // Rounds off to  2 decimal places
    public static double roundOff(double val){
        return Math.round(val * 100.0) / 100.0;
    }

}
