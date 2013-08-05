package com.android.Samkoonhmi.model;

import java.util.ArrayList;

import android.R.integer;
import android.util.Log;

import com.android.Samkoonhmi.skenum.BACKCSS;
import com.android.Samkoonhmi.skenum.CSS_TYPE;
import com.android.Samkoonhmi.skwindow.SKSceneManage.SHOW_TYPE;

/**
 * 画面属性
 * 
 * @author 瞿丽平
 * 
 */
public class ScenceInfo {
	private int id;             // integer id号
	private int nSceneId;       // integer 场景和窗口合并在一起的一个编号
	private int nBeforeSId;     // 进入当前场景的场景id
	private int nNum;           // 画面序号 
	private String sScreenName; // String 名字
	private int nSceneWidth;    // smallint 画面宽度
	private int nSceneHeight;   // smallint 画面高度
	private BACKCSS eBackType;  // smallint 背景类型：样式、图片,1-颜色，2-图片
	private int nBackColor;     // integer 背景颜色
	private int nForeColor;     // integer 前景色
	private CSS_TYPE eDrawStyle;// smallint 填充样式
	private String sPicturePath;// String 图片路径
	private boolean bShowTitle; // 是否显示标题
	private SHOW_TYPE eType;    // 是否是窗口
	private boolean bSlide;     //是否启动滑动打开画面
	private int nTowardLeftId;  //向左 跳转id，0-按顺序跳转
	private int nTowardRIghtId; //向右 跳转id，0-按顺序跳转
	private int nSlideStyle;    //切换效果
	private boolean bAddMenu;   //画面是否添加到画面菜单
	private int nLeftX;         //左顶点X坐标
	private int nLeftY;         //左顶点Y坐标
	private boolean bLogout;    //切换注销用户
	
	public int getnBeforeSId() {
		return nBeforeSId;
	}

	public void setnBeforeSId(int nBeforeSId) {
		this.nBeforeSId = nBeforeSId;
	}
	
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

	public int getnLeftX() {
		return nLeftX;
	}

	public void setnLeftX(int nLeftX) {
		this.nLeftX = nLeftX;
	}

	public int getnLeftY() {
		return nLeftY;
	}

	public void setnLeftY(int nLeftY) {
		this.nLeftY = nLeftY;
	}

	public boolean isbAddMenu() {
		return bAddMenu;
	}

	public void setbAddMenu(boolean bAddMenu) {
		this.bAddMenu = bAddMenu;
	}

	public boolean isbSlide() {
		return bSlide;
	}

	public void setbSlide(boolean bSlide) {
		this.bSlide = bSlide;
	}

	public int getnTowardLeftId() {
		return nTowardLeftId;
	}

	public void setnTowardLeftId(int nTowardLeftId) {
		this.nTowardLeftId = nTowardLeftId;
	}

	public int getnTowardRIghtId() {
		return nTowardRIghtId;
	}

	public void setnTowardRIghtId(int nTowardRIghtId) {
		this.nTowardRIghtId = nTowardRIghtId;
	}

	public int getnSlideStyle() {
		return nSlideStyle;
	}

	public void setnSlideStyle(int nSlideStyle) {
		this.nSlideStyle = nSlideStyle;
	}

	private ArrayList<Short>  SceneMacroIDList; //场景宏指令ID列表
	
	public ScenceInfo() {
		super();
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
	public int getnSceneWidth() {
		return nSceneWidth;
	}
	public void setnSceneWidth(int nSceneWidth) {
		this.nSceneWidth = nSceneWidth;
	}
	public int getnSceneHeight() {
		return nSceneHeight;
	}
	public void setnSceneHeight(int nSceneHeight) {
		this.nSceneHeight = nSceneHeight;
	}
	public BACKCSS geteBackType() {
		return eBackType;
	}
	public void seteBackType(BACKCSS eBackType) {
		this.eBackType = eBackType;
	}
	public int getnBackColor() {
		return nBackColor;
	}
	public void setnBackColor(int nBackColor) {
		this.nBackColor = nBackColor;
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
	public boolean isbShowTitle() {
		return bShowTitle;
	}
	public void setbShowTitle(boolean bShowTitle) {
		this.bShowTitle = bShowTitle;
	}
	public SHOW_TYPE geteType() {
		return eType;
	}
	public void seteType(SHOW_TYPE eType) {
		this.eType = eType;
	}
	
	/**
	 * 设置宏指令列表
	 * */
	public void setSceneMacroIDList(ArrayList<Short> mlist){
		if(null == mlist){
			Log.e("SceneInfo","setSceneMacroIDList: mlist is null");
			return;
		}
		this.SceneMacroIDList = mlist;
	}

	/**
	 * 获得宏指令列表
	 * */
	public ArrayList<Short> getSceneMacroIDList(){
		return this.SceneMacroIDList;
	}
}
