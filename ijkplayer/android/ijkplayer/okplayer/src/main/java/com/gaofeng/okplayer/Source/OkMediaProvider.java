package com.gaofeng.okplayer.Source;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by ligaofeng on 2017/12/23.
 */

public class OkMediaProvider {
    private static final String TAG = OkMediaProvider.class.getSimpleName();
    private static OkMediaProvider instance = new OkMediaProvider();

    private static Context mContext;
    private static final Uri URI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    public static int WIDTH = 100;
    public static int HEIGHT = 100;

    private OkMediaProvider() {

    }

    public static OkMediaProvider getInstance(Context context){
        mContext = context;
        return instance;
    }

    private static List<Media> mMediaList = new ArrayList<>();
    public List<Media> getMedia(){
        ContentResolver mContentResolver = mContext.getContentResolver();
        Cursor cursor = mContentResolver.query(URI, null, null, null,
                MediaStore.Video.Media.DEFAULT_SORT_ORDER);
        int vidsCount = 0;
        if (cursor != null) {
            vidsCount = cursor.getCount();
            if(mMediaList.size() == vidsCount){
                Log.i(TAG, "not need scan again");
                return mMediaList;
            }else{
                mMediaList.clear();
            }

            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                String size = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                String resolution = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.RESOLUTION));
                Media media = new Media();
                media.set(MediaStore.Video.Media.DATA, path);
                media.set(MediaStore.Video.Media.DISPLAY_NAME, name);
                media.set(MediaStore.Video.Media.SIZE, size);
                media.set(MediaStore.Video.Media.RESOLUTION, resolution);
                mMediaList.add(media);
            }
            cursor.close();
        }

        return mMediaList;
    }

    public Bitmap getThumbnail(final String path){
        Bitmap bitmap;
        bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, WIDTH, HEIGHT, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

//        try{
//            Glide.with(mContext).
//        }catch (Exception e){
//
//        }




        return bitmap;
    }

    public class Media{
        private String path = "unknown";
        private String name = "unknown";
        private String size = "0";
        private String resolution = "unknown";

        public void set(String id, String volume){
            if(volume == null || id == null){
                Log.e(TAG,"null data");
                return;
            }
            switch(id){
                case MediaStore.Video.Media.DATA:
                    path = volume;
                    break;
                case MediaStore.Video.Media.DISPLAY_NAME:
                    name = volume;
                    break;
                case MediaStore.Video.Media.SIZE:
                    size = volume;
                    break;
                case MediaStore.Video.Media.RESOLUTION:
                    resolution = volume;
                    break;
            }
        }

        public String get(String id){
            String s = "0";
            switch(id){
                case MediaStore.Video.Media.DATA:
                    s = path;
                    break;
                case MediaStore.Video.Media.DISPLAY_NAME:
                    s = name;
                    break;
                case MediaStore.Video.Media.SIZE:
                    s = size;
                    break;
                case MediaStore.Video.Media.RESOLUTION:
                    s = resolution;
                    break;
            }
            return  s;
        }

    }


}
