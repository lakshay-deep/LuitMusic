package com.vianet.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.vianet.musicplayer.adapters.MySingleton;
import com.vianet.musicplayer.adapters.SongAdapter;
import com.vianet.musicplayer.controls.Controls;
import com.vianet.musicplayer.dataloader.JsonParse1;
import com.vianet.musicplayer.service.SongService;
import com.vianet.musicplayer.util.MediaItem;
import com.vianet.musicplayer.util.PlayerConstants;
import com.vianet.musicplayer.util.UtilFunctions;

import java.io.File;


public class SongsList extends Fragment implements SeekBar.OnSeekBarChangeListener {

    //    private String JSON_URL_SONG = "http://lawwizard.in/appAdmin/API/webservices.php?action=Mp3Song&albumId=1";//+SONG_URL_ID;
    int tabNum, albumNum;

    public SongsList() {
        // Required empty public constructor
    }

    private static final String ARG_TAB_NUMBER = "tab_number";
    private static final String ARG_ALBUM_NUMBER = "album_number";

    public static SongsList newInstance(int tabNum, int albumNum) {
        SongsList fragment = new SongsList();
        Bundle args = new Bundle();
        args.putInt(ARG_TAB_NUMBER, tabNum);
        args.putInt(ARG_ALBUM_NUMBER, albumNum);
        fragment.setArguments(args);
        return fragment;
    }
//    public SongsList(int tabNum, int albumNum){
//        this.tabNum = tabNum;
//        this.albumNum = albumNum;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    View view;
    SongAdapter customAdapter = null;
    //    public LinearLayout linearLayout;
    NetworkImageView albumPreview;
    ProgressBar progress;
    TextView textAlbum, textLabel;
    ListView listView;
    static ImageLoader imageLoader;

    //    public static ArrayList<MediaItem> SONGS_LIST_TO_PLAY;
//    public static String SONG_URL_ID;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_album_song_list, container, false);
        tabNum = getArguments().getInt(ARG_TAB_NUMBER);
        albumNum = getArguments().getInt(ARG_ALBUM_NUMBER);
//        SONGS_LIST_TO_PLAY = UtilFunctions.listOfSongs();
        albumPreview = view.findViewById(R.id.albumPreview);
        textAlbum = view.findViewById(R.id.textView9);
        textLabel = view.findViewById(R.id.textView10);
        progress = view.findViewById(R.id.progressBar3);
        imageLoader = MySingleton.getInstance(getContext()).getImageLoader();
        if (tabNum == 0 || tabNum == 1) {
            albumPreview.setImageUrl(LandingPage.albumCount.get(tabNum).get(albumNum).get(1).get(0), imageLoader);
//            albumPreview.setScaleType(ImageView.ScaleType.FIT_XY);
            textAlbum.setText(LandingPage.albumCount.get(tabNum).get(albumNum).get(5).get(0));
        } else if (tabNum == 2) {
            albumPreview.setImageUrl(JsonParse1.thumbnail[albumNum], imageLoader);
//            albumPreview.setScaleType(ImageView.ScaleType.FIT_XY);
            textAlbum.setText(JsonParse1.album[albumNum]);
        } else if (tabNum == 3) {
            albumPreview.setDefaultImageResId(R.drawable.default_album_art);
            textAlbum.setText("Offline Songs");
        }
//        if (tabNum == 1){
//            SONG_URL_ID = LandingPage1.id1.get(albumNum);
////            Toast.makeText(getContext(), ""+SONG_URL_ID, Toast.LENGTH_SHORT).show();
//            textAlbum.setText(LandingPage1.category1.get(albumNum)+" | "+ LandingPage1.label1.get(albumNum));
////            textLabel.setText(LandingPage1.label1.get(albumNum));
//        }else if (tabNum == 2){
//            SONG_URL_ID = LandingPage1.id2.get(albumNum);
//            textAlbum.setText(LandingPage1.category2.get(albumNum)+" | "+ LandingPage1.label2.get(albumNum));
////            textLabel.setText(LandingPage1.label2.get(albumNum));
//        }else if (tabNum == 3){
//            SONG_URL_ID = LandingPage1.id3.get(albumNum);
//            textAlbum.setText(LandingPage1.category3.get(albumNum)+" | "+ LandingPage1.label3.get(albumNum));
////            textLabel.setText(LandingPage1.label3.get(albumNum));
//        }else if (tabNum == 4){
//            SONG_URL_ID = LandingPage1.id4.get(albumNum);
//            textAlbum.setText(LandingPage1.category4.get(albumNum)+" | "+ LandingPage1.label4.get(albumNum));
////            textLabel.setText(LandingPage1.label4.get(albumNum));
//        }else if (tabNum == 5){
//            SONG_URL_ID = LandingPage1.id5.get(albumNum);
//            textAlbum.setText(LandingPage1.category5.get(albumNum)+" | "+ LandingPage1.label5.get(albumNum));
////            textLabel.setText(LandingPage1.label5.get(albumNum));
//        }else if (tabNum == 6){
//            SONG_URL_ID = LandingPage1.id6.get(albumNum);
//            textAlbum.setText(LandingPage1.category6.get(albumNum)+" | "+ LandingPage1.label6.get(albumNum));
////            textLabel.setText(LandingPage1.label6.get(albumNum));
//        }else if (tabNum == 7){
//            SONG_URL_ID = LandingPage1.id7.get(albumNum);
//            textAlbum.setText(LandingPage1.category7.get(albumNum)+" | "+ LandingPage1.label7.get(albumNum));
////            textLabel.setText(LandingPage1.label7.get(albumNum));
//        }else if (tabNum == 8){
//            SONG_URL_ID = LandingPage1.id8.get(albumNum);
//            textAlbum.setText(LandingPage1.category8.get(albumNum)+" | "+ LandingPage1.label8.get(albumNum));
////            textLabel.setText(LandingPage1.label8.get(albumNum));
//        }else if (tabNum == 9){
//            SONG_URL_ID = LandingPage1.id9.get(albumNum);
//            textAlbum.setText(LandingPage1.category9.get(albumNum)+" | "+ LandingPage1.label9.get(albumNum));
////            textLabel.setText(LandingPage1.label9.get(albumNum));
//        }
////        Toast.makeText(getContext(), ""+SONG_URL_ID, Toast.LENGTH_SHORT).show();
//        sendRequest(getContext(), "http://lawwizard.in/appAdmin/API/webservices.php?action=Mp3Song&albumId="+SONG_URL_ID);
        listView = view.findViewById(R.id.songList);

        progress.setVisibility(View.VISIBLE);

        init();
//        setBackground();
        seekBarProgress();
        setList();
//        setList();
        return view;
    }

    public void setList() {
//        Toast.makeText(getContext(), ""+ParseJSONSong.songName[0], Toast.LENGTH_SHORT).show();
        customAdapter = new SongAdapter(getContext(), R.layout.custom_list, UtilFunctions.listOfSongs(tabNum, albumNum));
        progress.setVisibility(View.INVISIBLE);
        if (tabNum == 0 || tabNum == 1) {
            textLabel.setText("Total Tracks : " + LandingPage.albumCount.get(tabNum).get(albumNum).get(0).size());
        } else if (tabNum == 2) {
            textLabel.setText("Total Tracks : 1");
        } else if (tabNum == 3) {
            File mydir = context.getDir("luitNew", Context.MODE_PRIVATE);
            if (!mydir.exists()) {
                textLabel.setText("Total Tracks : 0");
                Toast.makeText(context, "No Offline Song", Toast.LENGTH_SHORT).show();
            } else {
                File list[] = mydir.listFiles();
                textLabel.setText("Total Tracks : " + list.length);
            }
        }
//        listView.setBackground(getResources().getDrawable(R.drawable.default_album_art_thumb));
        listView.setAdapter(customAdapter);
//        listView.setFastScrollEnabled(true);
//        listView.setScrollingCacheEnabled(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                linearLayoutPlayingSong.setVisibility(View.VISIBLE);
                if (UtilFunctions.listOfSongs.size() > position) {
                    progressBarSong.setVisibility(View.VISIBLE);
                    PlayerConstants.SONGS_LIST = UtilFunctions.listToPlay();
                    PlayerConstants.SONG_PAUSED = false;
                    PlayerConstants.SONG_NUMBER = position;
                    boolean isServiceRunning = UtilFunctions.isServiceRunning(SongService.class.getName(), getContext());
                    if (!isServiceRunning) {
                        Intent i = new Intent(getContext(), SongService.class);
                        getActivity().startService(i);
                    } else {
                        PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
                    }
                    updateUI();
                    changeButton();
                }

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        seekBarProgress();

        if (!UtilFunctions.isServiceRunning(SongService.class.getName(), getContext())) {
            linearLayoutPlayingSong.setVisibility(View.INVISIBLE);
        }
    }

    private void init() {
        getViews();
        setListeners();
        playingSong.setSelected(true);
//        if(PlayerConstants.SONGS_LIST.size() <= 0){
//            PlayerConstants.SONGS_LIST = UtilFunctions.listToPlay();
//        }

    }

    static TextView playingSong;
    Button btnPlayer;
    static Button btnPause, btnPlay, btnNext, btnPrevious;
    Button btnStop;
    public static LinearLayout linearLayoutPlayingSong;
    SeekBar seekBar;
    TextView textBufferDuration, textDuration;
    static NetworkImageView imageViewAlbumArt;
    public static Context context;
    public static ProgressBar progressBarSong;

    private void getViews() {
        context = getContext();
        progressBarSong = view.findViewById(R.id.progressBar7);
        progressBarSong.setVisibility(View.INVISIBLE);
        playingSong = view.findViewById(R.id.textNowPlaying);
        btnPlayer = view.findViewById(R.id.btnMusicPlayer);
        btnPause = view.findViewById(R.id.btnPause);
        btnPlay = view.findViewById(R.id.btnPlay);
        linearLayoutPlayingSong = view.findViewById(R.id.linearLayoutPlayingSong);
        seekBar = view.findViewById(R.id.seekBar);
        btnStop = view.findViewById(R.id.btnStop);
        textBufferDuration = view.findViewById(R.id.textBufferDuration);
        textDuration = view.findViewById(R.id.textDuration);
        imageViewAlbumArt = view.findViewById(R.id.imageViewAlbumArt);
        btnNext = view.findViewById(R.id.btnNext);
        btnPrevious = view.findViewById(R.id.btnPrevious);
        seekBar.setOnSeekBarChangeListener(this);
        boolean isServiceRunning = UtilFunctions.isServiceRunning(SongService.class.getName(), getContext());
        if (!isServiceRunning) {
            linearLayoutPlayingSong.setVisibility(View.INVISIBLE);
        } else {
            changeUI();
            View view = new View(getContext());
            view.setMinimumHeight(250);
            listView.addFooterView(view);
        }
    }


    private void setListeners() {

        btnPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), AudioPlayerActivity.class);
                startActivity(i);
            }
        });
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controls.playControl(getContext());
            }
        });
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controls.pauseControl(getContext());
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                progressBarSong.setVisibility(View.VISIBLE);
                Controls.nextControl(getContext());
            }
        });
        btnPrevious.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                progressBarSong.setVisibility(View.VISIBLE);
                Controls.previousControl(getContext());
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), SongService.class);
                context.stopService(i);
                linearLayoutPlayingSong.setVisibility(View.GONE);
                PlayerConstants.PROGRESSBAR_HANDLER.removeCallbacks(new Runnable() {
                    @Override
                    public void run() {
                        seekBarProgress();
                    }
                });
            }
        });
        imageViewAlbumArt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), AudioPlayerActivity.class);
                startActivity(i);
            }
        });
//        linearLayoutPlayingSong.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(AudioActivity.this,AudioPlayerActivity.class);
//                startActivity(i);
//            }
//        });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        PlayerConstants.PROGRESSBAR_HANDLER.removeCallbacks(new Runnable() {
            @Override
            public void run() {
                seekBarProgress();
            }
        });
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        PlayerConstants.PROGRESSBAR_HANDLER.removeCallbacks(new Runnable() {
            @Override
            public void run() {
                seekBarProgress();
            }
        });
        if (SongService.mediaPlayer != null) {
            int totalDuration = SongService.mediaPlayer.getDuration();
            int currentPosition = UtilFunctions.progressToTimer(seekBar.getProgress(), totalDuration);

            // forward or backward to certain seconds
            SongService.mediaPlayer.seekTo(currentPosition);
        }

        // update timer progress again
//        updateProgressBar();
        seekBarProgress();
    }

    public void seekBarProgress() {
        PlayerConstants.PROGRESSBAR_HANDLER = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Integer i[] = (Integer[]) msg.obj;
                textBufferDuration.setText(UtilFunctions.getDuration(i[0]));
//                Toast.makeText(getContext(), ""+UtilFunctions.getDuration(i[1]), Toast.LENGTH_SHORT).show();
                textDuration.setText(UtilFunctions.getDuration(i[1]));
                seekBar.setProgress(i[2]);
            }
        };
    }

    public static void changeButton() {
        if (PlayerConstants.SONG_PAUSED) {
            btnPause.setVisibility(View.GONE);
            btnPlay.setVisibility(View.VISIBLE);
        } else {
            btnPause.setVisibility(View.VISIBLE);
            btnPlay.setVisibility(View.GONE);
        }
    }

    public static void changeUI() {
        updateUI();
        changeButton();
    }

    @SuppressWarnings("deprecation")
    public static void updateUI() {
        try {
            MediaItem data = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER);

            playingSong.setText(data.getTitle() + " " + data.getArtist() + "-" + data.getAlbum());
//            Bitmap albumArt = UtilFunctions.getAlbumart(context, data.getAlbumIdOffline());
            if (data.getAlbumIdOffline() != null) {
                imageViewAlbumArt.setBackgroundDrawable(new BitmapDrawable(data.getAlbumIdOffline()));
            } else {
//                imageViewAlbumArt.setBackgroundDrawable(new BitmapDrawable(UtilFunctions.getDefaultAlbumArt(context)));
                imageViewAlbumArt.setImageUrl(data.getAlbumId(), imageLoader);
                imageViewAlbumArt.setScaleType(ImageView.ScaleType.FIT_XY);
            }
            linearLayoutPlayingSong.setVisibility(View.VISIBLE);
        } catch (Exception e) {
        }
    }
}
