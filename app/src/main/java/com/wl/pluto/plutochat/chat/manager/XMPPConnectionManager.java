//package com.wl.pluto.plutochat.chat.manager;
//
//import com.wl.pluto.plutochat.chat.base.BaseApplication;
//import com.wl.pluto.plutochat.chat.entity.LoginConfig;
//
//import org.jivesoftware.smack.ConnectionConfiguration;
//import org.jivesoftware.smack.SmackException;
//import org.jivesoftware.smack.XMPPException;
//import org.jivesoftware.smack.roster.Roster;
//import org.jivesoftware.smack.tcp.XMPPTCPConnection;
//import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
//
//import java.io.IOException;
//
///**
// * Created by jeck on 15-11-8.
// */
//public class XMPPConnectionManager {
//
//    /**
//     * Builder
//     */
//    private XMPPTCPConnectionConfiguration.Builder builder;
//
//
//    /**
//     * 登录配置信息
//     */
//    private LoginConfig loginConfig;
//
//    /**
//     * 实例
//     */
//    private volatile static XMPPConnectionManager instance;
//
//    private XMPPConnectionManager() {
//
//        //这个在第一次调用的时候会取得服务器的地址
//        loginConfig = BaseApplication.getInstance().getLoginConfig();
//
//        builder = XMPPTCPConnectionConfiguration.builder();
//        builder.setHost(loginConfig.getXmppHost());
//        builder.setPort(loginConfig.getXmppPost());
//
//        //这个地方只能用域名，不可以用ｉｐ地址
//        builder.setServiceName(loginConfig.getXmppServerDomainName());
//
//        //允许调试
//        builder.setDebuggerEnabled(true);
//
//        //
//        builder.setCompressionEnabled(false);
//
//        //允许登录成功后跟新在线状态
//        builder.setSendPresence(true);
//
//        //收到好友请求后，　manual标示需要经过同意才可以成为好友，accept_all标示不经过同意，自动成为好友
//        Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all);
//
//        //安全验证
//        //SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
//
//
//        builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
//
//    }
//
//    public static XMPPConnectionManager getInstance() {
//
//        if (instance == null) {
//            synchronized (XMPPConnectionManager.class) {
//
//                if (instance == null) {
//
//                    instance = new XMPPConnectionManager();
//                }
//            }
//        }
//
//        return instance;
//    }
//
//    /**
//     * 获取到服务器端的XMPP链接
//     *
//     * @return
//     */
//    public XMPPTCPConnection getConnection() throws IOException, XMPPException, SmackException {
//
//        XMPPTCPConnection connection = new XMPPTCPConnection(builder.build());
//
//        connection.connect();
//
//        return connection;
//    }
//}
