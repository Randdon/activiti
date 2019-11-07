package com.zhouyuan.saas.ihrm.activiti.springboot.controller;

import com.zhouyuan.saas.ihrm.activiti.config.SecurityUtil;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Activiti7PlusSpringbootController {

    private static final Logger logger = LoggerFactory.getLogger(Activiti7PlusSpringbootController.class);

    @Autowired
    SecurityUtil securityUtil;

    @Autowired
    ProcessRuntime processRuntime;

    @Autowired
    TaskRuntime taskRuntime;

    /**
     * 用url请求后出现的登录弹框是Spring Security的机制，用户名和密码在
     * com.zhouyuan.saas.ihrm.activiti.config.DemoApplicationConfiguration#myUserDetailsService()里配置
     * @return
     */
    @RequestMapping(value = "/activiti7",method = RequestMethod.GET)
    public String Activiti7ControllerTest(){
        //查询任务
        Page<Task> taskPage = taskRuntime.tasks(Pageable.of(0, 10));
        if (taskPage.getTotalItems()>0){
            //如果查询到任务为
            taskPage.getContent().forEach(task -> {
                logger.info("第一次查询到任务：{}",task);
                //认领任务
                taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(task.getId()).build());
                //完成任务
                taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(task.getId()).build());
            });
        }

        //再次查询任务
        taskPage = taskRuntime.tasks(Pageable.of(0, 10));
        if (taskPage.getTotalItems()>0){
            //如果查询到任务为
            taskPage.getContent().forEach(task -> {
                logger.info("第二次查询到任务：{}",task);
                //认领任务
                taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(task.getId()).build());
                //完成任务
                taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(task.getId()).build());
            });
        }
        return taskPage.getContent().get(0).toString();
    }

}
