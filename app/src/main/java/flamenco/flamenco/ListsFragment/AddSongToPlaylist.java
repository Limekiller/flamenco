package flamenco.flamenco.ListsFragment;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import flamenco.flamenco.MainFragment.SongAdapter;
import flamenco.flamenco.R;
import flamenco.flamenco.Song;

public class AddSongToPlaylist extends AppCompatActivity {

    Song chosenPlaylist;
    ArrayList<Song> songList;
    SongAdapter adapter;
    ListView add_song_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_song_to_playlist);
        add_song_list = findViewById(R.id.add_song_list);
        EditText inputSearch = findViewById(R.id.inputSearch);

        Bundle extra = getIntent().getBundleExtra("extra");
        songList = (ArrayList<Song>) extra.getSerializable("songs");
        chosenPlaylist = (Song) getIntent().getSerializableExtra("extra2");
        adapter = new SongAdapter(this, songList, "song");
        add_song_list.setAdapter(adapter);

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }


    public void songPicked(View view) {
        int pos = Integer.parseInt(view.getTag().toString());
        //chosenPlaylist.add(songList.get(pos));
        //chosenPlaylist.getAlbumSongList().add(songList.get(pos));

        chosenPlaylist.getAlbumSongList().add(adapter.getSongs().get(pos));
        Intent resultData = new Intent();
        Bundle extra = new Bundle();
        extra.putSerializable("playlist", chosenPlaylist);
        resultData.putExtra("extra", extra);
        setResult(Activity.RESULT_OK, resultData);
        finish();
    }
}
