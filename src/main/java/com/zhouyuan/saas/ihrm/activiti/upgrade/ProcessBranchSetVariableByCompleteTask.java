package com.zhouyuan.saas.ihrm.activiti.upgrade;

import com.zhouyuan.saas.ihrm.activiti.base.BaseActiveCompleteTask;
import com.zhouyuan.saas.ihrm.activiti.base.BaseActivitiDeploy;
import com.zhouyuan.saas.ihrm.activiti.base.BaseActivitiStartProcessInstance;
import com.zhouyuan.saas.ihrm.activiti.upgrade.entity.Holiday;

import java.util.HashMap;
import java.util.Map;

/**
 * 根据请假天数是否小于3天判断流程分支的走向，在完成任务时注入流程变量
 */
public class ProcessBranchSetVariableByCompleteTask {

    /**
     * diagram/holidayWithBranch.bpmn的流程定义主键
     */
    private static final String DEFINITION_KEY = "holidayWithBranch";

    /**
     * 该流程请假天数小于3天时流程走向所需经过的处理人
     */
    private static final String[] assigneeArrLtThree = {"Tom","Jerry","Lana Del"};

    /**
     * 该流程请假天数大于等于3天时流程走向所需经过的处理人
     */
    private static final String[] assigneeArrNotLtThree = {"Tom","Jerry","Winnie","Lana Del"};

    public static void main(String[] args) {
        //先部署该带判断分支的请假流程定义
        BaseActivitiDeploy.deploy("diagram/holidayWithBranch.bpmn","diagram/holidayWithBranch.png");
        //小于3天的情况
        testProcessByHolidayNum(1F);
        //大于等于3天的情况
        testProcessByHolidayNum(3F);
    }

    /**
     * 全流程测试该带判断分支的请假流程
     * @param num
     */
    public static void testProcessByHolidayNum(Float num){

        //启动流程实例
        BaseActivitiStartProcessInstance.startProcessInstance(DEFINITION_KEY);

        //根据请假天数选择不同的流程处理群组
        String[] assgineeArr = assigneeArrNotLtThree;
        if (num < 3){
            assgineeArr = assigneeArrLtThree;
        }

        //构造流程变量
        Holiday holiday = new Holiday();
        holiday.setNum(num);
        Map<String,Object> map = new HashMap<>(1);

        for (String assignee:
                assgineeArr) {
            //完成任务
            if ("Tom".equals(assignee)){
                //在提交请假单操作完成时赋予流程实例以流程变量
                map.put("holiday",holiday);
            }
            else {
                map = null;
            }
            BaseActiveCompleteTask.completeTaskByAssignee(DEFINITION_KEY,assignee,map);
        }
    }

}
