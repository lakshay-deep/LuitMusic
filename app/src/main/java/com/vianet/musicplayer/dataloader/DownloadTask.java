package com.vianet.musicplayer.dataloader;

import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.vianet.musicplayer.AudioPlayerActivity;
import com.vianet.musicplayer.util.PlayerConstants;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by editing2 on 23-Jan-17.
 */

public class DownloadTask extends AsyncTask<String, Integer, String> {

//    private NotificationManager mNotifyManager;
//    private NotificationCompat.Builder build;
//    private int id = 1;

    private Context context;
//        private PowerManager.WakeLock mWakeLock;

    public DownloadTask(Context context) {
        this.context = context;
//        this.mNotifyManager = mNotifyManager;
//        this.build = build;
    }

    @Override
    protected String doInBackground(String... sUrl) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(sUrl[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(true);
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
//                input = connection.getInputStream();
            input = new BufferedInputStream(url.openStream());
//                String mydir = Environment.getExternalStorageDirectory().getAbsolutePath();//context.getDir("luitNew", Context.MODE_PRIVATE);
            File mydir = context.getDir("luitNew", Context.MODE_PRIVATE);
            if (!mydir.exists()){
                mydir.mkdir();
            }
            File file = new File(mydir, extractFilename());
            output = new FileOutputStream(file);

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
//                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
//            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
//                    getClass().getName());
//            mWakeLock.acquire();

        // Displays the progress bar for the first time.
//        build.setProgress(100, 0, false);
//        mNotifyManager.notify(id, build.build());
        Toast.makeText(context, "Downloading Start..", Toast.LENGTH_SHORT).show();
    }

//    @Override
//    protected void onProgressUpdate(Integer... progress) {
//        super.onProgressUpdate(progress);
//        // if we get here, length is known, now set indeterminate to false
////        build.setProgress(100, progress[0], false);
////        mNotifyManager.notify(id, build.build());
////        super.onProgressUpdate(progress);
//    }

    @Override
    protected void onPostExecute(String result) {
//            mWakeLock.release();
//        build.setContentText("Download complete");
//        // Removes the progress bar
//        build.setProgress(0, 0, false);
//        mNotifyManager.notify(id, build.build());

        if (result != null) {
            Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
        }
        else
            Toast.makeText(context,"Song downloaded", Toast.LENGTH_SHORT).show();
    }


    private String extractFilename(){
        String url = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getPath();
        if(url.equals("")){
            return "";
        }
        String newFilename = "";
        if(url.contains("/")){
            int dotPosition = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getPath().lastIndexOf("/");
            newFilename = url.substring(dotPosition + 1, url.length());
        }
        else{
            newFilename = url;
        }
        return newFilename;
    }
}