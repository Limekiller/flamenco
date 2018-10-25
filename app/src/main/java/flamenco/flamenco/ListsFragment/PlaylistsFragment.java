package flamenco.flamenco.ListsFragment;

import android.app.Activity;
import android.content.Context;
import android.net.sip.SipSession;
import android.provider.SyncStateContract;
import android.support.v4.app.Fragment;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.security.auth.callback.Callback;

import flamenco.flamenco.R;

public class PlaylistsFragment extends Fragment{


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.playlistsfragment, container, false);

        return  view;
    }

}