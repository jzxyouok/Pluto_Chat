package com.wl.pluto.plutochat.model;

import android.content.Context;

import java.util.Map;

/**
 * 该类又是基于数据库来管理用户信息
 * Created by jeck on 15-11-19.
 */
public class UserLogicModel extends DefaultLogicModel {
    /**
     * @param context
     ***********************************************************************************/
    public UserLogicModel(Context context) {
        super(context);
    }

    public boolean isDebugMode() {
        return true;
    }

    /**
     * 保存联系人链表
     *
     * @return
     */
    public boolean saveContactsList() {
        return true;
    }

    /**
     * 获取联系人链表
     *
     * @return
     */
    public Map<String, Object> getContactsList() {
        return null;
    }

    /**
     * 关闭数据库
     */
    public void closeDatabase() {

    }

    @Override
    public String getAppProgressName() {
        return context.getPackageName();
    }
}
