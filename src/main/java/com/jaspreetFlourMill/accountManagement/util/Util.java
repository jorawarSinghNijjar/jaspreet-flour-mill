package com.jaspreetFlourMill.accountManagement.util;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;

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

    // Rounds off to  2 decimal places
    public static double roundOff(double val){
        return Math.round(val * 100.0) / 100.0;
    }
}
