package com.ly.act.core.api;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
@ContextConfiguration(locations = {"classpath:activiti-context.xml"})
public class IndentityServiceTest {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private IdentityService identityService;


    @Test
    public void test1() {
        User user1 = identityService.newUser("user1");
        user1.setEmail("test@qq.com");

        User user2 = identityService.newUser("user2");
        user2.setEmail("test2@qq.com");

        identityService.saveUser(user1);
        identityService.saveUser(user2);

        Group group = identityService.newGroup("group1");
        identityService.saveGroup(group);

        Group group2 = identityService.newGroup("group2");
        identityService.saveGroup(group2);

        identityService.createMembership("user1", "group1");
        identityService.createMembership("user2", "group1");
        identityService.createMembership("user1", "group2");

        List<User> userGroup1 = identityService.createUserQuery().memberOfGroup("group1").listPage(0, 100);
        log.info("group1 users = {}", ToStringBuilder.reflectionToString(userGroup1, ToStringStyle.SIMPLE_STYLE));

        List<Group> groups = identityService.createGroupQuery().listPage(0, 100);
        log.info("group length = {}", groups.size());
    }

    /**
     * 测试用户的更新
     */
    @Test
    public void test2() {
        User user = identityService.createUserQuery().userId("user1").singleResult();
        user.setEmail("tset @ qq. com ");
        identityService.saveUser(user);

        user = identityService.createUserQuery().userId("user1").singleResult();
        log.info("user = {}", ToStringBuilder.reflectionToString(user, ToStringStyle.SIMPLE_STYLE));
    }
}
