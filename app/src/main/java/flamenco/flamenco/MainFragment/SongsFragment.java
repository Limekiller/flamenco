package flamenco.flamenco.MainFragment;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import flamenco.flamenco.ListMusic;
import flamenco.flamenco.R;
import flamenco.flamenco.Song;

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
        updateCurrentSong(false);
        return  view;
    }


    public void updateCurrentSong(final Boolean selected) {

        int index = songView.getFirstVisiblePosition();
        View v = songView.getChildAt(0);
        int top = (v == null) ? 0 : v.getTop();

        songView.setAdapter(new SongAdapter(getActivity(), songList, "song") {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View row = super.getView(position, convertView, parent);

                if (songList.get(position) == ((ListMusic) getActivity()).getCurrSong()) {
                    if (selected) {
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