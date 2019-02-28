package com.ly.act.core.api;

import com.ly.activit.core.api.mapper.CustomMapper;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.history.*;
import org.activiti.engine.impl.cmd.AbstractCustomSqlExecution;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.management.TablePage;
import org.activiti.engine.runtime.Job;
import org.activiti.engine.runtime.ProcessInstance;
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
public class ManagerServiceTest {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoryService historyService;


    @Autowired
    private ManagementService managementService;

    @Autowired
    private RepositoryService repositoryService;

    /**
     * 测试job查询
     *  一般job
     *  定时job
     *  挂起的job
     *  死掉的job
     *         一般用于管理员查看
     */
    @Test
    public void test1() {
        repositoryService.createDeployment()
                .name("测试异步工作部署")
                .addClasspathResource("bpmn/MyProcess2.jobTest.bpmn20.xml")
                .deploy();

        List<Job> jobs = managementService.createTimerJobQuery().listPage(0, 100);
        log.info("定时的job个数 = {}", jobs.size());

        List<Job> jobs1 = managementService.createSuspendedJobQuery().listPage(0, 100);
        log.info("挂起的job个数 = {}", jobs1.size());

        int size = managementService.createDeadLetterJobQuery().list().size();
        log.info("死掉的job个数");
    }

    /**
     * 通用数据库查询
     */
    @Test
    public void test2() {
        TablePage tablePage = managementService.createTablePageQuery()
                .tableName(managementService.getTableName(ProcessInstance.class))
                .listPage(0, 100);


        log.info("tablePage = {}", tablePage);
    }

    /**
     * 自定义sql查询
     */
    @Test
    public void test3() {
        List<Map<String, Object>> maps = managementService.executeCustomSql(new AbstractCustomSqlExecution<CustomMapper, List<Map<String, Object>>>(CustomMapper.class) {

            public List<Map<String, Object>> execute(CustomMapper customMapper) {
                return customMapper.findAll();
            }
        });

        log.info("maps = {}", maps);
    }

    /**
     * 执行activiti命令
     */
    @Test
    public void test4() {
        ProcessDefinitionEntity processDefinitionEntity = managementService.executeCommand(new Command<ProcessDefinitionEntity>() {
            public ProcessDefinitionEntity execute(CommandContext commandContext) {
                ProcessDefinitionEntity latestProcessDefinitionByKey = commandContext.getProcessDefinitionEntityManager()
                        .findLatestProcessDefinitionByKey("my-job-test-11");
                return latestProcessDefinitionByKey;
            }
        });

        log.info("processDefinitionEntity = {}", processDefinitionEntity);
    }

}
