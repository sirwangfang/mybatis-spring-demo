package com.wangfang.domain;

import org.apache.ibatis.type.Alias;

import java.io.Serializable;
@Alias("user")
public class User implements Serializable {
    private int id;
    private String username;
    private String gender;
    private String address;
    private String birthday;

    public Integer getId() {
        return id;
    }

    public User(String username, String gender, String address, String birthday) {
        this.username = username;
        this.gender = gender;
        this.address = address;
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", gender='" + gender + '\'' +
                ", address='" + address + '\'' +
                ", birthday='" + birthday + '\'' +
                '}';
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


}
