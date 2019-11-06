package com.zhouyuan.saas.ihrm.activiti.spring;

import com.zhouyuan.saas.ihrm.activiti.ActivitiApplicationTests;
import com.zhouyuan.saas.ihrm.activiti.config.SecurityUtil;
import org.activiti.api.process.model.ProcessDefinition;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Activiti7整合Springboot：
 * 1.十分依赖Spring Security
 *
 * 2.将bpmn文件放于/src/main/resources/processes/目录下即可实现流程定义的自动部署，自动部署的`act_re_procdef`
 * 表里的流程定义ID为一串数字，如“8e662598-003f-11ea-8e52-e454e858234f”，而用之前所学习的手动部署方式的该表中的流程定义ID为流程
 * 定义KEY+一串数字，如：“holidayParallelGateway:1:8f09b89c-003f-11ea-8e52-e454e858234f”
 *
 * 3.这种方式只能创建17张表，少了8张历史表，act_hi_*，即使用原来的手动部署方式来部署，不把pom依赖改为原来的依赖还用
 * activiti-spring-boot-starter依赖的话，也还是只会建17张表，需要将pom和部署方式都改为原来的方式才能建25张表
 *
 * 4.SpringbootFirstCandidateGroups.bpmn流程定义使用Candidate-Groups来指定任务负责人，这里的groups使用的是
 * Spring Security的配置类com.zhouyuan.saas.ihrm.activiti.config.DemoApplicationConfiguration#myUserDetailsService()
 * 里的GROUP_activitiTeam
 */
public class ActivitiIntegrateSpringbootTest extends ActivitiApplicationTests {

    private final Logger logger = LoggerFactory.getLogger(ActivitiIntegrateSpringbootTest.class);

    private final String PROCESS_DEFINE_KEY = "activitiIntegrateSpringbootDemo";
    @Autowired
    ProcessRuntime processRuntime;//流程定义相关操作

    @Autowired
    SecurityUtil securityUtil;//Spring Security相关的工具类

    /**
     * 查看 流程定义
     */
    @Test
    public void defininationTest(){

        securityUtil.logInAs("salaboy");

        Page<ProcessDefinition> processDefinitionPage =
                processRuntime.processDefinitions(Pageable.of(0, 10));

        logger.info("查询到了{}条流程定义",processDefinitionPage.getTotalItems());

        processDefinitionPage.getContent().forEach(definition -> logger.info("流程定义信息：{}",definition));

    }

    /**
     * 启动流程实例
     */
    @Test
    public void startProcessInstanceTest(){
        securityUtil.logInAs("salaboy");
        ProcessInstance processInstance = processRuntime.start(ProcessPayloadBuilder.start()
                .withProcessDefinitionKey(PROCESS_DEFINE_KEY)
                .withProcessInstanceName("Springboot+Activiti`s First Test")
                .build());
        logger.info("启动流程实例：{}",processInstance);
    }
}
