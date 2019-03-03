package com.ly.activiti.table.design;


import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.ProcessDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 身份服务
 *  1. 身份服务关联的表
 *      1. act_id_user              用户表
 *      2. act_id_group             用户组表
 *      3. act_id_membership        用户和用户组 关系表
 *      4. act_id_info              用户额外信息表
 *
 *  2. 身份信息表字段描述
 *      act_id_user
 *          id_                 主键
 *          rev_                版本
 *          first_              firstName
 *          last_               lastName
 *          email_              电子邮箱
 *          pwd_                密码
 *          picture_id_         相片
 *
 *       act_id_group
 *          id_                 主键
 *          rev_                版本
 *          name_               组名
 *          type_               类型
 *
 *       act_id_membership
 *          user_id_            用户id
 *          group_id_           组id
 *
 *       act_id_info
 *          id_                 主键
 *          rev_                版本
 *          user_id             用户id
 *          type                类型（默认为user_info 表示用的额外字段信息）
 *          key_                属性key值
 *          value_              属性value值
 *          password_           密码
 *          parent_id_          父id(用户层级结构)
 */

@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
@ContextConfiguration(locations = {"classpath:activiti-context.xml"})

public class IdTableTest {

    @Autowired
    private ManagementService managementService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private IdentityService identityService;

    @Test
    public void test1() {
        User user = identityService.newUser("user1");
        user.setEmail("user1@qq.com");
        user.setFirstName("user1");
        user.setLastName("lastname1");

        User user2 = identityService.newUser("user2");
        user2.setEmail("test2qq.com");
        user2.setFirstName("user2");
        user2.setLastName("lastname2");

        identityService.saveUser(user);
        identityService.saveUser(user2);

        Group group = identityService.newGroup("group1");
        group.setName("name1");

        identityService.saveGroup(group);

        identityService.createMembership("user1", "group1");
        identityService.createMembership("user2", "group1");

        identityService.setUserInfo("user1", "key1", "value1");
        identityService.setUserInfo("user2", "key2", "value2");
    }

}
