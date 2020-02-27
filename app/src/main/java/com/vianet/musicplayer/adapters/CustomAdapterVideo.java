package com.vianet.musicplayer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.vianet.musicplayer.LandingPage;
import com.vianet.musicplayer.R;
import com.vianet.musicplayer.dataloader.JsonParseVideo;

/**
 * Created by editing2 on 18-Nov-16.
 */

public class CustomAdapterVideo extends BaseAdapter {

    private Context context;
    private int arraySize, selectData;

    public CustomAdapterVideo(Context context, int arraySize, int selectData) {
        this.context = context;
        this.selectData = selectData;
        this.arraySize = arraySize;
    }

    @Override
    public int getCount() {
        return arraySize;//ParseJSONGallery.image.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View view, ViewGroup parent) {

        if (view == null)
            view = LayoutInflater.from(context).inflate(R.layout.itemlist_even, parent, false);

        NetworkImageView networkImageView = view.findViewById(R.id.networkImageView1);
        TextView textViewEven = view.findViewById(R.id.textEven);
        ImageLoader loadImage = MySingleton.getInstance(context).getImageLoader();
        switch (selectData) {
            case 0:
                networkImageView.setImageUrl("http://img.youtube.com/vi/" + LandingPage.albumCount.get(0).get(position).get(1).get(0) + "/hqdefault.jpg", loadImage);
                networkImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                textViewEven.setText(LandingPage.album.get(position));
                break;
            case 1:
                networkImageView.setImageUrl("http://img.youtube.com/vi/" + LandingPage.albumCount.get(1).get(position).get(1).get(0) + "/hqdefault.jpg", loadImage);
                networkImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                textViewEven.setText(LandingPage.artist.get(position));
                break;
            case 2:
                networkImageView.setImageUrl("http://img.youtube.com/vi/" + JsonParseVideo.ytId[position] + "/hqdefault.jpg", loadImage);
                networkImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                textViewEven.setText(JsonParseVideo.title[position]);
                break;
        }
        return view;
    }
}
