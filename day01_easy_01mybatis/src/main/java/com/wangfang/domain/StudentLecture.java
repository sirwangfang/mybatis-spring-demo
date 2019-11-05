package com.wangfang.domain;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class StudentLecture {
    private int id;
    private Integer studentId;
    private Lecture lecture;
    private BigDecimal grade;
    private String note;

}
