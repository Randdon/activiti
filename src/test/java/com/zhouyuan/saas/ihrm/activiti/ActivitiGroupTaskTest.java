package com.zhouyuan.saas.ihrm.activiti;

import com.zhouyuan.saas.ihrm.activiti.base.BaseActiveCompleteTask;
import com.zhouyuan.saas.ihrm.activiti.base.BaseActivitiDeploy;
import com.zhouyuan.saas.ihrm.activiti.base.BaseActivitiQueryTask;
import com.zhouyuan.saas.ihrm.activiti.base.BaseActivitiStartProcessInstance;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 组任务-Candidate-Users流程定义、部署、启动、查询、候选人认领任务、退回任务、交接任务、完成任务流程测试类
 * 流程中的参与者的身份变化会记录在`act_hi_identitylink` 表中
 */
public class ActivitiGroupTaskTest extends ActivitiApplicationTests {

    private final String PROCESS_DEFINE_KEY = "holidayGroupTask";

    private final Logger logger = LoggerFactory.getLogger(ActivitiGroupTaskTest.class);

    @Test
    public void groupTaskTest(){
        //部署组任务的流程定义
        BaseActivitiDeploy.deploy("diagram/holidayGroupTask.bpmn","diagram/holidayGroupTask.png");
        logger.info("流程部署完成！！");

        //启动一个流程实例
        BaseActivitiStartProcessInstance.startProcessInstance(PROCESS_DEFINE_KEY);
        logger.info("流程实例启动完成！！");

        //先完成请假单申请任务，此时流程走到部门经理审批节点，但从数据库中可以看出此节点任务当下并无负责人，即为null
        BaseActiveCompleteTask.completeTaskByAssignee(PROCESS_DEFINE_KEY,"Tom",null);
        logger.info("{}完成任务" , "Tom");

        //根据候选人之一“Harvey”来查询当前任务
        Task taskOfHarvey = BaseActivitiQueryTask.queryGroupTask(PROCESS_DEFINE_KEY,"Harvey");
        //根据候选人之一“Mclellan”来查询当前任务
        Task taskOfMclellan = BaseActivitiQueryTask.queryGroupTask(PROCESS_DEFINE_KEY,"Mclellan");
        logger.info("候选人Harvey和Mclellan都进行了任务查询，是否查到了同样的任务：{}",
                taskOfHarvey.getId().equals(taskOfMclellan.getId()));

        if (null != taskOfHarvey){
            ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
            TaskService taskService = processEngine.getTaskService();
            //如果候选人Harvey查询到了任务，则让Harvey认领该任务从而成为任务的负责人，认领后数据库中该任务的负责人就会成为Harvey
            taskService.claim(taskOfHarvey.getId(),"Harvey");
            //当Harvey认领任务后，另一个候选人Mclellan就不能以候选人身份查询到任务，即查出的任务为null
            taskOfMclellan = BaseActivitiQueryTask.queryGroupTask(PROCESS_DEFINE_KEY,"Mclellan");
            logger.info("任务被候选人认领后，其他候选人还能否查询到该任务：{}",null != taskOfMclellan);

            if (null != taskOfMclellan){
                //如果候选人Harvey认领到任务后，候选人Mclellan还能以候选人身份查询到该任务，则看其是否能够认领该任务
                taskService.claim(taskOfMclellan.getId(),"Mclellan");
            }

            /**
             * 认领任务后也可以通过 setAssignee 方法来退回任务，第二个参数便是指定该任务的负责人，
             * 置为null便可以退回该任务到无人负责只有候选人的状况
             * 也可以通过 setAssignee 方法将任务委托给其它用户负责，注意被委托的用户可以不是候选人（建议不要这样使用）
             * 再次观察数据库中部门经理审批环节的负责人是否退回到null了
             */
            taskOfHarvey = BaseActivitiQueryTask.queryTask(PROCESS_DEFINE_KEY,"Harvey").get(0);
            if (null != taskOfHarvey){
                //退回任务前先确认是否为该任务的负责人，不是负责人不能进行任务退回操作
                taskService.setAssignee(taskOfHarvey.getId(),null);
            }

            //Harvey退回任务后，由Mclellan再次认领任务
            taskOfMclellan = BaseActivitiQueryTask.queryGroupTask(PROCESS_DEFINE_KEY,"Mclellan");
            if (null != taskOfMclellan){

                //任务负责人改为Mclellan
                taskService.claim(taskOfHarvey.getId(),"Mclellan");
                taskOfMclellan = BaseActivitiQueryTask.queryTask(PROCESS_DEFINE_KEY,"Mclellan").get(0);

                //任务交接，任务的接收者可以不是候选人
                if (null != taskOfMclellan){
                    //Mclellan将任务交接给SpiderMan，数据库中的任务负责人变为SpiderMan
                    taskService.setAssignee(taskOfMclellan.getId(),"SpiderMan");
                }
                Task spiderManTask = BaseActivitiQueryTask.queryTask(PROCESS_DEFINE_KEY, "SpiderMan").get(0);

                if (null != spiderManTask){
                    //完成任务
                    BaseActiveCompleteTask.completeTask(spiderManTask.getId(),null);
                }
            }

        }
    }

    @Test
    public void finishProcess(){
        BaseActiveCompleteTask.completeTask("23",null);
    }

}
