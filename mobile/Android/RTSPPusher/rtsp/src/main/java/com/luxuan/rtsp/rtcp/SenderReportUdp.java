package com.luxuan.rtsp.rtcp;

import android.util.Log;

import com.luxuan.rtsp.rtsp.RtpFrame;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class SenderReportUdp extends BaseSenderReport {

    private MulticastSocket multicastSocketVideo;
    private MulticastSocket multicastSocketAudio;
    private DatagramPacket datagramPacket=new DatagramPacket(new byte[]{0}, 1);

    public SenderReportUdp(int videoSourcePort, int audioSourcePort){
        super();
        try{
            multicastSocketVideo=new MulticastSocket(videoSourcePort);
            multicastSocketVideo.setTimeToLive(64);
            multicastSocketAudio=new MulticastSocket(videoSourcePort);
            multicastSocketAudio.setTimeToLive(64);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void setDataStream(OutputStream outputStream, String host){
        try{
            datagramPacket.setAddress(InetAddress.getByName(host));
        }catch(UnknownHostException e){
            e.printStackTrace();
        }
    }

    @Override
    public void sendReport(byte[] buffer, RtpFrame rtpFrame, String type, int packetCount, int octetCount) throws IOException{
        sendReportUDP(buffer, rtpFrame.getRtcpPort(), type, packetCount, octetCount);
    }

    @Override
    public void close(){
        multicastSocketVideo.close();
        multicastSocketAudio.close();
    }

    private void sendReportUDP(byte[] buffer, int port, String type, int packet, int octet) throws IOException{
        datagramPacket.setData(buffer);
        datagramPacket.setPort(port);
        datagramPacket.setLength(PACKET_LENGTH);
        if(type.equals("Video")){
            multicastSocketVideo.send(datagramPacket);
        }else{
            multicastSocketAudio.send(datagramPacket);
        }

        Log.i(TAG, "wrote report: "+ type+", port: "+port+", packets: "+packet+", oct: "+ octet);
    }
}
