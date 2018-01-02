## Spring Boot Eureka Ribbon sample
  * as of 2018-01-02, SpringBoot version 2.0.0.M7
  * for eureka server to see:https://github.com/tomoTaka01/eureka-server
  * for eureka client(service discovery) to see:https://github.com/tomoTaka01/eureka-client
  * minitor Hystrix stream is here: https://github.com/tomoTaka01/hystrix-dashboard-sample

### For Hystrix(Circuit Breaker)
  * The below show the response from eureka client port 8001 or 8002
  * If the work takes more then 3 sec, return value is [work not yet node.] by using Hystrix
  ![GitHub Logo](/images/work.png)

  * The flow image is
  ![GitHub Logo](/images/hystrix-flow.png)

  * WorkCotroller.java
    * HystrixComman annotaion with timeout setting work, so the work service take more than 3 sec fail.


```java
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
```

### Ribbon with Eureka service discovery
  ![GitHub Logo](/images/ribbon-eureka-flow.png)

  * just add @LoadBalanced annotaion to RestTemplate
  * fix the url to registered Eureka service name like greeting-service
  * SayHelloEurekaController.java
```java
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
```

  * to see the response

```
http://localhost:8888/sample/hello/eureka/java
```

  * The response is 

```
Hello java from port is [8001] from eureka
 or 
Hello java from port is [8002] from eureka
```

  * log
    * The below log shows the serverList like [list of Servers=[taka-no-air:8001, taka-no-air:8002]]

```
DynamicServerListLoadBalancer for client greeting-service initialized: DynamicServerListLoadBalancer:{NFLoadBalancer:name=greeting-service,current list of Servers=[taka-no-air:8001, taka-no-air:8002],Load balancer stats=Zone stats: {defaultzone=[Zone:defaultzone; Instance count:2;   Active connections count: 0;    Circuit breaker tripped count: 0;   Active connections per server: 0.0;] },Server stats: [[Server:taka-no-air:8002;  Zone:defaultZone;   Total Requests:0;   Successive connection failure:0;    Total blackout seconds:0;   Last connection made:Thu Jan 01 09:00:00 JST 1970;  First connection made: Thu Jan 01 09:00:00 JST 1970;    Active Connections:0;   total failure count in last (1000) msecs:0; average resp time:0.0;  90 percentile resp time:0.0;    95 percentile resp time:0.0;    min resp time:0.0;  max resp time:0.0;  stddev resp time:0.0] , [Server:taka-no-air:8001; Zone:defaultZone;   Total Requests:0;   Successive connection failure:0;    Total blackout seconds:0;   Last connection made:Thu Jan 01 09:00:00 JST 1970;  First connection made: Thu Jan 01 09:00:00 JST 1970;    Active Connections:0;   total failure count in last (1000) msecs:0; average resp time:0.0;  90 percentile resp time:0.0;    95 percentile resp time:0.0;    min resp time:0.0;  max resp time:0.0;  stddev resp time:0.0] ]}ServerList:org.springframework.cloud.netflix.ribbon.eureka.DomainExtractingServerList@e9079b9



```

### without Eureka

  * application.yml(for client load blancing)
```yml
server:
  port: 8888
  servlet:
    context-path: /sample
say-hello:
  ribbon:
    eureka:
      enabled: false
    listOfServers: localhost:8001,localhost:8002
    ServerListRefreshInterval: 15000
```


  * SayHelloController.java
```java
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
```

  * to see the response

```
http://localhost:8888/sample/hello/java
```

  * The response is 

```
Hello java from port is [8001]
 or 
Hello java from port is [8002]
```
The response port is 8001 or 8002, so client load balancing works fine!

  * to see the image(sorry the below link is in Japanese, but there is image picture)
    * see:http://tomotaka.hatenablog.com/entry/2017/12/31/183642
