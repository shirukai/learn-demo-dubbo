package learn.demo.dubbo.api.provider;

import learn.demo.dubbo.api.api.DemoService;

/**
 * Created by shirukai on 2019-06-20 09:25
 * DemoService接口实现
 */
public class DemoServiceImpl implements DemoService {
    @Override
    public String sayHello(String name) {
        return "Hello " + name;
    }
}
