package com.android.Samkoonhmi.skgraphics.noplc;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Vector;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Join;
import android.graphics.Point;
import android.graphics.Rect;
import com.android.Samkoonhmi.graphicsdrawframe.LineItem;
import com.android.Samkoonhmi.model.CalibrationModel;
import com.android.Samkoonhmi.model.IItem;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
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
public class SKCalibration extends SKGraphicsBase implements IItem {

	private CalibrationModel calibration;
	private Point lastPoint; // 最后一个点的坐标
	private Paint paint;// 画笔
	private Canvas canvas;// 画布
	private Rect totalRect;// 外矩形
	private SKItems skItem;// item
	private int sceneid;
	private int itemId;
	private boolean isShowFlag;
	private boolean showByUser;
	private boolean showByAddr;

	/**
	 * 构造函数
	 */
	public SKCalibration(int sceneid, int itemId, CalibrationModel model) {
		// 初始化值
		super();
		this.itemId = itemId;
		this.sceneid = sceneid;
		totalRect = new Rect();
		paint = new Paint();
		skItem = new SKItems();
		this.calibration = model;

		isShowFlag = true;
		showByAddr = false;
		showByUser = false;
		skItem.nCollidindId = calibration.getnCollidindId();
		skItem.itemId = itemId;
		skItem.nZvalue = calibration.getnZvalue();
		skItem.rect = totalRect;
		skItem.sceneId = sceneid;
		skItem.mGraphics = this;

		if (null != calibration.getShowInfo()) {
			if (-1 != calibration.getShowInfo().getnAddrId()
					&& calibration.getShowInfo().isbShowByAddr()) {
				showByAddr = true;
			}
			if (calibration.getShowInfo().isbShowByUser()) {
				showByUser = true;
			}
		}

		registAddr();
	}

	/**
	 * 计算一些常用坐标
	 */

	private void calculate() {
		totalRect
				.set(calibration.getStartX() - 3,
						calibration.getStartY() - 3,
						calibration.getStartX()
								+ calibration.getCalibrationWidth() + 6,
						calibration.getStartY()
								+ calibration.getCalibrationHeight() + 6);
		calibration.setLeftTopPoint(new Point(calibration.getStartX(),
				calibration.getStartY()));
		// 如果刻度的方向是向上
		if (CALIBRATION_DIRECTION.DIRECTION_UP == calibration
				.getCalibrationDirection()) {
			calibration.setLeftTopPoint(new Point(calibration.getStartX(),
					calibration.getStartY()
							+ calibration.getCalibrationHeight()));
			if (true == calibration.isShowText())
				calibration.setMainLineLength(calibration
						.getCalibrationHeight() / 2);// 主刻度长
			else
				calibration.setMainLineLength(calibration
						.getCalibrationHeight());// 主刻度长
			calibration.setNextLineLength(calibration.getMainLineLength() / 2);// 次刻度的长度=主刻度的长度/2
			calibration.setMainStopX(calibration.getLeftTopPoint().x);// 主支线线的结束点x=左上角x
			calibration.setNextStopX(calibration.getLeftTopPoint().x);// 次支线线的结束点x=左上角x
			calibration.setMainStopY(calibration.getLeftTopPoint().y
					- calibration.getMainLineLength());// 主支线的结束点y=左上角y-主刻度的长度
			calibration.setNextStopY(calibration.getLeftTopPoint().y
					- calibration.getNextLineLength());// 次支线的结束点y=左上角y-次刻度的
			calibration.setMainXSpace((calibration.getCalibrationWidth() + 1.2)
					/ (calibration.getMainNumberCount() - 1));//
			if (0 != calibration.getNextNumberCount()) {
				calibration.setNextXSpace(calibration.getMainXSpace()
						/ (calibration.getNextNumberCount() + 1));
			}
			lastPoint = new Point((calibration.getLeftTopPoint().x
					+ calibration.getCalibrationWidth() + 1),
					calibration.getLeftTopPoint().y);
		}
		// 向下
		else if (CALIBRATION_DIRECTION.DIRECTION_DOWN == calibration
				.getCalibrationDirection()) {
			if (true == calibration.isShowText())
				calibration.setMainLineLength(calibration
						.getCalibrationHeight() / 2);// 主刻度长
			else
				calibration.setMainLineLength(calibration
						.getCalibrationHeight());// 主刻度长

			calibration.setNextLineLength(calibration.getMainLineLength() / 2);// 次刻度的长度=主刻度的长度/2
			calibration.setMainStopX(calibration.getLeftTopPoint().x);
			calibration.setNextStopX(calibration.getLeftTopPoint().x);
			calibration.setMainStopY(calibration.getLeftTopPoint().y
					+ calibration.getMainLineLength());
			calibration.setNextStopY(calibration.getLeftTopPoint().y
					+ calibration.getNextLineLength());
			calibration.setMainXSpace((calibration.getCalibrationWidth() + 1.2)
					/ (calibration.getMainNumberCount() - 1));
			if (0 != calibration.getNextNumberCount()) {
				calibration.setNextXSpace(calibration.getMainXSpace()
						/ (calibration.getNextNumberCount() + 1));
			}
			lastPoint = new Point((calibration.getLeftTopPoint().x
					+ calibration.getCalibrationWidth() + 1),
					calibration.getLeftTopPoint().y);
		}
		// 向左
		else if (CALIBRATION_DIRECTION.DIRECTION_LEFT == calibration
				.getCalibrationDirection()) {
			if (true == calibration.isShowText())
				calibration
						.setMainLineLength(calibration.getCalibrationWidth() / 4);// 主刻度长
			else
				calibration
						.setMainLineLength(calibration.getCalibrationWidth());// 主刻度长
			calibration.setLeftTopPoint(new Point(calibration.getStartX()
					+ calibration.getCalibrationWidth(), calibration
					.getStartY()));

			calibration.setNextLineLength(calibration.getMainLineLength() / 2);// 次刻度的长度=主刻度的长度/2
			calibration.setMainStopX(calibration.getLeftTopPoint().x
					- calibration.getMainLineLength());
			calibration.setNextStopX(calibration.getLeftTopPoint().x
					- calibration.getNextLineLength());
			calibration.setMainStopY(calibration.getLeftTopPoint().y);
			calibration.setNextStopY(calibration.getLeftTopPoint().y);
			calibration
					.setMainYSpace((calibration.getCalibrationHeight() + 1.2)
							/ (calibration.getMainNumberCount() - 1));
			if (0 != calibration.getNextNumberCount()) {
				calibration.setNextYSpace(calibration.getMainYSpace()
						/ (calibration.getNextNumberCount() + 1));
			}
			lastPoint = new Point(calibration.getStartX()
					+ calibration.getCalibrationWidth(),
					calibration.getStartY()
							+ calibration.getCalibrationHeight() + 1);
		} else {// 向右
			if (true == calibration.isShowText())
				calibration
						.setMainLineLength(calibration.getCalibrationWidth() / 4);// 主刻度长
			else
				calibration
						.setMainLineLength(calibration.getCalibrationWidth());// 主刻度长

			calibration.setNextLineLength(calibration.getMainLineLength() / 2);// 次刻度的长度=主刻度的长度/2
			calibration.setMainStopX(calibration.getLeftTopPoint().x
					+ calibration.getMainLineLength());
			calibration.setNextStopX(calibration.getLeftTopPoint().x
					+ calibration.getNextLineLength());
			calibration.setMainStopY(calibration.getLeftTopPoint().y);
			calibration.setNextStopY(calibration.getLeftTopPoint().y);
			calibration
					.setMainYSpace((calibration.getCalibrationHeight() + 1.2)
							/ (calibration.getMainNumberCount() - 1));
			if (0 != calibration.getNextNumberCount()) {
				calibration.setNextYSpace(calibration.getMainYSpace()
						/ (calibration.getNextNumberCount() + 1));
			}
			lastPoint = new Point(calibration.getStartX(),
					calibration.getStartY()
							+ calibration.getCalibrationHeight() + 1);
		}
	}

	@Override
	public void initGraphics() {
		init();
	}

	public void init() {
		if (calibration == null) {
			return;
		}

		calibrationIsShow();
		calculate();

		SKSceneManage.getInstance().onRefresh(skItem);
	}

	@Override
	public boolean drawGraphics(Canvas canvas, int itemId) {
		this.canvas = canvas;
		if (this.itemId == itemId) {
			if (isShowFlag) {
				drawGraphics(canvas);
			}
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
		// if (true == calibration.isShowLateral()) {//如果显示支线
		// 画支线
		Vector<Point> nextPointList = getNextLinePoints();
		line = getLine(nextPointList);
		line.draw(paint, canvas);
		// }
	}

	/**
	 * 设置LineItem的属性
	 * 
	 * @param pointList
	 * @return
	 */
	private LineItem getLine(Vector<Point> pointList) {

		LineItem line = new LineItem(pointList);
		line.setAlpha(255);// 线的透明度
		line.setLineColor(calibration.getLineColor());// 线的颜色
		line.setLineWidth(1);// 线的宽度
		line.setLineType(LINE_TYPE.SOLID_LINE);// 线的类型
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
		Paint p = new Paint();
		p.setAntiAlias(false);// 去锯齿
		p.setColor(calibration.getTextColor());// 字体颜色
		p.setStyle(Paint.Style.STROKE);// 样式
		p.setTextSize(calibration.getTextSize());// 字体大小
		p.setStrokeJoin(Join.ROUND);
		if ("left".equals(direString)) {
			p.setTextAlign(Align.RIGHT);
		} else if ("right".equals(direString)) {
			p.setTextAlign(Align.LEFT);
		} else {
			p.setTextAlign(Align.CENTER);
		}
		paint.setAlpha(255);// 透明度
		canvas.drawText(text, x, y, p);

	}

	/**
	 * 画支线
	 */
	private Vector<Point> getNextLinePoints() {
		Vector<Point> pointList = new Vector<Point>();
		// 如果刻度的方向是向上 或者向下
		if (CALIBRATION_DIRECTION.DIRECTION_UP == calibration
				.getCalibrationDirection()
				|| CALIBRATION_DIRECTION.DIRECTION_DOWN == calibration
						.getCalibrationDirection()) {
			// 循环主刻度的数目 画线
			// 计算主横坐标的间距
			if (calibration.getMainNumberCount() > 1) {
				for (int i = 0; i < calibration.getMainNumberCount(); i++) {
					int x1 = (int) (calibration.getLeftTopPoint().x + (i * calibration
							.getMainXSpace()));
					int y1 = calibration.getLeftTopPoint().y;
					int x2 = x1;
					int y2 = (int) calibration.getMainStopY();
					// 添加主支线
					pointList.add(new Point(x1, y1));
					pointList.add(new Point(x2, y2));
					int temp = 2;
					if (i == 0) {
						temp = 6;
					} else if (i == calibration.getMainNumberCount() - 1) {
						temp = 0;
					}
					if (true == calibration.isShowText()) {// 显示刻度的数值
						if (CALIBRATION_DIRECTION.DIRECTION_UP == calibration
								.getCalibrationDirection()) {// 上
							this.drawNumberText(
									x2 + temp,
									y2 - calibration.getCalibrationHeight() / 5,
									i);
						} else if (CALIBRATION_DIRECTION.DIRECTION_DOWN == calibration
								.getCalibrationDirection()) {// 下
							this.drawNumberText(
									x2 + temp,
									y2 + calibration.getCalibrationHeight() / 5,
									i);
						}
					}
					if (0 != calibration.getNextNumberCount()
							&& i < calibration.getMainNumberCount() - 1) {
						for (int j = 1; j < calibration.getNextNumberCount() + 1; j++) {
							int nx1 = (int) (x1 + (j * calibration
									.getNextXSpace()));
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
		else if (CALIBRATION_DIRECTION.DIRECTION_LEFT == calibration
				.getCalibrationDirection()
				|| CALIBRATION_DIRECTION.DIRECTION_RIGHT == calibration
						.getCalibrationDirection()) {
			// 循环主刻度的数目 画线
			// 计算主横坐标的间距
			if (calibration.getMainNumberCount() > 1) {
				for (int i = 0; i < calibration.getMainNumberCount(); i++) {
					int x1 = calibration.getLeftTopPoint().x;
					int y1 = (int) (calibration.getLeftTopPoint().y + (i * calibration
							.getMainYSpace()));
					int x2 = (int) calibration.getMainStopX();
					int y2 = y1;
					pointList.add(new Point(x1, y1));
					pointList.add(new Point(x2, y2));
					int temp = 5;
					if (i == 0) {
						// 第一个
						temp = 7;
					} else if (i == calibration.getMainNumberCount() - 1) {
						temp = 2;
					}
					if (true == calibration.isShowText()) {// 显示刻度的数值
						if (CALIBRATION_DIRECTION.DIRECTION_LEFT == calibration
								.getCalibrationDirection()) {// 左
							drawNumberText(
									x2 - calibration.getCalibrationWidth() / 5,
									y2 + temp, i);
						} else if (CALIBRATION_DIRECTION.DIRECTION_RIGHT == calibration
								.getCalibrationDirection()) {// 右
							drawNumberText(
									x2 + calibration.getCalibrationWidth() / 5,
									y2 + temp, i);
						}

					}
					// 支线
					if (0 != calibration.getNextNumberCount()
							&& i < calibration.getMainNumberCount() - 1) {
						for (int j = 1; j < calibration.getNextNumberCount() + 1; j++) {
							int nx1 = calibration.getLeftTopPoint().x;
							int ny1 = (int) (y1 + (j * calibration
									.getNextYSpace()));
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
		if (calibration.getMinNumber() < 0 && calibration.getMaxNumber() < 0) {// 如果最大值和最小值为负数
			tempMax = -calibration.getMaxNumber();
			tempMin = -calibration.getMinNumber();
			if (tempMax > tempMin) {// 最大值大于最小值
				temp = -((tempMax - tempMin) / (calibration
						.getMainNumberCount() - 1));
			} else {
				temp = -((tempMin - tempMax) / (calibration
						.getMainNumberCount() - 1));
			}
		} else if (calibration.getMinNumber() < 0
				&& calibration.getMaxNumber() > 0) {// 最小值<0，最大值>0
			tempMin = -calibration.getMinNumber();
			temp = (calibration.getMaxNumber() + tempMin)
					/ (calibration.getMainNumberCount() - 1);
		} else if (calibration.getMinNumber() > 0
				&& calibration.getMaxNumber() < 0) {
			tempMax = -calibration.getMaxNumber();
			temp = (tempMax + calibration.getMinNumber())
					/ (calibration.getMainNumberCount() - 1);
		} else {
			if (calibration.getMinNumber() < calibration.getMaxNumber()) {
				temp = (calibration.getMaxNumber() - calibration.getMinNumber())
						/ (calibration.getMainNumberCount() - 1);
			} else {
				temp = (calibration.getMinNumber() - calibration.getMaxNumber())
						/ (calibration.getMainNumberCount() - 1);
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
		// 如果刻度方向向上，数值向左
		if (CALIBRATION_DIRECTION.DIRECTION_UP == calibration
				.getNumberIncreaseDirection()
				|| CALIBRATION_DIRECTION.DIRECTION_LEFT == calibration
						.getNumberIncreaseDirection()) { // 数值向上递增
			if (i == 0) {
				number = Math.rint(calibration.getMaxNumber());
			} else if (i == calibration.getMainNumberCount() - 1) {
				number = Math.rint(calibration.getMinNumber());
			} else {
				number = calibration.getMaxNumber() - Math.rint(i * temp);
			}
		} else if (CALIBRATION_DIRECTION.DIRECTION_DOWN == calibration
				.getNumberIncreaseDirection()
				|| CALIBRATION_DIRECTION.DIRECTION_RIGHT == calibration
						.getNumberIncreaseDirection()) {// 数值向下递增
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
		DecimalFormat format;// 小数点格式
		// 刻度数值
		double number = getNumberText(i);
		double tempNumber = 0L;
		// 根据小数的位数得到要显示的刻度
		if (1 == calibration.getDecimalCount()) {
			tempNumber = (0.1 * number);
			b = new BigDecimal(tempNumber);
			tempNumber = b.setScale(1, BigDecimal.ROUND_HALF_DOWN)
					.doubleValue();
			format = new DecimalFormat("#.0");
			text = format.format(tempNumber);
		} else if (2 == calibration.getDecimalCount()) {
			tempNumber = (0.01 * number);
			b = new BigDecimal(tempNumber);
			tempNumber = b.setScale(2, BigDecimal.ROUND_HALF_DOWN)
					.doubleValue();
			format = new DecimalFormat("#.00");
			text = format.format(tempNumber);
		} else if (3 == calibration.getDecimalCount()) {
			tempNumber = (0.001 * number);
			b = new BigDecimal(tempNumber);
			tempNumber = b.setScale(3, BigDecimal.ROUND_HALF_DOWN)
					.doubleValue();
			format = new DecimalFormat("#.000");
			text = format.format(tempNumber);
		} else {
			b = new BigDecimal(number);
			text = b.toPlainString();
		}
		b = new BigDecimal(text);
		text = b.toPlainString();
		// 根据不同的刻度显示的位置设置画刻度
		if (CALIBRATION_DIRECTION.DIRECTION_LEFT == calibration
				.getCalibrationDirection()) {
			getText(text, x2, y2, "left");
		} else if (CALIBRATION_DIRECTION.DIRECTION_DOWN == calibration
				.getCalibrationDirection()) {
			getText(text, x2, y2 + calibration.getTextSize() - 1, "center");
		} else if (CALIBRATION_DIRECTION.DIRECTION_UP == calibration
				.getCalibrationDirection()) {
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

	@Override
	public boolean isShow() {
		calibrationIsShow();
		SKSceneManage.getInstance().onRefresh(skItem);
		return isShowFlag;

	}

	private void calibrationIsShow() {
		if (showByAddr || showByUser) {
			isShowFlag = popedomIsShow(calibration.getShowInfo());
		}
	}

	private void registAddr() {
		// 注册显现地址值
		if (showByAddr && null != calibration.getShowInfo().getShowAddrProp()) {
			ADDRTYPE addrType = calibration.getShowInfo().geteAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				SKPlcNoticThread.getInstance().addNoticProp(
						calibration.getShowInfo().getShowAddrProp(), showCall,
						true, sceneid);
			} else {
				SKPlcNoticThread.getInstance().addNoticProp(
						calibration.getShowInfo().getShowAddrProp(), showCall,
						false, sceneid);
			}

		}
	}

	/**
	 * 显现地址值改变通知
	 */
	SKPlcNoticThread.IPlcNoticCallBack showCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			isShow();
		}

	};

	@Override
	/**
	 * 获取控件属性接口
	 */
	public IItem getIItem() {
		return this;
	}

	@Override
	public int getItemLeft(int id) {
		// TODO Auto-generated method stub
		if (calibration != null) {
			return calibration.getStartX();
		}
		return -1;
	}

	@Override
	public int getItemTop(int id) {
		// TODO Auto-generated method stub
		if (calibration != null) {
			return calibration.getStartY();
		}
		return -1;
	}

	@Override
	public int getItemWidth(int id) {
		// TODO Auto-generated method stub
		if (calibration != null) {
			return calibration.getCalibrationWidth();
		}
		return -1;
	}

	@Override
	public int getItemHeight(int id) {
		// TODO Auto-generated method stub
		if (calibration != null) {
			return calibration.getCalibrationHeight();
		}
		return -1;
	}

	@Override
	public short[] getItemForecolor(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public short[] getItemBackcolor(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public short[] getItemLineColor(int id) {
		// TODO Auto-generated method stub
		if (calibration != null) {
			return getColor(calibration.getLineColor());
		}
		return null;
	}

	@Override
	public boolean getItemVisible(int id) {
		// TODO Auto-generated method stub
		return isShowFlag;
	}

	@Override
	public boolean getItemTouchable(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setItemLeft(int id, int x) {
		// TODO Auto-generated method stub
		if (calibration != null) {
			if (x == calibration.getStartX()) {
				return true;
			}
			if (x < 0
					|| x > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneWidth()) {
				return false;
			}
			calibration.setStartX(x);
			int l = skItem.rect.left;
			skItem.rect.left = x;
			skItem.rect.right = x - l + skItem.rect.right;
			skItem.mMoveRect = new Rect();
			SKSceneManage.getInstance().onRefresh(skItem);
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean setItemTop(int id, int y) {
		// TODO Auto-generated method stub
		if (calibration != null) {
			if (y == calibration.getStartY()) {
				return true;
			}
			if (y < 0
					|| y > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneHeight()) {
				return false;
			}
			calibration.setStartY(y);
			int t = skItem.rect.top;
			skItem.rect.top = y;
			skItem.rect.bottom = y - t + skItem.rect.bottom;
			skItem.mMoveRect = new Rect();
			skItem.mMoveRect = new Rect();
			SKSceneManage.getInstance().onRefresh(skItem);
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean setItemWidth(int id, int w) {
		// TODO Auto-generated method stub
		if (calibration != null) {
			if (w == calibration.getCalibrationWidth()) {
				return true;
			}
			if (w < 0
					|| w > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneWidth()) {
				return false;
			}
			calibration.setCalibrationWidth(w);
			skItem.rect.right = w - skItem.rect.width() + skItem.rect.right;
			skItem.mMoveRect = new Rect();
			SKSceneManage.getInstance().onRefresh(skItem);
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean setItemHeight(int id, int h) {
		// TODO Auto-generated method stub
		if (calibration != null) {
			if (h == calibration.getCalibrationHeight()) {
				return true;
			}
			if (h < 0
					|| h > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneHeight()) {
				return false;
			}
			calibration.setCalibrationHeight(h);
			skItem.rect.bottom = h - skItem.rect.height() + skItem.rect.bottom;
			skItem.mMoveRect = new Rect();
			SKSceneManage.getInstance().onRefresh(skItem);
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean setItemForecolor(int id, short r, short g, short b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setItemBackcolor(int id, short r, short g, short b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setItemLineColor(int id, short r, short g, short b) {
		// TODO Auto-generated method stub
		if (calibration == null) {
			return false;
		}

		int color = Color.rgb(r, g, b);

		if (color == calibration.getLineColor()) {
			return true;
		}
		calibration.setLineColor(color);
		SKSceneManage.getInstance().onRefresh(skItem);
		return true;
	}

	@Override
	public boolean setItemVisible(int id, boolean v) {
		// TODO Auto-generated method stub
		if (v == isShowFlag) {
			return true;
		}
		isShowFlag = v;
		SKSceneManage.getInstance().onRefresh(skItem);
		return true;
	}

	@Override
	public boolean setItemTouchable(int id, boolean v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setItemPageUp(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setItemPageDown(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setItemFlick(int id, boolean v, int time) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setItemHroll(int id, int w) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setItemVroll(int id, int h) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setGifRun(int id, boolean v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setItemText(int id, int lid, String text) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setItemAlpha(int id, int alpha) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setItemStyle(int id, int style) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 颜色取反
	 */
	private short[] getColor(int color) {
		short[] c = new short[3];
		c[0] = (short) ((color >> 16) & 0xFF); // RED
		c[1] = (short) ((color >> 8) & 0xFF);// GREEN
		c[2] = (short) (color & 0xFF);// BLUE
		return c;

	}
}