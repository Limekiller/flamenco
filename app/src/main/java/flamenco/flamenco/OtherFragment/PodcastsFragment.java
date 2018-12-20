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

    private ArrayList<Song> podcastSeriesList = new ArrayList<>();
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.podcastsfragment, container, false);
        getSongList();
        return view;

    }

    public void getSongList() {

        ContentResolver musicResolver = getActivity().getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Albums.ALBUM);
            int artistColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Albums.ARTIST);
            int isPodcast = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.IS_PODCAST);
            int idColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);
            int albumIdColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM_ID);

            do {

                int thisIsPodcast = musicCursor.getInt(isPodcast);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                long thisId = musicCursor.getLong(idColumn);
                long thisAlbumId = musicCursor.getLong(albumIdColumn);
                Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
                Uri albumArt = ContentUris.withAppendedId(sArtworkUri, thisAlbumId);

                if (thisIsPodcast > 0) {

                    Song tempArtist = new Song(thisId, thisArtist, thisArtist, thisAlbumId, null, albumArt.toString());
                    tempArtist.setAlbumSongList(new ArrayList<Song>());
                    boolean artistFound = false;
                    for (int i=0;i<podcastSeriesList.size();i++) {
                        if (podcastSeriesList.get(i).getArtist().equals(tempArtist.getArtist())) {
                            artistFound = true;
                            podcastSeriesList.get(i).getAlbumSongList().add(new Song(thisId, thisTitle, thisArtist, thisAlbumId, null, albumArt.toString()));
                        }
                    }
                    if (!artistFound) {
                        tempArtist.getAlbumSongList().add(new Song(thisId, thisTitle, thisArtist, thisAlbumId, null, albumArt.toString()));
                        podcastSeriesList.add(tempArtist);
                    }
                }
            }
            while (musicCursor.moveToNext());
        }
        musicCursor.close();

        Collections.sort(podcastSeriesList, new Comparator<Song>() {
            @Override
            public int compare(flamenco.flamenco.Song o1, flamenco.flamenco.Song o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });


        // Initiate UI after cataloguing songs
        final ListView podList = view.findViewById(R.id.Podcast_List);
        final AlbumAdapter adapter = new AlbumAdapter(getContext(), podcastSeriesList, "album");
        podList.setAdapter(adapter);
    }
}
