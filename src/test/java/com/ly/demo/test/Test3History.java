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
public class Test3History {

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


        // 提交表单
        Task task = activitiRule.getTaskService().createTaskQuery().singleResult();
        log.info("task name {} ", task.getName());
        Map<String, String> properties = Maps.newHashMap();
        properties.put("my key", "my value 2'");
        activitiRule.getFormService().submitTaskFormData(task.getId(), properties);

        //输出历史内容
        // 输出历史活动
        List<HistoricActivityInstance> historicActivityInstances =
                activitiRule.getHistoryService().createHistoricActivityInstanceQuery().listPage(0, 100);

        for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
            log.info("historyActivityInstance = {}", historicActivityInstance);
        }
        log.info("historicActivityInstances size = {}", historicActivityInstances.size());


        // 历史变量
        List<HistoricVariableInstance> historicVariableInstances = activitiRule.getHistoryService().createHistoricVariableInstanceQuery().listPage(0, 100);
        for (HistoricVariableInstance historicVariableInstance : historicVariableInstances) {
            log.info("historicVariableInstance = {}", historicVariableInstance);
        }
        log.info("historicVariableInstances size = {}", historicVariableInstances.size());



        // 输出历史表单
        List<HistoricTaskInstance> historicTaskInstances = activitiRule.getHistoryService().createHistoricTaskInstanceQuery().listPage(0, 100);
        for (HistoricTaskInstance historicTaskInstances1 : historicTaskInstances) {
            log.info("historicTaskInstances1 = {}", historicTaskInstances1);
        }
        log.info("historicTaskInstances size = {}", historicTaskInstances.size());

        // 历史表单属性详情
        List<HistoricDetail> historicDetails =
                activitiRule.getHistoryService().createHistoricDetailQuery().formProperties().listPage(0, 100);
        for (HistoricDetail historicDetail : historicDetails) {
            log.info("historicDetail = {}", toString(historicDetail));
        }
        log.info("historicDetails size = {}", historicDetails.size());

        // 输出历史详情
        List<HistoricDetail> historicDetails2 = activitiRule.getHistoryService().createHistoricDetailQuery().listPage(0, 100);
        for (HistoricDetail historicDetail2 : historicDetails2) {
            log.info("historicDetail2 = {}", toString(historicDetail2));
        }
        log.info("historicDetails2 size = {}", historicDetails2.size());

//        activitiRule.getTaskService().complete(task.getId());
    }


    static String toString(HistoricDetail historicDetail) {
        return ToStringBuilder.reflectionToString(historicDetail, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
