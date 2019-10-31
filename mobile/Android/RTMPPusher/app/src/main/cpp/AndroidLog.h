//
// Created by Luxuan on 2019/10/26.
//

#pragma once
#ifndef RTMPPUSHER_ANDROIDLOG_H
#define RTMPPUSHER_ANDROIDLOG_H

#include <android/log.h>

#define LOG_SHOW true

#define LOGD(FORMAT,...) __android_log_print(ANDROID_LOG_DEBUG, "luxuan", FORMAT, ##__VA_ARGS__);
#define LOGE(FORMAT,...) __android_log_print(ANDROID_LOG_ERROR, "luxuan", FORMAT, ##__VA_ARGS__);

#endif //RTMPPUSHER_ANDROIDLOG_H
