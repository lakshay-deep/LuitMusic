package com.vianet.musicplayer.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.vianet.musicplayer.R;
import com.vianet.musicplayer.util.MediaItem;

import java.util.ArrayList;

public class SongAdapter extends ArrayAdapter<MediaItem> {

	private ArrayList<MediaItem> listOfSongs;
	private LayoutInflater inflator;

	public SongAdapter(Context context, int resource, ArrayList<MediaItem> listOfSongs) {
		super(context, resource, listOfSongs);
		this.listOfSongs = listOfSongs;
		inflator = LayoutInflater.from(context);
	}

	private class ViewHolder{
		TextView textViewSongName, textViewArtist;
	}

	private ViewHolder holder;

    @Nullable
    @Override
    public MediaItem getItem(int position) {
        return super.getItem(position);
    }

    //    private int lastPosition = -1;
	@NonNull
	@Override
	public View getView(int position, View convertView, @NonNull ViewGroup parent) {
		if(convertView == null){
			convertView = inflator.inflate(R.layout.custom_list, parent, false);
			holder = new ViewHolder();
			holder.textViewSongName = convertView.findViewById(R.id.textViewSongName);
			holder.textViewArtist = convertView.findViewById(R.id.textViewArtist);
//			holder.textViewDuration = (TextView) myView.findViewById(R.id.textViewDuration);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		final MediaItem detail = listOfSongs.get(position);
		holder.textViewSongName.setText(detail.toString());
		holder.textViewArtist.setText(detail.getAlbum() + " - " + detail.getArtist());

//		holder.textViewDuration.setText(UtilFunctions.getDuration(detail.getDuration()));
//        holder.textViewDuration.setText("5:00");
//        Animation animation = AnimationUtils.loadAnimation(getContext(), (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
//        myView.startAnimation(animation);
//        lastPosition = position;

		return convertView;
	}
}
