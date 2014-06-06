//import SKGraphicsBase;
package com.android.Samkoonhmi.skgraphics.noplc;

import java.util.ArrayList;
import java.util.Vector;
import com.android.Samkoonhmi.graphicsdrawframe.LineItem;
import com.android.Samkoonhmi.graphicsdrawframe.RectItem;
import com.android.Samkoonhmi.model.IItem;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.TableModel;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread.IPlcNoticCallBack;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skgraphics.noplc.base.SKGraphicsBase;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

/**
 * 表格控件
 * 
 * @author Administrator
 */
public class SKTable extends SKGraphicsBase implements IItem {

	private Paint paint;
	private TableModel table;// 实例化表格
	private Rect totalRect;// 外矩形
	private SKItems skItem;// item
	private int itemId;
	private RectItem mRectItem;
	private boolean showByAddr;
	private boolean showByUser;
	private boolean isShowFlag;

	public SKTable(int itemId, int sceneid, TableModel model) {
		super();
//		Log.d("SKTable", "SKTable");
		this.itemId = itemId;
		totalRect = new Rect();
		paint = new Paint();
		skItem = new SKItems();
		isShowFlag = true;
		table = model;
		if (table != null) {
			totalRect.set((int) table.getnLeftTopX(),
					(int) table.getnLeftTopY(), (int) table.getnLeftTopX()
							+ (int) table.getnTableWidth(),
					(int) table.getnLeftTopY() + (int) table.getnTableHeight());
			skItem.nCollidindId = table.getnCollidindId();
			skItem.nZvalue = table.getnZvalue();
			skItem.rect = totalRect;
			skItem.itemId = itemId;
			skItem.sceneId = sceneid;
			skItem.mGraphics = this;

			Rect rect = this.getFrameLineRect();// 画矩形
			mRectItem = new RectItem(rect);
			mRectItem.setBackColor(table.getnBackColor());
			mRectItem.setAlpha(table.getAlpha());
			mRectItem.setLineAlpha(255);

			// 是否描边
			if (table.isbShowFrameLine()) {
				mRectItem.setLineWidth(table.getnWLineWidth());
				mRectItem.setLineColor(table.getnWShowColor());
			} else {
				mRectItem.setLineWidth(0);
			}
			mRectItem.init();

			// 横线
			if (table.isbShowOrientationLine()) {
				ArrayList<LineItem> items = new ArrayList<LineItem>();
				table.setmRowItems(items);
				double y = table.getnLeftTopY();
				for (int i = 0; i < table.getnRows().size() - 1; i++) {
					y += table.getnRows().get(i);
					Vector<Point> Points = getOrientationLine(y);
					LineItem lineItem = new LineItem(Points);
					lineItem.setLineColor(table.getnNShowColor());
					lineItem.setLineType(table.geteNLineType());
					items.add(lineItem);
				}
			}

			// 画垂直线
			if (table.isbShowPortraitCount()) {
				if (table.getmColumsItems() == null) {
					ArrayList<LineItem> items = new ArrayList<LineItem>();
					table.setmColumsItems(items);
					double x = table.getnLeftTopX();
					for (int i = 0; i < table.getnColums().size() - 1; i++) {
						x += table.getnColums().get(i);
						Vector<Point> Points = getPortraitLine(x);
						LineItem lineItem = new LineItem(Points);
						lineItem.setLineColor(table.getnNShowColor());
						lineItem.setLineType(table.geteNLineType());
						items.add(lineItem);
					}
				}
			}
			if (null != table.getShowInfo()) {
				if (-1 != table.getShowInfo().getnAddrId()
						&& table.getShowInfo().isbShowByAddr()) {
					showByAddr = true;
				}
				if (table.getShowInfo().isbShowByUser()) {
					showByUser = true;
				}
			}
			// 注册显现地址值
			registAddr();

		}
	}

	@Override
	public void initGraphics() {
//		Log.d("SKTable", "initGraphics");
		if (table == null) {
			// 数据为空
			return;
		}

		tableIsShow();
		SKSceneManage.getInstance().onRefresh(skItem);
	}

	@Override
	public boolean drawGraphics(Canvas canvas, int itemId) {
//		Log.d("SKTable", "drawGraphics");
		if (null == table) {
			return false;
		}
		if (this.itemId == itemId) {
			if (isShowFlag) {
				drawGraphics(canvas);
			}
			return true;
		}
		return false;
	}

	public void drawGraphics(Canvas canvas) {
//		Log.d("SKTable", "drawGraphics");
		// 画矩形背景
		mRectItem.draw(paint, canvas);

		// 画横向线
		if (table.isbShowOrientationLine()) {
			if (table.getmRowItems() != null) {
				for (int i = 0; i < table.getmRowItems().size(); i++) {
					table.getmRowItems().get(i).draw(paint, canvas);
				}
			}
		}

		// 画垂直线
		if (table.isbShowPortraitCount()) {
			if (table != null) {
				for (int i = 0; i < table.getmColumsItems().size(); i++) {
					table.getmColumsItems().get(i).draw(paint, canvas);
				}
			}
		}
	}

	/**
	 * 画边框矩形
	 * 
	 * @return
	 */
	private Rect getFrameLineRect() {
//		Log.d("SKTable", "getFrameLineRect画边框矩形");
		Rect rect = new Rect((int) table.getnLeftTopX(),
				(int) table.getnLeftTopY(), (int) table.getnLeftTopX()
						+ table.getnTableWidth(), (int) table.getnLeftTopY()
						+ table.getnTableHeight());
		return rect;
	}

	/**
	 * 得到外边框线条
	 */
	private Vector<Point> getWLine() {
//		Log.d("SKTable", "getWLine");
		Vector<Point> pointList = new Vector<Point>();
		return pointList;
	}

	/**
	 * 得到横向的线条
	 */
	private Vector<Point> getOrientationLine(double y) {
//		Log.d("SKTable", "getOrientationLine");
		Vector<Point> pointList = new Vector<Point>();
		Point mPoint = new Point();
		mPoint.x = (int) table.getnLeftTopX();
		// if(table.getnOrientationCount()<5)
		// mPoint.y=(int)(y+2);
		// else
		// mPoint.y=(int)(y+4);
		mPoint.y = (int) (y);
		pointList.add(mPoint);

		Point mPoint1 = new Point();
		mPoint1.x = (int) table.getnLeftTopX() + table.getnTableWidth();
		// if(table.getnOrientationCount()<5)
		// mPoint1.y=(int)(y+2);
		// else
		// mPoint1.y=(int)(y+4);
		mPoint1.y = (int) (y);
		pointList.add(mPoint1);

		return pointList;

	}

	/**
	 * 得到纵向的线条
	 * 
	 * @return
	 */
	private Vector<Point> getPortraitLine(double x) {
//		Log.d("SKTable", "getPortraitLine");
		Vector<Point> pointList = new Vector<Point>();
		Point mPoint = new Point();
		// if(table.getnPortraitCount()<5)
		// mPoint.x=(int)(x+2);
		// else
		// mPoint.x=(int)(x+4);
		mPoint.x = (int) (x);
		mPoint.y = (int) table.getnLeftTopY();
		pointList.add(mPoint);

		Point mPoint1 = new Point();
		// if(table.getnPortraitCount()<5)
		// mPoint1.x=(int)(x+2);
		// else
		// mPoint1.x=(int)(x+4);
		mPoint1.x = (int) (x);
		mPoint1.y = (int) table.getnLeftTopY() + table.getnTableHeight();
		pointList.add(mPoint1);
		return pointList;
	}

	/**
	 * 从数据库中读取数据
	 */
	@Override
	public void getDataFromDatabase() {
		// TODO Auto-generated method stub
	}

	/**
	 * 设置数据库中的值
	 */
	@Override
	public void setDataToDatabase() {
		// TODO Auto-generated method stub

	}

	/**
	 * 清空所有属性
	 */
	@Override
	public void realseMemeory() {

	}

	@Override
	public boolean isShow() {
		// TODO Auto-generated method stub
		tableIsShow();
		SKSceneManage.getInstance().onRefresh(skItem);
		return isShowFlag;
	}

	private void tableIsShow() {
		if (showByAddr || showByUser) {
			isShowFlag = popedomIsShow(table.getShowInfo());
		}
	}

	private void registAddr() {
//		Log.d("SKTable", "registAddr");
		// 注册显现地址值
		if (showByAddr && null != table.getShowInfo().getShowAddrProp()) {
			ADDRTYPE addrType = table.getShowInfo().geteAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				SKPlcNoticThread.getInstance().addNoticProp(
						table.getShowInfo().getShowAddrProp(), showCall, true,
						skItem.sceneId);
			} else {
				SKPlcNoticThread.getInstance().addNoticProp(
						table.getShowInfo().getShowAddrProp(), showCall, false,
						skItem.sceneId);
			}

		}
		// 注册行列地址
		if (table != null) {
			if (table.getbHControl()) {
				SKPlcNoticThread.getInstance().addNoticProp(
						table.getnAddHControl(), rowChange, false,
						skItem.sceneId);
			}
			if (table.getbVControl()) {
				SKPlcNoticThread.getInstance().addNoticProp(
						table.getnAddrVControl(), columeChange, false,
						skItem.sceneId);
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
//			Log.d("SKTable", "显现地址值改变通知");
			isShow();
		}

	};
	Vector<Short> mSData;
	SKPlcNoticThread.IPlcNoticCallBack columeChange = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
//			Log.d("SKTable", "SKPlcNoticThread.IPlcNoticCallBack columeChange");
			if (mSData == null) {
				mSData = new Vector<Short>();
			} else {
				mSData.clear();
			}
			boolean result = PlcRegCmnStcTools.bytesToShorts(nStatusValue,
					mSData);
			if (result) {
				if (mSData.get(0) == table.getnColums().size()) {
					return;
				} else {
					double columBlockSize = table.getnTableWidth()
							/ mSData.get(0);
					table.getnColums().clear();
					for (int i = 0; i < mSData.get(0); i++) {
						double x = columBlockSize;
						table.getnColums().add(x);
					}
				}
			}
			// 画垂直线
			if (table.isbShowPortraitCount()) {
				ArrayList<LineItem> items = new ArrayList<LineItem>();
				table.setmColumsItems(items);
				double x = table.getnLeftTopX();
				for (int i = 0; i < table.getnColums().size() - 1; i++) {
					x += table.getnColums().get(i);
					Vector<Point> Points = getPortraitLine(x);
					LineItem lineItem = new LineItem(Points);
					lineItem.setLineColor(table.getnNShowColor());
					lineItem.setLineType(table.geteNLineType());
					items.add(lineItem);
				}
			}

			SKSceneManage.getInstance().onRefresh(skItem);
		}
	};

	SKPlcNoticThread.IPlcNoticCallBack rowChange = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
//			Log.d("SKTable", "SKPlcNoticThread.IPlcNoticCallBack rowChange");
			if (mSData == null) {
				mSData = new Vector<Short>();
			} else {
				mSData.clear();
			}
			boolean result = PlcRegCmnStcTools.bytesToShorts(nStatusValue,
					mSData);
			if (result) {
				if (mSData.get(0) == table.getnRows().size()) {
					return;
				} else {
					double columBlockSize = table.getnTableHeight()
							/ mSData.get(0);
					table.getnRows().clear();
					for (int i = 0; i < mSData.get(0); i++) {
						double x = columBlockSize;
						table.getnRows().add(x);
					}
				}
			}
			// 横线
			if (table.isbShowOrientationLine()) {
				ArrayList<LineItem> items = new ArrayList<LineItem>();
				table.setmRowItems(items);
				double y = table.getnLeftTopY();
				for (int i = 0; i < table.getnRows().size() - 1; i++) {
					y += table.getnRows().get(i);
					Vector<Point> Points = getOrientationLine(y);
					LineItem lineItem = new LineItem(Points);
					lineItem.setLineColor(table.getnNShowColor());
					lineItem.setLineType(table.geteNLineType());
					items.add(lineItem);
				}
			}

			SKSceneManage.getInstance().onRefresh(skItem);
		}
	};

	/**
	 * 获取控件属性接口
	 */
	@Override
	public IItem getIItem() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public int getItemLeft(int id) {
		// TODO Auto-generated method stub
		if (table != null) {
			return (int) table.getnLeftTopX();
		}
		return -1;
	}

	@Override
	public int getItemTop(int id) {
		// TODO Auto-generated method stub
		if (table != null) {
			return (int) table.getnLeftTopY();
		}
		return -1;
	}

	@Override
	public int getItemWidth(int id) {
		// TODO Auto-generated method stub
		if (table != null) {
			return table.getnTableWidth();
		}
		return -1;
	}

	@Override
	public int getItemHeight(int id) {
		// TODO Auto-generated method stub
		if (table != null) {
			return table.getnTableHeight();
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
		if (table != null) {
			return getColor(table.getnBackColor());
		}
		return null;
	}

	@Override
	public short[] getItemLineColor(int id) {
		// TODO Auto-generated method stub
		if (table != null) {
			return getColor(table.getnWShowColor());
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
		if (table != null) {
			if (x == table.getnLeftTopX()) {
				return true;
			}
			if (x < 0
					|| x > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneWidth()) {
				return false;
			}
			table.setnLeftTopX(x);
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
		if (table != null) {
			if (y == table.getnLeftTopY()) {
				return true;
			}
			if (y < 0
					|| y > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneHeight()) {
				return false;
			}
			table.setnLeftTopY(y);
			int t = skItem.rect.top;
			skItem.rect.top = y;
			skItem.rect.bottom = y - t + skItem.rect.bottom;
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
		if (table != null) {
			if (w == table.getnTableWidth()) {
				return true;
			}
			if (w < 0
					|| w > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneWidth()) {
				return false;
			}
			table.setnTableWidth(w);
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
		if (table != null) {
			if (h == table.getnTableHeight()) {
				return true;
			}
			if (h < 0
					|| h > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneHeight()) {
				return false;
			}
			table.setnTableHeight(h);
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
		if (table == null) {
			return false;
		}

		int color = Color.rgb(r, g, b);

		if (color == table.getnBackColor()) {
			return true;
		}
		table.setnBackColor(color);
		mRectItem.setBackColor(color);
		SKSceneManage.getInstance().onRefresh(skItem);
		return true;
	}

	@Override
	public boolean setItemLineColor(int id, short r, short g, short b) {
		// TODO Auto-generated method stub
		if (table == null) {
			return false;
		}

		int color = Color.rgb(r, g, b);

		if (color == table.getnNShowColor()) {
			return true;
		}
		table.setnWShowColor(color);
		mRectItem.setLineColor(color);
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
		if (table==null||alpha<0||alpha>255) {
			return false;
		}
		if (table.getAlpha()==alpha) {
			return true;
		}
		table.setAlpha(alpha);
		mRectItem.setAlpha(alpha);
		SKSceneManage.getInstance().onRefresh(skItem);
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