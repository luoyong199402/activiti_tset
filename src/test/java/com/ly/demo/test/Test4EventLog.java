package com.ly.demo.test;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.logging.LogMDC;
import org.activiti.engine.runtime.Execution;
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
    public ActivitiRule activitiRule = new ActivitiRule("activiti.cfg_history.xml");


    @Test
    @Deployment(resources = "MyProcess2.bpmn20.xml")
    public void test1() {
        // 创建变量
        Map<String, Object> varableMap = Maps.newHashMap();

        varableMap.put("key1", "value1");
        varableMap.put("key2", "value2");

        // 启动流程
       LogMDC.setMDCEnabled(true);
        activitiRule.getRuntimeService().startProcessInstanceByKey("my-process2", varableMap);

        // 修改变量
        List<Execution> executions = activitiRule.getRuntimeService().createExecutionQuery().listPage(0, 100);
        for (Execution execution : executions) {
            log.info("execution = {}", execution);
        }
        log.info("executions size = {}", executions.size());
        String id = executions.iterator().next().getId();
        activitiRule.getRuntimeService().setVariable(id, "key1", "modify value 1");


    }


    static String toString(HistoricDetail historicDetail) {
        return ToStringBuilder.reflectionToString(historicDetail, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
