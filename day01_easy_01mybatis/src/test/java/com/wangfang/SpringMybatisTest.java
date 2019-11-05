package com.wangfang;

import com.wangfang.dao.IUserDao;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class SpringMybatisTest {
    @Test
    public void test(){
        ApplicationContext ac =
                new ClassPathXmlApplicationContext("spring-context.xml");
        IUserDao userService = (IUserDao) ac.getBean("userService");
        userService.findAll();
    }

    @Test
    public void testMapper(){
        ApplicationContext ac =
                new ClassPathXmlApplicationContext("spring-context.xml");
        IUserDao userMapper = (IUserDao) ac.getBean("userMapper");
        userMapper.findAll();
    }
}
