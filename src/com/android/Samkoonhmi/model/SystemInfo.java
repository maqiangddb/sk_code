package com.android.Samkoonhmi.model;

import java.util.List;
import android.util.Log;
import com.android.Samkoonhmi.skenum.HMIMODEL;
import com.android.Samkoonhmi.skenum.PRINT_MODEL;
import com.android.Samkoonhmi.util.AddrProp;
import com.android.Samkoonhmi.util.ParameterSet;
import com.android.Samkoonhmi.util.SystemParam;

public class SystemInfo {
	private static String nModel = ""; // 触摸屏型号
	private static String sStartScreen; // 启动画面序号
	private static int nScreenTime; // 屏幕保护的时间~~~
	private static boolean bScreensaver; // 屏保显示方式(显示指定画面(true)，亮度变化(false))
	private static String sScreenIndex; // 屏保显示的画面号
	private static int nFlickRate = 10 * 100;// 闪烁频率
	private static int nBrightness; // 屏保亮度设置
	private static int nSetBoolParam; // 设置的一些参数SystemParam
	private static AddrProp nkWriteScreenAddr; // 写入画面的地址;
	private static AddrProp nkRecipeIndexAddr; // 写入配方的地址
	private static AddrProp nkWriteLanguageAddr;// 写入当前语言的地址
	private static AddrProp nkChangeScreenAddr; // 字来控制画面切换的地址
	private static List<PlcConnectionInfo> plcConnectionList; // 连接的属性
	private static String sUploadPassword; // 上载密码
	private static int languageNumber; // 语言数
	private static List<LanguageInfo> languageList; // 语言列表
	private static boolean bProtectType; // 时效授权方式(按使用时间（false） 按截至日期（true）)
	// private static String sProtectValue; // 授权终止后跳转画面 字符串
	private static List<PassWordInfo> passWord;// 密码表信息
	private static int currentScenceId;// 当前画面号
	private static int initSceneId;// 初始画面号
	private static CurrentRecipe currentRecipe;// 当前配方
	private static int currentLanguageId;// 当前语言号
	private static PassWordInfo onePassWord;
	private static UserInfo gloableUser;
	private static UserInfo defaultUser;// 默认用户
	private static int currentTouchX = 0;// 当前触摸横坐标
	private static int currentTouchY = 0;// 当前触摸纵坐标
	private static String loginTime;
	private static int nstartLB;
	private static int nlengthLB;
	private static int nstartLW;
	private static int nlengthLW;
	private static List<String> userNameList;
	private static boolean bBitScene; // 位地址控制画面切换
	private static List<BitSceneModle> bitSceneList;// 位控制画面切换集合
	private static short bSimulator; // 在线模拟还是离线模拟, 0: 屏上运行（默认）1：是离线， 2：是在线
	private static String sTgNum;// 3g号码
	private static String sSmsMsg;// 发送内容
	private static String strHmiName;// 别名
	private static boolean bLockIcon;// 权限不足是否显示锁
	private static String strMonitor;// 远程监控密码
	private static int nMonitorPort ;//远程监控端口
	private static HMIMODEL model;//触摸屏型号
	private static PRINT_MODEL mPrintModel;//打印机型号
	
	public static PRINT_MODEL getmPrintModel() {
		return mPrintModel;
	}

	public static void setmPrintModel(PRINT_MODEL mPrintModel) {
		SystemInfo.mPrintModel = mPrintModel;
	}

	public static HMIMODEL getModel() {
		return model;
	}

	public static void setModel(HMIMODEL model) {
		SystemInfo.model = model;
	}

	public static int getnMonitorPort() {
		return nMonitorPort;
	}

	public static void setnMonitorPort(int nMonitorPort) {
		SystemInfo.nMonitorPort = nMonitorPort;
	}

	public static String getStrHmiName() {
		return strHmiName;
	}

	public static void setStrHmiName(String strHmiName) {
		SystemInfo.strHmiName = strHmiName;
	}

	public static boolean isbLockIcon() {
		return bLockIcon;
	}

	public static void setbLockIcon(boolean bLockIcon) {
		SystemInfo.bLockIcon = bLockIcon;
	}

	public static String getStrMonitor() {
		return strMonitor;
	}

	public static void setStrMonitor(String strMonitor) {
		SystemInfo.strMonitor = strMonitor;
	}

	public static String getsTgNum() {
		return sTgNum;
	}

	public static void setsTgNum(String sTgNum) {
		SystemInfo.sTgNum = sTgNum;
	}

	public static String getsSmsMsg() {
		return sSmsMsg;
	}

	public static void setsSmsMsg(String sSmsMsg) {
		SystemInfo.sSmsMsg = sSmsMsg;
	}

	public static short getbSimulator() {
		return bSimulator;
	}

	public static void setbSimulator(short bSimulator) {
		SystemInfo.bSimulator = bSimulator;
	}

	public static int getInitSceneId() {
		return initSceneId;
	}

	public static void setInitSceneId(int initSceneId) {
		SystemInfo.initSceneId = initSceneId;
	}

	public static List<BitSceneModle> getBitSceneList() {
		return bitSceneList;
	}

	public static void setBitSceneList(List<BitSceneModle> bitSceneList) {
		SystemInfo.bitSceneList = bitSceneList;
	}

	public static boolean isbBitScene() {
		return bBitScene;
	}

	public static void setbBitScene(boolean bBitScene) {
		SystemInfo.bBitScene = bBitScene;
	}

	public static List<String> getUserNameList() {
		return userNameList;
	}

	public static void setUserNameList(List<String> userNameList) {
		SystemInfo.userNameList = userNameList;
	}

	public SystemInfo() {
		super();
	}

	public static int getCurrentTouchX() {
		return currentTouchX;
	}

	public static String getLoginTime() {
		return loginTime;
	}

	public static void setLoginTime(String loginTime) {
		SystemInfo.loginTime = loginTime;
	}

	public static void setCurrentTouchX(int currentTouchX) {
		SystemInfo.currentTouchX = currentTouchX;
	}

	public static int getCurrentTouchY() {
		return currentTouchY;
	}

	public static void setCurrentTouchY(int currentTouchY) {
		SystemInfo.currentTouchY = currentTouchY;
	}

	public static UserInfo getGloableUser() {
		return gloableUser;
	}

	public static void setGloableUser(UserInfo gloableUser) {
		SystemInfo.gloableUser = gloableUser;
	}

	public static UserInfo getDefaultUser() {
		return defaultUser;
	}

	public static void setDefaultUser(UserInfo defaultUser) {
		SystemInfo.defaultUser = defaultUser;
	}

	public static PassWordInfo getOnePassWord() {
		return onePassWord;
	}

	public static void setOnePassWord(PassWordInfo onePassWord) {
		SystemInfo.onePassWord = onePassWord;
	}

	public static int getCurrentScenceId() {
		return currentScenceId;
	}

	/**
	 * 设置当前画面号
	 * 
	 * @param currentScenceId
	 */
	public static void setCurrentScenceId(int currentScenceId) {
		SystemInfo.currentScenceId = currentScenceId;

		if ((SystemParam.WRITE_SCENE_ID & SystemInfo.getnSetBoolParam()) == SystemParam.WRITE_SCENE_ID) {

			ParameterSet.getInstance().writeCurrentScence(
					SystemInfo.getCurrentScenceId(),
					SystemInfo.getNkWriteScreenAddr());
		}
	}

	public static CurrentRecipe getCurrentRecipe() {
		if (currentRecipe == null) {
			currentRecipe = new CurrentRecipe();
		}
		return currentRecipe;
	}

	public static void setCurrentRecipe(CurrentRecipe currentRecipe) {
		SystemInfo.currentRecipe = currentRecipe;
		// 如果设置了将当前配方写入地址，则写入地址
		if ((SystemParam.WRITE_RECIPE & SystemInfo.getnSetBoolParam()) == SystemParam.WRITE_RECIPE) {

			ParameterSet.getInstance().writeCurrentRecipe(
					currentRecipe.getCurrentGroupRecipeId(),
					currentRecipe.getCurrentRecipeId(),
					SystemInfo.getNkRecipeIndexAddr());
		}
	}

	public static int getCurrentLanguageId() {
		return currentLanguageId;
	}

	/**
	 * 写入当前语言号
	 * 
	 * @param currentLanguageId
	 */
	public static void setCurrentLanguageId(int currentLanguageId) {
		SystemInfo.currentLanguageId = currentLanguageId;
		if ((SystemParam.WRITE_LANGUAGE & SystemInfo.getnSetBoolParam()) == SystemParam.WRITE_LANGUAGE) {
			ParameterSet.getInstance().writeCurrentLanguage(
					SystemInfo.currentLanguageId,
					SystemInfo.getNkWriteLanguageAddr());
		}
	}

	public static String getnModel() {
		return nModel;
	}

	public static void setnModel(String nModel) {
		SystemInfo.nModel = nModel;
	}

	public static String getsStartScreen() {
		return sStartScreen;
	}

	public static void setsStartScreen(String sStartScreen) {
		SystemInfo.sStartScreen = sStartScreen;
	}

	public static int getnScreenTime() {
		return nScreenTime;
	}

	public static void setnScreenTime(int nScreenTime) {
		SystemInfo.nScreenTime = nScreenTime;

	}

	public static boolean isbScreensaver() {
		return bScreensaver;
	}

	public static void setbScreensaver(boolean bScreensaver) {
		SystemInfo.bScreensaver = bScreensaver;
	}

	public static String getsScreenIndex() {
		return sScreenIndex;
	}

	public static void setsScreenIndex(String sScreenIndex) {
		SystemInfo.sScreenIndex = sScreenIndex;
	}

	public static int getnFlickRate() {
		return nFlickRate;
	}

	public static void setnFlickRate(int nFlickRate) {
		SystemInfo.nFlickRate = nFlickRate;
	}

	public static int getnBrightness() {
		return nBrightness;
	}

	public static void setnBrightness(int nBrightness) {
		SystemInfo.nBrightness = nBrightness;
	}

	public static int getnSetBoolParam() {
		return nSetBoolParam;
	}

	public static void setnSetBoolParam(int nSetBoolParam) {
		SystemInfo.nSetBoolParam |= nSetBoolParam;

	}

	public static void resetnSetBoolParam(int nSetBoolParam) {
		SystemInfo.nSetBoolParam = nSetBoolParam;
	}

	public static AddrProp getNkWriteScreenAddr() {
		return nkWriteScreenAddr;
	}

	public static void setNkWriteScreenAddr(AddrProp nkWriteScreenAddr) {
		SystemInfo.nkWriteScreenAddr = nkWriteScreenAddr;
	}

	public static AddrProp getNkRecipeIndexAddr() {
		return nkRecipeIndexAddr;
	}

	public static void setNkRecipeIndexAddr(AddrProp nkRecipeIndexAddr) {
		SystemInfo.nkRecipeIndexAddr = nkRecipeIndexAddr;
	}

	public static AddrProp getNkWriteLanguageAddr() {
		return nkWriteLanguageAddr;
	}

	public static void setNkWriteLanguageAddr(AddrProp nkWriteLanguageAddr) {
		SystemInfo.nkWriteLanguageAddr = nkWriteLanguageAddr;
	}

	public static AddrProp getNkChangeScreenAddr() {
		return nkChangeScreenAddr;
	}

	public static void setNkChangeScreenAddr(AddrProp nkChangeScreenAddr) {
		SystemInfo.nkChangeScreenAddr = nkChangeScreenAddr;
	}

	public static List<PlcConnectionInfo> getPlcConnectionList() {
		return plcConnectionList;
	}

	public static void setPlcConnectionList(
			List<PlcConnectionInfo> plcConnectionList) {
		SystemInfo.plcConnectionList = plcConnectionList;
	}

	// public static List<PlcAttributeInfo> getPlcAttributeList() {
	// return plcAttributeList;
	// }
	//
	// public static void setPlcAttributeList(List<PlcAttributeInfo>
	// plcAttributeList) {
	// SystemInfo.plcAttributeList = plcAttributeList;
	// }

	public static String getsUploadPassword() {
		return sUploadPassword;
	}

	public static void setsUploadPassword(String sUploadPassword) {
		SystemInfo.sUploadPassword = sUploadPassword;
	}

	public static int getLanguageNumber() {
		return languageNumber;
	}

	public static void setLanguageNumber(int languageNumber) {
		SystemInfo.languageNumber = languageNumber;
	}

	public static List<LanguageInfo> getLanguageList() {
		return languageList;
	}

	public static void setLanguageList(List<LanguageInfo> languageList) {
		SystemInfo.languageList = languageList;
	}

	public static boolean isbProtectType() {
		return bProtectType;
	}

	public static void setbProtectType(boolean bProtectType) {
		SystemInfo.bProtectType = bProtectType;
	}

	// public static String getsProtectValue() {
	// return sProtectValue;
	// }
	//
	// public static void setsProtectValue(String sProtectValue) {
	// SystemInfo.sProtectValue = sProtectValue;
	// }

	public static List<PassWordInfo> getPassWord() {
		return passWord;
	}

	public static void setPassWord(List<PassWordInfo> passWord) {
		SystemInfo.passWord = passWord;
	}

	public static int getnstartLB() {
		return nstartLB;
	}

	public static void setnstartLB(int nstartLB) {
		// System.out.println("nstartLB:" +nstartLB);
		SystemInfo.nstartLB = nstartLB;
	}

	public static int getnlengthLB() {
		return nlengthLB;
	}

	public static void setnlengthLB(int nlengthLB) {
		// System.out.println("nlengthLB:" +nlengthLB);
		SystemInfo.nlengthLB = nlengthLB;
	}

	public static int getnstartLW() {
		return nstartLW;
	}

	public static void setnstartLW(int nstartLW) {
		// System.out.println("nstartLW:" +nstartLW);
		SystemInfo.nstartLW = nstartLW;
	}

	public static int getnlengthLW() {
		return nlengthLW;
	}

	public static void setnlengthLW(int nlengthLW) {
		// System.out.println("nlengthLW:" +nlengthLW);
		SystemInfo.nlengthLW = nlengthLW;
	}

}
