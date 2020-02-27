package com.vianet.musicplayer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.vianet.musicplayer.adapters.MySingleton;
import com.vianet.musicplayer.controls.Controls;
import com.vianet.musicplayer.service.SongService;
import com.vianet.musicplayer.util.PlayerConstants;
import com.vianet.musicplayer.util.UtilFunctions;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AudioPlayerActivity extends Activity implements SeekBar.OnSeekBarChangeListener{

	Button btnBack;
	static Button btnPause;
	Button btnNext;
	static Button btnPlay;
    ImageButton btnRepeat;
    public ImageButton btnShuffle;
    public static ImageButton downloadSong;
	static TextView textNowPlaying;
	static TextView textAlbumArtist;
	static TextView textComposer;
	static FrameLayout linearLayoutPlayer;
    static NetworkImageView networkImageView;
	public static ProgressBar progressBar;
    ProgressBar progressDownload;
    SeekBar seekBar;
	static AudioPlayerActivity context;
	TextView textBufferDuration, textDuration;
    public static boolean isShuffle = false;
    public static boolean isRepeat = false;
    static boolean isCreated = false;
//    private NotificationManager mNotifyManager;
//    private NotificationCompat.Builder build;
//    private int id = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getActionBar().hide();
		setContentView(R.layout.audio_player);
		context = this;
        isCreated = true;
		init();
        downloadSong.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                mNotifyManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
//                build = new NotificationCompat.Builder(getBaseContext());
//                build.setContentTitle("Download: "+ PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getTitle())
//                        .setContentText("Download in progress")
//                        .setSmallIcon(android.R.drawable.stat_sys_download_done);
                final DownloadTask downloadTask = new DownloadTask(AudioPlayerActivity.this);
                downloadTask.execute(PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getPath());
//                startService(new Intent(getBaseContext(), DownloadService.class));
            }
        });
	}

    public void seekBarProgress(){
        PlayerConstants.PROGRESSBAR_HANDLER = new Handler(){
            @Override
            public void handleMessage(Message msg){
                Integer i[] = (Integer[])msg.obj;
                textBufferDuration.setText(UtilFunctions.getDuration(i[0]));
                textDuration.setText(UtilFunctions.getDuration(i[1]));
                seekBar.setProgress(i[2]);
            }
        };
    }

	private void init() {
		getViews();
		setListeners();
//		progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.white), Mode.SRC_IN);

		seekBarProgress();
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

            // update timer progress again
//        updateProgressBar();
            seekBarProgress();
        }
    }

	private void setListeners() {
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				progressBar.setVisibility(View.VISIBLE);
                Controls.previousControl(getApplicationContext());
			}
		});
		
		btnPause.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Controls.pauseControl(getApplicationContext());
			}
		});
		
		btnPlay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Controls.playControl(getApplicationContext());
			}
		});
		
		btnNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				progressBar.setVisibility(View.VISIBLE);
                Controls.nextControl(getApplicationContext());
			}
		});

        btnRepeat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (isRepeat) {
                    isRepeat = false;
                    Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
                    btnRepeat.setImageResource(R.drawable.btn_repeat);
                } else {
                    // make repeat to true
                    isRepeat = true;
                    Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isShuffle = false;
                    btnRepeat.setImageResource(R.drawable.repeatpressed);
                    btnShuffle.setImageResource(R.drawable.btn_shuffle);
                }
            }
        });

        btnShuffle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isShuffle){
                    isShuffle = false;
                    Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
                    btnShuffle.setImageResource(R.drawable.btn_shuffle);
                }else{
                    // make repeat to true
                    isShuffle= true;
                    Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isRepeat = false;
                    btnShuffle.setImageResource(R.drawable.shufflepressed);
                    btnRepeat.setImageResource(R.drawable.btn_repeat);
                }
            }
        });
	}
	
	public static void changeUI(){
		updateUI();
		changeButton();
	}
	
	private void getViews() {
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
		btnBack = findViewById(R.id.btnBack);
		btnPause = findViewById(R.id.btnPause);
		btnNext = findViewById(R.id.btnNext);
		btnPlay = findViewById(R.id.btnPlay);
        btnRepeat = findViewById(R.id.imageButton);
        btnShuffle = findViewById(R.id.imageButton2);
        if (isRepeat){
            btnRepeat.setImageResource(R.drawable.repeatpressed);
        }else if (isShuffle){
            btnShuffle.setImageResource(R.drawable.shufflepressed);
        }
        downloadSong = findViewById(R.id.downloadSong);

		textNowPlaying = findViewById(R.id.textNowPlaying);
		linearLayoutPlayer = findViewById(R.id.linearLayoutPlayer);
        networkImageView = findViewById(R.id.netBackground);
		textAlbumArtist = findViewById(R.id.textAlbumArtist);
		textComposer = findViewById(R.id.textComposer);
		progressBar = findViewById(R.id.progressBar);
        progressDownload = findViewById(R.id.progressBar2);
        progressDownload.setVisibility(View.GONE);
        if (SongService.mediaPlayer != null) {
            progressBar.setVisibility(View.GONE);
        }else {
            progressBar.setVisibility(View.VISIBLE);
        }
        seekBar = findViewById(R.id.seekBar2);
        seekBar.setOnSeekBarChangeListener(this);
		textBufferDuration = findViewById(R.id.textBufferDuration);
		textDuration = findViewById(R.id.textDuration);
		textNowPlaying.setSelected(true);
		textAlbumArtist.setSelected(true);

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		boolean isServiceRunning = UtilFunctions.isServiceRunning(SongService.class.getName(), getApplicationContext());
		if (isServiceRunning) {
			updateUI();
		}
		changeButton();
	}
	
	public static void changeButton() {
		if(PlayerConstants.SONG_PAUSED){
			btnPause.setVisibility(View.GONE);
			btnPlay.setVisibility(View.VISIBLE);
		}else{
			btnPause.setVisibility(View.VISIBLE);
			btnPlay.setVisibility(View.GONE);
		}
	}
	
	private static void updateUI() {
        if (PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getPath().contains("luitNew")){
            downloadSong.setVisibility(View.GONE);
        }else {
            downloadSong.setVisibility(View.VISIBLE);
        }
        File mydir = context.getDir("luitNew", Context.MODE_PRIVATE);
        if (mydir.exists()){
            boolean flag = false;
            File[] file = mydir.listFiles();
            for (File f : file){
                if (f.getName().equalsIgnoreCase(extractFilename())){
                    downloadSong.setImageResource(R.drawable.downloaded);
                    downloadSong.setEnabled(false);
                    flag = true;
                    break;
                }
            }
            if (!flag){
                downloadSong.setImageResource(R.drawable.download);
                downloadSong.setEnabled(true);
            }
        }
		try{
			String songName = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getTitle();
			String artist = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getArtist();
			String album = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getAlbum();
			String composer = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getComposer();
			textNowPlaying.setText(songName);
			textAlbumArtist.setText(artist + " - " + album);
			if(composer != null && composer.length() > 0){
				textComposer.setVisibility(View.VISIBLE);
				textComposer.setText(composer);
			}else{
				textComposer.setVisibility(View.GONE);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			String albumId = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getAlbumId();
            Bitmap albumIdOffline = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getAlbumIdOffline();
//			Bitmap albumArt = UtilFunctions.getAlbumart(context, albumId);
			if(albumId != null && isCreated){
//				linearLayoutPlayer.setBackgroundDrawable(new BitmapDrawable(albumArt));
//                new LoadBackground(linearLayoutPlayer, albumId, "android").execute();
                ImageLoader imageLoader = MySingleton.getInstance(context).getImageLoader();
                networkImageView.setImageUrl(albumId, imageLoader);
                networkImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			}else if (albumId == null && albumIdOffline != null){
//                Bitmap albumArt = UtilFunctions.getAlbumart(context, albumIdOffline);
                linearLayoutPlayer.setBackgroundDrawable(new BitmapDrawable(albumIdOffline));
            }
            else{
				linearLayoutPlayer.setBackgroundDrawable(new BitmapDrawable(UtilFunctions.getDefaultAlbumArt(context)));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}


    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
//        private PowerManager.WakeLock mWakeLock;
        String fileName;
        public DownloadTask(Context context) {
            this.context = context;
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
                fileName = extractFilename();
                File file = new File(mydir, fileName);
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
//                    if (fileLength > 0) // only if total length is known
//                        publishProgress((int) (total * 100 / fileLength));
                    if (isNetworkAvailable()) {
                        output.write(data, 0, count);
                    }else {
                        output.flush();
                        return "a";
                    }
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
//            build.setProgress(100, 0, false);
//            mNotifyManager.notify(id, build.build());
            downloadSong.setEnabled(false);
            progressDownload.setVisibility(View.VISIBLE);
            Toast.makeText(AudioPlayerActivity.this, "Start Downloading...", Toast.LENGTH_SHORT).show();
        }

//        @Override
//        protected void onProgressUpdate(Integer... progress) {
//            super.onProgressUpdate(progress);
//            // if we get here, length is known, now set indeterminate to false
////            build.setProgress(100, progress[0], false);
////            mNotifyManager.notify(id, build.build());
////            super.onProgressUpdate(progress);
//        }

        @Override
        protected void onPostExecute(String result) {
//            mWakeLock.release();
//            build.setContentText("Download complete");
            // Removes the progress bar
//            build.setProgress(0, 0, false);
//            mNotifyManager.notify(id, build.build());
            progressDownload.setVisibility(View.GONE);
            if (result != null){
                File mydir = context.getDir("luitNew", Context.MODE_PRIVATE);
                File[] files = mydir.listFiles();
                for (File f : files){
                    if (f.getName().equalsIgnoreCase(fileName)){
                        f.delete();
                        break;
                    }
                }
                Toast.makeText(context, "Network Error: Download incomplete", Toast.LENGTH_SHORT).show();
                downloadSong.setEnabled(true);
            }
//            else if (result == null) {
//                Toast.makeText(context, "Download error: try again.", Toast.LENGTH_LONG).show();
//                downloadSong.setEnabled(true);
//            }
            else {
                Toast.makeText(context, "Song downloaded", Toast.LENGTH_SHORT).show();
                downloadSong.setImageResource(R.drawable.downloaded);
                downloadSong.setEnabled(false);
            }
        }
    }

    private static String extractFilename(){
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

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
