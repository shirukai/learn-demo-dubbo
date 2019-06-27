# RPC框架初体验之Dubbo
> 版本说明：
> dubbo:2.7.2
> springboot:2.0.0

dubbo-demo-api:基于原生API配置的dubbo服务，使用实现自定义注册中心。

dubbo-demo-xml:基于XML配置的dubbo服务。使用广播注册中心。

dubbo-demo-annotation:基于注解的形式配置dubbo服务。使用广播注册中心。

dubbo-demo-springboot:dubbo整合springboot。使用广播注册中心。

dubbo-demo-registry: dubbo几种常见的注册中心

>描述
Dubbo是阿里开源的一款RPC框架，最近在学习微服务的时候有提及到。因此对Dubbo进行了入门级体验，这里主要体验体验Dubbo的几种配置方式，如XML配置、API配置、注解配置，以及Springboot里整合Dubbo，同时体验几种注册中心，如simple、zk、redis、multicast。

Dubbo官网内容还是很丰富的，支持中文。地址：http://dubbo.apache.org/zh-cn/docs/user/quick-start.html

# 1 Dubbo配置使用

## 1.1 基于原生API的配置

官网文档：http://dubbo.apache.org/zh-cn/docs/user/configuration/api.html

怎么说呢，参考官网这个文档实现时踩了不少坑，因为官网提供的注册中心没法使用，又是初次接触，走了不少弯路，而自己又是直肠子一根，非要自己实现一个注册中心，然后参考官网给的Simple注册中心的例子，有两点逼得我强迫症犯了，首先使用的是XML配置的方式创建注册中心服务，而我这里只想通过原生API实现，另一个

```xml
    <!-- 简单注册中心实现，可自行扩展实现集群和状态同步 -->
    <bean id="registryService" class="org.apache.dubbo.registry.simple.SimpleRegistryService" />
```

这里面提到的org.apache.dubbo.registry.simple.SimpleRegistryService这个类，我死活找不到。就这俩问题纠结了一天。后来终于解决了，在github上dubbo源码的测试文件里找到了。心里一万匹cnm在奔腾。回归正题，使用原生API的方式配置其实就是自己创建实例，传入配置参数，启动服务。下面将一步步讲解。

### 1.1.1 项目准备

首先创建一个名为dubbo-demo-api的模块，然后再该模块下创建三个子模块

```
dubbo-demo-api/
├── dubbo-demo-api-api
├── dubbo-demo-api-consumer
├── dubbo-demo-api-provider
└── pom.xml
```

* dubbo-demo-api-api：提供演示接口
* dubbo-demo-api-consumer：服务消费者
* dubbo-demo-api-provider：服务提供者

### 1.1.2 dubbo-demo-api-api 模块

该模块主要提供用来演示的接口，在learn.demo.dubbo.api.api包下创建DemoService接口，如下所示

```java
package learn.demo.dubbo.api.api;

/**
 * Created by shirukai on 2019-06-20 09:23
 * DemoService 接口
 */
public interface DemoService {
    String sayHello(String name);
}

```

### 1.1.3 dubbo-demo-api-provider模块

该模块是创建dubbo提供者服务。首先要引入dubbo相关依赖

```xml
        <!-- dubbo -->
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo</artifactId>
            <version>2.7.2</version>
        </dependency>
```

然后引入api模块

```xml
        <dependency>
            <groupId>learn.demo</groupId>
            <artifactId>dubbo-demo-api-api</artifactId>
            <version>1.0</version>
            <scope>compile</scope>
        </dependency>
```

#### 1.1.3.1 实现DemoService接口

上面我们在api模块中定义的DemoService接口，这里我们在learn.demo.dubbo.api.provider这个包下实现该接口。创建名为DemoServiceImpl的类，内容如下：

```java
package learn.demo.dubbo.api.provider;

import learn.demo.dubbo.api.api.DemoService;

/**
 * Created by shirukai on 2019-06-20 09:25
 * DemoService接口实现
 */
public class DemoServiceImpl implements DemoService {
    @Override
    public String sayHello(String name) {
        return "This service base in API.\nHello " + name;
    }
}

```

#### 1.1.3.2 实现Simple注册中心

这里在实现Provider之前，先实现以下Simple注册中心，之前也有讲到官网在讲Simple注册中心的时候，是使用Spring的XML配置去生成Bean然后创建服务的，而且里面的提到的SimpleRegistryService类也找不到，经过九九八十一难这里使用API去实现一个Simple注册中心。这里需要几个类：AbstractRegistryService，SimpleRegistryExporter，SimpleRegistryService。这几个类可以从dubbo的github上找到，https://github.com/apache/dubbo/tree/master/dubbo-registry/dubbo-registry-default/src/test/java/org/apache/dubbo/registry/dubbo。

我们可以在自己的项目里，创建 org.apache.dubbo.registry包，然后将这三个类复制进去。

在learn.demo.dubbo.api.provider包下创建SimpleRegistryServiceProvider类用来提供注册中心服务，代码如下：

```java
package learn.demo.dubbo.api.provider;

import org.apache.dubbo.registry.SimpleRegistryExporter;

/**
 * Created by shirukai on 2019-06-20 09:32
 * <p>
 * 简单注册中心服务提供者
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

```

#### 1.1.3.3 实现DemoServiceProvider

使用Dubbo的原生API实现生产者，大体流程：

1. 创建应用配置ApplicationConfig，我们可以设置应用名称、设置qos端口等等。
2. 创建注册中心配置RegistryConfig，可以设置注册中心类型，地址，端口，用户名，密码等。
3. 创建服务配置ServiceConfig，此实例很重要，有与注册中心的连接
4. 暴露服务export

内容如下：

```java
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

```

### 1.1.4 dubbo-demo-api-consumer模块

该模块dubbo服务消费者，依然需要引入dubbo和自定义api的jar包。

```xml
<!-- dubbo -->
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo</artifactId>
            <version>2.7.2</version>
        </dependency>
        <dependency>
            <groupId>learn.demo</groupId>
            <artifactId>dubbo-demo-api-api</artifactId>
            <version>1.0</version>
            <scope>compile</scope>
        </dependency>
```

consumer的实现与provider步骤类似

1. 创建应用配置ApplicationConfig
2. 创建配置中心配置RegistryConfig
3. 创建服务ReferenceConfig
4. 获取接口实例

代码如下：

```java
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

```

## 1.2 基于XML的配置

上面我们介绍了使用API进行Dubbo的配置，该中方式方便集成其它系统，但是实现繁琐。所以官方提供了Spring基于XML的配置方式，该配置方式依赖于Spring，是加载Bean的方式创建服务。官网文档：http://dubbo.apache.org/zh-cn/docs/user/configuration/xml.html

### 1.2.1 项目准备

首先创建一个名为dubbo-demo-xml的模块，然后再该模块下创建三个子模块

```
dubbo-demo-xml/
├── dubbo-demo-xml-api
├── dubbo-demo-xml-consumer
├── dubbo-demo-xml-provider
└── pom.xml
```

- dubbo-demo-xml-api：提供演示接口
- dubbo-demo-xml-consumer：服务消费者
- dubbo-demo-xml-provider：服务提供者

### 1.2.2 dubbo-demo-xml-api模块

与dubbo-demo-api-api相同。

### 1.2.3 dubbo-demo-xml-provider模块

同上引入api以及dubbo依赖。

#### 1.2.3.1 DemoServiceImpl

实现定义的api接口。

```java
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

```

#### 1.2.3.2 dubbo-provider.xml

在resource/spring下创建dubbo-provider.xml配置文件，用来配置dubbo的provider服务。具体参数参考官网。

```xml
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:dubbo="http://dubbo.apache.org/schema/dubbo"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.3.xsd
       http://dubbo.apache.org/schema/dubbo http://dubbo.apache.org/schema/dubbo/dubbo.xsd">

    <!-- provider's application name, used for tracing dependency relationship -->
    <dubbo:application name="demo-xml-provider">
        <!-- 指定qos端口，配置优先级低于dubbo.properties-->
        <dubbo:parameter key="qos.port" value="22222"/>
    </dubbo:application>

    <!-- 基于广播的注册中心 -->
    <dubbo:registry address="multicast://224.5.6.7:1234"/>

    <!-- use dubbo protocol to export service on port 20880 -->
    <dubbo:protocol name="dubbo"/>

    <!-- service implementation, as same as regular local bean -->
    <bean id="demoService" class="learn.demo.dubbo.xml.provider.DemoServiceImpl"/>

    <!-- declare the service interface to be exported -->
    <dubbo:service interface="learn.demo.dubbo.xml.api.DemoService" ref="demoService"/>

</beans>
```

#### 1.2.3.3 DemoServiceProvider

Provider实现比较简单，使用ClassPathXmlApplicationContext从xml获取应用上下文，然后启动。

```java
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
```

### 1.2.4 dubbo-demo-xml-consumer模块

同上需在pom中引入api和dubbo依赖。

#### 1.2.4.1 dubbo-consumer.xml

使用xml配置consumer服务。在resource/spring下创建dubbo-consumer.xml，内容如下

```xml
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:dubbo="http://dubbo.apache.org/schema/dubbo"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.3.xsd
       http://dubbo.apache.org/schema/dubbo http://dubbo.apache.org/schema/dubbo/dubbo.xsd">

    <dubbo:application name="demo-xml-consumer"/>

    <!-- 使用广播注册中心 -->
    <dubbo:registry address="multicast://224.5.6.7:1234"/>

    <!-- generate proxy for the remote service, then demoService can be used in the same way as the
    local regular interface -->
    <dubbo:reference id="demoService" check="false" interface="learn.demo.dubbo.xml.api.DemoService"/>

</beans>
```

#### 1.2.4.2 DemoServiceConsumer

使用ClassPathXmlApplicationContext获取应用上下文，并在上下文中获取DemoService接口对应的Bean，然后调用接口方法。

```java
package learn.demo.dubbo.xml.consumer;

import learn.demo.dubbo.xml.api.DemoService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by shirukai on 2019-06-20 11:07
 * 基于XML的Dubbo服务消费之
 */
public class DemoServiceConsumer {
    public static void main(String[] args){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring/dubbo-consumer.xml");
        context.start();
        DemoService demoService = context.getBean("demoService", DemoService.class);
        String message = demoService.sayHello("world");
        System.out.println(message);
    }
}

```

#### 1.2.4.3 补充，关于dubbo的配置

dubbo的配置，我们可以通过dubbo.properties文件来配置，如我要修改qos端口，可以只创建dubbo.properites文件，然后添加如下配置：

```properties
# dubbo相关配置文件
dubbo.application.qos.port=22223
```

## 1.3 基于注解的配置

官网：http://dubbo.apache.org/zh-cn/docs/user/configuration/annotation.html

dubbo关于注解的配置，与xml如出一辙，只不过我们不需要配置xml了，在需要暴露的接口实现类上添加@Service注解，然后通过@Configuration将相关配置注入到Spring里。

### 1.3.1 项目准备

首先创建一个名为dubbo-demo-xml的模块，然后再该模块下创建三个子模块

```
dubbo-demo-annotation/
├── dubbo-demo-annotation-api
├── dubbo-demo-annotation-consumer
├── dubbo-demo-annotation-provider
└── pom.xml
```

- dubbo-demo-annotation-api：提供演示接口
- dubbo-demo-annotation-consumer：服务消费者
- dubbo-demo-annotation-provider：服务提供者

### 1.3.2 dubbo-demo-annotation-api模块

跟dubbo-demo-api-api模块相同。

### 1.3.3 dubbo-demo-annotation-provider模块

#### 1.3.3.1 DemoServiceImpl

同上，实现DemoService接口，但是需要使用@Service来标记该实现类要通过Provider暴露服务。

````java
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
````

#### 1.3.3.2 dubbo-provider.properties

通过dubbo-provider.properties来对dubbo的provider进行相应的配置。在resource/spring下创建dubbo-provider.properties文件，内容如下

```properties
dubbo.application.name=dubbo-demo-annotation-provider
dubbo.application.qos.port=22222
dubbo.protocol.name=dubbo
dubbo.protocol.port=20880
```

#### 1.3.3.3 DemoServiceProvider

与xml配置相同，需要获取应用上下文，只不过使用AnnotationConfigApplicationContext来获取，并且需要将dubbo的配置通过@Configuration形式注入到Spring，并制定需要扫面的包路径，以及配置文件所在路径。内容如下：

```java
package learn.demo.dubbo.annotation.provider;

import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by shirukai on 2019-06-20 14:06
 * DemoService提供者
 */
public class DemoServiceProvider {
    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ProviderConfiguration.class);
        context.start();
        System.in.read();
    }

    @Configuration
    @EnableDubbo(scanBasePackages = "learn.demo.dubbo.annotation.provider")
    @PropertySource("classpath:/spring/dubbo-provider.properties")
    static class ProviderConfiguration {
        @Bean
        public RegistryConfig registryConfig() {
            RegistryConfig registryConfig = new RegistryConfig();
            registryConfig.setAddress("multicast://224.5.6.7:1234");
            return registryConfig;
        }
    }

}
```

### 1.3.4 dubbo-demo-annotation-consumer模块

#### 1.3.4.1 dubbo-consumer.properties

通过dubbo-consumer.properties来配置dubbo的consumer的相关配置，如服务名称、注册中心地址等

```java
dubbo.application.name=dubbo-demo-annotation-consumer
dubbo.registry.address=multicast://224.5.6.7:1234
```

#### 1.3.4.2 DemoServiceComponent

通过@Component创建DemoService组件，并通过@Reference注入远程Dubbo提供的接口实例。

```java
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

```

#### 1.3.4.3 DemoServiceConsumer

通过@Configuration注入dubbo相关配置，并通过AnnotationConfigApplicationContext获取应用上下文。

```java
package learn.demo.dubbo.annotation.consumer;

import lear.demo.dubbo.annotation.api.DemoService;
import learn.demo.dubbo.annotation.consumer.comp.DemoServiceComponent;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by shirukai on 2019-06-20 14:10
 * DemoService消费者
 */
public class DemoServiceConsumer {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ConsumerConfiguration.class);
        context.start();
        DemoService service = context.getBean("demoServiceComponent", DemoServiceComponent.class);
        String message = service.sayHello("world");
        System.out.println(message);
    }

    @Configuration
    @EnableDubbo(scanBasePackages = "learn.demo.dubbo.annotation.consumer.comp")
    @PropertySource("classpath:/spring/dubbo-consumer.properties")
    @ComponentScan(value = {"learn.demo.dubbo.annotation.consumer.comp"})
    static class ConsumerConfiguration {

    }
}
```

# 2 SpringBoot整合Dubbo

SpringBoot整合Dubbo简洁的不能再简洁，不需要额外的配置，只需要引入几个依赖包即可。

## 2.1 项目准备

首先创建一个名为dubbo-demo-springboot的模块，然后再该模块下创建三个子模块

```
dubbo-demo-springboot/
├── dubbo-demo-springboot-api
├── dubbo-demo-springboot-consumer
├── dubbo-demo-springboot-provider
└── pom.xml
```

- dubbo-demo-springboot-api：提供演示接口
- dubbo-demo-springboot-consumer：服务消费者
- dubbo-demo-springboot-provider：服务提供者

## 2.2 dubbo-demo-springboot-api模块

与之前api模块一样，定义DemoService接口。

## 2.3 dubbo-demo-springboot-provide模块

### 2.3.1 引入依赖

因为此项目是springboot项目，这里将springboot模块添加到parent

```xml
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.0.0.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
```

引入springboot starter

```xml
       <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
```

引入dubbo springboot starter

```xml
        <!-- Dubbo Spring Boot Starter -->
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo-spring-boot-starter</artifactId>
            <version>2.7.1</version>
        </dependency>
```

引入dubbo依赖

```xml
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo</artifactId>
            <version>2.7.2</version>
        </dependency>
```

引入api依赖

```
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo</artifactId>
            <version>2.7.2</version>
        </dependency>
```

### 2.3.2 application.properties

在resource创建application.properties配置文件，里面添加dubbo相应配置

```properties
spring.application.name=dubbo-spring-boot-provider
# dubbo
dubbo.scan.base-packages=learn.demo.dubbo.springboot.provider
dubbo.protocol.name=dubbo
dubbo.protocol.port=12345
dubbo.registry.address=multicast://224.5.6.7:1234
```

### 2.3.3 DemoServiceImpl

实现DemoService接口，并使用dubbo的@Service注解标记此类为dubbo暴露的服务实例

```java
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

```

### 2.3.4 DemoServiceProvider

Dubbo 服务提供实现，主要启动SpringBoot即可。

```java
package learn.demo.dubbo.springboot.provider;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by shirukai on 2019-06-20 15:08
 */
@SpringBootApplication
public class DemoServiceProvider {
    public static void main(String[] args) {
        SpringApplication.run(DemoServiceProvider.class, args);
    }
}

```

## 2.4 dubbo-demo-springboot-consumer模块

### 2.3.1 引入依赖

与provider一样，引入springboot-starter、dubbo-spring-boot-starter、dubbo、api依赖。另外这里提供了一个REST接口，所以需要引入web服务相关的依赖

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
```



### 2.3.2 application.properties

```properties
spring.application.name=dubbo-spring-boot-consumer
# dubbo
dubbo.scan.base-packages=learn.demo.dubbo.springboot.consumer
dubbo.registry.address=multicast://224.5.6.7:1234
```

### 2.3.4 DemoController

提供一个REST接口，并调用RPC服务

```java
package learn.demo.dubbo.springboot.consumer.controller;

import learn.demo.dubbo.springboot.api.DemoService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by shirukai on 2019-06-20 16:03
 */
@RestController
public class DemoController {
    @Reference(version = "1.0.0")
    private DemoService demoService;

    @GetMapping(value = "/sayHello")
    public String sayHello(@RequestParam("name") String name) {
        return demoService.sayHello(name);
    }
}

```

### 2.3.5 DemoServiceConsumer

Dubbo Consumser服务启动,，正常启动SpringBoot即可。

```java
package learn.demo.dubbo.springboot.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by shirukai on 2019-06-20 16:00
 */
@SpringBootApplication
public class DemoServiceConsumer {
    public static void main(String[] args) {
        SpringApplication.run(DemoServiceConsumer.class, args);
    }
}

```


