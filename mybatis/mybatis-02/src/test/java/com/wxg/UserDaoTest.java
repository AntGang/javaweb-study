package com.wxg;


import com.wxg.dao.UserMapper;
import com.wxg.pojo.User;
import com.wxg.utils.MyBatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

public class UserDaoTest {

    @Test
    public void test(){
        //第一步：获得SqlSession对象
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        try{
            //方式一：getMapper
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            List<User> userList = mapper.getUserList();


            //方式二：
            //List<User> userList = sqlSession.selectList("com.wxg.dao.UserDao.getUserList");

            for (User user : userList) {
                System.out.println(user);

            }

        }
        finally {
            //关闭SqlSession
            sqlSession.close();
        }
    }


    @Test
    public void addUser(){
        SqlSession sqlSession=MyBatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        int res=mapper.addUser(new User(5,"老五","1234"));
        System.out.println(res);
        sqlSession.commit();
        sqlSession.close();

    }




}

