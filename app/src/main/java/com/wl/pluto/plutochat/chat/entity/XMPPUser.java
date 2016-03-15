//package com.wl.pluto.plutochat.chat.entity;
//
//
//import android.os.Parcel;
//import android.os.Parcelable;
//
//import org.jivesoftware.smack.roster.packet.RosterPacket;
//
///**
// * Intent 可以传递实现了Parcelabel接口的类，实现Parcelable接口，需要实现三个方法
// * 　１：describeContents
// * 　２：将需要的数据写入Parcel中，框架调用这个方法传递数据
// * 　３：重写外部类反序列化该类时调用的方法.
// * <p/>
// * Created by jeck on 15-11-13.
// */
//public class XMPPUser implements Parcelable {
//
//    /**
//     * 将XMPPUser保存在Intent 中需要用到这个ｋｅｙ
//     */
//    public static final String XMPP_USER_KEY = "xmpp_user_key";
//
//    /**
//     * 昵称
//     */
//    private String name;
//
//    /**
//     * ＪＩＤ　还不知道是干什么的，应该和你之前定义的phoneNumber有关
//     */
//    private String JID;
//
//    /**
//     * 　来源
//     */
//    private String from;
//
//    /**
//     * 　状态
//     */
//    private String status;
//
//    /**
//     * 　所在的组名
//     */
//    private String groupName;
//
//    /**
//     * 　类型，不知道是指哪方面的
//     */
//    private static RosterPacket.ItemType type;
//
//    /**
//     * 用户状态对应的图片
//     */
//    private int statusImageId;
//
//    /**
//     * group 的　ｓｉｚｅ
//     */
//    private int groupSize;
//
//    /**
//     * 可访问性
//     */
//    private boolean available;
//
//
//    public XMPPUser() {
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getJID() {
//        return JID;
//    }
//
//    public void setJID(String JID) {
//        this.JID = JID;
//    }
//
//    public String getFrom() {
//        return from;
//    }
//
//    public void setFrom(String from) {
//        this.from = from;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public String getGroupName() {
//        return groupName;
//    }
//
//    public void setGroupName(String groupName) {
//        this.groupName = groupName;
//    }
//
//    public static RosterPacket.ItemType getType() {
//        return type;
//    }
//
//    public static void setType(RosterPacket.ItemType type) {
//        XMPPUser.type = type;
//    }
//
//    public int getStatusImageId() {
//        return statusImageId;
//    }
//
//    public void setStatusImageId(int statusImageId) {
//        this.statusImageId = statusImageId;
//    }
//
//    public int getGroupSize() {
//        return groupSize;
//    }
//
//    public void setGroupSize(int groupSize) {
//        this.groupSize = groupSize;
//    }
//
//    public boolean isAvailable() {
//        return available;
//    }
//
//    public void setAvailable(boolean available) {
//        this.available = available;
//    }
//
//    public static final Creator<XMPPUser> CREATOR = new Creator<XMPPUser>() {
//        @Override
//        public XMPPUser createFromParcel(Parcel in) {
//            XMPPUser user = new XMPPUser();
//            user.JID = in.readString();
//            user.name = in.readString();
//            user.from = in.readString();
//            user.status = in.readString();
//            user.available = in.readInt() == 1 ? true : false;
//            return user;
//        }
//
//        @Override
//        public XMPPUser[] newArray(int size) {
//            return new XMPPUser[size];
//        }
//    };
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(JID);
//        dest.writeString(name);
//        dest.writeString(from);
//        dest.writeString(status);
//        dest.writeInt(available ? 1 : 0);
//    }
//
//    @Override
//    public XMPPUser clone() {
//        XMPPUser user = new XMPPUser();
//        user.setAvailable(this.available);
//        user.setFrom(this.from);
//        user.setGroupName(this.groupName);
//        user.setStatusImageId(this.statusImageId);
//        user.setJID(this.JID);
//        user.setName(this.name);
//        user.setGroupSize(this.groupSize);
//        user.setStatus(this.status);
//        return user;
//    }
//
//    @Override
//    public String toString() {
//        return "XMPPUser{" +
//                "name='" + name + '\'' +
//                ", JID='" + JID + '\'' +
//                ", from='" + from + '\'' +
//                ", status='" + status + '\'' +
//                ", groupName='" + groupName + '\'' +
//                ", statusImageId=" + statusImageId +
//                '}';
//    }
//}
