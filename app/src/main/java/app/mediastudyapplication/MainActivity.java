package app.mediastudyapplication;

import android.content.ComponentName;
import android.graphics.Color;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import app.mediastudyapplication.utils.LogHelper;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private static final String TAG = LogHelper.makeLogTag(AppCompatActivity.class);
    private MediaBrowserCompat mMediaBrowser;//媒体浏览器
    public static int themeColor = Color.parseColor("#B24242");
    private String mMediaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mMediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, MusicService.class), mConnectionCallback, null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMediaBrowser.connect();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main3, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //MediaBrowserCompat链接成功后调用该回调
    private final MediaBrowserCompat.ConnectionCallback mConnectionCallback =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    LogHelper.d(TAG, "onConnected");
                    try {
                        connectToSession(mMediaBrowser.getSessionToken());
                    } catch (RemoteException e) {
                        LogHelper.e(TAG, e, "could not connect media controller");
                    }
                }
            };

    private void connectToSession(MediaSessionCompat.Token sessionToken) throws RemoteException {
        MediaControllerCompat mediaController = new MediaControllerCompat(this, sessionToken);
        MediaControllerCompat.setMediaController(this,mediaController);
        mediaController.registerCallback(mMediaControllerCallback);

        mMediaId = mMediaBrowser.getRoot();
        //发起数据请求
        //先解除订阅
        mMediaBrowser.unsubscribe(mMediaId);
        mMediaBrowser.subscribe(mMediaId, mSubscriptionCallback);

        // Add MediaController callback so we can redraw the list when metadata changes:
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(this);
        if (controller != null) {
            controller.registerCallback(mMediaControllerCallback);
        }
    }

    // Callback that ensures that we are showing the controls
    //媒体控制器控制播放过程中的回调接口
    private final MediaControllerCompat.Callback mMediaControllerCallback =
            new MediaControllerCompat.Callback() {
                @Override
                public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
//                    //播放状态发生改变时回调
//                    if (shouldShowControls()) {
//                        showPlaybackControls();
//                    } else {
//                        LogHelper.d(TAG, "mediaControllerCallback.onPlaybackStateChanged: " +
//                                "hiding controls because state is ", state.getState());
//                        hidePlaybackControls();
//                    }
                }

                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                    //播放的媒体数据发生变化时的回调
//                    if (shouldShowControls()) {
//                        showPlaybackControls();
//                    } else {
//                        LogHelper.d(TAG, "mediaControllerCallback.onMetadataChanged: " +
//                                "hiding controls because metadata is null");
//                        hidePlaybackControls();
//                    }
                }
            };

    //向媒体流量服务发起媒体浏览请求的回调接口
    private final MediaBrowserCompat.SubscriptionCallback mSubscriptionCallback =
            new MediaBrowserCompat.SubscriptionCallback() {
                @Override
                public void onChildrenLoaded(@NonNull String parentId,
                                             @NonNull List<MediaBrowserCompat.MediaItem> children) {
//                    try {
//                        LogHelper.d(TAG, "fragment onChildrenLoaded, parentId=" + parentId +
//                                "  count=" + children.size());
//                        checkForUserVisibleErrors(children.isEmpty());
//                        mBrowserAdapter.clear();
//                        for (MediaBrowserCompat.MediaItem item : children) {
//                            mBrowserAdapter.add(item);
//                        }
//                        mBrowserAdapter.notifyDataSetChanged();
//                    } catch (Throwable t) {
//                        LogHelper.e(TAG, "Error on childrenloaded", t);
//                    }
                }

                @Override
                public void onError(@NonNull String id) {
//                    LogHelper.e(TAG, "browse fragment subscription onError, id=" + id);
//                    Toast.makeText(getActivity(), R.string.error_loading_media, Toast.LENGTH_LONG).show();
//                    checkForUserVisibleErrors(true);
                }
            };
}
