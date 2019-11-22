package com.luxuan.encoder.util;

public class PCMUtil {

    public static byte[] mixPCM(byte[] pcm1, byte[] pcm2){
        int len1=pcm1.length;
        int len2=pcm2.length;
        byte[] pcmL;
        byte[] pcmS;
        int lenL;
        int lenS;
        if(len2>len1){
            lenL=len1;
            pcmL=pcm1;
            lenS=len2;
            pcmS=pcm2;
        }else{
            lenL=len2;
            pcmL=pcm2;
            lenS=len1;
            pcmS=pcm1;
        }

        for(int index=0;index<lenL;index++){
            int sample;
            if(index>=lenS){
                sample=pcmL[index];
            }else{
                sample=pcmL[index]+pcmS[index];
            }
            sample=(int)(sample*0.71);
            if(sample>127){
                sample=127;
            }
            if(sample<-128){
                sample=-128;
            }
            pcmL[index]=(byte)sample;
        }
        return pcmL;
    }

    private static final byte[] pcmBufferStereo=new byte[4096];

    public static byte[] pcmToStereo(byte[] pcm, int channels){
        int count=0;
        for(int i=0;i<pcm.length;i+=channels){
            byte channel1=pcm[i];
            byte channel2=pcm[i+1];

            pcmBufferStereo[count]=channel1;
            pcmBufferStereo[count+1]=channel2;
            count+=2;
        }

        return pcmBufferStereo;
    }
}
