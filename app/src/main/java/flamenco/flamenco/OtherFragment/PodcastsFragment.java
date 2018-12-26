package flamenco.flamenco.OtherFragment;

import android.animation.ObjectAnimator;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import flamenco.flamenco.Folder;
import flamenco.flamenco.MainFragment.AlbumAdapter;
import flamenco.flamenco.MainFragment.FoldersAdapter;
import flamenco.flamenco.MainFragment.MainFragmentAdapter;
import flamenco.flamenco.MusicActivity;
import flamenco.flamenco.R;
import flamenco.flamenco.Song;

public class PodcastsFragment extends Fragment {

    public MusicActivity musicActivity;
    private ArrayList<Song>  podcastList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.podcastsfragment, container, false);
        musicActivity = (MusicActivity) getActivity();
        podcastList = musicActivity.podcastList;

        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        final float deviceHeight = displayMetrics.heightPixels;

        final GridView podList = view.findViewById(R.id.Podcast_List);
        final AlbumAdapter adapter = new AlbumAdapter(getContext(), podcastList, "artist");
        podList.setAdapter(adapter);

        view.findViewById(R.id.arrow).animate().translationY(deviceHeight).setDuration(200);
        final GestureDetector gesture = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onDown(MotionEvent e) {
                        view.findViewById(R.id.imageView36).animate().scaleY(3f).setDuration(200);
                        view.findViewById(R.id.arrow).animate().translationY(deviceHeight-1000).setDuration(225).setInterpolator(new OvershootInterpolator(0.75f));
                        return true;
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                           float velocityY) {

                        if (e1.getY() - e2.getY() > 0) {
                            ObjectAnimator animation = ObjectAnimator.ofFloat(view.findViewById(R.id.Podcast_List),
                                    "translationY", deviceHeight, 0);
                            animation.setInterpolator(new DecelerateInterpolator(3));
                            animation.setDuration(300);
                            animation.start();

                            animation = ObjectAnimator.ofFloat(view.findViewById(R.id.podcastFocus),
                                    "translationY", 0, -70);
                            animation.setDuration(300);
                            animation.start();

                        }

                        return false;
                    }

                });

        view.findViewById(R.id.imageView36).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    view.findViewById(R.id.imageView36).animate().scaleY(1f).setDuration(200);
                    view.findViewById(R.id.arrow).animate().translationY(deviceHeight).setDuration(300).setInterpolator(new AccelerateInterpolator(3));
                }
                return gesture.onTouchEvent(event);
            }
        });

        return view;
    }


}
