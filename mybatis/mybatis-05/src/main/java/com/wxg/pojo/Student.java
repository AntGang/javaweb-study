package com.wxg.pojo;

import lombok.Data;

@Data
public class Student {
    private int id;
    private String name;

    //多对一
    private Teacher teacher;

    //一个老师对应多个学生
    //private int tid;
}
