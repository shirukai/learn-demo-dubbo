package learn.demo.dubbo.registry.provider;

import learn.demo.dubbo.reigstry.api.DemoService;
import org.apache.dubbo.config.annotation.Service;

/**
 * Created by shirukai on 2019-06-20 16:41
 * DemoService 实现类
 */
@Service(version = "1.0.0")
public class DemoServiceImpl implements DemoService {
    @Override
    public String sayHello(String name) {
        return "Hello " + name;
    }
}
