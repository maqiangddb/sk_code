package com.android.Samkoonhmi.model.skbutton;

import java.util.ArrayList;

import com.android.Samkoonhmi.graphicsdrawframe.TextItem;
import com.android.Samkoonhmi.model.ShowInfo;
import com.android.Samkoonhmi.model.StakeoutInfo;
import com.android.Samkoonhmi.model.TextInfo;
import com.android.Samkoonhmi.model.TouchInfo;
import com.android.Samkoonhmi.skenum.BUTTON;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.util.AddrProp;

/**
 * 开关按钮
 * @author 刘伟江
 * @version v 1.0.0.4
 * 创建时间 2012-5-22
 * 最后修改时间 2012-5-22
 */
public class ButtonInfo {

	//控件Id
	private int nButtonId;
	//功能id
	private int nFunId;
	//层id
	private short nZvalue;
	//控件组合Id
	private short nCollidindId;
	//开关类型
	private BUTTON.BUTTON_TYPE eButtonType;
	//监视类型
	private BUTTON.WATCH_TYPE eWatchType;
	//监视地址数据类型
	private DATA_TYPE eWatchDataType;
	//检测地址
	private AddrProp mWatchAddress;
	//地址类型，位-false，字-true
	private boolean bAddrType;
	//字地址的第几位
	private short nBitIndex;
	//检测条件
	private ArrayList<StakeoutInfo> mStakeoutList;
	//控件左上角X坐标
	private short nLp;
	//控件左上角Y坐标
	private short nTp;
	//控件宽度
	private short nWidth;
	//控件高度
	private short nHeight;
	//控件显示区域左上角X坐标
	private short nShowLp;
	//控件显示区域左上角Y坐标
	private short nShowTp;
	//控件显示区域宽
	private short nShowWidth;
	//控件显示区域高
	private short nShowHeight;
	//状态关联
	private boolean bSameState;
	//语言关联
	private boolean bSameLanguage;
	//语言ID
	private int nLanguageId;
	//外形路径
	private String sApeaPath;
	//是否调用宏指令
	private boolean bIsStartStatement;
	//宏指令ID
	private int nStatementId;
	//每个状态的文本
	private ArrayList<TextInfo> mTextList;
	//触控
	private TouchInfo mTouchInfo;
	//显现
	private ShowInfo mShowInfo;
	//是否是滑动开关
	private boolean bSlid;
	//切换条件 0-按值，1-寄存器的位,2-自定义值
	private short nCondition;
	
	public short getnCondition() {
		return nCondition;
	}
	public void setnCondition(short nCondition) {
		this.nCondition = nCondition;
	}
	public boolean isbSlid() {
		return bSlid;
	}
	public void setbSlid(boolean bSlid) {
		this.bSlid = bSlid;
	}
	public int getnButtonId() {
		return nButtonId;
	}
	public void setnButtonId(int nButtonId) {
		this.nButtonId = nButtonId;
	}
	public int getnFunId() {
		return nFunId;
	}
	public void setnFunId(int nFunId) {
		this.nFunId = nFunId;
	}
	public short getnZvalue() {
		return nZvalue;
	}
	public void setnZvalue(short nZvalue) {
		this.nZvalue = nZvalue;
	}
	public short getnCollidindId() {
		return nCollidindId;
	}
	public void setnCollidindId(short nCollidindId) {
		this.nCollidindId = nCollidindId;
	}
	public BUTTON.BUTTON_TYPE geteButtonType() {
		return eButtonType;
	}
	public void seteButtonType(BUTTON.BUTTON_TYPE eButtonType) {
		this.eButtonType = eButtonType;
	}
	public BUTTON.WATCH_TYPE geteWatchType() {
		return eWatchType;
	}
	public void seteWatchType(BUTTON.WATCH_TYPE eWatchType) {
		this.eWatchType = eWatchType;
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
	public boolean isbSameState() {
		return bSameState;
	}
	public void setbSameState(boolean bSameState) {
		this.bSameState = bSameState;
	}
	public boolean isbSameLanguage() {
		return bSameLanguage;
	}
	public void setbSameLanguage(boolean bSameLanguage) {
		this.bSameLanguage = bSameLanguage;
	}
	public int getnLanguageId() {
		return nLanguageId;
	}
	public void setnLanguageId(int nLanguageId) {
		this.nLanguageId = nLanguageId;
	}
	public String getsApeaPath() {
		if(sApeaPath==null){
			sApeaPath="";
		}
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
	
	public AddrProp getmWatchAddress() {
		return mWatchAddress;
	}
	public void setmWatchAddress(AddrProp mWatchAddress) {
		this.mWatchAddress = mWatchAddress;
	}
	public ArrayList<StakeoutInfo> getmStakeoutList() {
		return mStakeoutList;
	}
	public void setmStakeoutList(ArrayList<StakeoutInfo> mStakeoutList) {
		this.mStakeoutList = mStakeoutList;
	}
	public ArrayList<TextInfo> getmTextList() {
		return mTextList;
	}
	public void setmTextList(ArrayList<TextInfo> mTextList) {
		this.mTextList = mTextList;
	}
	public DATA_TYPE geteWatchDataType() {
		return eWatchDataType;
	}
	public void seteWatchDataType(DATA_TYPE eWatchDataType) {
		this.eWatchDataType = eWatchDataType;
	}
	public boolean isbAddrType() {
		return bAddrType;
	}
	public void setbAddrType(boolean bAddrType) {
		this.bAddrType = bAddrType;
	}
	public short getnBitIndex() {
		return nBitIndex;
	}
	public void setnBitIndex(short nBitIndex) {
		this.nBitIndex = nBitIndex;
	}
}
