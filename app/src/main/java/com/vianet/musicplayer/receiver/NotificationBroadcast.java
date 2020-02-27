package com.vianet.musicplayer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import com.vianet.musicplayer.AudioActivity;
import com.vianet.musicplayer.controls.Controls;
import com.vianet.musicplayer.service.SongService;
import com.vianet.musicplayer.util.PlayerConstants;

public class NotificationBroadcast extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                return;

            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                	if(!PlayerConstants.SONG_PAUSED){
    					Controls.pauseControl(context);
                	}else{
    					Controls.playControl(context);
                	}
                	break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
//                    Toast.makeText(context, "Play button clicked.", Toast.LENGTH_SHORT).show();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
//                    Toast.makeText(context, "pause button click.", Toast.LENGTH_SHORT).show();
                    break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                	break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
//                	Log.d("TAG", "TAG: KEYCODE_MEDIA_NEXT");
                	Controls.nextControl(context);
                	break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
//                	Log.d("TAG", "TAG: KEYCODE_MEDIA_PREVIOUS");
                	Controls.previousControl(context);
                	break;
            }
		}  else{
			switch (intent.getAction()) {
				case SongService.NOTIFY_PLAY:
					Controls.playControl(context);
					break;
				case SongService.NOTIFY_PAUSE:
					Controls.pauseControl(context);
					break;
				case SongService.NOTIFY_NEXT:
					Controls.nextControl(context);
					break;
				case SongService.NOTIFY_DELETE:
					Intent i = new Intent(context, SongService.class);
					context.stopService(i);
//                    AudioActivity.audioActivity.finish();
					Intent in = new Intent(context, AudioActivity.class);
					in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(in);
					break;
				case SongService.NOTIFY_PREVIOUS:
					Controls.previousControl(context);
					break;
			}
		}
	}
	
	public String ComponentName() {
		return this.getClass().getName(); 
	}
}
