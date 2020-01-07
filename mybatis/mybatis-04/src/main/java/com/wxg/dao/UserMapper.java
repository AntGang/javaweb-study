package com.wxg.dao;

import com.wxg.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface UserMapper {
    //查询全部用户
    @Select("select * from user")
    List<User> getUserList();

    //方法存在多个参数时，所有参数前面必须加上@Param（"id")
    @Select("select * from user where id=#{id}")
    User getUserById(@Param("id") int id);

    @Insert("insert into user(id,name,pwd) values (#{id},#{name},#{password})")
    int addUuser(User user);
}


