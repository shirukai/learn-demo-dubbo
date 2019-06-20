package learn.demo.dubbo.annotation.consumer.comp;

import lear.demo.dubbo.annotation.api.DemoService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

/**
 * Created by shirukai on 2019-06-20 14:22
 * DemoService 组件
 */
@Component("demoServiceComponent")
public class DemoServiceComponent implements DemoService {
    @Reference
    private DemoService demoService;

    @Override
    public String sayHello(String name) {
        return demoService.sayHello(name);
    }
}
