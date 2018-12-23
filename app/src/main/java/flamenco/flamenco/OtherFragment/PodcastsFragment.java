package flamenco.flamenco.OtherFragment;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import flamenco.flamenco.Folder;
import flamenco.flamenco.MainFragment.AlbumAdapter;
import flamenco.flamenco.MainFragment.MainFragmentAdapter;
import flamenco.flamenco.MusicActivity;
import flamenco.flamenco.R;
import flamenco.flamenco.Song;

public class PodcastsFragment extends Fragment {

    public MusicActivity musicActivity;
    private ArrayList<Song>  podcastList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.podcastsfragment, container, false);
        musicActivity = (MusicActivity) getActivity();
        podcastList = musicActivity.podcastList;

        final GridView podList = view.findViewById(R.id.Podcast_List);
        final AlbumAdapter adapter = new AlbumAdapter(getContext(), podcastList, "album");
        podList.setAdapter(adapter);
        return view;

    }


}
