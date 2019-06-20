package learn.demo.dubbo.annotation.consumer;

import lear.demo.dubbo.annotation.api.DemoService;
import learn.demo.dubbo.annotation.consumer.comp.DemoServiceComponent;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by shirukai on 2019-06-20 14:10
 * DemoService消费者
 */
public class DemoServiceConsumer {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ConsumerConfiguration.class);
        context.start();
        DemoService service = context.getBean("demoServiceComponent", DemoServiceComponent.class);
        String message = service.sayHello("world");
        System.out.println(message);
    }

    @Configuration
    @EnableDubbo(scanBasePackages = "learn.demo.dubbo.annotation.consumer.comp")
    @PropertySource("classpath:/spring/dubbo-consumer.properties")
    @ComponentScan(value = {"learn.demo.dubbo.annotation.consumer.comp"})
    static class ConsumerConfiguration {

    }
}
