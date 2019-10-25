package com.zhouyuan.saas.ihrm.activiti.upgrade.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 请假实体类:
 *    注意POJO类型，在用于流程变量时一定要实现Serializable接口，否则在存储这个pojo时就会报异常
 *    没有实现Serializable接口就在启动流程实例时用该实体作为流程变量，就会报
 *    org.activiti.engine.ActivitiException: couldn't find a variable type that is able to serialize
 */
public class Holiday implements Serializable {

    //请假人姓名
    private String name;

    //请假开始时间
    private Date beginTime;

    //请假结束时间
    private Date endTime;

    //请假天数
    private Float num;

    //请假事由
    private String reason;

    //请假类型
    private String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Float getNum() {
        return num;
    }

    public void setNum(Float num) {
        this.num = num;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
