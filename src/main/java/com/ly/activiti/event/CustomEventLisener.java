package com.ly.activiti.event;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiEventType;

@Slf4j
public class CustomEventLisener implements ActivitiEventListener {
    public void onEvent(ActivitiEvent activitiEvent) {
        if (ActivitiEventType.CUSTOM.equals(activitiEvent.getType())) {
            log.info("监听到自定义事件 {} \t {}",
                    activitiEvent.getType(), activitiEvent.getProcessInstanceId());
        }
    }

    public boolean isFailOnException() {
        return false;
    }
}
