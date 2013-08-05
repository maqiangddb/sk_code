package com.android.Samkoonhmi.util;

/**
 * 后台线程模块ID
 * 
 * @author 刘伟江
 * @version v 1.0.0.1 创建时间 2012-5-28
 */
public class MODULE {

	// 测试用
	public static final int MODULE_TEST = 0;

	// 图片显示模块
	public static final int IMAGEVIEWER = 1;

	// 动画显示器 ;
	public static final int SKANIMATION = 2;

	// 画面
	public static final int SCENE = 4;

	// 曲线计算模块
	public static final int CAL_CURVE = 5;

	/* 数值输入显示器 */
	public static final int NUMBER_INPUT_SHOW = 7;

	// 基本模块,用于所有控件信息读取
	public static final int BASESHAP = 8;

	// 报警模块
	public static final int ALARM = 9;

	// 历史数据
	public static final int HISTORY = 10;
	
	// 带回调接口模块
	public static final int CALLBACK=11;
	//注册
	public static final int REGIST=12;
	//移除
	public static final int REMOVE=13;
	
	//系统
	public static final int SYSTEM_CONTROL=14;

	/******************** 以下是通知线程 SKPlcNoticThread 的消息 *******************************/
	/* plc通知信号 */
	public static final int NOTIC_UI_REFREASH = 31;

	/* plc地址查询刷新线程 */
	public static final int LOCAL_ADDR_REFREASH = 32;

	/* 控件添加地址的消息 */
	public static final int PLC_NOTIC_VALUE_CHANGE = 33;

	/******************** 以下是消息 SKPlcNoticThread 、DataCollect共用 *******************************/
	/* 控件添加地址的消息 */
	public static final int ADD_CALL_BACK = 34;

	/* 通知刷新线程清除回调 */
	public static final int NOTIC_CLEAR_CALLBACK = 35;

	/******************** 以下是配方中心线程 RecipeDataCentre 的消息 *******************************/
	/* 配方采集 控件的初始化 */
	public static final int RECIPER_COLLECT_INIT = 36;

	/* 配方刷新 通知 */
	public static final int RECIPER_COLLECT_NOTIC = 37;

	/* 配方保存 */
	public static final int RECIPER_EDIT_SAVE = 38;

	/* 配方删除 */
	public static final int RECIPER_DELETE = 39;

	/* 配方组删除 */
	public static final int RECIPER_GROUP_DELETE = 40;

	/* 从PLC读配方 */
	public static final int READ_RECIPE_FROM_PLC = 41;

	/* 写配方到PLC去 */
	public static final int WRITE_RECIPE_TO_PLC = 42;

	/* 导出配方到文件去 */
	public static final int WRITE_RECIPE_TO_FILE = 43;

	/* 从文件中导入配方 */
	public static final int READ_RECIPE_FROM_FILE = 44;

	/******************** 以下是数据采集线程 DataCollect 的消息 *******************************/
	/* 数据采集 控件的初始化 */
	public static final int DATA_COLLECT_INIT = 45;

	/* 数据采集 通知 */
	public static final int DATA_COLLECT_NOTIC = 46;

	/* 数据采集回调烧毁 通知 */
	public static final int DATA_COLLECT_DESTORY_NOTIC = 47;

	/* 数据采集回调烧毁 通知 */
	public static final int DATA_COLLECT_CLEAR = 48;
	
	public static final int CANCEL_CALL_BACK = 49;

	/******************** 以下是数据采集线程 SKCommThread 的消息 *******************************/
	/* 通信类型 */
	public static final int USER_READ = 50; // 用户读
	public static final int USER_WRITE = 51; // 用户写
	public static final int SYSTEM_MAST_READ = 52; // 系统主站读
	public static final int SYSTEM_MAST_WRITE = 53; // 系统主站写
	public static final int SYSTEM_SLAVE_READ = 54; // 系统从站读
	public static final int SYSTEM_NET_SLAVE = 55; // 系统从站写

	public static final int NOTIC_SHOW_TOAST = 56; // 通知主线程显示提示信息
	public static final int NOTIC_SHOW_PRESS = 57; // 通知主线程显示提示信息
	public static final int NOTIC_HIDE_PRESS = 58; // 通知主线程显示提示信息
	public static final int TURN_MSG_DATA = 59;    // 转发消息

	/******************** 以下是全局后台线程GlobalBackHandler 的消息 *******************************/

	/* 历史数据的读取排队消息 */
	public static final int READ_HISTORY_FROM_DATABASE = 60;

	/* 历史数据的保存排队消息 */
	public static final int WRITE_HISTORY_TO_DATABASE = 61;
	
	/*掉电保存还原写数据库*/
	public static final int WRITE_POWER_SAVE_DATABASE = 74;
	
	/*穿透主站控制循环消息*/
	public static final int PENETRATE_MAST_CTL_MSG = 75;
	
	/*穿透从站控制循环消息*/
	public static final int PENETRATE_SLAVE_CTL_MSG = 76;

	/* 历史数据的保存到文件排队消息 */
	public static final int WRITE_HISTORY_TO_FILE = 62;

	/* 历史数据的删除 */
	public static final int DELETE_HISTORY_FROM_DATABASE = 63;

	/* 历史数据的读取排队消息 */
	public static final int AFTER_SAVE_DATABASE = 64;

	/* 用户读PLC数据 */
	public static final int USER_READ_PLC = 65;

	/* 用户写PLC数据 */
	public static final int USER_WRITE_PLC = 66;

	/* 刷新从PLC中读取的配方 */
	public static final int REFRESH_READ_RECIPE = 67;

	/* 发送数据到通信口 */
	public static final int SEND_DATA_TO_CMNPORT = 68;

	/* 历史数据的保存到文件排队消息 */
	public static final int BEGAI_SAVE_HISTORY_TO_FILE = 69;
	
	/* 更新当前配方的数据 */
	public static final int REFRESH_CURR_RECIPE = 70;
	
	/* 资料传输，刷新 */
	public static final int DATA_TRANS_REFRESH = 71;
	
	/* 资料传输，传输一组资料 */
	public static final int TRANS_ONCE_DATA = 72;
	
	/*通知注销回调*/
	public static final int DESTORY_CALLBACK = 73;

	// 动态矩形
	public static final int SKDYNAMICRECT = 80;

	// 动态圆形
	public static final int SKDYNAMICCIRCLE = 81;

	// GIF动画显示
	public static final int SKGIFVIEWER = 82;
	
	//
	
	//

}
