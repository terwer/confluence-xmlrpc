package com.terwergreen.confluence.xmlrpc;

import com.terwergreen.confluence.xmlrpc.utils.ConfluenceConstants;
import com.terwergreen.confluence.xmlrpc.utils.SpringBeanUtils;
import org.apache.xmlrpc.webserver.XmlRpcServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ConfluenceXmlrpcApplication {

    public static void main(String[] args) {
        // 启动Spring Boot
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ConfluenceXmlrpcApplication.class, args);
        // 提供给上下文工具
        SpringBeanUtils.setContext(applicationContext);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Bean
    public ServletRegistrationBean registerServlet() {
        return new ServletRegistrationBean(
                new XmlRpcServlet(),
                ConfluenceConstants.CONSTANT_XMLRPC_NAME // xml-rpc访问接口
        );
    }

}
