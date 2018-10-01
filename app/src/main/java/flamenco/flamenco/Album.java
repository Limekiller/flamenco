package flamenco.flamenco;

import android.content.ContentUris;
import android.graphics.drawable.Drawable;
import android.net.Uri;

public class Album {

    private String art;
    private String title;
    private String artist;
    private String year;

    public Album(String albumTitle, String albumArtist, String albumArt, String albumYear ) {
        title=albumTitle;
        artist=albumArtist;
        art=albumArt;
        year=albumYear;

    }

    public String getTitle(){
        return title;
    }
    public String getArtist(){
        return artist;
    }
    public String getAlbumArt() {return art;}
    public String getYear(){return year;}

}
