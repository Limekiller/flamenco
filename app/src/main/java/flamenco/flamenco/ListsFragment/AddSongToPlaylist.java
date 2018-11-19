package flamenco.flamenco.ListsFragment;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import flamenco.flamenco.ListMusic;
import flamenco.flamenco.MainFragment.SongAdapter;
import flamenco.flamenco.R;
import flamenco.flamenco.Song;

public class AddSongToPlaylist extends AppCompatActivity {

    Song chosenPlaylist;
    ArrayList<Song> songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_song_to_playlist);
        ListView add_song_list = findViewById(R.id.add_song_list);

        Bundle extra = getIntent().getBundleExtra("extra");

        songList = (ArrayList<Song>) extra.getSerializable("songs");
        chosenPlaylist = (Song) getIntent().getSerializableExtra("extra2");

        add_song_list.setAdapter(new SongAdapter(this, songList, "song"));
    }

    public void songPicked(View view) {
        int pos = Integer.parseInt(view.getTag().toString());
        //chosenPlaylist.add(songList.get(pos));
        chosenPlaylist.getAlbumSongList().add(songList.get(pos));
        Intent resultData = new Intent();
        Bundle extra = new Bundle();
        extra.putSerializable("playlist", chosenPlaylist);
        resultData.putExtra("extra", extra);
        setResult(Activity.RESULT_OK, resultData);
        finish();
    }
}
