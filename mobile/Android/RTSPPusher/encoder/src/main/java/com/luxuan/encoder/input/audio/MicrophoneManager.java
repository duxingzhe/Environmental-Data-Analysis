package com.luxuan.encoder.input.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.HandlerThread;
import android.util.Log;

import java.nio.ByteBuffer;

public class MicrophoneManager {

    private final String TAG="MicrophoneManager";
    private static final int BUFFER_SIZE=4096;
    private AudioRecord audioRecord;
    private GetMicrophoneData getMicrophoneData;
    private ByteBuffer pcmBuffer=ByteBuffer.allocateDirect(BUFFER_SIZE);
    private byte[] pcmBufferMutex=new byte[BUFFER_SIZE];
    private boolean running=false;
    private boolean created=false;

    private int sampleRate=32000;
    private int audioFormat= AudioFormat.ENCODING_PCM_16BIT;
    private int channel=AudioFormat.CHANNEL_IN_STEREO;
    private boolean muted=false;
    private AudioPostProcessEffect audioPostProcessEffect;
    private HandlerThread handlerThread;

    public MicrophoneManager(GetMicrophoneData getMicrophoneData){
        this.getMicrophoneData=getMicrophoneData;
    }

    public void createMicrophone(){
        createMicrophone(sampleRate, true, false, false);
        Log.i(TAG, "Microphoe created, "+sampleRate+"hz, Stereo");
    }

    public void createMicrophone(int sampleRate, boolean isStereo, boolean echoCanceler, boolean noiseSuppressor){
        this.sampleRate=sampleRate;
        if(!isStereo){
            channel=AudioFormat.CHANNEL_IN_MONO;
        }
        audioRecord=new AudioRecord(MediaRecorder.AudioSource.DEFAULT, sampleRate, channel, audioFormat,
                getPcmBufferSize());
        audioPostProcessEffect=new AudioPostProcessEffect(audioRecord.getAudioSessionId());
        if(echoCanceler){
            audioPostProcessEffect.enableEchoCanceler();
        }
        if(noiseSuppressor)audioPostProcessEffect.enableNoiseSuppressor();
        String chl=(isStereo)?"Stereo":"Mono";
        Log.i(TAG, "Microphoe created, "+sampleRate+"hz, "+chl);
        created=true;
    }
}
