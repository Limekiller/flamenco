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
    private ArrayList<Song> albumSongList;
    private GridView songView;
    public ListMusic listMusic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.albumsfragment, container, false);
        albumList = new ArrayList<Album>();
        albumSongList = new ArrayList<Song>();
        songView = view.findViewById(R.id.album_list);
        listMusic = (ListMusic) getActivity();
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
                    (MediaStore.Audio.Media.YEAR);
            int artColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Albums.ALBUM_ART);
            int isMusic = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.IS_MUSIC);
            int songTitleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);
            int albumIdColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM_ID);
            do {

                String thisArt = null;
                if (artColumn > -1) {
                    thisArt = musicCursor.getString(artColumn);
                }

                String thisYear = musicCursor.getString(yearColumn);
                int thisIsMusic = musicCursor.getInt(isMusic);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisSongTitle = musicCursor.getString(songTitleColumn);
                long thisId = musicCursor.getLong(idColumn);
                long thisAlbumId = musicCursor.getLong(albumIdColumn);

                if (thisIsMusic > 0) {
                    if (albumList.size() == 0) {
                        albumList.add(new Album(thisTitle, thisArtist, thisArt, thisYear));
                    } else if (!albumList.get(albumList.size()-1).getTitle().equals(thisTitle)) {
                        albumList.get(albumList.size() -1).setAlbumSongList(albumSongList);
                        albumList.add(new Album(thisTitle, thisArtist, thisArt, thisYear));
                        albumSongList.clear();
                    }
                    albumSongList.add(new Song(thisId, thisSongTitle, thisArtist, thisAlbumId, thisYear));

                }
            }
            while (musicCursor.moveToNext());
        }
        albumList.get(albumList.size() -1).setAlbumSongList(albumSongList);
        musicCursor.close();

        Collections.sort(albumList, new Comparator<Album>() {
            @Override
            public int compare(Album o1, Album o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });

        AlbumAdapter songAdt = new AlbumAdapter(getActivity(), albumList);
        songView.setAdapter(songAdt);
        listMusic.albumList = albumList;

    }

}