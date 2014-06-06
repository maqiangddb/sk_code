package com.android.Samkoonhmi.graphicsdrawframe;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Vector;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Join;
import android.graphics.Point;
import android.sax.StartElementListener;
import android.util.Log;

import com.android.Samkoonhmi.graphicsdrawframe.LineItem;
import com.android.Samkoonhmi.model.CalibrationModel;
import com.android.Samkoonhmi.model.SliderModel;
import com.android.Samkoonhmi.skenum.CALIBRATION_DIRECTION;
import com.android.Samkoonhmi.skenum.Direction.DIRECTION;
import com.android.Samkoonhmi.skenum.END_ARROW_TYPE;
import com.android.Samkoonhmi.skenum.END_POINT_TYPE;
import com.android.Samkoonhmi.skenum.LINE_TYPE;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.SKSlide2;
import com.android.Samkoonhmi.util.GlobalPopWindow.ICallBack;

//import SKGraphicsBase;
/**
 * 画刻度
 * 
 * @author Administrator
 * 
 */
public class CalibrationItem{

	private SliderModel slideInfo;
	private Point lastPoint; // 最后一个点的坐标
	private Paint mpaint;
	private Canvas mcanvas;
	
	private Point leftTopPoint;// 左上角的位置
	 private int nextLineLength ; // 次刻度的长度
	 private int mainLineLength ; // 主刻度的长度
	 private int mainStopX ; // 主支线线的结束点x
	 private int nextStopX ; // 次支线线的结束点x
	 private int mainStopY ; // 主支线的结束点y
	 private int nextStopY ; // 次支线的结束点y
	 private double mainXSpace ; // 主横坐标的间距
	 private double nextXSpace ; // 次横坐标的间距
	 private double mainYSpace ; // 主纵坐标的间距
	 private double nextYSpace ; // 次纵坐标的间距
	 private int mStartX=0;
	 private int mStartY=0;
	 private int mStopX=0;
	 private int mStopY=0;
	 /**
	  * 通过滑动块那边传过来的x y 坐标 得到刻度的值
	  */
     SKSlide2.ICallBack callback=new SKSlide2.ICallBack()
     {

		@Override
		public int move(int x, int y) {
			// TODO Auto-generated method stub
			return 0;
		}
    	 
     };
	/**
	 * 构造函数
	 */
	public CalibrationItem(SliderModel s,int startX,int startY,int stopX,int stopY) {
		
		this.slideInfo=s;
		 mStartX=startX;
		 mStartY=startY;
		 mStopX=stopX;
		 mStopY=stopY;
		calculate();
	}
	/**
	 * 计算一些常用坐标
	 */
	private void calculate() {
		// 如果刻度的方向是向上
		if (CALIBRATION_DIRECTION.DIRECTION_UP == slideInfo.getDirection()) {
			int len=slideInfo.getmHeight()-slideInfo.getnSlideHeight();
			leftTopPoint=new Point(mStartX, len);
			mainLineLength=len/2;//主刻度长
			nextLineLength=this.mainLineLength/2;//次刻度的长度=主刻度的长度/2
			mainStopX=this.leftTopPoint.x;//主支线线的结束点x=左上角x
			nextStopX=this.leftTopPoint.x;//次支线线的结束点x=左上角x
			mainStopY=this.leftTopPoint.y - this.mainLineLength+2;//主支线的结束点y=左上角y-主刻度的长度
			nextStopY=this.leftTopPoint.y - this.nextLineLength;//次支线的结束点y=左上角y-次刻度的
			mainXSpace=(mStopX-mStartX+1.1) / (slideInfo.getnMaxNumber()-1);//
			if (0 != slideInfo.getnMinNumber()) {
				nextXSpace=this.mainXSpace / (slideInfo.getnMinNumber()+1);
			}
			lastPoint = new Point(mStopX,this.leftTopPoint.y);
		}
		// 向下
		else if (CALIBRATION_DIRECTION.DIRECTION_DOWN == slideInfo.getDirection()) {
			leftTopPoint=new Point(mStartX, 1);
			int len=slideInfo.getmHeight()-slideInfo.getnSlideHeight();
			this.mainLineLength=len/2;//主刻度长
			this.nextLineLength=mainLineLength/2;//次刻度的长度=主刻度的长度/2
			this.mainStopX=this.leftTopPoint.x;
			this.nextStopX=this.leftTopPoint.x;
			this.mainStopY=this.leftTopPoint.y+this.mainLineLength-4;
			this.nextStopY=this.leftTopPoint.y+this.nextLineLength;
			this.mainXSpace=(mStopX-mStartX+1.1)/(slideInfo.getnMaxNumber()-1);
			if (0 != slideInfo.getnMinNumber()) {
				this.nextXSpace=this.mainXSpace/(slideInfo.getnMinNumber()+1);
			}
			lastPoint = new Point(mStopX,this.leftTopPoint.y);
		
		}
		// 向左
		else if (CALIBRATION_DIRECTION.DIRECTION_LEFT == slideInfo.getDirection()) {
			this.mainLineLength=slideInfo.getmWidth()/8;//主刻度长
			leftTopPoint=new Point(slideInfo.getmWidth()/2, mStartY);
			this.nextLineLength=this.mainLineLength/2;//次刻度的长度=主刻度的长度/2
			this.mainStopX=this.leftTopPoint.x-this.mainLineLength+2;
			this.nextStopX=this.leftTopPoint.x-this.nextLineLength;
			this.mainStopY=this.leftTopPoint.y;
			this.nextStopY=this.leftTopPoint.y;
			this.mainYSpace=(mStopY-mStartY+1.1)/(slideInfo.getnMaxNumber()-1);
			if (0 != slideInfo.getnMinNumber()) {
				this.nextYSpace=this.mainYSpace/(slideInfo.getnMinNumber()+1);
			}
			lastPoint = new Point(this.leftTopPoint.x,mStopY);
		} else {//向右
			this.mainLineLength=slideInfo.getmWidth()/8;//主刻度长
			leftTopPoint=new Point(1,mStartY);
			this.nextLineLength=this.mainLineLength/2;
			this.mainStopX=this.leftTopPoint.x+this.mainLineLength-2;
			this.nextStopX=this.leftTopPoint.x+this.nextLineLength;
			this.mainStopY=this.leftTopPoint.y;
			this.nextStopY=this.leftTopPoint.y;
			this.mainYSpace=(mStopY-mStartY+1.1)/(slideInfo.getnMaxNumber()-1);
			if (0 != slideInfo.getnMinNumber()) {
				this.nextYSpace=this.mainYSpace/(slideInfo.getnMinNumber()+1);
			}
			lastPoint = new Point(this.leftTopPoint.x,mStopY);
		}
	}

	

	/**
	 * 画图
	 */
	public void drawGraphics(Paint paint,Canvas canvas) {
		this.mcanvas=canvas;
		this.mpaint=paint;
		// 画主线
		Vector<Point> pointList = getMainLinePoints();
		LineItem line = getLine(pointList);
		line.draw(paint, canvas);
		//if (true == slideInfo.isbShowText()) {//如果显示支线
			// 画支线
			Vector<Point> nextPointList = getNextLinePoints();
			line = getLine(nextPointList);
			line.draw(paint, canvas);
		//}
	}

	/**
	 * 设置LineItem的属性
	 * 
	 * @param pointList
	 * @return
	 */
	private LineItem getLine(Vector<Point> pointList) {
		LineItem line = new LineItem(pointList); 
		line.setAlpha(255);//线的透明度
		line.setLineColor(slideInfo.getnCalibrationColor());//线的颜色
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
		pointList.add(this.leftTopPoint);
		pointList.add(lastPoint);
		return pointList;
	}

	/**
	 * 画刻度的数值方法
	 */
	private void getText(String text, int x, int y, String direString) {
		Paint p=new Paint();
		p.setAntiAlias(false);//去锯齿
		p.setColor(slideInfo.getnCalibrationColor());//字体颜色
		p.setStyle(Paint.Style.STROKE);//样式
		p.setTextSize(slideInfo.getnTextSize());//字体大小
		p.setStrokeJoin(Join.ROUND);
		if ("left".equals(direString)) {
			p.setTextAlign(Align.RIGHT);
		} else if ("right".equals(direString)) {
			p.setTextAlign(Align.LEFT);
		} else {
			p.setTextAlign(Align.CENTER);
		}
		mpaint.setAlpha(255);//透明度
		mcanvas.drawText(text, x, y, p);

	}

	/**
	 * 画支线
	 */
	private Vector<Point> getNextLinePoints() {
		Vector<Point> pointList = new Vector<Point>();
		// 如果刻度的方向是向上 或者向下
		if (CALIBRATION_DIRECTION.DIRECTION_UP == slideInfo.getDirection()
				|| CALIBRATION_DIRECTION.DIRECTION_DOWN == slideInfo.getDirection()) {
			// 循环主刻度的数目 画线
			// 计算主横坐标的间距
			if (slideInfo.getnMaxNumber() > 1) {
				for (int i = 0; i < slideInfo.getnMaxNumber(); i++) {
					int x1 = (int) (this.leftTopPoint.x + (i * this.mainXSpace));
					int y1 = this.leftTopPoint.y;
					int x2 = x1;
					int y2 = this.mainStopY;
					// 添加主支线
					pointList.add(new Point(x1, y1));
					pointList.add(new Point(x2, y2));
					if (true == slideInfo.isbShowText()) {// 显示刻度的数值
						if(slideInfo.getDirection()==CALIBRATION_DIRECTION.DIRECTION_UP){//上
							this.drawNumberText(x2, y2, i);
						}else if(slideInfo.getDirection()==CALIBRATION_DIRECTION.DIRECTION_DOWN){//下
							this.drawNumberText(x2, y2+slideInfo.getmHeight()/10, i);
						}
						
					}
					if (0 != slideInfo.getnMinNumber() && i < slideInfo.getnMaxNumber() - 1) {
						for (int j = 1; j < slideInfo.getnMinNumber() + 1; j++) {
							int nx1 = (int) (x1 + (j * this.nextXSpace));
							int ny1 = this.leftTopPoint.y;
							int nx2 = nx1;
							int ny2 = this.nextStopY;
							// 添加次支线
							pointList.add(new Point(nx1, ny1));
							pointList.add(new Point(nx2, ny2));
						}
					}
				}
			}
		}
		// 刻度方向左向右
		else if (CALIBRATION_DIRECTION.DIRECTION_LEFT == slideInfo.getDirection()
				|| CALIBRATION_DIRECTION.DIRECTION_RIGHT == slideInfo.getDirection()) {
			// 循环主刻度的数目 画线
			// 计算主横坐标的间距
			if (slideInfo.getnMaxNumber() > 1) {
				for (int i = 0; i < slideInfo.getnMaxNumber(); i++) {
					int x1 = this.leftTopPoint.x;
					int y1 = (int) (this.leftTopPoint.y + (i * this.mainYSpace));
					int x2 = this.mainStopX;
					int y2 = y1;
					pointList.add(new Point(x1, y1));
					pointList.add(new Point(x2, y2));

					if (true == slideInfo.isbShowText()) {// 显示刻度的数值
						if(slideInfo.getDirection()==CALIBRATION_DIRECTION.DIRECTION_LEFT){//左
							this.drawNumberText(x2-2, y2+4, i);
						}else if(slideInfo.getDirection()==CALIBRATION_DIRECTION.DIRECTION_RIGHT){//右
							this.drawNumberText(x2+2, y2+4, i);
						}
					}
					//支线
					if (0 != slideInfo.getnMinNumber()
							&& i < slideInfo.getnMaxNumber() - 1) {
						for (int j = 1; j < slideInfo.getnMinNumber() + 1; j++) {
							int nx1 = this.leftTopPoint.x;
							int ny1 = (int) (y1 + (j * this.nextYSpace));
							int nx2 = this.nextStopX;
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
		if (slideInfo.getnCalibrationMin() < 0 && slideInfo.getnCalibrationMax() < 0) {//如果最大值和最小值为负数
			tempMax = -slideInfo.getnCalibrationMax();
			tempMin = -slideInfo.getnCalibrationMin();
			if (tempMax > tempMin) {//最大值大于最小值
				temp = -((tempMax - tempMin) / (slideInfo.getnMinNumber() - 1));
			} else {
				temp = -((tempMin - tempMax) / (slideInfo.getnMaxNumber() - 1));
			}
		} else if (slideInfo.getnCalibrationMin() < 0 && slideInfo.getnCalibrationMax() > 0) {//最小值<0，最大值>0
			tempMin = -slideInfo.getnCalibrationMin();
			temp = (slideInfo.getnCalibrationMax() + tempMin)/ (slideInfo.getnMaxNumber() - 1);
		} else if (slideInfo.getnCalibrationMin() > 0 && slideInfo.getnCalibrationMax() < 0) {
			tempMax = -slideInfo.getnCalibrationMax();
			temp = (tempMax + slideInfo.getnCalibrationMin())/ (slideInfo.getnMaxNumber() - 1);
		} else {
			if (slideInfo.getnCalibrationMin() < slideInfo.getnCalibrationMax()) {
				temp = (slideInfo.getnCalibrationMax() - slideInfo.getnCalibrationMin())/ (slideInfo.getnMaxNumber() - 1);
			} else {
				temp = (slideInfo.getnCalibrationMin() - slideInfo.getnCalibrationMax())/ (slideInfo.getnMaxNumber() - 1);
			}
		}
		return temp;
	}

	/**
	 * 根据第i个主刻度，得到要显示的数值
	 */
	private double getNumberText(int i) {
		double temp = getTemp();
		double number = 0;
		//如果刻度方向向上，数值向左
		if (CALIBRATION_DIRECTION.DIRECTION_UP == slideInfo.getnPosition()
				|| CALIBRATION_DIRECTION.DIRECTION_LEFT == slideInfo.getnPosition()){ // 数值向上递增
			if (i == 0) {
				number = Math.rint(slideInfo.getnCalibrationMax());
			} else if (i == slideInfo.getnMaxNumber() - 1) {
				number = Math.rint(slideInfo.getnCalibrationMin());
			} else {
				number = slideInfo.getnCalibrationMax() - Math.rint(i * temp);
			}
		} else if (CALIBRATION_DIRECTION.DIRECTION_DOWN == slideInfo.getnPosition()
				|| CALIBRATION_DIRECTION.DIRECTION_RIGHT == slideInfo.getnPosition()) {// 数值向下递增
			if (i == 0) {
				number = Math.rint(slideInfo.getnCalibrationMin());

			} else if (i == slideInfo.getnMaxNumber() - 1) {
				number = Math.rint(slideInfo.getnCalibrationMax());
			} else {
				number = slideInfo.getnCalibrationMin() + Math.rint(i * temp);
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
		
		if (1 == slideInfo.getnDecimalCount()) {
			tempNumber =  (0.1 * number);
			b=new BigDecimal(tempNumber);
			tempNumber= b.setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue();
			format=new DecimalFormat("#.0");
//			if(tempNumber==0.1){
//				tempNumber=0.0;
//			}
			text=format.format(tempNumber);
		}else if (2 == slideInfo.getnDecimalCount()) {
			tempNumber = (0.01 * number);
			b=new BigDecimal(tempNumber);
			tempNumber= b.setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
			format=new DecimalFormat("#.00");
//			if(tempNumber==0.01){
//				tempNumber=0.00;
//			}
			text=format.format(tempNumber);
		}else if (3 == slideInfo.getnDecimalCount()) {
			tempNumber = (0.001 * number);
			b=new BigDecimal(tempNumber);
			tempNumber= b.setScale(3, BigDecimal.ROUND_HALF_DOWN).doubleValue();
			format=new DecimalFormat("#.000");
//			if(tempNumber==0.001){
//				tempNumber=0.000;
//			}
			text=format.format(tempNumber);
		}else if (4 == slideInfo.getnDecimalCount()) {
			tempNumber = (0.0001 * number);
			b=new BigDecimal(tempNumber);
			tempNumber= b.setScale(4, BigDecimal.ROUND_HALF_DOWN).doubleValue();
			format=new DecimalFormat("#.0000");
//			if(tempNumber==0.0001){
//				tempNumber=0.0000;
//			}
			text=format.format(tempNumber);
		}else if (5 == slideInfo.getnDecimalCount()) {
			tempNumber = (0.00001 * number);
			b=new BigDecimal(tempNumber);
			tempNumber= b.setScale(5, BigDecimal.ROUND_HALF_DOWN).doubleValue();
			format=new DecimalFormat("#.00000");
//			if(tempNumber==0.00001){
//				tempNumber=0.00000;
//			}
			text=format.format(tempNumber);
		}else if (6 == slideInfo.getnDecimalCount()) {
			tempNumber = (0.000001 * number);
			b=new BigDecimal(tempNumber);
			tempNumber= b.setScale(6, BigDecimal.ROUND_HALF_DOWN).doubleValue();
			format=new DecimalFormat("#.000000");
//			if(tempNumber==0.000001){
//				tempNumber=0.000000;
//			}
			text=format.format(tempNumber);
		}else if (7 == slideInfo.getnDecimalCount()) {
			tempNumber = (0.0000001 * number);
			b=new BigDecimal(tempNumber);
			tempNumber= b.setScale(7, BigDecimal.ROUND_HALF_DOWN).doubleValue();
			format=new DecimalFormat("#.0000000");
//			if(tempNumber==0.0000001){
//				tempNumber=0.000000;
//			}
			text=format.format(tempNumber);
		}else if (8 == slideInfo.getnDecimalCount()) {
			tempNumber = (0.00000001 * number);
			b=new BigDecimal(tempNumber);
			tempNumber= b.setScale(8, BigDecimal.ROUND_HALF_DOWN).doubleValue();
			format=new DecimalFormat("#.00000000");
//			if(tempNumber==0.00000001){
//				tempNumber=0.00000000;
//			}
			text=format.format(tempNumber);
		}else if (9 == slideInfo.getnDecimalCount()) {
			tempNumber = (0.000000001 * number);
			b=new BigDecimal(tempNumber);
			tempNumber= b.setScale(9, BigDecimal.ROUND_HALF_DOWN).doubleValue();
			format=new DecimalFormat("#.000000000");
//			if(tempNumber==0.000000001){
//				tempNumber=0.000000000;
//			}
			text=format.format(tempNumber);
		}else if (10 == slideInfo.getnDecimalCount()) {
			tempNumber = (0.0000000001 * number);
			b=new BigDecimal(tempNumber);
			tempNumber= b.setScale(10, BigDecimal.ROUND_HALF_DOWN).doubleValue();
			format=new DecimalFormat("#.0000000000");
//			if(tempNumber==0.0000000001){
//				tempNumber=0.0000000000;
//			}
			text=format.format(tempNumber);
		}else{
			tempNumber =  (1 * number);
			b=new BigDecimal(tempNumber);
			format=new DecimalFormat("#");
//			if(tempNumber==1){
//				tempNumber=0;
//			}
			text=format.format(tempNumber);
		}
		b=new BigDecimal(text);
		text=b.toPlainString();
		// 根据不同的刻度显示的位置设置画刻度
		if (CALIBRATION_DIRECTION.DIRECTION_LEFT == slideInfo.getDirection()) {
			getText(text, x2, y2, "left");
		} else if (CALIBRATION_DIRECTION.DIRECTION_DOWN == slideInfo.getDirection()) {
			getText(text, x2, y2 + slideInfo.getnTextSize()-1, "center");
		} else if (CALIBRATION_DIRECTION.DIRECTION_UP == slideInfo.getDirection()){
			getText(text, x2, y2, "center");
		} else {
			getText(text, x2, y2, "right");
		}
	}
}