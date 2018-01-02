package com.example.eurekafeign.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class WorkController {
    private static final Logger logger = LoggerFactory.getLogger(WorkController.class);
    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/work/eureka")
    @HystrixCommand(fallbackMethod = "workFallback", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3100")
    })
    public String work() {
        logger.info("start work from eureka");
        String work = restTemplate.getForObject("http://greeting-service/work", String.class);
        logger.info("end work from eureka");
        return work;
    }

    public String workFallback() {
        return "work not yet done.";
    }
}
