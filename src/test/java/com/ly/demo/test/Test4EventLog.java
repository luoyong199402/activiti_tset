package com.ly.demo.test;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.event.EventLogEntry;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.logging.LogMDC;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.Map;

@Slf4j
public class Test4EventLog {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti.cfg_eventLog.xml");


    @Test
    @Deployment(resources = "MyProcess2.bpmn20.xml")
    public void test1() {
        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process2");

        Task task = activitiRule.getTaskService().createTaskQuery().singleResult();
        activitiRule.getTaskService().complete(task.getId());

        List<EventLogEntry> eventLogEntriesByProcessInstanceId = activitiRule.getManagementService().getEventLogEntriesByProcessInstanceId(processInstance.getId());

        for (EventLogEntry eventLogEntry : eventLogEntriesByProcessInstanceId) {
            log.info("eventLog.type = {}, eventLog.data = {}", eventLogEntry.getType(), new String(eventLogEntry.getData()));
        }

    }


    static String toString(HistoricDetail historicDetail) {
        return ToStringBuilder.reflectionToString(historicDetail, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
