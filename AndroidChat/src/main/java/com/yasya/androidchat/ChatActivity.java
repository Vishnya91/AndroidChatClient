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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatActivity extends Activity {

    ListView msgView;
    ArrayAdapter<String> msgList;
    Socket socket;
    NetworkTask networktask;
    FragListView fragList;
    Receiver receiver;
    boolean connected;
    Button btnSend;
    EditText txtEdit;
    private static long back_pressed;

    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        receiver = new Receiver();
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
        TextView connInfo = (TextView) findViewById(R.id.txt_conn_info);
        if (receiver.UIList() != null) {
            connInfo.setVisibility(View.GONE);
            fragList = new FragListView();
            FragmentManager fMan = getFragmentManager();
            FragmentTransaction ft = fMan.beginTransaction();
            ft.add(R.id.frame, fragList);
            ft.commit();
        } else {
            connInfo.setVisibility(View.VISIBLE);
        }
    }

    private void checkConnectivity() {
        if (!connected && isNetworkAvailable()) {
            getLogin();
            this.setTitle("Android Chat - disconnected");
            this.setTitleColor(Color.RED);
        } else if (!isNetworkAvailable()) {
            btnSend.setEnabled(false);
            txtEdit.setEnabled(false);
            Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
            this.setTitle("Android Chat - disconnected");
            this.setTitleColor(Color.RED);
        } else if (connected) {
            Toast.makeText(this, "Connected!", Toast.LENGTH_LONG).show();
            initFragment();
            this.setTitle("Android Chat - connected");
            this.setTitleColor(Color.GREEN);

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
       /* MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);*/
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
                // login.matches("");
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
        final String str1 = str;
        PrintWriter out;
        try {
            out = new PrintWriter(socket.getOutputStream());
            out.println(str1);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendLoginToServer() {
        String login = receiver.outLogin();
        Toast.makeText(getApplicationContext(), login, Toast.LENGTH_LONG).show();
        PrintWriter out;
        try {
            out = new PrintWriter(socket.getOutputStream());
            out.print(login);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void receive() {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (true) {
            String msg = null;
            try {
                msg = in.readLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (msg == null) {
                break;
            } else {
                receiver.receiveString(msg);

                Message message = Message.obtain();
                message.obj = receiver.UIMessage();
                message.setTarget(handler);
                message.sendToTarget();
            }
        }
    }

    public void logOut() {
        try {
            socket.close();
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
        // When there is message, execute this method
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String message = (String) msg.obj;
            // Update UI
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
            // String host = "10.0.2.2";
            // String host2 = "127.0.0.1";
            String host3 = "192.168.0.106";
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