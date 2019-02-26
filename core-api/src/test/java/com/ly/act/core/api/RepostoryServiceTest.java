package com.ly.act.core.api;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.IdentityLink;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
@ContextConfiguration(locations = {"classpath:activiti-context.xml"})
public class RepostoryServiceTest {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    // 一次部署。 一个部署名称， 多个流程定义对象

    /**
     * 测试流程部署和查询
     */
    @Test
    public void test1() {
        // 创建流程部署对象
        DeploymentBuilder deployment = repositoryService.createDeployment();

        // 设置部署的资源
        deployment.name("测试部署资源")
                .addClasspathResource("bpmn/MyProcess.bpmn20.xml")
                .addClasspathResource("bpmn/MyProcess2.bpmn20.xml");

        // 部署资源
        Deployment deploy = deployment.deploy();

        // 设置部署的资源2
        DeploymentBuilder deployment2 = repositoryService.createDeployment();
        deployment2.name("测试部署资源2")
                .addClasspathResource("bpmn/MyProcess.bpmn20.xml")
                .addClasspathResource("bpmn/MyProcess2.bpmn20.xml");
        deployment2.deploy();

        // 部署对象查询
        DeploymentQuery deploymentQuery = repositoryService.createDeploymentQuery();
//        Deployment queryDeploy = deploymentQuery.deploymentId(deploy.getId()).singleResult();
        List<Deployment> deployments = deploymentQuery.orderByDeploymenTime().asc().listPage(0, 100);
        for (Deployment deploymentNew : deployments) {
            log.info("deploymentNew = {}", deploymentNew);
        }

//        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().deploymentId(deploy.getId()).listPage(0, 100);
        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().orderByDeploymentId().asc().listPage(0, 100);

        for (ProcessDefinition processDefinitionsItem : processDefinitions) {
            log.info("processDefinetion = {}, version  = {}, key = {}, id = {}",
                    processDefinitionsItem, processDefinitionsItem.getVersion(), processDefinitionsItem.getKey(), processDefinitionsItem.getId());
        }

    }

    /**
     * 测试流程运行和挂起
     */
    @Test
    @org.activiti.engine.test.Deployment(resources = "bpmn/MyProcess2.bpmn20.xml")
    public void test2() {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("my-process2").listPage(0, 1).get(0);

        log.info("流程挂起");
        // 流程挂起
        repositoryService.suspendProcessDefinitionById(processDefinition.getId());

        try {
            log.info("测试挂起流程的启动");
            runtimeService.startProcessInstanceById(processDefinition.getId());
            log.info("挂起流程启动成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("挂起流程启动失败, {}", e);
        }

        log.info("流程激活");
        repositoryService.activateProcessDefinitionById(processDefinition.getId());

        try {
            log.info("测试激活流程启动");
            runtimeService.startProcessInstanceById(processDefinition.getId());
            log.info("激活流程启动成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("激活流程启动激活, {}", e);
        }
    }

    /**
     * 控制流程定义文件和 用户/用户组关系的时候
     *      1. 设置关系
     *      2. 解除关系
     */
    @Test
    public void test3() {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("my-process2").listPage(0, 1).get(0);

        repositoryService.addCandidateStarterUser(processDefinition.getId(), "user1");
        repositoryService.addCandidateStarterGroup(processDefinition.getId(), "group1");

        List<IdentityLink> identityLinksForProcessDefinition = repositoryService.getIdentityLinksForProcessDefinition(processDefinition.getId());
        for (IdentityLink identityLink : identityLinksForProcessDefinition) {
            log.info("identityLink = {}", identityLink);
        }

        repositoryService.deleteCandidateStarterUser(processDefinition.getId(), "user1");
        repositoryService.deleteCandidateStarterGroup(processDefinition.getId(), "group1");
        List<IdentityLink> identityLinksForProcessDefinition2 = repositoryService.getIdentityLinksForProcessDefinition(processDefinition.getId());
        for (IdentityLink identityLink : identityLinksForProcessDefinition2) {
            log.info("第二次查询 identityLink = {}", identityLink);
        }
    }

}
