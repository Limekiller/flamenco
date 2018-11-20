package flamenco.flamenco.MainFragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
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

                        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
                        float height = displayMetrics.heightPixels;
                        if (e1.getY() - e2.getY() > 0) {

                            if (view.findViewById(R.id.album_list).getAlpha() == 1f) {
                                ObjectAnimator animation2 = ObjectAnimator.ofFloat(view.findViewById(R.id.album_list_container),
                                        "translationY", 0, -height);
                                animation2.setDuration(200);
                                animation2.start();
                                view.findViewById(R.id.album_list).setAlpha(0.99f);

                                animation2 = ObjectAnimator.ofFloat(view.findViewById(R.id.artist_list),
                                        "translationY", height, 0);
                                animation2.setDuration(200);
                                animation2.start();
                                view.findViewById(R.id.artist_list).setAlpha(1f);

                            } else {
                                ObjectAnimator animation = ObjectAnimator.ofFloat(view.findViewById(R.id.album_list_container),
                                        "translationY", height, 0);
                                animation.setDuration(200);
                                animation.start();
                                view.findViewById(R.id.album_list).setAlpha(1f);

                                animation = ObjectAnimator.ofFloat(view.findViewById(R.id.albumFocus),
                                        "translationY", 0, -height);
                                animation.setDuration(200);
                                animation.start();
                                view.findViewById(R.id.albumFocus).setAlpha(0.99f);
                            }

                        } else {
                            if (view.findViewById(R.id.album_list).getAlpha() != 1f) {
                                ObjectAnimator animation = ObjectAnimator.ofFloat(view.findViewById(R.id.artist_list),
                                        "translationY", -height, 0);
                                animation.setDuration(200);
                                animation.start();
                                view.findViewById(R.id.artist_list).setAlpha(1f);

                                animation = ObjectAnimator.ofFloat(view.findViewById(R.id.albumFocus),
                                        "translationY", 0, height);
                                animation.setDuration(200);
                                animation.start();
                                view.findViewById(R.id.albumFocus).setAlpha(0.99f);
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