//import SKGraphicsBase;
package com.android.Samkoonhmi.skgraphics.noplc;

import java.util.Vector;

import com.android.Samkoonhmi.graphicsdrawframe.PolygonItem;
import com.android.Samkoonhmi.model.IItem;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.ShapInfo;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skgraphics.noplc.base.SKGraphicsBase;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * 多边形
 * 
 * @author Administrator
 * 
 */
public class SKPolygon extends SKGraphicsBase implements IItem {
	private ShapInfo info;
	private Vector<Point> pointList;
	private Paint mPaint;
	private SKItems items;
	private Rect myRect;
	private int itemId;
	private int sceneId;
	private PolygonItem mPolygonItem;
	private boolean isShowFlag;
	private boolean showByUser;
	private boolean showByAddr;

	public SKPolygon(int itemId, int sceneId, ShapInfo info) {
		this.itemId = itemId;
		this.sceneId = sceneId;
		mPaint = new Paint();
		items = new SKItems();
		this.info = info;
		isShowFlag = true;
		showByAddr = false;
		showByUser = false;
		if (info != null) {
			myRect = new Rect(info.getnPointX(), info.getnPointY(),
					info.getnPointX() + info.getnWidth(), info.getnPointY()
							+ info.getnHeight());

			items.itemId = this.itemId;
			items.nCollidindId = info.getnCollidindId();
			items.nZvalue = info.getnZvalue();
			items.sceneId = this.sceneId;
			items.rect = myRect;
			items.mGraphics = this;

			mPolygonItem = new PolygonItem(info.getListPoint());
			mPolygonItem.setAlpha(info.getnAlpha());
			mPolygonItem.setBackColor(info.getnBackColor());
			mPolygonItem.setForeColor(info.getnForeColor());
			mPolygonItem.setLineColor(info.getnLineColor());
			mPolygonItem.setLineType(info.geteLineType());
			mPolygonItem.setLineWidth(info.getnLineWidth());
			mPolygonItem.setStyle(info.geteStyle());

			if (null != info.getShowInfo()) {
				if (-1 != info.getShowInfo().getnAddrId()
						&& info.getShowInfo().isbShowByAddr()) {
					showByAddr = true;
				}
				if (info.getShowInfo().isbShowByUser()) {
					showByUser = true;
				}
			}
			registAddr();
		}

	}

	private void init() {
		// Log.d("Scene", "SKPolygon info :" + info);
		if (null == info) {
			return;
		}

		polyIsShow();
		SKSceneManage.getInstance().onRefresh(items);
	}

	@Override
	public void initGraphics() {
		init();

	}

	@Override
	public boolean drawGraphics(Canvas canvas, int itemId) {
		if (null != info) {

			if (itemId == info.getId()) {
				if (mPolygonItem != null && isShowFlag) {
					mPolygonItem.draw(mPaint, canvas);
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public void getDataFromDatabase() {
	}

	@Override
	public void setDataToDatabase() {
		// TODO Auto-generated method stub
	}

	@Override
	public void realseMemeory() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isShow() {
		polyIsShow();
		SKSceneManage.getInstance().onRefresh(items);
		return isShowFlag;

	}

	private void polyIsShow() {
		if (showByAddr || showByUser) {
			isShowFlag = popedomIsShow(info.getShowInfo());
		}
	}

	private void registAddr() {
		// 注册显现地址值
		if (showByAddr && null != info.getShowInfo().getShowAddrProp()) {
			ADDRTYPE addrType = info.getShowInfo().geteAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				SKPlcNoticThread.getInstance().addNoticProp(
						info.getShowInfo().getShowAddrProp(), showCall, true,
						sceneId);
			} else {
				SKPlcNoticThread.getInstance().addNoticProp(
						info.getShowInfo().getShowAddrProp(), showCall, false,
						sceneId);
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
		if (info != null) {
			return info.getnPointX();
		}
		return -1;
	}

	@Override
	public int getItemTop(int id) {
		// TODO Auto-generated method stub
		if (info != null) {
			return info.getnPointY();
		}
		return -1;
	}

	@Override
	public int getItemWidth(int id) {
		// TODO Auto-generated method stub
		if (info != null) {
			return info.getnWidth();
		}
		return -1;
	}

	@Override
	public int getItemHeight(int id) {
		// TODO Auto-generated method stub
		if (info != null) {
			return info.getnHeight();
		}
		return -1;
	}

	@Override
	public short[] getItemForecolor(int id) {
		// TODO Auto-generated method stub
		if (info != null) {
			return getColor(info.getnForeColor());
		}
		return null;
	}

	@Override
	public short[] getItemBackcolor(int id) {
		// TODO Auto-generated method stub
		if (info != null) {
			return getColor(info.getnBackColor());
		}
		return null;
	}

	@Override
	public short[] getItemLineColor(int id) {
		// TODO Auto-generated method stub
		if (info != null) {
			return getColor(info.getnLineColor());
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
		if (info != null) {
			if (x == info.getnPointX()) {
				return true;
			}
			if (x < 0
					|| x > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneWidth()) {
				return false;
			}
			info.setnPointX(x);
			int l = x - items.rect.left;
			items.rect.left = x;
			items.rect.right = x - l + items.rect.right;
			items.mMoveRect = new Rect();
			for (int i = 0; i < info.getListPoint().size(); i++) {
				info.getListPoint().get(i).x = info.getListPoint().get(i).x + l;
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
		if (info != null) {
			if (y == info.getnPointY()) {
				return true;
			}
			if (y < 0
					|| y > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneHeight()) {
				return false;
			}
			info.setnPointY(y);
			int t = y - items.rect.top;
			items.rect.top = y;
			items.rect.bottom = t + items.rect.bottom;
			items.mMoveRect = new Rect();
			for (int i = 0; i < info.getListPoint().size(); i++) {
				info.getListPoint().get(i).y = info.getListPoint().get(i).y + t;
			}

			SKSceneManage.getInstance().onRefresh(items);
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean setItemWidth(int id, int w) {

		return false;
	}

	@Override
	public boolean setItemHeight(int id, int h) {
		// TODO Auto-generated method stub

		return false;
	}

	@Override
	public boolean setItemForecolor(int id, short r, short g, short b) {
		// TODO Auto-generated method stub
		if (info == null) {
			return false;
		}
		int color = Color.rgb(r, g, b);
		if (color == info.getnForeColor()) {
			return true;
		}
		info.setnForeColor(color);
		mPolygonItem.setForeColor(color);
		SKSceneManage.getInstance().onRefresh(items);
		return true;
	}

	@Override
	public boolean setItemBackcolor(int id, short r, short g, short b) {
		// TODO Auto-generated method stub
		if (info == null) {
			return false;
		}

		int color = Color.rgb(r, g, b);

		if (color == info.getnBackColor()) {
			return true;
		}
		info.setnBackColor(color);
		mPolygonItem.setBackColor(color);
		SKSceneManage.getInstance().onRefresh(items);
		return true;
	}

	@Override
	public boolean setItemLineColor(int id, short r, short g, short b) {
		// TODO Auto-generated method stub
		if (info == null) {
			return false;
		}

		int color = Color.rgb(r, g, b);

		if (color == info.getnLineColor()) {
			return true;
		}
		info.setnLineColor(color);
		mPolygonItem.setLineColor(color);
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
		if (info==null||alpha<0||alpha>255) {
			return false;
		}
		if (info.getnAlpha()==alpha) {
			return true;
		}
		info.setnAlpha(alpha);
		mPolygonItem.setAlpha(alpha);
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