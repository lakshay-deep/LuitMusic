package com.vianet.musicplayer.dataloader;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.LinearLayout;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by editing2 on 28-Oct-16.
 */

public class LoadBackground extends AsyncTask<String, Void, Drawable> {

    private String imageUrl , imageName;
    private LinearLayout linearLayout;

    public LoadBackground(LinearLayout linearLayout, String url, String file_name) {
        this.linearLayout = linearLayout;
        this.imageUrl = url;
        this.imageName = file_name;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Drawable doInBackground(String... urls) {

        try {
            InputStream is = (InputStream) this.fetch(this.imageUrl);
            Drawable d = Drawable.createFromStream(is, this.imageName);
            return d;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private Object fetch(String address) throws MalformedURLException,IOException {
        URL url = new URL(address);
        Object content = url.getContent();
        return content;
    }

    @Override
    protected void onPostExecute(Drawable result) {
        super.onPostExecute(result);
        linearLayout.setBackgroundDrawable(result);
    }

}