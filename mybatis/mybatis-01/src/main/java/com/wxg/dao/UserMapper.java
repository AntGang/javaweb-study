package com.wxg.dao;

import com.wxg.pojo.User;

import java.util.List;
import java.util.Map;

public interface UserMapper {

    List<User> likeByName(String name);

    //查询全部用户
    List<User> getUserList();

    //根据id查询用户
    User getUserById(int id);

    //万能Map
    int addUser2(Map<String, Object> map);

    //insert一个用户
    int addUser(User user);

    //修改用户
    int updateUser(User user);

    //删除一个用户
    int deleteUser(int id);
}
