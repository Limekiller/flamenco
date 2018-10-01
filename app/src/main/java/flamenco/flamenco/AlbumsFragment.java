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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.albumsfragment, container, false);
        albumList = new ArrayList<Album>();
        songView = view.findViewById(R.id.album_list);
        getAlbumList();
        return view;
    }

    public void getAlbumList() {
        ContentResolver musicResolver = (getActivity().getContentResolver());
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Albums.ALBUM);
            int artistColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Albums.ARTIST);
            int yearColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Albums.FIRST_YEAR);
            int artColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Albums.ALBUM_ART);
            int isMusic = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.IS_MUSIC);
            do {


                String thisYear = null;
                String thisArt = null;
                if (yearColumn > -1) {
                    thisYear = musicCursor.getString(yearColumn);
                }
                if (artColumn > -1) {
                    thisArt = musicCursor.getString(artColumn);
                }


                int thisIsMusic = musicCursor.getInt(isMusic);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                if (thisIsMusic > 0) {
                    if (musicCursor.getPosition() == 0) {
                        albumList.add(new Album(thisTitle, thisArtist, thisArt, thisYear));
                    } else if (!albumList.get(albumList.size()-1).getTitle().equals(thisTitle)) {
                        albumList.add(new Album(thisTitle, thisArtist, thisArt, thisYear));
                    }
                }
            }
            while (musicCursor.moveToNext());
        }
        musicCursor.close();

        Collections.sort(albumList, new Comparator<Album>() {
            @Override
            public int compare(Album o1, Album o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });

        AlbumAdapter songAdt = new AlbumAdapter(getActivity(), albumList);
        songView.setAdapter(songAdt);
    }

}