package flamenco.flamenco.MainFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

import flamenco.flamenco.ListMusic;
import flamenco.flamenco.MainFragment.AlbumAdapter;
import flamenco.flamenco.R;
import flamenco.flamenco.Song;
import flamenco.flamenco.animations;

public class ArtistsFragment extends Fragment {

    private ArrayList<Song> artistList;
    private GridView songView;
    public ListMusic listMusic;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.artistsfragment, container, false);
        artistList = new ArrayList<Song>();
        songView = view.findViewById(R.id.artist_list);
        listMusic = (ListMusic) getActivity();

        artistList = listMusic.artistList;

        final GestureDetector gesture = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onDown(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                           float velocityY) {

                        if (e1.getY() - e2.getY() > 0) {

                            if (view.findViewById(R.id.album_list).getVisibility() == View.VISIBLE) {
                                animations.hideViewUp(view.findViewById(R.id.album_list),
                                        view.getContext());
                                view.findViewById(R.id.album_list).setVisibility(View.GONE);
                                view.findViewById(R.id.artist_list).setVisibility(View.VISIBLE);
                                animations.showViewUp(view.findViewById(R.id.artist_list), view.getContext());

                            } else {
                                view.findViewById(R.id.album_list).setVisibility(View.VISIBLE);
                                animations.showViewUp(view.findViewById(R.id.album_list), view.getContext());
                                view.findViewById(R.id.album_list).setVisibility(View.VISIBLE);
                                animations.hideViewUp(view.findViewById(R.id.albumFocus),
                                        view.getContext());
                                view.findViewById(R.id.albumFocus).setVisibility(View.GONE);
                            }

                        } else {
                            if (view.findViewById(R.id.album_list).getVisibility() != View.VISIBLE) {
                                view.findViewById(R.id.artist_list).setVisibility(View.VISIBLE);
                                animations.showViewDown(view.findViewById(R.id.artist_list), view.getContext());
                                animations.hideViewDown(view.findViewById(R.id.albumFocus),
                                        view.getContext());
                                view.findViewById(R.id.albumFocus).setVisibility(View.GONE);
                            }
                        }
                        return false;
                    }
                });

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        });

        AlbumAdapter songAdt = new AlbumAdapter(getActivity(), artistList, "artist");
        songView.setAdapter(songAdt);
        return view;
    }
}