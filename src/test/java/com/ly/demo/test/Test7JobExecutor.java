package com.ly.demo.test;

import lombok.extern.slf4j.Slf4j;
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

import java.util.List;

@Slf4j
public class Test7JobExecutor {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti.cfg_jobexcutor.xml");


    @Test
    @Deployment(resources = "MyProcess2_job.bpmn20.xml")
    public void test1() throws InterruptedException {
        log.info("start");

        List<Job> jobs = activitiRule.getManagementService().createTimerJobQuery().listPage(0, 100);

        for (Job job : jobs) {
            log.info("定时任务 = {}， 默认重试次数 = {} ", job, job.getRetries());
        }

        log.info("jobList.size = {}", jobs.size());

        Thread.sleep(100 * 1000);
        log.info("end");
    }

    static String toString(HistoricDetail historicDetail) {
        return ToStringBuilder.reflectionToString(historicDetail, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
