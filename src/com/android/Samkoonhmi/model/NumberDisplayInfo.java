package com.android.Samkoonhmi.model;

import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.INPUT_TYPE;
import com.android.Samkoonhmi.skenum.SHOWAREA;
import com.android.Samkoonhmi.skenum.TEXT_PIC_ALIGN;
import com.android.Samkoonhmi.util.AddrProp;
import com.android.Samkoonhmi.util.TextAttribute;

public class NumberDisplayInfo {
	private int Id; // 编号
	private int nStartX; // 起点横坐标
	private int nStartY; // 起点纵坐标
	private int nWidth; // 控件的宽
	private int nHeight; // 控件的高
	private int nTextStartX; // 文本区域的起点横坐标
	private int nTextStartY; // 文本区域的起点纵坐标
	private int nTextWidth; // 文本区域的宽度
	private int nTextHeight; // 文本区域的高度
	private String sShapId; // 外形编号
	private AddrProp nAddress; // 监控字地址
	private AddrProp mOffSetAddr;//地址偏移
	private AddrProp mOffSetAddress;//偏移后监视地址值
	private boolean bIsInput; // 允许输入
	private boolean bIsScale; // 允许缩放
	private DATA_TYPE eNumberType; // 数据类型
	private int nByteLength; // 位长
	private SHOWAREA eSourceArea; // 源范围
	private double nSourceMax; // 源范围最大值
	private double nSourceMin; // 源范围最小值
	private SHOWAREA nShow; // 显示指定
	private boolean bRound; // 四舍五入
	private double nShowMax; // 显示最大值
	private double nShowMin; // 显示最小值
	private String sFontType; // 字体类型
	private int nFontSize; // 字体大小
	private short eFontCss; // 字体样式（标准，粗体，斜体，下划线）
	private int nAllbytelength; // 总显示位数
	private SHOWAREA eDecimalType; // 小数显示类型
	private int nDecimalLength;// 小数显示长度/小数位数地址
	private TEXT_PIC_ALIGN eShowStyle;// 显示样式|（左，中，右）
	private int nFontColor; // 字体颜色
	private int nBackColor; // 字体背景颜色
	private int nHightColor; // 高位背景色
	private int nLowerColor; // 低位背景颜色
	private INPUT_TYPE eInputTypeId;// 输入方式（触摸，位）
	private int nKeyId; // 键盘编号
	private AddrProp sBitAddress; // 位输入地址
//	private int nTouchPropId; // 触控Id
//	private int nShowPropId; // 显隐Id
	private int nZvalue;
	private int nCollidindId;
	private double nLowerNumber;// 低限值
	private double nHightNumber;// 高限值
	private boolean bIsStartStatement;//是否启动宏指令
	private int nScriptId; //脚本库id
	private AddrProp decimaNumberAddrProp; // 小数位数地址
	private AddrProp sourceMaxAddrProp;// 源范围最大值地址
	private AddrProp sourceMinAddrProp;// 源范围最小值地址
	private AddrProp showMaxAddrProp;// 显示范围最大值地址
	private AddrProp showMinAddrProp;// 显示范围最小值地址
	private TouchInfo touchInfo;// 触控属性
	private ShowInfo showInfo;// 显现属性、
	private int nTransparent;//控件透明度
	private SHOWAREA eInputAreaType;//	输入范围类型（地址 常量）	Short
	private double nInputMax;//	输入最大值（有可能是地址编号）	
	private double nInputMin;//	输入最小值（有可能是地址编号）	
	private boolean bInputSign;//	输入提示框	Boolean
	private int nBoardX;//	自定义键盘起点X	Int
	private int nBoardY;//	自定义键盘起点Y	int
	private AddrProp inputMaxAddr;//输入最大值地址
	private AddrProp inputMinAddr;//输入最小值地址
    private boolean bAutoChangeBit ;//位地址控制键盘弹出 是否自动复位 

	
    public AddrProp getmOffSetAddress() {
		return mOffSetAddress;
	}

	public void setmOffSetAddress(AddrProp mOffSetAddress) {
		this.mOffSetAddress = mOffSetAddress;
	}
	
	public boolean isbAutoChangeBit() {
		return bAutoChangeBit;
	}

	public void setbAutoChangeBit(boolean bAutoChangeBit) {
		this.bAutoChangeBit = bAutoChangeBit;
	}

	public double getnInputMax() {
		return nInputMax;
	}

	public void setnInputMax(double nInputMax) {
		this.nInputMax = nInputMax;
	}

	public double getnInputMin() {
		return nInputMin;
	}

	public void setnInputMin(double nInputMin) {
		this.nInputMin = nInputMin;
	}

	public AddrProp getInputMaxAddr() {
		return inputMaxAddr;
	}

	public void setInputMaxAddr(AddrProp inputMaxAddr) {
		this.inputMaxAddr = inputMaxAddr;
	}

	public AddrProp getInputMinAddr() {
		return inputMinAddr;
	}

	public void setInputMinAddr(AddrProp inputMinAddr) {
		this.inputMinAddr = inputMinAddr;
	}

	public SHOWAREA geteInputAreaType() {
		return eInputAreaType;
	}

	public void seteInputAreaType(SHOWAREA eInputAreaType) {
		this.eInputAreaType = eInputAreaType;
	}


	public boolean isbInputSign() {
		return bInputSign;
	}

	public void setbInputSign(boolean bInputSign) {
		this.bInputSign = bInputSign;
	}

	public int getnBoardX() {
		return nBoardX;
	}

	public void setnBoardX(int nBoardX) {
		this.nBoardX = nBoardX;
	}

	public int getnBoardY() {
		return nBoardY;
	}

	public void setnBoardY(int nBoardY) {
		this.nBoardY = nBoardY;
	}

	public int getnTransparent() {
		return nTransparent;
	}

	public void setnTransparent(int nTransparent) {
		this.nTransparent = nTransparent;
	}

	public AddrProp getDecimaNumberAddrProp() {
		return decimaNumberAddrProp;
	}

	public void setDecimaNumberAddrProp(AddrProp decimaNumberAddrProp) {
		this.decimaNumberAddrProp = decimaNumberAddrProp;
	}

	public AddrProp getSourceMaxAddrProp() {
		return sourceMaxAddrProp;
	}

	public void setSourceMaxAddrProp(AddrProp sourceMaxAddrProp) {
		this.sourceMaxAddrProp = sourceMaxAddrProp;
	}

	public AddrProp getSourceMinAddrProp() {
		return sourceMinAddrProp;
	}

	public void setSourceMinAddrProp(AddrProp sourceMinAddrProp) {
		this.sourceMinAddrProp = sourceMinAddrProp;
	}

	public AddrProp getShowMaxAddrProp() {
		return showMaxAddrProp;
	}

	public void setShowMaxAddrProp(AddrProp showMaxAddrProp) {
		this.showMaxAddrProp = showMaxAddrProp;
	}

	public AddrProp getShowMinAddrProp() {
		return showMinAddrProp;
	}

	public void setShowMinAddrProp(AddrProp showMinAddrProp) {
		this.showMinAddrProp = showMinAddrProp;
	}

	public TouchInfo getTouchInfo() {
		return touchInfo;
	}

	public void setTouchInfo(TouchInfo touchInfo) {
		this.touchInfo = touchInfo;
	}

	public ShowInfo getShowInfo() {
		return showInfo;
	}

	public void setShowInfo(ShowInfo showInfo) {
		this.showInfo = showInfo;
	}

	public NumberDisplayInfo() {
		super();
	}

	public boolean isbIsStartStatement() {
		return bIsStartStatement;
	}

	public void setbIsStartStatement(boolean bIsStartStatement) {
		this.bIsStartStatement = bIsStartStatement;
	}

	public int getnScriptId() {
		return nScriptId;
	}

	public void setnScriptId(int nScriptId) {
		this.nScriptId = nScriptId;
	}


	public double getnLowerNumber() {
		return nLowerNumber;
	}

	public void setnLowerNumber(double nLowerNumber) {
		this.nLowerNumber = nLowerNumber;
	}

	public double getnHightNumber() {
		return nHightNumber;
	}

	public void setnHightNumber(double nHightNumber) {
		this.nHightNumber = nHightNumber;
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

//	public int getnTouchPropId() {
//		return nTouchPropId;
//	}
//
//	public void setnTouchPropId(int nTouchPropId) {
//		this.nTouchPropId = nTouchPropId;
//	}
//
//	public int getnShowPropId() {
//		return nShowPropId;
//	}
//
//	public void setnShowPropId(int nShowPropId) {
//		this.nShowPropId = nShowPropId;
//	}

	public int getnHightColor() {
		return nHightColor;
	}

	public void setnHightColor(int nHightColor) {
		this.nHightColor = nHightColor;
	}

	public int getnLowerColor() {
		return nLowerColor;
	}

	public void setnLowerColor(int nLowerColor) {
		this.nLowerColor = nLowerColor;
	}

	public int getnTextStartX() {
		return nTextStartX;
	}

	public void setnTextStartX(int nTextStartX) {
		this.nTextStartX = nTextStartX;
	}

	public int getnTextStartY() {
		return nTextStartY;
	}

	public void setnTextStartY(int nTextStartY) {
		this.nTextStartY = nTextStartY;
	}

	public int getnTextWidth() {
		return nTextWidth;
	}

	public void setnTextWidth(int nTextWidth) {
		this.nTextWidth = nTextWidth;
	}

	public int getnTextHeight() {
		return nTextHeight;
	}

	public void setnTextHeight(int nTextHeight) {
		this.nTextHeight = nTextHeight;
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public int getnStartX() {
		return nStartX;
	}

	public void setnStartX(int nStartX) {
		this.nStartX = nStartX;
	}

	public int getnStartY() {
		return nStartY;
	}

	public void setnStartY(int nStartY) {
		this.nStartY = nStartY;
	}

	public int getnWidth() {
		return nWidth;
	}

	public void setnWidth(int nWidth) {
		this.nWidth = nWidth;
	}

	public int getnHeight() {
		return nHeight;
	}

	public void setnHeight(int nHeight) {
		this.nHeight = nHeight;
	}

	public String getsShapId() {
		return sShapId;
	}

	public void setsShapId(String sShapId) {
		this.sShapId = sShapId;
	}

	public AddrProp getnAddress() {
		return nAddress;
	}

	public void setnAddress(AddrProp nAddress) {
		this.nAddress = nAddress;
	}

	public boolean isbIsInput() {
		return bIsInput;
	}

	public void setbIsInput(boolean bIsInput) {
		this.bIsInput = bIsInput;
	}

	public boolean isbIsScale() {
		return bIsScale;
	}

	public void setbIsScale(boolean bIsScale) {
		this.bIsScale = bIsScale;
	}

	public DATA_TYPE geteNumberType() {
		return eNumberType;
	}

	public void seteNumberType(DATA_TYPE eNumberType) {
		this.eNumberType = eNumberType;
	}

	public int getnByteLength() {
		return nByteLength;
	}

	public void setnByteLength(int nByteLength) {
		this.nByteLength = nByteLength;
	}

	public SHOWAREA geteSourceArea() {
		return eSourceArea;
	}

	public void seteSourceArea(SHOWAREA eSourceArea) {
		this.eSourceArea = eSourceArea;
	}

	public double getnSourceMax() {
		return nSourceMax;
	}

	public void setnSourceMax(double nSourceMax) {
		this.nSourceMax = nSourceMax;
	}

	public double getnSourceMin() {
		return nSourceMin;
	}

	public void setnSourceMin(double nSourceMin) {
		this.nSourceMin = nSourceMin;
	}

	public SHOWAREA getnShow() {
		return nShow;
	}

	public void setnShow(SHOWAREA nShow) {
		this.nShow = nShow;
	}

	public boolean isbRound() {
		return bRound;
	}

	public void setbRound(boolean bRound) {
		this.bRound = bRound;
	}

	public double getnShowMax() {
		return nShowMax;
	}

	public void setnShowMax(double nShowMax) {
		this.nShowMax = nShowMax;
	}

	public double getnShowMin() {
		return nShowMin;
	}

	public void setnShowMin(double nShowMin) {
		this.nShowMin = nShowMin;
	}

	public String getsFontType() {
		return sFontType;
	}

	public void setsFontType(String sFontType) {
		this.sFontType = sFontType;
	}

	public int getnFontSize() {
		return nFontSize;
	}

	public void setnFontSize(int nFontSize) {
		this.nFontSize = nFontSize;
	}

	public short geteFontCss() {
		return eFontCss;
	}

	public void seteFontCss(short eFontCss) {
		this.eFontCss |= eFontCss;
	}

	public void reseteFontCss(short eFontCss) {
		this.eFontCss &= ~eFontCss;
	}

	public int getnAllbytelength() {
		return nAllbytelength;
	}

	public void setnAllbytelength(int nAllbytelength) {
		this.nAllbytelength = nAllbytelength;
	}

	public SHOWAREA geteDecimalType() {
		return eDecimalType;
	}

	public void seteDecimalType(SHOWAREA eDecimalType) {
		this.eDecimalType = eDecimalType;
	}

	public int getnDecimalLength() {
		return nDecimalLength;
	}

	public void setnDecimalLength(int nDecimalLength) {
		this.nDecimalLength = nDecimalLength;
	}

	public TEXT_PIC_ALIGN geteShowStyle() {
		return eShowStyle;
	}

	public void seteShowStyle(TEXT_PIC_ALIGN eShowStyle) {
		this.eShowStyle = eShowStyle;
	}

	public int getnFontColor() {
		return nFontColor;
	}

	public void setnFontColor(int nFontColor) {
		this.nFontColor = nFontColor;
	}

	public int getnBackColor() {
		return nBackColor;
	}

	public void setnBackColor(int nBackColor) {
		this.nBackColor = nBackColor;
	}

	public INPUT_TYPE geteInputTypeId() {
		return eInputTypeId;
	}

	public void seteInputTypeId(INPUT_TYPE eInputTypeId) {
		this.eInputTypeId = eInputTypeId;
	}

	public int getnKeyId() {
		return nKeyId;
	}

	public void setnKeyId(int nKeyId) {
		this.nKeyId = nKeyId;
	}

	public AddrProp getsBitAddress() {
		return sBitAddress;
	}

	public void setsBitAddress(AddrProp sBitAddress) {
		this.sBitAddress = sBitAddress;
	}
	
	public AddrProp getmOffSetAddr() {
		return mOffSetAddr;
	}

	public void setmOffSetAddr(AddrProp mOffSetAddr) {
		this.mOffSetAddr = mOffSetAddr;
	}

}
