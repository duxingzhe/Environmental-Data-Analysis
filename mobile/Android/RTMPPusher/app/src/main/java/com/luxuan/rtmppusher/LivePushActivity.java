package com.luxuan.rtmppusher;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.luxuan.rtmppusher.camera.LxCameraView;
import com.luxuan.rtmppusher.push.LxPushEncodec;
import com.luxuan.rtmppusher.push.LxPushVideo;

public class LivePushActivity extends AppCompatActivity {

    private LxPushVideo lxPushVideo;
    private LxCameraView lxCameraView;
    private boolean start=false;
    private LxPushEncodec lxPushEncodec;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
