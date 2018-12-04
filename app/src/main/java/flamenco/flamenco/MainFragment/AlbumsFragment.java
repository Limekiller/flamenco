package flamenco.flamenco.MainFragment;

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
import flamenco.flamenco.R;
import flamenco.flamenco.Song;
import flamenco.flamenco.animations;

public class AlbumsFragment extends Fragment {

    private ArrayList<Song> albumList;
    private GridView songView;
    public ListMusic listMusic;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.albumsfragment, container, false);
        final View albumFocus = view.findViewById(R.id.albumFocus);

        albumList = new ArrayList<Song>();
        songView = view.findViewById(R.id.album_list);
        listMusic = (ListMusic) getActivity();

        albumList = listMusic.albumList;
        AlbumAdapter songAdt = new AlbumAdapter(getActivity(), albumList, "albums");
        songView.setAdapter(songAdt);

        final GestureDetector gesture = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onDown(MotionEvent e) {
                        view.findViewById(R.id.imageView3).animate().scaleY(3f).setDuration(200);
                        return true;
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                           float velocityY) {

                        if (e1.getY() - e2.getY() > 0) {
                            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
                            float height = displayMetrics.heightPixels;

                            ObjectAnimator animation = ObjectAnimator.ofFloat(albumFocus,
                                    "translationY", 0, -70);
                            animation.setDuration(300);
                            animation.start();

                            animation = ObjectAnimator.ofFloat(view.findViewById(R.id.album_list),
                                    "translationY", height, 0);
                            animation.setDuration(300);
                            animation.start();

                            animation = ObjectAnimator.ofFloat(view.findViewById(R.id.a_song_list),
                                    "translationY", 0, -150);
                            animation.setDuration(300);
                            animation.start();
                        }

                        return false;
                    }
                });

        view.findViewById(R.id.imageView3).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    view.findViewById(R.id.imageView3).animate().scaleY(1f).setDuration(200);
                }
                return gesture.onTouchEvent(event);
            }
        });

        return view;
    }
}