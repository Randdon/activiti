package com.zhouyuan.saas.ihrm.activiti.upgrade;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * 需求：
 * 1.从Activiti的act_ge_bytearray表中读取两个资源文件
 * 2.将两个资源文件保存到路径：   "D:\\projects\\zhouyuan\\saas-ihrm\\test\\"
 *
 * 技术方案：
 *     1.第一种方式使用actviti的api来实现
 *     2.第二种方式：其实就是原理层面，可以使用jdbc的对blob类型，clob类型数据的读取，并保存
 *        IO流转换，最好commons-io.jar包可以轻松解决IO操作
 *
 * 真实应用场景：用户想查看这个请假流程具体有哪些步骤要走？
 *
 *
 */
public class QueryBpmnAndPngFiles {

    //流程定义key
    private static final String DEFINITION_KEY = "holiday";

    //资源文件本地保存路径
    private static final String FILE_PATH = "D:\\projects\\zhouyuan\\saas-ihrm\\test\\";

    public static void main(String[] args) {
        querySourceFiles(DEFINITION_KEY,FILE_PATH);
    }

    /**
     *
     * @param processDefinitionKey 流程定义的key
     * @param sourceFileSavePath 资源文件本地保存路径
     */
    public static void querySourceFiles(String processDefinitionKey, String sourceFileSavePath){

        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //2.得到RepositoryService对象
        RepositoryService repositoryService = processEngine.getRepositoryService();

        //3.得到查询器:ProcessDefinitionQuery对象
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();

        //4.设置查询条件,执行查询操作,查询出想要的流程定义
        ProcessDefinition processDefinition = processDefinitionQuery.processDefinitionKey(processDefinitionKey)
                .singleResult();

        //5.通过流程定义信息，得到部署ID
        String deployId = processDefinition.getDeploymentId();

        //processDefinition.getDiagramResourceName() 代表获取png图片资源的名称
        String pngName = processDefinition.getDiagramResourceName();

        //processDefinition.getResourceName()代表获取bpmn文件的名称
        String bpmnName = processDefinition.getResourceName();

        //6.通过repositoryService的方法,实现读取图片信息及bpmn文件信息(输入流)
        //getResourceAsStream()方法的参数说明：第一个参数部署id,第二个参数代表资源名称
        InputStream pngIS = repositoryService.getResourceAsStream(deployId, pngName);

        InputStream bpmnIs = repositoryService.getResourceAsStream(deployId, bpmnName);

        try {
            //7.构建出OutputStream流
            OutputStream pngOs = new FileOutputStream(sourceFileSavePath + pngName);

            OutputStream bpmnOs = new FileOutputStream(sourceFileSavePath + bpmnName);

            //8.输入流，输出流的转换  commons-io-xx.jar中的方法
            IOUtils.copy(pngIS,pngOs);

            IOUtils.copy(bpmnIs,bpmnOs);

            //9.关闭流
            pngOs.close();
            bpmnOs.close();
            pngIS.close();
            bpmnIs.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
