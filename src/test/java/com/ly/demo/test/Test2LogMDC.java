package com.ly.demo.test;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.logging.LogMDC;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;

@Slf4j
public class Test2LogMDC {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti.cfg_mdc.xml");

    @Test
    @Deployment(resources = "MyProcess.bpmn20.xml")
    public void test1() {
        activitiRule.getRuntimeService().startProcessInstanceByKey("myProcess");

        Task task = activitiRule.getTaskService().createTaskQuery().singleResult();
        log.info("task name {} ", task.getName());
    }

    @Test
    @Deployment(resources = "MyProcess2.bpmn20.xml")
    public void test2() {
        LogMDC.setMDCEnabled(true);
        activitiRule.getRuntimeService().startProcessInstanceByKey("my-process2");

        Task task = activitiRule.getTaskService().createTaskQuery().singleResult();
        log.info("task name {} ", task.getName());

        activitiRule.getTaskService().complete(task.getId());
    }


}
