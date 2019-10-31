package com.zhouyuan.saas.ihrm.activiti.upgrade;

import com.zhouyuan.saas.ihrm.activiti.base.BaseActiveCompleteTask;
import com.zhouyuan.saas.ihrm.activiti.base.BaseActivitiDeploy;
import com.zhouyuan.saas.ihrm.activiti.base.BaseActivitiStartProcessInstance;
import com.zhouyuan.saas.ihrm.activiti.upgrade.entity.Holiday;

import java.util.HashMap;
import java.util.Map;

/**
 * 根据请假天数是否小于3天判断流程分支的走向，在启动流程实例时注入流程变量
 */
public class ProcessBranchSetVariableByStart {

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
        /**
         * 两个条件都不满足的情况，流程走到部门经理审批时，该环境无法完成任务，完成任务时就会报错：
         * org.activiti.engine.ActivitiException:
         * No outgoing sequence flow of element '_4' could be selected for continuing the process
         */
        testProcessByHolidayNum(null);

        /**
         * 如果流程定义中两个分支的条件分别为num>3和num>1那么当流程变量num为5时，就会遇到两个分支条件都满足的情况，这种情况下，
         * 这两个流程分支就都会被执行，即数据库中执行完部门经理审批后会同时出现总经理审批和人事归档两条记录，
         * 要解决这个问题，就要使用网关了，见com.zhouyuan.saas.ihrm.activiti.ExclusiveGatewayProcessTest
         */
    }

    /**
     * 全流程测试该带判断分支的请假流程
     * @param num
     */
    public static void testProcessByHolidayNum(Float num){

        //启动流程实例，将请假天数在启动时加入到该流程的流程变量中去
        setVariableByStart(num);

        //根据请假天数选择不同的流程处理群组
        String[] assgineeArr = assigneeArrNotLtThree;
        if (num < 3){
            assgineeArr = assigneeArrLtThree;
        }


        for (String assignee:
                assgineeArr) {
            //完成任务
            BaseActiveCompleteTask.completeTaskByAssignee(DEFINITION_KEY,assignee,null);
        }
    }

    /**
     * 在启动流程实例的时候设置流程变量
     * @param num 请假天数
     */
    public static void setVariableByStart(Float num){
        Holiday holiday = new Holiday();
        holiday.setNum(num);
        Map<String,Object> map = new HashMap<>(1);
        map.put("holiday",holiday);
        BaseActivitiStartProcessInstance.startProcessInstanceWithProcessVariable(DEFINITION_KEY,map);
    }

}
