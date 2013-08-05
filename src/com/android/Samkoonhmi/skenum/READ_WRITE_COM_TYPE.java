package com.android.Samkoonhmi.skenum;

public class READ_WRITE_COM_TYPE {
	public static final short SCENE_CONTROL_ONCE_W = 0x01;                       // scene Control only write once
	public static final short SCENE_CONTROL_LOOP_W = 0x02;                       // scene Control loop write
	public static final short GLOBAL_ONCE_W = 0x03;                              // global only write once
	public static final short GLOBAL_LOOP_W = 0x04;                              // global loop write
	public static final short MACRO_ONCE_W = 0x05;                               // macro only write once
	public static final short MACRO_LOOP_W = 0x06;                               // macro loop write
	public static final short SCENE_CONTROL_ONCE_R = 0x07;                       // scene Control only read once
	public static final short SCENE_CONTROL_LOOP_R = 0x08;                       // scene Control loop read
	public static final short ALARM_LOOP_R = 0x09;                               // alarm loop read
	public static final short RECIPE_ONCE_R = 0x0a;                              // recipe once read
	public static final short GLOBAL_ONCE_R = 0x0b;                              // global only read once
	public static final short GLOBAL_LOOP_R = 0x0c;                              // global loop read
	public static final short MACRO_ONCE_R = 0x0d;                               // macro only read once
	public static final short MACRO_LOOP_R = 0x0e;                               // macro loop read
	public static final short DATA_COLLECT_ONCE_R = 0x0f;                        // data collect only read once
	public static final short DATA_COLLECT_LOOP_R = 0x10;                        // data collect loop read
	public static final short OTHER_CONTROL_TYPE = 0x11;                         // other control type
}
