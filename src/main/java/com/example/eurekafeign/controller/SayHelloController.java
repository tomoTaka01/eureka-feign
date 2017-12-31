package com.example.eurekafeign.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RibbonClient(name = "say-hello")
public class SayHelloController {
    @Autowired
    private RestTemplate restTemplate;

    @LoadBalanced
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @RequestMapping("hello/{name}")
    public String hello(@PathVariable String name) {
        String greeting = this.restTemplate.getForObject("http://say-hello/hello/" + name, String.class);
        return greeting;
    }
}
