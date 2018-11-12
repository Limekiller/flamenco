package flamenco.flamenco;

import android.content.ContentUris;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.util.ArrayList;

public class Song {

    private long id;
    //private Uri albumArt;
    private String albumArt;
    private long albumId;
    private String title;
    private String artist;
    private String year;
    private ArrayList<Song> albumSongList;


    public Song(long songID, String songTitle, String songArtist, long songAlbumId, String songYear, String songAlbumArt ) {
        id=songID;
        title=songTitle;
        artist=songArtist;
        albumId=songAlbumId;
        year=songYear;
        albumArt = songAlbumArt;

    }

    public void setAlbumSongList (ArrayList<Song> songList) {
        albumSongList=new ArrayList<>(songList);
    }

    public long getID() {
        return id;
    }
    public String getTitle(){
        return title;
    }
    public String getArtist(){
        return artist;
    }
    public long getAlbumId() {return albumId;}
    //public Uri getAlbumArt() {return albumArt;}
    public String getAlbumArt() {return albumArt;}
    public String getYear(){return year;}
    public ArrayList<Song> getAlbumSongList() {return albumSongList;}

}
