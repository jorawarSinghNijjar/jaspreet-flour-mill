package com.jaspreetFlourMill.accountManagement.util;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons;
import jiconfont.javafx.IconFontFX;
import jiconfont.javafx.IconNode;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormValidation {

    private Map<String,Boolean> formFields;

    public FormValidation() {
        formFields = new HashMap<String,Boolean>();
    }

    public Map<String, Boolean> getFormFields() {
        return formFields;
    }


    public static ValidatedResponse isName(String name, Label responseLabel){
        String exp = "^[A-Za-z\\s]+$";
        boolean valid = name.matches(exp);
        String msg = "Only alphabets and spaces allowed";
        return validationResponse(responseLabel,valid,msg);
    }

    public static ValidatedResponse isPhoneNumber(String phoneNumber, Label responseLabel){
        String exp = "((\\+*)((0[ -]*)*|((91 )*))((\\d{12})+|(\\d{10})+))|\\d{5}([- ]*)\\d{6}";
        boolean valid = phoneNumber.matches(exp);
        String msg = "Invalid Phone number";
        return validationResponse(responseLabel,valid,msg);
    }

    public static ValidatedResponse isAddress(String address, Label responseLabel){
        boolean valid = address.isEmpty() ? false : true;
        String msg = "Please enter a valid address";
        return validationResponse(responseLabel,valid,msg);
    }


    public static ValidatedResponse isAdhaarNumber(String adhaarNumber, Label responseLabel){
        String exp = "^[0-9]{4}(\\s?|-?)[0-9]{4}(\\s?|-?)[0-9]{4}$";
        boolean valid = adhaarNumber.matches(exp);
        String msg = "Invalid Adhaar Number";
        return validationResponse(responseLabel,valid,msg);
    }

    public static ValidatedResponse isRationCardNumber(String rationCardNumber, Label responseLabel){
        String exp = "^[0-9]{4}(\\s?|-?)[0-9]{4}(\\s?|-?)[0-9]{4}$";
        boolean valid = rationCardNumber.matches(exp);
        String msg = "Invalid Ration Card Number";
        return validationResponse(responseLabel,valid,msg);
    }

    public static ValidatedResponse isDob(LocalDate dob, Label responseLabel){
        boolean valid = dob.isBefore(LocalDate.now());
        String msg = "Date cannot be greater than today";
        return validationResponse(responseLabel,valid,msg);
    }

    public static ValidatedResponse isIdProof(File file, Label responseLabel){
        String ext = FilenameUtils.getExtension(file.getAbsolutePath());
        System.out.println(ext);
        String exp = "^(jpg|jpeg|png|gif|bmp)$";
        boolean valid = ext.matches(exp);
        String msg = "Only jpeg /jpg / png / gif / bmp are allowed";
        return validationResponse(responseLabel,valid,msg);
    }

    public static ValidatedResponse isPassword(String password, Label responseLabel){
        String exp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";
        boolean valid = password.matches(exp);
        String msg = "Minimum eight characters, at least one uppercase letter, one lowercase letter and one number:";
        return validationResponse(responseLabel,valid,msg);
    }


    public static ValidatedResponse isConfPassword(String password, String confPassword, Label responseLabel){
        boolean valid = password.equals(confPassword);
        String msg = "Password does not match";
        return validationResponse(responseLabel,valid,msg);
    }

    public static ValidatedResponse isInteger(String val, Label responseLabel){
        String exp = "^[1-9]\\d*$";
        boolean valid = val.matches(exp);
        String msg = "Only integer value";
        return validationResponse(responseLabel,valid,msg);
    }

    public static ValidatedResponse isDouble(String val, Label responseLabel){
        String exp = "[+-]?\\d*\\.?\\d+";
        boolean valid = val.matches(exp);
        String msg = "Only decimal or integer value";
        return validationResponse(responseLabel,valid,msg);
    }

    public static ValidatedResponse isUsername(String val, Label responseLabel){
        String exp =  "^[A-Za-z][A-Za-z0-9_]{4,29}$";
        boolean valid = val.matches(exp);
        String msg = "Only alphabets, numbers and underscore is allowed. Char Limit: Min -5, Max-30";
        return validationResponse(responseLabel,valid,msg);
    }


    public static ValidatedResponse isEmailId(String val, Label responseLabel){
        String exp =  "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$"  ;
        boolean valid = val.matches(exp);
        String msg = "Please enter a valid email address";
        return validationResponse(responseLabel,valid,msg);
    }

    // Simple methods with boolean returns
    public static boolean isImage(File file){
        String ext = FilenameUtils.getExtension(file.getAbsolutePath());
        String exp = "^(jpg|jpeg|png)$";
        boolean result = ext.matches(exp);
        return result;
    }

    public static ValidatedResponse validationResponse(Label responseLabel, boolean valid,String msg){
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
            responseLabel.setText(msg);
            IconNode wrongIcon = new IconNode(GoogleMaterialDesignIcons.HIGHLIGHT_OFF);
            wrongIcon.setIconSize(24);
            wrongIcon.setFill(Color.web("#fdca40"));
            responseLabel.setGraphic(wrongIcon);
            responseLabel.getStyleClass().add("validate-err");
            return new ValidatedResponse(responseLabel,false);
        }
    }
}
