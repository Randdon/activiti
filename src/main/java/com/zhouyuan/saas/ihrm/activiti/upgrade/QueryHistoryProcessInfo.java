package com.zhouyuan.saas.ihrm.activiti.upgrade;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;

import java.util.List;

/**
 * 查看历史数据
 */
public class QueryHistoryProcessInfo {

    public static void main(String[] args) {
        queryHistoryInfo();
    }

    public static void queryHistoryInfo(){

        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //2.得到HistoryService
        HistoryService historyService = processEngine.getHistoryService();

        //3.得到HistoricActivitiInstanceQuery对象
        HistoricActivityInstanceQuery historicActivityInstanceQuery =
                historyService.createHistoricActivityInstanceQuery();

        //4.根据流程实例ID执行查询
        List<HistoricActivityInstance> list = historicActivityInstanceQuery.processInstanceId("10001")
                .orderByHistoricActivityInstanceStartTime()
                .asc()
                .list();

        //5.遍历查询结果
        list.stream().forEach(historicActivityInstance -> {
            System.out.println(historicActivityInstance.getActivityId());
            System.out.println(historicActivityInstance.getActivityName());
            System.out.println(historicActivityInstance.getAssignee());
            System.out.println(historicActivityInstance.getProcessDefinitionId());
            System.out.println(historicActivityInstance.getProcessInstanceId());
            System.out.println("#############################################");
        });
    }
}
