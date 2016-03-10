//package com.wl.pluto.plutochat.manager;
//
//import com.wl.pluto.plutochat.constant.GroupConstants;
//import com.wl.pluto.plutochat.entity.XMPPUser;
//
//import org.jivesoftware.smack.SmackException;
//import org.jivesoftware.smack.XMPPConnection;
//import org.jivesoftware.smack.XMPPException;
//import org.jivesoftware.smack.packet.Presence;
//import org.jivesoftware.smack.roster.Roster;
//import org.jivesoftware.smack.roster.RosterEntry;
//import org.jivesoftware.smack.roster.RosterGroup;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by jeck on 15-11-13.
// */
//public class XMPPContactsManager {
//
//    private static XMPPContactsManager instance;
//
//    private Map<String, XMPPUser> contacts;
//
//    private XMPPContactsManager() {
//
//        contacts = new HashMap<>();
//
//        try {
//            init();
//        } catch (XMPPException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (SmackException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    public static XMPPContactsManager getInstance() {
//        if (instance == null) {
//            instance = new XMPPContactsManager();
//        }
//
//        return instance;
//    }
//
//    /**
//     * 初始化联系人链表
//     *
//     * @throws XMPPException
//     * @throws IOException
//     * @throws SmackException
//     */
//    private void init() throws XMPPException, IOException, SmackException {
//
//        XMPPConnection connection = XMPPConnectionManager.getInstance().getConnection();
//        Roster roster = Roster.getInstanceFor(connection);
//        for (RosterEntry entry : roster.getEntries()) {
//            contacts.put(entry.getName(), transEntryToXMPPUser(entry, roster));
//        }
//    }
//
//    /**
//     * 将Entry 转化成User
//     *
//     * @param entry
//     * @param roster
//     * @return
//     */
//    private XMPPUser transEntryToXMPPUser(RosterEntry entry, Roster roster) {
//        XMPPUser user = new XMPPUser();
//        if (entry.getName() == null) {
//            user.setName("pluto");
//        } else {
//            user.setName(entry.getName());
//        }
//
//        user.setJID(entry.getUser());
//        Presence presence = roster.getPresence(entry.getUser());
//        user.setFrom(presence.getFrom());
//        user.setStatus(presence.getStatus());
//        user.setGroupSize(entry.getGroups().size());
//        user.setAvailable(presence.isAvailable());
//        user.setType(entry.getType());
//
//        return user;
//    }
//
//    /**
//     * 获取所有的联系人链表
//     *
//     * @return
//     */
//    public List<XMPPUser> getXmppContactsList() {
//
//        if (contacts == null) {
//            throw new RuntimeException("contacts is null");
//        }
//
//        List<XMPPUser> users = new ArrayList<>();
//
//        for (String key : contacts.keySet()) {
//            users.add(contacts.get(key));
//        }
//
//        return users;
//    }
//
//    /**
//     * 获取所有未分组的联系人链表
//     *
//     * @param roster
//     * @return
//     */
//    public List<XMPPUser> getNoGroupUserList(Roster roster) {
//        List<XMPPUser> userList = new ArrayList<>();
//
//        for (RosterEntry entry : roster.getUnfiledEntries()) {
//
//            //复制一份
//            userList.add(contacts.get(entry.getUser()).clone());
//        }
//
//        return userList;
//    }
//
//    /**
//     * 获取所有分组的联系人
//     *
//     * @return
//     */
//    public List<XMPPRosterGroup> getXMPPRosterGroup(Roster roster) {
//
//        if (contacts == null) {
//            throw new RuntimeException("contacts is null");
//        }
//
//        List<XMPPRosterGroup> groups = new ArrayList<>();
//
//        groups.add(new XMPPRosterGroup(GroupConstants.ALL_FRIEND, getXmppContactsList()));
//
//        for (RosterGroup group : roster.getGroups()) {
//            List<XMPPUser> xmppUsers = new ArrayList<>();
//            for (RosterEntry entry : group.getEntries()) {
//                xmppUsers.add(contacts.get(entry.getUser()));
//            }
//
//            groups.add(new XMPPRosterGroup(group.getName(), xmppUsers));
//        }
//
//        groups.add(new XMPPRosterGroup(GroupConstants.NO_GROUP_FRIEND, getNoGroupUserList(roster)));
//
//        return groups;
//
//    }
//
//    /**
//     * 修改好友的昵称
//     *
//     * @param user
//     * @param nickName
//     * @param connection
//     * @throws SmackException.NotConnectedException
//     * @throws XMPPException.XMPPErrorException
//     * @throws SmackException.NoResponseException
//     */
//    public void setNickName(XMPPUser user, String nickName, XMPPConnection connection)
//            throws SmackException.NotConnectedException, XMPPException.XMPPErrorException,
//            SmackException.NoResponseException {
//
//        RosterEntry entry = Roster.getInstanceFor(connection).getEntry(user.getJID());
//        entry.setName(nickName);
//    }
//
//    /**
//     * 根据Jid, 获得用户昵称
//     *
//     * @param jid
//     * @param connection
//     * @return
//     */
//    public XMPPUser getNickName(final String jid, final XMPPConnection connection) {
//
//        Roster roster = Roster.getInstanceFor(connection);
//
//        for (RosterEntry entry : roster.getEntries()) {
//            String str = entry.getUser();
//
//            if (str.split("/")[0].equals(jid)) {
//                return transEntryToXMPPUser(entry, roster);
//            }
//        }
//
//        return null;
//    }
//
//    /**
//     * 把一个好友添加到一个组中，可以分组聊天，就是多对多的聊天
//     *
//     * @param user
//     * @param groupName
//     * @param connection
//     */
//    public void addXmppUserToGroup(final XMPPUser user, final String groupName, final XMPPConnection connection) {
//
//        if (user == null || groupName == null) {
//            return;
//        }
//
//        //将一个RosterEntry 添加到group 是一个PackCollection,会阻塞线程
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                RosterGroup group = Roster.getInstanceFor(connection).getGroup(groupName);
//                //这个组如果存在，那就直接添加进来，如果不存在，那先创建一个组
//                RosterEntry entry = Roster.getInstanceFor(connection).getEntry(user.getJID());
//
//                try {
//
//                    //如果存在这个组
//                    if (group != null) {
//                        if (entry != null) {
//                            group.addEntry(entry);
//                        }
//                    } else {
//                        //创建一个分组
//                        RosterGroup newGroup = Roster.getInstanceFor(connection).createGroup(groupName);
//                        if (entry != null) {
//                            newGroup.addEntry(entry);
//                        }
//                    }
//
//                } catch (Exception e) {
//
//                }
//            }
//        }).start();
//    }
//
//    /**
//     * 把一个好友从组中删除
//     *
//     * @param user
//     * @param groupName
//     * @param connection
//     */
//    public void removeXmppUserFromGroup(final XMPPUser user, final String groupName, final XMPPConnection connection) {
//
//        if (user == null || groupName == null) {
//            return;
//        }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                RosterGroup group = Roster.getInstanceFor(connection).getGroup(groupName);
//                if (group != null) {
//
//                    try {
//
//                        RosterEntry entry = Roster.getInstanceFor(connection).getEntry(user.getJID());
//
//                        if (entry != null)
//                            group.removeEntry(entry);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
//    }
//
//    /**
//     * 添加分组
//     *
//     * @param groupName
//     * @param connection
//     */
//    public void addGroup(final String groupName, final XMPPConnection connection) {
//
//        if (groupName == null) {
//            return;
//        }
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//
//                    RosterGroup group = Roster.getInstanceFor(connection).getGroup(groupName);
//
//                    //如果存在该分组，则返回
//                    if (group != null) {
//                        return;
//                    } else {
//
//                        //创建一个分组
//                        Roster.getInstanceFor(connection).createGroup(groupName);
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }
//
//    /**
//     * 获取所有组名
//     *
//     * @param roster
//     * @return
//     */
//    public List<String> getAllGroupName(Roster roster) {
//
//        List<String> groupNames = new ArrayList<>();
//        for (RosterGroup group : roster.getGroups()) {
//            groupNames.add(group.getName());
//        }
//        return groupNames;
//    }
//
//    /**
//     * 通过ＪＩＤ来获取XMPPUser
//     *
//     * @param userJid
//     * @param connection
//     * @return
//     */
//    public XMPPUser getXMPPUserBuJid(String userJid, XMPPConnection connection) {
//
//        Roster roster = Roster.getInstanceFor(connection);
//        RosterEntry entry = Roster.getInstanceFor(connection).getEntry(userJid);
//
//        if (entry == null) {
//            return null;
//        }
//
//        XMPPUser user = new XMPPUser();
//
//        if (entry.getName() == null) {
//            user.setName("pluto");
//        } else {
//            user.setName(entry.getName());
//        }
//
//        user.setJID(entry.getUser());
//        user.setGroupSize(entry.getGroups().size());
//        user.setType(entry.getType());
//
//        Presence presence = roster.getPresence(entry.getUser());
//        user.setFrom(presence.getFrom());
//        user.setStatus(presence.getStatus());
//        user.setAvailable(presence.isAvailable());
//        return user;
//    }
//    /****************************************************************************************/
//
//    /**
//     * 花名册的分组
//     */
//    public class XMPPRosterGroup {
//
//        /**
//         * 组名
//         */
//        private String name;
//
//        /**
//         * 对应的链表
//         */
//        private List<XMPPUser> xmppUserList;
//
//        public XMPPRosterGroup(String name, List<XMPPUser> users) {
//            this.name = name;
//            this.xmppUserList = users;
//        }
//
//        public int getCount() {
//            if (xmppUserList != null) {
//                return xmppUserList.size();
//            }
//
//            return 0;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        public List<XMPPUser> getXmppUserList() {
//            return xmppUserList;
//        }
//
//        public void setXmppUserList(List<XMPPUser> xmppUserList) {
//            this.xmppUserList = xmppUserList;
//        }
//    }
//}
