package flamenco.flamenco;

import android.app.Activity;
import android.content.Context;
import android.net.sip.SipSession;
import android.provider.SyncStateContract;
import android.support.v4.app.Fragment;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import javax.security.auth.callback.Callback;

public class SongsFragment extends Fragment{

    private ArrayList<Song> songList;
    private ListView songView;
    public ListMusic listMusic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.songsfragment, container, false);
        songView = view.findViewById(R.id.song_list);
        songList = new ArrayList<Song>();
        listMusic = (ListMusic) getActivity();
        getSongList();
        return  view;
    }


    public void getSongList() {
        ContentResolver musicResolver = (getActivity().getContentResolver());
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST);
            int yearColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.YEAR);
            int albumIdColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM_ID);
            do {
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisYear = musicCursor.getString(yearColumn);
                long thisId = musicCursor.getLong(idColumn);
                long thisAlbumId = musicCursor.getLong(albumIdColumn);
                songList.add(new Song(thisId, thisTitle, thisArtist, thisAlbumId, thisYear));
            }
            while (musicCursor.moveToNext());
        }
        musicCursor.close();

        Collections.sort(songList, new Comparator<Song>() {
            @Override
            public int compare(Song o1, Song o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });


        SongAdapter songAdt = new SongAdapter(getActivity(), songList);
        songView.setAdapter(songAdt);
        listMusic.songList = songList;
    }

}