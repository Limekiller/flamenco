package flamenco.flamenco;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaTimestamp;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentUris;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import flamenco.flamenco.ListMusic;

import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

    private MediaPlayer player;
    private ArrayList<Song> songs;
    private int songPosn;
    private final IBinder musicBind = new MusicBinder();
    private AudioManager audioManager;


    public MusicService() {
    }


    public void onCreate(){
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
        }
    }


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
            mp.reset();
            playNext();

        }
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
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
        player.pause();
    }


    public void seek(int posn){
        player.seekTo(posn);
    }


    public void go(){
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
}
