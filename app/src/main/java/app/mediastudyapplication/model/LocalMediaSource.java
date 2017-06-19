package app.mediastudyapplication.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.media.MediaMetadataCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import app.mediastudyapplication.utils.LogHelper;

/**
 * Created by Administrator on 2017/6/16.
 */

public class LocalMediaSource implements MusicProviderSource{
    private static final String TAG = LogHelper.makeLogTag(LocalMediaSource.class);
    private Context mContext;

    public LocalMediaSource(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public Iterator<MediaMetadataCompat> iterator() {
        return getLocalSongs().iterator();
    }

    //获取本地歌曲
    private List<MediaMetadataCompat> getLocalSongs() {
        ContentResolver musicResolver = mContext.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        List<MediaMetadataCompat> list = new ArrayList<>();

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST);
            int albumColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM);
            int pathColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DATA);
            int durationColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DURATION);

            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisAlbum = musicCursor.getString(albumColumn);
                String path = musicCursor.getString(pathColumn);
                long duration = musicCursor.getLong(durationColumn);

                MediaMetadataCompat media = new MediaMetadataCompat.Builder()
                        .putLong(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, thisId)
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, thisAlbum)
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, thisArtist)
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, thisTitle)
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI,path)
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
                        .build();
                list.add(media);
            }
            while (musicCursor.moveToNext());
        }
        return list;
    }
}
