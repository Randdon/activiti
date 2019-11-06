package com.zhouyuan.saas.ihrm.activiti.spring;

import com.zhouyuan.saas.ihrm.activiti.ActivitiApplicationTests;
import org.activiti.engine.RepositoryService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 * 通过配置文件实例化Activiti所需要的bean，输出repositoryService并且创建25张数据库表
 */
@ContextConfiguration("classpath:activiti-spring.xml")
public class ActivitiIntegrateSpringTest extends ActivitiApplicationTests {

    @Autowired
    RepositoryService repositoryService;

    @Test
    public void integrateTest(){
        System.out.println("#################################"+repositoryService+"###############################");
    }
}
