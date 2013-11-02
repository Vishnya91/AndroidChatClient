package com.yasya.androidchat;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

import java.util.ArrayList;

public class FragListView extends ListFragment {
    Receiver receiver;
    private static FragListView instance;

    public FragListView() {
    }

    public static FragListView getInstance() {
        if (instance == null) {
            instance = new FragListView();
        }
        return instance;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fraglistview, null);
        receiver = Receiver.getInstance();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayList<String> user = receiver.UIList();
        Log.e("array from frag", user.toString());
        ListAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.item, user);
        setListAdapter(adapter);

    }
       /* mp = new MediaPlayer();
        audioList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> song;

        try {
            JSONArray jsonArray = res.getJSONArray("response");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jObject = jsonArray.getJSONObject(i);
                ID = String.valueOf(jObject.get("aid"));
                ARTIST = (String) jObject.get("artist");
                TITLE = (String) jObject.get("title");
                URL = (String) jObject.get("url");
                song = new HashMap<String, String>();
                song.put("aid", ID);
                song.put("artist", ARTIST);
                song.put("title", TITLE);
                song.put("url", URL);
                audioList.add(song);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ListAdapter adapter = new SimpleAdapter(getActivity(), audioList,
                R.layout.playlist_item, new String[]{"title",
                "artist"}, new int[]{R.id.title,
                R.id.artist});

        setListAdapter(adapter);
        // selecting single ListView item
        ListView lv = getListView();
        // listening to single listitem click
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                try {
                    mp.reset();
                    mp.setDataSource(audioList.get(position).get("url"));
                    mp.prepareAsync();
                    mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                        }
                    });


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }*/
}