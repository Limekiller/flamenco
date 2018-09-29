package flamenco.flamenco;

import android.graphics.drawable.Drawable;

public class Song {

    private long id;
    private String title;
    private String artist;
    private Drawable cover;

    public Song(long songID, String songTitle, String songArtist, Drawable songCover) {
        id=songID;
        title=songTitle;
        artist=songArtist;
        cover=songCover;
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
    public Drawable getCover(){return cover;}

}
