// IBookManager.aidl
package com.wl.pluto.plutochat.chat.aidl;

// Declare any non-default types here with import statements
import com.wl.pluto.plutochat.chat.aidl.BookEntity;
import com.wl.pluto.plutochat.chat.aidl.IOnNewBookArrivedListener;

interface IBookManager {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    List<BookEntity> getBookList();

    void addBook(in BookEntity book);

    //注册观察者,在aidl中使用aidl接口，只能使用aidl接口，不支持普通的java接口
    void registerListener(IOnNewBookArrivedListener listener);

    //注销观察者
    void unRegisterListener(IOnNewBookArrivedListener listener);
}
