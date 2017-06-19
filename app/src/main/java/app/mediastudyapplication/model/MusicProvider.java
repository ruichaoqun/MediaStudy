package app.mediastudyapplication.model;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import app.mediastudyapplication.utils.LogHelper;

/**
 * Created by Administrator on 2017/6/16.
 * 媒体资源提供者
 */

public class MusicProvider {
    private static final String TAG = LogHelper.makeLogTag(MusicProvider.class);

    private MusicProviderSource mSource;
    private Context mContext;
    private ConcurrentMap<String, List<MediaMetadataCompat>> mMusicListByGenre;//根据流派分类
    private ConcurrentMap<String, List<MediaMetadataCompat>> mMusicListLocal;//本地所有音乐
    private ConcurrentMap<String, List<MediaMetadataCompat>> mMusicListByFile;//根据文件夹分类

    private final ConcurrentMap<String, MutableMediaMetadata> mMusicListById;


    enum State {
        NON_INITIALIZED, INITIALIZING, INITIALIZED
    }

    private volatile State mCurrentState = State.NON_INITIALIZED;

    public MusicProvider(Context mContext){
        this(new LocalMediaSource(mContext));
        this.mContext = mContext;
    }
    public MusicProvider(MusicProviderSource mSource) {
        this.mSource = mSource;
        mMusicListByGenre = new ConcurrentHashMap<>();
        mMusicListLocal = new ConcurrentHashMap<>();
        mMusicListByFile = new ConcurrentHashMap<>();
        mMusicListById = new ConcurrentHashMap<>();
    }

    /**
     * 异步下载歌曲
     */
    public void retrieveMediaAsync(final Callback callback) {
        LogHelper.d(TAG, "retrieveMediaAsync called");
        if (mCurrentState == State.INITIALIZED) {
            if (callback != null) {
                // Nothing to do, execute callback immediately
                callback.onMusicCatalogReady(true);
            }
            return;
        }

        new AsyncTask<Void, Void, State>() {
            @Override
            protected State doInBackground(Void... params) {
                retrieveMedia();
                return mCurrentState;
            }

            @Override
            protected void onPostExecute(State current) {
                if (callback != null) {
                    callback.onMusicCatalogReady(current == State.INITIALIZED);
                }
            }
        }.execute();
    }

    //下载歌曲
    private synchronized void retrieveMedia() {
        try {
            if (mCurrentState == State.NON_INITIALIZED) {
                mCurrentState = State.INITIALIZING;

                Iterator<MediaMetadataCompat> tracks = mSource.iterator();
                while (tracks.hasNext()) {
                    MediaMetadataCompat item = tracks.next();
                    String musicId = item.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);
                    mMusicListById.put(musicId, new MutableMediaMetadata(musicId, item));
                }
                buildListsByGenre();
                mCurrentState = State.INITIALIZED;
            }
        } finally {
            if (mCurrentState != State.INITIALIZED) {
                // Something bad happened, so we reset state to NON_INITIALIZED to allow
                // retries (eg if the network connection is temporary unavailable)
                mCurrentState = State.NON_INITIALIZED;
            }
        }
    }

    //根据歌曲风格创建播放列表
    private synchronized void buildListsByGenre() {
        ConcurrentMap<String, List<MediaMetadataCompat>> newMusicListByGenre = new ConcurrentHashMap<>();

        for (MutableMediaMetadata m : mMusicListById.values()) {
            String genre = m.metadata.getString(MediaMetadataCompat.METADATA_KEY_GENRE);
            List<MediaMetadataCompat> list = newMusicListByGenre.get(genre);
            if (list == null) {
                list = new ArrayList<>();
                newMusicListByGenre.put(genre, list);
            }
            list.add(m.metadata);
        }
        mMusicListByGenre = newMusicListByGenre;
    }

    /**
     * 获取本地所有音乐
     */
    public Iterable<MediaMetadataCompat> getShuffledMusic() {
        if (mCurrentState != State.INITIALIZED) {
            return Collections.emptyList();
        }
        List<MediaMetadataCompat> shuffled = new ArrayList<>(mMusicListById.size());
        for (MutableMediaMetadata mutableMetadata: mMusicListById.values()) {
            shuffled.add(mutableMetadata.metadata);
        }
        Collections.shuffle(shuffled);
        return shuffled;
    }


    public List<MediaBrowserCompat.MediaItem> getChildren(String mediaId, Resources resources){
        return null;
    }

    public boolean isInitialized() {
        return mCurrentState == State.INITIALIZED;
    }

    public interface Callback {
        void onMusicCatalogReady(boolean success);
    }
}
