package com.yasya.androidchat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListFragment;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class FragListView extends ListFragment {
    Receiver receiver;
   
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fraglistview, null);
        receiver = new Receiver();
        return view;
    }

   @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayList<String> user = receiver.UIList();
        
        ListAdapter adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.item, user);

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