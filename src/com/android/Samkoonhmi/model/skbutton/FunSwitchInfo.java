package com.android.Samkoonhmi.model.skbutton;
import java.util.ArrayList;
import com.android.Samkoonhmi.model.ShowInfo;
import com.android.Samkoonhmi.model.StakeoutInfo;
import com.android.Samkoonhmi.model.TextInfo;
import com.android.Samkoonhmi.model.TouchInfo;
import com.android.Samkoonhmi.skenum.BUTTON;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.FLICK_TYPE;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.SKButton;
import com.android.Samkoonhmi.util.AddrProp;

/**
 * 多功能开关
 */
public class FunSwitchInfo {

	// 控件Id
	private int nItemId;
	// 层id
	private int nZvalue;
	// 控件组合Id
	private int nCollidindId;
	// 控件左上角X坐标
	private short nLp;
	// 控件左上角Y坐标
	private short nTp;
	// 控件宽度
	private short nWidth;
	// 控件高度
	private short nHeight;
	// 控件显示区域左上角X坐标
	private short nShowLp;
	// 控件显示区域左上角Y坐标
	private short nShowTp;
	// 控件显示区域宽
	private short nShowWidth;
	// 控件显示区域高
	private short nShowHeight;
	// 外形路径
	private String sApeaPath;
	//闪烁
	private FLICK_TYPE eFlickType;
	//颜色
	private int nColor;
	//透明度
	private int nAlpha;
	//控件外形，1-图片，2-图库图片，3-外部图片，4-不使用图片
	private short nShapeType;
	// 是否调用宏指令
	private boolean bIsStartStatement;
	// 宏指令ID
	private int nStatementId;
	// 每个状态的文本
	private ArrayList<TextInfo> mTextList;
	// 触控
	private TouchInfo mTouchInfo;
	// 显现
	private ShowInfo mShowInfo;
	//功能集合
	private ArrayList<SKButton> mSkButtons;
	//监视类型
	private BUTTON.WATCH_TYPE eWatchType;
	//地址类型，位-false，字-true
	private boolean bAddrType;
	//字地址的第几位
	private short nBitIndex;
	//切换条件 0-按值，1-寄存器的位,2-自定义值
	private short nCondition;
	//监视地址数据类型
	private DATA_TYPE eWatchDataType;
	//检测地址
	private AddrProp mWatchAddress;
	//检测条件
	private ArrayList<StakeoutInfo> mStakeoutList;
	
	public AddrProp getmWatchAddress() {
		return mWatchAddress;
	}
	public void setmWatchAddress(AddrProp mWatchAddress) {
		this.mWatchAddress = mWatchAddress;
	}
	
	public DATA_TYPE geteWatchDataType() {
		return eWatchDataType;
	}
	public void seteWatchDataType(DATA_TYPE eWatchDataType) {
		this.eWatchDataType = eWatchDataType;
	}
		
	public short getnCondition() {
		return nCondition;
	}
	public void setnCondition(short nCondition) {
		this.nCondition = nCondition;
	}
	
	public short getnBitIndex() {
		return nBitIndex;
	}
	public void setnBitIndex(short nBitIndex) {
		this.nBitIndex = nBitIndex;
	}
	
	public boolean isbAddrType() {
		return bAddrType;
	}
	public void setbAddrType(boolean bAddrType) {
		this.bAddrType = bAddrType;
	}
	
	public BUTTON.WATCH_TYPE geteWatchType() {
		return eWatchType;
	}
	public void seteWatchType(BUTTON.WATCH_TYPE eWatchType) {
		this.eWatchType = eWatchType;
	}
	
	public int getnColor() {
		return nColor;
	}
	public void setnColor(int nColor) {
		this.nColor = nColor;
	}
	public int getnAlpha() {
		return nAlpha;
	}
	public void setnAlpha(int nAlpha) {
		this.nAlpha = nAlpha;
	}
	public short getnShapeType() {
		return nShapeType;
	}
	public void setnShapeType(short nShapeType) {
		this.nShapeType = nShapeType;
	}
	public FLICK_TYPE geteFlickType() {
		return eFlickType;
	}
	public void seteFlickType(FLICK_TYPE eFlickType) {
		this.eFlickType = eFlickType;
	}
	public int getnItemId() {
		return nItemId;
	}
	public void setnItemId(int nItemId) {
		this.nItemId = nItemId;
	}
	public int getnZvalue() {
		return nZvalue;
	}
	public void setnZvalue(int nZvalue) {
		this.nZvalue = nZvalue;
	}
	public int getnCollidindId() {
		return nCollidindId;
	}
	public void setnCollidindId(int nCollidindId) {
		this.nCollidindId = nCollidindId;
	}
	public short getnLp() {
		return nLp;
	}
	public void setnLp(short nLp) {
		this.nLp = nLp;
	}
	public short getnTp() {
		return nTp;
	}
	public void setnTp(short nTp) {
		this.nTp = nTp;
	}
	public short getnWidth() {
		return nWidth;
	}
	public void setnWidth(short nWidth) {
		this.nWidth = nWidth;
	}
	public short getnHeight() {
		return nHeight;
	}
	public void setnHeight(short nHeight) {
		this.nHeight = nHeight;
	}
	public short getnShowLp() {
		return nShowLp;
	}
	public void setnShowLp(short nShowLp) {
		this.nShowLp = nShowLp;
	}
	public short getnShowTp() {
		return nShowTp;
	}
	public void setnShowTp(short nShowTp) {
		this.nShowTp = nShowTp;
	}
	public short getnShowWidth() {
		return nShowWidth;
	}
	public void setnShowWidth(short nShowWidth) {
		this.nShowWidth = nShowWidth;
	}
	public short getnShowHeight() {
		return nShowHeight;
	}
	public void setnShowHeight(short nShowHeight) {
		this.nShowHeight = nShowHeight;
	}
	public String getsApeaPath() {
		return sApeaPath;
	}
	public void setsApeaPath(String sApeaPath) {
		this.sApeaPath = sApeaPath;
	}
	public boolean isbIsStartStatement() {
		return bIsStartStatement;
	}
	public void setbIsStartStatement(boolean bIsStartStatement) {
		this.bIsStartStatement = bIsStartStatement;
	}
	public int getnStatementId() {
		return nStatementId;
	}
	public void setnStatementId(int nStatementId) {
		this.nStatementId = nStatementId;
	}
	public ArrayList<TextInfo> getmTextList() {
		return mTextList;
	}
	public void setmTextList(ArrayList<TextInfo> mTextList) {
		this.mTextList = mTextList;
	}
	public TouchInfo getmTouchInfo() {
		return mTouchInfo;
	}
	public void setmTouchInfo(TouchInfo mTouchInfo) {
		this.mTouchInfo = mTouchInfo;
	}
	public ShowInfo getmShowInfo() {
		return mShowInfo;
	}
	public void setmShowInfo(ShowInfo mShowInfo) {
		this.mShowInfo = mShowInfo;
	}
	public ArrayList<SKButton> getmSkButtons() {
		return mSkButtons;
	}
	public void setmSkButtons(ArrayList<SKButton> mSkButtons) {
		this.mSkButtons = mSkButtons;
	}
	public ArrayList<StakeoutInfo> getmStakeoutList() {
		return mStakeoutList;
	}
	public void setmStakeoutList(ArrayList<StakeoutInfo> mStakeoutList) {
		this.mStakeoutList = mStakeoutList;
	}
}
