package com.zhouyuan.saas.ihrm.activiti.upgrade;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;

import java.util.List;

public class QueryAndDeleteProcessDefinition {

    private static final String DEFINITION_KEY = "holiday";

    public static void main(String[] args) {

        String id = queryProcessDefinitionInfoByKey(DEFINITION_KEY);
        //deleteProcessDefinitionByDeployId(id);
        deleteProcessDefinitionByDeployId2(id);
    }

    /**
     * 查询流程定义信息
     * @param key 流程定义的Key值
     * @return
     */
    public static String queryProcessDefinitionInfoByKey(String key){

        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //2.创建RepositoryService对象
        RepositoryService repositoryService = processEngine.getRepositoryService();

        //3.得到ProcessDefinitionQuery对象,可以认为它就是一个查询器
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();

        //4.设置条件，并查询出当前的所有流程定义   查询条件：流程定义的key=holiday
        //orderByProcessDefinitionVersion() 设置排序方式,根据流程定义的版本号进行排序
        List<ProcessDefinition> processDefinitionList = processDefinitionQuery.processDefinitionKey(key)
                .orderByProcessDefinitionVersion()
                .desc()
                .list();

        //5.输出流程定义信息
        processDefinitionList.forEach(processDefinition -> {
            System.out.println("流程定义ID：" + processDefinition.getId());
            System.out.println("流程定义名称：" + processDefinition.getName());
            System.out.println("流程定义的Key：" + processDefinition.getKey());
            System.out.println("流程定义的版本号：" + processDefinition.getVersion());
            System.out.println("流程部署ID：" + processDefinition.getDeploymentId());
        });

        //6.返回流程部署ID
        return processDefinitionList.get(0).getDeploymentId();
    }

    /**
     * 删除已经部署的流程定义，该方法不可以删除未审批完成的流程定义信息
     *
     * 背后影响的表:
     * act_ge_bytearray
     * act_re_deployment
     * act_re_procdef
     * 可以看到删除流程定义所影响到的表和流程部署时所影响到的表是一样的，
     * 见com.zhouyuan.saas.ihrm.activiti.base.BaseActivitiDeploy
     * 不会影响act_hi_*的历史表
     *
     * @param id 流程部署的id
     */
    public static void deleteProcessDefinitionByDeployId(String id){


        /**
         * 注意事项：
         *     1.当我们正在执行的这一套流程没有完全审批结束的时候，此时如果要删除流程定义信息就会失败
         *     2.如果公司层面要强制删除,可以参见deleteProcessDefinitionByDeployId2();
         */
        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //2.创建RepositoryService对象
        RepositoryService repositoryService = processEngine.getRepositoryService();

        //3.执行删除流程定义  参数代表流程部署的id
        repositoryService.deleteDeployment(id);
    }

    /**
     * 级联删除流程定义信息
     * 该方法可以删除未审批完成的流程定义信息
     *
     * 也会清空act_hi_*的历史表及act_ru_*的运行时表
     * @param id
     */
    public static void deleteProcessDefinitionByDeployId2(String id){


        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //2.创建RepositoryService对象
        RepositoryService repositoryService = processEngine.getRepositoryService();

        //3.执行删除流程定义  参数代表流程部署的id
        //参数true代表级联删除，此时就会先删除没有完成的流程结点，最后就可以删除流程定义信息  false的值代表不级联
        repositoryService.deleteDeployment(id,true);
    }
}
