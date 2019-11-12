package com.luxuan.rtmppusher;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.luxuan.rtmppusher.camera.LxCameraView;

public class CameraActivity extends AppCompatActivity {

    private LxCameraView lxCameraView;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        lxCameraView=findViewById(R.id.cameraview);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        lxCameraView.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        lxCameraView.previewAngle(this);
    }
}
