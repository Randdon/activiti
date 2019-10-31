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
 * 排他网关测试类
 * 排他网关（也叫异或（XOR）网关，或叫基于数据的排他网关），用来在流程中实现决策。
 * 经过排他网关必须要有一条且只有一条分支走。
 */
public class ExclusiveGatewayProcessTest extends ActivitiApplicationTests {

    private final String PROCESS_DEFINE_KEY = "holidayExclusiveGateway";


    /**
     * 两个分支条件都满足，排他网关会选择id较小的任务分支进行处理
     */
    @Test
    public void bothTrueTest(){
        holidayExclusiveGateway(5F);
    }

    /**
     * 如果分支条件都不满足，流程依旧会报错：
     * org.activiti.engine.ActivitiException:
     * No outgoing sequence flow of the exclusive gateway '_5' could be selected for continuing the process
     */
    @Test
    public void NoTrueTest(){
        holidayExclusiveGateway(0F);
    }


    public void holidayExclusiveGateway(Float num){

        //部署
        BaseActivitiDeploy.deploy("diagram/holidayExclusiveGateway.bpmn",
                "diagram/holidayExclusiveGateway.png");

        //启动
        BaseActivitiStartProcessInstance.startProcessInstance(PROCESS_DEFINE_KEY);

        //查询任务
        Task task = BaseActivitiQueryTask.queryTask(PROCESS_DEFINE_KEY, "spiderman").get(0);

        //构造流程变量
        Map<String,Object> variable = new HashMap<>(1);
        Holiday holiday = new Holiday();
        holiday.setNum(num);
        variable.put("holiday",holiday);

        //完成第一步的同时注入流程变量
        BaseActiveCompleteTask.completeTask(task.getId(),variable);

        //完成第二步
        BaseActiveCompleteTask.completeTaskByAssignee(PROCESS_DEFINE_KEY,"eraserHead",null);

        /**
         * 此时，因为流程变量中num=5，既满足num>=1又满足num>3，但是因为用了排他网关（ExclusiveGateway），
         * 有且仅有一个为true的分支可以通过，那么在这种有多个为true分支的情况下，排他网关会选择节点的id值最小的那个分支
         * 即在此例中的总经理审批（id=_6），而不是人事归档（id=_7），所以，在业务中出现多个条件都满足的情况下，我们可以将
         * id值较小的那个分支的节点设置为业务上所期望的默认业务
         */
        //第三步：总经理审批
        BaseActiveCompleteTask.completeTaskByAssignee(PROCESS_DEFINE_KEY,"ironman",null);

        //第四步
        BaseActiveCompleteTask.completeTaskByAssignee(PROCESS_DEFINE_KEY,"allMight",null);
    }
}
