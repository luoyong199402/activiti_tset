package com.ly.demo.test;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.runtime.Job;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:activiti-context.xml"})
@Slf4j
public class Test8Spring {

    @Autowired
    @Rule
    public ActivitiRule activitiRule;

    @Autowired
    public RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Test
    @Deployment(resources = "MyProcess2.bpmn20.xml")
    public void test1() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process2");
        Task task = taskService.createTaskQuery().active().singleResult();
        taskService.complete(task.getId());
        log.info("spring 整合activiti ok");
    }
}
