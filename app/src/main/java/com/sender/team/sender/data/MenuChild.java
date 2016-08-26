package com.sender.team.sender.data;

import com.sender.team.sender.MenuAdapter;

/**
 * Created by Tacademy on 2016-08-25.
 */
public class MenuChild extends NaviItem {
    public int childMenuType;
    public String childName;

    public MenuChild(int childMenuType, String childName) {
        this.type = MenuAdapter.CHILD;
        this.childMenuType = childMenuType;
        this.childName = childName;
    }
}
