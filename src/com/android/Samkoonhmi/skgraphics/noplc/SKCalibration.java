package com.android.Samkoonhmi.skgraphics.noplc;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Vector;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Join;
import android.graphics.Point;
import android.graphics.Rect;
import com.android.Samkoonhmi.graphicsdrawframe.LineItem;
import com.android.Samkoonhmi.model.CalibrationModel;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.skenum.CALIBRATION_DIRECTION;
import com.android.Samkoonhmi.skenum.END_ARROW_TYPE;
import com.android.Samkoonhmi.skenum.END_POINT_TYPE;
import com.android.Samkoonhmi.skenum.LINE_TYPE;
import com.android.Samkoonhmi.skgraphics.noplc.base.SKGraphicsBase;
import com.android.Samkoonhmi.skwindow.SKSceneManage;

/**
 * 画刻度
 * 
 * @author Administrator
 * 
 */
public class SKCalibration extends SKGraphicsBase {

	private CalibrationModel calibration;
	private Point lastPoint; // 最后一个点的坐标
	private Paint paint;//画笔
	private Canvas canvas;//画布
	private Rect totalRect;//外矩形
	private SKItems skItem;//item
	private int sceneid;
	private int itemId;

	/**
	 * 构造函数
	 */
	public SKCalibration(int sceneid,int itemId,CalibrationModel model) {
		// 初始化值
		super();
		this.itemId=itemId;
		this.sceneid=sceneid;
		totalRect=new Rect();
		paint=new Paint();
		skItem=new SKItems();
		this.calibration=model;

		skItem.nCollidindId=calibration.getnCollidindId();
		skItem.itemId=itemId;
		skItem.nZvalue=calibration.getnZvalue();
		skItem.rect=totalRect;
		skItem.sceneId=sceneid;
		skItem.mGraphics=this;
	}
	/**
	 * 计算一些常用坐标
	 */

	private void calculate() {
		totalRect.set(calibration.getStartX(), calibration.getStartY(), calibration.getStartX()+calibration.getCalibrationWidth(), calibration.getStartY()+calibration.getCalibrationHeight());
		calibration.setLeftTopPoint(new Point(calibration.getStartX(), calibration.getStartY()));
		// 如果刻度的方向是向上
		if (CALIBRATION_DIRECTION.DIRECTION_UP == calibration.getCalibrationDirection()) {
			calibration.setLeftTopPoint(new Point(calibration.getStartX(), calibration.getStartY()+calibration.getCalibrationHeight()));
			if (true == calibration.isShowText())
				calibration.setMainLineLength(calibration.getCalibrationHeight()/2);//主刻度长
			else
				calibration.setMainLineLength(calibration.getCalibrationHeight());//主刻度长
			calibration.setNextLineLength(calibration.getMainLineLength()/2);//次刻度的长度=主刻度的长度/2
			calibration.setMainStopX(calibration.getLeftTopPoint().x);//主支线线的结束点x=左上角x
			calibration.setNextStopX(calibration.getLeftTopPoint().x);//次支线线的结束点x=左上角x
			calibration.setMainStopY(calibration.getLeftTopPoint().y - calibration.getMainLineLength());//主支线的结束点y=左上角y-主刻度的长度
			calibration.setNextStopY(calibration.getLeftTopPoint().y - calibration.getNextLineLength());//次支线的结束点y=左上角y-次刻度的
			calibration.setMainXSpace((calibration.getCalibrationWidth()+1.2) / (calibration.getMainNumberCount()-1));//
			if (0 != calibration.getNextNumberCount()) {
				calibration.setNextXSpace(calibration.getMainXSpace() / (calibration.getNextNumberCount()+1));
			}
			lastPoint = new Point((calibration.getLeftTopPoint().x + calibration.getCalibrationWidth()+1),calibration.getLeftTopPoint().y);
		}
		// 向下
		else if (CALIBRATION_DIRECTION.DIRECTION_DOWN == calibration.getCalibrationDirection()) {
			if (true == calibration.isShowText())
				calibration.setMainLineLength(calibration.getCalibrationHeight()/2);//主刻度长
			else
				calibration.setMainLineLength(calibration.getCalibrationHeight());//主刻度长
			
			calibration.setNextLineLength(calibration.getMainLineLength()/2);//次刻度的长度=主刻度的长度/2
			calibration.setMainStopX(calibration.getLeftTopPoint().x);
			calibration.setNextStopX(calibration.getLeftTopPoint().x);
			calibration.setMainStopY(calibration.getLeftTopPoint().y + calibration.getMainLineLength());
			calibration.setNextStopY(calibration.getLeftTopPoint().y + calibration.getNextLineLength());
			calibration.setMainXSpace((calibration.getCalibrationWidth()+1.2) / (calibration.getMainNumberCount() - 1));
			if (0 != calibration.getNextNumberCount()) {
				calibration.setNextXSpace(calibration.getMainXSpace()/ (calibration.getNextNumberCount() + 1));
			}
			lastPoint = new Point((calibration.getLeftTopPoint().x + calibration.getCalibrationWidth()+1),calibration.getLeftTopPoint().y);
		}
		// 向左
		else if (CALIBRATION_DIRECTION.DIRECTION_LEFT == calibration.getCalibrationDirection()) {
			if (true == calibration.isShowText())
				calibration.setMainLineLength(calibration.getCalibrationWidth()/4);//主刻度长
			else
				calibration.setMainLineLength(calibration.getCalibrationWidth());//主刻度长
			calibration.setLeftTopPoint(new Point(calibration.getStartX()+calibration.getCalibrationWidth(), calibration.getStartY()));
			
			calibration.setNextLineLength(calibration.getMainLineLength()/2);//次刻度的长度=主刻度的长度/2
			calibration.setMainStopX(calibration.getLeftTopPoint().x - calibration.getMainLineLength());
			calibration.setNextStopX(calibration.getLeftTopPoint().x - calibration.getNextLineLength());
			calibration.setMainStopY(calibration.getLeftTopPoint().y);
			calibration.setNextStopY(calibration.getLeftTopPoint().y);
			calibration.setMainYSpace((calibration.getCalibrationHeight()+1.2) / (calibration.getMainNumberCount() - 1));
			if (0 != calibration.getNextNumberCount()) {
				calibration.setNextYSpace(calibration.getMainYSpace()/ (calibration.getNextNumberCount() + 1));
			}
			lastPoint = new Point(calibration.getStartX()+calibration.getCalibrationWidth(), calibration.getStartY() + calibration.getCalibrationHeight()+1);
		} else {//向右
			if (true == calibration.isShowText())
				calibration.setMainLineLength(calibration.getCalibrationWidth()/4);//主刻度长
			else
				calibration.setMainLineLength(calibration.getCalibrationWidth());//主刻度长
			
			calibration.setNextLineLength(calibration.getMainLineLength()/2);//次刻度的长度=主刻度的长度/2
			calibration.setMainStopX(calibration.getLeftTopPoint().x + calibration.getMainLineLength());
			calibration.setNextStopX(calibration.getLeftTopPoint().x + calibration.getNextLineLength());
			calibration.setMainStopY(calibration.getLeftTopPoint().y);
			calibration.setNextStopY(calibration.getLeftTopPoint().y);
			calibration.setMainYSpace((calibration.getCalibrationHeight()+1.2) / (calibration.getMainNumberCount() - 1));
			if (0 != calibration.getNextNumberCount()) {
				calibration.setNextYSpace(calibration.getMainYSpace()/ (calibration.getNextNumberCount() + 1));
			}
			lastPoint = new Point(calibration.getStartX(), calibration.getStartY() + calibration.getCalibrationHeight()+1);
		}
	}

	
	@Override
	public void initGraphics() {
		init();
	}
	
	public void init(){
		if(calibration==null){
			return ;
		}
		
		calculate();
		
		SKSceneManage.getInstance().onRefresh(skItem);
	}
	
	@Override
	public boolean drawGraphics(Canvas canvas, int itemId) {
		this.canvas=canvas;
		if(this.itemId==itemId){
			drawGraphics(canvas);
			return true;
		}
		return false;
	}
	/**
	 * 画图
	 */
	public void drawGraphics(Canvas canvas) {
		// 画主线
		Vector<Point> pointList = getMainLinePoints();
		LineItem line = getLine(pointList);
		line.draw(paint, canvas);
//		if (true == calibration.isShowLateral()) {//如果显示支线
			// 画支线
			Vector<Point> nextPointList = getNextLinePoints();
			line = getLine(nextPointList);
			line.draw(paint, canvas);
//		}
	}

	/**
	 * 设置LineItem的属性
	 * 
	 * @param pointList
	 * @return
	 */
	private LineItem getLine(Vector<Point> pointList) {
		
		LineItem line =new LineItem(pointList);
		line.setAlpha(255);//线的透明度
		line.setLineColor(calibration.getLineColor());//线的颜色
		line.setLineWidth(1);//线的宽度
		line.setLineType(LINE_TYPE.SOLID_LINE);//线的类型
		line.setEndPointType(END_POINT_TYPE.FLAT_CAP);
		line.setEndArrowType(END_ARROW_TYPE.ARROW_STYLE_BUTT);
		return line;
	}

	/**
	 * 计算主干的末尾点
	 * 
	 * @return
	 */
	private Vector<Point> getMainLinePoints() {
		Vector<Point> pointList = new Vector<Point>();
		// 添加初始点
		pointList.add(calibration.getLeftTopPoint());
		// 添加末尾点
		pointList.add(lastPoint);
		return pointList;
	}

	/**
	 * 画刻度的数值方法
	 */
	private void getText(String text, int x, int y, String direString) {
		Paint p=new Paint();
		p.setAntiAlias(false);//去锯齿
		p.setColor(calibration.getTextColor());//字体颜色
		p.setStyle(Paint.Style.STROKE);//样式
		p.setTextSize(calibration.getTextSize());//字体大小
		p.setStrokeJoin(Join.ROUND);
		if ("left".equals(direString)) {
			p.setTextAlign(Align.RIGHT);
		} else if ("right".equals(direString)) {
			p.setTextAlign(Align.LEFT);
		} else {
			p.setTextAlign(Align.CENTER);
		}
		paint.setAlpha(255);//透明度
		canvas.drawText(text, x, y, p);

	}

	/**
	 * 画支线
	 */
	private Vector<Point> getNextLinePoints() {
		Vector<Point> pointList = new Vector<Point>();
		// 如果刻度的方向是向上 或者向下
		if (CALIBRATION_DIRECTION.DIRECTION_UP == calibration.getCalibrationDirection()
				|| CALIBRATION_DIRECTION.DIRECTION_DOWN == calibration.getCalibrationDirection()) {
			// 循环主刻度的数目 画线
			// 计算主横坐标的间距
			if (calibration.getMainNumberCount() > 1) {
				for (int i = 0; i < calibration.getMainNumberCount(); i++) {
					int x1 = (int) (calibration.getLeftTopPoint().x + (i * calibration.getMainXSpace()));
					int y1 = calibration.getLeftTopPoint().y;
					int x2 = x1;
					int y2 = (int)calibration.getMainStopY();
					// 添加主支线
					pointList.add(new Point(x1, y1));
					pointList.add(new Point(x2, y2));

					if (true == calibration.isShowText()) {// 显示刻度的数值
						if(CALIBRATION_DIRECTION.DIRECTION_UP == calibration.getCalibrationDirection()){//上
							this.drawNumberText(x2, y2-calibration.getCalibrationHeight()/5, i);
						}else if(CALIBRATION_DIRECTION.DIRECTION_DOWN == calibration.getCalibrationDirection()){//下
							this.drawNumberText(x2, y2+calibration.getCalibrationHeight()/5, i);
						}
					}
					if (0 != calibration.getNextNumberCount() && i < calibration.getMainNumberCount() - 1) {
						for (int j = 1; j < calibration.getNextNumberCount() + 1; j++) {
							int nx1 = (int) (x1 + (j * calibration.getNextXSpace()));
							int ny1 = calibration.getLeftTopPoint().y;
							int nx2 = nx1;
							int ny2 = (int) calibration.getNextStopY();
							// 添加次支线
							pointList.add(new Point(nx1, ny1));
							pointList.add(new Point(nx2, ny2));
						}
					}
				}
			}
		}
		// 刻度方向左向右
		else if (CALIBRATION_DIRECTION.DIRECTION_LEFT == calibration.getCalibrationDirection()
				|| CALIBRATION_DIRECTION.DIRECTION_RIGHT == calibration.getCalibrationDirection()) {
			// 循环主刻度的数目 画线
			// 计算主横坐标的间距
			if (calibration.getMainNumberCount() > 1) {
				for (int i = 0; i < calibration.getMainNumberCount(); i++) {
					int x1 = calibration.getLeftTopPoint().x;
					int y1 =  (int) (calibration.getLeftTopPoint().y + (i * calibration.getMainYSpace()));
					int x2 =  (int) calibration.getMainStopX();
					int y2 = y1;
					pointList.add(new Point(x1, y1));
					pointList.add(new Point(x2, y2));

					if (true == calibration.isShowText()) {// 显示刻度的数值
						if(CALIBRATION_DIRECTION.DIRECTION_LEFT == calibration.getCalibrationDirection()){//左
							drawNumberText(x2-calibration.getCalibrationWidth()/5, y2+5, i);
						}else if(CALIBRATION_DIRECTION.DIRECTION_RIGHT == calibration.getCalibrationDirection()){//右
							drawNumberText(x2+calibration.getCalibrationWidth()/5, y2+5, i);
						}
						
					}
					//支线
					if (0 != calibration.getNextNumberCount()
							&& i < calibration.getMainNumberCount() - 1) {
						for (int j = 1; j < calibration.getNextNumberCount() + 1; j++) {
							int nx1 = calibration.getLeftTopPoint().x;
							int ny1 = (int) (y1 + (j * calibration.getNextYSpace()));
							int nx2 = (int) calibration.getNextStopX();
							int ny2 = ny1;
							pointList.add(new Point(nx1, ny1));
							pointList.add(new Point(nx2, ny2));
						}
					}
				}
			}
		}
		return pointList;
	}

	/**
	 * 计算每个主刻度之间的间距 包括符号（可能为负数）
	 */
	private double getTemp() {
		double tempMax = 0;
		double tempMin = 0;
		double temp = 0;
		if (calibration.getMinNumber() < 0 && calibration.getMaxNumber() < 0) {//如果最大值和最小值为负数
			tempMax = -calibration.getMaxNumber();
			tempMin = -calibration.getMinNumber();
			if (tempMax > tempMin) {//最大值大于最小值
				temp = -((tempMax - tempMin) / (calibration.getMainNumberCount() - 1));
			} else {
				temp = -((tempMin - tempMax) / (calibration.getMainNumberCount() - 1));
			}
		} else if (calibration.getMinNumber() < 0 && calibration.getMaxNumber() > 0) {//最小值<0，最大值>0
			tempMin = -calibration.getMinNumber();
			temp = (calibration.getMaxNumber() + tempMin)/ (calibration.getMainNumberCount() - 1);
		} else if (calibration.getMinNumber() > 0 && calibration.getMaxNumber() < 0) {
			tempMax = -calibration.getMaxNumber();
			temp = (tempMax + calibration.getMinNumber())/ (calibration.getMainNumberCount() - 1);
		} else {
			if (calibration.getMinNumber() < calibration.getMaxNumber()) {
				temp = (calibration.getMaxNumber() - calibration.getMinNumber())/ (calibration.getMainNumberCount() - 1);
			} else {
				temp = (calibration.getMinNumber() - calibration.getMaxNumber())/ (calibration.getMainNumberCount() - 1);
			}
		}
		return temp;
	}

	/**
	 * 根据第i个主刻度，得到要显示的数值
	 */
	private double getNumberText(int i) {
		double temp = getTemp();
		double number = 0L;
		//如果刻度方向向上，数值向左
		if (CALIBRATION_DIRECTION.DIRECTION_UP == calibration.getNumberIncreaseDirection()
				|| CALIBRATION_DIRECTION.DIRECTION_LEFT == calibration.getNumberIncreaseDirection()){ // 数值向上递增
			if (i == 0) {
				number = Math.rint(calibration.getMaxNumber());
			} else if (i == calibration.getMainNumberCount() - 1) {
				number = Math.rint(calibration.getMinNumber());
			} else {
				number = calibration.getMaxNumber() - Math.rint(i * temp);
			}
		} else if (CALIBRATION_DIRECTION.DIRECTION_DOWN == calibration.getNumberIncreaseDirection()
				|| CALIBRATION_DIRECTION.DIRECTION_RIGHT == calibration.getNumberIncreaseDirection()) {// 数值向下递增
			if (i == 0) {
				number = Math.rint(calibration.getMinNumber());

			} else if (i == calibration.getMainNumberCount() - 1) {
				number = Math.rint(calibration.getMaxNumber());
			} else {
				number = calibration.getMinNumber() + Math.rint(i * temp);
			}
		} else {
			number = 0;
		}
		return (double) number;
	}

	/**
	 * 根据点画刻度数值
	 */
	private void drawNumberText(int x2, int y2, int i) {
		// 要显示的刻度字符串
		String text = "";
		BigDecimal b;
		DecimalFormat format;//小数点格式
		// 刻度数值
		double number = getNumberText(i);
		double tempNumber = 0L;
		// 根据小数的位数得到要显示的刻度
		if (1 == calibration.getDecimalCount()) {
			tempNumber =  (0.1 * number);
			b=new BigDecimal(tempNumber);
			tempNumber= b.setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue();
			format=new DecimalFormat("#.0");
			text=format.format(tempNumber);
		} else if (2 == calibration.getDecimalCount()) {
			tempNumber = (0.01 * number);
			b=new BigDecimal(tempNumber);
			tempNumber= b.setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
			format=new DecimalFormat("#.00");
			text=format.format(tempNumber);
		} else if (3 == calibration.getDecimalCount()) {
			tempNumber =  (0.001 * number);
			b=new BigDecimal(tempNumber);
			tempNumber= b.setScale(3, BigDecimal.ROUND_HALF_DOWN).doubleValue();
			format=new DecimalFormat("#.000");
			text=format.format(tempNumber);
		} else {
			b=new BigDecimal(number);
			text=b.toPlainString();
		}
		b=new BigDecimal(text);
		text=b.toPlainString();
		// 根据不同的刻度显示的位置设置画刻度
		if (CALIBRATION_DIRECTION.DIRECTION_LEFT == calibration.getCalibrationDirection()) {
			getText(text, x2, y2, "left");
		} else if (CALIBRATION_DIRECTION.DIRECTION_DOWN == calibration.getCalibrationDirection()) {
			getText(text, x2, y2 + calibration.getTextSize()-1, "center");
		} else if (CALIBRATION_DIRECTION.DIRECTION_UP == calibration.getCalibrationDirection()){
			getText(text, x2, y2, "center");
		} else {
			getText(text, x2, y2, "right");
		}
	}

	
	/**
	 * 得到数据库的数据
	 */
	@Override
	public void getDataFromDatabase() {
		
	}

	/**
	 * 设置数据库的数据
	 */
	@Override
	public void setDataToDatabase() {

	}

	/**
	 * 清空所有属性，释放内服
	 */
	@Override
	public void realseMemeory() {
		
	}
	
	
}