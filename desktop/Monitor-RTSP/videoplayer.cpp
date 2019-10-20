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

    for(i=0;i<pFormatCtx->nb_streams;i++)
    {
        if(pFormatCtx->streams[i]->codec->codec_type==AVMEDIA_TYPE_VIDEO)
        {
            videoStream=i;
        }
    }

    if(videoStream==-1)
    {
        printf("Didn't find a video stream.\n");
        return;
    }

    pCodecCtx=pFormatCtx->streams[videoStream]->codec;
    pCodec=avcodec_find_decoder(pCodecCtx->codec_id);
    pCodecCtx->bit_rate=0;
    pCodecCtx->time_base.num=1;
    pCodecCtx->time_base.den=0;
    pCodecCtx->frame_number=1;

    if(pCodec==NULL)
    {
        printf("Codec not found.\n");
        return;
    }

    if(avcodec_open2(pCodecCtx, pCodec, NULL)<0)
    {
        printf("Could not open codec.\n");
        return;
    }

    pFrame=av_frame_alloc();
    pFrameRGB=av_frame_alloc();

    img_convert_ctx=sws_getContext(pCodecCtx->width, pCodecCtx->height,
                                   pCodecCtx->pix_fmt, pCodecCtx->width, pCodecCtx->height,
                                   AV_PIX_FMT_RGB32, SWS_BICUBIC, NULL, NULL, NULL);

    numBytes=avpicture_get_size(AV_PIX_FMT_RGB32, pCodecCtx->width, pCodecCtx->height);

    out_buffer=(uint8_t *)av_malloc(numBytes*sizeof(uint8_t));
    avpicture_fill((AVPicture *)pFrameRGB, out_buffer, AV_PIX_FMT_RGB32,
                   pCodecCtx->width, pCodecCtx->height);

    int y_size=pCodecCtx->width *pCodecCtx->height;

    packet=(AVPacket *)malloc(sizeof(AVPacket));
    av_new_packet(packet, y_size);S

}
