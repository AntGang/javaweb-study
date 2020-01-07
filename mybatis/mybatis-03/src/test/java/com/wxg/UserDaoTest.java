package com.wxg;


import com.wxg.dao.UserMapper;
import com.wxg.pojo.User;
import com.wxg.utils.MyBatisUtils;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

public class UserDaoTest {

    static Logger logger = Logger.getLogger(UserDaoTest.class); //LogDemo为相关的类

    @Test
    public void test(){
        //第一步：获得SqlSession对象
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        try{
            //方式一：getMapper
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            User user = mapper.getUserById(1);


            //方式二：
            //List<User> userList = sqlSession.selectList("com.wxg.dao.UserDao.getUserList");

                System.out.println(user);
        }
        finally {
            //关闭SqlSession
            sqlSession.close();
        }
    }

    @Test
    public void testLog4j(){
        logger.info("info:进入testLog4j");
        logger.debug("debug:进入testLog4j");
        logger.error("error:进入testLog4j");
    }


    @Test
    public void getUserByLimit(){
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
        hashMap.put("startIndex",1);
        hashMap.put("pageSize",2);
        List<User> userList = userMapper.getUserByLimit(hashMap);
        for (User user : userList) {
            System.out.println(user);
        }
        sqlSession.close();
    }

    @Test
    public void getUserByRowBounds(){
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        RowBounds rowBounds = new RowBounds(1,2);
        List<User> userList=sqlSession.selectList("getUserByRowBounds",null,rowBounds);

        for (User user : userList) {
            System.out.println(user);
        }
        sqlSession.close();
    }
}

