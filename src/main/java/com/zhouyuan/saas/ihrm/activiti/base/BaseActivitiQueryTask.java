package com.zhouyuan.saas.ihrm.activiti.base;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;

import java.util.List;

public class BaseActivitiQueryTask {

    /**
     * 查询任务列表，查询对数据库表没有影响
     * @param processDefinitionKey 任务流程定义名称
     * @param assignee 任务负责人
     * @return
     */
    public static List<Task> queryTask(String processDefinitionKey, String assignee){

        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //2.得到TaskService对象
        TaskService taskService = processEngine.getTaskService();

        //3.根据流程定义的key,负责人assignee来实现当前用户的任务列表查询
        List<Task> taskList = taskService.createTaskQuery()
                .processDefinitionKey(processDefinitionKey)
                .taskAssignee(assignee)
                //.singleResult()//如果明确知道只有一个任务则可以用这个方法获取这个任务，如果可能有多个任务则不可以用这个方法
                .list();
        taskList.stream()
                .forEach(task -> {
                    System.out.println("流程实例ID：" + task.getProcessDefinitionId());
                    System.out.println("任务ID：" + task.getId());
                    System.out.println("任务负责人：" + task.getAssignee());
                    System.out.println("任务名称：" + task.getName());
                });

        return taskList;
    }

    /**
     * 根据候选人查询任务
     * @param processDefinitionKey
     * @param candidateUser
     * @return
     */
    public static Task queryGroupTask(String processDefinitionKey, String candidateUser){

        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //2.得到TaskService对象
        TaskService taskService = processEngine.getTaskService();

        //3.根据流程定义的key和候选人来实现当前用户的任务列表查询
        Task task = taskService.createTaskQuery()
                .processDefinitionKey(processDefinitionKey)
                .taskCandidateUser(candidateUser)//该任务的候选人
                .singleResult();

        if (null != task){
            System.out.println("流程实例ID：" + task.getProcessDefinitionId());
            System.out.println("任务ID：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }
        return task;
    }
    public static void main(String[] args) {
        queryTask("holiday","Tom");
    }
}
