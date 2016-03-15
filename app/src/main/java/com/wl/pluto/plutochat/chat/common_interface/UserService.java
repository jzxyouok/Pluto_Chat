package com.wl.pluto.plutochat.chat.common_interface;

/**
 * Created by jeck on 15-11-5.
 */
public interface UserService {

    /**
     * 登录接口
     *
     * @param loginName
     * @param loginPassword
     * @throws Exception
     */
    void userLogin(String loginName, String loginPassword) throws Exception;
}
