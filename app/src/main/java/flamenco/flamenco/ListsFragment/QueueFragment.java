package flamenco.flamenco.ListsFragment;

import android.animation.ObjectAnimator;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ListView;

import java.util.ArrayList;

import flamenco.flamenco.MusicActivity;
import flamenco.flamenco.MainFragment.SongAdapter;
import flamenco.flamenco.R;
import flamenco.flamenco.Song;

public class QueueFragment extends Fragment{

    private ArrayList<Song> songList;
    private ListView songView;
    private int origSize;
    public MusicActivity musicActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View view =  inflater.inflate(R.layout.queuefragment, container, false);
        songView = view.findViewById(R.id.queueList);
        musicActivity = (MusicActivity) getActivity();
        songList = musicActivity.songList;

        return  view;
    }

    public ArrayList<Song> getList() {
        return songList;
    }

    public void refreshQueue() {

        if (songList.size() == 0) {
            return;
        }

        origSize = -1;
        songList = new ArrayList<>(musicActivity.getServiceList());
        ArrayList<Song> songsToRemove = new ArrayList<>();
        Song currSong = musicActivity.getCurrSong();

        for (flamenco.flamenco.Song dog : songList) {
            origSize++;
            if (dog.getTitle().equals(currSong.getTitle()) && dog.getArtist().equals(currSong.getArtist())) {
                break;
            }
            songsToRemove.add(dog);
        }
        songList.removeAll(songsToRemove);
        updateCurrentSong();
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

                if ((((MusicActivity)getActivity()).getCurrSongPosn() == position ||
                        ((MusicActivity)getActivity()).getCurrSongPosn() == position + origSize) &&
                        ((MusicActivity) getActivity()).getCurrSong().getTitle().equals(
                                songList.get(position).getTitle())) {
                    ObjectAnimator animation = ObjectAnimator.ofFloat(bar,
                            "scaleX", 0, 1);
                    animation.setDuration(200);
                    animation.setInterpolator(new AccelerateInterpolator(2));
                    animation.start();
                    bg.animate().alpha(1).setDuration(200);
                }  else if ( ((MusicActivity) getActivity()).lastChosenSong != null &&
                        ((MusicActivity) getActivity()).lastChosenSong.getTitle().equals(songList.get(position).getTitle())){
                        //&& ((MusicActivity) getActivity()).lastChosenSong.getArtist().equals(songList.get(position).getArtist())) {
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