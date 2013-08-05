package com.android.Samkoonhmi.model;

import android.graphics.Color;

import com.android.Samkoonhmi.skenum.CSS_TYPE;
import com.android.Samkoonhmi.skenum.Direction.DIRECTION;
import com.android.Samkoonhmi.skenum.SPEED;
import com.android.Samkoonhmi.util.AddrProp;

/**
 * 流动块实体类
 * 
 * @author 马文俊
 * 
 */
public class FlowBlockModel {

	private Integer id; // 编号
	private int startX = 60; // 开始坐标x
	private int startY = 60; // 开始坐标y
	private int rectWidth = 240; // 矩形宽
	private int rectHeight = 65; // 矩形高
	private int nFForeColor = Color.BLUE; // 流动块前景色
	private int nFBackColor = Color.YELLOW; // 流动块背景色
	private int nDForeColor = Color.YELLOW; // 管道前景色
	private int nDBackColor = Color.GRAY; // 管道背景色
	private int nFrameColor = Color.BLACK; // 边框色
	private CSS_TYPE eStyle = CSS_TYPE.CSS_SOLIDCOLOR; // 样式
	private AddrProp nTriggerAddress; // 触发地址
	private DIRECTION eShowWay = DIRECTION.LEVEL; // 显示方式(垂直/水平)
	private DIRECTION eFlowDirection = DIRECTION.TOWARD_RIGHT; // 流动方向(左/右/上/下)
	private boolean bTouchAddress = false; // 使用触控地址把流动方向更改为反方向
	private AddrProp nTouchAddress; // 触发地址
	private int nValidState;// 有效状态
	private int nFlowNum = 5; // 流动块数
	private boolean bSizeLine = true; // 是否显示边框线
	private SPEED eSpeedType = SPEED.TRENDFLOWSPEED; // 选择流动速度
	private SPEED eFixedFlowSpeed = SPEED.LOW; // 固定流动速度(低中高)
	private AddrProp nTrendFlowAddress;// 动态流动速度地址
	private int nTrendFlowSpeed = 1; // 动态流动速度(1~10)
	private int nZvalue;
	private int nCollidindId;
	private ShowInfo showInfo;//显现对象

	/**
	 * 构造函数
	 */
	public FlowBlockModel() {
		super();
	}

	public ShowInfo getShowInfo() {
		return showInfo;
	}

	public void setShowInfo(ShowInfo showInfo) {
		this.showInfo = showInfo;
	}

	/**
	 * 封装
	 * 
	 * @return
	 */
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getStartX() {
		return startX;
	}

	public void setStartX(int startX) {
		this.startX = startX;
	}

	public int getStartY() {
		return startY;
	}

	public void setStartY(int startY) {
		this.startY = startY;
	}

	public int getRectWidth() {
		return rectWidth;
	}

	public void setRectWidth(int rectWidth) {
		this.rectWidth = rectWidth;
	}

	public int getRectHeight() {
		return rectHeight;
	}

	public void setRectHeight(int rectHeight) {
		this.rectHeight = rectHeight;
	}

	public int getnFForeColor() {
		return nFForeColor;
	}

	public void setnFForeColor(int nFForeColor) {
		this.nFForeColor = nFForeColor;
	}

	public int getnFBackColor() {
		return nFBackColor;
	}

	public void setnFBackColor(int nFBackColor) {
		this.nFBackColor = nFBackColor;
	}

	public int getnDForeColor() {
		return nDForeColor;
	}

	public void setnDForeColor(int nDForeColor) {
		this.nDForeColor = nDForeColor;
	}

	public int getnDBackColor() {
		return nDBackColor;
	}

	public void setnDBackColor(int nDBackColor) {
		this.nDBackColor = nDBackColor;
	}

	public int getnFrameColor() {
		return nFrameColor;
	}

	public void setnFrameColor(int nFrameColor) {
		this.nFrameColor = nFrameColor;
	}

	public CSS_TYPE geteStyle() {
		return eStyle;
	}

	public void seteStyle(CSS_TYPE eStyle) {
		this.eStyle = eStyle;
	}

	public DIRECTION geteShowWay() {
		return eShowWay;
	}

	public void seteShowWay(DIRECTION eShowWay) {
		this.eShowWay = eShowWay;
	}

	public DIRECTION geteFlowDirection() {
		return eFlowDirection;
	}

	public void seteFlowDirection(DIRECTION eFlowDirection) {
		this.eFlowDirection = eFlowDirection;
	}

	public boolean isbTouchAddress() {
		return bTouchAddress;
	}

	public void setbTouchAddress(boolean bTouchAddress) {
		this.bTouchAddress = bTouchAddress;
	}

	public int getnValidState() {
		return nValidState;
	}

	public void setnValidState(int nValidState) {
		this.nValidState = nValidState;
	}

	public int getnFlowNum() {
		return nFlowNum;
	}

	public void setnFlowNum(int nFlowNum) {
		this.nFlowNum = nFlowNum;
	}

	public boolean isbSizeLine() {
		return bSizeLine;
	}

	public void setbSizeLine(boolean bSizeLine) {
		this.bSizeLine = bSizeLine;
	}

	public SPEED geteSpeedType() {
		return eSpeedType;
	}

	public void seteSpeedType(SPEED eSpeedType) {
		this.eSpeedType = eSpeedType;
	}

	public SPEED geteFixedFlowSpeed() {
		return eFixedFlowSpeed;
	}

	public void seteFixedFlowSpeed(SPEED eFixedFlowSpeed) {
		this.eFixedFlowSpeed = eFixedFlowSpeed;
	}

	public int getnTrendFlowSpeed() {
		return nTrendFlowSpeed;
	}

	public void setnTrendFlowSpeed(int nTrendFlowSpeed) {
		this.nTrendFlowSpeed = nTrendFlowSpeed;
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

	public AddrProp getnTriggerAddress() {
		return nTriggerAddress;
	}

	public void setnTriggerAddress(AddrProp nTriggerAddress) {
		this.nTriggerAddress = nTriggerAddress;
	}

	public AddrProp getnTouchAddress() {
		return nTouchAddress;
	}

	public void setnTouchAddress(AddrProp nTouchAddress) {
		this.nTouchAddress = nTouchAddress;
	}

	public AddrProp getnTrendFlowAddress() {
		return nTrendFlowAddress;
	}

	public void setnTrendFlowAddress(AddrProp nTrendFlowAddress) {
		this.nTrendFlowAddress = nTrendFlowAddress;
	}
}
