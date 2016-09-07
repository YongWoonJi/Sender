package com.sender.team.sender.data;

import java.util.List;

/**
 * Created by Tacademy on 2016-08-25.
 */
public class MenuGroup extends NaviItem {
    public String groupName;
    public int icon;
    public List<MenuChild> children;
    public boolean isHaveChild;
    public int alarm;

    public MenuGroup(int type, String groupName, int icon, boolean isHaveChild, int alarm) {
        this.type = type;
        this.groupName = groupName;
        this.icon = icon;
        this.isHaveChild = isHaveChild;
        this.alarm = alarm;
    }
}
