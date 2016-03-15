package com.wl.pluto.plutochat.chat.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by pluto on 16-1-24.
 */
public class BookEntity implements Parcelable {


    private int bookId;
    private String bookName;

    public BookEntity(int id, String name) {

        this.bookId = id;
        this.bookName = name;
    }

    private BookEntity(Parcel in) {
        bookId = in.readInt();
        bookName = in.readString();
    }

    public static final Creator<BookEntity> CREATOR = new Creator<BookEntity>() {
        @Override
        public BookEntity createFromParcel(Parcel in) {
            return new BookEntity(in);
        }

        @Override
        public BookEntity[] newArray(int size) {
            return new BookEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(bookId);
        dest.writeString(bookName);
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    @Override
    public String toString() {
        return "BookEntity{" +
                "bookId=" + bookId +
                ", bookName='" + bookName + '\'' +
                '}';
    }
}
