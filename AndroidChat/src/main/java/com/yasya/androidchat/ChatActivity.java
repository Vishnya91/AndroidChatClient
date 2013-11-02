package com.yasya.androidchat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatActivity extends Activity {

    Context context;
    ListView msgView;
    ArrayAdapter<String> msgList;
    Socket socket;
    NetworkTask networktask;
    FragListView fragList;
    FragmentManager fMan;
    FragmentTransaction ft;
    Receiver receiver;
    boolean connected;
    boolean received;
    Button btnSend;
    EditText txtEdit;
    TextView connInfo;
    private static long back_pressed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        context = getApplicationContext();
        receiver = Receiver.getInstance();
        fragList = new FragListView();
        msgView = (ListView) findViewById(R.id.listView);
        btnSend = (Button) findViewById(R.id.btn_Send);
        txtEdit = (EditText) findViewById(R.id.txt_inputText);
        setOrientation();
        checkConnectivity();

        msgList = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);
        msgView.setAdapter(msgList);
        networktask = new NetworkTask();
        networktask.execute();
        Log.e("received onCreate", String.valueOf(received));

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receiver.receiveMessage(txtEdit.getText().toString());
                sendMessageToServer(receiver.outMessage());
                msgView.smoothScrollToPosition(msgList.getCount() - 1);

            }
        });

    }

    private void initFragment() {
        connInfo = (TextView) findViewById(R.id.txt_conn_info);
        if (receiver.UIList() != null) {
            Log.e("array from activity", receiver.UIList().toString());
            connInfo.setVisibility(View.GONE);
            fragList = FragListView.getInstance();
            fMan = getFragmentManager();
            ft = fMan.beginTransaction();
            ft.add(R.id.frame, fragList);
            ft.commit();
        } else {
            connInfo.setVisibility(View.VISIBLE);
        }
    }

    private void checkConnectivity() {
        if (isNetworkAvailable()) {
            getLogin();
            this.setTitle("Android Chat - disconnected");
            this.setTitleColor(Color.RED);
        } else {
            btnSend.setEnabled(false);
            txtEdit.setEnabled(false);
            Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
            this.setTitle("Android Chat - disconnected");
            this.setTitleColor(Color.RED);
        }
        //todo check for internet connection and connected = true
    }

    public boolean isNetworkAvailable() {
        Context context = getApplicationContext();
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void setOrientation() {
        // Get current screen orientation
        int orientation = this.getResources().getConfiguration().orientation;
        switch (orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "Exit");
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.setGroupVisible(0, connected);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                logOut();
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    public void getLogin() {
        final EditText setLogin = new EditText(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                String login = setLogin.getText().toString();
                if (login.matches("")) {
                    Toast.makeText(getApplicationContext(), "You did not enter a login", Toast.LENGTH_SHORT).show();
                } else {
                    receiver.receiveLogin(login);
                    sendLoginToServer();
                }
            }
        });

        builder.setTitle("Enter Login");
        builder.setView(setLogin);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void sendMessageToServer(String str) {
        str = receiver.outMessage();
        try {
            OutputStream os = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeUTF(str);
            dos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendLoginToServer() {
        String login = receiver.outLogin();
        Toast.makeText(context, login, Toast.LENGTH_LONG).show();
        try {
            OutputStream os = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeUTF(login);
            dos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void receive() {
        InputStream os;
        DataInputStream dos = null;
        try {
            os = socket.getInputStream();
            dos = new DataInputStream(os);

        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            String msg = null;
            try {
                msg = dos.readUTF();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (msg == null) {
                break;
            } else {
                receiver.receiveString(msg);
                received = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setTitle("Android Chat - connected");
                        setTitleColor(Color.GREEN);
                        initFragment();
                    }
                });
                String msgFromReceiver;
                if (receiver.UIMessage() != null) {
                    msgFromReceiver = receiver.UIMessage();
                    Message message = Message.obtain();
                    message.obj = msgFromReceiver;
                    message.setTarget(handler);
                    message.sendToTarget();
                }
            }
        }
    }

    public void logOut() {
        try {
          //  networktask.cancel(true);
            socket.close();
            connected = false;
            ft.hide(fragList);
            connInfo.setVisibility(View.VISIBLE);
            this.setTitle("Android Chat - disconnected");
            this.setTitleColor(Color.RED);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) super.onBackPressed();
        else
            Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String message = (String) msg.obj;
            msgList.add(message);
            msgView.setAdapter(msgList);
            msgView.smoothScrollToPosition(msgList.getCount() - 1);
        }
    };


    public class NetworkTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            String host3 = "192.168.0.100";
            try {
                socket = new Socket(host3, 6005);
                connected = socket.isConnected();
                receive();

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}