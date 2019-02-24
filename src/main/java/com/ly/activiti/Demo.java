package com.ly.activiti;


import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Slf4j
public class Demo {

    public static void main(String[] args) {
        log.info("开始程序");

        ProcessEngineConfiguration processEngineConfiguration
                = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration();
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();

        log.info(processEngine.getName());
        log.info(processEngine.VERSION);

        RepositoryService repositoryService = processEngine.getRepositoryService();
        DeploymentBuilder deployment = repositoryService.createDeployment();
        DeploymentBuilder deploymentBuilder = deployment.addClasspathResource("MyProcess.bpmn20.xml");
        Deployment deploy = deploymentBuilder.deploy();

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deploy.getId()).singleResult();

        log.info("流程id {} ", processDefinition.getId());
        log.info("流程名称 {} ", processDefinition.getName());

        // 启动流程
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId());

        Scanner scanner = new Scanner(System.in);
        while (processInstance != null && !processInstance.isEnded()) {

            // 处理流程任务
            TaskService taskService = processEngine.getTaskService();
            List<Task> list = taskService.createTaskQuery().list();
            log.info("任务数量: {}", list.size());

            for (Task task : list) {
                log.info("待处理的task {}", task.getName());
                FormService formService = processEngine.getFormService();
                TaskFormData taskFormData = formService.getTaskFormData(task.getId());

                List<FormProperty> formProperties = taskFormData.getFormProperties();

                Map<String, Object> map = Maps.newHashMap();
                for (FormProperty property : formProperties) {
                    log.info("当前的类型是 {}", property.getType());
                    log.info("当前的Id是 {}", property.getId());
                    log.info("当前的值是 {}", property.getValue());
                    log.info("请输入 {} 的值", property.getName());
                    String val = scanner.nextLine();
                    log.info("你输入的内容是: {}" , val);
                    log.info("");

                    map.put(property.getId(), val);
                }
                taskService.complete(task.getId(), map);
            }



            processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstance.getId()).singleResult();
        }



        log.info("结束activitiDemo");
    }
}
