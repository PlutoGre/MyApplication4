package com.example.app;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.example.service.MusicService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnStart;
    private Button btnStop;
    private Button btnExit;
    private MusicService musicService;
    private SeekBar seekBar;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        bindMusicService();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    musicService.mediaPlayer.seekTo(seekBar.getProgress());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            seekBar.setProgress(musicService.mediaPlayer.getCurrentPosition());
            seekBar.setMax(musicService.mediaPlayer.getDuration());
            handler.postDelayed(runnable,100);
        }
    };

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = ((MusicService.MusicBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService = null;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable,100);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_play:
                if(musicService != null) {
                    if (musicService.mediaPlayer.isPlaying()) {
                        musicService.mediaPlayer.pause();
                        btnStart.setText("播放");
                    } else {
                        musicService.mediaPlayer.start();
                        btnStart.setText("暂停");
                    }
                }
                break;
            case R.id.btn_stop:
                if(musicService != null){
                    musicService.mediaPlayer.stop();
                    btnStart.setText("播放");
                }
                break;
            case R.id.btn_next:
                break;
            case R.id.btn_exit:
                handler.removeCallbacks(runnable);
                unbindService(serviceConnection);
                stopService(intent);
                this.finish();
                break;
        }
    }

    private void initView() {
        btnStart = findViewById(R.id.btn_play);
        btnStop = findViewById(R.id.btn_stop);
        btnExit = findViewById(R.id.btn_exit);
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnExit.setOnClickListener(this);

        seekBar = findViewById(R.id.sb);

    }

    private void bindMusicService() {
        intent = new Intent(MainActivity.this , MusicService.class);
        startService(intent);
        bindService(intent ,serviceConnection ,BIND_AUTO_CREATE);
    }
}
