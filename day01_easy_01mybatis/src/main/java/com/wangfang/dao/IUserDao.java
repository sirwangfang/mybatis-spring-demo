package com.wangfang.dao;

import com.wangfang.domain.User;
import com.wangfang.domain.UserParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IUserDao {

    List<User> findAll();

    User findUser(Integer id,String username);

    boolean saveUser(User user);

    boolean saveUsers(List<User> list);
    User findUserByParamBean(UserParam param);
    /**
     * 使用javabean存储参数
     * @param param
     * @return
     */
    boolean saveUser(UserParam param);

    /**
     * mybatis多参数传值
     * @param address
     * @param id
     * @return
     */
    boolean setUser(@Param("address") String address, @Param("id") Integer id);

    User getUserByParam(@Param("id") int id,@Param("username") String username);

    List<User> getUserByTag2(UserParam param);

    List<User> getUserByTag3(UserParam param);

    List<User> getUserByTag5(UserParam param);

    boolean setByTag(UserParam param);

    boolean setByTagPassByTrim(UserParam param);

    List<User> getByForeach(List<?> list);

    List<User> getUserByBind(@Param("username") String username);
}
