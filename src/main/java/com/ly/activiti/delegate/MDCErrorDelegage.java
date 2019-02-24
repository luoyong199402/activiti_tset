package com.ly.activiti.delegate;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

@Slf4j
public class MDCErrorDelegage implements JavaDelegate {

    public void execute(DelegateExecution execution) {
        log.info("run  MDCErrorDelegage");
        throw new RuntimeException("on task");
    }
}
