package com.yasya.androidchat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Receiver {
    UserName username = new UserName();
    Message message = new Message();
    String msgUI;
    ArrayList<String> users;

    public void receiveString(String str) {
        if (str.contains("Message")) {
            try {
                JSONObject msg = new JSONObject(str);
                msgUI = (String) msg.get("message");

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (str.contains("UserList")) {
            users = new ArrayList<String>();
            try {
                JSONObject list = new JSONObject(str);
                JSONArray jsonArray = list.getJSONArray("UserList");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jObject = jsonArray.getJSONObject(i);
                    String user = jObject.getString("UserName");
                    users.add(user);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void receiveLogin(String str) {
        username.setUserName(str);
    }

    public void receiveMessage(String str) {
        message.setMessage(str);
    }

    public String outLogin() {
        JSONObject login = new JSONObject();
        try {
            login.put("ClassName", "UserName");
            login.put("UserName", username.getUserName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return login.toString();
    }

    public String outMessage() {
        JSONObject outMessage = new JSONObject();

        try {
            outMessage.put("ClassName", "Message");
            outMessage.put("message", message.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return outMessage.toString();
    }

    public ArrayList<String> UIList() {
        return users;
    }

    public String UIMessage() {
        return msgUI;
    }
}
