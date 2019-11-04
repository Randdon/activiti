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
 * 包含网关测试类
 * 包含网关可以看做是排他网关和并行网关的结合体。 和排他网关一样，你可以在外出顺序流上定义条件，包含网关会解析它们。
 * 但是主要的区别是包含网关可以选择多于一条顺序流，这和并行网关一样
 * 所有外出顺序流的条件都会被解析，结果为 true 的顺序流会以并行方式继续执行
 */
public class InclusiveGatewayProcessTest extends ActivitiApplicationTests {

    private final String PROCESS_DEFINE_KEY = "physical";


    /**
     * 跟并行网关测试类一样，这里预计也可以去掉第一个包含网关，用有条件的顺序流进行代替，
     * 但是没有做跟并行网关那样的测试来验证这个预估
     */
    @Test
    public void inclusiveGatewayTest(){
        inclusiveGatewayPhysical(2,PROCESS_DEFINE_KEY);
    }

    /**
     * @param userType ：1代表普通职员，2代表领导层人员；领导层人员可以享受附加体检项目
     * @param processDefineKey
     */
    public void inclusiveGatewayPhysical(Integer userType,String processDefineKey){

        //部署
        BaseActivitiDeploy.deploy("diagram/physical.bpmn", "diagram/physical.png");

        //启动
        BaseActivitiStartProcessInstance.startProcessInstance(processDefineKey);

        //查询任务
        Task task = BaseActivitiQueryTask.queryTask(processDefineKey, "James").get(0);

        //构造流程变量
        Map<String,Object> variable = new HashMap<>(1);
        variable.put("userType",userType);

        /**
         * 完成第一步的同时注入流程变量
         * 走完该步骤后会在`act_ru_task`、`act_ru_execution`、`act_hi_actinst`和`act_hi_taskinst`
         * 表中几乎同时出现常规检查、血液检查和附加项检查3条记录，如果不是领导层，则没有附加项检查的记录
         */
        BaseActiveCompleteTask.completeTask(task.getId(),variable);

        /**
         * 完成常规检查，走完该步骤后`act_ru_task` 表和act_ru_execution表中的该步的记录会消失，
         * act_hi_actinst表中记录该步任务的结束时间，并紧接着这个结束时间，开始记录了一条只有开始时间的InclusiveGateway的记录，
         * 以表明该步已到达包含网关的聚合点，act_ru_execution表中也会多一条InclusiveGateway的记录
         */
        BaseActiveCompleteTask.completeTaskByAssignee(processDefineKey,"Chopper",null);

        /**
         * 完成血液检查，走完该步骤后`act_ru_task` 表和act_ru_execution表中的该步的记录会消失，但都会增加吃早餐的记录
         * act_hi_actinst表中记录该步任务的结束时间，并紧接着这个结束时间，开始记录了一条只有开始时间的吃早餐的记录，
         */
        BaseActiveCompleteTask.completeTaskByAssignee(processDefineKey,"Zoro",null);

        /**
         * 吃早餐，走完该步骤后`act_ru_task` 表和act_ru_execution表中的该步的记录会消失，
         * act_hi_actinst表中记录该步任务的结束时间，并紧接着这个结束时间，开始再记录一条只有开始时间的InclusiveGateway的记录，
         * 以表明该步已到达包含网关的聚合点，act_ru_execution表中也会再多一条InclusiveGateway的记录
         */
        BaseActiveCompleteTask.completeTaskByAssignee(processDefineKey,"Paul",null);

        /**
         * 附加检查，走完该步骤后`act_ru_task` 表和act_ru_execution表中的该流程实例的所有记录会消失，
         * act_hi_actinst表中记录该步任务的结束时间，并紧接着这个结束时间，开始再记录一条InclusiveGateway的完整记录，
         * 并给前两条InclusiveGateway记录打上结束时间，任务走向endEvent
         */
        BaseActiveCompleteTask.completeTaskByAssignee(processDefineKey,"Wade",null);

    }
}
