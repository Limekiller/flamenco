package flamenco.flamenco.MainFragment;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import flamenco.flamenco.ListMusic;
import flamenco.flamenco.R;
import flamenco.flamenco.Song;

public class SongsFragment extends Fragment{

    private ArrayList<Song> songList;
    private ListView songView;
    public ListMusic listMusic;
    private boolean isReady = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.songsfragment, container, false);
        songView = view.findViewById(R.id.song_list);
        listMusic = (ListMusic) getActivity();
        songList = listMusic.songList;

        SongAdapter songAdt = new SongAdapter(getActivity(), songList, "song");
        updateCurrentSong();
        return  view;
    }

    public void updateCurrentSong() {

        int index = songView.getFirstVisiblePosition();
        View v = songView.getChildAt(0);
        int top = (v == null) ? 0 : v.getTop();


        songView.setAdapter(new SongAdapter(getActivity(), songList, "song") {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View row = super.getView(position, convertView, parent);
                View bar = row.findViewById(R.id.imageView6);
                View bg = row.findViewById(R.id.selectedBG);

                if (songList.get(position).getTitle().equals(((ListMusic) getActivity()).getCurrSong().getTitle())
                        && songList.get(position).getArtist().equals(((ListMusic) getActivity()).getCurrSong().getArtist())) {
                    ObjectAnimator animation = ObjectAnimator.ofFloat(bar,
                            "scaleX", 0, 1);
                    animation.setDuration(200);
                    animation.setInterpolator(new AccelerateInterpolator(2));
                    animation.start();
                    bg.animate().alpha(1).setDuration(200);
                }  else if ( ((ListMusic) getActivity()).lastChosenSong != null &&
                        ((ListMusic) getActivity()).lastChosenSong.getTitle().equals(songList.get(position).getTitle())
                            && ((ListMusic) getActivity()).lastChosenSong.getArtist().equals(songList.get(position).getArtist())) {
                    bar.setScaleX(1);
                    ObjectAnimator animation = ObjectAnimator.ofFloat(bar,
                            "scaleX", 1, 0);
                    animation.setDuration(200);
                    animation.setInterpolator(new AccelerateInterpolator(2));
                    animation.start();
                    bg.animate().alpha(0).setDuration(200);

                } else {
                    bar.setScaleX(0);
                }

                return row;
            }
        });

        songView.setSelectionFromTop(index, top);
    }

}