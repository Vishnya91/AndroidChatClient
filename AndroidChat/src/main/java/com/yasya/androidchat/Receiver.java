package com.yasya.androidchat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Receiver {
    UserName username = new UserName();
    Message message = new Message();
    String msgUI;
    static ArrayList<String> users;
    private static Receiver instance;

    public Receiver() {
    }

    public static Receiver getInstance() {
        if (instance == null) {
            instance = new Receiver();
        }
        return instance;
    }

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
                JSONArray jsonArray = list.getJSONArray("userList");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jObject = jsonArray.getJSONObject(i);
                    String user = (String) jObject.get("userName");
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
            login.put("userName", username.getUserName());
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
