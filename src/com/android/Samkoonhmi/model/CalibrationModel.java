package com.android.Samkoonhmi.model;

import android.graphics.Point;

import com.android.Samkoonhmi.skenum.CALIBRATION_DIRECTION;

public class CalibrationModel {
	private Integer id;
	private int lineColor;// 线的颜色
	private int textColor;// 文本的颜色
	private CALIBRATION_DIRECTION calibrationDirection;// 刻度的方向
	private CALIBRATION_DIRECTION numberIncreaseDirection;// 文本递增方向
	private int mainNumberCount;// 主刻度数目
	private int nextNumberCount;// 次刻度数目
	private boolean isShowText;// 是否显示文字 （数值）
	private double maxNumber;// 最大值
	private double minNumber;// 最小值
	private int decimalCount;// 文本小数位数
	private int textSize; // 字体的大小
	private int startX; // 线的开始点x
	private int startY; // 线的开始点y
	private int calibrationWidth;// 刻度的宽
	private int calibrationHeight;// 刻度的高

	private boolean isShowLateral;// 是否显示支线 （次刻度和数值）
	// 以下的属性可计算得来，不需再次定值

	private Point leftTopPoint;// 左上角的位置
	private int nextLineLength; // 次刻度的长度
	private int mainLineLength; // 主刻度的长度
	private double mainStopX; // 主支线线的结束点x
	private double nextStopX; // 次支线线的结束点x
	private double mainStopY; // 主支线的结束点y
	private double nextStopY; // 次支线的结束点y
	private double mainXSpace; // 主横坐标的间距
	private double nextXSpace; // 次横坐标的间距
	private double mainYSpace; // 主纵坐标的间距
	private double nextYSpace; // 次纵坐标的间距
	private boolean bShowShaft;// 是否显示轴
	private boolean nTotalCount;// 总位数

	private int nZvalue;
	private int nCollidindId;

	public CalibrationModel() {
		super();
	}

	public CalibrationModel(Integer id, int lineColor, int textColor,
			CALIBRATION_DIRECTION calibrationDirection,
			CALIBRATION_DIRECTION numberIncreaseDirection, int mainNumberCount,
			int nextNumberCount, boolean isShowText, double maxNumber,
			double minNumber, int decimalCount, int textSize, int startX,
			int startY, int calibrationWidth, int calibrationHeight,
			boolean isShowLateral, Point leftTopPoint, int nextLineLength,
			int mainLineLength, double mainStopX, double nextStopX,
			double mainStopY, double nextStopY, double mainXSpace,
			double nextXSpace, double mainYSpace, double nextYSpace,
			boolean bShowShaft, boolean nTotalCount, int nZvalue,
			int nCollidindId) {
		super();
		this.id = id;
		this.lineColor = lineColor;
		this.textColor = textColor;
		this.calibrationDirection = calibrationDirection;
		this.numberIncreaseDirection = numberIncreaseDirection;
		this.mainNumberCount = mainNumberCount;
		this.nextNumberCount = nextNumberCount;
		this.isShowText = isShowText;
		this.maxNumber = maxNumber;
		this.minNumber = minNumber;
		this.decimalCount = decimalCount;
		this.textSize = textSize;
		this.startX = startX;
		this.startY = startY;
		this.calibrationWidth = calibrationWidth;
		this.calibrationHeight = calibrationHeight;
		this.isShowLateral = isShowLateral;
		this.leftTopPoint = leftTopPoint;
		this.nextLineLength = nextLineLength;
		this.mainLineLength = mainLineLength;
		this.mainStopX = mainStopX;
		this.nextStopX = nextStopX;
		this.mainStopY = mainStopY;
		this.nextStopY = nextStopY;
		this.mainXSpace = mainXSpace;
		this.nextXSpace = nextXSpace;
		this.mainYSpace = mainYSpace;
		this.nextYSpace = nextYSpace;
		this.bShowShaft = bShowShaft;
		this.nTotalCount = nTotalCount;
		this.nZvalue = nZvalue;
		this.nCollidindId = nCollidindId;
	}

	public double getMainXSpace() {
		return mainXSpace;
	}

	public void setMainXSpace(double mainXSpace) {
		this.mainXSpace = mainXSpace;
	}

	public double getNextXSpace() {
		return nextXSpace;
	}

	public void setNextXSpace(double nextXSpace) {
		this.nextXSpace = nextXSpace;
	}

	public double getMainYSpace() {
		return mainYSpace;
	}

	public void setMainYSpace(double mainYSpace) {
		this.mainYSpace = mainYSpace;
	}

	public double getNextYSpace() {
		return nextYSpace;
	}

	public void setNextYSpace(double nextYSpace) {
		this.nextYSpace = nextYSpace;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getLineColor() {
		return lineColor;
	}

	public void setLineColor(int lineColor) {
		this.lineColor = lineColor;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public CALIBRATION_DIRECTION getCalibrationDirection() {
		return calibrationDirection;
	}

	public void setCalibrationDirection(
			CALIBRATION_DIRECTION calibrationDirection) {
		this.calibrationDirection = calibrationDirection;
	}

	public CALIBRATION_DIRECTION getNumberIncreaseDirection() {
		return numberIncreaseDirection;
	}

	public void setNumberIncreaseDirection(
			CALIBRATION_DIRECTION numberIncreaseDirection) {
		this.numberIncreaseDirection = numberIncreaseDirection;
	}

	public boolean isShowLateral() {
		return isShowLateral;
	}

	public void setShowLateral(boolean isShowLateral) {
		this.isShowLateral = isShowLateral;
	}

	public int getMainNumberCount() {
		return mainNumberCount;
	}

	public void setMainNumberCount(int mainNumberCount) {
		this.mainNumberCount = mainNumberCount;
	}

	public int getNextNumberCount() {
		return nextNumberCount;
	}

	public void setNextNumberCount(int nextNumberCount) {
		this.nextNumberCount = nextNumberCount;
	}

	public boolean isShowText() {
		return isShowText;
	}

	public void setShowText(boolean isShowText) {
		this.isShowText = isShowText;
	}

	public double getMaxNumber() {
		return maxNumber;
	}

	public void setMaxNumber(double maxNumber) {
		this.maxNumber = maxNumber;
	}

	public double getMinNumber() {
		return minNumber;
	}

	public void setMinNumber(double minNumber) {
		this.minNumber = minNumber;
	}

	public int getDecimalCount() {
		return decimalCount;
	}

	public void setDecimalCount(int decimalCount) {
		this.decimalCount = decimalCount;
	}

	public int getTextSize() {
		return textSize;
	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

	public Point getLeftTopPoint() {
		return leftTopPoint;
	}

	public void setLeftTopPoint(Point leftTopPoint) {
		this.leftTopPoint = leftTopPoint;
	}

	public int getCalibrationWidth() {
		return calibrationWidth;
	}

	public void setCalibrationWidth(int calibrationWidth) {
		this.calibrationWidth = calibrationWidth;
	}

	public int getCalibrationHeight() {
		return calibrationHeight;
	}

	public void setCalibrationHeight(int calibrationHeight) {
		this.calibrationHeight = calibrationHeight;
	}

	public int getMainLineLength() {
		return mainLineLength;
	}

	public void setMainLineLength(int mainLineLength) {
		this.mainLineLength = mainLineLength;
	}

	public int getNextLineLength() {
		return nextLineLength;
	}

	public void setNextLineLength(int nextLineLength) {
		this.nextLineLength = nextLineLength;
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

	public double getMainStopX() {
		return mainStopX;
	}

	public void setMainStopX(double mainStopX) {
		this.mainStopX = mainStopX;
	}

	public double getNextStopX() {
		return nextStopX;
	}

	public void setNextStopX(double nextStopX) {
		this.nextStopX = nextStopX;
	}

	public double getMainStopY() {
		return mainStopY;
	}

	public void setMainStopY(double mainStopY) {
		this.mainStopY = mainStopY;
	}

	public double getNextStopY() {
		return nextStopY;
	}

	public void setNextStopY(double nextStopY) {
		this.nextStopY = nextStopY;
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

	public boolean isbShowShaft() {
		return bShowShaft;
	}

	public void setbShowShaft(boolean bShowShaft) {
		this.bShowShaft = bShowShaft;
	}

	public boolean isnTotalCount() {
		return nTotalCount;
	}

	public void setnTotalCount(boolean nTotalCount) {
		this.nTotalCount = nTotalCount;
	}

}
