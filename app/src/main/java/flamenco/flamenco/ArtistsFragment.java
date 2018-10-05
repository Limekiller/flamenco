package flamenco.flamenco;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

public class ArtistsFragment extends Fragment {

    private ArrayList<Song> artistList;
    private GridView songView;
    public ListMusic listMusic;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.artistsfragment, container, false);
        artistList = new ArrayList<Song>();
        songView = view.findViewById(R.id.artist_list);
        listMusic = (ListMusic) getActivity();

        artistList = listMusic.artistList;
        AlbumAdapter songAdt = new AlbumAdapter(getActivity(), artistList, "artist");
        songView.setAdapter(songAdt);
        return view;
    }
}