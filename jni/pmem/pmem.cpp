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
#include <linux/android_pmem.h>

#include "pmem.h"

#include "android/log.h"
static const char *TAG="pmem";
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)

#define	PMEM_WRITE	0

#define	PMEM_READ	1

int powersave_fd;

int master_fd;
int pmem_size;
char* VirtualAddr;

/*多线程同步解锁*/
//pthread_mutex_t m_mutex;

#define PMEM_BASE 0x9ff00000 
#define PMEM_BASE_SIZE 0x100000


static int init_pmem_area(void)
{
    int err = 0,phys;

    powersave_fd=open("/powersave.bin", O_RDONLY, 0);
    if (powersave_fd < 0)
        LOGE("can not open powersave_fd");

    master_fd = open("/dev/skmem", O_RDWR | O_NDELAY);
    LOGD("/dev/skmem =0x%x",master_fd);
    if (master_fd >= 0) {
        
        size_t size;

        char* base =(char*) mmap(0, PMEM_BASE_SIZE, PROT_READ|PROT_WRITE, MAP_SHARED, master_fd, PMEM_BASE);
        if (base == MAP_FAILED) {
            err = -errno;
            base = 0;
            LOGE("mmap failed");
            close(master_fd);
            master_fd = -1;
        }
        VirtualAddr = base;
        LOGE("VirtualAddr 0x%x",VirtualAddr);       
    } else {
        err = -errno;
    }
    return err;
}

static void clear_pmem_area(int index) 
{
    int i,size=10*1024;
    int offset=index*1024*10;;
    char *buf=VirtualAddr;
//    LOGD("clear_pmem_area %d",index);
		if(VirtualAddr==NULL)
			return;
    for(i=0;i<size;i++)
    {
        *(buf+i+offset)=0;
    }
}

static void clear_allpmem_area(void) 
{
    int i,size=128*1024;
    int offset=0;
    char *buf=VirtualAddr;
    LOGD("clear_highpmem_area");
		if(VirtualAddr==NULL)
			return;
    for(i=0;i<size;i++)
    {
        *(buf+i+offset)=0;
    }
}

static void write_pmem_area(POWER_SAVE_PROP mPmemProp)
{
    int i,size=mPmemProp.nAddrLen;
    int offset=mPmemProp.nAddrOffset;
    char *buf=VirtualAddr;
 		if(VirtualAddr==NULL)
			return;
    for(i=0;i<size;i++)
        *(buf+i+offset)=mPmemProp.WriteBuff.at(i);
}

static void read_pmem_area(POWER_SAVE_PROP mPmemProp,vector<uchar > &tmpList)
{
    int i,size=mPmemProp.nAddrLen;
    int offset=mPmemProp.nAddrOffset;
    char *buf=VirtualAddr+512*1024;
    unsigned char * pTmpOut = NULL;
		if(VirtualAddr==NULL)
			return;
    LOGD("read_pmem_area1 sizer=0x%x",size);
    lseek(powersave_fd,0,SEEK_SET);
    read(powersave_fd,buf,size);
    //  LOGD("read_pmem_area2");
    for(i=0;i<size;i++)
        tmpList.push_back(*(buf+i+offset));
    // LOGD("tmpList size 0x%x",tmpList.size());
		
}


static void close_pmem_area(void)
{
    munmap(VirtualAddr,pmem_size);
    close(master_fd);
}
/*
* Class:     pmem
* Method:    powersave_open
* Signature: ()V
*/
JNIEXPORT void JNICALL Java_com_android_Samkoonhmi_pmem_PowerSave_open
        (JNIEnv *env, jobject thiz)
{
    init_pmem_area();
}

/*
* Class:     pmem
* Method:    powersave_close
* Signature: ()V
*/
JNIEXPORT void JNICALL Java_com_android_Samkoonhmi_pmem_PowerSave_close
        (JNIEnv *env, jobject thiz)
{
    close_pmem_area();
}

/*
* Class:     pmem
* Method:    clear index mem
* Signature: ()V
*/
JNIEXPORT void JNICALL Java_com_android_Samkoonhmi_pmem_PowerSave_clearmem
        (JNIEnv *env, jobject thiz,jint index)
{
    clear_pmem_area(index);
}

/*
* Class:     pmem
* Method:    powersave_clear_high
* Signature: ()V
*/
JNIEXPORT void JNICALL Java_com_android_Samkoonhmi_pmem_PowerSave_clearall
        (JNIEnv *env, jobject thiz)
{
    clear_allpmem_area();
}


/*
* Class:     pmem
* Method:    powersave_write
* Signature: ()V
*/
JNIEXPORT void JNICALL Java_com_android_Samkoonhmi_pmem_PowerSave_write
        (JNIEnv *env, jobject thiz,jobject joComParam)
{

    POWER_SAVE_PROP mPmemProp = getPmemParamFromJni(env, joComParam,PMEM_WRITE);

    //		LOGE("mPmemProp.nAddrOffset = 0x%x",mPmemProp.nAddrOffset);
    //		LOGE("mPmemProp.nAddrLen = 0x%x",mPmemProp.nAddrLen);

    write_pmem_area(mPmemProp);
}


/*
* Class:     pmem
* Method:    powersave_read
* Signature: ()V
*/
JNIEXPORT void JNICALL Java_com_android_Samkoonhmi_pmem_PowerSave_read
        (JNIEnv *env, jobject thiz,jobject joComParam,jbyteArray out)
{

    jbyte * data;
    POWER_SAVE_PROP mPmemProp;

//    char *buf=VirtualAddr+512*1024;
    unsigned char * pTmpOut = NULL;


    data  = env->GetByteArrayElements(out, 0);
    mPmemProp = getPmemParamFromJni(env, joComParam,PMEM_READ);

    //		read_pmem_area(mPmemProp,data);
    pTmpOut = (unsigned char*)data;
    LOGD("read_pmem_area sizer=0x%x",mPmemProp.nAddrLen);
    lseek(powersave_fd,0,SEEK_SET);
    read(powersave_fd,pTmpOut,mPmemProp.nAddrLen);

		LOGD("read_pmem_area pTmpOut[0]=0x%x",pTmpOut[0]);
    // 	  pTmpOut = (unsigned char*)data;
    //    memcpy(pTmpOut, buf, mPmemProp.nAddrLen);
		
    setPmemParamToJni(env, joComParam,mPmemProp/*,tmpList,data*/);
		env->ReleaseByteArrayElements(out, data, 0);
    //	tmpList.clear();
    //  env->ReleaseByteArrayElements(out, data, 0);
 //   LOGD("mPmemProp.nAddrLen = 0x%x",mPmemProp.nAddrLen);
}


/******************************************************************
* Function: 获得读数据的打包数据
* Parameters:
* Return: true or false
******************************************************************/
POWER_SAVE_PROP getPmemParamFromJni(JNIEnv *env, jobject joPmemParam,int	flag)
{
    /*获取地址结构体*/
    POWER_SAVE_PROP mPmemProp;

    /*获取Java中的实例类PowerSaveProp*/
    jclass jPmemParamClass = env->FindClass("com/android/Samkoonhmi/util/PowerSaveProp");

    /*get int nAddrOffset;*/
    jfieldID joAddrOffset = env->GetFieldID(jPmemParamClass, "nAddrOffset", "I");
    mPmemProp.nAddrOffset = env->GetIntField(joPmemParam, joAddrOffset);

    /*get int nAddrLen;*/
    jfieldID joAddrLen = env->GetFieldID(jPmemParamClass, "nAddrLen", "I");
    mPmemProp.nAddrLen = env->GetIntField(joPmemParam, joAddrLen);


    /*get int WriteBuff;*/
    if(flag==PMEM_WRITE)
    {
    	jfieldID josWrite = env->GetFieldID(jPmemParamClass, "WriteBuff", "[B");
    	jbyteArray jDataList = (jbyteArray)env->GetObjectField(joPmemParam, josWrite);

     	if(jDataList != NULL)
    	{
            jbyte *pDataList = env->GetByteArrayElements(jDataList,0);
            if(0 != pDataList)
            {
                int len = env->GetArrayLength(jDataList);
                for(int i = 0; i < len; i++)
                {
                    mPmemProp.WriteBuff.push_back(pDataList[i]);
                }
                env->ReleaseByteArrayElements(jDataList, pDataList, 0);
            }
    	}
    }

    env->DeleteLocalRef(jPmemParamClass);
    return mPmemProp;
}


/******************************************************************
 * Function: 设置发送数据的结构体
 * Parameters:
 * Return: true or false
 ******************************************************************/
void setPmemParamToJni(JNIEnv *env, jobject joSend, POWER_SAVE_PROP mPmemProp/*,vector<uchar > tmpList,jbyte *buf*/)
{
    if(env == NULL || joSend == NULL) return ;

    /*获取Java中的实例类 POWER_SAVE_PROP*/
    jclass jSendObj = env->FindClass("com/android/Samkoonhmi/util/PowerSaveProp");

    /*get int nAddrOffset;*/
    jfieldID joAddrOffset = env->GetFieldID(jSendObj, "nAddrOffset", "I");
    env->SetIntField(joSend, joAddrOffset, mPmemProp.nAddrOffset);

    /*get int nAddrLen;*/
    jfieldID joAddrLen = env->GetFieldID(jSendObj, "nAddrLen", "I");
    env->SetIntField(joSend, joAddrLen, mPmemProp.nAddrLen);

    /*get int WriteBuff;*/
    jfieldID josWriteBuff = env->GetFieldID(jSendObj, "WriteBuff", "[B");

    /*转换成数值jbyteArray*/
    /*
    int nSize = tmpList.size();
 		LOGD("nSize = 0x%x",nSize); 
    if(nSize > 0)
    {
    		uchar nTmpValue = 0;
       for(int i = 0; i < nSize; i++)
        {
            nTmpValue = tmpList.at(i);
            memcpy(buf + i, &nTmpValue, 1);
        }
    }
*/
    env->DeleteLocalRef(jSendObj);
}
