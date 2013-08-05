
LOCAL_PATH := $(call my-dir)

######## pmem ##########
include $(CLEAR_VARS)

TARGET_PLATFORM := android-3
LOCAL_MODULE    := pmem
LOCAL_SRC_FILES := pmem/pmem.cpp
LOCAL_LDLIBS    := -llog

include $(BUILD_SHARED_LIBRARY)


######## serial ##########
include $(CLEAR_VARS)

TARGET_PLATFORM := android-3
LOCAL_MODULE    := serial_port
LOCAL_SRC_FILES := serial/SerialPort.cpp
LOCAL_LDLIBS    := -llog

include $(BUILD_SHARED_LIBRARY)


######## skcommon ##########
include $(CLEAR_VARS)

TARGET_PLATFORM := android-3
LOCAL_MODULE    := skcommon
LOCAL_SRC_FILES := skcommon/skcommon.cpp
LOCAL_LDLIBS    := -llog

include $(BUILD_SHARED_LIBRARY)
