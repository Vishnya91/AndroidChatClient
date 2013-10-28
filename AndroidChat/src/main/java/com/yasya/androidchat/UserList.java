package com.yasya.androidchat;

import java.util.ArrayList;

public class UserList {
    private String ClassName = "UserList";

    private ArrayList<UserName> userList = null;

    public ArrayList<UserName> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<UserName> userList) {
        this.userList = userList;
    }


}
