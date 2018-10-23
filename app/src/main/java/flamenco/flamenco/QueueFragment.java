package flamenco.flamenco;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
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
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.security.auth.callback.Callback;

public class QueueFragment extends Fragment{

    private ArrayList<Song> songList;
    private ListView songView;
    public ListMusic listMusic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View view =  inflater.inflate(R.layout.queuefragment, container, false);
        songView = view.findViewById(R.id.queueList);
        listMusic = (ListMusic) getActivity();
        songList = listMusic.songList;

        return  view;
    }

    public void refreshQueue() {
        songList = listMusic.getServiceList();
        updateCurrentSong(false);
    }

    public void updateCurrentSong(final boolean selected) {

        int index = songView.getFirstVisiblePosition();
        View v = songView.getChildAt(0);
        int top = (v == null) ? 0 : v.getTop();

        songView.setAdapter(new SongAdapter(getActivity(), songList, "song") {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View row = super.getView(position, convertView, parent);

                if(position == ((ListMusic) getActivity()).getCurrSongPosn()) {
                    if (selected == true) {
                        ObjectAnimator.ofObject(row, "backgroundColor", new ArgbEvaluator(),
                                Color.WHITE, getResources().getColor(R.color.colorAccentLight))
                                .setDuration(150).start();
                    } else {
                        row.setBackgroundColor (getResources().getColor(R.color.colorAccentLight));
                    }
                }
                else {
                    row.setBackgroundColor (Color.WHITE);
                }
                return row;
            }
        });

        songView.setSelectionFromTop(index, top);
    }

}