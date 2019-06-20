package learn.demo.dubbo.springboot.consumer.controller;

import learn.demo.dubbo.springboot.api.DemoService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by shirukai on 2019-06-20 16:03
 */
@RestController
public class DemoController {
    @Reference(version = "1.0.0")
    private DemoService demoService;

    @GetMapping(value = "/sayHello")
    public String sayHello(@RequestParam("name") String name) {
        return demoService.sayHello(name);
    }
}
