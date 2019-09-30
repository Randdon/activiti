package com.zhouyuan.saas.ihrm.activiti;

import com.zhouyuan.saas.ihrm.activiti.base.BaseActiveCompleteTask;
import com.zhouyuan.saas.ihrm.activiti.base.BaseActivitiQueryTask;
import org.junit.Test;

public class ActivitiHolidayProcessTest extends ActivitiApplicationTests {

    private static final String PROCESS_DEFINITIION_KEY = "holiday";
    @Test
    public void holidayFullProcessTest(){
        //目前构造的请假流程只有一个任务，所以直接get（0）获取到该任务
        String taskId = BaseActivitiQueryTask.queryTask(PROCESS_DEFINITIION_KEY,"James").get(0).getId();
        //部门经理审批
        BaseActiveCompleteTask.completeTask(taskId);

        taskId = BaseActivitiQueryTask.queryTask(PROCESS_DEFINITIION_KEY,"Lana De").get(0).getId();
        //总经理审批
        BaseActiveCompleteTask.completeTask(taskId);

        /**
         * 当最后一个审批流程，即总经理审批完成后
         *  `act_ru_execution` 执行表
         *  `act_ru_identitylink` 正在执行的参与者信息
         *  `act_ru_task` 目前正在执行的任务
         *  这三张表中的数据都会被清空，体现了运行时的数据表的特点，因为没有正在运行的任务了，
         *  这也是运行时数据表查询效率高的原因，即数据即用即删，表中没有积压数据，数据量比较少，查询快
         *  运行结束的流程任务的数据存储到历史表中去了
         */
    }
}
