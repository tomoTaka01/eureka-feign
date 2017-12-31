## Spring Boot eureka Ribbon sample
  * as of 2017-12-31, SpringBoot version 2.0.0.M7
  * for eureka server to see:https://github.com/tomoTaka01/eureka-server
  * for eureka client(service discovery) to see:https://github.com/tomoTaka01/eureka-client
  * so far this is without eureka(using listOfServicers)

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
