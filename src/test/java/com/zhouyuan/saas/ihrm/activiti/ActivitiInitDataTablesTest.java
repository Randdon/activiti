package com.zhouyuan.saas.ihrm.activiti;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.junit.Test;

public class ActivitiInitDataTablesTest extends ActivitiApplicationTests {

    /**
     * 初始化Activiti7所需要的25张数据库表
     */
    @Test
    public void initTbls(){

        //1.创建ProcessEngineConfiguration对象，点进这个方法可以看到，第二个参数beanName默认是processEngineConfiguration
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml");

        //2.创建ProcesEngine对象
        ProcessEngine engine = configuration.buildProcessEngine();

        //3.输出processEngine对象
        System.out.println(engine);
    }

    @Test
    public void initTbls1(){

        /**
         * 1.创建ProcessEngineConfiguration对象，第一个参数代表配置文件的名称，第二个参数代表配置文件中的ProcessEngine配置对象的bean的id
         * 配置文件的名称也可以改为自定义的，不过都要保持代码和配置文件的内容的一致性
         */
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.01cfg.xml","changedBeanName");

        //2.创建ProcesEngine对象
        ProcessEngine engine = configuration.buildProcessEngine();

        //3.输出processEngine对象
        System.out.println(engine);
    }
}
