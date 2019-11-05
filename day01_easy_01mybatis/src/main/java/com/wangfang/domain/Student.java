package com.wangfang.domain;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.util.List;

@Alias("student")
@Data
public class Student {
    private Integer id;
    private String cnname;
    private String sex;
    private String selfCardNo;
    private String note;
    private StudentSelfCard studentSelfcard;
    private List<StudentLecture> lectureList;
}
