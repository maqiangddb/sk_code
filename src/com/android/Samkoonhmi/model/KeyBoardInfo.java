package com.android.Samkoonhmi.model;

import android.graphics.Color;

import com.android.Samkoonhmi.skenum.CSS_TYPE;
import com.android.Samkoonhmi.skenum.KEYBOARD_OPERATION;
import com.android.Samkoonhmi.skenum.KEY_STYLE;
import com.android.Samkoonhmi.skenum.TEXT_PIC_ALIGN;

/**
 * 键盘
 * 
 * @author Eisen
 * 
 */
public class KeyBoardInfo {
	// 要从数据库对应的字段
	private Integer id;// 编号
	private String skeyName;// 键盘名
	private int nkeyStartX;// X轴
	private int nkeyStartY;// Y轴
	private int nkeyWidth;// 长度
	private int nkeyHeight;// 宽度
	private CSS_TYPE ekeyStyle;// 填充样式
	private int nkeyBackColor;// 键盘背景色
	private int nkeyForeColor;// 键盘前景色
	private KEY_STYLE eBackType;// 背景类型：样式、图片
	private String sPicturePath;// 图片路径

	private int nMaxStartX;
	private int nMaxStartY;
	private int nMaxWidth;
	private int nMaxHeight;
	private String nMaxFont;
	private TEXT_PIC_ALIGN nMaxAlign;
	private int nMaxFontSize;
	private short nMaxFontPro;
	private int nMaxFontColor;
	private CSS_TYPE nMaxStyle;
	private int nMaxAlpha;
	private int nMaxForeColor;
	private int nMaxBackColor;
	private boolean nMaxAdapt;

	private int nMinStartX;
	private int nMinStartY;
	private int nMinWidth;
	private int nMinHeight;
	private String nMinFont;
	private TEXT_PIC_ALIGN nMinAlign;
	private int nMinFontSize;
	private short nMinFontPro;
	private int nMinFontColor;
	private CSS_TYPE nMinStyle;
	private int nMinAlpha;
	private int nMinForeColor;
	private int nMinBackColor;
	private boolean nMinAdapt;

	private int nTextStartX;
	private int nTextStartY;
	private int nTextWidth;
	private int nTextHeight;
	private String nTextFont;
	private TEXT_PIC_ALIGN nTextAlign;
	private int nTextFontSize;
	private short nTextFontPro;
	private int nTextFontColor;
	private CSS_TYPE nTextStyle;
	private int nTextAlpha;
	private int nTextForeColor;
	private int nTextBackColor;
	private boolean nTextAdapt;

	/**
	 * 构造方法
	 */
	public KeyBoardInfo() {
		super();
	}

	public KeyBoardInfo(Integer id, String skeyName, int nkeyStartX,
			int nkeyStartY, int nkeyWidth, int nkeyHeight, CSS_TYPE ekeyStyle,
			int nkeyBackColor, int nkeyForeColor, KEY_STYLE eBackType,
			String sPicturePath, int nMaxStartX, int nMaxStartY, int nMaxWidth,
			int nMaxHeight, String nMaxFont, TEXT_PIC_ALIGN nMaxAlign,
			int nMaxFontSize, short nMaxFontPro, int nMaxFontColor,
			CSS_TYPE nMaxStyle, int nMaxAlpha, int nMaxForeColor,
			int nMaxBackColor, boolean nMaxAdapt, int nMinStartX,
			int nMinStartY, int nMinWidth, int nMinHeight, String nMinFont,
			TEXT_PIC_ALIGN nMinAlign, int nMinFontSize, short nMinFontPro,
			int nMinFontColor, CSS_TYPE nMinStyle, int nMinAlpha,
			int nMinForeColor, int nMinBackColor, boolean nMinAdapt,
			int nTextStartX, int nTextStartY, int nTextWidth, int nTextHeight,
			String nTextFont, TEXT_PIC_ALIGN nTextAlign, int nTextFontSize,
			short nTextFontPro, int nTextFontColor, CSS_TYPE nTextStyle,
			int nTextAlpha, int nTextForeColor, int nTextBackColor,
			boolean nTextAdapt) {
		super();
		this.id = id;
		this.skeyName = skeyName;
		this.nkeyStartX = nkeyStartX;
		this.nkeyStartY = nkeyStartY;
		this.nkeyWidth = nkeyWidth;
		this.nkeyHeight = nkeyHeight;
		this.ekeyStyle = ekeyStyle;
		this.nkeyBackColor = nkeyBackColor;
		this.nkeyForeColor = nkeyForeColor;
		this.eBackType = eBackType;
		this.sPicturePath = sPicturePath;
		this.nMaxStartX = nMaxStartX;
		this.nMaxStartY = nMaxStartY;
		this.nMaxWidth = nMaxWidth;
		this.nMaxHeight = nMaxHeight;
		this.nMaxFont = nMaxFont;
		this.nMaxAlign = nMaxAlign;
		this.nMaxFontSize = nMaxFontSize;
		this.nMaxFontPro = nMaxFontPro;
		this.nMaxFontColor = nMaxFontColor;
		this.nMaxStyle = nMaxStyle;
		this.nMaxAlpha = nMaxAlpha;
		this.nMaxForeColor = nMaxForeColor;
		this.nMaxBackColor = nMaxBackColor;
		this.nMaxAdapt = nMaxAdapt;
		this.nMinStartX = nMinStartX;
		this.nMinStartY = nMinStartY;
		this.nMinWidth = nMinWidth;
		this.nMinHeight = nMinHeight;
		this.nMinFont = nMinFont;
		this.nMinAlign = nMinAlign;
		this.nMinFontSize = nMinFontSize;
		this.nMinFontPro = nMinFontPro;
		this.nMinFontColor = nMinFontColor;
		this.nMinStyle = nMinStyle;
		this.nMinAlpha = nMinAlpha;
		this.nMinForeColor = nMinForeColor;
		this.nMinBackColor = nMinBackColor;
		this.nMinAdapt = nMinAdapt;
		this.nTextStartX = nTextStartX;
		this.nTextStartY = nTextStartY;
		this.nTextWidth = nTextWidth;
		this.nTextHeight = nTextHeight;
		this.nTextFont = nTextFont;
		this.nTextAlign = nTextAlign;
		this.nTextFontSize = nTextFontSize;
		this.nTextFontPro = nTextFontPro;
		this.nTextFontColor = nTextFontColor;
		this.nTextStyle = nTextStyle;
		this.nTextAlpha = nTextAlpha;
		this.nTextForeColor = nTextForeColor;
		this.nTextBackColor = nTextBackColor;
		this.nTextAdapt = nTextAdapt;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSkeyName() {
		return skeyName;
	}

	public void setSkeyName(String skeyName) {
		this.skeyName = skeyName;
	}

	public int getNkeyStartX() {
		return nkeyStartX;
	}

	public void setNkeyStartX(int nkeyStartX) {
		this.nkeyStartX = nkeyStartX;
	}

	public int getNkeyStartY() {
		return nkeyStartY;
	}

	public void setNkeyStartY(int nkeyStartY) {
		this.nkeyStartY = nkeyStartY;
	}

	public int getNkeyWidth() {
		return nkeyWidth;
	}

	public void setNkeyWidth(int nkeyWidth) {
		this.nkeyWidth = nkeyWidth;
	}

	public int getNkeyHeight() {
		return nkeyHeight;
	}

	public void setNkeyHeight(int nkeyHeight) {
		this.nkeyHeight = nkeyHeight;
	}

	public CSS_TYPE getEkeyStyle() {
		return ekeyStyle;
	}

	public void setEkeyStyle(CSS_TYPE ekeyStyle) {
		this.ekeyStyle = ekeyStyle;
	}

	public int getNkeyBackColor() {
		return nkeyBackColor;
	}

	public void setNkeyBackColor(int nkeyBackColor) {
		this.nkeyBackColor = nkeyBackColor;
	}

	public int getNkeyForeColor() {
		return nkeyForeColor;
	}

	public void setNkeyForeColor(int nkeyForeColor) {
		this.nkeyForeColor = nkeyForeColor;
	}

	public KEY_STYLE geteBackType() {
		return eBackType;
	}

	public void seteBackType(KEY_STYLE eBackType) {
		this.eBackType = eBackType;
	}

	public String getsPicturePath() {
		return sPicturePath;
	}

	public void setsPicturePath(String sPicturePath) {
		this.sPicturePath = sPicturePath;
	}

	public int getnMaxStartX() {
		return nMaxStartX;
	}

	public void setnMaxStartX(int nMaxStartX) {
		this.nMaxStartX = nMaxStartX;
	}

	public int getnMaxStartY() {
		return nMaxStartY;
	}

	public void setnMaxStartY(int nMaxStartY) {
		this.nMaxStartY = nMaxStartY;
	}

	public int getnMaxWidth() {
		return nMaxWidth;
	}

	public void setnMaxWidth(int nMaxWidth) {
		this.nMaxWidth = nMaxWidth;
	}

	public int getnMaxHeight() {
		return nMaxHeight;
	}

	public void setnMaxHeight(int nMaxHeight) {
		this.nMaxHeight = nMaxHeight;
	}

	public String getnMaxFont() {
		return nMaxFont;
	}

	public void setnMaxFont(String nMaxFont) {
		this.nMaxFont = nMaxFont;
	}

	public TEXT_PIC_ALIGN getnMaxAlign() {
		return nMaxAlign;
	}

	public void setnMaxAlign(TEXT_PIC_ALIGN nMaxAlign) {
		this.nMaxAlign = nMaxAlign;
	}

	public int getnMaxFontSize() {
		return nMaxFontSize;
	}

	public void setnMaxFontSize(int nMaxFontSize) {
		this.nMaxFontSize = nMaxFontSize;
	}

	public short getnMaxFontPro() {
		return nMaxFontPro;
	}

	public void setnMaxFontPro(short nMaxFontPro) {
		this.nMaxFontPro = nMaxFontPro;
	}

	public int getnMaxFontColor() {
		return nMaxFontColor;
	}

	public void setnMaxFontColor(int nMaxFontColor) {
		this.nMaxFontColor = nMaxFontColor;
	}

	public CSS_TYPE getnMaxStyle() {
		return nMaxStyle;
	}

	public void setnMaxStyle(CSS_TYPE nMaxStyle) {
		this.nMaxStyle = nMaxStyle;
	}

	public int getnMaxAlpha() {
		return nMaxAlpha;
	}

	public void setnMaxAlpha(int nMaxAlpha) {
		this.nMaxAlpha = nMaxAlpha;
	}

	public int getnMaxForeColor() {
		return nMaxForeColor;
	}

	public void setnMaxForeColor(int nMaxForeColor) {
		this.nMaxForeColor = nMaxForeColor;
	}

	public int getnMaxBackColor() {
		return nMaxBackColor;
	}

	public void setnMaxBackColor(int nMaxBackColor) {
		this.nMaxBackColor = nMaxBackColor;
	}

	public boolean isnMaxAdapt() {
		return nMaxAdapt;
	}

	public void setnMaxAdapt(boolean nMaxAdapt) {
		this.nMaxAdapt = nMaxAdapt;
	}

	public int getnMinStartX() {
		return nMinStartX;
	}

	public void setnMinStartX(int nMinStartX) {
		this.nMinStartX = nMinStartX;
	}

	public int getnMinStartY() {
		return nMinStartY;
	}

	public void setnMinStartY(int nMinStartY) {
		this.nMinStartY = nMinStartY;
	}

	public int getnMinWidth() {
		return nMinWidth;
	}

	public void setnMinWidth(int nMinWidth) {
		this.nMinWidth = nMinWidth;
	}

	public int getnMinHeight() {
		return nMinHeight;
	}

	public void setnMinHeight(int nMinHeight) {
		this.nMinHeight = nMinHeight;
	}

	public String getnMinFont() {
		return nMinFont;
	}

	public void setnMinFont(String nMinFont) {
		this.nMinFont = nMinFont;
	}

	public TEXT_PIC_ALIGN getnMinAlign() {
		return nMinAlign;
	}

	public void setnMinAlign(TEXT_PIC_ALIGN nMinAlign) {
		this.nMinAlign = nMinAlign;
	}

	public int getnMinFontSize() {
		return nMinFontSize;
	}

	public void setnMinFontSize(int nMinFontSize) {
		this.nMinFontSize = nMinFontSize;
	}

	public short getnMinFontPro() {
		return nMinFontPro;
	}

	public void setnMinFontPro(short nMinFontPro) {
		this.nMinFontPro = nMinFontPro;
	}

	public int getnMinFontColor() {
		return nMinFontColor;
	}

	public void setnMinFontColor(int nMinFontColor) {
		this.nMinFontColor = nMinFontColor;
	}

	public CSS_TYPE getnMinStyle() {
		return nMinStyle;
	}

	public void setnMinStyle(CSS_TYPE nMinStyle) {
		this.nMinStyle = nMinStyle;
	}

	public int getnMinAlpha() {
		return nMinAlpha;
	}

	public void setnMinAlpha(int nMinAlpha) {
		this.nMinAlpha = nMinAlpha;
	}

	public int getnMinForeColor() {
		return nMinForeColor;
	}

	public void setnMinForeColor(int nMinForeColor) {
		this.nMinForeColor = nMinForeColor;
	}

	public int getnMinBackColor() {
		return nMinBackColor;
	}

	public void setnMinBackColor(int nMinBackColor) {
		this.nMinBackColor = nMinBackColor;
	}

	public boolean isnMinAdapt() {
		return nMinAdapt;
	}

	public void setnMinAdapt(boolean nMinAdapt) {
		this.nMinAdapt = nMinAdapt;
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

	public String getnTextFont() {
		return nTextFont;
	}

	public void setnTextFont(String nTextFont) {
		this.nTextFont = nTextFont;
	}

	public TEXT_PIC_ALIGN getnTextAlign() {
		return nTextAlign;
	}

	public void setnTextAlign(TEXT_PIC_ALIGN nTextAlign) {
		this.nTextAlign = nTextAlign;
	}

	public int getnTextFontSize() {
		return nTextFontSize;
	}

	public void setnTextFontSize(int nTextFontSize) {
		this.nTextFontSize = nTextFontSize;
	}

	public short getnTextFontPro() {
		return nTextFontPro;
	}

	public void setnTextFontPro(short nTextFontPro) {
		this.nTextFontPro = nTextFontPro;
	}

	public int getnTextFontColor() {
		return nTextFontColor;
	}

	public void setnTextFontColor(int nTextFontColor) {
		this.nTextFontColor = nTextFontColor;
	}

	public CSS_TYPE getnTextStyle() {
		return nTextStyle;
	}

	public void setnTextStyle(CSS_TYPE nTextStyle) {
		this.nTextStyle = nTextStyle;
	}

	public int getnTextAlpha() {
		return nTextAlpha;
	}

	public void setnTextAlpha(int nTextAlpha) {
		this.nTextAlpha = nTextAlpha;
	}

	public int getnTextForeColor() {
		return nTextForeColor;
	}

	public void setnTextForeColor(int nTextForeColor) {
		this.nTextForeColor = nTextForeColor;
	}

	public int getnTextBackColor() {
		return nTextBackColor;
	}

	public void setnTextBackColor(int nTextBackColor) {
		this.nTextBackColor = nTextBackColor;
	}

	public boolean isnTextAdapt() {
		return nTextAdapt;
	}

	public void setnTextAdapt(boolean nTextAdapt) {
		this.nTextAdapt = nTextAdapt;
	}

	
}
