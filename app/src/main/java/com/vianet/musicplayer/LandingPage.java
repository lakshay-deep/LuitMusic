package com.vianet.musicplayer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.vianet.musicplayer.dataloader.JsonParse1;
import com.vianet.musicplayer.dataloader.JsonParse2;
import com.vianet.musicplayer.dataloader.JsonParseVideo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LandingPage extends AppCompatActivity {

    private String url_welcome = "http://104.251.217.194/luit/API/webservices.php?action=welcometext";
    private String url_mp3 = "http://104.251.217.194/luit/API/webservices.php?action=luitMp3";
    private String url_video = "http://104.251.217.194/luit/API/webservices.php?action=luitVideos";
    TextView textView;
    ProgressBar progressBar;
    ImageView imageView;
    Button audioButton, videoButton;
    InterstitialAd mInterstitialAd;
    int flag;

    public static List<List<List<List<String>>>> albumCount = new ArrayList<>();
    public static ArrayList<String> album = new ArrayList<>();
    public static ArrayList<String> artist = new ArrayList<>();
    public static ArrayList<String> albumThumb = new ArrayList<>();
    public static ArrayList<String> artistThumb = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_landing_page);
        deleteCache(getBaseContext());
        imageView = findViewById(R.id.imageView2);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        textView = findViewById(R.id.textView2);
//        textView.setText(Html.fromHtml(JsonParse2.ids[0]));
        mInterstitialAd = new InterstitialAd(LandingPage.this);
        mInterstitialAd.setAdUnitId("ca-app-pub-9835050435008441/5362447411");
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();
        mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
//                requestNewInterstitial();
//                sendRequest(JSON_URL_VIDEO_TAB, 0);
                if (flag == 0) {
                    startActivity(new Intent(getBaseContext(), VideoActivity.class));
                } else {
                    startActivity(new Intent(getBaseContext(), AudioActivity.class));
                }
                finish();
            }
        });
        videoButton = findViewById(R.id.video);
        videoButton.setVisibility(View.GONE);
        audioButton = findViewById(R.id.Audia);
        audioButton.setVisibility(View.GONE);
        audioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (jsonPass) {
                    sendRequestcat(url_mp3, 0);
                    progressBar.setVisibility(View.VISIBLE);
//                        audioButton.setClickable(false);
                    videoButton.setVisibility(View.INVISIBLE);
                } else {
                    LandingPage.this.finish();
                    deleteCache(getBaseContext());
                }
            }
        });

        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (jsonPass) {
                    sendRequestcat(url_video, 2);
                    progressBar.setVisibility(View.VISIBLE);
//                    videoButton.setClickable(false);
                    audioButton.setVisibility(View.INVISIBLE);
                } else {
                    sendRequestcat(url_welcome, 1);
                    progressBar.setVisibility(View.VISIBLE);
                    videoButton.setVisibility(View.GONE);
                    audioButton.setVisibility(View.GONE);
                }
            }
        });
        if (isNetworkAvailable()) {
            sendRequestcat(url_welcome, 1);
        } else {
            myalert();
        }

    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_LONG).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    private void requestNewInterstitial(int a) {
//        AdRequest adRequest = new AdRequest.Builder()
//                //.addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
//                .build();
//        mInterstitialAd.loadAd(adRequest);
        flag = a;
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            if (a == 0) {
                startActivity(new Intent(getBaseContext(), VideoActivity.class));
            } else {
                startActivity(new Intent(getBaseContext(), AudioActivity.class));
            }
            finish();
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void myalert() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);

        // set title
        alertDialogBuilder.setTitle("Luit");

        // set dialog message
        alertDialogBuilder
                .setMessage("No Internet Found ! Check Your Connection.")
                .setCancelable(false)
                .setIcon(R.drawable.baab)
                .setPositiveButton("Go Offline", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
//                        LandingPage.this.finish();
                        Intent i = new Intent(getBaseContext(), AudioActivity.class);
                        i.putExtra("offline", 0);
                        startActivity(i);
                    }
                })
                .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        if (isNetworkAvailable()) {
                            try {
                                sendRequestcat(url_welcome, 1);
                            } catch (Exception e) {
                                Toast.makeText(getBaseContext(), "Data loading Exception", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            myalert();
                        }
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    public boolean jsonPass = true;

    private void sendRequestcat(String JSON_URL, final int id) {

        StringRequest stringRequest = new StringRequest(JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showJSON(response, id);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        jsonPass = false;
                        Toast.makeText(getBaseContext(), "Network error.", Toast.LENGTH_LONG).show();
                        audioButton.setText("Exit");
                        videoButton.setText("Retry");
                        progressBar.setVisibility(View.INVISIBLE);
                        audioButton.setVisibility(View.VISIBLE);
                        videoButton.setVisibility(View.VISIBLE);
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
        requestQueue.add(stringRequest);
    }

    private void showJSON(String json, int id) {

        if (id == 0) {
            JsonParse1 pj = new JsonParse1(json);
            pj.parseJSON();

            album.clear();
            artist.clear();
            albumCount.clear();
            albumThumb.clear();
            artistThumb.clear();
            boolean flagAlbum;
            boolean flagArtist;
            for (int i = 0; i < JsonParse1.album.length; i++) {
                flagAlbum = true;
                flagArtist = true;
                if (album.size() == 0) {
                    album.add(JsonParse1.album[i]);
                    albumThumb.add(JsonParse1.thumbnail[i]);
                } else {
                    for (int k = 0; k < album.size(); k++) {
                        if (album.get(k).equalsIgnoreCase(JsonParse1.album[i])) {
                            flagAlbum = false;
                            break;
                        }
                    }
                    if (flagAlbum) {
                        album.add(JsonParse1.album[i]);
                        albumThumb.add(JsonParse1.thumbnail[i]);
                    }
                }

                if (artist.size() == 0) {
                    artist.add(JsonParse1.artist[i]);
                    artistThumb.add(JsonParse1.thumbnail[i]);
                } else {
                    for (int k = 0; k < artist.size(); k++) {
                        if (artist.get(k).equalsIgnoreCase(JsonParse1.artist[i])) {
                            flagArtist = false;
                            break;
                        }
                    }
                    if (flagArtist) {
                        artist.add(JsonParse1.artist[i]);
                        artistThumb.add(JsonParse1.thumbnail[i]);
                    }
                }
            }

            for (int tab = 0; tab < 2; tab++) {
                List<List<List<String>>> tabData = new ArrayList<>();
                albumCount.add(tabData);
            }

            for (int l = 0; l < album.size(); l++) {
                List<List<String>> albumData = new ArrayList<>();
                albumCount.get(0).add(albumData);
                for (int k = 0; k < 6; k++) {
                    List<String> data = new ArrayList<>();
                    albumCount.get(0).get(l).add(data);
                    if (k == 5) {
                        for (int h = 0; h < JsonParse1.album.length; h++) {
                            if (album.get(l).equalsIgnoreCase(JsonParse1.album[h])) {
                                albumCount.get(0).get(l).get(0).add(JsonParse1.title[h]);
                                albumCount.get(0).get(l).get(1).add(JsonParse1.thumbnail[h]);
                                albumCount.get(0).get(l).get(2).add(JsonParse1.file[h]);
                                albumCount.get(0).get(l).get(3).add(JsonParse1.musi[h]);
                                albumCount.get(0).get(l).get(4).add(JsonParse1.producer[h]);
                                albumCount.get(0).get(l).get(5).add(JsonParse1.album[h]);
                                Log.d("showJSON: ", l+"");
                            }
                        }
                    }
                }
            }

            for (int g = 0; g < artist.size(); g++) {
                List<List<String>> albumData = new ArrayList<>();
                albumCount.get(1).add(albumData);
                for (int k = 0; k < 6; k++) {
                    List<String> data = new ArrayList<>();
                    albumCount.get(1).get(g).add(data);
                    if (k == 5) {
                        for (int h = 0; h < JsonParse1.album.length; h++) {
                            if (artist.get(g).equalsIgnoreCase(JsonParse1.artist[h])) {
                                albumCount.get(1).get(g).get(0).add(JsonParse1.title[h]);
                                albumCount.get(1).get(g).get(1).add(JsonParse1.thumbnail[h]);
                                albumCount.get(1).get(g).get(2).add(JsonParse1.file[h]);
                                albumCount.get(1).get(g).get(3).add(JsonParse1.musi[h]);
                                albumCount.get(1).get(g).get(4).add(JsonParse1.producer[h]);
                                albumCount.get(1).get(g).get(5).add(JsonParse1.artist[h]);
                            }
                        }
                    }
                }
            }

//            Toast.makeText(LandingPage.this, " "+albumCount.get(1).get(0).get(5).size(), Toast.LENGTH_SHORT).show();

//            audioButton.setVisibility(View.VISIBLE);

            progressBar.setVisibility(View.INVISIBLE);
            if (jsonPass) {
//                startActivity(new Intent(getBaseContext(), AudioActivity.class));
//                finish();
                requestNewInterstitial(1);
            } else {
                Toast.makeText(LandingPage.this, "NetWork Error..Try after re-launching the app.", Toast.LENGTH_SHORT).show();
            }

        } else if (id == 1) {


            JsonParse2 pj = new JsonParse2(json);
            pj.parseJSON();
            imageView.setBackgroundColor(getResources().getColor(android.R.color.white));
            textView.setText(Html.fromHtml(JsonParse2.ids[0]));
            jsonPass = true;
            audioButton.setText("Audio");
            videoButton.setText("Video");
            progressBar.setVisibility(View.INVISIBLE);
            audioButton.setVisibility(View.VISIBLE);
            videoButton.setVisibility(View.VISIBLE);
//            sendRequestcat("http://viduda.com/luit/API/webservices.php?action=luitMp3", 0);
        } else if (id == 2) {
            JsonParseVideo pV = new JsonParseVideo(json);
            pV.parseJSON();

            album.clear();
            artist.clear();
            albumCount.clear();
            boolean flagAlbum;
            boolean flagArtist;
            for (int i = 0; i < JsonParseVideo.album.length; i++) {
                flagAlbum = true;
                flagArtist = true;
                if (album.size() == 0) {
                    album.add(JsonParseVideo.album[i]);
                } else {
                    for (int k = 0; k < album.size(); k++) {
                        if (album.get(k).equalsIgnoreCase(JsonParseVideo.album[i])) {
                            flagAlbum = false;
                            break;
                        }
                    }
                    if (flagAlbum) {
                        album.add(JsonParseVideo.album[i]);
                    }
                }

                if (artist.size() == 0) {
                    artist.add(JsonParseVideo.artist[i]);
                } else {
                    for (int k = 0; k < artist.size(); k++) {
                        if (artist.get(k).equalsIgnoreCase(JsonParseVideo.artist[i])) {
                            flagArtist = false;
                            break;
                        }
                    }
                    if (flagArtist) {
                        artist.add(JsonParseVideo.artist[i]);
                    }
                }
            }

            for (int tab = 0; tab < 2; tab++) {
                List<List<List<String>>> tabData = new ArrayList<>();
                albumCount.add(tabData);
            }

            for (int l = 0; l < album.size(); l++) {
                List<List<String>> albumData = new ArrayList<>();
                albumCount.get(0).add(albumData);
                for (int k = 0; k < 4; k++) {
                    List<String> data = new ArrayList<>();
                    albumCount.get(0).get(l).add(data);
                    if (k == 3) {
                        for (int h = 0; h < JsonParseVideo.album.length; h++) {
                            if (album.get(l).equalsIgnoreCase(JsonParseVideo.album[h])) {
                                albumCount.get(0).get(l).get(0).add(JsonParseVideo.title[h]);
                                albumCount.get(0).get(l).get(1).add(JsonParseVideo.ytId[h]);
                                albumCount.get(0).get(l).get(2).add(JsonParseVideo.description[h]);
                                albumCount.get(0).get(l).get(3).add(JsonParseVideo.album[h]);
                            }
                        }
                    }
                }
            }
            for (int g = 0; g < artist.size(); g++) {
                List<List<String>> albumData = new ArrayList<>();
                albumCount.get(1).add(albumData);
                for (int k = 0; k < 4; k++) {
                    List<String> data = new ArrayList<>();
                    albumCount.get(1).get(g).add(data);
                    if (k == 3) {
                        for (int h = 0; h < JsonParseVideo.album.length; h++) {
                            if (artist.get(g).equalsIgnoreCase(JsonParseVideo.artist[h])) {
                                albumCount.get(1).get(g).get(0).add(JsonParseVideo.title[h]);
                                albumCount.get(1).get(g).get(1).add(JsonParseVideo.ytId[h]);
                                albumCount.get(1).get(g).get(2).add(JsonParseVideo.description[h]);
                                albumCount.get(1).get(g).get(3).add(JsonParseVideo.artist[h]);
                            }
                        }
                    }
                }
            }

            progressBar.setVisibility(View.INVISIBLE);
            if (jsonPass) {
//                Toast.makeText(this, ""+album.size(), Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(getBaseContext(), VideoActivity.class));
//                finish();
                requestNewInterstitial(0);
            } else {
                Toast.makeText(LandingPage.this, "NetWork Error..Try after re-launching the app.", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
