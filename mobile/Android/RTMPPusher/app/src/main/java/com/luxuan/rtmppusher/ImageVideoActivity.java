package com.luxuan.rtmppusher;

import android.os.Bundle;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;

import com.luxuan.rtmppusher.encodec.LxMediaEncodec;
import com.luxuan.rtmppusher.imgVideo.LxImgVideoView;
import com.ywl5320.libmusic.WlMusic;
import com.ywl5320.listener.OnPreparedListener;
import com.ywl5320.listener.OnShowPcmDataListener;

public class ImageVideoActivity extends AppCompatActivity {

    private LxImgVideoView lxImgVideoView;
    private LxMediaEncodec lxMediaEncodec;
    private WlMusic wlMusic;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagevideo);

        lxImgVideoView=findViewById(R.id.imgvideoview);

        wlMusic=WlMusic.getInstance();
        wlMusic.setCallBackPcmData(true);

        wlMusic.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                wlMusic.playCutAudio(0, 60);
            }
        });

        wlMusic.setOnShowPcmDataListener(new OnShowPcmDataListener() {
            @Override
            public void onPcmInfo(int samplerate, int bit, int channels) {
                lxMediaEncodec=new LxMediaEncodec(ImageVideoActivity.this, lxImgVideoView.getFboTextureId());
                lxMediaEncodec.initEncodec(lxImgVideoView.getEglContext(),
                        Environment.getExternalStorageDirectory().getAbsolutePath()+"/lx_image_video.mp4",
                        720, 500, samplerate, channels);
                lxMediaEncodec.startRecord();
                startImgs();
            }

            @Override
            public void onPcmData(byte[] pcmdata, int size, long clock) {
                if(lxMediaEncodec!=null){
                    lxMediaEncodec.putPCMData(pcmdata, size);
                }
            }
        });
    }

}
