package com.yasya.androidchat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatActivity extends Activity {

    public ListView msgView;
    public ArrayAdapter<String> msgList;
    Socket socket;
    NetworkTask networktask;
    FragListView fragList;
    Receiver receiver;
    boolean connected;

    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        receiver = new Receiver();
        fragList = new FragListView();

        setOrientation();

        FragmentManager fMan = getFragmentManager();
        FragmentTransaction ft = fMan.beginTransaction();
        ft.add(R.id.frame, fragList);
        ft.commit();

        msgView = (ListView) findViewById(R.id.listView);
        msgList = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);
        msgView.setAdapter(msgList);

        networktask = new NetworkTask();
        networktask.execute();

        Button btnSend = (Button) findViewById(R.id.btn_Send);

        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final EditText txtEdit = (EditText) findViewById(R.id.txt_inputText);
                receiver.receiveMessage(txtEdit.getText().toString());
                sendMessageToServer(receiver.outMessage());
                msgView.smoothScrollToPosition(msgList.getCount() - 1);

            }
        });

    }

    private void checkConnectivity() {
        //todo check for internet connection and connected = true
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_exit:
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
                receiver.receiveLogin(login);
                sendLoginToServer();
            }
        });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.setTitle("Enter Login");

        builder.setView(setLogin);

        AlertDialog dialog = builder.create();
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
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            try {
                msg = in.readLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (msg == null) {
                break;
            } else {
                /*
				 * Message message = Message.obtain(); // Creates an new Message
				 * // instance message.obj = msg; // Put the string into
				 * Message, into "obj" // field. message.setTarget(handler); //
				 * Set the Handler message.sendToTarget();
				 */
                receiver.receiveString(msg);
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                msgList.add(receiver.UIMessage());
                msgView.setAdapter(msgList);
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

	/*
	 * private Handler handler = new Handler() {
	 * 
	 * @Override // When there is message, execute this method public void
	 * handleMessage(Message msg) { super.handleMessage(msg); String message =
	 * (String) msg.obj; // Update UI msgList.add(message);
	 * msgView.setAdapter(msgList);
	 * msgView.smoothScrollToPosition(msgList.getCount() - 1); } };
	 */

    public class NetworkTask extends AsyncTask<Void, Void, Void> {
        // Activity activity = new ChatActivity();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) { // This runs on a

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