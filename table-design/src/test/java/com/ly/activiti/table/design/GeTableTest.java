package com.ly.activiti.table.design;


import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ByteArrayEntity;
import org.activiti.engine.impl.persistence.entity.ByteArrayEntityImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
@ContextConfiguration(locations = {"classpath:activiti-context.xml"})

/**
 *
 * 通用数据库有两张表构成
 *  1. act_ge_property 存储activiti的版本元数据
 *  2. act_ge_byteArray 存储流程引擎的二进制资源文件
 *
 * 通用数据表的字段描述
 *  1. act_ge_property
 *      name_:              activiti元数据key值
 *      values_:            activiti元数据value值
 *      rev_ :              activiti元数据版本号
 *
 *  2. act_ge_bytearray
 *      id_                 主键
 *      rev_                版本号
 *      name_               部署二进制流资源名称
 *      deployment_id_      部署id（一次部署可以部署多个资源文件）
 *      bytes_              二进制文件（可能是xml配置文件， 或者是png图片文件）
 *      generated_          是否为自动生成（0： 部署文件， 1： 表示生成的图片文件）
 */
public class GeTableTest {

    @Autowired
    private ManagementService managementService;

    @Autowired
    private RepositoryService repositoryService;

    /**
     * 获取表名称
     */
    @Test
    public void test1() {
        Map<String, Long> tableCount = managementService.getTableCount();

        Set<Map.Entry<String, Long>> entries = tableCount.entrySet();

        for (Map.Entry<String, Long> entry : entries) {
            log.info("tableName = {}", entry.getKey());
            log.info("tableCount = {}", entry.getValue());
        }
    }

    /**
     * 清理表结构
     */
    @Test
    public void test2() {
        managementService.executeCommand(new Command<Object>() {
            public Object execute(CommandContext commandContext) {
                commandContext.getDbSqlSession().dbSchemaDrop();
                log.info("清空表");
                return null;
            }
        });
    }

    /**
     * 使用核心api部署资源。 查看表信息
     */
    @Test
    public void test3() {
        repositoryService.createDeployment()
                .name("测试部署一")
                .addClasspathResource("MyProcess.bpmn20.xml")
                .deploy();

        log.info("流程部署完成");
    }

    /**
     * 手工添加资源数据
     */
    @Test
    public void test4() {
        managementService.executeCommand(new Command<Object>() {
            public Object execute(CommandContext commandContext) {
                ByteArrayEntity entity = new ByteArrayEntityImpl();
                entity.setName("123");
                entity.setBytes("test message 1".getBytes());

                commandContext.getByteArrayEntityManager()
                        .insert(entity);

                return null;
            }
        });

        log.info("手动写入资源库成功！");
    }

}
