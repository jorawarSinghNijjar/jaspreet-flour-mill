package com.jaspreetFlourMill.accountManagement.util;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons;
import jiconfont.javafx.IconFontFX;
import jiconfont.javafx.IconNode;

public class FormValidation {

    public FormValidation() {

    }

    public static ValidatedResponse isName(String name, Label responseLabel){
        String exp = "^[A-Za-z\\s]+$";
        boolean valid = name.matches(exp);
//        Label errTextLabel = new Label();

        if(valid){
            System.out.println("Validation success : " + name);
            return validationResponse(responseLabel,true);
        }
        else{
            System.out.println("Validation failed : " + name);
            return validationResponse(responseLabel,false);
        }

    }

    public static ValidatedResponse validationResponse(Label responseLabel, boolean valid){
        IconFontFX.register(GoogleMaterialDesignIcons.getIconFont());

        responseLabel.setText("");
        responseLabel.setGraphic(null);
        responseLabel.getStyleClass().clear();
        responseLabel.setWrapText(true);

        if(valid){
            responseLabel.setText("");
            IconNode correctIcon = new IconNode(GoogleMaterialDesignIcons.CHECK_CIRCLE);
            correctIcon.setIconSize(24);
            correctIcon.setFill(Color.web("#152769"));
            responseLabel.setGraphic(correctIcon);
            responseLabel.getStyleClass().add("validate-success");
            return new ValidatedResponse(responseLabel,true);
        }
        else{
            responseLabel.setText("Name can only have alphabets");
            IconNode wrongIcon = new IconNode(GoogleMaterialDesignIcons.HIGHLIGHT_OFF);
            wrongIcon.setIconSize(24);
            wrongIcon.setFill(Color.web("#fdca40"));
            responseLabel.setGraphic(wrongIcon);
            responseLabel.getStyleClass().add("validate-err");
            return new ValidatedResponse(responseLabel,false);
        }
    }
}
