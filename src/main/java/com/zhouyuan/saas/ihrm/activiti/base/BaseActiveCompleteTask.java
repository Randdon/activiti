package com.zhouyuan.saas.ihrm.activiti.base;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;

import java.util.Map;

public class BaseActiveCompleteTask {

    public static void main(String[] args) {
        completeTask("2505",null);
    }

    /**
     *  背后操作的表：
     *  act_hi_actinst
     *  act_hi_identitylink
     *  act_hi_taskinst
     *  act_ru_identitylink
     *  act_ru_task
     *  `act_ru_execution`
     * @param taskId 任务ID
     * @param variable 在完成任务时注入流程变量
     */
    public static void completeTask(String taskId, Map<String,Object> variable){

        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //2.得到TaskService对象
        TaskService taskService = processEngine.getTaskService();

        //3.处理任务,结合当前用户任务列表的查询操作的话,例如任务ID:5002
        taskService.complete(taskId,variable);
    }

    /**
     * 根据流程定义主键和任务执行人完成任务
     * @param definitionKey
     * @param assignee
     * @param variable 在完成任务时注入流程变量*
     */
    public static void completeTaskByAssignee(String definitionKey,String assignee,Map<String,Object> variable){
        Task task = BaseActivitiQueryTask.queryTask(definitionKey,assignee).get(0);
        if (null != task){
            completeTask(task.getId(),variable);
            System.out.println(task.getId() + "任务执行完毕");
        }
    }

    /**
     * 根据流程定义主键和任务执行人完成任务
     * @param definitionKey
     * @param assignee
     * @param variable 在完成任务前通过任务ID注入流程变量*
     */
    public static void completeTaskByAssigneeWithVariable(
            String definitionKey,String assignee,Map<String,Object> variable){
        Task task = BaseActivitiQueryTask.queryTask(definitionKey,assignee).get(0);
        if (null != task){
            injectProcessVariableByTaskIdBeforeCompleteTask(task.getId(),variable);
            System.out.println(task.getId() + "任务执行完毕");
        }
    }

    /**
     * 在完成任务前通过任务ID注入流程变量*
     * @param taskId 任务ID
     * @param variable 流程变量
     */
    public static void injectProcessVariableByTaskIdBeforeCompleteTask(String taskId, Map<String,Object> variable){

        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //2.得到TaskService对象
        TaskService taskService = processEngine.getTaskService();

        //通过taskID注入流程变量
        taskService.setVariables(taskId,variable);

        //3.处理任务,结合当前用户任务列表的查询操作的话,例如任务ID:5002
        taskService.complete(taskId);
    }
}
