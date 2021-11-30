package com.jaspreetFlourMill.accountManagement.controllers;

import com.jaspreetFlourMill.accountManagement.StageReadyEvent;
import com.jaspreetFlourMill.accountManagement.model.Employee;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@FxmlView("/views/employeeList.fxml")
public class EmployeesListController implements Initializable, ApplicationListener<StageReadyEvent> {

        @FXML
        public HBox employeeListContainerHBox;

        @FXML
        public VBox employeeListContainerVBox;

        @FXML
        public VBox employeeListBox;

        @FXML
        public ScrollPane employeeListSP;

        @FXML
        public TextField searchEmployeeTextField;

        @FXML
        public VBox employeeDetailsFromList;

        private Employee[] employees;

        private FxControllerAndView<EmployeeDetailsController, Node> employeeDetailsCV;

        private final FxWeaver fxWeaver;
        private Stage stage;

    public EmployeesListController(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }

    @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {

            Employee.getAllEmployees().ifPresent(employees -> {
                this.displayEmployees(employees);
                employeeDetailsFromList.setVisible(false);
                searchEmployeeTextField.textProperty().addListener((observableValue, oldVal, newVal) -> {
                    employeeListBox.getChildren().clear();
                    for (Employee employee : employees) {
                        if (employee.getName().toLowerCase().contains(newVal.toLowerCase())) {
                            Label employeeLabel = new Label(employee.getName()
                                    + "(ID-" + employee.getId() + ")");

                            employeeLabel.setMinWidth(250);
                            employeeLabel.setPrefWidth(employeeListSP.getPrefWidth());
                            employeeLabel.setMaxWidth(Double.MAX_VALUE);
                            employeeLabel.getStyleClass().add("list-item");
                            employeeLabel.setOnMouseClicked(mouseEvent -> {
                                showEmployeeDetails(employee);
                            });
                            employeeListBox.getChildren().add(employeeLabel);
                        }
                    }
                });
            });
        }


        public void displayEmployees(Employee[] employeeArr) {

            for (Employee employee : employeeArr) {
                Label employeeLabel = new Label(employee.getName()
                        + "(ID-" + employee.getId() + " )");

                employeeLabel.setMinWidth(250);
                employeeLabel.setPrefWidth(employeeListSP.getPrefWidth());
                employeeLabel.setMaxWidth(Double.MAX_VALUE);
//                employeeLabel.setPrefHeight(400);
                employeeLabel.getStyleClass().add("list-item");
                employeeLabel.setOnMouseClicked(mouseEvent -> {
                    showEmployeeDetails(employee);
                });
                employeeListBox.getChildren().add(employeeLabel);
            }

        }

        public void showEmployeeDetails(Employee employee) {
            employeeDetailsCV = fxWeaver.load(EmployeeDetailsController.class);
            employeeDetailsCV.getView().ifPresent(view -> {
                employeeDetailsFromList.getChildren().clear();
                employeeDetailsFromList.getChildren().add(view);
                employeeDetailsFromList.setVisible(true);
                employeeDetailsFromList.setAlignment(Pos.TOP_CENTER);

                Button editBtn = new Button("Edit");
                editBtn.getStyleClass().add("tertiary-btn");

                Button deleteEmployeeBtn = new Button("Delete Employee");
                deleteEmployeeBtn.getStyleClass().add("danger-btn");

                // Edit Employee
                editBtn.setOnAction(actionEvent -> {
                    ContentController.navigationHandler.handleEditEmployee(employee);
                });

                // Delete Employee Account
                deleteEmployeeBtn.setOnAction(actionEvent -> {
                    if(Employee.delete(employee.getUser())){
                        ContentController.navigationHandler.handleShowEmployees();
                    }
                });

                HBox btnBox = new HBox(editBtn, deleteEmployeeBtn);
                btnBox.setPrefWidth(employeeDetailsFromList.getWidth());
                btnBox.setSpacing(employeeDetailsFromList.getWidth() * 0.02);
                btnBox.setAlignment(Pos.CENTER);

                employeeDetailsFromList.getChildren().add(btnBox);
            });

            employeeDetailsCV.getController().updateEmployeeDetails(employee.getUser());
        }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        this.stage = event.getStage();
    }

}
