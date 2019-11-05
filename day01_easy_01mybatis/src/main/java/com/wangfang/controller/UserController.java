package com.wangfang.controller;

import com.wangfang.dao.IUserDao;
import com.wangfang.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {

    @Autowired
    private IUserDao userMapper;

    @RequestMapping("/getAll")
    @ResponseBody
    public User getAllUser(){
        return (User)userMapper.findAll();
    }

}
