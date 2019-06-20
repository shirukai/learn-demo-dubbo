package learn.demo.dubbo.registry.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by shirukai on 2019-06-20 16:46
 * Consumer
 */
@SpringBootApplication
public class DemoServiceConsumer {
    public static void main(String[] args) {
        SpringApplication.run(DemoServiceConsumer.class, args);
    }
}