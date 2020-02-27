package com.vianet.musicplayer;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.vianet.musicplayer.dataloader.JsonParseVideo;

import java.io.File;

public class YoutubePlayer extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener{
    public static TextView t1,t2;
    int sc = 0, mm = 0, hh = 0;
    dotime task;
    public static SeekBar sk;
    ImageButton im,im3;
    static boolean boolb=false;
    public static int tyu;
    YouTubePlayerView youTubePlayerView;
    public static final String API_KEY = "AIzaSyAznLIu7lYME1302tBfSPfsU_HOkqJpcaw";

    public static int duration;
//    public static String P_ID = "";
    static int clickPosition;
    int albumNum;
    static int tabPosition;


    public void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if(appDir.exists()){
            String[] children = appDir.list();
            for(String s : children){
                if(!s.equals("lib")){
                    deleteDir(new File(appDir, s));
                    Log.i("TAG", "File /data/data/com.Total.Aastha/" + s + " DELETED");
                }
            }
        }
    }

    public boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }

        return dir != null && dir.delete();
    }

    public void viewme()
    {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        clearApplicationData();
        viewme();

        im3=(ImageButton)findViewById(R.id.imageButton3);
        im=(ImageButton)findViewById(R.id.imageButton2);
        sk=(SeekBar)findViewById(R.id.seekBar);

//        im.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(bool==false) {
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                    bool=true;
//                }
//                else
//                {
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                    bool=false;
//                }
//            }
//        });

        im3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (boolb == false) {
                    player1.pause();
                    boolb = true;
                } else {

                    player1.play();
                    boolb = false;
                }
            }
        });

        //  pb.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        t1=(TextView)findViewById(R.id.t1);
        t2=(TextView)findViewById(R.id.t2);
        t1.setText("00:00");
        t2.setText("00:00");
        youTubePlayerView = (YouTubePlayerView)findViewById(R.id.youtubeplayerview);
//        Intent i = this.getIntent();
//        VIDEO_ID = i.getStringExtra("videoid");
//        P_ID = i.getStringExtra("pid");

        Bundle bundle = getIntent().getExtras();
        albumNum = bundle.getInt("albumNum");
        tabPosition = bundle.getInt("tabPosition");
        clickPosition = bundle.getInt("clickPosition");

        youTubePlayerView.initialize(API_KEY, this);

        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onStopTrackingTouch(SeekBar bar) {
//                int value = bar.getProgress(); // the value of the seekBar progress
                player1.seekToMillis(bar.getProgress());
                hh=(player1.getCurrentTimeMillis()/(1000*60*60));
                sc=((player1.getCurrentTimeMillis()/(1000))%60);
                mm=((player1.getCurrentTimeMillis()/(1000*60))%60);
            }

            public void onStartTrackingTouch(SeekBar bar) {

            }

            public void onProgressChanged(SeekBar bar,
                                          int paramInt, boolean paramBoolean) {
//                textView.setText("" + paramInt + "%"); // here in textView the percent will be shown
            }
        });
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult result) {
//        Toast.makeText(getApplicationContext(),
//                "onInitializationFailure()",
//                Toast.LENGTH_LONG).show();

        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.youtube"));
        try {
            startActivity(intent);
        }catch (ActivityNotFoundException e){
            Toast.makeText(getBaseContext(), "Please Retry..", Toast.LENGTH_SHORT).show();
        }

    }

    static YouTubePlayer player1;

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                        boolean wasRestored) {

        player.setPlayerStateChangeListener(playerStateChangeListener);
        player.setPlaybackEventListener(playbackEventListener);
        player1 = player;


        if (!wasRestored) {

            if (tabPosition != 2) {
                if (LandingPage.albumCount.get(tabPosition).get(albumNum).get(1).get(clickPosition) != null) {
//                Toast.makeText(this, ""+ videoFileID, Toast.LENGTH_SHORT).show();
                    player.loadVideo(LandingPage.albumCount.get(tabPosition).get(albumNum).get(1).get(clickPosition));

                }else {
                    Toast.makeText(this, "Video format is not supported.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            else{
                player.loadVideo(JsonParseVideo.ytId[clickPosition]);
            }


            player.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);


        }
    }

    @Override
    public void onBackPressed() {

//        player1.seekToMillis(player1.getDurationMillis());
//        h1.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                finish();
//            }
//        },100);
//Thread.currentThread().interrupt();
////        Thread.currentThread().set;
        if(task!=null)
            task.cancel(true);
        if(player1!=null)
            player1.pause();

//        if(player1.isPlaying())
//        {
//            player1.pause();
//        }
//        else {

//        }
//super.onPause();
//startActivity(new Intent(getBaseContext(),web.class));
        finish();
        super.onBackPressed();
//this.finish();
//        Intent back=new Intent((Intent.ACTION_MAIN));
//        back.addCategory(Intent.CATEGORY_HOME);
//        back.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(back);
    }

    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {

        @Override
        public void onBuffering(boolean arg0) {
//            viewme();
//            player1.play();
//            Toast.makeText(getBaseContext(),"on buffer"+arg0,Toast.LENGTH_SHORT).show();
            if(arg0==true)
                tyu=1;
            else
                tyu=0;
        }

        @Override
        public void onPaused() {
            im3.setImageResource(R.drawable.pp);
//player1.play();
            tyu=1;
//timep=1;
            //task=null;
            // onStopped();
//            Toast.makeText(getBaseContext(),"pause",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPlaying() {
            im3.setImageResource(R.drawable.pa);
//            Toast.makeText(getBaseContext(),"play"+Thread.activeCount(),Toast.LENGTH_SHORT).show();

            tyu=0;

//            MyCount dd=new MyCount(player1.getCurrentTimeMillis(),(player1.getDurationMillis()-player1.getCurrentTimeMillis()));
//dd.start();

        }

        @Override
        public void onSeekTo(int arg0) {
            // player1.seekToMillis(100);

//            Toast.makeText(getBaseContext(),"seek",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStopped() {
            viewme();
//            if(adv==0)
//            player1.play();
//tthj.interrupt();
//tthj=null;
//            Toast.makeText(getBaseContext(),"stop",Toast.LENGTH_SHORT).show();

        }

    };

    int adv=0;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewme();
        if (player1 != null) {
            player1.release();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        viewme();
        onDestroy();
    }

    @Override
    protected void onResume() {

        super.onResume();
        viewme();
    }

    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {


        @Override
        public void onAdStarted() {
            adv=1;
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason arg0) {
        }

        @Override
        public void onLoaded(String arg0) {
//            Toast.makeText(getBaseContext(),"loaded",Toast.LENGTH_SHORT).show();
//tyu=0;

        }

        @Override
        public void onLoading() {
//            Toast.makeText(getBaseContext(),"loading",Toast.LENGTH_SHORT).show();
//tyu=1;
        }

        @Override
        public void onVideoEnded() {
//            Toast.makeText(getBaseContext(),"ended",Toast.LENGTH_SHORT).show();
            tyu = 1;
            if (tabPosition != 2) {
                clickPosition++;
                if (clickPosition < LandingPage.albumCount.get(tabPosition).get(albumNum).get(1).size()) {
                    task.cancel(true);
                    resetTime();
                    player1.loadVideo(LandingPage.albumCount.get(tabPosition).get(albumNum).get(1).get(clickPosition));
//                      youTubePlayerView.initialize(API_KEY, YoutubePlayer.this);

                } else {
                    clickPosition = 0;
                    try {
                        task.cancel(true);
                        resetTime();
//                          videoFileID = LoadingPageVideo.tabCount1.get(tabPosition).get(5).get(clickPosition);
                        player1.loadVideo(LandingPage.albumCount.get(tabPosition).get(albumNum).get(1).get(clickPosition));
                    } catch (Exception e) {
                        youTubePlayerView.initialize(API_KEY, YoutubePlayer.this);
                    }
                }
            }else {
                clickPosition++;
                if (clickPosition < JsonParseVideo.ytId.length){
                    task.cancel(true);
                    resetTime();
                    player1.loadVideo(JsonParseVideo.ytId[clickPosition]);
                }else {
                    clickPosition = 0;
                    task.cancel(true);
                    resetTime();
                    player1.loadVideo(JsonParseVideo.ytId[clickPosition]);
                }
            }
        }

        @Override
        public void onVideoStarted() {
            adv=0;
            task = (dotime)new dotime().execute();
            int mm1=0,hh1=0,sc1=0;
            hh1=(player1.getDurationMillis()/(1000*60*60));
            sc1=((player1.getDurationMillis()/(1000))%60);
            mm1=((player1.getDurationMillis()/(1000*60))%60);
            sk.setMax(player1.getDurationMillis());
            if(hh1==0) {
                if (mm1 < 10 && sc1 < 10) {
                    t2.setText("0" + mm1 + ":0" + sc1);
                }else if (mm1 > 10 && sc1 < 10){
                    t2.setText("" + mm1 + ":0" + sc1);
                } else if (mm1 < 10 && sc1 > 10){
                    t2.setText("0" + mm1 + ":" + sc1);
                }
            }
            else
                t2.setText(""+hh1+":"+mm1+":"+sc1);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        viewme();
    }

    public static int i=0;
    private class dotime extends AsyncTask<Void,Integer,Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {

            for(i=(player1.getCurrentTimeMillis())/(1000);i<(player1.getDurationMillis())/(1000);i++)
            {
//    ty1.setText(""+i);

                if(task.isCancelled())
                    break;
                try{
                    Thread.sleep(1000);
                    publishProgress(i);

                }catch(Exception e)
                {

                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
//            super.onProgressUpdate(values);

            if (tyu==0) {
                if (sc < 60) {
                    sc++;
                } else if (mm < 60) {
                    sc = 1;
                    mm = mm + 1;
                } else {
                    sc = 1;
                    mm = 0;
                    hh++;
                }
                if(hh == 0) {
                    if (sc < 10 && mm < 10)
                        t1.setText("0" + mm + ":0" + sc);
                    else if (mm > 10 && sc < 10)
                        t1.setText("" + mm + ":0" + sc);
                    else if (mm < 10 && sc > 10)
                        t1.setText("0" + mm + ":" + sc);
                }
                else
                    t1.setText(""+hh+":" + mm + ":" + sc);
                sk.setProgress(player1.getCurrentTimeMillis());

            }

        }

    }

    private void resetTime(){
        sc = 0;
        mm = 0;
        hh = 0;
    }
}