package flamenco.flamenco;

import android.content.ContentUris;
import android.graphics.drawable.Drawable;
import android.net.Uri;

public class Song {

    private long id;
    private Uri albumArt;
    private long albumId;
    private String title;
    private String artist;
    private String year;

    public Song(long songID, String songTitle, String songArtist, long songAlbumId, String songYear ) {
        id=songID;
        title=songTitle;
        artist=songArtist;
        albumId=songAlbumId;
        year=songYear;

        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        albumArt = ContentUris.withAppendedId(sArtworkUri, albumId);
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
    public Uri getAlbumArt() {return albumArt;}
    public String getYear(){return year;}

}
