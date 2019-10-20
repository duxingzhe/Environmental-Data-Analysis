#include "videoplayer.h"

extern "C"
{
    #include "libavcodec/avcodec.h"
    #include "libavformat/avformat.h"
    #include "libavutil/pixfmt.h"
    #include "libswscale/swscale.h"
}

#include <stdio.h>
#include <iostream>

using namespace std;

VideoPlayer::VideoPlayer()
{

}

VideoPlayer::~VideoPlayer()
{

}

void VideoPlayer::startPlay()
{
    this->start();
}

void VideoPlayer::run()
{
    AVFormatContext *pFormatCtx;
    AVCodecContext *pCodecCtx;
    AVCodec *pCodec;
    AVFrame *pFrame, *pFrameRGB;
    AVPacket *packet;
    uint8_t *out_buffer;

    static struct SwsContext *img_convert_ctx;

    int videoStream, i, numBytes;
    int ret, got_picture;

    avformat_network_init();
    av_register_all();

    pFormatCtx=avformat_alloc_context();

    AVDictionary *avdic=NULL;
    char option_key[]="rtsp_transport";
    char option_value[]="tcp";
    av_dict_set(&avdic, option_key, option_value, 0);
    char option_key2[]="max_delay";
    char option_value2[]="100";
    av_dict_set(&avdic, option_key2, option_value2, 0);
    char url[]="rtsp://admin:admin@192.168.1.18:554/h264/ch1/main/av_stream";

    if(avformat_open_input(&pFormatCtx, url, NULL, &avdic)!=0)
    {
        printf("can't open the file.\n");
        return;
    }

    if(avformat_find_stream_info(pFormatCtx, NULL)<0)
    {
        printf("Couldn't find stream information.\n");
        return;
    }

    videoStream=-1;
}
