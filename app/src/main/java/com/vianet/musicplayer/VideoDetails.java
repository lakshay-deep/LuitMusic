package com.vianet.musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.vianet.musicplayer.adapters.MySingleton;
import com.vianet.musicplayer.dataloader.JsonParseVideo;


public class VideoDetails extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";


    public VideoDetails() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @param param3 Parameter 2.
     * @return A new instance of fragment VideoDetails.
     */
    // TODO: Rename and change types and number of parameters
    public static VideoDetails newInstance(int param1, int param2, int param3) {
        VideoDetails fragment = new VideoDetails();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        args.putInt(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    View view;
    TextView title, album, desc;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_video_details, container, false);
        AdView mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        final int tabNum = getArguments().getInt(ARG_PARAM1);
        final int albumNum = getArguments().getInt(ARG_PARAM2);
        final int position = getArguments().getInt(ARG_PARAM3);
        ImageLoader imageLoader = MySingleton.getInstance(getContext()).getImageLoader();
        NetworkImageView videoThumb = view.findViewById(R.id.videoThumb);
        videoThumb.setScaleType(ImageView.ScaleType.FIT_XY);
        ImageButton play = view.findViewById(R.id.playButton);
        title = view.findViewById(R.id.title);
        album = view.findViewById(R.id.album);
        desc = view.findViewById(R.id.description);

        if (tabNum != 2){
            videoThumb.setImageUrl("http://img.youtube.com/vi/" + LandingPage.albumCount.get(tabNum).get(albumNum).get(1).get(position) + "/hqdefault.jpg", imageLoader);
            title.setText(LandingPage.albumCount.get(tabNum).get(albumNum).get(0).get(position));
            album.setText("Album: " + LandingPage.albumCount.get(tabNum).get(albumNum).get(3).get(position));
            desc.setText(LandingPage.albumCount.get(tabNum).get(albumNum).get(2).get(position));
        } else {
            videoThumb.setImageUrl("http://img.youtube.com/vi/" + JsonParseVideo.ytId[position] + "/hqdefault.jpg", imageLoader);
            title.setText(JsonParseVideo.title[position]);
            album.setText("Album: " + JsonParseVideo.album[position]);
            desc.setText(JsonParseVideo.description[position]);
        }

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), YoutubePlayer.class);
                intent.putExtra("albumNum", albumNum);
                intent.putExtra("tabPosition", tabNum);
                intent.putExtra("clickPosition", position);
                startActivity(intent);
            }
        });
        return view;
    }

}
