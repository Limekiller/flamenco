package flamenco.flamenco;

import android.net.Uri;

public class Song {

    private long id;
    private String title;
    private String artist;
    private Uri art;

    public Song(long songID, String songTitle, String songArtist, Uri songArt) {
        id=songID;
        title=songTitle;
        artist=songArtist;
        art=songArt;
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
    public Uri getArt() { return art;}

}
