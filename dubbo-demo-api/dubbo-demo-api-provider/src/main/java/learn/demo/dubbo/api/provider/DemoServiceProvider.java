package learn.demo.dubbo.api.provider;

import learn.demo.dubbo.api.api.DemoService;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;

/**
 * Created by shirukai on 2019-06-20 09:30
 * <p>
 * DemoService 生产者
 * 该Demo注册中心使用自定义注册中心，SimpleRegistryService
 * 所以在启动该服务之前，需要先启动注册中心
 */
public class DemoServiceProvider {
    public static void main(String[] args) throws Exception {
        // 应用配置
        ApplicationConfig applicationConfig = new ApplicationConfig("api-demo-service-provider");

        // qos 默认端口22222在本地同时启动多个服务时，需要手动修改
        applicationConfig.setQosPort(22222);

        // 注册中心配置
        RegistryConfig registryConfig = new RegistryConfig("127.0.0.0:9090");

        // 服务配置，此实例很重，封装了与注册中心的连接，请自行缓存，否则可能造成内存和连接泄漏
        ServiceConfig<DemoService> service = new ServiceConfig<>();
        service.setApplication(applicationConfig);
        service.setRegistry(registryConfig);
        service.setInterface(DemoService.class);
        service.setRef(new DemoServiceImpl());

        // 暴露服务
        service.export();

        // 输入任意退出
        System.in.read();

    }
}
