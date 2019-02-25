package com.ly.activiti.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.impl.interceptor.AbstractCommandInterceptor;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandConfig;

/**
 * 测试时间拦截器
 */
@Slf4j
public class TimeCommondInterceptor extends AbstractCommandInterceptor {

    public <T> T execute(CommandConfig config, Command<T> command) {
        Long startTime = System.currentTimeMillis();

        try {
            return this.getNext().execute(config, command);
        } finally {
            long time = System.currentTimeMillis() - startTime;
            log.info("{} 执行时长 {} 毫秒 {}", command.getClass().getCanonicalName(), time);
        }
    }
}
