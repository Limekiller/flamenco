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
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;

import flamenco.flamenco.MusicActivity;
import flamenco.flamenco.R;
import flamenco.flamenco.Song;

public class AlbumsFragment extends Fragment {

    private ArrayList<Song> albumList;
    private GridView songView;
    public MusicActivity musicActivity;
    private View view;
    private ListView minorSongView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.albumsfragment, container, false);
        final View albumFocus = view.findViewById(R.id.albumFocus);

        albumList = new ArrayList<Song>();
        songView = view.findViewById(R.id.album_list);

        minorSongView = view.findViewById(R.id.a_song_list);
        musicActivity = (MusicActivity) getActivity();

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        final float deviceHeight = displayMetrics.heightPixels;

        albumList = musicActivity.albumList;
        AlbumAdapter songAdt = new AlbumAdapter(getActivity(), albumList, "albums");
        songView.setAdapter(songAdt);
        view.findViewById(R.id.arrow_up).animate().translationY(deviceHeight).setDuration(200);

        final GestureDetector gesture = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onDown(MotionEvent e) {
                        view.findViewById(R.id.imageView3).animate().scaleY(3f).setDuration(200);
                        view.findViewById(R.id.arrow_up).animate().translationY(deviceHeight-1000).setDuration(225).setInterpolator(new OvershootInterpolator(0.75f));
                        return true;
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                           float velocityY) {

                        if (e1.getY() - e2.getY() > 0) {
                            ObjectAnimator animation = ObjectAnimator.ofFloat(view.findViewById(R.id.album_list),
                                    "translationY", deviceHeight, 0);
                            animation.setInterpolator(new DecelerateInterpolator(3));
                            animation.setDuration(300);
                            animation.start();

                            animation = ObjectAnimator.ofFloat(view.findViewById(R.id.albumFocus),
                                    "translationY", 0, -70);
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
                    view.findViewById(R.id.arrow_up).animate().translationY(deviceHeight).setDuration(300).setInterpolator(new AccelerateInterpolator(3));
                }
                return gesture.onTouchEvent(event);
            }
        });

        return view;
    }

}