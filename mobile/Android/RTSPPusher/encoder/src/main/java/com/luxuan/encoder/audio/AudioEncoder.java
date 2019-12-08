package com.luxuan.encoder.audio;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;

import com.luxuan.encoder.BaseEncoder;
import com.luxuan.encoder.input.audio.GetMicrophoneData;
import com.luxuan.encoder.util.CodecUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AudioEncoder extends BaseEncoder implements GetMicrophoneData {

    private static final String TAG="AudioEncoder";

    private GetAacData getAacData;
    private int bitRate=64*1024;
    private int sampleRate=32000;
    private boolean isStereo=true;

    public AudioEncoder(GetAacData getAacData){
        this.getAacData=getAacData;
    }

    public boolean preprareAudioEncoder(int bitRate, int sampleRate, boolean isStereo, int maxInputSize){
        this.sampleRate=sampleRate;
        isBufferMode=true;
        try{
            List<MediaCodecInfo> encoders=new ArrayList<>();
            if(force== CodecUtil.Force.FIRST_COMPATIABLE_FOUND){
                MediaCodecInfo encoder=chooseEncoder(CodecUtil.AAC_MIME);
                if(encoder!=null){
                    codec= MediaCodec.createByCodecName(encoder.getName());
                }else{
                    Log.e(TAG, "Valid encoder not found");
                    return false;
                }
            }else{
                if(encoders.isEmpty()){
                    Log.e(TAG, "Valid encoder not found");
                    return false;
                }else{
                    codec=MediaCodec.createByCodecName(encoders.get(0).getName());
                }
            }

            int channelCount=(isStereo)?2:1;
            MediaFormat audioFormat=MediaFormat.createAudioFormat(CodecUtil.AAC_MIME, sampleRate, channelCount);
            audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);
            audioFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, maxInputSize);
            audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            codec.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            running=false;
            Log.i(TAG,"prepared");
            return true;
        }catch(IOException | IllegalStateException e){
            Log.e(TAG,"Create AudioEncoder failed.", e);
            return false;
        }
    }
}
