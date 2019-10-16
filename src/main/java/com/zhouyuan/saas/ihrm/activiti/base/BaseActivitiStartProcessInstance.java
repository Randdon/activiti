package com.zhouyuan.saas.ihrm.activiti.base;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;

import java.util.HashMap;
import java.util.Map;

/**
 * 启动流程实例，前提是已经完成流程定义的部署工作
 * 影响到的数据库表有：
 * `act_hi_actinst` 已完成的活动信息
 * `act_hi_identitylink` 参与者信息
 * `act_hi_procinst` 流程实例
 * `act_hi_taskinst` 历史任务实例
 * `act_ru_execution` 执行表
 * `act_ru_identitylink` 正在执行的参与者信息
 * `act_ru_task` 目前正在执行的任务
 */
public class BaseActivitiStartProcessInstance {

    public static void main(String[] args) {

        //startProcessInstance("holiday");
        //startProcessInstanceV2("holiday","12345");
        startProcessInstanceByUELExpression("holiday1");
    }

    public static void startProcessInstance(String processDefinitionKey){
        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //2.得到RunService对象
        RuntimeService runtimeService = processEngine.getRuntimeService();

        //3.创建流程实例  流程定义的key需要知道 holiday
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey);

        //4.输出流程实例的相关信息
        System.out.println("部署ID：" + processInstance.getDeploymentId());
        System.out.println("流程定义ID：" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例ID：" + processInstance.getId());
        System.out.println("流程实例ID：" + processInstance.getProcessInstanceId());
        System.out.println("活动ID：" + processInstance.getActivityId());

    }

    /**
     * 该方法主要用于将业务系统与Activiti整合起来
     * 启动流程实例，添加进业务标识businessKey
     * 本质：act_ru_execution和`act_hi_procinst` 表中的businessKey的字段要存入业务标识
     * @param processDefinitionKey
     */
    public static void startProcessInstanceV2(String processDefinitionKey,String businessKey){
        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //2.得到RunService对象
        RuntimeService runtimeService = processEngine.getRuntimeService();

        //3.启动流程实例,同时还要指定业务标识businessKey  它本身就是请假单的id
        //第一个参数：是指流程定义key
        //第二个参数：业务标识businessKey
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey,businessKey);

        //4.输出processInstance相关的属性,取出businessKey使用:processInstance.getBusinessKey()
        System.out.println(processInstance.getBusinessKey());
    }

    /**
     * 采用UEL表达式的方式动态配置每个任务节点的负责人
     * @param processDefinitionKey
     */
    public static void startProcessInstanceByUELExpression(String processDefinitionKey){

        //先部署采用了UEL表达式来指定任务负责人的资源文件
        //采用diagram/holiday_UEL.bpmn和diagram/holiday_UEL.png的名称会部署失败，提示找不到资源
        BaseActivitiDeploy.deploy("diagram/holidayUEL.bpmn","diagram/holidayUEL.png");

        //3.设置流程定义的assignee的值 用户可以在界面上设置流程的执行人
        Map<String,Object> uelDynamicAssignee = new HashMap<>(3);
        uelDynamicAssignee.put("assignee0","Tom");
        uelDynamicAssignee.put("assignee1","Jerry");
        uelDynamicAssignee.put("assignee2","Winnie");

        startProcessInstanceWithProcessVariable(processDefinitionKey,uelDynamicAssignee);
    }

    /**
     * 启动流程实例，并设置流程变量
     * @param processDefinitionKey 流程定义key
     * @param processVariable 流程变量
     */
    public static void startProcessInstanceWithProcessVariable(
            String processDefinitionKey, Map<String,Object> processVariable){

        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //2.得到RunService对象
        RuntimeService runtimeService = processEngine.getRuntimeService();

        //3.启动流程实例,同时还要设置流程变量
        ProcessInstance processInstance =
                runtimeService.startProcessInstanceByKey(processDefinitionKey,processVariable);

        //4.输出processInstance相关的属性
        System.out.println(processInstance.getName());
    }
}
