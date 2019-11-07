package com.zhouyuan.saas.ihrm.activiti;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

/**
 * 因为用com.zhouyuan.saas.ihrm.activiti.config.DemoApplicationConfiguration配置了Spring Security，所以这里就不需要
 * 自动扫描了，exclude = SecurityAutoConfiguration.class
 */
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class ActivitiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActivitiApplication.class, args);
    }

}
