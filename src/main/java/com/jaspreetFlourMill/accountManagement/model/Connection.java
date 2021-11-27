package com.jaspreetFlourMill.accountManagement.model;

import com.jaspreetFlourMill.accountManagement.util.AlertDialog;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static com.jaspreetFlourMill.accountManagement.util.Rest.BASE_URI;
import static com.jaspreetFlourMill.accountManagement.util.Rest.PORT;

public class Connection {
    private String status;
    private boolean isConnected;
    private int retryCount;

    public Connection() {
        this.status = "Disconnected";
        this.isConnected = false;
        this.retryCount = 0;
    }

    public boolean isConnected(){
        return isConnected;
    }

    public boolean retry(){
        if(retryCount > 60){
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error","Connection Timed Out: Error Connecting ...","Try relaunching ther application! Exiting....", Alert.AlertType.ERROR);
            alertDialog.showInformationDialog();
            Platform.exit();
        }
        try {
            Thread.sleep(3000);
            System.out.println("Retrying to connect.....");
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        retryCount++;
        return connect();
    }

    public boolean connect(){
        try {
            System.out.println("Connecting to server at...." + PORT);
            String uri = BASE_URI + "/connect";
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);

            // Find customer successful
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                status = responseEntity.getBody();
                this.isConnected = true;
                System.out.println("Connection status : " + status);
                return this.isConnected;
            }

        } catch (ResourceAccessException e) {
            // Connection not found
            this.status = "Connection refused !";
            this.isConnected = false;
            String errMsg = "Connection not found at: " + PORT;
            System.out.println(errMsg);
            return this.isConnected;
        }
        catch (Exception e){
            // Connection Error
            this.status = "Error Connecting....";
            this.isConnected = false;
            String errMsg = "Connection Error";
            System.out.println(errMsg);
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error",errMsg,e.getMessage(), Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
            return this.isConnected;
        }

        // Connection failed
        System.out.println("Failed to connect: ");
        return this.isConnected;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
