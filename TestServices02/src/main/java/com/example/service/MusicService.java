package com.example.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.example.app.R;

public class MusicService extends Service {

    public final IBinder binder = new MusicBinder();
    public MediaPlayer mediaPlayer;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this , R.raw.mymusic);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopSelf();
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }
        return START_STICKY;
    }

    public class MusicBinder extends Binder{

        public MusicService getService(){
            return MusicService.this;
        }
    }
}
