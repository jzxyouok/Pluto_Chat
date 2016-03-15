package com.wl.pluto.plutochat.chat.widget;

import com.wl.pluto.plutochat.chat.entity.ContactsItemData;

import java.util.Comparator;

/**
 * 可排序的类
 * <p>
 * Created by jeck on 15-11-1.
 */
public class PinYinComparator implements Comparator<ContactsItemData> {
    @Override
    public int compare(ContactsItemData model1, ContactsItemData model2) {

        if (model1.getSortLetters().equals("@") || model2.getSortLetters().equals("#")) {
            return -1;
        } else if (model1.getSortLetters().equals("#") || model2.getSortLetters().equals("@")) {
            return 1;
        } else {
            return model1.getSortLetters().compareTo(model2.getSortLetters());
        }
    }
}
