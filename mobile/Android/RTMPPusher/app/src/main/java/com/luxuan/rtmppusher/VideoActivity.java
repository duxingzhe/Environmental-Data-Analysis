package com.luxuan.rtmppusher;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.luxuan.rtmppusher.camera.LxCameraView;
import com.luxuan.rtmppusher.encodec.LxBaseMediaEncoder;
import com.luxuan.rtmppusher.encodec.LxMediaEncodec;
import com.ywl5320.libmusic.WlMusic;
import com.ywl5320.listener.OnCompleteListener;
import com.ywl5320.listener.OnPreparedListener;
import com.ywl5320.listener.OnShowPcmDataListener;

public class VideoActivity extends AppCompatActivity {

    private LxCameraView lxCameraView;
    private Button btnRecord;

    private LxMediaEncodec lxMediaEncodec;

    private WlMusic wlMusic;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        lxCameraView=findViewById(R.id.cameraview);
        btnRecord=findViewById(R.id.btn_record);

        wlMusic=WlMusic.getInstance();
        wlMusic.setCallBackPcmData(true);
        wlMusic.setOnPreparedListener(new OnPreparedListener(){

            @Override
            public void onPrepared(){
                wlMusic.playCutAudio(39, 60);
            }
        });

        wlMusic.setOnCompleteListener(new OnCompleteListener(){

            @Override
            public void onComplete(){
                if(lxMediaEncodec!=null){
                    lxMediaEncodec.stopRecord();
                    lxMediaEncodec=null;
                    runOnUiThread(new Runnable(){

                        @Override
                        public void run(){
                            btnRecord.setText("开始录制");
                        }
                    });
                }
            }
        });

        wlMusic.setOnShowPcmDataListener(new OnShowPcmDataListener(){

            @Override
            public void onPcmInfo(int sampleRate, int bit, int channels){
                Log.d("lx", "textureId is "+lxCameraView.getTextureId());
                lxMediaEncodec=new LxMediaEncodec(VideoActivity.this, lxCameraView.getTextureId());
                lxMediaEncodec.initEncodec(lxCameraView.getEglContext(), Environment.getExternalStorageDirectory().getAbsolutePath()+"/wl_live_pusher.mp4", 720, 1289, sampleRate, channels);
                lxMediaEncodec.setOnMediaInfoListener(new LxBaseMediaEncoder.OnMediaInfoListener(){
                    @Override
                    public void onMediaTime(int times){
                        Log.d("lx", "time is:"+ times);
                    }
                });
                lxMediaEncodec.startRecord();
            }

            @Override
            public void onPcmData(byte[] pcmData, int size, long clock){
                if(lxMediaEncodec!=null){
                    lxMediaEncodec.putPCMData(pcmData, size);
                }
            }
        });
    }
}
