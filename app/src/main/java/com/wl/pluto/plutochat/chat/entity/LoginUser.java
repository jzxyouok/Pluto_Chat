package com.wl.pluto.plutochat.chat.entity;

/**
 * 登录用户
 * Created by jeck on 15-10-27.
 */
public class LoginUser extends UserEntity {

    /**
     * 密码
     */
    private String userPassword = "";

    public LoginUser() {

    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    @Override
    public String toString() {
        return "LoginUser{" +
                "userName='" + username + '\'' +
                ", userPassword='" + userPassword + '\'' +
                '}';
    }
}
