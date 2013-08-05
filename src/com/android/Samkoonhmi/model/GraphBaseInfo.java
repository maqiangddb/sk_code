package com.android.Samkoonhmi.model;

import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.Direction.DIRECTION;
import com.android.Samkoonhmi.skenum.Graph.GRAPH_TYPE;
import com.android.Samkoonhmi.util.AddrProp;

/**
 * 图表-基类
 * 
 * @author 刘伟江
 * @version v 1.0.0.1 创建时间 2012-4-19 最后修改时间：2012-4-26
 */
public class GraphBaseInfo {

	//控件id
	private int nItemId;
	// 图表类别
	private GRAPH_TYPE eGraphType; // 0-普通，1-统计，2-仪表
	// 图表形状，大类型中具体的每个图表的形状
	private int nShapeId;
	// 监控地址
	private AddrProp mAddress;
	// 数据类型
	private DATA_TYPE eDataType;
	// 显示方向
	private DIRECTION eDirection;
	// 显示颜色
	private int nTextColor;
	// 背景颜色
	private int nBackColor;
	// 控件左上角X坐标
	private short nLeftTopX;
	// 控件左上角Ｙ坐标
	private short nLeftTopY;
	// 控件宽度
	private short nWidth;
	// 控件高度
	private short nHeigth;
	// 主标尺刻度
	private short nMainRuling;
	// 是否显示次标尺
	private boolean bShowRuling;
	// 次标尺刻度
	private short nRuling;
	//是否显示刻度值
	private boolean bShowRuleValue;
	// 标尺颜色
	private int nRulingColor;
	// 内部动态显示图顶点X坐标
	private short nShowLeftTopX;
	// 内部动态显示图顶点Y坐标
	private short nShowLeftTopY;
	// 内部动态显示图宽
	private short nShowWidth;
	// 内部动态显示图高
	private short nShowHigth;
	// 标尺顶点X坐标
	private short nRulerLeftTopX;
	// 标尺顶点Y坐标
	private short nRulerLeftTopY;
	// 标尺宽
	private short nRulerWidth;
	// 标尺高
	private short nRulerHigth;
	// 标尺方向
	private DIRECTION eRulerDirection;
	//是否启动报警
	private boolean bAlarm;
	//报警操作
	private short nType;
	//下限
	private double nMin;
	//上限
	private double nMax;
	//下限报警地址
	private AddrProp mAlarmMinAddr;
	//上限报警地址
	private AddrProp mAlarmMaxAddr;
	//显示颜色
	private int nAlarmTextColor;
	//图案颜色
	private int nDesignColor;
	//控件组合id
	private short nColidindId;
	//层次id
	private short nZvalue;
	//背景图片路径
	private String sPic;
	//是否有背景
	private boolean hasBg;
	//是否有标尺
	private boolean hasRuler;
	//是否有边框
	private boolean hasFrame;
	//显现
	private ShowInfo mShowInfo;
	//源数据类型-0常数，1地址
	private short nSourceRang;
	//位长
	private short nBitLength;
	// 源是否显示符号
	private boolean bSourceMark;
	// 源最小值
	private double nSourceMin;
	// 源最大值
	private double nSourceMax;
	// 显示范围，是否显示符号
	private boolean bShowMark;
	// 显示范围，最小值
	private double nShowMin;
	// 显示范围，最大值
	private double nShowMax;
	// 最小值地址
	private AddrProp mMinAddrProp;
	// 最大值地址
	private AddrProp mMaxAddrProp;
	// 指针类型
	private short nPointerType;
	//透明度
	private short nAlpha;
	//是否显示边框
	private boolean bShowFrame;
	
	public short getnAlpha() {
		return nAlpha;
	}

	public void setnAlpha(short nAlpha) {
		this.nAlpha = nAlpha;
	}

	public short getnPointerType() {
		return nPointerType;
	}

	public void setnPointerType(short nPointerType) {
		this.nPointerType = nPointerType;
	}

	public short getnBitLength() {
		return nBitLength;
	}

	public void setnBitLength(short nBitLength) {
		this.nBitLength = nBitLength;
	}

	public boolean isbSourceMark() {
		return bSourceMark;
	}

	public void setbSourceMark(boolean bSourceMark) {
		this.bSourceMark = bSourceMark;
	}
	public double getnMin() {
		return nMin;
	}

	public void setnMin(double nMin) {
		this.nMin = nMin;
	}

	public double getnMax() {
		return nMax;
	}

	public void setnMax(double nMax) {
		this.nMax = nMax;
	}

	public double getnSourceMin() {
		return nSourceMin;
	}

	public void setnSourceMin(double nSourceMin) {
		this.nSourceMin = nSourceMin;
	}

	public double getnSourceMax() {
		return nSourceMax;
	}

	public void setnSourceMax(double nSourceMax) {
		this.nSourceMax = nSourceMax;
	}

	public double getnShowMin() {
		return nShowMin;
	}

	public void setnShowMin(double nShowMin) {
		this.nShowMin = nShowMin;
	}

	public double getnShowMax() {
		return nShowMax;
	}

	public void setnShowMax(double nShowMax) {
		this.nShowMax = nShowMax;
	}
	

	public boolean isbShowMark() {
		return bShowMark;
	}

	public void setbShowMark(boolean bShowMark) {
		this.bShowMark = bShowMark;
	}

	

	public AddrProp getmMinAddrProp() {
		return mMinAddrProp;
	}

	public void setmMinAddrProp(AddrProp mMinAddrProp) {
		this.mMinAddrProp = mMinAddrProp;
	}

	public AddrProp getmMaxAddrProp() {
		return mMaxAddrProp;
	}

	public void setmMaxAddrProp(AddrProp mMaxAddrProp) {
		this.mMaxAddrProp = mMaxAddrProp;
	}

	public short getnSourceRang() {
		return nSourceRang;
	}

	public void setnSourceRang(short nSourceRang) {
		this.nSourceRang = nSourceRang;
	}

	public boolean isHasFrame() {
		return hasFrame;
	}

	public void setHasFrame(boolean hasFrame) {
		this.hasFrame = hasFrame;
	}

	public ShowInfo getmShowInfo() {
		return mShowInfo;
	}

	public void setmShowInfo(ShowInfo mShowInfo) {
		this.mShowInfo = mShowInfo;
	}

	public boolean isHasBg() {
		return hasBg;
	}

	public void setHasBg(boolean hasBg) {
		this.hasBg = hasBg;
	}

	public boolean isHasRuler() {
		return hasRuler;
	}

	public void setHasRuler(boolean hasRuler) {
		this.hasRuler = hasRuler;
	}

	public String getsPic() {
		return sPic;
	}

	public void setsPic(String sPic) {
		this.sPic = sPic;
	}

	public short getnColidindId() {
		return nColidindId;
	}

	public void setnColidindId(short nColidindId) {
		this.nColidindId = nColidindId;
	}

	public short getnZvalue() {
		return nZvalue;
	}

	public void setnZvalue(short nZvalue) {
		this.nZvalue = nZvalue;
	}

	
	public boolean isbAlarm() {
		return bAlarm;
	}

	public void setbAlarm(boolean bAlarm) {
		this.bAlarm = bAlarm;
	}
	
	public short getnType() {
		return nType;
	}

	public void setnType(short nType) {
		this.nType = nType;
	}

	public int getnAlarmTextColor() {
		return nAlarmTextColor;
	}

	public void setnAlarmTextColor(int nAlarmTextColor) {
		this.nAlarmTextColor = nAlarmTextColor;
	}

	public int getnDesignColor() {
		return nDesignColor;
	}

	public void setnDesignColor(int nDesignColor) {
		this.nDesignColor = nDesignColor;
	}

	public int getnItemId() {
		return nItemId;
	}

	public void setnItemId(int nItemId) {
		this.nItemId = nItemId;
	}
	
	public GRAPH_TYPE geteGraphType() {
		return eGraphType;
	}

	public void seteGraphType(GRAPH_TYPE eGraphType) {
		this.eGraphType = eGraphType;
	}

	public int getnShapeId() {
		return nShapeId;
	}

	public void setnShapeId(int nShapeId) {
		this.nShapeId = nShapeId;
	}

	public AddrProp getmAddress() {
		return mAddress;
	}

	public void setmAddress(AddrProp mAddress) {
		this.mAddress = mAddress;
	}

	public DATA_TYPE getnDataType() {
		return eDataType;
	}

	public void setnDataType(DATA_TYPE nDataType) {
		this.eDataType = nDataType;
	}

	public DIRECTION geteDirection() {
		return eDirection;
	}

	public void seteDirection(DIRECTION eDirection) {
		this.eDirection = eDirection;
	}

	public int getnTextColor() {
		return nTextColor;
	}

	public void setnTextColor(int nTextColor) {
		this.nTextColor = nTextColor;
	}

	public int getnBackColor() {
		return nBackColor;
	}

	public void setnBackColor(int nBackColor) {
		this.nBackColor = nBackColor;
	}

	public short getnLeftTopX() {
		return nLeftTopX;
	}

	public void setnLeftTopX(short nLeftTopX) {
		this.nLeftTopX = nLeftTopX;
	}

	public short getnLeftTopY() {
		return nLeftTopY;
	}

	public void setnLeftTopY(short nLeftTopY) {
		this.nLeftTopY = nLeftTopY;
	}

	public short getnWidth() {
		return nWidth;
	}

	public void setnWidth(short nWidth) {
		this.nWidth = nWidth;
	}

	public short getnHeigth() {
		return nHeigth;
	}

	public void setnHeigth(short nHeigth) {
		this.nHeigth = nHeigth;
	}

	public short getnMainRuling() {
		return nMainRuling;
	}

	public void setnMainRuling(short nMainRuling) {
		this.nMainRuling = nMainRuling;
	}

	public boolean isbShowRuling() {
		return bShowRuling;
	}

	public void setbShowRuling(boolean bShowRuling) {
		this.bShowRuling = bShowRuling;
	}

	public short getnRuling() {
		return nRuling;
	}

	public void setnRuling(short nRuling) {
		this.nRuling = nRuling;
	}

	public int getnRulingColor() {
		return nRulingColor;
	}

	public void setnRulingColor(int nRulingColor) {
		this.nRulingColor = nRulingColor;
	}
	
	public DATA_TYPE geteDataType() {
		return eDataType;
	}

	public void seteDataType(DATA_TYPE eDataType) {
		this.eDataType = eDataType;
	}

	public short getnShowLeftTopX() {
		return nShowLeftTopX;
	}

	public void setnShowLeftTopX(short nShowLeftTopX) {
		this.nShowLeftTopX = nShowLeftTopX;
	}

	public short getnShowLeftTopY() {
		return nShowLeftTopY;
	}

	public void setnShowLeftTopY(short nShowLeftTopY) {
		this.nShowLeftTopY = nShowLeftTopY;
	}

	public short getnShowWidth() {
		return nShowWidth;
	}

	public void setnShowWidth(short nShowWidth) {
		this.nShowWidth = nShowWidth;
	}

	public short getnShowHigth() {
		return nShowHigth;
	}

	public void setnShowHigth(short nShowHigth) {
		this.nShowHigth = nShowHigth;
	}

	public short getnRulerLeftTopX() {
		return nRulerLeftTopX;
	}

	public void setnRulerLeftTopX(short nRulerLeftTopX) {
		this.nRulerLeftTopX = nRulerLeftTopX;
	}

	public short getnRulerLeftTopY() {
		return nRulerLeftTopY;
	}

	public void setnRulerLeftTopY(short nRulerLeftTopY) {
		this.nRulerLeftTopY = nRulerLeftTopY;
	}

	public short getnRulerWidth() {
		return nRulerWidth;
	}

	public void setnRulerWidth(short nRulerWidth) {
		this.nRulerWidth = nRulerWidth;
	}

	public short getnRulerHigth() {
		return nRulerHigth;
	}

	public void setnRulerHigth(short nRulerHigth) {
		this.nRulerHigth = nRulerHigth;
	}

	public DIRECTION geteRulerDirection() {
		return eRulerDirection;
	}

	public void seteRulerDirection(DIRECTION eRulerDirection) {
		this.eRulerDirection = eRulerDirection;
	}

	public AddrProp getmAlarmMinAddr() {
		return mAlarmMinAddr;
	}

	public void setmAlarmMinAddr(AddrProp mAlarmMinAddr) {
		this.mAlarmMinAddr = mAlarmMinAddr;
	}

	public AddrProp getmAlarmMaxAddr() {
		return mAlarmMaxAddr;
	}

	public void setmAlarmMaxAddr(AddrProp mAlarmMaxAddr) {
		this.mAlarmMaxAddr = mAlarmMaxAddr;
	}
	public boolean isbShowRuleValue() {
		return bShowRuleValue;
	}

	public void setbShowRuleValue(boolean bShowRuleValue) {
		this.bShowRuleValue = bShowRuleValue;
	}
	public void setShowFrame(boolean showframe){
		this.bShowFrame = showframe;
	}
	public boolean getShowFrame(){
		return bShowFrame;
	}
}
