package com.android.Samkoonhmi.skgraphics.noplc;

import java.util.Vector;

import com.android.Samkoonhmi.graphicsdrawframe.CurveArcItem;
import com.android.Samkoonhmi.model.IItem;
import com.android.Samkoonhmi.model.LineInfo;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skenum.POINT_TYPE;
import com.android.Samkoonhmi.skgraphics.noplc.base.SKGraphicsBase;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

//import SKGraphicsBase;
/**
 * 画曲线圆弧
 * 
 * @author 瞿丽平
 * 
 */
public class SKCurveArc extends SKGraphicsBase implements IItem {
	private LineInfo line;
	private Vector<Point> listPoint;
	private Vector<POINT_TYPE> pointTypeList;
	private Paint mPaint;
	private SKItems items;
	private Rect mRect;
	private int itemId;
	private int scenId;
	private CurveArcItem mCurveArcItem;
	private boolean isShowFlag;
	private boolean showByUser;
	private boolean showByAddr;

	public SKCurveArc(int itemId, int scenId, LineInfo line) {
		this.scenId = scenId;
		this.itemId = itemId;
		listPoint = new Vector<Point>();
		pointTypeList = new Vector<POINT_TYPE>();
		mPaint = new Paint();
		items = new SKItems();
		isShowFlag = true;
		showByAddr = false;
		showByUser = false;
		this.line = line;

		if (line != null) {
			mRect = new Rect(line.getnStartX(), line.getnStartY(),
					line.getnStartX() + line.getnWidth(), line.getnStartY()
							+ line.getnHeight());

			items.itemId = this.itemId;
			items.nCollidindId = line.getnCollidindId();
			items.nZvalue = line.getnZvalue();
			items.sceneId = this.scenId;
			items.rect = mRect;
			items.mGraphics = this;

			mCurveArcItem = new CurveArcItem(line.getPointList(),
					line.getPointTypeList());
			mCurveArcItem.setAlpha(line.getnAlpha());
			mCurveArcItem.setEndArrowType(line.geteLineArrow());
			mCurveArcItem.setLineColor(line.getnLineColor());
			mCurveArcItem.setLineType(line.geteLineType());
			mCurveArcItem.setLineWidth(line.getnLineWidth());

			if (null != line.getShowInfo()) {
				if (-1 != line.getShowInfo().getnAddrId()
						&& line.getShowInfo().isbShowByAddr()) {
					showByAddr = true;
				}
				if (line.getShowInfo().isbShowByUser()) {
					showByUser = true;
				}
			}

			registAddr();
		}

	}

	private void init() {

		if (null == line) {
			return;
		}

		curveArcIsShow();
		SKSceneManage.getInstance().onRefresh(items);
	}

	@Override
	public boolean drawGraphics(Canvas canvas, int itemId) {
		if (null == line) {
			return false;
		}
		if (this.itemId == itemId) {
			if (mCurveArcItem != null && isShowFlag) {
				mCurveArcItem.draw(mPaint, canvas);
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void realseMemeory() {
	}

	@Override
	public void getDataFromDatabase() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDataToDatabase() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initGraphics() {
		init();

	}

	@Override
	public boolean isShow() {
		curveArcIsShow();
		SKSceneManage.getInstance().onRefresh(items);
		return isShowFlag;

	}

	private void curveArcIsShow() {
		if (showByAddr || showByUser) {
			isShowFlag = popedomIsShow(line.getShowInfo());
		}
	}

	private void registAddr() {
		// 注册显现地址值
		if (showByAddr && null != line.getShowInfo().getShowAddrProp()) {
			ADDRTYPE addrType = line.getShowInfo().geteAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				SKPlcNoticThread.getInstance().addNoticProp(
						line.getShowInfo().getShowAddrProp(), showCall, true,
						scenId);
			} else {
				SKPlcNoticThread.getInstance().addNoticProp(
						line.getShowInfo().getShowAddrProp(), showCall, false,
						scenId);
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

	/**
	 * 获取控件属性接口
	 */
	public IItem getIItem() {
		return this;
	}

	@Override
	public int getItemLeft(int id) {
		// TODO Auto-generated method stub
		if (line != null) {
			return line.getnStartX();
		}
		return -1;
	}

	@Override
	public int getItemTop(int id) {
		// TODO Auto-generated method stub
		if (line != null) {
			return line.getnStartY();
		}
		return -1;
	}

	@Override
	public int getItemWidth(int id) {
		// TODO Auto-generated method stub
		if (line != null) {
			return line.getnWidth();
		}
		return -1;
	}

	@Override
	public int getItemHeight(int id) {
		// TODO Auto-generated method stub
		if (line != null) {
			return line.getnHeight();
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
		if (line != null) {
			return getColor(line.getnLineColor());
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
		if (line != null) {
			if (x == line.getnStartX()) {
				return true;
			}
			if (x < 0
					|| x > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneWidth()) {
				return false;
			}
			int len=x-line.getnStartX();
			line.setnStartX(x);
			int l = items.rect.left;
			items.rect.left = x;
			items.rect.right = x - l + items.rect.right;
			items.mMoveRect = new Rect();
			if(line.getPointList()!=null){
				for(int i=0;i<line.getPointList().size();i++){
					Point p=line.getPointList().get(i);
					p.x=p.x+len;
				}
			}				
			SKSceneManage.getInstance().onRefresh(items);
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean setItemTop(int id, int y) {
		// TODO Auto-generated method stub
		if (line != null) {
			if (y == line.getnStartY()) {
				return true;
			}
			if (y < 0
					|| y > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneHeight()) {
				return false;
			}
			int hen=y-line.getnStartY();
			line.setnStartY(y);
			int t = items.rect.top;
			items.rect.top = y;
			items.rect.bottom = y - t + items.rect.bottom;
			items.mMoveRect = new Rect();
			if(line.getPointList()!=null){
				for(int i=0;i<line.getPointList().size();i++){
					Point p=line.getPointList().get(i);
					p.y=p.y+hen;
				}
			}		
			SKSceneManage.getInstance().onRefresh(items);
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean setItemWidth(int id, int w) {
		// TODO Auto-generated method stub
		if (line != null) {
			if (w == line.getnWidth()) {
				return true;
			}
			if (w < 0
					|| w > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneWidth()) {
				return false;
			}
			line.setnWidth(w);
			items.rect.right = w - items.rect.width() + items.rect.right;
			items.mMoveRect = new Rect();
			SKSceneManage.getInstance().onRefresh(items);
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean setItemHeight(int id, int h) {
		// TODO Auto-generated method stub
		if (line != null) {
			if (h == line.getnHeight()) {
				return true;
			}
			if (h < 0
					|| h > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneHeight()) {
				return false;
			}
			line.setnHeight(h);
			items.rect.bottom = h - items.rect.height() + items.rect.bottom;
			items.mMoveRect = new Rect();
			SKSceneManage.getInstance().onRefresh(items);
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
		if (line == null) {
			return false;
		}

		int color = Color.rgb(r, g, b);

		if (color == line.getnLineColor()) {
			return true;
		}
		line.setnLineColor(color);
		mCurveArcItem.setLineColor(color);
		SKSceneManage.getInstance().onRefresh(items);
		return true;
	}

	@Override
	public boolean setItemVisible(int id, boolean v) {
		// TODO Auto-generated method stub
		if (v == isShowFlag) {
			return true;
		}
		isShowFlag = v;
		SKSceneManage.getInstance().onRefresh(items);
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
		if (line==null||alpha<0||alpha>255) {
			return false;
		}
		if (line.getnAlpha()==alpha) {
			return true;
		}
		line.setnAlpha(alpha);
		mCurveArcItem.setLineAlpha(alpha);
		SKSceneManage.getInstance().onRefresh(items);
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