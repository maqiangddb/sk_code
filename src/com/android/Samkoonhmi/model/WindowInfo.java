package com.android.Samkoonhmi.model;

import com.android.Samkoonhmi.skenum.BACKCSS;
import com.android.Samkoonhmi.skenum.CSS_TYPE;

/**
 * 窗口属性类
 * @author 瞿丽平
 *
 */
public class WindowInfo {
	private int id	;//integer	id号
	private int nSceneId;//	integer	场景和窗口合并在一起的一个编号
	private int nNum;//画面序号
	private String sScreenName;//	名字
	private int nShowPosX;	//smallint	窗口的起点x坐标
	private int nShowPosY;	//smallint	窗口的起点y坐标
	private int nWindownWidth;	//smallint	窗口的宽度
	private int nWindownHeight;//	smallint	窗口的高度
	private boolean bShowTitle;//	bool;	//是否显示标题
	private boolean bShowShutBtn;//	Bool;	//是否显示关闭按钮
	private String sTileName;//	String	标题名字
	private int nBackColor;//	integer	背景色
	private BACKCSS eBackType;  // smallint 背景类型：样式、图片,1-颜色，2-图片
	private int nForeColor;     // integer 前景色
	private CSS_TYPE eDrawStyle;// smallint 填充样式
	private String sPicturePath;// String 图片路径
	private boolean bShowMiddle;//是否显示正中
	private boolean bLogout;
	
	public int getnNum() {
		return nNum;
	}

	public void setnNum(int nNum) {
		this.nNum = nNum;
	}
	public boolean isbLogout() {
		return bLogout;
	}

	public void setbLogout(boolean bLogout) {
		this.bLogout = bLogout;
	}

	public boolean isbShowMiddle() {
		return bShowMiddle;
	}

	public void setbShowMiddle(boolean bShowMiddle) {
		this.bShowMiddle = bShowMiddle;
	}

	public WindowInfo() {
		super();
	}
	
	public BACKCSS geteBackType() {
		return eBackType;
	}
	public void seteBackType(BACKCSS eBackType) {
		this.eBackType = eBackType;
	}
	public int getnForeColor() {
		return nForeColor;
	}
	public void setnForeColor(int nForeColor) {
		this.nForeColor = nForeColor;
	}
	public CSS_TYPE geteDrawStyle() {
		return eDrawStyle;
	}
	public void seteDrawStyle(CSS_TYPE eDrawStyle) {
		this.eDrawStyle = eDrawStyle;
	}
	public String getsPicturePath() {
		return sPicturePath;
	}
	public void setsPicturePath(String sPicturePath) {
		this.sPicturePath = sPicturePath;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getnSceneId() {
		return nSceneId;
	}
	public void setnSceneId(int nSceneId) {
		this.nSceneId = nSceneId;
	}
	public String getsScreenName() {
		return sScreenName;
	}
	public void setsScreenName(String sScreenName) {
		this.sScreenName = sScreenName;
	}
	public int getnShowPosX() {
		return nShowPosX;
	}
	public void setnShowPosX(int nShowPosX) {
		this.nShowPosX = nShowPosX;
	}
	public int getnShowPosY() {
		return nShowPosY;
	}
	public void setnShowPosY(int nShowPosY) {
		this.nShowPosY = nShowPosY;
	}
	public int getnWindownWidth() {
		return nWindownWidth;
	}
	public void setnWindownWidth(int nWindownWidth) {
		this.nWindownWidth = nWindownWidth;
	}
	public int getnWindownHeight() {
		return nWindownHeight;
	}
	public void setnWindownHeight(int nWindownHeight) {
		this.nWindownHeight = nWindownHeight;
	}
	public boolean isbShowTitle() {
		return bShowTitle;
	}
	public void setbShowTitle(boolean bShowTitle) {
		this.bShowTitle = bShowTitle;
	}
	public boolean isbShowShutBtn() {
		return bShowShutBtn;
	}
	public void setbShowShutBtn(boolean bShowShutBtn) {
		this.bShowShutBtn = bShowShutBtn;
	}
	public String getsTileName() {
		return sTileName;
	}
	public void setsTileName(String sTileName) {
		this.sTileName = sTileName;
	}
	public int getnBackColor() {
		return nBackColor;
	}
	public void setnBackColor(int nBackColor) {
		this.nBackColor = nBackColor;
	}
   

}
