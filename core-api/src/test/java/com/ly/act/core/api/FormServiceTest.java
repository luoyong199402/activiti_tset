package com.ly.act.core.api;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
@ContextConfiguration(locations = {"classpath:activiti-context.xml"})
public class FormServiceTest {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private FormService formService;

    @Test
    public void test1() {
        repositoryService.createDeployment()
                .name("表单部署测试")
                .addClasspathResource("bpmn/MyProcess2.formEvent.bpmn20.xml")
                .deploy();

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("my-form1-test").listPage(0, 100).get(0);

        String startFormKey = formService.getStartFormKey(processDefinition.getId());
        log.info("startFormKey = {}", startFormKey);

        StartFormData startFormData = formService.getStartFormData(processDefinition.getId());
        List<FormProperty> formProperties = startFormData.getFormProperties();

        for (FormProperty formProperty : formProperties) {
            log.info("form type = {}", formProperty.getType());
            log.info("form name = {}", formProperty.getName());
            log.info("form id = {}", formProperty.getId());
        }

        Map<String, String> properties = Maps.newHashMap();
        properties.put("message", "this is my send  data");
        ProcessInstance processInstance = formService.submitStartFormData(processDefinition.getId(), properties);

        Task someTask = taskService.createTaskQuery().processInstanceId(processInstance.getId()).taskDefinitionKey("someTask").singleResult();
        List<FormProperty> formProperties1 = formService.getTaskFormData(someTask.getId()).getFormProperties();
        for (FormProperty formProperty : formProperties1) {
            log.info("task form id = {}", formProperty.getId());
            log.info("task form name = {}", formProperty.getName());
            log.info("task form type = {}", formProperty.getType());
        }

        Map<String, String> properties2 = Maps.newHashMap();
        properties2.put("yesOrNo", "yes");

        formService.submitTaskFormData(someTask.getId(), properties2);

//        ProcessInstance processInstance1 = runtimeService.createProcessInstanceQuery().processDefinitionKey("my-form1-test").singleResult();
//
//        log.info("processInstance1 = {}", processInstance1);
    }


}
