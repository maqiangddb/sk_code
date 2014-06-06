package com.android.Samkoonhmi.util;

public class SystemParam {
	
	public static final int DEFAULT_SET= 0x00000000;// – DEFAULT_SET默认设置
	public static final int VERTICAL_SHOW=0x00000001;//垂直显示
	public static final int USE_SAVER=0x00000002;// 使用屏保
	public static final int LOGOUT=0x00000004;//进入屏保注销用户
	public static final int USE_TOUCH_SOUND=0x00000008;//-USE_TOUCH_SOUND使用触摸声音。
	public static final int WRITE_SCENE_ID=0x00000010;//把当前画面写入PLC
	public static final int WRITE_RECIPE=0x00000020;//-WRITE_RECIPE写当前配方号
	public static final int WRITE_LANGUAGE=0x00000040;//写当前语言号
	public static final int WORD_CHANGE_SCENE=0x00000080;//字切换画面
	public static final int UP_LOAD=0x00000100 ;//允许上载组态
	public static final int UP_LOAD_USE_PWD=0x00000200;//--UP_LOAD_USE_PWD//上载需要密码
	public static final int HMI_PROTECT=0x00000400 ;//--HMI_PROTECT //使用时效权限
	public static final int LONG_INSPECT       = 0x00000800;////是否启动远程监控
    public static final int VNC_CONNECT_MUCH = 0x00001000;//允许多人访问vnc
	
//   public static final int DEFAULT_SET=0x00000000;//默认设置
//   public static final int VERTICAL_SHOW=0x00000001;//垂直显示
//   public static final int USE_SAVER=0x00000002;//使用屏保
//   public static final int LOGOUT=0x00000004;//进入屏保退出登录
//   public static final int USE_TOUCH_SOUND=0x00000008;//使用触摸声音
//   public static final int MOUSE_TOUCH=0x00000010;//鼠标操作
//   public static final int SHOW_MOUSE =0x00000020;//显示鼠标
//   public static final int USE_CUSTOM_INVALID=0x00000040;//使用自定义触控无效图片.
//   public static final int PIC_TRANS=0x00000080;//设置图片透明
//   public static final int WRITE_SCENE_ID=0x00000100;//把当前画面写入PLC
//   public static final int WRITE_RECIPE=0x00000200;//写当前配方号
//   public static final int WRITE_LANGUAGE=0x00000400;//写当前语言号
//   public static final int WORD_CHANGE_SCENE=0x00000800;//字切换画面
//   public static final int BIT_CHANGE_SCENE=0x00001000;//位切换画面
//   public static final int DOWN_SYSTEM_TIME=0x00002000;//下载系统时间到HMI
//   
//   public static final int USE_ALARM_SOUND=0x00004000;//使用报警声音
//   public static final int USE_ALARM_FLICK=0x00008000;//报警时闪烁画面
//   public static final int ENABLE_ALARM=0x00010000;//启用系统报警
//   public static final int COMPRESS_PIC=0x00020000;//下载时保留原图片格式
//   public static final int DOWN_RESTART= 0x00040000;//下载后启动HMI
//   public static final int DOWN_CLEAR_HIS=0x00080000;//下载后清除历史数据
//   public static final int DOWN_CLEAR_ALARM=0x00100000;//下载清除报警
//   public static final int DOWN_LOAD_RECIPE=0x00200000;//下载时写入配方
//   public static final int UP_LOAD=   0x00400000;//允许上载组态
//   public static final int UP_LOAD_USE_PWD=0x00800000;//上载需要密码
//   public static final int HMI_PROTECT= 0x01000000; //使用时效权限




}
