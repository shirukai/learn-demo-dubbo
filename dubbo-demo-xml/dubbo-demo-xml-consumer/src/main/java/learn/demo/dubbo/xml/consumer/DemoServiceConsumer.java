package learn.demo.dubbo.xml.consumer;

import learn.demo.dubbo.xml.api.DemoService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by shirukai on 2019-06-20 11:07
 * 基于XML的Dubbo服务消费之
 */
public class DemoServiceConsumer {
    public static void main(String[] args){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring/dubbo-consumer.xml");
        context.start();
        DemoService demoService = context.getBean("demoService", DemoService.class);
        String message = demoService.sayHello("world");
        System.out.println(message);
    }
}
