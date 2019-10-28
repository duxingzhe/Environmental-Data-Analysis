
cmake_minimum_required(VERSION 3.4.1)

set(CURRENT_DIR ${CMAKE_SOURCE_DIR})
set(distribution_DIR ${CURRENT_DIR}/libs)

add_library( # Sets the name of the library.
             lxpush

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/lxpush.cpp
             src/main/cpp/lxQueue.cpp
             src/main/cpp/RtmpPush.cpp
        src/main/cpp/LxCallJava.cpp
)

add_library( rtmp
        SHARED
        IMPORTED)
set_target_properties( rtmp
        PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/${ANDROID_ABI}/librtmp.so)

include_directories(${distribution_DIR}/include
        )
find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )


target_link_libraries( # Specifies the target library.
                       lxpush
                        rtmp
                       ${log-lib} )