package flamenco.flamenco;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
                        return true;
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                           float velocityY) {

                        if (e1.getY() - e2.getY() > 0) {
                            animations.hideViewUp(albumFocus, view.getContext());
                            albumFocus.setVisibility(View.GONE);
                            view.findViewById(R.id.album_list).setVisibility(View.VISIBLE);
                            animations.showViewUp(view.findViewById(R.id.album_list),
                                    view.getContext());
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

        return view;
    }
}