package com.luxuan.rtmppusher.push;

import android.text.TextUtils;

public class LxPushVideo {

    private LxConnectListener lxConnectListener;

    static {
        System.loadLibrary("lxpush");
    }

    public void setLxConnectListener(LxConnectListener lxConnectListener){
        this.lxConnectListener=lxConnectListener;
    }

    private void onConnecting(){
        if(lxConnectListener!=null){
            lxConnectListener.onConnecting();
        }
    }

    private void onConnectSuccess(){
        if(lxConnectListener!=null){
            lxConnectListener.onConnectSuccess();
        }
    }

    private void onConnectFailed(String msg){
        if(lxConnectListener!=null){
            lxConnectListener.onConnectFailed(msg);
        }
    }

    public void initLivePush(String url){
        if(!TextUtils.isEmpty(url)) {
            initPush(url);
        }
    }

    public void pushSPSPPS(byte[] sps, byte[] pps){
        if(sps!=null&&pps!=null){
            pushSPSPPS(sps, sps.length, pps, pps.length);
        }
    }

    public void pushVideoData(byte[] data, boolean keyframe){
        if(data!=null){
            pushVideoData(data, data.length, keyframe);
        }
    }

    public void pushAudioData(byte[] data){
        if(data!=null){
            pushAudioData(data, data.length);
        }
    }

    public void stopPush(){
        pushStop();
    }

    private native void pushStop();
}
