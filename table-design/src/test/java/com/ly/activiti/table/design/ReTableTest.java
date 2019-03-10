package com.ly.activiti.table.design;


import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ByteArrayEntity;
import org.activiti.engine.impl.persistence.entity.ByteArrayEntityImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;
import java.util.Set;

/**
 * 流程定义存储细信息
 *  1. act_re_deployment        流程部署记录表
 *  2. act_re_procdef           流程定义信息表
 *  3. act_re_model             模型信息表（用于web设计器）
 *  4. act_procdef_info         流程定义动态改变信息表
 *
 *  1. act_re_deployment （一次部署， 产生多个流程信息， 产生多个流程二级文件）
 *      id_:                    主键
 *      name_:                  名称
 *      category_:              分类
 *      key_:                   分类key值
 *      tenant_id_:             租户id
 *      deploy_time_:           部署时间
 *      engine_version_:        流程版本
 *
 *  2. act_re_procdef  流程定义信息表
 *      id_:                    主键  流程配置文件key:流程version:流程部署id
 *      rev_:                   版本号
 *      category_:              分类信息
 *      name_:                  流程配置文件的 name
 *      key_:                   流程配置文件的id
 *      version_:               版本号
 *      deployment_id_:         流程部署id
 *      resource_name_:         资源名称， 部署时候的xml文件名称
 *      dgrm_resource_name_:    资源的生成的图片名称
 *      description_:           流程定义文件的描述信息
 *      has_start_form_key_:    是否有start_form_Key
 *      has_graphical_notation_:是否有图片信息
 *      suspension_state_:      挂起状态（1： 激活， 2： 挂起）
 *      tenant_id_:             租户id
 *      engine_version_:        流程引擎版本
 */

@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
@ContextConfiguration(locations = {"classpath:activiti-context.xml"})

public class ReTableTest {

    @Autowired
    private ManagementService managementService;

    @Autowired
    private RepositoryService repositoryService;

    @Test
    public void test1() {
        repositoryService.createDeployment()
                .name("这是我的部署信息")
                .addClasspathResource("MyProcess.bpmn20.xml")
                .deploy();

        log.info("流程部署完成！");
    }

    /**
     * 测试挂起流程
     */
    @Test
    public void test2() {
        repositoryService.suspendProcessDefinitionById("myProcess:2:10004");

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId("myProcess:2:10004").singleResult();

        boolean suspended = processDefinition.isSuspended();

        log.info("流程 {} 的状态是 {}", processDefinition.getId(), suspended);
    }

}
