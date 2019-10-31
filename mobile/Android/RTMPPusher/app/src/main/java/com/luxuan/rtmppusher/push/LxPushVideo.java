package com.luxuan.rtmppusher.push;

public class LxPushVideo {

    public void stopPush(){
        pushStop();
    }

    private native void pushStop();
}
