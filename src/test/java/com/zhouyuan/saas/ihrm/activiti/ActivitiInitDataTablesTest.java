package com.zhouyuan.saas.ihrm.activiti;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.junit.Test;

public class ActivitiInitDataTablesTest extends ActivitiApplicationTests {

    @Test
    public void initTbls(){

        //1.创建ProcessEngineConfiguration对象
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml");

        //2.创建ProcesEngine对象
        ProcessEngine engine = configuration.buildProcessEngine();

        //3.输出processEngine对象
        System.out.println(engine);
    }
}
