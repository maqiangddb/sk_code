//import SKGraphCmnTouch;
package com.android.Samkoonhmi.skgraphics.plc.touchshow;

import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.SKThread;
import com.android.Samkoonhmi.databaseinterface.AddrPropBiz;
import com.android.Samkoonhmi.databaseinterface.DBTool;
import com.android.Samkoonhmi.databaseinterface.SlideBiz;
import com.android.Samkoonhmi.databaseinterface.TouchShowInfoBiz;
import com.android.Samkoonhmi.graphicsdrawframe.CalibrationItem;
import com.android.Samkoonhmi.graphicsdrawframe.LineItem;
import com.android.Samkoonhmi.graphicsdrawframe.PolygonItem;
import com.android.Samkoonhmi.graphicsdrawframe.RectItem;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.ShowInfo;
import com.android.Samkoonhmi.model.SliderModel;
import com.android.Samkoonhmi.model.TouchInfo;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skenum.CALIBRATION_DIRECTION;
import com.android.Samkoonhmi.skenum.CSS_TYPE;
import com.android.Samkoonhmi.skenum.READ_WRITE_COM_TYPE;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.base.SKGraphCmnTouch;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.ImageFileTool;
import com.android.Samkoonhmi.util.MODULE;
import com.android.Samkoonhmi.util.SEND_DATA_STRUCT;
import com.android.Samkoonhmi.util.TASK;

public class SKSlide2 extends SKGraphCmnTouch {

	private SliderModel sliderModel;// 滑动块实体类
	private CalibrationItem cItem;
	private float barStartX;// 滑轨的起始点x
	private float barStartY;// 滑轨的起始点y
	private float barStopX;// 滑轨的终点x
	private float barStopY;// 滑轨的终点y
	private Rect slideRect;// 滑动条所在的矩形
	private Rect totalRect;// 外面最大的矩形
	Vector<Point> barPointList; // 滑轨的两个端点集合
	private Point barPointStart;// 滑轨的起点
	private Point barPointStop;// 滑轨的终点
	private Vector<Point> fingerPointList;// 画指针的多边形的点集合
	// 指标五个角
	private Point fingerP1;
	private Point fingerP2;
	private Point fingerP3;
	private Point fingerP4;
	private Point fingerP5;
	private int pointWidth; // 指针的宽度
	private int pointHeight; // 指针的高度
	// 计算刻度数值的属性
	private float nLength = 0;// 刻度范围值
	private float xLength = 0;// 刻度所占X轴的长度
	private float everyXLength = 0;// 每个X所占的长度
	private float moveXLength = 0;// 移动的X长度
	private int totalLength = 0;// 最终长度
	private float getX;// 得到X坐标
	private Paint mpaint;
	private SKItems item;
	private int sceneid;
	private int itemId;
	private String sTaskName;
	private boolean flag;
	// private AddrProp WriteAddrID;// 写入地址
	// private AddrProp MinAddrID;// 动态最小值地址
	// private AddrProp MaxAddrID;// 动态最大值地址
	private int mStartX; // 控件的起点X
	private int mStartY; // 控件的起点Y
	private int mWidth;// 控件的宽度
	private int mHeight;// 控件的高度
	private Rect pointRect; // 指针的矩形
	private Rect pPointRect;// 指针放大之后的矩形
	private Bitmap mybgBitmap = null; // 滑轨矩形的图片
	private Bitmap mypointBitmap = null;// 滑动指针的图片
	private Bitmap scaleBitmap = null; // 刻度的图片
	private ICallBack callback;
	private int barLength;
	private double writeToAddress;
	private double calibrationArea; // 刻度范围
	private double sliderArea; // 实际显示范围
	// private TouchInfo touchInfo;// 触控属性
	// private ShowInfo showInfo;// 显现属性
	private boolean isTouchFlag;
	private boolean isShowFlag;
	private boolean touchByUser;
	private boolean showByUser;
	private boolean showByAddr;
	private boolean touchByAddr;
	private double maxTrend;
	private double minTrend;
	private int indent = 0; // 滑轨和刻度的缩进值
	private boolean addrBoo;// 地址值已经通知的标志
	private boolean minBoo; // 最小值已经通知的标志
	private boolean maxBoo;// 最大值已经通知的标志
	private Bitmap mLockBitmap;

	public interface ICallBack {
		int move(int x, int y); //
	};

	public ICallBack getCallback() {
		return callback;
	}

	public void setCallback(ICallBack callback) {
		this.callback = callback;
	}

	public SKSlide2(int sceneid, int itemId, SliderModel model) {
		// new 一些要用到的对象
		super();
		addrBoo = false;
		minBoo = false;
		maxBoo = false;
		isTouchFlag = true;
		isShowFlag = true;
		touchByUser = false;
		showByUser = false;
		showByAddr = false;
		touchByAddr = false;
		sTaskName = "";
		barLength = 0;
		maxTrend = 0;
		minTrend = 0;
		writeToAddress = 0;
		this.itemId = itemId;
		this.sceneid = sceneid;
		totalRect = new Rect();
		fingerPointList = new Vector<Point>();
		pointRect = new Rect();
		pPointRect = new Rect();
		fingerP1 = new Point();
		fingerP2 = new Point();
		fingerP3 = new Point();
		fingerP4 = new Point();
		fingerP5 = new Point();
		mpaint = new Paint();
		flag = true;
		this.sliderModel = model;
		
		if (sliderModel!=null) {
			totalRect.set(sliderModel.getStartX(), sliderModel.getStartY(),
					sliderModel.getStartX() + sliderModel.getmWidth(),
					sliderModel.getStartY() + sliderModel.getmHeight());
			item = new SKItems();
			item.nCollidindId = sliderModel.getnCollidindId();
			item.nZvalue = sliderModel.getnZvalue();
			item.rect = totalRect;
			item.itemId = itemId;
			item.sceneId = sceneid;
			item.mGraphics=this;
		}
	}

	public void init() {

		if (null == sliderModel) {
			return;
		}
		flag = true;
		// 初始化控件的起点 和宽度
		mStartX = sliderModel.getStartX();
		mStartY = sliderModel.getStartY();
		mWidth = sliderModel.getmWidth();
		mHeight = sliderModel.getmHeight();

		initValue();

		if (null != sliderModel.getTouchInfo()) {
			if (null != sliderModel.getTouchInfo().getTouchAddrProp()) {
				touchByAddr = true;
			}
			if (sliderModel.getTouchInfo().isbTouchByUser()) {
				touchByUser = true;
			}
		}
		if (null != sliderModel.getShowInfo()) {
			if (null != sliderModel.getShowInfo().getShowAddrProp()) {
				showByAddr = true;
			}
			if (sliderModel.getShowInfo().isbShowByUser()) {
				showByUser = true;
			}
		}
		calibrationArea = sliderModel.getnCalibrationMax()
				- sliderModel.getnCalibrationMin();
		if (!sliderModel.isTrend()) {
			maxTrend = sliderModel.getMaxTrend();
			minTrend = sliderModel.getMinTrend();
		}
		// 注册地址
		registAddr();
		// 初始化触控跟显现标志
		sliderIsShow();
		sliderIsTouch();
		
		SKSceneManage.getInstance().onRefresh(item);
	}

	private void initValue() {
		// 判断是否显示刻度,决定滑块矩形的高度
		if (null == slideRect) {
			slideRect = new Rect();
			if (sliderModel.isShowCalibration()) {
				setSlideRect(true);
			} else {
				setSlideRect(false);
			}
		}
		// 设置指针
		setPoint();

	}

	/**
	 * 注册地址
	 */
	private void registAddr() {

		// 注册触控通知
		if (touchByAddr) {
			ADDRTYPE addrType = sliderModel.getTouchInfo().geteCtlAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				SKPlcNoticThread.getInstance().addNoticProp(
						sliderModel.getTouchInfo().getTouchAddrProp(),
						touchCall, true);
			} else {
				SKPlcNoticThread.getInstance().addNoticProp(
						sliderModel.getTouchInfo().getTouchAddrProp(),
						touchCall, false);
			}
		}
		// 注册显现通知
		if (showByAddr) {
			ADDRTYPE addrType = sliderModel.getShowInfo().geteAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				SKPlcNoticThread.getInstance().addNoticProp(
						sliderModel.getShowInfo().getShowAddrProp(), showCall,
						true);
			} else {
				SKPlcNoticThread.getInstance().addNoticProp(
						sliderModel.getShowInfo().getShowAddrProp(), showCall,
						false);
			}

		}
		// 写入地址
		// Log.d("slider",
		// "------- 注册滑动块地址值 Id="+sliderModel.getId()+"--WriteAddrID="+sliderModel.getWriteKeyAdd());
		if (null != sliderModel.getWriteKeyAdd()) {
			// Log.d("slider", "------- 注册滑动块地址值 Id="+sliderModel.getId());
			SKPlcNoticThread.getInstance().addNoticProp(
					sliderModel.getWriteKeyAdd(), wirteCall, false); // 动态流动速度地址
		}
		// 动态最小值地址
		if (null != sliderModel.getMinTrendAdd()
				&& sliderModel.isTrend() == true) {
			SKPlcNoticThread.getInstance().addNoticProp(
					sliderModel.getMinTrendAdd(), trendMinCall, false);//
		} else {
			minBoo = true;
		}
		// 动态最大值地址
		if (null != sliderModel.getMaxTrendAdd()
				&& sliderModel.isTrend() == true) {
			SKPlcNoticThread.getInstance().addNoticProp(
					sliderModel.getMaxTrendAdd(), trendMaxCall, false);//
		} else {
			maxBoo = true;
		}

	}

	/**
	 * 触控地址值改变通知
	 */
	SKPlcNoticThread.IPlcNoticCallBack touchCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			isTouchFlag = isTouch();
		}

	};
	/**
	 * 显现地址值改变通知
	 */
	SKPlcNoticThread.IPlcNoticCallBack showCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			isShowFlag = isShow();

		}

	};

	/**
	 * 设置滑轨矩形里面的配置信息
	 */
	private void setSlideRect(boolean isShowCalibration) {
		// 显示刻度
		if (isShowCalibration) {

			if (CALIBRATION_DIRECTION.DIRECTION_DOWN == sliderModel
					.getDirection()) {
				// 向下
				// 设置滑轨矩形
				slideRect.left = mStartX;
				slideRect.top = mStartY;
				slideRect.right = mStartX + mWidth;
				slideRect.bottom = mStartY + mHeight / 2;
			} else if (CALIBRATION_DIRECTION.DIRECTION_UP == sliderModel
					.getDirection()) {
				// 向上
				// 设置滑轨矩形
				slideRect.left = mStartX;
				slideRect.top = mStartY + mHeight / 2;
				slideRect.right = mStartX + mWidth;
				slideRect.bottom = mStartY + mHeight;

			} else if (CALIBRATION_DIRECTION.DIRECTION_RIGHT == sliderModel
					.getDirection()) {
				// 向右
				// 设置滑轨的矩形
				slideRect.left = mStartX;
				slideRect.top = mStartY;
				slideRect.right = mStartX + mWidth / 2;
				slideRect.bottom = mStartY + mHeight;
			} else if (CALIBRATION_DIRECTION.DIRECTION_LEFT == sliderModel
					.getDirection()) {
				// 向左
				// 设置滑轨的矩形
				slideRect.left = mStartX + mWidth / 2;
				slideRect.top = mStartY;
				slideRect.right = mStartX + mWidth;
				slideRect.bottom = mStartY + mHeight;

			}
		} else {
			slideRect.left = mStartX;
			slideRect.top = mStartY;
			slideRect.right = mStartX + mWidth;
			slideRect.bottom = mStartY + mHeight;
		}
		// 设置滑轨线条
		if (CALIBRATION_DIRECTION.DIRECTION_LEFT == sliderModel.getDirection()
				|| CALIBRATION_DIRECTION.DIRECTION_RIGHT == sliderModel
						.getDirection()) {
			setLineLR();
		} else {
			setLineUD();
		}

	}

	/**
	 * 向左向右 滑轨的线条
	 */
	private void setLineLR() {

		// 设置滑轨的线条
		if (null != slideRect) {
			barStartX = slideRect.centerX();
			indent = sliderModel.getmHeight() / 16;
			barStartY = slideRect.top + indent;
			barStopX = barStartX;
			barStopY = slideRect.bottom - indent;
		}
	}

	/**
	 * 向上 向下 滑轨的线条
	 */
	private void setLineUD() {
		if (null != slideRect) {
			indent = sliderModel.getmWidth() / 20;
			barStartX = slideRect.left + indent;
			barStartY = slideRect.centerY();
			barStopX = slideRect.right - indent;
			barStopY = barStartY;
		}
	}

	/**
	 * 计算指针的宽度和高度
	 */
	private void setPointWH() {

		if (CALIBRATION_DIRECTION.DIRECTION_DOWN == sliderModel.getDirection()
				|| CALIBRATION_DIRECTION.DIRECTION_UP == sliderModel
						.getDirection()) {
			pointWidth = (int) ((float)slideRect.width() / 25);
			pointHeight = (int)((float)slideRect.height() / 4);

			barLength = (int) (barStopX - barStartX);
		} else {
			pointHeight = (int) ((float)slideRect.height() / 15);
			pointWidth = (int) ((float)slideRect.width() / 4);
			barLength = (int) (barStopY - barStartY);
		}
	}

	/**
	 * 计算指针的五个点
	 */
	private void calculatePoint() {
		// 滑块的方向向下 即指针的方向向下
		if (CALIBRATION_DIRECTION.DIRECTION_DOWN == sliderModel.getDirection()) {
			// 指针靠左 数值往右增大
			if (CALIBRATION_DIRECTION.DIRECTION_RIGHT == sliderModel
					.getnPosition()) {
				fingerP1 = new Point((int) (barStartX - pointWidth / 2),
						(int) (barStartY - pointHeight));
				fingerP2 = new Point((int) (barStartX + pointWidth / 2),
						(int) (barStartY - pointHeight));
				fingerP3 = new Point((int) (barStartX + pointWidth / 2),
						(int) (barStartY + pointHeight));
				fingerP4 = new Point((int) barStartX, (int) (barStartY
						+ pointHeight + pointHeight / 2));
				fingerP5 = new Point((int) (barStartX - pointWidth / 2),
						(int) (barStartY + pointHeight));
				pointRect.left = fingerP1.x;
				pointRect.top = fingerP1.y;
				pointRect.right = fingerP2.x;
				pointRect.bottom = fingerP4.y;
				sliderModel.setPointStart(new Point(fingerP1.x, fingerP1.y));
			}
			// 指针靠右 数值往左增大
			else if (CALIBRATION_DIRECTION.DIRECTION_LEFT == sliderModel
					.getnPosition()) {
				fingerP1 = new Point((int) (barStopX + pointWidth / 2),
						(int) (barStopY - pointHeight));
				fingerP2 = new Point((int) (barStopX - pointWidth / 2),
						(int) (barStopY - pointHeight));
				fingerP3 = new Point((int) (barStopX - pointWidth / 2),
						(int) (barStopY + pointHeight));
				fingerP4 = new Point((int) (barStopX), (int) (barStopY
						+ pointHeight + pointHeight / 2));
				fingerP5 = new Point((int) (barStopX + pointWidth / 2),
						(int) (barStopY + pointHeight));
				pointRect.left = fingerP2.x;
				pointRect.top = fingerP2.y;
				pointRect.right = fingerP1.x;
				pointRect.bottom = fingerP4.y;
				sliderModel.setPointStart(new Point(fingerP2.x, fingerP2.y));
			}
		}
		// 滑块的方向向上 即指针的方向向上
		if (CALIBRATION_DIRECTION.DIRECTION_UP == sliderModel.getDirection()) {
			// 指针靠左 数值往右增大
			if (CALIBRATION_DIRECTION.DIRECTION_RIGHT == sliderModel
					.getnPosition()) {
				fingerP1 = new Point((int) (barStartX - pointWidth / 2),
						(int) (barStartY + pointHeight));
				fingerP2 = new Point((int) (barStartX + pointWidth / 2),
						(int) (barStartY + pointHeight));
				fingerP3 = new Point((int) (barStartX + pointWidth / 2),
						(int) (barStartY - pointHeight));
				fingerP4 = new Point((int) (barStartX), (int) (barStartY
						- pointHeight - pointHeight / 2));
				fingerP5 = new Point((int) (barStartX - pointWidth / 2),
						(int) (barStartY - pointHeight));
				pointRect.left = fingerP1.x;
				pointRect.top = fingerP4.y;
				pointRect.right = fingerP2.x;
				pointRect.bottom = fingerP2.y;
				sliderModel.setPointStart(new Point(fingerP5.x, fingerP4.y));
			}
			// 指针靠右 数值往左增大
			else if (CALIBRATION_DIRECTION.DIRECTION_LEFT == sliderModel
					.getnPosition()) {
				fingerP1 = new Point((int) (barStopX + pointWidth / 2),
						(int) (barStopY + pointHeight));
				fingerP2 = new Point((int) (barStopX - pointWidth / 2),
						(int) (barStopY + pointHeight));
				fingerP3 = new Point((int) (barStopX - pointWidth / 2),
						(int) (barStopY - pointHeight));
				fingerP4 = new Point((int) (barStopX), (int) (barStopY
						- pointHeight - pointHeight / 2));
				fingerP5 = new Point((int) (barStopX + pointWidth / 2),
						(int) (barStopY - pointHeight));
				pointRect.left = fingerP2.x;
				pointRect.top = fingerP4.y;
				pointRect.right = fingerP1.x;
				pointRect.bottom = fingerP1.y;
				sliderModel.setPointStart(new Point(fingerP3.x, fingerP4.y));
			}
		}
		// 滑块的方向向左 即指针的方向向左
		if (CALIBRATION_DIRECTION.DIRECTION_LEFT == sliderModel.getDirection()) {
			// 指针靠上 数值往下增大
			if (CALIBRATION_DIRECTION.DIRECTION_DOWN == sliderModel
					.getnPosition()) {
				fingerP1 = new Point((int) (barStartX + pointWidth),
						(int) (barStartY - pointHeight / 2));
				fingerP2 = new Point((int) (barStartX + pointWidth),
						(int) (barStartY + pointHeight / 2));
				fingerP3 = new Point((int) (barStartX - pointWidth),
						(int) (barStartY + pointHeight / 2));
				fingerP4 = new Point(
						(int) (barStartX - pointWidth - pointWidth / 2),
						(int) (barStartY));
				fingerP5 = new Point((int) (barStartX - pointWidth),
						(int) (barStartY - pointHeight / 2));
				pointRect.left = fingerP4.x;
				pointRect.top = fingerP1.y;
				pointRect.right = fingerP1.x;
				pointRect.bottom = fingerP2.y;
				sliderModel.setPointStart(new Point(fingerP4.x, fingerP5.y));
			}
			// 指针靠下
			else if (CALIBRATION_DIRECTION.DIRECTION_UP == sliderModel
					.getnPosition()) {
				fingerP1 = new Point((int) (barStopX + pointWidth),
						(int) (barStopY + pointHeight / 2));
				fingerP2 = new Point((int) (barStopX + pointWidth),
						(int) (barStopY - pointHeight / 2));
				fingerP3 = new Point((int) (barStopX - pointWidth),
						(int) (barStopY - pointHeight / 2));
				fingerP4 = new Point(
						(int) (barStopX - pointWidth - pointWidth / 2),
						(int) (barStopY));
				fingerP5 = new Point((int) (barStopX - pointWidth),
						(int) (barStopY + pointHeight / 2));

				pointRect.left = fingerP4.x;
				pointRect.top = fingerP2.y;
				pointRect.right = fingerP2.x;
				pointRect.bottom = fingerP1.y;
				sliderModel.setPointStart(new Point(fingerP4.x, fingerP3.y));
			}
		}
		// 滑块的方向向右 即指针的方向向右
		if (CALIBRATION_DIRECTION.DIRECTION_RIGHT == sliderModel.getDirection()) {
			// 指针靠上
			if (CALIBRATION_DIRECTION.DIRECTION_DOWN == sliderModel
					.getnPosition()) {
				fingerP1 = new Point((int) (barStartX - pointWidth),
						(int) (barStartY - pointHeight / 2));
				fingerP2 = new Point((int) (barStartX - pointWidth),
						(int) (barStartY + pointHeight / 2));
				fingerP3 = new Point((int) (barStartX + pointWidth),
						(int) (barStartY + pointHeight / 2));
				fingerP4 = new Point(
						(int) (barStartX + pointWidth + pointWidth / 2),
						(int) (barStartY));
				fingerP5 = new Point((int) (barStartX + pointWidth),
						(int) barStartY - pointHeight / 2);
				pointRect.left = fingerP1.x;
				pointRect.top = fingerP1.y;
				pointRect.right = fingerP4.x;
				pointRect.bottom = fingerP2.y;
				sliderModel.setPointStart(new Point(fingerP1.x, fingerP1.y));
			}
			// 指针靠下
			else if (CALIBRATION_DIRECTION.DIRECTION_UP == sliderModel
					.getnPosition()) {
				fingerP1 = new Point((int) (barStopX - pointWidth),
						(int) (barStopY - pointHeight / 2));
				fingerP2 = new Point((int) (barStopX - pointWidth),
						(int) (barStopY + pointHeight / 2));
				fingerP3 = new Point((int) (barStopX + pointWidth),
						(int) (barStopY + pointHeight / 2));
				fingerP4 = new Point(
						(int) (barStopX + pointWidth + pointWidth / 2),
						(int) (barStopY));
				fingerP5 = new Point((int) (barStopX + pointWidth),
						(int) (barStopY - pointHeight / 2));
				pointRect.left = fingerP1.x;
				pointRect.top = fingerP1.y;
				pointRect.right = fingerP4.x;
				pointRect.bottom = fingerP2.y;
				sliderModel.setPointStart(new Point(fingerP1.x, fingerP1.y));
			}
		}

	}

	/**
	 * 设置指针的点
	 */
	private void setPoint() {
		// 长度=刻度最大值-刻度最小值
		// nLength = (int) (sliderModel.getnCalibrationMax() - sliderModel
		// .getnCalibrationMin());
		nLength = (float) (sliderModel.getMaxTrend() - sliderModel
				.getMinTrend());
		if (nLength < 0) {
			nLength = Math.abs(nLength);
		}
		// 设置指针的宽度和高度
		setPointWH();
		// 计算指针的五个点
		calculatePoint();
	}

	/**
	 * 画滑动块的矩形背景框图片
	 * 
	 * @param width
	 * @param paint
	 * @return
	 */
	private Bitmap getBgBitmap(int width, int height, Paint paint) {
		if (width < 0 || height < 0 || width == 0 || height == 0) {
			return null;
		} else {
			Bitmap bgBitmap = Bitmap.createBitmap(width, height,
					Config.ARGB_8888);
			Canvas bCanvas = new Canvas(bgBitmap);
			paint.setAntiAlias(true);
			// 画矩形
			RectItem rectItem = new RectItem(new Rect(0, 0, slideRect.width(),
					slideRect.height()));
			rectItem.setBackColor(sliderModel.getRectColor());
			rectItem.setStyle(CSS_TYPE.CSS_SOLIDCOLOR);
			rectItem.draw(paint, bCanvas);

			// 画滑动滑轨线条
			if (null == barPointStart) {
				barPointStart = new Point();
				barPointStart.x = (int) barStartX - slideRect.left;
				barPointStart.y = (int) barStartY - slideRect.top;
			}
			if (null == barPointStop) {
				barPointStop = new Point();
				barPointStop.x = (int) barStopX - slideRect.left;
				barPointStop.y = (int) barStopY - slideRect.top;
			}
			if (null == barPointList) {
				barPointList = new Vector<Point>();
				barPointList.add(barPointStart);
				barPointList.add(barPointStop);
			}
			LineItem lineItem = new LineItem(barPointList);
			lineItem.setLineColor(sliderModel.getSlideBarColor());
			lineItem.draw(paint, bCanvas);
			return bgBitmap;
		}

	}

	/**
	 * 将指针画到一张图片上 每次移动 只要移动图片
	 * 
	 * @param paint
	 * @return
	 */
	private Bitmap getPointBitmap(Paint paint) {
		if (pointRect == null || pointRect.width() == 0
				|| pointRect.height() == 0) {
			return null;
		} else {
			Bitmap pointBitmap = Bitmap.createBitmap(pointRect.width(),
					pointRect.height(), Config.ARGB_8888);
			Canvas bCanvas = new Canvas(pointBitmap);
			paint.setAntiAlias(true);
			// 画指针
			int pointRectLeft = pointRect.left;
			int pointRectTop = pointRect.top;
			fingerPointList.add(new Point(fingerP1.x - pointRectLeft,
					fingerP1.y - pointRectTop));
			fingerPointList.add(new Point(fingerP2.x - pointRectLeft,
					fingerP2.y - pointRectTop));
			fingerPointList.add(new Point(fingerP3.x - pointRectLeft,
					fingerP3.y - pointRectTop));
			fingerPointList.add(new Point(fingerP4.x - pointRectLeft,
					fingerP4.y - pointRectTop));
			fingerPointList.add(new Point(fingerP5.x - pointRectLeft,
					fingerP5.y - pointRectTop));
			PolygonItem polyItem = new PolygonItem(fingerPointList);
			polyItem.setStyle(CSS_TYPE.CSS_SOLIDCOLOR);
			polyItem.setBackColor(sliderModel.getFingerBackColor());
			polyItem.setLineWidth(1);
			polyItem.setLineColor(sliderModel.getFingerLineColor());
			polyItem.draw(paint, bCanvas);
			return pointBitmap;
		}

	}

	/**
	 * 画刻度图片
	 * 
	 * @param paint
	 * @return
	 */
	public Bitmap getScaleBitmap(int width, int height, Paint paint) {
		if (width < 0 || height < 0 || height == 0 || width == 0) {
			return null;
		} else {
			// 创建图片的大小
			Bitmap scaleBitmaps = Bitmap.createBitmap(width, height,
					Config.ARGB_8888);
			// 将图片做成一个画布
			Canvas canvasa = new Canvas(scaleBitmaps);
			CalibrationItem cItems = null;
			if (CALIBRATION_DIRECTION.DIRECTION_LEFT == sliderModel
					.getDirection()) {// 向左
				cItems = new CalibrationItem(sliderModel, 0, (int) indent, 0,
						(int) barLength + indent);
				cItems.drawGraphics(paint, canvasa);
			} else if (CALIBRATION_DIRECTION.DIRECTION_RIGHT == sliderModel // 向右
					.getDirection()) {
				cItems = new CalibrationItem(sliderModel, 0, (int) indent, 0,
						(int) barLength + indent);
				cItems.drawGraphics(paint, canvasa);
			} else if (CALIBRATION_DIRECTION.DIRECTION_DOWN == sliderModel // 向下
					.getDirection()) {
				cItems = new CalibrationItem(sliderModel, (int) indent,
						(int) 0, (int) barLength + indent, 0);
				cItems.drawGraphics(paint, canvasa);
			} else { // 向上
				cItems = new CalibrationItem(sliderModel, (int) indent,
						(int) 0, (int) barLength + indent, 0);
				cItems.drawGraphics(paint, canvasa);
			}
			return scaleBitmaps;
		}

	}

	@Override
	public boolean drawGraphics(Canvas canvas, int itemId) {
		if (null == sliderModel) {
			return false;
		}
		if (this.itemId == itemId) {
			if (isShowFlag) {
				drawGraphics(canvas);
			}
			flag = true;
			return true;
		} else {
			return false;
		}
	}

	// 画画
	public void drawGraphics(Canvas mcanvas) {
		draw(mpaint, mcanvas);
	}

	public void draw(Paint paint, Canvas canvas) {

		// 画刻度
		if (sliderModel.isShowCalibration()) {
			// 获取刻度图片
			if (null == scaleBitmap) {
				scaleBitmap = getScaleBitmap(slideRect.width(),
						slideRect.height(), paint);
			}
			// 将刻度图片画到总的画布上
			// 方向不同，画布起点的位置不同\
			if (null != scaleBitmap) {
				if (CALIBRATION_DIRECTION.DIRECTION_LEFT == sliderModel
						.getDirection()) {
					canvas.drawBitmap(scaleBitmap,
							slideRect.left - slideRect.width(), slideRect.top,
							paint);
				} else if (CALIBRATION_DIRECTION.DIRECTION_RIGHT == sliderModel
						.getDirection()) {
					canvas.drawBitmap(scaleBitmap, slideRect.right,
							slideRect.top, paint);
				} else if (CALIBRATION_DIRECTION.DIRECTION_DOWN == sliderModel
						.getDirection()) {
					canvas.drawBitmap(scaleBitmap, slideRect.left,
							slideRect.bottom, paint);

				} else {
					canvas.drawBitmap(scaleBitmap, slideRect.left,
							slideRect.top - slideRect.height(), paint);
				}
			}
		}
		if (null == mybgBitmap) {
			mybgBitmap = getBgBitmap(slideRect.width(), slideRect.height(),
					paint);
		}
		// 将滑动块矩形背景图片画到总的画布上
		if (null != mybgBitmap) {
			canvas.drawBitmap(mybgBitmap, slideRect.left, slideRect.top, paint);
		}
		// 将指针的图片画到总画布上
		if (null == mypointBitmap) {
			mypointBitmap = getPointBitmap(paint);
		}
		if (null == sliderModel.getPointStart()) {
			calculatePoint();
		}
		if (touchDown) {
			if (CALIBRATION_DIRECTION.DIRECTION_DOWN == sliderModel
					.getDirection()
					|| CALIBRATION_DIRECTION.DIRECTION_UP == sliderModel
							.getDirection()) {
				pPointRect.left = sliderModel.getPointStart().x - 2;
				pPointRect.top = pointRect.top;
				pPointRect.right = sliderModel.getPointStart().x
						+ pointRect.width() + 2;
				pPointRect.bottom = pointRect.bottom;
				if (null != mypointBitmap) {
					canvas.drawBitmap(mypointBitmap, null, pPointRect, paint);
				}

			} else {
				pPointRect.left = pointRect.left;
				pPointRect.top = sliderModel.getPointStart().y - 2;
				pPointRect.right = pointRect.right;
				pPointRect.bottom = sliderModel.getPointStart().y + 2
						+ pointRect.height();
				if (null != mypointBitmap) {
					canvas.drawBitmap(mypointBitmap, null, pPointRect, paint);
				}
			}
		} else {
			if (null != mypointBitmap) {
				canvas.drawBitmap(mypointBitmap, sliderModel.getPointStart().x,
						sliderModel.getPointStart().y, paint);
			}
		}

		// 不可触控加上锁图标
		if (!isTouchFlag) {
			if (mLockBitmap == null) {
				if(SKSceneManage.getInstance().mContext!=null)
				{
					mLockBitmap = ImageFileTool
							.getBitmap(R.drawable.lock, SKSceneManage.getInstance().mContext);
				}
				
			}
			if (mLockBitmap != null) {
				canvas.drawBitmap(mLockBitmap, sliderModel.getStartX(),
						sliderModel.getStartY(), null);
			}
		}

	}

	/**
	 * 移动事件 writeValue 实际移动的值
	 * 
	 * @param event
	 * @return
	 */
	private int x = 0;
	private int y = 0;

	public boolean moveSlider(MotionEvent event) {
		if (sliderModel == null) {
			return false;
		}
		boolean bool = false;
		x = (int) event.getX();
		y = (int) event.getY();

		// 滑块的方向向下 即指针的方向向下 或者向上 移动的时候指针矩形起点y坐标不变 x变
		if (CALIBRATION_DIRECTION.DIRECTION_DOWN == sliderModel.getDirection()
				|| CALIBRATION_DIRECTION.DIRECTION_UP == sliderModel
						.getDirection()) {
			if (nLength == 0 || nLength < 0) // 最大最小值相等不允许滑动
			{
				return false;
			}
			// 判断是否在滑轨区域滑动
			if (x > (barStopX - pointRect.width() / 2 + 1)
					|| x < barStartX - (pointRect.width() / 2) - 1) {
				bool = false;
			} else {
				sliderModel.getPointStart().x = x;
				bool = true;
			}
			if (bool) {
				if (CALIBRATION_DIRECTION.DIRECTION_LEFT == sliderModel
						.getnPosition()) {
					writeToAddress = (x - barStartX + pointWidth / 2) * nLength
							/ (barLength);
					writeToAddress = sliderModel.getMaxTrend() - writeToAddress;
				} else {
					writeToAddress = (x - barStartX + pointWidth / 2) * nLength
							/ (barLength) + minTrend;
				}
				// Log.d("rect", writeToAddress + ",.........");
				setValueToAddr(writeToAddress);
			}
		}
		// 滑块的方向向左 即指针的方向向左 指针向上或者向下移动
		else if (CALIBRATION_DIRECTION.DIRECTION_LEFT == sliderModel
				.getDirection()
				|| CALIBRATION_DIRECTION.DIRECTION_RIGHT == sliderModel
						.getDirection()) {
			if (nLength == 0 || nLength < 0) {
				return false;// 最大最小值相等 不更新界面
			}
			// 判断是否在滑轨区域滑动
			if (y > (barStopY - pointRect.height() / 2 + 1)
					|| y < barStartY - (pointRect.height() / 2) - 1) {
				bool = false;
			} else {
				sliderModel.getPointStart().y = y;
				bool = true;
			}
			if (bool) {

				if (CALIBRATION_DIRECTION.DIRECTION_UP == sliderModel
						.getnPosition()) {
					writeToAddress = (y - barStartY + pointHeight / 2)
							* nLength / (barLength);
					writeToAddress = sliderModel.getMaxTrend() - writeToAddress;
				} else {
					writeToAddress = (y - barStartY + pointHeight / 2)
							* nLength / (barLength) + minTrend;
				}
				setValueToAddr(writeToAddress);
			}
		}

		return bool;
	}

	private void setValueToAddr(double writeValue) {
		// 不根据数据类型进行转换 直接写入地址
		// 用最大最小值和显示刻度的最大最小值进行缩放存入plc
		// 刻度范围
		if (null != sliderModel.getWriteKeyAdd()) {
			sliderArea = maxTrend - minTrend;
			double writePlcValue = writeValue;
			// if (calibrationArea != 0) {
			// writePlcValue = writeValue * sliderArea / calibrationArea;
			// }
			// 将writeValue写入地址
			Vector<Double> dataList = new Vector<Double>();
			// Log.d("rect", "写入地址的值:" + writePlcValue);
			dataList.add(writePlcValue);
			SEND_DATA_STRUCT mSendData = new SEND_DATA_STRUCT();
			mSendData.eDataType = sliderModel.getDataType();
			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;

			PlcRegCmnStcTools.setRegDoubleData(sliderModel.getWriteKeyAdd(),
					dataList, mSendData);
		}
	}

	/**
	 * 移动事件
	 */
	private boolean touchDown = false;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		SKSceneManage.getInstance().time = 0;
		// 如果滑动块实体类为空，则返回false
		if (sliderModel == null) {
			return false;
		}
		boolean bool = false;
		boolean touch = false;
		x = (int) event.getX();
		y = (int) event.getY();

		if (slideRect == null) {
			initValue();
		}
		
		// 判断是否在矩形区域滑动
		if (null == slideRect) {
			slideRect = new Rect();
			if (sliderModel.isShowCalibration()) {
				setSlideRect(true);
			} else {
				setSlideRect(false);
			}
		}
		if (x > slideRect.right || x < slideRect.left || y < slideRect.top
				|| y > slideRect.bottom) {
			if (touchDown) {
				touchDown = false;
				SKSceneManage.getInstance().onRefresh(item);                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
			}
			return false;
		}else {
			if (!isTouchFlag || !isShowFlag) {
				// 控件不可触摸
				if (!touch&&sliderModel!=null) {
					if (sliderModel.getTouchInfo()!=null) {
						if (event.getAction()==MotionEvent.ACTION_DOWN) {
							if (sliderModel.getTouchInfo().isbTouchByUser()) {
								SKSceneManage.getInstance().turnToLoginPop();
							}
						}
					}
				}
				return false;
			}
		}

		int action = event.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			touchDown = true;
			SKSceneManage.getInstance().onRefresh(item);
			touch = true;

		} else if (MotionEvent.ACTION_UP == event.getAction()
				|| MotionEvent.ACTION_CANCEL == event.getAction()) {
			touchDown = false;
			if (touch) {
				bool = moveSlider(event);
				SKSceneManage.getInstance().onRefresh(item);
			}
		}
		if (action == MotionEvent.ACTION_MOVE) {
			touchDown = true;
			bool = moveSlider(event);
			SKSceneManage.getInstance().onRefresh(item);
		} else {
			touchDown = false;
			SKSceneManage.getInstance().onRefresh(item);
		}
		return bool;
	}

	@Override
	public boolean isShow() {
		// TODO Auto-generated method stub\
		sliderIsShow();
		SKSceneManage.getInstance().onRefresh(item);
		return isShowFlag;
	}

	private void sliderIsShow() {
		if (showByAddr || showByUser) {
			isShowFlag = popedomIsShow(sliderModel.getShowInfo());
		}
	}

	@Override
	public boolean isTouch() {
		// TODO Auto-generated method stub
		sliderIsTouch();
		SKSceneManage.getInstance().onRefresh(item);
		return isTouchFlag;

	}

	private void sliderIsTouch() {
		if (touchByAddr || touchByUser) {
			isTouchFlag = popedomIsTouch(sliderModel.getTouchInfo());
		}
	}

	@Override
	public void realseMemeory() {
		/* 注销通知接口 */
		SKPlcNoticThread.getInstance().destoryCallback(showCall);// 显现
		SKPlcNoticThread.getInstance().destoryCallback(touchCall);// 触控
		SKPlcNoticThread.getInstance().destoryCallback(wirteCall);// 写入
		SKPlcNoticThread.getInstance().destoryCallback(trendMinCall);// 动态最小值
		SKPlcNoticThread.getInstance().destoryCallback(trendMaxCall);// 动态最大值
	}

	@Override
	public void getDataFromDatabase() {

	}

	@Override
	public void setDataToDatabase() {
	}

	@Override
	public void initGraphics() {
		init();
	}

	private Vector<Integer> mIData = null;
	private Vector<Long> mLData = null;
	private Vector<Short> mSData = null;
	private Vector<Float> mFData = null;
	private boolean result;
	/**
	 * 写入地址
	 */
	SKPlcNoticThread.IPlcNoticCallBack wirteCall = new SKPlcNoticThread.IPlcNoticCallBack() {
		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub

			// TODO Auto-generated method stub
			int nLen = nStatusValue.size();
			byte[] nTmpBytes = new byte[nLen];
			for (int i = 0; i < nLen; i++) {
				nTmpBytes[i] = nStatusValue.get(i);
			}

			double receiveNumber = converDataType(nStatusValue);
			// Log.d("slider", "------- 地址值改变 通知----" + receiveNumber);
			writeToAddress = receiveNumber;
			// 将通知过来的值转换为指针的点
			addrBoo = true;
			if (addrBoo && minBoo && maxBoo) {
				afterNotic(receiveNumber);
				SKSceneManage.getInstance().onRefresh(item);
			}
		}
	};

	/**
	 * 地址值改变之后的计算
	 * 
	 * @param receiveNumber
	 */
	private void afterNotic(double receiveNumber) {
		sliderArea = maxTrend - minTrend;
		// 回调之后的数值进行缩放后显示在界面
		if (receiveNumber > sliderModel.getMaxTrend()) {
			receiveNumber = sliderModel.getMaxTrend();
		}
		if (receiveNumber < sliderModel.getMinTrend()) {
			receiveNumber = sliderModel.getMinTrend();
		}
		// if (0 != sliderArea) {
		// receiveNumber = receiveNumber * calibrationArea / sliderArea;
		// }
		if (CALIBRATION_DIRECTION.DIRECTION_DOWN == sliderModel.getDirection()
				|| CALIBRATION_DIRECTION.DIRECTION_UP == sliderModel
						.getDirection()) {

			if (CALIBRATION_DIRECTION.DIRECTION_LEFT == sliderModel
					.getnPosition()) {
				if (nLength == 0 || nLength < 0) {
					int i = (int) (barStopX - pointWidth / 2);
					if (null != sliderModel.getPointStart()) {
						sliderModel.getPointStart().x = i;
					}
				} else {
					int i = (int) ((sliderModel.getMaxTrend() - receiveNumber)
							* barLength / nLength + barStartX - pointWidth / 2);
					if (i > barStopX) {
						i = (int) (barStopX - pointWidth / 2);
					} else if (i < barStartX) {
						i = (int) barStartX - pointWidth / 2;
					}
					if (null != sliderModel.getPointStart()) {
						sliderModel.getPointStart().x = i;
					}
				}

			} else {
				if (nLength == 0 || nLength < 0) {
					int i = (int) barStartX - pointWidth / 2;
					if (null != sliderModel.getPointStart()) {
						sliderModel.getPointStart().x = i;
					}
				} else {
					int i = (int) ((receiveNumber - minTrend) * barLength
							/ nLength + barStartX - pointWidth / 2);
					if (i > barStopX) {
						i = (int) (barStopX - pointWidth / 2);
					} else if (i < barStartX) {
						i = (int) barStartX - pointWidth / 2;
					}
					if (null != sliderModel.getPointStart()) {
						sliderModel.getPointStart().x = i;
					}
				}
			}

		} else {
			if (CALIBRATION_DIRECTION.DIRECTION_UP == sliderModel
					.getnPosition()) {
				// 被除数不能为0 即最大最小值相等
				if (nLength == 0 || nLength < 0) {
					int i = (int) barStopY - pointHeight / 2;
					if (null != sliderModel.getPointStart()) {
						sliderModel.getPointStart().y = i;
					}
				} else {
					int i = (int) ((sliderModel.getMaxTrend() - receiveNumber)
							* barLength / nLength + barStartY - pointHeight / 2);
					if (i > barStopY) {
						i = (int) barStopY - pointHeight / 2;
					} else if (i < barStartY) {
						i = (int) barStartY - pointHeight / 2;
					}
					if (null != sliderModel.getPointStart()) {
						sliderModel.getPointStart().y = i;
					}
				}
			} else {
				if (nLength == 0 || nLength < 0) {
					int i = (int) barStartY - pointHeight / 2;
					if (null != sliderModel.getPointStart()) {
						sliderModel.getPointStart().y = i;
					}
				} else {
					int i = (int) ((receiveNumber - minTrend) * barLength
							/ nLength + barStartY - pointHeight / 2);
					if (i > barStopY) {
						i = (int) barStopY - pointHeight / 2;
					} else if (i < barStartY) {
						i = (int) barStartY - pointHeight / 2;
					}
					if (null != sliderModel.getPointStart()) {
						sliderModel.getPointStart().y = i;
					}
				}
			}
		}
	}

	/**
	 * 数据类型转换
	 * 
	 * @param nStatusValue
	 * @return
	 */
	private double converDataType(Vector<Byte> nStatusValue) {
		double receiveNumber = 0;
		boolean valueBool = false;
		switch (sliderModel.getDataType()) {
		case INT_16: // 16位整数
			if (null == mSData) {
				mSData = new Vector<Short>();
			} else {
				mSData.clear();
			}
			valueBool = PlcRegCmnStcTools.bytesToShorts(nStatusValue, mSData);
			if (valueBool && mSData.size() != 0) {
				receiveNumber = mSData.get(0);
			}
			break;
		case POSITIVE_INT_16: // 16位正整数
			if (null == mIData) {
				mIData = new Vector<Integer>();
			} else {
				mIData.clear();
			}
			valueBool = PlcRegCmnStcTools.bytesToUShorts(nStatusValue, mIData);
			if (valueBool && 0 != mIData.size()) {
				receiveNumber = mIData.get(0);
			}
			break;
		case INT_32: // 32位整数
			if (null == mIData) {
				mIData = new Vector<Integer>();
			} else {
				mIData.clear();
			}
			valueBool = PlcRegCmnStcTools.bytesToInts(nStatusValue, mIData);
			if (valueBool && 0 != mIData.size()) {
				receiveNumber = mIData.get(0);
			}

			break;
		case POSITIVE_INT_32: // 32位正整数
			if (null == mLData) {
				mLData = new Vector<Long>();
			} else {
				mLData.clear();
			}
			valueBool = PlcRegCmnStcTools.bytesToUInts(nStatusValue, mLData);
			if (valueBool && 0 != mLData.size()) {
				receiveNumber = mLData.get(0);
			}

			break;
		case FLOAT_32: // 浮点数
			if (null == mFData) {
				mFData = new Vector<Float>();
			} else {
				mFData.clear();
			}
			valueBool = PlcRegCmnStcTools.bytesToFloats(nStatusValue, mFData);
			if (valueBool && 0 != mFData.size()) {
				receiveNumber = mFData.get(0);
			}

			break;
		default:
			if (null == mSData) {
				mSData = new Vector<Short>();
			} else {
				mSData.clear();
			}
			valueBool = PlcRegCmnStcTools.bytesToShorts(nStatusValue, mSData);
			if (valueBool && mSData.size() != 0) {
				receiveNumber = mSData.get(0);
			}
			break;
		}
		return receiveNumber;
	}

	/**
	 * 动态最小值地址
	 */
	SKPlcNoticThread.IPlcNoticCallBack trendMinCall = new SKPlcNoticThread.IPlcNoticCallBack() {
		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// 根据数据类型转换
			minTrend = converDataType(nStatusValue);
			sliderModel.setMinTrend((int) minTrend);
			nLength = (float) (maxTrend - minTrend);

			minBoo = true;
			if (addrBoo && minBoo && maxBoo) {
				// 重新设置写入到地址的值
				setValueToAddr(writeToAddress);
				afterNotic(writeToAddress);
				SKSceneManage.getInstance().onRefresh(item);
			}
		}
	};

	/**
	 * 动态最大值地址
	 */
	SKPlcNoticThread.IPlcNoticCallBack trendMaxCall = new SKPlcNoticThread.IPlcNoticCallBack() {
		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			maxTrend = converDataType(nStatusValue);
			sliderModel.setMaxTrend((int) maxTrend);
			nLength = (float) (maxTrend - minTrend);

			maxBoo = true;
			if (addrBoo && minBoo && maxBoo) {
				// 重新设置写入到地址的值
				setValueToAddr(writeToAddress);
				afterNotic(writeToAddress);
				SKSceneManage.getInstance().onRefresh(item);
			}

		}
	};

	/**
	 * 得到字符宽度
	 * 
	 * @param font
	 * @param paint
	 * @return
	 */
	private int getFontWidth(String font, Paint paint) {
		if (null != font) {
			return (int) paint.measureText(font);
		} else {
			return 0;
		}
	}
}
