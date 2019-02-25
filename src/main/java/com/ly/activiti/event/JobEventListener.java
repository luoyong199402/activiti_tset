package com.ly.activiti.event;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class JobEventListener implements ActivitiEventListener {
    public void onEvent(ActivitiEvent event) {
        String name = event.getType().name();

        if (StringUtils.startsWith(name, "TIMER") || StringUtils.startsWith(name, "JOB")) {
            log.info("定时事件 = {}", name);
        }
    }

    public boolean isFailOnException() {
        return false;
    }
}
