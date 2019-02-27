package com.ly.act.core.api;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceBuilder;
import org.activiti.engine.task.*;
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
public class TaskServiceTest {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    /**
     * 测试task的变量信息和完成task任务
     *  流程实列可以获取task变量， 但是不能获取 taskLocal变量
     */
    @Test
    public void test1() {
        repositoryService.createDeployment()
                .name("测试task部署一")
                .addClasspathResource("bpmn/MyProcess.task1.bpmn20.xml")
                .deploy();

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("process-task-1");

        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();


        log.info("task.name = {}", task.getName());
        log.info("task.desc = {}", task.getDescription());


        // 设置task变量
        taskService.setVariable(task.getId(),"task var 1", "val1");
        // 设置taskLocal变量
        taskService.setVariableLocal(task.getId(), "task local var 1", "var2");

        // 设置流程变量
        runtimeService.setVariable(processInstance.getId(),"process var 1", "val1");

        // 获取task变量
        log.info("task var = {}", taskService.getVariables(task.getId()));

        // 获取taskLocal变量
        log.info("taskLocal var = {}", taskService.getVariablesLocal(task.getId()));

        // 获取流程实列变量
        log.info("process var = {}", runtimeService.getVariables(processInstance.getId()));

        Map<String, Object> varMap = Maps.newHashMap();
        varMap.put("test", "test value");
        // 完成task任务
        taskService.complete(task.getId(), varMap);


        // 查看是否完成
        log.info("task info = {}", taskService.createTaskQuery().taskId(task.getId()).singleResult());
    }


    /**
     * 设置节点的权限信息
     */
    @Test
    public void test2() {
        repositoryService.createDeployment()
                .name("测试task部署一")
                .addClasspathResource("bpmn/MyProcess.task1.bpmn20.xml")
                .deploy();


        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("process-task-1");

        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();

        taskService.setOwner(task.getId(), "ly");

        List<Task> lyTaskList = taskService
                .createTaskQuery()
                .taskCandidateUser("ly")
                .taskUnassigned()
                .listPage(0, 100);

        for (Task taskItem : lyTaskList) {
            try {
                taskService.claim(taskItem.getId(), "ly");
                log.info("设置候选人为ly时候的办理人");
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        List<IdentityLink> identityLinksForTask = taskService.getIdentityLinksForTask(task.getId());
        for (IdentityLink identityLink : identityLinksForTask) {
            log.info("identityLink = {}", identityLink);
        }

        List<Task> lyTasks = taskService.createTaskQuery()
                .taskAssignee("ly").listPage(0, 100);

        for (Task lyTask : lyTasks) {
            taskService.complete(lyTask.getId());
            log.info("完成我的任务");
        }

        lyTasks = taskService.createTaskQuery()
                .taskAssignee("ly").listPage(0, 100);

        log.info("ly 存在待办的个数 = {}", lyTasks.size());
    }


    /**
     * 测试task的附件信息
     *  任务附件（Attachmenet）
     *  任务评论（Comment）创建和查询
     *  事件记录（event）创建和查询
     */
    @Test
    public void test3() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("process-task-1");

        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();

        taskService.createAttachment("url", task.getId(),
                task.getProcessInstanceId(), "这是我的附件", "附件描述","/url/附件信息");


        List<Attachment> taskAttachments = taskService.getTaskAttachments(task.getId());
        for (Attachment taskAttachment : taskAttachments) {
            log.info("taskAttachment = ", taskAttachment);
        }

    }

    @Test
    public void test4() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("process-task-1");

        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();

        taskService.createAttachment("url", task.getId(),
                task.getProcessInstanceId(), "这是我的附件", "附件描述","/url/附件信息");
        taskService.setOwner(task.getId(), "ly3");

        taskService.addComment(task.getId(), task.getProcessInstanceId(), "这是我的评论一");
        taskService.addComment(task.getId(), task.getProcessInstanceId(), "这是我的评论二");
        List<Comment> taskComments = taskService.getTaskComments(task.getId());

        for (Comment taskComment : taskComments) {
            log.info("taskComment = ", taskComment);
        }

        // 记录task 操作的event  信息。
        List<Event> taskEvents = taskService.getTaskEvents(task.getId());
        for (Event taskEvent : taskEvents) {
            log.info("taskEvent = {}", taskEvent);
        }

    }

}
