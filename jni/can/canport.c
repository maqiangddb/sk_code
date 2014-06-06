#include <jni.h>

#include <errno.h>
#include <fcntl.h>
#include <limits.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <getopt.h>
#include <libgen.h>

#include <sys/socket.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/ioctl.h>
#include <sys/uio.h>

#include <net/if.h>
#include <pthread.h>

#include "can.h"
#include "raw.h"

#include "libsocketcan.h"
#include "can_config.h"

#if 1
#include <android/log.h>
static const char *TAG = "D_CAN_port";
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)
#else
#define LOGI(fmt, args...) 
#define LOGD(fmt, args...) 
#define LOGE(fmt, args...) 
#endif

const char *can_states[CAN_STATE_MAX] = { "ERROR-ACTIVE", "ERROR-WARNING",
		"ERROR-PASSIVE", "BUS-OFF", "STOPPED", "SLEEPING" };

const char candevices[2][6] = { { 'c', 'a', 'n', '0', 0, 0 }, { 'c', 'a', 'n',
		'1', 0, 0 } };

int devicesindex = 3;
static int is_dump = 0; // 是否接收数据
static int nCacheLen=600;//缓存长度

#ifndef _Included_Samkoonhmi_DCAN_JniInterface
#define _Included_Samkoonhmi_DCAN_JniInterface

#ifdef __cplusplus
extern "C" {
#endif

//com_android_Samkoonhmi_can_can
JNIEXPORT jint JNICALL Java_com_android_Samkoonhmi_can_can_showbitrate(
		JNIEnv *env, jobject thiz, jobject bittimeobj, jint candeviceid) {
	struct can_bittiming bt;
	jclass clazz;
	jfieldID fid;

	if (can_get_bittiming(candevices[candeviceid], &bt) < 0) {
		LOGE("%s: failed to get bitrate\n", candevices[candeviceid]);
		return (-1);
	} else
		LOGI("%s bitrate: %u, sample-point: %0.3f\n",
				candevices[candeviceid], bt.bitrate, (float)((float)bt.sample_point / 1000));

//  clazz = (*env)->GetObjectClass(env, bittimeobj);
	clazz = (*env)->FindClass(env,
			"com/android/Samkoonhmi/can/model/bittiming");
	if (0 == clazz) {
		LOGE("Get bittime ObjectClass returned 0\n");
		return (-1);
	}

	fid = (*env)->GetFieldID(env, clazz, "bitrate", "I");
	(*env)->SetIntField(env, bittimeobj, fid, bt.bitrate);

	fid = (*env)->GetFieldID(env, clazz, "sample_point", "I");
	(*env)->SetIntField(env, bittimeobj, fid, bt.sample_point);

	return 0;
}

JNIEXPORT jint JNICALL Java_com_android_Samkoonhmi_can_can_setbitrate(
		JNIEnv *env, jobject thiz, jobject bittimeobj, jint candeviceid) {
	jclass clazz;
	jfieldID fid;
	struct can_bittiming bt;
	__u32 bitrate = 0;
	__u32 sample_point = 0;
	int err;

	devicesindex = candeviceid + 2;
	clazz = (*env)->GetObjectClass(env, bittimeobj);
	clazz = (*env)->FindClass(env,
			"com/android/Samkoonhmi/can/model/bittiming");
	if (0 == clazz) {
		LOGE("Get bitrate ObjectClass returned 0\n");
		return (-1);
	}

	fid = (*env)->GetFieldID(env, clazz, "bitrate", "I");
	bitrate = (*env)->GetIntField(env, bittimeobj, fid);
	LOGI("bitrate=%d, %d\n", bitrate, candeviceid);

	fid = (*env)->GetFieldID(env, clazz, "sample_point", "I");
	sample_point = (*env)->GetIntField(env, bittimeobj, fid);
	LOGI("sample_point=%d, %s\n", sample_point, candevices[candeviceid]);

	if (sample_point)
		err = can_set_bitrate_samplepoint(candevices[candeviceid], bitrate,
				sample_point);
	else
		err = can_set_bitrate(candevices[candeviceid], bitrate);

	if (err < 0) {
		LOGE("failed to set bitrate of %s to %d \r\n",
				candevices[candeviceid], bitrate);
		return (-1);
	}

	LOGI("Java_com_android_Samkoonhmi_can_can_setbitrate-----\n");

	return 0;
}

JNIEXPORT jint JNICALL Java_com_android_Samkoonhmi_can_can_setbittiming(
		JNIEnv *env, jobject thiz, jobject bittimeobj, jint candeviceid) {
	jclass clazz;
	jfieldID fid;
	struct can_bittiming bt;
	int bt_par_count = 0;

	memset(&bt, 0, sizeof(bt));

//  clazz = (*env)->GetObjectClass(env, bittimeobj);
	clazz = (*env)->FindClass(env,
			"com/android/Samkoonhmi/can/model/bittiming");
	if (0 == clazz) {
		LOGE("Get bittime ObjectClass returned 0\n");
		return (-1);
	}

	fid = (*env)->GetFieldID(env, clazz, "tq", "I");
	bt.tq = (*env)->GetIntField(env, bittimeobj, fid);

	fid = (*env)->GetFieldID(env, clazz, "prop_seg", "I");
	bt.prop_seg = (*env)->GetIntField(env, bittimeobj, fid);

	fid = (*env)->GetFieldID(env, clazz, "phase_seg1", "I");
	bt.phase_seg1 = (*env)->GetIntField(env, bittimeobj, fid);

	fid = (*env)->GetFieldID(env, clazz, "phase_seg2", "I");
	bt.phase_seg2 = (*env)->GetIntField(env, bittimeobj, fid);

	fid = (*env)->GetFieldID(env, clazz, "sjw", "I");
	bt.sjw = (*env)->GetIntField(env, bittimeobj, fid);

	fid = (*env)->GetFieldID(env, clazz, "brp", "I");
	bt.brp = (*env)->GetIntField(env, bittimeobj, fid);

	if (can_set_bittiming(candevices[candeviceid], &bt) < 0) {
		LOGE(stderr, "%s: unable to set bittiming\n", candevices[candeviceid]);
		return (-1);
	}

	return 0;
}

JNIEXPORT jint JNICALL Java_com_android_Samkoonhmi_can_can_showbittiming(
		JNIEnv *env, jobject thiz, jobject bittimeobj, jint candeviceid) {
	jclass clazz;
	jfieldID fid;
	struct can_bittiming bt;

	if (can_get_bittiming(candevices[candeviceid], &bt) < 0) {
		LOGE("%s: failed to get bittiming\n", candevices[candeviceid]);
		return (-1);
	} else
		LOGI("%s bittiming:\n\t"
		"tq: %u, prop-seq: %u phase-seq1: %u phase-seq2: %u "
		"sjw: %u, brp: %u\n",
				candevices[candeviceid], bt.tq, bt.prop_seg, bt.phase_seg1, bt.phase_seg2, bt.sjw, bt.brp);

//  clazz = (*env)->GetObjectClass(env, bittimeobj);
	clazz = (*env)->FindClass(env,
			"com/android/Samkoonhmi/can/model/bittiming");
	if (0 == clazz) {
		LOGE("Get bittime ObjectClass returned 0\n");
		return (-1);
	}

	fid = (*env)->GetFieldID(env, clazz, "tq", "I");
	(*env)->SetIntField(env, bittimeobj, fid, bt.tq);

	fid = (*env)->GetFieldID(env, clazz, "prop_seg", "I");
	(*env)->SetIntField(env, bittimeobj, fid, bt.prop_seg);

	fid = (*env)->GetFieldID(env, clazz, "phase_seg1", "I");
	(*env)->SetIntField(env, bittimeobj, fid, bt.phase_seg1);

	fid = (*env)->GetFieldID(env, clazz, "phase_seg2", "I");
	(*env)->SetIntField(env, bittimeobj, fid, bt.phase_seg2);

	fid = (*env)->GetFieldID(env, clazz, "sjw", "I");
	(*env)->SetIntField(env, bittimeobj, fid, bt.sjw);

	fid = (*env)->GetFieldID(env, clazz, "brp", "I");
	(*env)->SetIntField(env, bittimeobj, fid, bt.brp);

	return 0;
}

/*
 public class DCAN_bittiming_const {
 protected String name;
 protected int tseg1_min;
 protected int tseg1_max;
 protected int tseg2_min;
 protected int tseg2_max;
 protected int sjw_max;
 protected int brp_min;
 protected int brp_max;
 protected int brp_inc;
 };
 //*/
JNIEXPORT jint JNICALL Java_com_android_Samkoonhmi_can_can_showbittimingconst(
		JNIEnv *env, jobject thiz, jobject bittimeconstobj, jint candeviceid) {
	jclass clazz;
	jfieldID fid;
	struct can_bittiming_const btc;

	if (can_get_bittiming_const(candevices[candeviceid], &btc) < 0) {
		LOGE("%s: failed to get bittiming_const\n", candevices[candeviceid]);
		return -1;
	} else
		LOGI("%s bittiming-constants: name %s,\n\t"
		"tseg1-min: %u, tseg1-max: %u, "
		"tseg2-min: %u, tseg2-max: %u,\n\t"
		"sjw-max %u, brp-min: %u, brp-max: %u, brp-inc: %u,\n",
				candevices[candeviceid], btc.name, btc.tseg1_min, btc.tseg1_max, btc.tseg2_min, btc.tseg2_max, btc.sjw_max, btc.brp_min, btc.brp_max, btc.brp_inc);

//  clazz = (*env)->GetObjectClass(env, bittimeconstobj);
	clazz = (*env)->FindClass(env,
			"com/android/Samkoonhmi/can/model/bittimingconst");
	if (0 == clazz) {
		LOGE("Get bittimeconst ObjectClass returned 0\n");
		return (-1);
	}

	fid = (*env)->GetFieldID(env, clazz, "name", "Ljava/lang/String;");
	(*env)->SetObjectField(env, bittimeconstobj, fid, btc.name);

	fid = (*env)->GetFieldID(env, clazz, "tseg1_min", "I");
	(*env)->SetIntField(env, bittimeconstobj, fid, btc.tseg1_min);

	fid = (*env)->GetFieldID(env, clazz, "tseg1_max", "I");
	(*env)->SetIntField(env, bittimeconstobj, fid, btc.tseg1_max);

	fid = (*env)->GetFieldID(env, clazz, "tseg2_min", "I");
	(*env)->SetIntField(env, bittimeconstobj, fid, btc.tseg2_min);

	fid = (*env)->GetFieldID(env, clazz, "tseg2_max", "I");
	(*env)->SetIntField(env, bittimeconstobj, fid, btc.tseg2_max);

	fid = (*env)->GetFieldID(env, clazz, "sjw_max", "I");
	(*env)->SetIntField(env, bittimeconstobj, fid, btc.sjw_max);

	fid = (*env)->GetFieldID(env, clazz, "brp_min", "I");
	(*env)->SetIntField(env, bittimeconstobj, fid, btc.brp_min);

	fid = (*env)->GetFieldID(env, clazz, "brp_max", "I");
	(*env)->SetIntField(env, bittimeconstobj, fid, btc.brp_max);

	fid = (*env)->GetFieldID(env, clazz, "brp_inc", "I");
	(*env)->SetIntField(env, bittimeconstobj, fid, btc.brp_inc);

	return 0;

}

JNIEXPORT jint JNICALL Java_com_android_Samkoonhmi_can_can_showstate(
		JNIEnv *env, jobject thiz, jint candeviceid) {
	int state;

	if (can_get_state(candevices[candeviceid], &state) < 0) {
		LOGE("%s: failed to get state \n", candevices[candeviceid]);
		return -1;
	}

	if (state >= 0 && state < CAN_STATE_MAX)
		LOGI("%s state: %s\n", candevices[candeviceid], can_states[state]);
	else
		LOGE("%s: unknown state\n", candevices[candeviceid]);

	return state;
}

JNIEXPORT jint JNICALL Java_com_android_Samkoonhmi_can_can_showclockfreq(
		JNIEnv *env, jobject thiz, jint candeviceid) {
	struct can_clock clock;

	memset(&clock, 0, sizeof(struct can_clock));
	if (can_get_clock(candevices[candeviceid], &clock) < 0) {
		LOGE("%s: failed to get clock parameters\n", candevices[candeviceid]);
		return -1;
	}

	LOGI("%s clock freq: %u\n", candevices[candeviceid], clock.freq);

	return clock.freq;

}

JNIEXPORT jint JNICALL Java_com_android_Samkoonhmi_can_can_restart(JNIEnv *env,
		jobject thiz, jint candeviceid) {
	if (can_do_restart(candevices[candeviceid]) < 0) {
		LOGE("%s: failed to restart\n", candevices[candeviceid]);
		return -1;
	} else {
		LOGI("%s restarted\n", candevices[candeviceid]);
	}

	return 0;
}

JNIEXPORT jint JNICALL Java_com_android_Samkoonhmi_can_can_start(JNIEnv *env,
		jobject thiz, jint candeviceid) {
	LOGI("start: %d, %s\r\n", candeviceid, candevices[candeviceid]);
	if (can_do_start(candevices[candeviceid]) < 0) {
		LOGE("%s: failed to start\n", candevices[candeviceid]);
		return -1;
	} else {
		LOGI("%s: success to start\n", candevices[candeviceid]);
		Java_com_android_Samkoonhmi_can_can_showstate(env, thiz, candeviceid);
	}
	return 0;
}

JNIEXPORT jint JNICALL Java_com_android_Samkoonhmi_can_can_stop(JNIEnv *env,
		jobject thiz, jint candeviceid) {
	LOGI("stop: %d, %s\r\n", candeviceid, candevices[candeviceid]);
	is_dump = 0;
	if (can_do_stop(candevices[candeviceid]) < 0) {
		LOGE("%s: failed to stop\n", candevices[candeviceid]);
		return -1;
	} else {
		LOGI("%s: success to stop\n", candevices[candeviceid]);
		Java_com_android_Samkoonhmi_can_can_showstate(env, thiz, candeviceid);
	}
	return 0;
}

static inline void print_ctrlmode(__u32 cm_flags) {
	LOGI("loopback[%s], listen-only[%s], tripple-sampling[%s],"
	"one-shot[%s], berr-reporting[%s]\n",
			(cm_flags & CAN_CTRLMODE_LOOPBACK) ? "ON" : "OFF", (cm_flags & CAN_CTRLMODE_LISTENONLY) ? "ON" : "OFF", (cm_flags & CAN_CTRLMODE_3_SAMPLES) ? "ON" : "OFF", (cm_flags & CAN_CTRLMODE_ONE_SHOT) ? "ON" : "OFF", (cm_flags & CAN_CTRLMODE_BERR_REPORTING) ? "ON" : "OFF");
}

JNIEXPORT jint JNICALL Java_com_android_Samkoonhmi_can_can_showctrlmode(
		JNIEnv *env, jobject thiz, jint candeviceid) {
	struct can_ctrlmode cm;

	if (can_get_ctrlmode(candevices[candeviceid], &cm) < 0) {
		LOGE("%s: failed to get controlmode\n", candevices[candeviceid]);
		return -1;
	} else {
		LOGI("%s ctrlmode: ", candevices[candeviceid]);
		print_ctrlmode(cm.flags);
	}
	return cm.flags;
}

JNIEXPORT jint JNICALL Java_com_android_Samkoonhmi_can_can_setctrlmode(
		JNIEnv *env, jobject thiz, jint d_can_ctrlmode, jint candeviceid) {
	struct can_ctrlmode cm;

	memset(&cm, 0, sizeof(cm));

	cm.flags = d_can_ctrlmode;
	cm.mask = d_can_ctrlmode;

	LOGI("setctrlmode: %d, %s\r\n", candeviceid, candevices[candeviceid]);

	if (can_set_ctrlmode(candevices[candeviceid], &cm) < 0) {
		LOGE("%s: failed to set ctrlmode\n", candevices[candeviceid]);
		return -1;
	}
	return 0;
}

JNIEXPORT jint JNICALL Java_com_android_Samkoonhmi_can_can_showrestartms(
		JNIEnv *env, jobject thiz, jint candeviceid) {
	__u32 restart_ms;

	if (can_get_restart_ms(candevices[candeviceid], &restart_ms) < 0) {
		LOGE("%s: failed to get restart_ms\n", candevices[candeviceid]);
		return -1;
	} else
		LOGI("%s restart-ms: %u\n", candevices[candeviceid], restart_ms);

	return restart_ms;
}

JNIEXPORT jint JNICALL Java_com_android_Samkoonhmi_can_can_setrestartms(
		JNIEnv *env, jobject thiz, jint restartms, jint candeviceid) {
	if (can_set_restart_ms(candevices[candeviceid], restartms) < 0) {
		LOGE("failed to set restart_ms of %s to %lu\n",
				candevices[candeviceid], restartms);
		return -1;
	}

	return 0;
}

/*
 public class DCAN_berr_counter {
 protected short txerr;
 protected short rxerr;
 };
 //*/
JNIEXPORT jint JNICALL Java_com_android_Samkoonhmi_can_can_showberrcounter(
		JNIEnv *env, jobject thiz, jobject berrcounter, jint candeviceid) {
	jclass clazz;
	jfieldID fid;
	struct can_berr_counter bc;
	struct can_ctrlmode cm;

	if (can_get_ctrlmode(candevices[candeviceid], &cm) < 0) {
		LOGE("%s: failed to get controlmode\n", candevices[candeviceid]);
		return -1;
	}

	if (cm.flags & CAN_CTRLMODE_BERR_REPORTING) {
		memset(&bc, 0, sizeof(struct can_berr_counter));

		if (can_get_berr_counter(candevices[candeviceid], &bc) < 0) {
			LOGE("%s: failed to get berr counters\n", candevices[candeviceid]);
			return -1;
		}

		LOGI("%s txerr: %u rxerr: %u\n",
				candevices[candeviceid], bc.txerr, bc.rxerr);

//    clazz = (*env)->GetObjectClass(env, berrcounter);
		clazz = (*env)->FindClass(env,
				"com/android/Samkoonhmi/can/model/berrcounter");
		if (0 == clazz) {
			LOGE("Get berrcounter ObjectClass returned 0\n");
			return (-1);
		}

		fid = (*env)->GetFieldID(env, clazz, "txerr", "S");
		(*env)->SetShortField(env, berrcounter, fid, bc.txerr);

		fid = (*env)->GetFieldID(env, clazz, "rxerr", "S");
		(*env)->SetShortField(env, berrcounter, fid, bc.rxerr);

	}

	return 0;

}

/*
 public class DCAN_canframe {
 protected int can_id;
 protected byte    can_dlc;
 protected byte    data[8];
 };
 //*/
JNIEXPORT jint JNICALL Java_com_android_Samkoonhmi_can_can_send(JNIEnv *env,
		jobject thiz, jobject canframeobj, jint frameextended, jint candeviceid) //jint canid, jbyteArray canframedata, jint datalen
{
	jclass clazz;
	jfieldID fid;
	struct w_frame frame = { .can_id = 1, };
	struct ifreq ifr;
	struct sockaddr_can addr;
	char *interface;
	int family = PF_CAN, type = SOCK_RAW, proto = CAN_RAW;
	int loopcount = 1, infinite = 0;
	int s, opt, ret, i, dlc = 0, rtr = 0, extended = 0;
	int verbose = 1;
	unsigned char canframedata[8];
	jbyteArray jDataList = NULL;
	jbyte * pDataList = NULL;
	unsigned char datalen = 0;

	memset(canframedata, 0, 8);
	//LOGI("send: %d, %s\r\n", candeviceid, candevices[candeviceid]);
	clazz = (*env)->FindClass(env, "com/android/Samkoonhmi/can/model/canframe");
	if (0 == clazz) {
		LOGE("Send Get canframe ObjectClass returned 0\n");
		return (-1);
	}

	fid = (*env)->GetFieldID(env, clazz, "can_id", "I");
	frame.can_id = (*env)->GetIntField(env, canframeobj, fid);

	fid = (*env)->GetFieldID(env, clazz, "can_dlc", "B");
	datalen = (*env)->GetByteField(env, canframeobj, fid);

	fid = (*env)->GetFieldID(env, clazz, "data", "[B");
	jDataList = (jbyteArray)(*env)->GetObjectField(env, canframeobj, fid);
	if (jDataList != NULL) {
		pDataList = (*env)->GetByteArrayElements(env, jDataList, 0);
		if (0 != pDataList) {
			for (i = 0; i < 8; i++)
				canframedata[i] = ((unsigned char *) pDataList)[i];
			(*env)->ReleaseByteArrayElements(env, jDataList, pDataList, 0);
		}
	}

	interface = candevices[candeviceid];
	extended = frameextended;

	//LOGI("Send: interface = %s, family = %d, type = %d, proto = %d, extended = %d\n",
	//       interface, family, type, proto, extended);
	//LOGI("Send2: %d, %d, %x\n", frame.can_id, datalen, canframedata[0]);

	s = socket(family, type, proto);
	if (s < 0) {
		//LOGE("Send: socket error");
		return 1;
	}

	addr.can_family = family;
	strcpy(ifr.ifr_name, interface);
	if (ioctl(s, SIOCGIFINDEX, &ifr)) {
		LOGE("Send: ioctl error");
		close(s);
		return 1;
	}
	addr.can_ifindex = ifr.ifr_ifindex;

	if (bind(s, (struct sockaddr *) &addr, sizeof(addr)) < 0) {
		LOGE("Send: bind error");
		close(s);
		return 1;
	}

	for (i = 0; i < datalen; i++) {
		frame.data[i] = ((__u8 *) canframedata)[i];
		dlc++;
		if (dlc == 8)
			break;
	}
	frame.can_dlc = datalen;
	frame.can_dlc = dlc;

	if (extended) {
		frame.can_id &= CAN_EFF_MASK;
		frame.can_id |= CAN_EFF_FLAG;
	} else {
		frame.can_id &= CAN_SFF_MASK;
	}

	if (rtr)
		frame.can_id |= CAN_RTR_FLAG;

//	if (verbose) {
//		LOGI("Send: id: %x ", frame.can_id);
//		LOGI("Send: dlc: %d\n", frame.can_dlc);
//		for (i = 0; i < frame.can_dlc; i++)
//			LOGI("Send: 0x%02x ", frame.data[i]);
//		LOGI("\n");
//	}

	while (infinite || loopcount--) {
		ret = write(s, &frame, sizeof(frame));
		if (ret == -1) {
			LOGE("Send: write error");
			break;
		}
	}

	close(s);
	return 0;
}

static struct can_filter *filter = NULL;
static int filter_count = 0;

JNIEXPORT jint JNICALL Java_com_android_Samkoonhmi_can_can_addfilter(
		JNIEnv *env, jobject thiz, jint id, jint mask, jint candeviceid) {
	filter = realloc(filter, sizeof(struct can_filter) * (filter_count + 1));
	if (!filter)
		return -1;

	filter[filter_count].can_id = id;
	filter[filter_count].can_mask = mask;
	filter_count++;

	LOGI("add_filter id: 0x%08x mask: 0x%08x\n", id, mask);
	return 0;
}

#define BUF_SIZ	(255)

static int s = -1;

static m_frame *head;

JNIEXPORT jint JNICALL Java_com_android_Samkoonhmi_can_can_startdump(
		JNIEnv *env, jobject thiz, jint candeviceid) {

	LOGE("Java_com_android_Samkoonhmi_can_can_startdump ......  ");

	if (is_dump == 1) {
		return -1;
	}
	is_dump = 1;
	head = (m_frame *) malloc(sizeof(m_frame));
	head->next = NULL;

	struct can_frame frame;
	struct ifreq ifr;
	struct sockaddr_can addr;
	char *interface = "can0";
	char *optout = NULL;
	char *ptr;
	char buf[BUF_SIZ];
	int family = PF_CAN, type = SOCK_RAW, proto = CAN_RAW;
	int n = 0, err;
	int nbytes, i;
	int opt, optdaemon = 0;
	uint32_t id, mask;
	jbyteArray jDataList = NULL;
	jbyte * pDataList = NULL;

	interface = candevices[candeviceid];

	//先关闭之前socket
	close(s);

	if ((s = socket(family, type, proto)) < 0) {
		//LOGE("dump socket error");
		return 1;
	}

	addr.can_family = family;
	strncpy(ifr.ifr_name, interface, sizeof(ifr.ifr_name));
	if (ioctl(s, SIOCGIFINDEX, &ifr)) {
		LOGE("dump ioctl error");
		return 1;
	}
	addr.can_ifindex = ifr.ifr_ifindex;

	if (bind(s, (struct sockaddr *) &addr, sizeof(addr)) < 0) {
		LOGE("dump bind error");
		return 1;
	}

	if (filter) {
		if (setsockopt(s, SOL_CAN_RAW, CAN_RAW_FILTER, filter,
				filter_count * sizeof(struct can_filter)) != 0) {
			LOGE("dump setsockopt error");
			return 1;
		}
	}

	while (is_dump) {

		if ((nbytes = read(s, &frame, sizeof(struct can_frame))) >= 0) {
			struct can_frame temp = frame;
			do_data(0, temp);
			//LOGE("dump read id = %d ,count = %d \n", temp.can_id,count);
		}

		//sleep(1);
	}

}

/**
 * 设置缓存长度
 */

JNIEXPORT jint JNICALL Java_com_android_Samkoonhmi_can_can_cacheLen(
		JNIEnv *env, jobject thiz, jint cacheLen){
	LOGE("cacheLen = %d",cacheLen);
	if(cacheLen<5){
		cacheLen=5;
	}else if(cacheLen>2000){
		cacheLen=2000;
	}
	nCacheLen=cacheLen;
	return 0;
}

//数据处理
int count = 0;
struct can_frame frames;
//同步锁
pthread_mutex_t mutex;
void do_data(int type, struct can_frame frame) {

	//线程同步
	pthread_mutex_lock(&mutex);
	if (type == 0) {
		//添加
		if (head != NULL) {

			while(deleteData()==1){
				//LOGE("------------");
			};

			count++;
			m_frame *p = head;
			while (p->next != NULL) {
				p = p->next;
			}
			m_frame *n = (m_frame *) malloc(sizeof(m_frame));
			*n = frame;
			n->next = p->next;
			p->next = n;

		}

	} else if (type == 1) {
		//获取
		if (count > 0) {
			m_frame * temp;
			temp = head->next;

			if (head->next != NULL) {
				m_frame *p_delete = head->next;
				head->next = p_delete->next;

			}
			count--;
			frames = *temp;
			frames.state = 1;
			free(temp);
			temp = NULL;
			//LOGE("state =%d ",frames.state);
		}
	}
	pthread_mutex_unlock(&mutex);
}

//删除缓存数据
int deleteData(){
	if (count > nCacheLen) {
		//缓存最大为nCacheLen
		if (head->next != NULL) {
			m_frame *p_delete = head->next;
			head->next = p_delete->next;
			free(p_delete);
			p_delete = NULL;
			count--;
		}
	}
	int result=0;
	if(count > nCacheLen){
		result=1;
	}
	//LOGE("count = %d,nCacheLen = %d,result = %d",count,nCacheLen,result);
}

JNIEXPORT jint JNICALL Java_com_android_Samkoonhmi_can_can_dump(JNIEnv *env,
		jobject thiz, jobject canframeobj, jint candeviceid) {
	jclass clazz;
	jfieldID fid;
	int i = 0;
	jbyteArray jDataList = NULL;
	jbyte * pDataList = NULL;

	struct can_frame f;
	do_data(1, f);

	if (frames.state == 0) {
		return -1;
	}
	frames.state = 0;
	struct can_frame frame = frames;

	//LOGE("dump get data id = %d ,d =%d \n", frame.can_id,frame.data[0]);

	clazz = (*env)->FindClass(env, "com/android/Samkoonhmi/can/model/canframe");
	if (0 == clazz) {
		LOGE("dump Get canframe ObjectClass returned 0\n");
		return (-1);
	}

	if (frame.can_id & CAN_EFF_FLAG) {
		frame.can_id = frame.can_id & CAN_EFF_MASK;
	} else {
		frame.can_id = frame.can_id & CAN_SFF_MASK;
	}

	fid = (*env)->GetFieldID(env, clazz, "can_id", "I");
	(*env)->SetIntField(env, canframeobj, fid, frame.can_id);

	fid = (*env)->GetFieldID(env, clazz, "can_dlc", "B");
	(*env)->SetByteField(env, canframeobj, fid, frame.can_dlc);

	fid = (*env)->GetFieldID(env, clazz, "data", "[B");
	jDataList = (*env)->NewByteArray(env, 8);

	if (0 != jDataList) {
		pDataList = (*env)->GetByteArrayElements(env, jDataList, 0);
		if (0 != pDataList) {
			for (i = 0; i < 8; i++) {
				((unsigned char *) pDataList)[i] = frame.data[i];
				//LOGE("dump get data id = %d ,d =%d \n", frame.can_id,pDataList[i]);
			}
			(*env)->SetObjectField(env, canframeobj, fid, jDataList);
			(*env)->ReleaseByteArrayElements(env, jDataList, pDataList, 0);
		}
	}

	return 0;
}

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
	void *venv;

	LOGI("call JNI_OnLoad!");
	if ((*vm)->GetEnv(vm, (void**) &venv, JNI_VERSION_1_4) != JNI_OK) {
		LOGE("ERROR: GetEnv failed");
		return -1;
	}

	return JNI_VERSION_1_4;
}

void JNI_OnUnload(JavaVM* vm, void* reserved) {
	LOGI("call JNI_OnUnload ~~!!");
}

#ifdef __cplusplus
}
#endif

#endif

