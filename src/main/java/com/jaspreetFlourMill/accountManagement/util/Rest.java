package com.jaspreetFlourMill.accountManagement.util;

import com.jaspreetFlourMill.accountManagement.model.Customer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

public class Rest {
    public static final int PORT = 8080;
    public static final String BASE_URI = "http://localhost:" + PORT;

}
