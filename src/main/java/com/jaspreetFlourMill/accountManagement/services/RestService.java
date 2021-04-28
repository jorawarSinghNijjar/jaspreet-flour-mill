//package com.jaspreetFlourMill.accountManagement.services;
//
//import org.springframework.boot.web.client.RestTemplateBuilder;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//@Service
//public class RestService {
//    private final RestTemplate restTemplate;
//
//    public RestService(RestTemplateBuilder restTemplateBuilder) {
//        this.restTemplate = restTemplateBuilder.build();
//    }
//
//    public String getPostsPlainJSON() {
//        String url = "http://localhost:8080/employees";
//        return this.restTemplate.getForObject(url, String.class);
//    }
//}
