package flamenco.flamenco;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.sip.SipSession;
import android.provider.SyncStateContract;
import android.support.v4.app.Fragment;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.security.auth.callback.Callback;

public class SongsFragment extends Fragment{

    private ArrayList<Song> songList;
    private ListView songView;
    public ListMusic listMusic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.songsfragment, container, false);
        songView = view.findViewById(R.id.song_list);
        listMusic = (ListMusic) getActivity();
        songList = listMusic.songList;

        SongAdapter songAdt = new SongAdapter(getActivity(), songList, "song");
        //songView.setAdapter(songAdt);
        songView.setAdapter(new SongAdapter(getActivity(), songList, "song") {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View row = super.getView(position, convertView, parent);

                if(position == ((ListMusic) getActivity()).getCurrSong()) {
                    row.setBackgroundColor (Color.RED);
                }
                else {
                    row.setBackgroundColor (Color.WHITE);
                }
                return row;
            }
        });
        return  view;
    }




    public void updateCurrentSong(Integer pos, Integer oldPos) {

    }

}