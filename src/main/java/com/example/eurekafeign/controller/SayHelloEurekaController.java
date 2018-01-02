package com.example.eurekafeign.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class SayHelloEurekaController {
    private static final Logger logger = LoggerFactory.getLogger(SayHelloEurekaController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @RequestMapping("hello/eureka/{name}")
    @HystrixCommand(fallbackMethod = "helloFallback")
    public String hello(@PathVariable String name) {
        String greeting = this.restTemplate.getForObject("http://greeting-service/hello/" + name, String.class);
        return greeting + " from eureka";
    }

    public String helloFallback(String name) {
        return "sorry " + name + "! greeting is out of service";
    }

    @RequestMapping("/hellolong")
    @HystrixCommand(fallbackMethod = "helloLongFallback", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5100")
    })
    public String helloLong() {
        logger.info("start hello long");
        String greeting = this.restTemplate.getForObject("http://greeting-service/hellolong", String.class);
        logger.info("end hello long");
        return greeting + " from eureka";
    }

    public String helloLongFallback() {
        logger.info("start hellolong fallback");
        return "hello long is out of service";
    }
}
