package com.zhouyuan.saas.ihrm.activiti.upgrade;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;

public class SuspendOrActivateProcess {

    public static void main(String[] args) {
        //suspendOrActivateProcessDefinition("holiday");
        suspendOrActivateProcessInstance("2501");
    }

    /**
     * 全部流程实例挂起与激活
     * 如果流程实例已经被挂起，再去执行这个流程的任务就会报
     * org.activiti.engine.ActivitiException: Cannot complete a suspended task
     * @param definitionKey 流程定义的key
     */
    public static void suspendOrActivateProcessDefinition(String definitionKey){

        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //2.得到RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();

        //3.查询流程定义的对象
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(definitionKey)
                .singleResult();

        //4.得到当前流程定义的实例是否都为暂停状态
        boolean suspended = processDefinition.isSuspended();

        String processDefinitionId = processDefinition.getId();
        //5.判断
        if (suspended){
            //说明是暂停，就可以激活操作
            repositoryService.activateProcessDefinitionById(processDefinitionId,true,null);
            System.out.println("流程定义：" + processDefinitionId + "激活");
        }else {
            repositoryService.suspendProcessDefinitionById(processDefinitionId,true,null);
            System.out.println("流程定义：" + processDefinitionId + "挂起");
        }
    }

    /**
     *　单个流程实例挂起与激活
     * @param processInstanceId 流程实例ID
     */
    public static void suspendOrActivateProcessInstance(String processInstanceId){

        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //2.得到RuntimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();

        //3.查询流程实例对象
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey("12345")
                //.processInstanceId(processInstanceId)
                .singleResult();

        //4.得到当前流程例是否为暂停状态
        boolean suspended = processInstance.isSuspended();

        processInstanceId = processInstance.getId();

        //5.判断
        if (suspended){
            //说明是暂停，就可以激活操作
            runtimeService.activateProcessInstanceById(processInstanceId);
            System.out.println("流程实例：" + processInstanceId + "激活");
        }else {
            runtimeService.suspendProcessInstanceById(processInstanceId);
            System.out.println("流程实例：" + processInstanceId + "挂起");
        }
    }
}
