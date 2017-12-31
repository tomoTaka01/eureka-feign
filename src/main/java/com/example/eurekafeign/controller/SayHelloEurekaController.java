package com.example.eurekafeign.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class SayHelloEurekaController {
    @Autowired
    private RestTemplate restTemplate;

    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @RequestMapping("hello/eureka/{name}")
    public String hello(@PathVariable String name) {
        String greeting = this.restTemplate.getForObject("http://greeting-service/hello/" + name, String.class);
        return greeting + " from eureka";
    }
}
