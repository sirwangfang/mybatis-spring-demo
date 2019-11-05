package com.wangfang.dao;

import com.wangfang.domain.Student;

import java.util.List;

public interface StudentMapper {

    public Student getStudent(int id);

    public List<Student> getAllStudent(int id);
}
