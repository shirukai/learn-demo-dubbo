package learn.demo.dubbo.xml.provider;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by shirukai on 2019-06-20 10:18
 * 基于XML的Dubbo服务提供者
 */
public class DemoServiceProvider {
    public static void main(String[] args) throws Exception {
        // 从XML配置文件中获取应用上下文
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring/dubbo-provider.xml");
        context.start();
        System.in.read();
    }
}
