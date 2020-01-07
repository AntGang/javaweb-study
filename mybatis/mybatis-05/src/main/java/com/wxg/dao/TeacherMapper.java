package com.wxg.dao;

import com.wxg.pojo.Teacher;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TeacherMapper {

    @Select("select * from teacher where id=#{tid}")
    Teacher getTeacherById(@Param("tid")int id);

    //获取一个老师的所有学生
    Teacher getTeacher(@Param("tid")int id);

    //获取一个老师的所有学生
    Teacher getTeacher2(@Param("tid")int id);

}
