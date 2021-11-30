package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.StageReadyEvent;
import com.jaspreetFlourMill.accountManagement.model.Employee;
import com.jaspreetFlourMill.accountManagement.model.User;
import com.jaspreetFlourMill.accountManagement.util.AlertDialog;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;


@Component
@FxmlView("/views/employeeDetails.fxml")
public class EmployeeDetailsController implements Initializable, ApplicationListener<StageReadyEvent> {

//    @FXML
//    public AnchorPane employeeDetailAnchorPane;

    @FXML
    public VBox employeeDetailVBox;

    @FXML
    public GridPane employeeDetailGridPane;

    @FXML
    public HBox imageContainerHBox;

    private FxControllerAndView<ModalImageViewController, Node> modalImageViewCV;

    private FxControllerAndView<ContentController, Node> contentControllerCV;

    @FXML
    private Label employeeIdDisplay;

    @FXML
    private Label employeeAddress;

    @FXML
    private Label employeeContactNumber;

    @FXML
    private Label employeeDesignation;

    @FXML
    private Label employeeName;

    @FXML
    private Label employeeEmailId;

    @FXML
    public ImageView employeeImage;

    public String idProofImageUri;

    private final FxWeaver fxWeaver;
    private Stage stage;


    public EmployeeDetailsController(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


    }

    public void updateEmployeeDetails(User user) {
        // Find employee
        Employee.get(user).ifPresent((updatedEmployee) -> {
            // If employee is found, find employee account and update it to view
            Employee.get(updatedEmployee.getUser()).ifPresent((updatedEmployeeAccount) -> {
                employeeIdDisplay.setText(updatedEmployee.getId());
                employeeAddress.setText(updatedEmployee.getAddress());
                employeeContactNumber.setText(updatedEmployee.getContactNumber());
                employeeName.setText(updatedEmployee.getName());
                employeeEmailId.setText(updatedEmployee.getEmailId());
                employeeDesignation.setText(updatedEmployee.getJobDesignation());
                try {
                    employeeImage.setImage(new Image(new FileInputStream(updatedEmployee.getUser().getProfileImgLocation())));
                } catch (FileNotFoundException e) {
                    // Information dialog
                    AlertDialog alertDialog = new AlertDialog("Error", "Error reading updating input file", e.getMessage(), Alert.AlertType.ERROR);
                    alertDialog.showErrorDialog(e);
                }
                catch (NullPointerException e){
                    System.out.println("Image location is null");
                    employeeImage.setImage(new Image("images/avatar.png"));
                }
            });
        });


    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        this.stage = event.getStage();
    }
    
}
