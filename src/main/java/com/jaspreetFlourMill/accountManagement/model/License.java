package com.jaspreetFlourMill.accountManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;
import java.util.Optional;

import static com.jaspreetFlourMill.accountManagement.util.Rest.BASE_URI;

@JsonIgnoreProperties(ignoreUnknown = true)
public class License {

    private String id;
    private String licenseKey;

    public License() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLicenseKey() {
        return licenseKey;
    }

    public void setLicenseKey(String licenseKey) {
        this.licenseKey = licenseKey;
    }

    public static boolean match(String lKey){
        try {
            System.out.println("Matching License Key ...." + lKey);
            String uri = BASE_URI + "/licenses/" + lKey;
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<License> responseEntity = restTemplate.getForEntity(uri, License.class);

            // Find Key successful
            if (responseEntity.getStatusCode() == HttpStatus.ACCEPTED) {
                System.out.println("License Match Success: " + lKey);
                return true;
            }

        }
        catch (HttpClientErrorException.NotFound e) {
            // Key not found
            System.out.println("Wrong License Key: " + lKey);
            return false;
        }

        // Find customer failed
        System.out.println("Failed to fetch Key: " + lKey);
        return false;
    }
}
