package com.zhouyuan.saas.ihrm.activiti;

import com.zhouyuan.saas.ihrm.activiti.base.BaseActiveCompleteTask;
import com.zhouyuan.saas.ihrm.activiti.base.BaseActivitiDeploy;
import com.zhouyuan.saas.ihrm.activiti.base.BaseActivitiQueryTask;
import com.zhouyuan.saas.ihrm.activiti.base.BaseActivitiStartProcessInstance;
import com.zhouyuan.saas.ihrm.activiti.upgrade.entity.Holiday;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * 并行网关测试类
 * 并行网关允许将流程分成多条分支（fork），也可以把多条分支汇聚到一起（join），并行网关的功能是基于进入和外出顺序流的
 * 并行网关不会解析条件。 即使顺序流中定义了条件，也会被忽略。
 * 并行网关在业务应用中常用于会签任务，会签任务即多个参与者共同办理的任务。
 */
public class ParallelGatewayProcessTest extends ActivitiApplicationTests {

    private final String PROCESS_DEFINE_KEY = "holidayParallelGateway";


    /**
     * 两个并行网关，一个用来创建并发分支，一个用来汇聚分支
     */
    @Test
    public void parallelGatewayForkAndJoinTest(){
        holidayParallelGateway("diagram/holidayParallelGateway.bpmn",PROCESS_DEFINE_KEY);
    }

    /**
     * 只用一个并行网关来汇聚分支，不用并行网关而用顺序流的两条分支来形成多分支同时进行任务的场景
     * 看是否可以用这样的方式来代替上一个流程例子里的第一个网关，而达到同样的效果
     * 答案是肯定的，即可以用无条件的多支顺序流来代替并行网关的fork功能
     */
    @Test
    public void parallelGatewayWithoutForkTest(){
        holidayParallelGateway("diagram/holidayParallelGatewayWithoutFork.bpmn","holidayParallelGatewayWithoutFork");
    }
    public void holidayParallelGateway(String bpmnPath,String processDefineKey){

        //部署
        BaseActivitiDeploy.deploy(bpmnPath,
                "diagram/holidayParallelGateway.png");

        //启动
        BaseActivitiStartProcessInstance.startProcessInstance(processDefineKey);

        //查询任务
        Task task = BaseActivitiQueryTask.queryTask(processDefineKey, "spiderman").get(0);

        //构造流程变量
        Map<String,Object> variable = new HashMap<>(1);
        Holiday holiday = new Holiday();
        holiday.setNum(5F);
        variable.put("holiday",holiday);

        //完成第一步的同时注入流程变量
        BaseActiveCompleteTask.completeTask(task.getId(),variable);

        //完成第二步
        BaseActiveCompleteTask.completeTaskByAssignee(processDefineKey,"eraserHead",null);

        //第三步：总经理审批
        BaseActiveCompleteTask.completeTaskByAssignee(processDefineKey,"ironman",null);

        /**
         * 第四步：人事归档，走完该步骤后会在`act_ru_task`、`act_ru_execution`、`act_hi_actinst`和`act_hi_taskinst`
         * 表中几乎同时出现第五步和第六步两条记录
         */
        BaseActiveCompleteTask.completeTaskByAssignee(processDefineKey,"allMight",null);

        /**
         * 第五步：财务会计，走完该步骤后`act_ru_task` 表中的第五步的记录会消失，第六步记录还在
         * act_ru_execution表中的第五步记录的IS_ACTIVE字段会由1变为0，第六步记录该字段仍为1
         * act_hi_actinst表中记录第五步任务的结束时间，并紧接着这个结束时间，开始记录了一条ParallelGateway的完整记录，以表明第五步
         * 任务已到达并行网关的聚合点
         */
        BaseActiveCompleteTask.completeTaskByAssignee(processDefineKey,"Hulk",null);

        /**
         * 第六步：行政考勤，走完该步骤后`act_ru_task`和act_ru_execution表中的该实例的任务记录会全部消失，
         * act_hi_actinst表中记录第六步任务的结束时间，并紧接着这个结束时间，开始记录了一条ParallelGateway的完整记录，以表明第六步
         * 任务也已到达并行网关的聚合点，然后走向并行网关的下一节点
         */
        BaseActiveCompleteTask.completeTaskByAssignee(processDefineKey,"Naruto",null);

    }
}
