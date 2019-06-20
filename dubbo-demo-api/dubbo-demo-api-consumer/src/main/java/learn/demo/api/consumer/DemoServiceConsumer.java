package learn.demo.api.consumer;

import learn.demo.dubbo.api.api.DemoService;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;

/**
 * Created by shirukai on 2019-06-20 09:53
 * DemoService 消费者
 */
public class DemoServiceConsumer {
    public static void main(String[] args) {
        // 应用配置
        ApplicationConfig applicationConfig = new ApplicationConfig("dubbo-demo-api-consumer");
        applicationConfig.setQosPort(22223);

        // 配置注册中心
        RegistryConfig registryConfig = new RegistryConfig("127.0.0.1:9090");
        ReferenceConfig<DemoService> reference = new ReferenceConfig<>();
        reference.setApplication(applicationConfig);
        reference.setRegistry(registryConfig);
        reference.setInterface(DemoService.class);
        // 获取服务
        DemoService service = reference.get();
        // 服务调用
        String message = service.sayHello("dubbo !");
        System.out.println(message);
    }
}
