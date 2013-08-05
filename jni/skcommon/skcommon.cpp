#include <limits.h>
#include <unistd.h>
#include <fcntl.h>
#include <errno.h>
#include <pthread.h>
#include <stdlib.h>
#include <string.h>

#include <sys/mman.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/ioctl.h>

#include <jni.h>

#include "skcommon.h"

#include "android/log.h"
static const char *TAG="skcommon";
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)


#define SK_MAJOR_NUM 234
#define SK_MINOR_NUM 1

#define IOCTL_SET_BACKLIGHTON   	_IO(SK_MAJOR_NUM, 100)
#define IOCTL_SET_BACKLIGHTOFF		_IO(SK_MAJOR_NUM, 101)
#define IOCTL_GET_SKMODE0   			_IO(SK_MAJOR_NUM, 102)
#define IOCTL_GET_SKMODE1   			_IO(SK_MAJOR_NUM, 103)
#define IOCTL_GET_SKMODE2   			_IO(SK_MAJOR_NUM, 104)

int skcommon_fd;


static int init_sk_common(void)
{
    int err = 0,phys;

    skcommon_fd = open("/dev/sk_common", O_RDWR | O_NDELAY);
    LOGD("/dev/sk_common =0x%x",skcommon_fd);
    if (skcommon_fd < 0) {
        err = -errno;
    }
    return err;
}

static void skcommon_backlighton() 
{
    if (skcommon_fd >= 0)
	ioctl(skcommon_fd, IOCTL_SET_BACKLIGHTON, 0);
}

static void skcommon_backlightoff() 
{
    if (skcommon_fd >= 0)
	ioctl(skcommon_fd, IOCTL_SET_BACKLIGHTOFF, 0);
}

static int skcommon_getmode0() 
{
    struct sk_common_info_t info;
    if (skcommon_fd >= 0)
        ioctl(skcommon_fd, IOCTL_GET_SKMODE0, &info);
    //  LOGD("skcommon_getmode0 info.module =0x%x",info.module);
    return info.module;
}

static int skcommon_getmode1() 
{
    struct sk_common_info_t info;
    if (skcommon_fd >= 0)
	ioctl(skcommon_fd, IOCTL_GET_SKMODE1, &info);
    //  LOGD("skcommon_getmode1 info.module =0x%x",info.module);
    return info.module;
}

static int skcommon_getmode2() 
{
    struct sk_common_info_t info;
    if (skcommon_fd >= 0)
	ioctl(skcommon_fd, IOCTL_GET_SKMODE2, &info);
    //  LOGD("skcommon_getmode2 info.module =0x%x",info.module);
    return info.module;
}

static void close_skcommon(void)
{
    close(skcommon_fd);
}


/*
* Class:     skcommon
* Method:    SkCommon_open
* Signature: ()V
*/
JNIEXPORT void JNICALL Java_com_android_Samkoonhmi_skcommon_SkCommon_open
        (JNIEnv *env, jobject thiz)
{
    init_sk_common();
}

/*
* Class:     skcommon
* Method:    SkCommon_close
* Signature: ()V
*/
JNIEXPORT void JNICALL Java_com_android_Samkoonhmi_skcommon_SkCommon_close
        (JNIEnv *env, jobject thiz)
{
    close_skcommon();
}

/*
* Class:     skcommon
* Method:    skcommon_backlighton
* Signature: ()V
*/
JNIEXPORT void JNICALL Java_com_android_Samkoonhmi_skcommon_SkCommon_backlighton
        (JNIEnv *env, jobject thiz)
{
    skcommon_backlighton();
}

/*
* Class:     skcommon
* Method:    skcommon_backlightoff
* Signature: ()V
*/
JNIEXPORT void JNICALL Java_com_android_Samkoonhmi_skcommon_SkCommon_backlightoff
        (JNIEnv *env, jobject thiz)
{
    skcommon_backlightoff();
}

/*
* Class:     skcommon
* Method:    skcommon_getmode0
* Signature: ()V
*/
JNIEXPORT void JNICALL Java_com_android_Samkoonhmi_skcommon_SkCommon_getmode0
        (JNIEnv *env, jobject thiz,jobject joComParam)
{
    SK_COMMON_INFO_T mSk_common_info_t;
    int module;

    mSk_common_info_t = getSkCommonParamFromJni(env, joComParam);

    module=skcommon_getmode0();
    mSk_common_info_t.module=module;
    //  	LOGD("Java_com_android_Samkoonhmi_skcommon_SkCommon_getmode0 1 mSk_common_info_t.module =0x%x",mSk_common_info_t.module);
    setSkCommonParamToJni(env, joComParam,mSk_common_info_t);

}

/*
* Class:     skcommon
* Method:    skcommon_getmode1
* Signature: ()V
*/
JNIEXPORT void JNICALL Java_com_android_Samkoonhmi_skcommon_SkCommon_getmode1
        (JNIEnv *env, jobject thiz,jobject joComParam)
{
    SK_COMMON_INFO_T mSk_common_info_t;
    int module;

    mSk_common_info_t = getSkCommonParamFromJni(env, joComParam);
    module=skcommon_getmode1();
    mSk_common_info_t.module=module;
    //  	LOGD("Java_com_android_Samkoonhmi_skcommon_SkCommon_getmode1 1 mSk_common_info_t.module =0x%x",mSk_common_info_t.module);

    setSkCommonParamToJni(env, joComParam,mSk_common_info_t);
}

/*
* Class:     skcommon
* Method:    skcommon_getmode2
* Signature: ()V
*/
JNIEXPORT void JNICALL Java_com_android_Samkoonhmi_skcommon_SkCommon_getmode2
        (JNIEnv *env, jobject thiz,jobject joComParam)
{
    SK_COMMON_INFO_T mSk_common_info_t;
    int module;

    mSk_common_info_t = getSkCommonParamFromJni(env, joComParam);
    module=skcommon_getmode2();
    mSk_common_info_t.module=module;
    //   	LOGD("Java_com_android_Samkoonhmi_skcommon_SkCommon_getmode2 1 mSk_common_info_t.module =0x%x",mSk_common_info_t.module);

    setSkCommonParamToJni(env, joComParam,mSk_common_info_t);
}


/******************************************************************
* Function: 获得模式结构体数据
* Parameters:
* Return: true or false
******************************************************************/
SK_COMMON_INFO_T getSkCommonParamFromJni(JNIEnv *env, jobject joSkCommonParam)
{
    /*获取地址结构体*/
    SK_COMMON_INFO_T mSk_common_info_t;

    /*获取Java中的实例类SK_COMMON_INFO_T*/
    jclass jSkCommonParamClass = env->FindClass("com/android/Samkoonhmi/util/Sk_CommonInfo");

    /*get int module;*/
    jfieldID jomodule = env->GetFieldID(jSkCommonParamClass, "module", "I");
    mSk_common_info_t.module = env->GetIntField(joSkCommonParam, jomodule);

    /*get int TMP1;*/
    jfieldID joTMP1 = env->GetFieldID(jSkCommonParamClass, "TMP1", "I");
    mSk_common_info_t.TMP1 = env->GetIntField(joSkCommonParam, joTMP1);

    /*get int TMP2;*/
    jfieldID joTMP2 = env->GetFieldID(jSkCommonParamClass, "TMP2", "I");
    mSk_common_info_t.TMP2 = env->GetIntField(joSkCommonParam, joTMP2);
    
    env->DeleteLocalRef(jSkCommonParamClass);
    return mSk_common_info_t;
}


/******************************************************************
 * Function: 设置模式结构体数据
 * Parameters:
 * Return: true or false
 ******************************************************************/
void setSkCommonParamToJni(JNIEnv *env, jobject joSend, SK_COMMON_INFO_T mSk_common_info_t)
{
    if(env == NULL || joSend == NULL) return ;

    /*获取Java中的实例类SK_COMMON_INFO_T*/
    jclass jSendObj = env->FindClass("com/android/Samkoonhmi/util/Sk_CommonInfo");

    /*get int module;*/
    jfieldID jomodule = env->GetFieldID(jSendObj, "module", "I");
    env->SetIntField(joSend, jomodule, mSk_common_info_t.module);

    /*get int TMP1;*/
    jfieldID joTMP1 = env->GetFieldID(jSendObj, "TMP1", "I");
    env->SetIntField(joSend, joTMP1, mSk_common_info_t.TMP1);

    /*get int TMP2;*/
    jfieldID joTMP2 = env->GetFieldID(jSendObj, "TMP2", "I");
    env->SetIntField(joSend, joTMP2, mSk_common_info_t.TMP2);

    env->DeleteLocalRef(jSendObj);
}
