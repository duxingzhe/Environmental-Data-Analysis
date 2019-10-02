#include <QDebug>

#include "decoder.h"

Decoder::Decoder() :
    timeTotal(0),
    playState(STOP),
    isStop(false),
    isPause(false),
    isSeek(false),
    isReadFinished(false),
    audioDecoder(new AudioDecoder),
    filterGraph(NULL)
{
    av_init_packet(&seekPacket);
    seekPacket.data=(uint8_t *)"FLUSH";

    connect(audioDecoder, SIGNAL(playFinished()), this, SLOT(audioFinished()));
    connect(this, SIGNAL(readFinished()), audioDecoder, SLOT(readFileFinished()));
}

Decoder::~Decoder()
{

}

void Decoder::displayVideo(QImage image)
{
    emit gotVideo(image);
}

void Decoder::clearData()
{
    videoIndex=-1;
    audioIndex=-1;
    subtitleIndex=-1;

    timeTotal=0;

    isStop=false;
    isPause=false;
    isSeek=false;
    isReadFinished=false;
    isDecodeFinished=false;

    videoQueue.empty();

    audioDecoder->emptyAudioData();

    videoClock=0;
}

void Decoder::setPlayState(Decoder::PlayState state)
{
    emit playStateChanged(state);
    playState=state;
}

bool Decoder::isRealtime(AVFormatContext *pFormatCtx)
{
    if(!strcmp(pFormatCtx->iformat->name, "rtp")
       || !strcmp(pFormatCtx->iformat->name, "rtsp")
       || !strcmp(pFormatCtx->iformat->name, "sdp"))
    {
        return true;
    }

    if(pFormatCtx->pb && (!strncmp(pFormatCtx->filename, "rtp:", 4)
                          || !strncmp(pFormatCtx->filename, "udp:", 4)))
    {
        return true;
    }

    return false;
}

int Decoder::initFilter()
{
    int ret;

    AVFilterInOut *out=avfilter_inout_alloc();
    AVFilterInOut *in=avfilter_inout_alloc();

    enum AVPixelFormat pixFmts[]={AV_PIX_FMT_RGB32, AV_PIX_FMT_NONE};

    if(filterGraph)
    {
        avfilter_graph_free(&filterGraph);
    }

    filterGraph=avfilter_graph_alloc();

    QString filter("pp=hb/vb/dr/al");

    QString args=QString("video_size=%1x%2:pix_fmt=%3:time_base:%4/%5:pixel_aspect=%6/&7")
            .arg(pCodecCtx->width).arg(pCodecCtx->height).arg(pCodecCtx->pix_fmt)
            .arg(videoStream->time_base.num).arg(videoStream->time_base.den)
            .arg(pCodecCtx->sample_aspect_ratio.num).arg(pCodecCtx->sample_aspect_ratio.den);

    ret=avfilter_graph_create_filter(&filterSrcCtx, avfilter_get_by_name("buffer"), "in", args.toLocal8Bit().data(), NULL, filterGraph);
    if(ret<0)
    {
        qDebug()<<"avfilter graph create filter failed, ret: "<<ret;
        avfilter_graph_free(&filterGraph);
        goto out;
    }

    ret=av_opt_set_int_list(&filterSinkCtx, "pix_fmts", pixFmts, AV_PIX_FMT_NONE, AV_OPT_SEARCH_CHILDREN);
    if(ret<0)
    {
        qDebug()<<"av opt set int list failed, ret: "<<ret;
        avfilter_graph_free(&filterGraph);
        goto out;
    }

    out->name=av_strdup("in");
    out->filter_ctx=filterSrcCtx;
    out->pad_idx=0;
    out->next=NULL;

    in->name=av_strdup("out");
    in->filter_ctx=filterSinkCtx;
    in->pad_idx=0;
    in->next=NULL;

    if(filter.isEmpty()||filter.isNull())
    {
        ret=avfilter_link(filterSrcCtx, 0, filterSinkCtx, 0);
        if(ret<0)
        {
            qDebug()<<"avfilter link failed, ret: "<<ret;
            avfilter_graph_free(&filterGraph);
            goto out;
        }
    }
    else
    {
        ret=avfilter_graph_parse_ptr(filterGraph, filter.toLatin1().data(), &in, &out, NULL);
        if(ret<0)
        {
            qDebug()<<"avfilter graph config failed, ret: "<<ret;
            avfilter_graph_free(&filterGraph);
        }
    }

out:
    avfilter_inout_free(&out);
    avfilter_inout_free(&in);

    return ret;
}

void Decoder::decoderFile(QString file, QString type)
{
    qDebug()<<"File name: "<<file<<", type: "<<type;
    if(playState!=STOP)
    {
        isStop=true;
        while(playState!=STOP)
        {
            SDL_Delay(10);
        }
        SDL_Delay(100);
    }

    clearData();

    SDL_Delay(100);

    currentFile=file;
    currentType=type;

    this->start();
}

void Decoder::audioFinished()
{
    isStop=true;
    if(currentType=="music")
    {
        SDL_Delay(100);
        emit playStateChanged(Decoder::FINISH);
    }
}

void Decoder::stopVideo()
{
    if(playState==STOP)
    {
        setPlayState(Decoder::STOP);
        return;
    }

    gotStop=true;
    isStop=true;
    audioDecoder->stopAudio();

    if(currentType=="video")
    {
        while(isReadFinished|| isDecodeFinished)
        {
            SDL_Delay(10);
        }
    }
    else
    {
        while(!isReadFinished)
        {
            SDL_Delay(10);
        }
    }
}

void Decoder::pauseVideo()
{
    if(playState==STOP)
    {
        return;
    }

    isPause=!isPause;
    audioDecoder->pauseAudio(isPause);
    if(isPause)
    {
        av_read_pause(pFormatCtx);
        setPlayState(PAUSE);
    }
    else
    {
        av_read_play(pFormatCtx);
        setPlayState(PLAYING);
    }
}

int Decoder::getVolume()
{
    return audioDecoder->getVolume();
}

void Decoder::setVolume(int volume)
{
    audioDecoder->setVolume(volume);
}

double Decoder::getCurrentTime()
{
    if(audioIndex>=0)
    {
        return audioDecoder->getAudioClock();
    }

    return 0;
}

void Decoder::seekProgress(qint64 pos)
{
    if(!isSeek)
    {
        seekPos=pos;
        isSeek=true;
    }
}

double Decoder::synchronize(AVFrame *frame, double pts)
{
    double delay;

    if(pts!=0)
    {
        videoClock=pts;
    }
    else
    {
        pts=videoClock;
    }

    delay=av_q2d(pCodecCtx->time_base);
    delay+=frame->repeat_pict*(delay*0.5);

    videoClock+=delay;

    return pts;
}

int Decoder::videoThread(void *arg)
{
    int ret;
    double pts;
    AVPacket packet;
    Decoder *decoder=(Decoder *)arg;
    AVFrame *pFrame = av_frame_alloc();

    while(true)
    {
        if(decoder->isStop)
        {
            break;
        }

        if(decoder->isPause)
        {
            SDL_Delay(10);
            continue;
        }

        if(decoder->videoQueue.queueSize()<=0)
        {
            if(decoder->isReadFinished)
            {
                break;
            }
            SDL_Delay(1);
            continue;
        }

        decoder->videoQueue.dequeue(&packet, true);

        if(!strcmp((char *)packet.data, "FLUSH"))
        {
            qDebug()<<"Seek video";
            avcodec_flush_buffers(decoder->pCodecCtx);
            av_packet_unref(&packet);
            continue;
        }

        ret=avcodec_send_packet(decoder->pCodecCtx, &packet);
        if((ret<0)&&(ret!=AVERROR(EAGAIN))&&(ret!=AVERROR_EOF))
        {
            qDebug()<<"Video send to decoder failed, error code: "<< ret;
            av_packet_unref(&packet);
            continue;
        }

        ret=avcodec_receive_frame(decoder->pCodecCtx, pFrame);
        if((ret<0)&&(ret!=AVERROR_EOF))
        {
            qDebug()<<"Video frame decode failed, error code: "<<ret;
            av_packet_unref(&packet);
            continue;
        }

        if((pts=pFrame->pts)==AV_NOPTS_VALUE)
        {
            pts=0;
        }

        pts*=av_q2d(decoder->videoStream->time_base);
        pts=decoder->synchronize(pFrame, pts);

        if(decoder->audioIndex>=0)
        {
            while(1)
            {
                if(decoder->isStop)
                {
                    break;
                }
                double audioClock=decoder->audioDecoder->getAudioClock();
                pts=decoder->videoClock;

                if(pts<=audioClock)
                {
                    break;
                }
                int delayTime=(pts-audioClock)*1000;

                delayTime=delayTime>5?5:delayTime;

                SDL_Delay(delayTime);
            }
        }

        if(av_buffersrc_add_frame(decoder->filterSrcCtx, pFrame)<0)
        {
            qDebug()<<"av buffersrc add frame failed.";
            av_packet_unref(&packet);
            continue;
        }

        if(av_buffersink_get_frame(decoder->filterSinkCtx, pFrame)<0)
        {
            qDebug()<<"av buffersrc add frame failed.";
            av_packet_unref(&packet);
            continue;
        }
        else
        {
            QImage tmpImage(pFrame->data[0], decoder->pCodecCtx->width, decoder->pCodecCtx->height,QImage::Format_RGB32);
            QImage image=tmpImage.copy();
            decoder->displayVideo(image);
        }

        av_frame_unref(pFrame);
        av_packet_unref(&packet);
    }

    av_frame_free(&pFrame);

    if(!decoder->isStop)
    {
        decoder->isStop=true;
    }

    qDebug()<<"Video decoer finished.";

    SDL_Delay(100);

    decoder->isDecodeFinished=true;

    if(decoder->gotStop)
    {
        decoder->setPlayState(Decoder::STOP);
    }
    else
    {
        decoder->setPlayState(Decoder::FINISH);
    }

    return 0;
}
