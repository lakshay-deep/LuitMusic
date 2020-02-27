package com.vianet.musicplayer.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.vianet.musicplayer.LandingPage;
import com.vianet.musicplayer.R;
import com.vianet.musicplayer.SongsList;
import com.vianet.musicplayer.dataloader.JsonParse1;

import java.io.File;
import java.io.FileDescriptor;
import java.util.ArrayList;

public class UtilFunctions {


//    public static int tabNum, albumNum;

    /**
     * Check if service is running or not
     *
     * @param serviceName ..
     * @param context     ..
     * @return ..
     */
    public static boolean isServiceRunning(String serviceName, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<MediaItem> listOfSongs;

    /**
     * Read the songs present in url storage
     *
     * @return ..
     */
    public static ArrayList<MediaItem> listOfSongs(int tabNum, int albumNum) {
//        UtilFunctions.tabNum = tabNum;
//        UtilFunctions.albumNum = albumNum;
//		Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//		Cursor c = context.getContentResolver().query(uri, null, MediaStore.Audio.Media.IS_MUSIC + " != 0", null, null);
        listOfSongs = new ArrayList<MediaItem>();
        listOfSongs.clear();
//		c.moveToFirst();
//		while(c.moveToNext())
        if (tabNum == 0 || tabNum == 1) {
            for (int i = 0; i < LandingPage.albumCount.get(tabNum).get(albumNum).get(0).size(); i++) {
                MediaItem songData = new MediaItem();
//			if (ParseJSONAlbum.category[i].equalsIgnoreCase(String.valueOf(tabNum)) && ParseJSONSong.albumId[i].equalsIgnoreCase(String.valueOf(albumNum))) {
                String title = LandingPage.albumCount.get(tabNum).get(albumNum).get(0).get(i); //c.getString(c.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String artist = LandingPage.albumCount.get(tabNum).get(albumNum).get(4).get(i);//c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String album = LandingPage.albumCount.get(tabNum).get(albumNum).get(3).get(i);//songTitle[i];//c.getString(c.getColumnIndex(MediaStore.Audio.Media.ALBUM));
//                double duration1 = Double.parseDouble(ParseJSONSong.duration[i]);//c.getLong(c.getColumnIndex(MediaStore.Audio.Media.DURATION));
//                long duration = (long) duration1*60*1000;
                String data = LandingPage.albumCount.get(tabNum).get(albumNum).get(2).get(i);//c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));
                String albumId = LandingPage.albumCount.get(tabNum).get(albumNum).get(1).get(i);//c.getLong(c.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                String composer = LandingPage.albumCount.get(tabNum).get(albumNum).get(4).get(i);//songTitle[i];//c.getString(c.getColumnIndex(MediaStore.Audio.Media.COMPOSER));

                songData.setTitle(title);
                songData.setAlbum(album);
                songData.setArtist(artist);
//                songData.setDuration(duration);
                songData.setPath(data);
                songData.setAlbumId(albumId);
                songData.setComposer(composer);
                listOfSongs.add(songData);
//                LOG_CLASS = "set songs";
            }
        } else if (tabNum == 2) {
            MediaItem songData = new MediaItem();
//			if (ParseJSONAlbum.category[i].equalsIgnoreCase(String.valueOf(tabNum)) && ParseJSONSong.albumId[i].equalsIgnoreCase(String.valueOf(albumNum))) {
            String title = JsonParse1.title[albumNum]; //c.getString(c.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String artist = JsonParse1.artist[albumNum];//c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String album = JsonParse1.album[albumNum];//songTitle[i];//c.getString(c.getColumnIndex(MediaStore.Audio.Media.ALBUM));
//                double duration1 = Double.parseDouble(ParseJSONSong.duration[i]);//c.getLong(c.getColumnIndex(MediaStore.Audio.Media.DURATION));
//                long duration = (long) duration1*60*1000;
            String data = JsonParse1.file[albumNum];//c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));
            String albumId = JsonParse1.thumbnail[albumNum];//c.getLong(c.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            String composer = JsonParse1.producer[albumNum];//songTitle[i];//c.getString(c.getColumnIndex(MediaStore.Audio.Media.COMPOSER));

            songData.setTitle(title);
            songData.setAlbum(album);
            songData.setArtist(artist);
//                songData.setDuration(duration);
            songData.setPath(data);
            songData.setAlbumId(albumId);
            songData.setComposer(composer);
            listOfSongs.add(songData);
        } else if (tabNum == 3) {
            File mydir = SongsList.context.getDir("luitNew", Context.MODE_PRIVATE);
            MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();

            byte[] art;
            if (mydir.exists()) {
                File[] list = mydir.listFiles();
                for (File f : list) {
                    MediaItem songData = new MediaItem();
                    metaRetriver.setDataSource(f.getPath());
                    try {
                        art = metaRetriver.getEmbeddedPicture();
                        songData.setAlbumIdOffline(BitmapFactory.decodeByteArray(art, 0, art.length));
                        String album = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                        String artist = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                        String title = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                        String composer = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER);
                        if (album != null) {
                            songData.setAlbum(album);
                        } else {
                            songData.setAlbum("");
                        }
                        if (artist != null) {
                            songData.setArtist(artist);
                        } else {
                            songData.setArtist("");
                        }
                        if (title != null) {
                            songData.setTitle(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
                        } else {
                            songData.setTitle(f.getName().replace(".mp3", ""));
                        }
                        if (composer != null) {
                            songData.setComposer(composer);
                        } else {
                            songData.setComposer("");
                        }
                        songData.setPath(f.getPath());

                    } catch (Exception e) {
//                        Toast.makeText(SongsList.context, "exception", Toast.LENGTH_SHORT).show();
                    }
                    listOfSongs.add(songData);
                }
            }
        }
//		}
//		c.close();
//		Log.d("SIZE", "SIZE: " + listOfSongs.size());
        return listOfSongs;
    }

    public static ArrayList<MediaItem> listToPlay() {
        ArrayList<MediaItem> listToPlay = new ArrayList<MediaItem>();
        listToPlay.clear();
        listToPlay.addAll(listOfSongs);

        return listToPlay;
    }

    /**
     * Get the album image from albumId
     *
     * @param context  ..
     * @param album_id ..
     * @return ..
     */
    public static Bitmap getAlbumart(Context context, Long album_id) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
            ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
            if (pfd != null) {
                FileDescriptor fd = pfd.getFileDescriptor();
                bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
//	            pfd = null;
//	            fd = null;
            }
        } catch (Error | Exception ee) {
            ee.printStackTrace();
        }
        return bm;
    }

    /**
     * @param context ..
     * @return ..
     */
    public static Bitmap getDefaultAlbumArt(Context context) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_album_art, options);
        } catch (Error | Exception ee) {
            ee.printStackTrace();
        }
        return bm;
    }

    /**
     * Convert milliseconds into time hh:mm:ss
     *
     * @param milliseconds ..
     * @return time in String
     */
    public static String getDuration(long milliseconds) {
        long sec = (milliseconds / 1000) % 60;
        long min = (milliseconds / (60 * 1000)) % 60;
        long hour = milliseconds / (60 * 60 * 1000);

        String s = (sec < 10) ? "0" + sec : "" + sec;
        String m = (min < 10) ? "0" + min : "" + min;
        String h = "" + hour;

        String time = "";
        if (hour > 0) {
            time = h + ":" + m + ":" + s;
        } else {
            time = m + ":" + s;
        }
        return time;
    }

    public static int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double) progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }

    public static boolean currentVersionSupportBigNotification() {
        int sdkVersion = android.os.Build.VERSION.SDK_INT;
        return sdkVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean currentVersionSupportLockScreenControls() {
        int sdkVersion = android.os.Build.VERSION.SDK_INT;
        return sdkVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }
}
