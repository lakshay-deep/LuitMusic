package com.vianet.musicplayer.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.RemoteControlClient;
import android.media.RemoteControlClient.MetadataEditor;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.vianet.musicplayer.AudioPlayerActivity;
import com.vianet.musicplayer.R;
import com.vianet.musicplayer.SongsList;
import com.vianet.musicplayer.controls.Controls;
import com.vianet.musicplayer.receiver.NotificationBroadcast;
import com.vianet.musicplayer.util.MediaItem;
import com.vianet.musicplayer.util.PlayerConstants;
import com.vianet.musicplayer.util.UtilFunctions;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class SongService extends Service implements AudioManager.OnAudioFocusChangeListener {

    private MediaPlayer mp;
    int NOTIFICATION_ID = 1111;
    public static final String NOTIFY_PREVIOUS = "com.vianet.musicplayer.receiver.previous";
    public static final String NOTIFY_DELETE = "com.vianet.musicplayer.receiver.delete";
    public static final String NOTIFY_PAUSE = "com.vianet.musicplayer.receiver.pause";
    public static final String NOTIFY_PLAY = "com.vianet.musicplayer.receiver.play";
    public static final String NOTIFY_NEXT = "com.vianet.musicplayer.receiver.next";

    private RemoteControlClient remoteControlClient;
    AudioManager audioManager;
    Bitmap mDummyAlbumArt;
    public static Timer timer;
    public MainTask mainTask;
    private static boolean currentVersionSupportBigNotification = false;
    private static boolean currentVersionSupportLockScreenControls = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mp = new MediaPlayer();
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        currentVersionSupportBigNotification = UtilFunctions.currentVersionSupportBigNotification();
        currentVersionSupportLockScreenControls = UtilFunctions.currentVersionSupportLockScreenControls();
        timer = new Timer();
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer1) {

                mp.start();
                SongsList.progressBarSong.setVisibility(View.GONE);
                if (AudioPlayerActivity.progressBar != null) {
                    AudioPlayerActivity.progressBar.setVisibility(View.GONE);
                }
                mediaPlayer = mp;
//            SongsList.progressBarSong.setVisibility(View.INVISIBLE);
                mainTask = new MainTask();
                timer.scheduleAtFixedRate(mainTask, 0, 100);
            }
        });

        mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
//                SongsList.progressBarSong.setVisibility(View.VISIBLE);
//                if (AudioPlayerActivity.progressBar != null) {
//                    AudioPlayerActivity.progressBar.setVisibility(View.VISIBLE);
//                }
                switch (i) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        SongsList.progressBarSong.setVisibility(View.VISIBLE);
                        if (AudioPlayerActivity.progressBar != null) {
                            AudioPlayerActivity.progressBar.setVisibility(View.VISIBLE);
                        }
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        SongsList.progressBarSong.setVisibility(View.GONE);
                        if (AudioPlayerActivity.progressBar != null) {
                            AudioPlayerActivity.progressBar.setVisibility(View.GONE);
                        }
                        break;
                }
                return false;

            }
        });

        mp.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                SongsList.progressBarSong.setVisibility(View.VISIBLE);
                if (AudioPlayerActivity.progressBar != null) {
                    AudioPlayerActivity.progressBar.setVisibility(View.VISIBLE);
                }
                if (AudioPlayerActivity.isRepeat) {
                    // repeat is on play same song again
                    MediaItem data = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER);
                    String songPath = data.getPath();
                    playSong(songPath, data);
                } else if (AudioPlayerActivity.isShuffle) {
                    // shuffle is on - play a random song
                    Random rand = new Random();
                    PlayerConstants.SONG_NUMBER = rand.nextInt((PlayerConstants.SONGS_LIST.size() - 1) + 1);
                    MediaItem data = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER);
                    String songPath = data.getPath();
                    PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
                    playSong(songPath, data);
                } else {
                    // no repeat or shuffle ON - play next song
//                    if(currentSongIndex < (song.length - 1)){
//                        playSong(currentSongIndex + 1);
//                        currentSongIndex = currentSongIndex + 1;
//                    }else{
//                        // play first song
//                        playSong(0);
//                        currentSongIndex = 0;
//                    }
                    Controls.nextControl(getApplicationContext());
                }

            }
        });
        super.onCreate();
    }

    /**
     * Send message from timer
     *
     * @author jonty.ankit
     */
    private class MainTask extends TimerTask {
        public void run() {
            handler.sendEmptyMessage(0);
        }
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mp != null) {
                if (mp.isPlaying() && mp.getDuration() != 0) {
                    int progress = (mp.getCurrentPosition() * 100) / mp.getDuration();
                    Integer i[] = new Integer[3];
                    i[0] = mp.getCurrentPosition();
                    i[1] = mp.getDuration();
                    i[2] = progress;
                    try {
                        PlayerConstants.PROGRESSBAR_HANDLER.sendMessage(PlayerConstants.PROGRESSBAR_HANDLER.obtainMessage(0, i));
                    } catch (Exception e) {
                    }
                }
            }
        }
    };

    @SuppressLint("NewApi")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (PlayerConstants.SONGS_LIST.size() <= 0) {
                PlayerConstants.SONGS_LIST = UtilFunctions.listToPlay();
            }
            MediaItem data = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER);
            if (currentVersionSupportLockScreenControls) {
                RegisterRemoteClient();
            }
            String songPath = data.getPath();
            playSong(songPath, data);
            newNotification();
            PlayerConstants.SONG_CHANGE_HANDLER = new Handler(new Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    MediaItem data = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER);
                    String songPath = data.getPath();
                    newNotification();
                    try {
                        playSong(songPath, data);
                        SongsList.changeUI();
                        AudioPlayerActivity.changeUI();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            });

            PlayerConstants.PLAY_PAUSE_HANDLER = new Handler(new Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    String message = (String) msg.obj;
                    if (mp == null)
                        return false;
                    if (message.equalsIgnoreCase(getResources().getString(R.string.play))) {
                        PlayerConstants.SONG_PAUSED = false;
                        if (currentVersionSupportLockScreenControls) {
                            remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
                        }
                        mp.start();
                    } else if (message.equalsIgnoreCase(getResources().getString(R.string.pause))) {
                        PlayerConstants.SONG_PAUSED = true;
                        if (currentVersionSupportLockScreenControls) {
                            remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
                        }
                        mp.pause();
                    }
                    newNotification();
                    try {
                        SongsList.changeButton();
                        AudioPlayerActivity.changeButton();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//					Log.d("TAG", "TAG Pressed: " + message);
                    return false;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    /**
     * Notification
     * Custom Bignotification is available from API 16
     */
    @SuppressLint("NewApi")
    private void newNotification() {
        String songName = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getTitle();
        String albumName = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getAlbum();
        RemoteViews simpleContentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.custom_notification);
        RemoteViews expandedView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.big_notification);

        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_music)
                .setContentTitle(songName).build();

        setListeners(simpleContentView);
        setListeners(expandedView);

        notification.contentView = simpleContentView;
        if (currentVersionSupportBigNotification) {
            notification.bigContentView = expandedView;
        }

        try {
//            String albumId = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getAlbumId();
//			Bitmap albumArt = UtilFunctions.getAlbumart(getApplicationContext(), albumId);
//			if(albumArt != null){
//				notification.contentView.setImageViewBitmap(R.id.imageViewAlbumArt, albumArt);
//				if(currentVersionSupportBigNotification){
//					notification.bigContentView.setImageViewBitmap(R.id.imageViewAlbumArt, albumArt);
//				}
//			}else{
            notification.contentView.setImageViewResource(R.id.imageViewAlbumArt, R.drawable.default_album_art);
            if (currentVersionSupportBigNotification) {
                notification.bigContentView.setImageViewResource(R.id.imageViewAlbumArt, R.drawable.default_album_art);
            }
//			}
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (PlayerConstants.SONG_PAUSED) {
            notification.contentView.setViewVisibility(R.id.btnPause, View.GONE);
            notification.contentView.setViewVisibility(R.id.btnPlay, View.VISIBLE);

            if (currentVersionSupportBigNotification) {
                notification.bigContentView.setViewVisibility(R.id.btnPause, View.GONE);
                notification.bigContentView.setViewVisibility(R.id.btnPlay, View.VISIBLE);
            }
        } else {
            notification.contentView.setViewVisibility(R.id.btnPause, View.VISIBLE);
            notification.contentView.setViewVisibility(R.id.btnPlay, View.GONE);

            if (currentVersionSupportBigNotification) {
                notification.bigContentView.setViewVisibility(R.id.btnPause, View.VISIBLE);
                notification.bigContentView.setViewVisibility(R.id.btnPlay, View.GONE);
            }
        }

        notification.contentView.setTextViewText(R.id.textSongName, songName);
        notification.contentView.setTextViewText(R.id.textAlbumName, albumName);
        if (currentVersionSupportBigNotification) {
            notification.bigContentView.setTextViewText(R.id.textSongName, songName);
            notification.bigContentView.setTextViewText(R.id.textAlbumName, albumName);
        }
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
//        startForeground(NOTIFICATION_ID, notification);
    }

    /**
     * Notification click listeners
     *
     * @param view ..
     */
    public void setListeners(RemoteViews view) {
        Intent previous = new Intent(NOTIFY_PREVIOUS);
        Intent delete = new Intent(NOTIFY_DELETE);
        Intent pause = new Intent(NOTIFY_PAUSE);
        Intent next = new Intent(NOTIFY_NEXT);
        Intent play = new Intent(NOTIFY_PLAY);

        PendingIntent pPrevious = PendingIntent.getBroadcast(getApplicationContext(), 0, previous, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnPrevious, pPrevious);

        PendingIntent pDelete = PendingIntent.getBroadcast(getApplicationContext(), 0, delete, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnDelete, pDelete);

        PendingIntent pPause = PendingIntent.getBroadcast(getApplicationContext(), 0, pause, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnPause, pPause);

        PendingIntent pNext = PendingIntent.getBroadcast(getApplicationContext(), 0, next, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnNext, pNext);

        PendingIntent pPlay = PendingIntent.getBroadcast(getApplicationContext(), 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnPlay, pPlay);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mp != null) {
            if (mainTask != null) {
                mainTask.cancel();
                if (timer != null) {
                    timer.cancel();
                    mainTask.cancel();
                }
            }
            mp.stop();
            mp.reset();
            mp.release();
            mp = null;
        }
    }

    public static MediaPlayer mediaPlayer = null;

    /**
     * Play song, Update Lockscreen fields
     *
     * @param songPath song url
     * @param data song data
     */
    @SuppressLint("NewApi")
    private void playSong(String songPath, MediaItem data) {
        try {
            if (currentVersionSupportLockScreenControls) {
                UpdateMetadata(data);
                remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
            }
            mediaPlayer = null;
            mp.reset();
            mp.setDataSource(songPath);
            mp.prepare();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    private void RegisterRemoteClient() {
        ComponentName remoteComponentName = new ComponentName(getApplicationContext(), new NotificationBroadcast().ComponentName());
        try {
            if (remoteControlClient == null) {
                audioManager.registerMediaButtonEventReceiver(remoteComponentName);
                Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                mediaButtonIntent.setComponent(remoteComponentName);
                PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
                remoteControlClient = new RemoteControlClient(mediaPendingIntent);
                audioManager.registerRemoteControlClient(remoteControlClient);
            }
            remoteControlClient.setTransportControlFlags(
                    RemoteControlClient.FLAG_KEY_MEDIA_PLAY |
                            RemoteControlClient.FLAG_KEY_MEDIA_PAUSE |
                            RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE |
                            RemoteControlClient.FLAG_KEY_MEDIA_STOP |
                            RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS |
                            RemoteControlClient.FLAG_KEY_MEDIA_NEXT);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    private void UpdateMetadata(MediaItem data) {
        if (remoteControlClient == null)
            return;
        MetadataEditor metadataEditor = remoteControlClient.editMetadata(true);
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ALBUM, data.getAlbum());
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, data.getArtist());
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_TITLE, data.getTitle());
//		mDummyAlbumArt = UtilFunctions.getAlbumart(getApplicationContext(), data.getAlbumId());
        if (mDummyAlbumArt == null) {
            mDummyAlbumArt = BitmapFactory.decodeResource(getResources(), R.drawable.default_album_art);
        }
        metadataEditor.putBitmap(MetadataEditor.BITMAP_KEY_ARTWORK, mDummyAlbumArt);
        metadataEditor.apply();
        audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {

//        switch (focusChange) {
//            case AudioManager.AUDIOFOCUS_GAIN:
//                Toast.makeText(getBaseContext(), "Audio focus gain", Toast.LENGTH_SHORT).show();
//                Controls.playControl(getApplicationContext());
//                break;
//            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
//                Toast.makeText(getBaseContext(), "AUDIO FOCUS LOSS TRANSIENT CAN DUCK", Toast.LENGTH_SHORT).show();
////               Controls.playControl(getApplicationContext()); // play your media player here
//                break;
//            case AudioManager.AUDIOFOCUS_LOSS:
//                Toast.makeText(getBaseContext(), "AUDIO FOCUS LOSS", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(getApplicationContext(), SongService.class);
//                stopService(intent);
//                AudioActivity.linearLayoutPlayingSong.setVisibility(View.GONE);
////                Controls.pauseControl(getApplicationContext());// Pause your media player here
//                break;
//            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
//                Toast.makeText(getBaseContext(), "AUDIO FOCUS LOSS TRANSIENT", Toast.LENGTH_SHORT).show();
//                Controls.pauseControl(getApplicationContext());// Pause your media player here
//                break;
//        }
    }
//    int result;
//    boolean flag;
//    Handler handler1 = new Handler();
//    Thread thread = new Thread(new Runnable() {
//        @Override
//        public void run() {
//            result = audioManager.requestAudioFocus(SongService.this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
//            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
//                Controls.playControl(getApplicationContext());
////                thread.stop();
//                flag = false;
//            }
//            if (flag) {
//                handler1.postDelayed(this, 1000);
//            }
//        }
//    });
}
