package com.zhouyuan.saas.ihrm.activiti;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;

/**
 * 删除流程实例测试
 */
public class ProcessInstanceDeleteTest extends ActivitiApplicationTests {

    @Test
    public void deleteTest(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //启动流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("holidayExclusiveGateway");
        //删除`act_ru_execution` `act_ru_task` act_ru_identitylink里该任务实例的记录
        runtimeService.deleteProcessInstance(processInstance.getId(),"testDeleteProcessInstance");
        HistoryService historyService = processEngine.getHistoryService();
        //删除`act_hi_procinst``act_hi_actinst` act_hi_identitylink act_hi_taskinst里的该任务实例的记录
        historyService.deleteHistoricProcessInstance(processInstance.getId());

    }

}
