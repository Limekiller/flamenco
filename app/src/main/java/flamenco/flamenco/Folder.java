package flamenco.flamenco;

import android.content.ContentUris;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.util.ArrayList;

public class Folder {

    private String path;
    private ArrayList<Song> songList;
    private ArrayList<Folder> folderList;
    private Folder parentFolder;


    public Folder(String folderPath, ArrayList<Song> folderSongList, ArrayList<Folder> folderFolderList, Folder folderParentFolder) {

        path = folderPath;
        songList = folderSongList;
        folderList = folderFolderList;
        parentFolder = folderParentFolder;

    }

    public String getPath() {return path;}
    public ArrayList<Song> getSongList() {return songList;}
    public ArrayList<Folder> getFolderList() {return folderList;}
    public Folder getParentFolder() { return parentFolder;}

    public void addToSongList(Song song) {
        songList.add(song);
    }

    public void addToFolderList(Folder folder) {
        folderList.add(folder);
    }


}
