package flamenco.flamenco;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import java.util.ArrayList;

import android.app.PendingIntent;
import android.content.ContentUris;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.gson.Gson;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

    private MediaPlayer player;
    private ArrayList<Song> songs;
    private int songPosn;
    private final IBinder musicBind = new MusicBinder();
    private AudioManager audioManager;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;


    public MusicService() {
    }

    private final BroadcastReceiver nReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra("action");
            switch (action) {
                case "pause":
                    if (isPng()) {
                        pausePlayer();
                    } else {
                        go();
                    }
                    break;
                case "next":
                    playNext();
                    break;
                default:
                    playPrev();
                    break;
            }
        }
    };

    public void onCreate(){
        registerReceiver(nReceiver, new IntentFilter("mediaControl"));

        createNotificationChannel();
        super.onCreate();
        songPosn = 0;
        player = new MediaPlayer();
        initMusicPlayer();
    }


    public void initMusicPlayer(){
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public ArrayList<Song> getList(){
        return songs;
    }


    public void setList(ArrayList<Song> theSongs){
        songs=theSongs;
    }

    @Override
    public void onAudioFocusChange(int focusState) {
        switch (focusState) {
            case AudioManager.AUDIOFOCUS_GAIN:
                if (player == null) initMusicPlayer();
                else if (!player.isPlaying()) player.start();
                player.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                if (player.isPlaying()) player.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:

                if (player.isPlaying()) player.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (player.isPlaying()) player.setVolume(0.1f, 0.1f);
                break;
        }
    }

    public boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //Focus gained
            return true;
        }
        //Could not gain focus
        return false;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                audioManager.abandonAudioFocus(this);
    }


    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }


    public void playSong(){

        if (requestAudioFocus()) {
            player.reset();

            Song playSong = songs.get(songPosn);
            long currSong = playSong.getID();
            Uri trackUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    currSong);

            try {
                player.setDataSource(getApplicationContext(), trackUri);
            } catch (Exception e) {
                Log.e("MUSIC SERVICE", "Error setting data source", e);
            }
            player.prepareAsync();
            Handler handler = new Handler();
            handler.removeCallbacks(savePos);
            handler.postDelayed(savePos, 1000);
        }
    }

    private Runnable savePos = new Runnable() {
        @Override
        public void run() {

            if (isPng()) {
                prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                prefsEditor = prefs.edit();

                Gson gson = new Gson();
                String json = gson.toJson(getList());

                prefsEditor.remove("currentList").apply();
                prefsEditor.putInt("currentTime", getPosn());
                prefsEditor.putInt("currentPos", getSongPosn());
                prefsEditor.putString("currentList", json);
                prefsEditor.commit();
            }

            Handler handler = new Handler();
            handler.postDelayed(this, 5000);
        }
    };

    public void setSong(int songIndex){
        songPosn=songIndex;
    }

    public Song getSong() {
        return songs.get(songPosn);
    }

    public Integer getSongPosn() {return songPosn;}


    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }


    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        if(player.getCurrentPosition()>0){
            mp.reset();playNext();

        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        NotificationManager notificationManager = ((NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE));
        notificationManager.cancelAll();
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }


    public void setUpNotification() {
        Intent pauseIntent = new Intent("mediaControl");
        pauseIntent.putExtra("action", "pause");
        PendingIntent pausePendingIntent = PendingIntent.getBroadcast(this, 0, pauseIntent, 0);

        Intent nextIntent = new Intent("mediaControl");
        nextIntent.putExtra("action", "next");
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this, 1, nextIntent, 0);

        Intent prevIntent = new Intent("mediaControl");
        prevIntent.putExtra("action", "prev");
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(this, 2, prevIntent, 0);

        Intent homeIntent = new Intent(this, MusicActivity.class);
        PendingIntent homePendingIntent = PendingIntent.getActivity(this, 3, homeIntent, 0);


        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification);
        contentView.setTextViewText(R.id.songTitle, getSong().getTitle());
        contentView.setTextViewText(R.id.songArtist, getSong().getArtist());
        contentView.setImageViewResource(R.id.nLast, R.drawable.exo_controls_previous);

        if (isPng()) {
            contentView.setImageViewResource(R.id.nPlay, R.drawable.exo_controls_play);
        } else {
            contentView.setImageViewResource(R.id.nPlay, R.drawable.exo_controls_pause);
        }

        contentView.setImageViewResource(R.id.nNext, R.drawable.exo_controls_next);
        contentView.setImageViewResource(R.id.songArt, R.drawable.ic_launcher_round);

        contentView.setOnClickPendingIntent(R.id.nLast, prevPendingIntent);
        contentView.setOnClickPendingIntent(R.id.nPlay, pausePendingIntent);
        contentView.setOnClickPendingIntent(R.id.nNext, nextPendingIntent);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "ID")
                .setSmallIcon(R.drawable.ic_stat_flamenco_logo_onecolor)
                .setContent(contentView)
                .setContentIntent(homePendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(1, mBuilder.build());


    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        setUpNotification();

        Intent onPreparedIntent = new Intent("MEDIA_PLAYER_PREPARED");
        LocalBroadcastManager.getInstance(this).sendBroadcast(onPreparedIntent);
        mp.start();
    }


    public int getPosn(){
        return player.getCurrentPosition();
    }


    public int getDur(){
        return player.getDuration();
    }


    public boolean isPng(){
        try {
            return player.isPlaying();
        } catch (NullPointerException e) {
            return false;
        }
    }


    public void pausePlayer(){
        setUpNotification();
        player.pause();
    }


    public void seek(int posn){
        player.seekTo(posn);
    }


    public void go(){
        setUpNotification();
        player.start();
    }


    public void playPrev(){
        songPosn--;
        if(songPosn < 0) songPosn=songs.size()-1;
        playSong();
    }


    public void playNext(){
        songPosn++;
        if(songPosn >= songs.size()) songPosn=0;
        playSong();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = ("Music Service");
            String description = "This notification allows control of the music service from anywhere.";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel("ID", name, importance);
            channel.setVibrationPattern(new long[]{0});
            channel.enableVibration(true);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
