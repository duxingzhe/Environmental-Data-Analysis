package com.luxuan.rtsp.utils;

import android.util.Log;

import java.io.IOException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class CreateSSLSocket {

    public static Socket createSSLSocket(String host, int port){
        try{
            TLSSocketFactory socketFactory=new TLSSocketFactory();
            return socketFactory.createSocket(host, port);
        }catch(NoSuchAlgorithmException | KeyManagementException | IOException e){
            Log.e("CreateSSLSocket", "Error", e);
            return null;
        }
    }
}
