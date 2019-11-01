package com.luxuan.rtmppusher.push;

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

    public void stopPush(){
        pushStop();
    }

    private native void pushStop();
}
