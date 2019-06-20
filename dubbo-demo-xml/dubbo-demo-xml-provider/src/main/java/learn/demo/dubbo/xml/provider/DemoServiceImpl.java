package learn.demo.dubbo.xml.provider;

import learn.demo.dubbo.xml.api.DemoService;

/**
 * Created by shirukai on 2019-06-20 10:19
 * 实验DemoService接口
 */
public class DemoServiceImpl implements DemoService {
    @Override
    public String sayHello(String name) {
        return "This service base in XML.\nHello " + name;
    }
}
