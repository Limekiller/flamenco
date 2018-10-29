package flamenco.flamenco.MainFragment;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import flamenco.flamenco.Folder;
import flamenco.flamenco.ListMusic;
import flamenco.flamenco.R;
import flamenco.flamenco.Song;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class FoldersAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Folder> folders;
    private LayoutInflater songInf;
    public ListMusic listMusic;

    public FoldersAdapter(Context context, ArrayList<Folder> theFolders) {

        this.context = context;
        folders=theFolders;
        songInf=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return folders.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //map to song layout
        final LinearLayout folderLay;
        folderLay = (LinearLayout) songInf.inflate(R.layout.folder, parent, false);

        //get title and artist views
        TextView pathView = folderLay.findViewById(R.id.folder_path);
        ImageView artView = folderLay.findViewById(R.id.album_art);
        LayoutInflater inflater = LayoutInflater.from(context);
        //get song using position
        Folder currFolder = folders.get(position);
        //get title and artist strings
        pathView.setText(currFolder.getPath());

        try {
            Glide.with(context).load(currFolder.getSongList().get(0).getAlbumArt())
                    .error(R.drawable.placeholder).crossFade().dontAnimate().centerCrop().into(artView);
        } catch (IndexOutOfBoundsException e) {
            Glide.with(context).load(R.drawable.placeholder)
                    .crossFade().dontAnimate().centerCrop().into(artView);
        }

        //set position as tag
        folderLay.setTag(position);
        return folderLay;
    }
}
