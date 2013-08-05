package com.android.Samkoonhmi.model;

import com.android.Samkoonhmi.skenum.RECIPESELECT_TYPE;

public class RecipeSelectInfo {
	private int id;//	integer	id号
	private int nStartPosX;//	smallint	起始位置X坐标
	private int nStartPosY;//	smallint	起始位置Y坐标
	private int nWidth	;//smallint	控件的宽度
	private int nHeight	;//smallint	控件的高度
	private RECIPESELECT_TYPE eShowType;//	smallint	配方显示的样式（下拉或列表）
	private int sShowRecipeId;//	smallint	配方组的编号
	private String sFontFamily;//	varchar	字体样式
	private int nFontSize;//	smallint	字体大小
	private int nTextColor;	//Integer	文本颜色
	private int nBackColor;	//Integer	背景颜色
	private int nCurrShowRow;	//smallint	当前显示的行数
	private boolean bUseMacro;//	Boolean	是否使用宏指令
	private int nMacroId;//	smallint	宏指令ID号
	private int nTouchPropId;//	Int	触控属性ID号
	private int nShowPropId;//	Int	显隐属性ID号
	private int nZvalue ;
	private int nCollidindId ;
	private TouchInfo touchInfo;// 触控属性
	private ShowInfo showInfo;// 显现属性
	private int nTransparent;//控件透明度
	
	public int getnTransparent() {
		return nTransparent;
	}


	public void setnTransparent(int nTransparent) {
		this.nTransparent = nTransparent;
	}


	public RecipeSelectInfo() {
		super();
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
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getnStartPosX() {
		return nStartPosX;
	}
	public void setnStartPosX(int nStartPosX) {
		this.nStartPosX = nStartPosX;
	}
	public int getnStartPosY() {
		return nStartPosY;
	}
	public void setnStartPosY(int nStartPosY) {
		this.nStartPosY = nStartPosY;
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
	public RECIPESELECT_TYPE geteShowType() {
		return eShowType;
	}
	public void seteShowType(RECIPESELECT_TYPE eShowType) {
		this.eShowType = eShowType;
	}
	public int getsShowRecipeId() {
		return sShowRecipeId;
	}
	public void setsShowRecipeId(int sShowRecipeId) {
		this.sShowRecipeId = sShowRecipeId;
	}
	public String getsFontFamily() {
		return sFontFamily;
	}
	public void setsFontFamily(String sFontFamily) {
		this.sFontFamily = sFontFamily;
	}
	public int getnFontSize() {
		return nFontSize;
	}
	public void setnFontSize(int nFontSize) {
		this.nFontSize = nFontSize;
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
	public int getnCurrShowRow() {
		return nCurrShowRow;
	}
	public void setnCurrShowRow(int nCurrShowRow) {
		this.nCurrShowRow = nCurrShowRow;
	}
	public boolean isbUseMacro() {
		return bUseMacro;
	}
	public void setbUseMacro(boolean bUseMacro) {
		this.bUseMacro = bUseMacro;
	}
	public int getnMacroId() {
		return nMacroId;
	}
	public void setnMacroId(int nMacroId) {
		this.nMacroId = nMacroId;
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


}
