package flamenco.flamenco;

import android.content.ContentUris;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Album {

    private String art;
    private String title;
    private String artist;
    private String year;
    private ArrayList<Song> albumSongList;

    public Album(String albumTitle, String albumArtist, String albumArt, String albumYear ) {
        title=albumTitle;
        artist=albumArtist;
        art=albumArt;
        year=albumYear;

    }

    public void setAlbumSongList (ArrayList<Song> songList) {
        albumSongList=new ArrayList<>(songList);
    }


    public String getTitle(){
        return title;
    }
    public String getArtist(){
        return artist;
    }
    public String getAlbumArt() {return art;}
    public ArrayList<Song> getAlbumSongList() {return albumSongList;}
    public String getYear(){return year;}

}
