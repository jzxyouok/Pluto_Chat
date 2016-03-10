//package com.wl.pluto.plutochat.manager;
//
//import org.jivesoftware.smack.SmackException;
//import org.jivesoftware.smack.XMPPConnection;
//import org.jivesoftware.smack.XMPPException;
//import org.jivesoftware.smackx.vcardtemp.packet.VCard;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//
///**
// * XMPPUser 管理
// * Created by jeck on 15-11-13.
// */
//public class XMPPUserManager {
//
//    private static XMPPUserManager ourInstance = new XMPPUserManager();
//
//    public static XMPPUserManager getInstance() {
//        return ourInstance;
//    }
//
//    private XMPPUserManager() {
//    }
//
//    /**
//     * 获取用户的VCard
//     *
//     * @param jid
//     * @return
//     */
//    public VCard getUserVCard(String jid) throws XMPPException, IOException, SmackException {
//
//        VCard vCard = new VCard();
//
//        XMPPConnection connection = XMPPConnectionManager.getInstance().getConnection();
//
//        vCard.load(connection, jid);
//
//        return vCard;
//    }
//
//    /**
//     * @param card
//     * @return
//     * @throws XMPPException
//     * @throws IOException
//     * @throws SmackException
//     */
//    public VCard saveUserVCard(VCard card) throws XMPPException, IOException, SmackException {
//
//        XMPPConnection connection = XMPPConnectionManager.getInstance().getConnection();
//        card.save(connection);
//
//        return getUserVCard(card.getJabberId());
//    }
//
//    /**
//     * 获取用户头像信息
//     *
//     * @param jid
//     * @return
//     * @throws XMPPException
//     * @throws IOException
//     * @throws SmackException
//     */
//    public InputStream getUserImage(String jid) throws XMPPException, IOException, SmackException {
//        XMPPConnection connection = XMPPConnectionManager.getInstance().getConnection();
//
//        try {
//
//            VCard card = new VCard();
//
//            card.load(connection, jid);
//
//            if (card == null || card.getAvatar() == null) {
//                return null;
//            }
//
//            ByteArrayInputStream stream = new ByteArrayInputStream(card.getAvatar());
//
//            return stream;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//}
