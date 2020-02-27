package com.vianet.musicplayer;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.vianet.musicplayer.adapters.MySingleton;


public class VideosList extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    public VideosList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VideosList.
     */
    // TODO: Rename and change types and number of parameters
    public static VideosList newInstance(int param1, int param2) {
        VideosList fragment = new VideosList();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_videos_list, container, false);
        final int tabNum = getArguments().getInt(ARG_PARAM1);
        final int albumNum = getArguments().getInt(ARG_PARAM2);
        AdView mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        TextView albumName = view.findViewById(R.id.albumName);
        if (tabNum == 0) {
            albumName.setText(LandingPage.album.get(albumNum) + " | " + "Total Videos: " + LandingPage.albumCount.get(tabNum).get(albumNum).get(1).size());
        } else if (tabNum == 1) {
            albumName.setText(LandingPage.artist.get(albumNum) + " | " + "Total Videos: " + LandingPage.albumCount.get(tabNum).get(albumNum).get(1).size());
        }
        NetworkImageView albumImage = view.findViewById(R.id.albumImage);
        albumImage.setScaleType(ImageView.ScaleType.FIT_XY);
        ImageLoader imageLoader = MySingleton.getInstance(getContext()).getImageLoader();
        GridView gridView = view.findViewById(R.id.gridSongs);
        CustomAdapterVideoList customAdapterVideoList = new CustomAdapterVideoList(getContext(), LandingPage.albumCount.get(tabNum).get(albumNum).get(1).size(), tabNum, albumNum);
        albumImage.setImageUrl("http://img.youtube.com/vi/" + LandingPage.albumCount.get(tabNum).get(albumNum).get(1).get(0) + "/hqdefault.jpg", imageLoader);
        gridView.setAdapter(customAdapterVideoList);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.content_main_video, VideoDetails.newInstance(tabNum, albumNum, i)).addToBackStack(null);
                fragmentTransaction.commit();

            }
        });

        return view;
    }


    public class CustomAdapterVideoList extends BaseAdapter {

        Context context;
        int arraySize, selectData, selectAlbum;

        public CustomAdapterVideoList(Context context, int arraySize, int selectData, int selectAlbum) {
            this.context = context;
            this.selectData = selectData;
            this.arraySize = arraySize;
            this.selectAlbum = selectAlbum;
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

        private NetworkImageView networkImageView;
        private TextView textViewEven;

        public View getView(final int position, View view, ViewGroup parent) {
            if (view == null)
                view = LayoutInflater.from(context).inflate(R.layout.itemlist_even, parent, false);

            networkImageView = view.findViewById(R.id.networkImageView1);
            textViewEven = view.findViewById(R.id.textEven);
            ImageLoader loadImage = MySingleton.getInstance(context).getImageLoader();
            networkImageView.setImageUrl("http://img.youtube.com/vi/" + LandingPage.albumCount.get(selectData).get(selectAlbum).get(1).get(position) + "/hqdefault.jpg", loadImage);
            networkImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            textViewEven.setText(LandingPage.albumCount.get(selectData).get(selectAlbum).get(0).get(position));

            return view;
        }
    }

}
