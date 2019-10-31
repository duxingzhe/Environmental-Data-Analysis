package com.luxuan.rtmppusher.push;

public interface LxConnectListener {

    void onConnecting();

    void onConnectSuccess();

    void onConnectFailed(String msg);
}
