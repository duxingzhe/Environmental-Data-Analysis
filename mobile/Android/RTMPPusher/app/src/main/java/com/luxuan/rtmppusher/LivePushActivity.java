package com.luxuan.rtmppusher;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.luxuan.rtmppusher.camera.LxCameraView;
import com.luxuan.rtmppusher.push.LxBasePushEncoder;
import com.luxuan.rtmppusher.push.LxConnectListener;
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
        setContentView(R.layout.activity_livepush);
        lxCameraView=findViewById(R.id.cameraview);
        lxPushVideo=new LxPushVideo();
        lxPushVideo.setLxConnectListener(new LxConnectListener(){

            @Override
            public void onConnecting(){
                Log.d("lx", "连接服务器中...");
            }

            @Override
            public void onConnectSuccess(){
                Log.d("lx", "连接服务器成功了，可以开始推流了。");
                lxPushEncodec=new LxPushEncodec(LivePushActivity.this, lxCameraView.getTextureId());
                lxPushEncodec.initEncodec(lxCameraView.getEglContext(), 720/2, 1280/2, 44100, 2);
                lxPushEncodec.startRecord();
                lxPushEncodec.setOnMediaInfoListener(new LxBasePushEncoder.OnMediaInfoListener(){

                    @Override
                    public void onMediaTime(int times){

                    }

                    @Override
                    public void onSPSPPSInfo(byte[] sps, byte[] pps){
                        lxPushVideo.pushSPSPPS(sps, pps);
                    }

                    @Override
                    public void onVideoInfo(byte[] data, boolean keyframe){
                        lxPushVideo.pushVideoData(data, keyframe);
                    }

                    @Override
                    public void onAudioInfo(byte[] data){
                        lxPushVideo.pushAudioData(data);
                    }
                });
            }

            @Override
            public void onConnectFailed(String msg){
                Log.d("lx", msg);
            }
        });
    }
}
