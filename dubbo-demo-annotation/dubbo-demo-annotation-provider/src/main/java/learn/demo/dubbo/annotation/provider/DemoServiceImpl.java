package learn.demo.dubbo.annotation.provider;

import lear.demo.dubbo.annotation.api.DemoService;
import org.apache.dubbo.config.annotation.Service;

/**
 * Created by shirukai on 2019-06-20 14:04
 * DemoService实现类
 */
@Service
public class DemoServiceImpl implements DemoService {
    @Override
    public String sayHello(String name) {
        return "The service base in Annotation.\nHello" + name;
    }
}
