package flamenco.flamenco;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AlbumsFragment extends Fragment {

    private ArrayList<Album> albumList;
    private GridView songView;
    public ListMusic listMusic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.albumsfragment, container, false);
        albumList = new ArrayList<Album>();
        songView = view.findViewById(R.id.album_list);
        listMusic = (ListMusic) getActivity();

        albumList = listMusic.albumList;
        AlbumAdapter songAdt = new AlbumAdapter(getActivity(), albumList);
        songView.setAdapter(songAdt);
        return view;
    }

}