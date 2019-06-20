package learn.demo.dubbo.springboot.provider;

import learn.demo.dubbo.springboot.api.DemoService;
import org.apache.dubbo.config.annotation.Service;

/**
 * Created by shirukai on 2019-06-20 15:08
 */
@Service(version = "1.0.0")
public class DemoServiceImpl implements DemoService {
    @Override
    public String sayHello(String name) {
        return "The service of dubbo from springboot.\nHello " + name;
    }
}
