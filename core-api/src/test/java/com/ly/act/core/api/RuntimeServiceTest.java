package com.ly.act.core.api;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceBuilder;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.test.Deployment;
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
public class RuntimeServiceTest {

    private final static String PROCESS_KEY = "my-process2";

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    /**
     * 通过流程key启动流程
     *      流程key的 最新的版本
     */
    @Test
    @org.activiti.engine.test.Deployment(resources = "bpmn/MyProcess2.bpmn20.xml")
    public void test1() {

        Map<String, Object> variables = Maps.newHashMap();
        variables.put("key", "value1");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process2", variables);


        log.info("通过key 启动流程 = {}", processInstance);
    }

    /**
     * 通过流程定义id  --> 启动流程
     */
    @Test
    @org.activiti.engine.test.Deployment(resources = "bpmn/MyProcess2.bpmn20.xml")
    public void test2() {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().orderByDeploymentId().desc().listPage(0, 100).get(0);
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("key", "value1");

        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId(), variables);

        log.info("processInstance by id = {}", processInstance);
    }

    /**
     * 通过 processInstenceBuilder 启动流程
     */
    @Test
    @org.activiti.engine.test.Deployment(resources = "bpmn/MyProcess2.bpmn20.xml")
    public void test3() {
        ProcessInstanceBuilder processInstanceBuilder = runtimeService.createProcessInstanceBuilder();

        Map<String, Object> variables = Maps.newHashMap();
        variables.put("key", "value1");
        ProcessInstance start = processInstanceBuilder
                .businessKey("my-businessKey")
                .processDefinitionKey("my-process2")
                .variables(variables)
                .start();

        log.info("启动的流程为 process = {}", start);
    }

    /**
     * 测试启动流程
     *  修改变量 (通过执行流id修改)
     */
    @Test
    @org.activiti.engine.test.Deployment(resources = "bpmn/MyProcess2.bpmn20.xml")
    public void test4() {
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("key", "value1");
        variables.put("key2", "value2");

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_KEY, variables);
        Map<String, Object> variables1 = runtimeService.getVariables(processInstance.getId());
        log.info("流程中的原始数据： " + variables1);

        runtimeService.setVariable(processInstance.getId(), "key3", "values3");
        runtimeService.setVariable(processInstance.getId(), "key2", "modify2");

        Map<String, Object> variables2 = runtimeService.getVariables(processInstance.getId());
        log.info("流程中的修改后的数据 = {}", variables2);
    }

    /**
     * 流程实列 查询
     *
     */
    @Test
    @org.activiti.engine.test.Deployment(resources = "bpmn/MyProcess2.bpmn20.xml")
    public void test5() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_KEY);

//        ProcessInstance processInstance1 = runtimeService.createProcessInstanceQuery().processInstanceId(processInstance.getId()).singleResult();
        ProcessInstance processInstance1 = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey("my-businessKey").singleResult();

        log.info("processInstance1 = {}", processInstance1);
    }

    /**
     * 流程执行对象查询
     */
    @Test
    @org.activiti.engine.test.Deployment(resources = "bpmn/MyProcess2.bpmn20.xml")
    public void test6() {
        List<Execution> list = runtimeService.createExecutionQuery().list();

        for (Execution execution : list) {
            log.info("execution = {}", execution);
        }
    }

    /**
     * 触发节点触发
     *  receiveTask 节点触发
     */
    @Test
    public void test7() {
        DeploymentBuilder deployment = repositoryService.createDeployment();
        deployment.name("测试触发部署")
                .addClasspathResource("bpmn/MyProcess2.triger.bpmn20.xml")
                .deploy();

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process2-triger");

        Execution execution = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).activityId("someTask").singleResult();

        log.info("流程执行流对象： {}", execution);
        runtimeService.trigger(execution.getId());

        Execution execution2 = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).activityId("someTask").singleResult();
        log.info("流程执行流对象2： {}", execution2);
    }

    /**
     * 触发节点触发
     *  signal 信号触发
     */
    @Test
    public void test8() {
        DeploymentBuilder deployment = repositoryService.createDeployment();
        deployment.name("测试触发部署")
                .addClasspathResource("bpmn/MyProcess2.triger2.bpmn20.xml")
                .deploy();

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process3-triger");

        Execution execution = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId())
                .signalEventSubscriptionName("my-signal")
                .singleResult();
        log.info("获取的执行流对象是 = {}", execution);


        runtimeService.signalEventReceived("my-signal");

        execution = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId())
                .signalEventSubscriptionName("my-signal")
                .singleResult();
        log.info("触发以后的执行流对象是 = {}", execution);
    }

    /**
     * 触发节点触发
     *  消息触发
     *      触发是 需要message名称和 执行 执行流id
     */
    @Test
    public void test9() {
        DeploymentBuilder deployment = repositoryService.createDeployment();
        deployment.name("测试触发部署")
                .addClasspathResource("bpmn/MyProcess2.triger3.bpmn20.xml")
                .deploy();

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process4-triger");

        Execution execution = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId())
                .messageEventSubscriptionName("my-message")
                .singleResult();
        log.info("获取的执行流对象是 = {}", execution);

        runtimeService.messageEventReceived("my-message", execution.getId());

        execution = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId())
                .messageEventSubscriptionName("my-message")
                .singleResult();
        log.info("获取的执行流对象是 = {}", execution);
    }


    /**
     * 基于message启动流程
     *  底层还是 基于key启动。 只是发送一个message消息
     */
    @Test
    public void test10() {
        DeploymentBuilder deployment = repositoryService.createDeployment();
        deployment.name("测试触发部署")
                .addClasspathResource("bpmn/MyProcess2.triger4.bpmn20.xml")
                .deploy();

        ProcessInstance processInstance = runtimeService.startProcessInstanceByMessage("my-message-2");

        log.info("启动的流程实列为 = {}", processInstance);


    }

}
