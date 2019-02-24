package com.ly.demo.test;

        import lombok.extern.slf4j.Slf4j;
        import org.activiti.engine.ProcessEngineConfiguration;
        import org.activiti.engine.logging.LogMDC;
        import org.junit.Test;

@Slf4j
public class Test1DatabaseConfig {

    @Test
    public void test1() {
        ProcessEngineConfiguration processEngineConfigurationFromResourceDefault = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResourceDefault();

        processEngineConfigurationFromResourceDefault.buildProcessEngine();
        log.info("ProcessEngineConfiguration = {}", processEngineConfigurationFromResourceDefault);
    }

    @Test
    public void test2() {
        LogMDC.setMDCEnabled(true);
        ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration
                .createStandaloneProcessEngineConfiguration();

        log.info("ProcessEngineConfiguration = {}", processEngineConfiguration);
    }
}
