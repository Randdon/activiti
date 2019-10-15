package com.zhouyuan.saas.ihrm.activiti.base;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;

import java.io.InputStream;
import java.util.zip.ZipInputStream;

/**
 * 流程定义部署
 */
public class BaseActivitiDeploy {

    public static void main(String[] args) {

        deployByZip();
    }

    /**
     * 部署影响到的数据库表有：
     * `act_re_deployment`——存储了部署信息
     * `act_re_procdef`——存储了流程定义的一些信息
     * `act_ge_bytearray` ——存储了bpmn和png文件
     */
    public static void deploy(String bpmnPath,String pngPath){

        //创建processEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //生成repositoryService实例
        RepositoryService repositoryService = processEngine.getRepositoryService();

        //进行部署
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource(bpmnPath)//添加bpmn资源
                .addClasspathResource(pngPath)//添加png资源
                .name("请假流程")
                .deploy();

        //输出部署信息
        System.out.println(deployment.getId());
        System.out.println(deployment.getName());

    }

    public static void deployByZip(){

        //创建processEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //生成repositoryService实例
        RepositoryService repositoryService = processEngine.getRepositoryService();

        //构造zip输入流
        InputStream inputStream = BaseActivitiDeploy.class.getClassLoader()
                .getResourceAsStream("diagram/holiday.zip");
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);

        //进行部署
        Deployment deployment = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .name("请假流程")
                .deploy();

        //输出部署信息
        System.out.println(deployment.getId());
        System.out.println(deployment.getName());

    }
}
