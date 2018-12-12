package flamenco.flamenco;

import android.Manifest;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
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
import android.view.MotionEvent;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;

import flamenco.flamenco.ListsFragment.AddSongToPlaylist;
import flamenco.flamenco.ListsFragment.QueueFragment;
import flamenco.flamenco.MainFragment.AlbumAdapter;
import flamenco.flamenco.MainFragment.FoldersAdapter;
import flamenco.flamenco.MainFragment.MainFragmentAdapter;
import flamenco.flamenco.MainFragment.SongAdapter;
import flamenco.flamenco.MainFragment.SongsFragment;
import flamenco.flamenco.MusicService.MusicBinder;
import android.widget.MediaController.MediaPlayerControl;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ListMusic extends AppCompatActivity implements MediaPlayerControl, OnGestureListener{

    public ArrayList<flamenco.flamenco.Song> songList;
    public ArrayList<flamenco.flamenco.Song> artistList;
    public ArrayList<flamenco.flamenco.Song> albumList;
    public ArrayList<flamenco.flamenco.Folder> folderList;
    public ArrayList<Song> playList;
    public Folder lastFolder;
    private ArrayList<Song> shuffledList;
    private MusicService musicSrv;
    private Intent playIntent;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private boolean musicBound=false;
    private boolean isShuffled = false;

    private Song lastChosenPlaylist;
    public int lastPlaylistIndex;
    public Song lastChosenSong;

    private LinearLayout audioController;
    private ImageView currSongArt;
    private TextView currSongInfo;
    private SeekBar seekBar;
    private TextView currTime;
    private ImageButton playBtn;
    private Handler handler;
    private float deviceHeight;
    private float deviceWidth;
    private ImageButton fullscreenPlay;
    private ImageView fullscreenArt;
    GestureDetector gestureDetector;
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    Fragment activityListMusic = new Fragment();


    /*public boolean onTouchEvent(MotionEvent event){
        int action = MotionEventCompat.getActionMasked(event);

        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
                //playBtn.setImageResource(R.drawable.exo_controls_play);
                return true;
            case (MotionEvent.ACTION_MOVE) :
                //playBtn.setImageResource(R.drawable.exo_controls_play);
                return true;
            case (MotionEvent.ACTION_UP) :
                //playBtn.setImageResource(R.drawable.exo_controls_play);
                return true;
            case (MotionEvent.ACTION_CANCEL) :
                return true;
            case (MotionEvent.ACTION_OUTSIDE) :
                //playBtn.setImageResource(R.drawable.exo_controls_play);
                return true;
            default :
                return super.onTouchEvent(event);
        }
    }*/

    private boolean hasUpdated=false;
    private boolean paused=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentTransaction.add(R.id.container, activityListMusic);
        setContentView(R.layout.activity_list_music);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefsEditor = prefs.edit();
        ActivityCompat.requestPermissions(ListMusic.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

        shuffledList = new ArrayList<>();
        songList = new ArrayList<>();
        artistList = new ArrayList<>();
        albumList = new ArrayList<>();
        folderList = new ArrayList<>();
        currSongArt = findViewById(R.id.currSongArt);
        currSongInfo = findViewById(R.id.currSongInfo);
        seekBar = findViewById(R.id.seekBar);
        ImageButton rewindBtn = findViewById(R.id.rewindBtn);
        playBtn = findViewById(R.id.playBtn);
        ImageButton ffBtn = findViewById(R.id.ffBtn);
        ImageButton shuffBtn = findViewById(R.id.shuffButton);
        currTime = findViewById(R.id.currTime);
        handler = new Handler();
        audioController = findViewById(R.id.audioController);
        gestureDetector = new GestureDetector(ListMusic.this, ListMusic.this);


        // Load saved playlists as well as last saved shuffle.
        // If null, create new, empty list of playlists
        Gson gson = new Gson();
        String json = gson.toJson(shuffledList);

        Type type = new TypeToken<ArrayList<Song>>() {
        }.getType();
        playList = gson.fromJson(json, type);
        String response = prefs.getString("playList", "");
        playList = gson.fromJson(response, new TypeToken<ArrayList<Song>>() {
        }.getType());
        if (playList == null) {
            playList = new ArrayList<>();
        }

        // Detect taps for play, rewind, fast forward, and shuffle buttons
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!musicSrv.isPng() && musicSrv.requestAudioFocus()) {
                    musicSrv.go();
                    playBtn.setImageResource(R.drawable.exo_controls_pause);
                } else {
                    musicSrv.pausePlayer();
                    playBtn.setImageResource(R.drawable.exo_controls_play);
                }
            }
        });

        rewindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastChosenSong = musicSrv.getSong();
                musicSrv.playPrev();
                playBtn.setImageResource(R.drawable.exo_controls_pause);
                updateSong();
            }
        });

        ffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastChosenSong = musicSrv.getSong();
                musicSrv.playNext();
                playBtn.setImageResource(R.drawable.exo_controls_pause);
                updateSong();
            }
        });

        shuffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // If not shuffled, attempt to load last shuffle
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

                    // If null, create new shuffle
                    int savedIndex = prefs.getInt("shuffleIndex", 0);
                    if (shuffledList == null || shuffledList.size() == 0) {
                        Toast.makeText(ListMusic.this, "New shuffle created from current queue", Toast.LENGTH_LONG).show();
                        shuffledList = new ArrayList<>(musicSrv.getList());
                        Collections.shuffle(shuffledList);

                        for (int i = 0; i < shuffledList.size(); i++) {
                            if (shuffledList.get(i) == getCurrSong()) {
                                shuffledList.add(0, getCurrSong());
                                shuffledList.remove(i);
                            }
                        }

                        // Save new shuffle
                        json = gson.toJson(shuffledList);
                        prefsEditor.remove("shuffledList").apply();
                        prefsEditor.putString("shuffledList", json);
                        prefsEditor.commit();
                    } else {
                        Toast.makeText(ListMusic.this, "Previous shuffle loaded", Toast.LENGTH_LONG).show();
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

                    // Set the playling list to the shuffle, and refresh the queue fragment.
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
            // Get width and height variables, and begin music service
            DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
            deviceHeight = displayMetrics.heightPixels;
            deviceWidth = displayMetrics.widthPixels;
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    // This method receives data from the "add song to playlist" activity and updates the lists
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Bundle result = data.getBundleExtra("extra");
                lastChosenPlaylist = (Song)result.getSerializable("playlist");
                playList.set(lastPlaylistIndex, lastChosenPlaylist);
                ListView lv = findViewById(R.id.specPlaylist);
                SongAdapter songAdt = new SongAdapter(lv.getContext(), lastChosenPlaylist.getAlbumSongList(), "song");
                lv.setAdapter(songAdt);
                savePlaylistList();

            }
        }
    }



    // This method handles moving from one song to the next automatically.
    public void updateSong() {
        hasUpdated = true;
        flamenco.flamenco.Song currSong = musicSrv.getSong();
        currSongInfo.setText(currSong.getArtist()+" — "+currSong.getTitle());
        Glide.with(this).load(currSong.getAlbumArt()).error(R.drawable.placeholder)
                .crossFade().centerCrop().into(currSongArt);

        // If the music is shuffled, it pops the first song from the playing list
        // (So the shuffle gets smaller as it is listened to)
        if (isShuffled) {
            prefsEditor.remove("shuffleIndex");
            prefsEditor.putInt("shuffleIndex", getCurrSongPosn());
            prefsEditor.commit();
        }

        int mediaPos = musicSrv.getPosn();
        int mediaMax = musicSrv.getDur();

        seekBar.setMax(mediaMax);
        seekBar.setProgress(mediaPos);

        updateCurrSong();

        handler.removeCallbacks(updateTime);
        handler.postDelayed(updateTime, 100);
    }


    // Refresh the queue fragment list (this only affects the UI)
    public void refreshQueue(){
        QueueFragment queueFragment = (QueueFragment) getSupportFragmentManager().getFragments()
                .get(1).getChildFragmentManager().getFragments().get(1);
        queueFragment.refreshQueue();
    }

    // Get the list that the queue fragment is displaying
    public ArrayList<Song> getQueueList(){
        QueueFragment queueFragment = (QueueFragment) getSupportFragmentManager().getFragments()
                .get(1).getChildFragmentManager().getFragments().get(1);
        return queueFragment.getList();
    }


    // This method handles moving from one song to another based on user input
    public void songPicked(View view){
        lastChosenSong = musicSrv.getSong();
        Integer pos;
        // Check which list choice is coming from

        // If from artists fragment, check if it's an album or a song
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
        // If from song fragment
        } else if (((ViewGroup)view.getParent()).getId() == R.id.song_list) {
            musicSrv.setList(songList);
        // if from folder fragment
        } else if (((ViewGroup)view.getParent()).getId() == R.id.f_folder_list) {
            musicSrv.setList(lastFolder.getSongList());
        // if from playlists fragment
        } else if (((ViewGroup)view.getParent()).getId() == R.id.specPlaylist) {
            musicSrv.setList(playList.get(lastPlaylistIndex).getAlbumSongList());
        }



        // Show audio controller (only affects on first pick)
        audioController.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        audioController.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 450, 0.2f));

        // In the queue, we only show the current list starting from the playing song.
        // So here we get that sublist by popping everything from the front of the currently playing
        // list until we reach the currently playing song.
        // The indices don't change, though, so for song selection on the queue fragment to work,
        // we need to add the number of removed songs to the current position
        pos = Integer.parseInt(view.getTag().toString());

        if (((ViewGroup)view.getParent()).getId() == R.id.queueList) {
            ArrayList<Song> queueList = getQueueList();
            int listDiff = musicSrv.getList().size() - queueList.size();
            pos = pos + listDiff;
        }
        
        musicSrv.setSong(pos);
        musicSrv.playSong();
        updateSong();

        if (((ViewGroup)view.getParent()).getId() != R.id.queueList) {
            isShuffled = false;
        }

    }

    public void clearShuffle(View view) {
        // Delete saved shuffle and refresh queue
        Toast.makeText(ListMusic.this, "Shuffle deleted", Toast.LENGTH_LONG).show();
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


    public void savePlaylistList() {
        // Commit all of the playlists to storage
        Gson gson = new Gson();
        String json = gson.toJson(playList);

        prefsEditor.remove("playList").apply();
        prefsEditor.putString("playList", json);
        prefsEditor.commit();
    }

    public void updateCurrSong() {
        // This method is used to display the currently playing song (the red bar and grey background)
        // Right now it only works on the Song and Queue fragments. In the future, duplicated methods
        // for the other fragments will need to be added to support the other fragments.
        List<Fragment> allFragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment: allFragments) {
            for (final Fragment child: fragment.getChildFragmentManager().getFragments()) {
                if (child instanceof SongsFragment) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((SongsFragment) child).updateCurrentSong();
                        }
                    }, 350);
                }
                if (child instanceof  QueueFragment) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((QueueFragment) child).updateCurrentSong();
                        }
                    }, 350);                }
            }
        }
    }


    // This method shows the Album Focus page when selecting an album
    public void setAlbumVisibility(View view) {
        RelativeLayout albumParent;
        Boolean artistsTab = false;
        TextView title = view.findViewById(R.id.album_title);
        ArrayList<flamenco.flamenco.Song> tempList;
        String albumTitle = (String)title.getText();

        // Determine if this is the artists or album fragment
        if (view.getParent().getParent() == view.getRootView().findViewById(R.id.album_list_container)) {
            albumParent = (RelativeLayout) view.getParent().getParent().getParent();
            artistsTab = true;
        } else {
            albumParent = (RelativeLayout) view.getParent().getParent();
        }

        ListView tempAlbumList = albumParent.findViewById(R.id.a_song_list);

        // Search for and find the desired list of songs
        tempList = albumList.get(0).getAlbumSongList();
        for (flamenco.flamenco.Song dog : albumList) {
            if (dog.getTitle().equals(albumTitle)) {
                tempList = dog.getAlbumSongList();
                break;
            }
        }

        // Load in the data and begin animations
        ((TextView)albumParent.findViewById(R.id.albumFocusTitle)).setText(albumTitle);
        ((TextView)albumParent.findViewById(R.id.albumFocusArtist)).setText(tempList.get(0).getArtist());
        Glide.with(albumParent.getContext()).load(tempList.get(0).getAlbumArt())
                .error(R.drawable.placeholder).crossFade().centerCrop()
                .into((ImageView) albumParent.findViewById(R.id.albumFocusImage));

        ObjectAnimator animation;
        if (artistsTab) {
            animation = ObjectAnimator.ofFloat(albumParent.findViewById(R.id.album_list_container),
                    "translationY", 0, deviceHeight);
        } else {
            animation = ObjectAnimator.ofFloat(albumParent.findViewById(R.id.album_list),
                    "translationY", 0, deviceHeight);
        }
        animation.setDuration(300);
        animation.setStartDelay(225);
        animation.setInterpolator(new AccelerateInterpolator(2));
        animation.start();
        albumParent.findViewById(R.id.album_list).setAlpha(0.99f);

        tempAlbumList.setTag(view.getTag());

        animation = ObjectAnimator.ofFloat(albumParent.findViewById(R.id.albumFocus),
                "translationY", -70, 0).setDuration(225);
        animation.setDuration(300);
        animation.setStartDelay(225);
        animation.start();

        albumParent.findViewById(R.id.albumFocus).setAlpha(1f);

        // Adapt list data to ListView
        SongAdapter songAdt = new SongAdapter(albumParent.getContext(), tempList, "album");
        tempAlbumList.setAdapter(songAdt);

    }

    // This method shows the songs in a playlist
    public void setPlaylistVisibility(final View view) {
        TextView title = view.findViewById(R.id.playlist_title);
        ArrayList<flamenco.flamenco.Song> tempList;
        String playlistTitle = (String)title.getText();
        RelativeLayout playlistParent = (RelativeLayout) view.getParent().getParent().getParent().getParent();
        ListView tempPlayList = playlistParent.findViewById(R.id.specPlaylist);

        // Search for and retrieve the right song list
        tempList = playList.get(0).getAlbumSongList();
        for (flamenco.flamenco.Song dog : playList) {
            if (dog.getTitle().equals(playlistTitle)) {
                lastPlaylistIndex = playList.indexOf(dog);
                lastChosenPlaylist = dog;
                tempList = dog.getAlbumSongList();
                break;
            }
        }

        // Load in data, do animations, adapt list to listview
        ObjectAnimator animation = ObjectAnimator.ofFloat(playlistParent.findViewById(R.id.playlist_init),
                "translationY", 0, deviceHeight);
        animation.setDuration(300);
        animation.setStartDelay(225);
        animation.setInterpolator(new AccelerateInterpolator(2));
        animation.start();

        animation = ObjectAnimator.ofFloat(playlistParent.findViewById(R.id.playlistFocus),
                "translationY", -70, 0);
        animation.setDuration(225);
        animation.setStartDelay(225);
        animation.start();

        ((FloatingActionButton)view.getRootView().findViewById(R.id.addNew)).hide();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((FloatingActionButton)view.getRootView().findViewById(R.id.floatingActionButton2)).show();
            }
        }, 550);

        SongAdapter songAdt = new SongAdapter(playlistParent.getContext(), tempList, "song");
        tempPlayList.setAdapter(songAdt);

    }

    // Again, this is for folders
    public void setFolderVisibility(View view) {
        ArrayList<Folder> tempFolderList = new ArrayList<>();
        ArrayList<Folder> tempSearchFolderList;
        ArrayList<Song> tempSongList = new ArrayList<>();
        String path = (String)((TextView)view.findViewById(R.id.folder_path)).getText();
        View parentView = view.getRootView();
        ListView tempFolderView = parentView.findViewById(R.id.f_folder_list);

        // Check if this is a root folder or not
        if (lastFolder == null) {
            tempSearchFolderList = folderList;

            ObjectAnimator animation = ObjectAnimator.ofFloat(parentView.findViewById(R.id.folder_list),
                    "translationY", 0, deviceHeight);
            animation.setDuration(300);
            animation.setStartDelay(225);
            animation.setInterpolator(new AccelerateInterpolator(2));
            animation.start();

            animation = ObjectAnimator.ofFloat(parentView.findViewById(R.id.folderFocus),
                    "translationY", -70, 0);
            animation.setDuration(225);
            animation.setStartDelay(225);
            animation.start();


        } else {
            tempSearchFolderList = lastFolder.getFolderList();

            ObjectAnimator animation = ObjectAnimator.ofFloat(parentView.findViewById(R.id.folderFocus),
                    "translationY", -deviceHeight, 0);
            animation.setDuration(225);
            animation.setStartDelay(225);
            animation.setInterpolator(new AccelerateInterpolator(3));
            animation.start();

        }

        // Find the correct folder in the last opened folder or the root directory
        // (Here the root directory is the initial list of folders)
        for (Folder folder : tempSearchFolderList) {
            if (folder.getPath().equals(path)) {
                tempFolderList = folder.getFolderList();
                tempSongList = folder.getSongList();
                lastFolder = folder;
                break;
            }
        }

        FoldersAdapter foldersAdapter = new FoldersAdapter(view.getContext(), tempFolderList, tempSongList);
        tempFolderView.setAdapter(foldersAdapter);

    }

    // This method launches the AddSongToPlaylist activity, and provides it with the total list of songs
    // as well as the playlist that is being added to
    public void addSongToPlaylist(View view) {
        Intent intent = new Intent(this, AddSongToPlaylist.class);

        Bundle extra = new Bundle();
        extra.putSerializable("songs", songList);
        intent.putExtra("extra", extra);

        intent.putExtra("extra2", lastChosenPlaylist);
        startActivityForResult(intent,1);
    }

    // This method shows the albums belonging to an artist
    // This should make sense by now hopefully
    public void showAlbums(View view) {
        ArrayList<flamenco.flamenco.Song> tempAlbumList = new ArrayList<>();
        TextView title = view.findViewById(R.id.album_artist);
        String artistName = (String) title.getText();
        final View parentView = (View) view.getParent().getParent();

        for (flamenco.flamenco.Song dog : albumList) {
            if (dog.getArtist().equals(artistName)) {
                tempAlbumList.add(dog);
            }
        }

        ObjectAnimator animation = ObjectAnimator.ofFloat(parentView.findViewById(R.id.artist_list),
                "translationY", 0, deviceHeight);
        animation.setDuration(300);
        animation.setInterpolator(new AccelerateInterpolator(2));
        animation.start();
        parentView.findViewById(R.id.artist_list).setAlpha(0.99f);

        animation = ObjectAnimator.ofFloat(parentView.findViewById(R.id.album_list_container),
                "translationY", -70, 0);
        animation.setDuration(225);
        animation.start();
        parentView.findViewById(R.id.album_list).setAlpha(1f);

        GridView albumView = parentView.findViewById(R.id.album_list);
        AlbumAdapter songAdt = new AlbumAdapter(view.getContext(), tempAlbumList, "albums");
        albumView.setAdapter(songAdt);
    }


    // This connects this activity to the music service
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
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
                    Toast.makeText(ListMusic.this, "Permission denied to read your External storage",
                            Toast.LENGTH_SHORT).show();
                }
            }
            // add other cases for more permissions
        }
    }


    // Some of these may be able to be cleaned up/removed?
    @Override
    public void start() {
        musicSrv.go();
    }

    @Override
    public void pause() {
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
            int songPos = musicSrv.getPosn();
            int songDur = musicSrv.getDur();
            seekBar.setMax(songDur);
            seekBar.setProgress(songPos);
            currTime.setText("" + milliSecondsToTimer(songPos));

            handler.postDelayed(this, 100);
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

        // If seconds = 0, we're on a new song, so we should update the UI to reflect that
        if (seconds == 0) {
            // We use the hasUpdated variable to make sure we only update once
            if (!hasUpdated) {
                hasUpdated = true;
                lastChosenSong = musicSrv.getSong();
                updateSong();
            }
            if (musicSrv.isPng()) {
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

    // Not sure if this method is needed
    public Integer getCurrSongPosn() {
        try {
            return musicSrv.getSongPosn();
        } catch (NullPointerException e) {
            return 0;
        }
    }

    // Get the currently playing song
    public Song getCurrSong() {
        try {
            return musicSrv.getSong();
        } catch (NullPointerException e) {
            return null;
        }
    }

    // Recursive method that assists in building the folder relations when the app is started
    // It takes a song, a parent folder, and a path, and figures out where to place the song
    public boolean addFolders(Folder folder, Song song, String path) {
        // Works by splitting the path by slashes, and then building it back up, checking each time
        // if the string is equal to the parent's path. If it is, and it's only one folder behind
        // the current path, it adds the song to the parent folder. If not, that means there's more
        // folders in the way, so it keeps adding folders and checking the path until it can add
        // the song.
        String[] splitPath = path.split("/");
        StringBuilder buildPath = new StringBuilder("/"+splitPath[0]);
        for (int i = 1; i < splitPath.length; i++) {
            buildPath.append(splitPath[i]).append('/');
            if (buildPath.toString().equals(folder.getPath())) {
                if (i == splitPath.length-1) {
                    folder.addToSongList(song);
                    return true;
                } else {
                    buildPath.append(splitPath[i+1]).append('/');
                    for (Folder subFolder : folder.getFolderList()) {
                        if (subFolder.getPath().equals(buildPath.toString())) {
                            return addFolders(subFolder, song, path);
                        }
                    }
                    Folder newFolder = new Folder(path, new ArrayList<Song>(), new ArrayList<Folder>(), folder);
                    newFolder.addToSongList(song);
                    folder.addToFolderList(newFolder);
                    return true;
                }
            }
        }
        return false;

    }

    // This method runs on startup, constructing a list of songs, artists, and albums
    public void getSongList() {
        // Pretty complicated, basically uses Android's built in method to find all audio files known
        // to be music, and then extracts the info from the files
        // The Song class is then used to create structures of artists, albums, and folders
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
            int pathColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DATA);
            do {

                String thisYear = musicCursor.getString(yearColumn);
                int thisIsMusic = musicCursor.getInt(isMusic);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisSongTitle = musicCursor.getString(songTitleColumn);
                String thisPath = musicCursor.getString(pathColumn);
                long thisId = musicCursor.getLong(idColumn);
                long thisAlbumId = musicCursor.getLong(albumIdColumn);
                Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
                Uri albumArt = ContentUris.withAppendedId(sArtworkUri, thisAlbumId);

                if (thisIsMusic > 0) {

                    String path = thisPath.substring(0, thisPath.lastIndexOf("/"));

                    // Use addFolders method to add the song to a folder
                    boolean hasBeenAdded = false;
                    for (int i=0; i<folderList.size(); i++) {
                        if (addFolders(folderList.get(i), new Song(thisId, thisSongTitle, thisArtist, thisAlbumId, thisYear, albumArt.toString()), path+"/")) {
                            hasBeenAdded = true;
                            break;
                        }
                    }
                    // If it failed for some reason, just add a new folder and put the song in it.
                    if (!hasBeenAdded) {
                        folderList.add(new Folder(path+"/", new ArrayList<Song>(), new ArrayList<Folder>(), null));
                        folderList.get(folderList.size()-1).addToSongList(new Song(thisId, thisSongTitle, thisArtist, thisAlbumId, thisYear, albumArt.toString()));
                    }

                    // If album or artist don't exist yet, make them
                    // Otherwise, find them in their respective lists and add the song to them
                    if (albumList.size() == 0) {
                        albumList.add(new flamenco.flamenco.Song(thisId, thisTitle, thisArtist, thisAlbumId, thisYear, albumArt.toString()));
                        artistList.add(new flamenco.flamenco.Song(thisId, thisSongTitle, thisArtist, thisAlbumId, thisYear, albumArt.toString()));
                    } else if (!albumList.get(albumList.size()-1).getTitle().equals(thisTitle)) {
                        albumList.get(albumList.size() -1).setAlbumSongList(albumSongList);

                        Song tempArtist = new Song(thisId, thisArtist, thisArtist, thisAlbumId, thisYear, albumArt.toString());
                        boolean artistFound = false;
                        for (int i=0;i<artistList.size();i++) {
                            if (artistList.get(i).getArtist().equals(tempArtist.getArtist())) {
                                artistFound = true;
                            }
                        }
                        if (!artistFound) {
                            artistList.add(tempArtist);
                        }

                        albumList.add(new flamenco.flamenco.Song(thisId, thisTitle, thisArtist, thisAlbumId, thisYear, albumArt.toString()));
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
        assert musicCursor != null;
        musicCursor.close();

        // Sort the songs, albums, and artists alphabetically
        //TODO: Do not treat songs starting with "The, A," etc, as if they begin with T or A, etc.
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

        // Initiate UI after cataloguing songs
        final ViewPager viewPager = findViewById(R.id.pager);
        final MainFragmentAdapter adapter = new MainFragmentAdapter(
                getSupportFragmentManager(), 2);
        viewPager.setAdapter(adapter);
    }

    // detect swipes up
    // now can load fullscreen player and revert to the activity_list_music
    // but it doesn't show the player at the bottom, even though music is still playing.
    // This code is really a band aid right now, until I can figure out fragment transactions.
    @Override
    public boolean onFling(MotionEvent motionEvent1, MotionEvent motionEvent2, float x, float y){
        //playBtn.setImageResource(R.drawable.exo_controls_play);
        float factor = 8;
        if(motionEvent1.getY() - motionEvent2.getY() > 20){
            // need to load the fullscreen player xml here, and make it functional. Also need
            // to make sure that it only works when you swipe from the music controller at the
            // bottom of the screen.
            /* Song currSong = musicSrv.getSong();
            setContentView(R.layout.fullscreen_player);
            fullscreenArt = findViewById(R.id.fullscreen_art);
            fullscreenPlay = findViewById(R.id.fullscreen_play);
            Glide.with(this).load(currSong.getAlbumArt()).error(R.drawable.placeholder)
                    .crossFade().centerCrop().into(fullscreenArt);
            fullscreenPlay.setImageResource(R.drawable.exo_controls_play);*/
//            ObjectAnimator expansion = ObjectAnimator.ofFloat(R.id.audioController, "scaleX", "scaleY", deviceHeight);
//            expansion.setDuration(500);
//            expansion.start();
            animations.expandObject(findViewById(R.id.audioController), 1f, factor);
            animations.translateObject(findViewById(R.id.audioController), 0f, -deviceHeight/3f);

            animations.expandObject(findViewById(R.id.seekBar), 1f, 1/factor);

            animations.expandObject(findViewById(R.id.ffBtn), 1f, 1/factor);
            animations.expandObject(findViewById(R.id.playBtn), 1f, 1/factor);
            animations.expandObject(findViewById(R.id.rewindBtn), 1f, 1/factor);
            animations.expandObject(findViewById(R.id.shuffButton), 1f, 1/factor);
            animations.expandObject(findViewById(R.id.currSongInfo), 1f, 1/factor);
            animations.expandObject(findViewById(R.id.currTime), 1f, 1/factor);
            animations.expandObject(findViewById(R.id.currSongArt), 4f, 4/factor);

            animations.translateObject(findViewById(R.id.currSongArt), deviceWidth/2f - 100f, -100f);
            animations.translateObject(findViewById(R.id.currSongInfo), 0f, deviceHeight/30f);
            animations.translateObject(findViewById(R.id.pager), 0f, deviceHeight);

            return true;
        }
        else if (motionEvent2.getY() - motionEvent1.getY() > 20) {
            // animate the player back to small
            animations.contractObject(findViewById(R.id.audioController), 1f, factor);
            animations.translateObject(findViewById(R.id.audioController), 0f, 0f);

            animations.contractObject(findViewById(R.id.seekBar), 1f, 1/factor);
            animations.contractObject(findViewById(R.id.ffBtn), 1f, 1/factor);
            animations.contractObject(findViewById(R.id.playBtn), 1f, 1/factor);
            animations.contractObject(findViewById(R.id.rewindBtn), 1f, 1/factor);
            animations.contractObject(findViewById(R.id.shuffButton), 1f, 1/factor);
            animations.contractObject(findViewById(R.id.currSongInfo), 1f, 1/factor);
            animations.contractObject(findViewById(R.id.currTime), 1f, 1/factor);
            animations.contractObject(findViewById(R.id.currSongArt), 4f, 4/factor);

            animations.translateObject(findViewById(R.id.currSongArt), 0f, 0f);
            animations.translateObject(findViewById(R.id.currSongInfo), 0f, 0f);
            animations.translateObject(findViewById(R.id.pager), 0f, 0f);
        }
        return false;
    }

    // overriding the rest of the methods in GestureDetector so that the program doesn't break
    @Override
    public void onLongPress(MotionEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onShowPress(MotionEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean onSingleTapUp(MotionEvent arg0) {
        //playBtn.setImageResource(R.drawable.exo_controls_play);
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        // TODO Auto-generated method stub
        return gestureDetector.onTouchEvent(motionEvent);
    }

    @Override
    public boolean onDown(MotionEvent arg0) {
        // TODO Auto-generated method stub
        return false;
    }

}
