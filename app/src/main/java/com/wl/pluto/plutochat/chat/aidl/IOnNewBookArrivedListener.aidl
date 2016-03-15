// IOnNewBookArrivedＬListener.aidl
package com.wl.pluto.plutochat.chat.aidl;

// Declare any non-default types here with import statements
import com.wl.pluto.plutochat.chat.aidl.BookEntity;

interface IOnNewBookArrivedListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void onNewBookArrived(in BookEntity book);
}
