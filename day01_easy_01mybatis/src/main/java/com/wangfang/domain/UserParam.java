package com.wangfang.domain;

import org.apache.ibatis.type.Alias;

@Alias("userParam")
public class UserParam {
    private int id;
    private String username;
    private String gender;
    private String address;
    private String birthday;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;

    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public UserParam(){

    }

    public UserParam(int id,String username, String gender, String address, String birthday) {
        this.id = id;
        this.username = username;
        this.gender = gender;
        this.address = address;
        this.birthday = birthday;
    }
}
