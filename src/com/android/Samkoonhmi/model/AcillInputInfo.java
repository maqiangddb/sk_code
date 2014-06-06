package com.android.Samkoonhmi.model;

import com.android.Samkoonhmi.skenum.INPUT_TYPE;
import com.android.Samkoonhmi.skenum.TEXT_LANGUAGE;
import com.android.Samkoonhmi.skenum.TEXT_PIC_ALIGN;
import com.android.Samkoonhmi.util.AddrProp;

/**
 * ASCII显示
 * 
 * @author 瞿丽平
 * 
 */
public class AcillInputInfo {
	private int Id;// 编号
	private int nStartX;// 控件起点横坐标
	private int nStartY;// 控件起点纵坐标
	private int nWidth;// 控件宽度
	private int nHeight;// 控件高度
	private int nTextStartX;// 文本起点横坐标
	private int nTextStartY;// 文本起点纵坐标
	private int nTextWidth;// 文本宽度
	private int nTextHeight;// 文本高度
	private String sShapId;// 外形图片地址
	private AddrProp nAddress;// 监控地址
	private AddrProp mOffSetAddress;//偏移后监视地址值
	private boolean bIsinput;// 是否允许输入
	private String sFontStyle;// 字体类型
	private int nFontsize;// 字体大小
	private TEXT_LANGUAGE nLanguageTypeId;// 显示语言
	private short eFontCss;// 文本属性
	private int nShowCharNumber;// 显示字符数
	private TEXT_PIC_ALIGN nShowStyle;// 显示样式
	private short nCode;// ACILL编码
	private int nFontColor;// 字体颜色
	private int nBackColor;// 字体背景颜色
	private int nTouchPropId;// 触控Id
	private int nShowPropId;// 显隐Id
	private AddrProp sBitAddress; // 位输入地址
	private AddrProp mOffSetAddr;//地址偏移
	private INPUT_TYPE eInputTypeId;// 输入方式（触摸，位）
	private int nKeyId;  //键盘编号
	private int nZvalue;
	private int nCollidindId;
	private boolean bIsStartStatement;//是否启动宏指令
	private int nScriptId; //脚本库id
	private TouchInfo mTouchinInfo;
	private ShowInfo mShowInfo;
	private int nTransparent;//控件透明度
	private boolean bInputSign;//	输入提示框	Boolean
	private int nBoardX;//	自定义键盘起点X	Int
	private int nBoardY;//	自定义键盘起点Y	int
	private boolean bAutoChangeBit;// 位地址控制键盘弹出 是否自动复位
	public boolean isInputIsShow() {
		return inputIsShow;
	}

	public void setInputIsShow(boolean inputIsShow) {
		this.inputIsShow = inputIsShow;
	}

	public AddrProp getInputAddr() {
		return inputAddr;
	}

	public void setInputAddr(AddrProp inputAddr) {
		this.inputAddr = inputAddr;
	}

	private boolean inputIsShow;//输入跟显示地址是否一致
	private AddrProp inputAddr;//输入地址

	
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


	public TouchInfo getmTouchinInfo() {
		return mTouchinInfo;
	}


	public void setmTouchinInfo(TouchInfo mTouchinInfo) {
		this.mTouchinInfo = mTouchinInfo;
	}


	public ShowInfo getmShowInfo() {
		return mShowInfo;
	}


	public void setmShowInfo(ShowInfo mShowInfo) {
		this.mShowInfo = mShowInfo;
	}


	public AcillInputInfo() {
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

	public INPUT_TYPE geteInputTypeId() {
		return eInputTypeId;
	}

	public void seteInputTypeId(INPUT_TYPE eInputTypeId) {
		this.eInputTypeId = eInputTypeId;
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

	public int getnTouchPropId() {
		return nTouchPropId;
	}

	public void setnTouchPropId(int nTouchPropId) {
		this.nTouchPropId = nTouchPropId;
	}

	public int getnShowPropId() {
		return nShowPropId;
	}

	public void setnShowPropId(int nShowPropId) {
		this.nShowPropId = nShowPropId;
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

	public boolean isbIsinput() {
		return bIsinput;
	}

	public void setbIsinput(boolean bIsinput) {
		this.bIsinput = bIsinput;
	}

	public String getsFontStyle() {
		return sFontStyle;
	}

	public void setsFontStyle(String sFontStyle) {
		this.sFontStyle = sFontStyle;
	}

	public int getnFontsize() {
		return nFontsize;
	}

	public void setnFontsize(int nFontsize) {
		this.nFontsize = nFontsize;
	}

	public TEXT_LANGUAGE getnLanguageTypeId() {
		return nLanguageTypeId;
	}

	public void setnLanguageTypeId(TEXT_LANGUAGE nLanguageTypeId) {
		this.nLanguageTypeId = nLanguageTypeId;
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

	public int getnShowCharNumber() {
		return nShowCharNumber;
	}

	public void setnShowCharNumber(int nShowCharNumber) {
		this.nShowCharNumber = nShowCharNumber;
	}

	public TEXT_PIC_ALIGN getnShowStyle() {
		return nShowStyle;
	}

	public void setnShowStyle(TEXT_PIC_ALIGN nShowStyle) {
		this.nShowStyle = nShowStyle;
	}

	public short getnCode() {
		return nCode;
	}

	public void setnCode(short nCode) {
		this.nCode |= nCode;
	}

	public void resetnCode(short nCode) {
		this.nCode &= ~nCode;
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
	
	public AddrProp getmOffSetAddr() {
		return mOffSetAddr;
	}

	public void setmOffSetAddr(AddrProp mOffSetAddr) {
		this.mOffSetAddr = mOffSetAddr;
	}

}
