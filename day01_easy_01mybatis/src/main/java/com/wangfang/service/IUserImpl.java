package com.wangfang.service;

import com.wangfang.dao.IUserDao;
import com.wangfang.domain.User;
import com.wangfang.domain.UserParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IUserImpl implements IUserDao{

    @Autowired
    private IUserDao userDao;
    public List<User> findAll() {
        return userDao.findAll();
    }

    public User findUser(Integer id, String username) {
        return null;
    }

    public boolean saveUser(User user) {
        return false;
    }

    public boolean saveUsers(List<User> list) {
        return false;
    }

    public User findUserByParamBean(UserParam param) {
        return null;
    }

    public boolean saveUser(UserParam param) {
        return false;
    }

    public boolean setUser(String address, Integer id) {
        return false;
    }

    public User getUserByParam(int id, String username) {
        return null;
    }

    public List<User> getUserByTag2(UserParam param) {
        return null;
    }

    public List<User> getUserByTag3(UserParam param) {
        return null;
    }

    public List<User> getUserByTag5(UserParam param) {
        return null;
    }

    public boolean setByTag(UserParam param) {
        return false;
    }

    public boolean setByTagPassByTrim(UserParam param) {
        return false;
    }

    public List<User> getByForeach(List<?> list) {
        return null;
    }

    public List<User> getUserByBind(String username) {
        return null;
    }
}
