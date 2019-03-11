package com.ly.activiti.table.design;


import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;


/**
 *
 * 1. 运行时相关表
 *      1. act_ru_execution     流程实例执行表
 *      2. act_ru_task          用户任务信息表
 *      3. act_ru_variable      变量信息
 *      4. act_ru_identitylink  参与者信息
 *      5. act_ru_event_subscr  事件监听表
 *      6. act_ru_job           作业表
 *      7. act_ru_timer_job     定时器表
 *      8. act_ru_suspended_job 暂停作业表
 *      9. act_ru_deadletter_job死信表
 *
 * 2. act_ru_execution  流程是列表
 *      1. proc_inst_id_        流程实例id
 *      2. business_key_        业务标识
 *      3. parent_id_           父执行信息
 *      4. proc_def_id_         流程实例id
 *      5. super_exec_          父流程实例对应的执行
 *      6. act_id_              流程定义节点Id
 *      7. is_active_           是否活动的执行 0 非活动 1. 活动
 *      8. is_concurrent_       是否并行分支 0非  1是
 *      9. is_scope_            是否全局流程执行 0非， 1是   （流程实例启动： 表示全局。  流程中启动： 表示局部）
 *      10.is_event_scope_      是否是激活状态
 *      11.suspension_state     挂起状态 1. 正常  2挂起
 *      12 lock_time            锁定时间
 *
 * 3. act_ru_task       流程任务表
 *      1. executoin_id_        执行流id
 *      2. proc_inst_id_        流程实例id
 *      3. proc_def_id_         流程定义id
 *      4. parent_task_id_      父任务
 *      5. task_def_key_        任务定义id
 *      6. name_                任务定义名称
 *      7. owner_               拥有人
 *      8. assignee_            代理人
 *      9. delegation_          委托状态 pending 委托中， resolved 已处理
 *      10. priority_           优先级
 *      11. due_date_           过期时间
 *      form_key_               表单标识
 *
 * 4. act_ru_variable   变量信息表
 *      1. type_                变量类型（integer, string, double, json）
 *      2. name_                变量名
 *      3. bytearray_id_        资源表id
 *      4. double_              浮点值
 *      5. long_                长整型
 *      6. text_                文本值
 *
 * 5. act_ru_identityLink  参与者信息表
 *      1. id_                  主键
 *      2. group_id_            用户组
 *      3. type_                类型 assignee, candidate, owner, starter..
 *      4. user_id_             用户id
 *      5. task_id_             任务id
 *      6. proc_inst_id_        流程实例
 *
 * 6. act_ru_event_subscr   事件订阅表  (1 基于流程定义文件。 接受消息启动流程。  2. 当流程启动以后（基于流程实例）(只有当基于流程实例的才可以有  执行id 和 流程实例id)。 才可以监听事件)
 *      1. event_type_          事件类型 message, signal
 *      2. event_name_          事件名称
 *      3. execution_id_        流程执行id
 *      4. proc_inst_id_        流程实例id
 *      5. activity_id_         流程定义节点id
 *      6. ciguration_          配置
 *
 * 7. act_ru_job            作业信息表
 *      1. type_                类型
 *      2. lock_exp_time_       锁定过期时间
 *      3. lock_owner_          锁定节点
 *      4. exclusive_           是否唯一
 *      5. retries_             重试次数3
 *      6. repeat_              重复表达式 R5/RT10S
 *      7. exception_stack_id_  异常堆栈（资源表ID）
 *      8. exception_msg_       异常信息
 *      9. duedate_             过期时间
 *      10. handler_type_       处理器类型
 *      11. handler_cfg_        处理器配置
 *      12. execution_id_       流程执行表id
 */

@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
@ContextConfiguration(locations = {"classpath:activiti-context.xml"})

public class RuTableTest {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Test
    public void test1() {
        Map<String, Object> mapVaraible = Maps.newHashMap();
        mapVaraible.put("test key", "test value");

        repositoryService.createDeployment()
                .name("测试runtime表的部署")
                .addClasspathResource("MyProcess.bpmn20.xml")
                .deploy();

        runtimeService.createProcessInstanceBuilder()
                .businessKey("business key")
                .processDefinitionKey("myProcess")
                .variables(mapVaraible)
                .start();

//        ProcessInstance myProcess = runtimeService.startProcessInstanceByKey("myProcess");
        log.info("部署启动文件成功!");
    }

    @Test
    public void testSetOwner() {
        Task task = taskService.createTaskQuery().processDefinitionKey("myProcess").singleResult();
        taskService.setOwner(task.getId(), "ly");

        log.info("设置 taskid = {}", task.getId());
    }

    @Test
    public void testJobTask() throws InterruptedException {
        repositoryService.createDeployment()
                .name("测试runtime表的部署")
                .addClasspathResource("MyProcess2.jobTest.bpmn20.xml")
                .deploy();

        Thread.sleep(1000 * 30);
    }

}
