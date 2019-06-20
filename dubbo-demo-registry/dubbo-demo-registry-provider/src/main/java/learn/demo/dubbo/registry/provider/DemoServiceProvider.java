package learn.demo.dubbo.registry.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by shirukai on 2019-06-20 16:42
 * DemoService提供者
 */
@SpringBootApplication
public class DemoServiceProvider {
    public static void main(String[] args) {
        SpringApplication.run(DemoServiceProvider.class, args);
    }
}
