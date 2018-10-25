package flamenco.flamenco;

import android.Manifest;
import android.animation.LayoutTransition;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.IBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.View;
import flamenco.flamenco.MusicService.MusicBinder;
import android.widget.MediaController.MediaPlayerControl;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public class ListMusic extends AppCompatActivity implements MediaPlayerControl{

    public ArrayList<flamenco.flamenco.Song> songList;
    public ArrayList<flamenco.flamenco.Song> artistList;
    public ArrayList<flamenco.flamenco.Song> albumList;
    private ArrayList<Song> shuffledList;
    private MusicService musicSrv;
    private Intent playIntent;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private boolean musicBound=false;
    private boolean isShuffled = false;

    private LinearLayout audioController;
    private ImageView currSongArt;
    private TextView currSongInfo;
    private SeekBar seekBar;
    private TextView currTime;
    private ImageButton rewindBtn;
    private ImageButton playBtn;
    private ImageButton shuffBtn;
    private ImageButton ffBtn;
    private Handler handler;
    private ImageLoader imageLoader;

    private boolean hasUpdated=false;
    private boolean paused=false, playbackPaused=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_music);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefsEditor = prefs.edit();
        ActivityCompat.requestPermissions(ListMusic.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

        // Wait for list view fragments to populate
        // TODO: Continue as soon as views are ready rather than waiting an arbitrary amount of time
        try {
            TimeUnit.MILLISECONDS.sleep(1000 );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        imageLoader = ImageLoader.getInstance();
        shuffledList = new ArrayList<>();
        songList = new ArrayList<>();
        artistList = new ArrayList<>();
        albumList = new ArrayList<>();
        currSongArt = findViewById(R.id.currSongArt);
        currSongInfo = findViewById(R.id.currSongInfo);
        seekBar = findViewById(R.id.seekBar);
        rewindBtn = findViewById(R.id.rewindBtn);
        playBtn = findViewById(R.id.playBtn);
        ffBtn = findViewById(R.id.ffBtn);
        shuffBtn = findViewById(R.id.shuffButton);
        currTime = findViewById(R.id.currTime);
        handler = new Handler();
        audioController = findViewById(R.id.audioController);

        // Instantiate parent fragments
        final ViewPager viewPager = findViewById(R.id.pager);
        final flamenco.flamenco.MainFragmentAdapter adapter = new flamenco.flamenco.MainFragmentAdapter(
                getSupportFragmentManager(), 2);
        viewPager.setAdapter(adapter);

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playbackPaused) {
                    musicSrv.go();
                    playBtn.setImageResource(R.drawable.exo_controls_pause);
                    playbackPaused = false;
                } else {
                    musicSrv.pausePlayer();
                    playbackPaused = true;
                    playBtn.setImageResource(R.drawable.exo_controls_play);

                }
                updateSong(false);
            }
        });

        rewindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicSrv.playPrev();
                playBtn.setImageResource(R.drawable.exo_controls_pause);
                playbackPaused = false;
                updateSong(true);
            }
        });

        ffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicSrv.playNext();
                playBtn.setImageResource(R.drawable.exo_controls_pause);
                playbackPaused = false;
                updateSong(true);
            }
        });

        shuffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isShuffled) {
                    isShuffled = true;
                    Gson gson = new Gson();
                    String json = gson.toJson(shuffledList);
                    Type type = new TypeToken<ArrayList<Song>>() {
                    }.getType();
                    shuffledList = gson.fromJson(json, type);
                    String response = prefs.getString("shuffledList", "");
                    shuffledList = gson.fromJson(response, new TypeToken<ArrayList<Song>>() {
                    }.getType());

                    int savedIndex = prefs.getInt("shuffleIndex", 0);
                    if (shuffledList == null || shuffledList.size() == 0) {
                        shuffledList = new ArrayList<>(musicSrv.getList());
                        Collections.shuffle(shuffledList);

                        for (int i = 0; i < shuffledList.size(); i++) {
                            if (shuffledList.get(i) == getCurrSong()) {
                                shuffledList.add(0, getCurrSong());
                                shuffledList.remove(i);
                            }
                        }

                        json = gson.toJson(shuffledList);
                        prefsEditor.remove("shuffledList").apply();
                        prefsEditor.putString("shuffledList", json);
                        prefsEditor.commit();
                    } else {
                        int i = 0;
                        while (i < savedIndex) {
                            shuffledList.remove(0);
                            i++;
                        }

                        shuffledList.add(0, musicSrv.getSong());
                        json = gson.toJson(shuffledList);
                        prefsEditor.remove("shuffledList").apply();
                        prefsEditor.putString("shuffledList", json);
                        prefsEditor.commit();
                    }

                    musicSrv.setList(shuffledList);
                    musicSrv.setSong(0);
                    refreshQueue();
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(musicSrv != null && fromUser) {
                    musicSrv.seek(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }



    // This method handles moving from one song to the next automatically.
    public void updateSong(boolean selected) {
        flamenco.flamenco.Song currSong = musicSrv.getSong();
        currSongInfo.setText(currSong.getArtist()+" — "+currSong.getTitle());
        updateCurrSong(selected);
        Glide.with(this).load(currSong.getAlbumArt()).error(R.drawable.placeholder)
                .crossFade().centerCrop().into(currSongArt);

        if (isShuffled) {
            prefsEditor.remove("shuffleIndex");
            prefsEditor.putInt("shuffleIndex", getCurrSongPosn());
            prefsEditor.commit();
        }

        int mediaPos = musicSrv.getPosn();
        int mediaMax = musicSrv.getDur();

        seekBar.setMax(mediaMax);
        seekBar.setProgress(mediaPos);

        handler.removeCallbacks(updateTime);
        handler.postDelayed(updateTime, 100);
    }


    public void refreshQueue(){
        QueueFragment queueFragment = (QueueFragment) getSupportFragmentManager().getFragments()
                .get(1).getChildFragmentManager().getFragments().get(1);
        queueFragment.refreshQueue();
    }


    // This method handles moving from one song to another based on user input
    public void songPicked(View view){

        Integer pos;
        //Integer oldPos = musicSrv.getSongPosn();

        // Check which list choice is coming from
        if (((ViewGroup)view.getParent()).getId() == R.id.a_song_list) {
            if (((ViewGroup)view.getParent().getParent().getParent()).getId() == R.id.artistView) {
                String albumTitle = (String) ((TextView)((ViewGroup)view.getParent().getParent())
                        .findViewById(R.id.albumFocusTitle)).getText();
                for (flamenco.flamenco.Song dog : albumList) {
                    if (dog.getTitle().equals(albumTitle)) {
                        musicSrv.setList(dog.getAlbumSongList());
                        break;
                    }
                }
            } else {
                musicSrv.setList(albumList.get(Integer.parseInt(((ListView) view.getParent()).getTag().toString()))
                        .getAlbumSongList());
            }
        } else if (((ViewGroup)view.getParent()).getId() == R.id.song_list) {
            musicSrv.setList(songList);
        }

        if (((ViewGroup)view.getParent()).getId() != R.id.queueList) {
            isShuffled = false;
            refreshQueue();
        }

        // Show audio controller (only affects on first pick)
        audioController.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        audioController.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.06f));

        pos = Integer.parseInt(view.getTag().toString());
        musicSrv.setSong(pos);
        musicSrv.playSong();
        updateSong(true);

        if(playbackPaused){
            playbackPaused=false;
        }

    }

    public void clearShuffle(View view) {
        shuffledList.clear();
        musicSrv.setList(songList);
        isShuffled = false;

        Gson gson = new Gson();
        String json = gson.toJson(shuffledList);

        prefsEditor.putInt("shuffleIndex", 0);
        prefsEditor.remove("shuffledList").apply();
        prefsEditor.putString("shuffledList", json);
        prefsEditor.commit();

        refreshQueue();
    }


    public void updateCurrSong(boolean selected) {
        List<Fragment> allFragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment: allFragments) {
            for (Fragment child: fragment.getChildFragmentManager().getFragments()) {
                if (child instanceof  SongsFragment) {
                    ((SongsFragment) child).updateCurrentSong(selected);
                }
                if (child instanceof  QueueFragment) {
                    ((QueueFragment) child).updateCurrentSong(selected);
                }
            }
        }
    }


    // This method shows the Album Focus page when selecting an album
    public void setAlbumVisibility(View view) {
        TextView title = view.findViewById(R.id.album_title);
        ArrayList<flamenco.flamenco.Song> tempList;
        String albumTitle = (String)title.getText();
        RelativeLayout albumParent = (RelativeLayout) view.getParent().getParent();
        ListView tempAlbumList = albumParent.findViewById(R.id.a_song_list);

        tempList = albumList.get(0).getAlbumSongList();
        for (flamenco.flamenco.Song dog : albumList) {
            if (dog.getTitle().equals(albumTitle)) {
                tempList = dog.getAlbumSongList();
                break;
            }
        }

        ((TextView)albumParent.findViewById(R.id.albumFocusTitle)).setText(albumTitle);
        ((TextView)albumParent.findViewById(R.id.albumFocusArtist)).setText(tempList.get(0).getArtist());
        Glide.with(albumParent.getContext()).load(tempList.get(0).getAlbumArt())
                .error(R.drawable.placeholder).crossFade().centerCrop()
                .into((ImageView) albumParent.findViewById(R.id.albumFocusImage));

        animations.hideViewDown(albumParent.findViewById(R.id.album_list), this);
        albumParent.findViewById(R.id.album_list).setVisibility(View.GONE);
        tempAlbumList.setTag(view.getTag());
        albumParent.findViewById(R.id.albumFocus).setVisibility(View.VISIBLE);
        animations.showViewDown((albumParent.findViewById(R.id.albumFocus)), this);

        flamenco.flamenco.SongAdapter songAdt = new flamenco.flamenco.SongAdapter(albumParent.getContext(), tempList, "album");
        tempAlbumList.setAdapter(songAdt);

    }

    // This method shows the albums belonging to an artist
    public void showAlbums(View view) {
        ArrayList<flamenco.flamenco.Song> tempAlbumList = new ArrayList<>();
        TextView title = view.findViewById(R.id.album_artist);
        String artistName = (String) title.getText();
        View parentView = (View) view.getParent().getParent();

        for (flamenco.flamenco.Song dog : albumList) {
            if (dog.getArtist().equals(artistName)) {
                tempAlbumList.add(dog);
            }
        }

        animations.hideViewDown(parentView.findViewById(R.id.artist_list), this);
        parentView.findViewById(R.id.artist_list).setVisibility(View.GONE);
        parentView.findViewById(R.id.album_list).setVisibility(View.VISIBLE);
        animations.showViewDown(parentView.findViewById(R.id.album_list), this);
        GridView albumView = parentView.findViewById(R.id.album_list);

        flamenco.flamenco.AlbumAdapter songAdt = new flamenco.flamenco.AlbumAdapter(view.getContext(), tempAlbumList, "albums");
        albumView.setAdapter(songAdt);
    }


    private ServiceConnection musicConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicBinder binder = (MusicBinder)service;
            musicSrv = binder.getService();
            musicSrv.setList(songList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };


    // This method handles the storage permissions
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
            }
            // add other cases for more permissions
        }
    }


    @Override
    public void start() {
        musicSrv.go();
    }

    @Override
    public void pause() {
        playbackPaused=true;
        musicSrv.pausePlayer();
    }

    @Override
    public int getDuration() {
        if(musicSrv!=null && musicBound && musicSrv.isPng())
            return musicSrv.getDur();
        else return 0;
    }

    @Override
    public int getCurrentPosition() {
        if(musicSrv!=null && musicBound && musicSrv.isPng())
        return musicSrv.getPosn();
        else return 0;
    }

    @Override
    public void seekTo(int pos) {
        musicSrv.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        if(musicSrv!=null && musicBound)
        return musicSrv.isPng();
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    protected void onPause(){
        super.onPause();
        paused=true;
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(paused){
            //setController();
            paused=false;
        }
    }

    @Override
    protected void onStop() {
        //controller.hide();
        super.onStop();
    }


    // This method runs continually and updates the seekbar
    private Runnable updateTime = new Runnable() {
        @Override
        public void run() {
            if (!playbackPaused) {
                int songPos = musicSrv.getPosn();
                int songDur = musicSrv.getDur();
                seekBar.setMax(songDur);
                seekBar.setProgress(songPos);
                currTime.setText(""+milliSecondsToTimer(songPos));

                handler.postDelayed(this, 100);
            }
        }
    };


    // This method assists the above, converting song position to human readable form
    public String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString;

        int hours = (int)( milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
        if(hours > 0){
            finalTimerString = hours + ":";
        }

        if (seconds == 0 && !hasUpdated) {
            hasUpdated = true;
            updateSong(false);
            if (!playbackPaused) {
                playBtn.setImageResource(R.drawable.exo_controls_pause);
            }
        }

        if (seconds > 0) {
            hasUpdated = false;
        }

        if(seconds < 10){
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;}

        finalTimerString = finalTimerString + minutes + ":" + secondsString;
        return finalTimerString;
    }


    // This is a helper method for fragments that need to access the currently playing song list
    public ArrayList<Song> getServiceList() {
        return musicSrv.getList();
    }

    public Integer getCurrSongPosn() {
        try {
            return musicSrv.getSongPosn();
        } catch (NullPointerException e) {
            return 0;
        }
    }

    public Song getCurrSong() {
        try {
            return musicSrv.getSong();
        } catch (NullPointerException e) {
            return null;
        }
    }

    // This method runs on startup, constructing a list of songs, artists, and albums
    public void getSongList() {
        ArrayList<flamenco.flamenco.Song> albumSongList = new ArrayList<>();
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Albums.ALBUM);
            int artistColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Albums.ARTIST);
            int yearColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.YEAR);
            int isMusic = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.IS_MUSIC);
            int songTitleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);
            int albumIdColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM_ID);
            do {

                String thisYear = musicCursor.getString(yearColumn);
                int thisIsMusic = musicCursor.getInt(isMusic);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisSongTitle = musicCursor.getString(songTitleColumn);
                long thisId = musicCursor.getLong(idColumn);
                long thisAlbumId = musicCursor.getLong(albumIdColumn);
                Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
                Uri albumArt = ContentUris.withAppendedId(sArtworkUri, thisAlbumId);

                if (thisIsMusic > 0) {
                    if (albumList.size() == 0) {
                        albumList.add(new flamenco.flamenco.Song(thisId, thisTitle, thisArtist, thisAlbumId, thisYear, albumArt.toString()));
                        artistList.add(new flamenco.flamenco.Song(thisId, thisSongTitle, thisArtist, thisAlbumId, thisYear, albumArt.toString()));
                    } else if (!albumList.get(albumList.size()-1).getTitle().equals(thisTitle)) {
                        albumList.get(albumList.size() -1).setAlbumSongList(albumSongList);
                        albumList.add(new flamenco.flamenco.Song(thisId, thisTitle, thisArtist, thisAlbumId, thisYear, albumArt.toString()));
                        if (!artistList.get(artistList.size()-1).getArtist().equals(thisArtist)) {
                            artistList.add(new flamenco.flamenco.Song(thisId, thisSongTitle, thisArtist, thisAlbumId, thisYear, albumArt.toString()));
                        }
                        albumSongList.clear();
                    }
                    albumSongList.add(new flamenco.flamenco.Song(thisId, thisSongTitle, thisArtist, thisAlbumId, thisYear, albumArt.toString()));
                    songList.add(new flamenco.flamenco.Song(thisId, thisSongTitle, thisArtist, thisAlbumId, thisYear, albumArt.toString()));
                }
            }
            while (musicCursor.moveToNext());
        }
        try {
            albumList.get(albumList.size() - 1).setAlbumSongList(albumSongList);
        } catch (ArrayIndexOutOfBoundsException e) {
            Toast.makeText(ListMusic.this, "No audio found!", Toast.LENGTH_SHORT).show();
        }
        musicCursor.close();

        Collections.sort(songList, new Comparator<flamenco.flamenco.Song>() {
            @Override
            public int compare(flamenco.flamenco.Song o1, flamenco.flamenco.Song o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });
        Collections.sort(albumList, new Comparator<flamenco.flamenco.Song>() {
            @Override
            public int compare(flamenco.flamenco.Song o1, flamenco.flamenco.Song o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });
        Collections.sort(artistList, new Comparator<flamenco.flamenco.Song>() {
            @Override
            public int compare(flamenco.flamenco.Song o1, flamenco.flamenco.Song o2) {
                return o1.getArtist().compareTo(o2.getArtist());
            }
        });
    }

}
