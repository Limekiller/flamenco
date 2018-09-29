package flamenco.flamenco;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ListMusic extends AppCompatActivity {

    private ArrayList<Song> songList;
    private ListView songView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_music);
        songView = (ListView)findViewById(R.id.song_list);
        songList = new ArrayList<Song>();

        ActivityCompat.requestPermissions(ListMusic.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted and now can proceed
                    getSongList();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(ListMusic.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // add other cases for more permissions
        }
    }

    public void getSongList() {
        ContentResolver musicResolver = getContentResolver();
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST);
            String coverPath = musicCursor
                    .getString(musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
            do {
                //SOMEHOW USE MEDIA METADATA RETRIEVER TO GET SHIT
                Drawable thisCover = Drawable.createFromPath(coverPath);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                long thisId = musicCursor.getLong(idColumn);
                songList.add(new Song(thisId, thisTitle, thisArtist, thisCover));
            }
            while (musicCursor.moveToNext());
        }

        Collections.sort(songList, new Comparator<Song>() {
            @Override
            public int compare(Song o1, Song o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });

        SongAdapter songAdt = new SongAdapter(this, songList);
        songView.setAdapter(songAdt);
    }
}
