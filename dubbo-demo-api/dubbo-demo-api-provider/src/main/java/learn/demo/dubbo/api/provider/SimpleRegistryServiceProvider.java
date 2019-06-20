package learn.demo.dubbo.api.provider;

import org.apache.dubbo.registry.SimpleRegistryExporter;

/**
 * Created by shirukai on 2019-06-20 09:32
 * <p>
 * 简单注册中心服务生产者
 * 基于RegistryService接口实现的简单注册中心
 * 代码位置：
 * https://github.com/apache/dubbo/tree/master/dubbo-registry/dubbo-registry-default/src/test/java/org/apache/dubbo/registry/dubbo
 */
public class SimpleRegistryServiceProvider {
    public static void main(String[] args) throws Exception {

        // 暴露注册中心服务，端口为9090
        SimpleRegistryExporter.export(9090);

        // 任意输入退出
        System.in.read();
    }
}
