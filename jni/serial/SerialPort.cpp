#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <jni.h>

#include "SerialPort.h"

#include "android/log.h"
static const char *TAG="serial_port";
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)

static speed_t getBaudrate(jint baudrate)
{
    switch(baudrate) {
    case 0: return B0;
    case 50: return B50;
    case 75: return B75;
    case 110: return B110;
    case 134: return B134;
    case 150: return B150;
    case 200: return B200;
    case 300: return B300;
    case 600: return B600;
    case 1200: return B1200;
    case 1800: return B1800;
    case 2400: return B2400;
    case 4800: return B4800;
    case 9600: return B9600;
    case 19200: return B19200;
    case 38400: return B38400;
    case 57600: return B57600;
    case 115200: return B115200;
    case 230400: return B230400;
    case 460800: return B460800;
    case 500000: return B500000;
    case 576000: return B576000;
    case 921600: return B921600;
    case 1000000: return B1000000;
    case 1152000: return B1152000;
    case 1500000: return B1500000;
    case 2000000: return B2000000;
    case 2500000: return B2500000;
    case 3000000: return B3000000;
    case 3500000: return B3500000;
    case 4000000: return B4000000;
    default: return -1;
    }
}

#define  TTY_DEV            "/dev/ttyO"    /*�˿�·��*/


char *getDevFilePath(int nSerialPortNum)
{
    char *ptty = NULL;
    switch(nSerialPortNum)
    {
    case 0:
        {
            ptty = TTY_DEV"0";
            break;
        }
    case 1:
        {
            ptty = TTY_DEV"1";
            break;
        }
    case 2:
        {
            ptty = TTY_DEV"2";
            break;
        }
    case 3:
        {
            ptty = TTY_DEV"3";
            break;
        }
    case 4:
        {
            ptty = TTY_DEV"4";
            break;
        }
    case 5:
        {
            ptty = TTY_DEV"5";
            break;
        }
    default:
        {
            break;
        }
    }

    return ptty;
}

/*
* Class:     android_serialport_SerialPort
* Method:    open
* Signature: (Ljava/lang/String;II)Ljava/io/FileDescriptor;
*/
JNIEXPORT jobject JNICALL Java_com_android_Samkoonhmi_serial_SerialPort_open
        (JNIEnv *env, jclass thiz, jobject joComParam, jint flags)//jstring path, jint baudrate, jint flags)
{
    COM_PORT_PARAM_PROP mComParam = getComParamFromJni(env, joComParam);

	LOGE("mComParam.nBaudRate = %d",mComParam.nBaudRate); 
	LOGE("mComParam.nDataBits = %d",mComParam.nDataBits);
	LOGE("mComParam.nParityType = %d",mComParam.nParityType);
	LOGE("mComParam.nStopBit = %d",mComParam.nStopBit);
	LOGE("mComParam.nFlowType = %d",mComParam.nFlowType);


    int fd;
    speed_t speed;
    jobject mFileDescriptor;

    /* Check arguments */
    {
        speed = getBaudrate(mComParam.nBaudRate);
        if (speed == -1) {
            /* TODO: throw an exception */
            LOGE("Invalid baudrate");
            return NULL;
        }
    }

    /* Opening device */
    {
        jboolean iscopy;
        char *pTtyPath = getDevFilePath(mComParam.nSerialPortNum);

        LOGE("open serial port nSerialPortNum = %d filepath = %s with flags = %x", mComParam.nSerialPortNum, pTtyPath, flags);
        fd = open(pTtyPath, flags);
        if (fd == -1)
        {
            /* Throw an exception */
            LOGE("Cannot open port");
            /* TODO: throw an exception */
            return NULL;
        }
    }

    /* Configure device */
    {
        struct termios cfg;
        LOGD("Configuring serial port");
        if (tcgetattr(fd, &cfg))
        {
            LOGE("tcgetattr() failed");
            close(fd);
            /* TODO: throw an exception */
            return NULL;
        }

        cfmakeraw(&cfg);

        /*���ò�����*/
        cfsetispeed(&cfg, speed);
        cfsetospeed(&cfg, speed);

#if 1
        /*�������λ*/
        cfg.c_cflag &= ~CSIZE;        /*����ģʽ�������ַ��Сλ*/
        int nDataBit = mComParam.nDataBits;
        switch(nDataBit)
        {
        case 5:
            {
                cfg.c_cflag |= CS5;
                break;
            }
        case 6:
            {
                cfg.c_cflag |= CS6;
                break;
            }
        case 7:
            {
                cfg.c_cflag |= CS7;
                break;
            }
        default:
            {
                cfg.c_cflag |= CS8;
                break;
            }
        }

        /*���ü��鷽ʽ*/
        int parity = mComParam.nParityType;
        switch(parity)
        {
        case 0:
            {
                cfg.c_cflag &= ~PARENB;        /*no parity check*/
                break;
            }
        case 1:
            {
                cfg.c_cflag |= PARENB;        /*even check*/
                cfg.c_cflag &= ~PARODD;
                break;
            }
        case 2:
            {
                cfg.c_cflag |= PARENB;        /*odd check*/
                cfg.c_cflag |= PARODD;
                break;
            }
        default:
            {
                cfg.c_cflag &= ~PARENB;        /*no parity check*/
            }
        }

        /*����ֹͣλ*/
        int nStopBit = mComParam.nStopBit;
        switch(nStopBit)
        {
        case 2:
            {
                cfg.c_cflag |= CSTOPB;    /*2 stop bits*/
                break;
            }
        default:
            {
                cfg.c_cflag &= ~CSTOPB;    /*1 stop bits*/
                break;
            }
        }

        /*�������������*/
        int nFlowType = mComParam.nFlowType;
        switch(nFlowType)
        {
        case 0:
            {
                cfg.c_cflag &= ~CRTSCTS;//no flow control
                break;
            }
        case 1:
            {
                cfg.c_cflag |= CRTSCTS;//hardware flow control
                break;
            }
        case 2:
            {
                cfg.c_iflag |= IXON | IXOFF |IXANY; //software flow control
                break;
            }
        default:
            {
                cfg.c_cflag &= ~CRTSCTS;//no flow control
                break;
            }
        }
#endif

        if (tcsetattr(fd, TCSANOW, &cfg))
        {
            LOGE("tcsetattr() failed");
            close(fd);
            /* TODO: throw an exception */
            return NULL;
        }

//        /*other attributions default*/
//        cfg.c_oflag &= ~OPOST;            /*���ģʽ��ԭʼ������*/
//        cfg.c_cc[VMIN]  = 1;            /*�����ַ�, ��Ҫ��ȡ�ַ����С����*/
//        cfg.c_cc[VTIME] = 1;        /*�����ַ�, ��ȡ��һ���ַ�ĵȴ�ʱ��    unit: (1/10)second*/

//        tcflush(fdcom, TCIFLUSH);                /*�������ݿ��Խ��գ�������*/
//        tmp = tcsetattr(fdcom, TCSANOW, &cfg);    /*���������ԣ�TCSANOW�����иı�������Ч*/    tcgetattr(fdcom, &termios_old);

    }

    /* Create a corresponding file descriptor */
    {
        jclass cFileDescriptor = env->FindClass("java/io/FileDescriptor");
        jmethodID iFileDescriptor = env->GetMethodID(cFileDescriptor, "<init>", "()V");
        jfieldID descriptorID = env->GetFieldID(cFileDescriptor, "descriptor", "I");
        mFileDescriptor = env->NewObject(cFileDescriptor, iFileDescriptor);
        env->SetIntField(mFileDescriptor, descriptorID, (jint)fd);
        env->DeleteLocalRef(cFileDescriptor);
    }

    LOGD("get com%d Configuring serial port succes", mComParam.nSerialPortNum);

    return mFileDescriptor;
}

/*
* Class:     cedric_serial_SerialPort
* Method:    close
* Signature: ()V
*/
JNIEXPORT void JNICALL Java_com_android_Samkoonhmi_serial_SerialPort_close
        (JNIEnv *env, jobject thiz)
{
    jclass SerialPortClass = env->GetObjectClass(thiz);
    jclass FileDescriptorClass = env->FindClass("java/io/FileDescriptor");

    jfieldID mFdID = env->GetFieldID(SerialPortClass, "mFd", "Ljava/io/FileDescriptor;");
    jfieldID descriptorID = env->GetFieldID(FileDescriptorClass, "descriptor", "I");

    jobject mFd = env->GetObjectField(thiz, mFdID);
    jint descriptor = env->GetIntField(mFd, descriptorID);

    LOGD("close(fd = %d)", descriptor);

    env->DeleteLocalRef(SerialPortClass);
    env->DeleteLocalRef(FileDescriptorClass);
    close(descriptor);
}


/******************************************************************
* Function: ��ö���ݵĴ�����
* Parameters:
* Return: true or false
******************************************************************/
COM_PORT_PARAM_PROP getComParamFromJni(JNIEnv *env, jobject joComParam)
{
    /*��ȡ��ַ�ṹ��*/
    COM_PORT_PARAM_PROP mComProp;

    /*��ȡJava�е�ʵ����AddrProp*/
    jclass jComParamClass = env->FindClass("com/android/Samkoonhmi/util/COM_PORT_PARAM_PROP");

    /*get int SerialPortNum;*/
    jfieldID joSerialPortNum = env->GetFieldID(jComParamClass, "nSerialPortNum", "I");
    mComProp.nSerialPortNum = env->GetIntField(joComParam, joSerialPortNum);

    /*get int nBaudRate;*/
    jfieldID joBaudRate = env->GetFieldID(jComParamClass, "nBaudRate", "I");
    mComProp.nBaudRate = env->GetIntField(joComParam, joBaudRate);

    /*get int nDataBits;*/
    jfieldID joDataBits = env->GetFieldID(jComParamClass, "nDataBits", "I");
    mComProp.nDataBits = env->GetIntField(joComParam, joDataBits);

    /*get int joParityType;*/
    jfieldID joParityType = env->GetFieldID(jComParamClass, "nParityType", "I");
    mComProp.nParityType = env->GetIntField(joComParam, joParityType);

    /*get int nStopBit;*/
    jfieldID jonStopBit = env->GetFieldID(jComParamClass, "nStopBit", "I");
    mComProp.nStopBit = env->GetIntField(joComParam, jonStopBit);

    /*get int nFlowType;*/
    jfieldID jonFlowType = env->GetFieldID(jComParamClass, "nFlowType", "I");
    mComProp.nFlowType = env->GetIntField(joComParam, jonFlowType);

    env->DeleteLocalRef(jComParamClass);

    return mComProp;
}
