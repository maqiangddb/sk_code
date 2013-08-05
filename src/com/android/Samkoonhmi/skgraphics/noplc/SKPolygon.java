//import SKGraphicsBase;
package com.android.Samkoonhmi.skgraphics.noplc;

import java.util.Vector;

import com.android.Samkoonhmi.graphicsdrawframe.PolygonItem;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.ShapInfo;
import com.android.Samkoonhmi.skgraphics.noplc.base.SKGraphicsBase;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * 多边形
 * 
 * @author Administrator
 * 
 */
public class SKPolygon extends SKGraphicsBase {
	private ShapInfo info;
	private Vector<Point> pointList;
	private Paint mPaint;
	private SKItems items;
	private Rect myRect;
	private int itemId;
	private int sceneId;
	private PolygonItem mPolygonItem;

	public SKPolygon(int itemId, int sceneId, ShapInfo info) {
		this.itemId = itemId;
		this.sceneId = sceneId;
		mPaint = new Paint();
		items = new SKItems();
		this.info = info;
		
		if (info!=null) {
			myRect = new Rect(info.getnPointX(), info.getnPointY(),
					info.getnPointX() + info.getnWidth(), info.getnPointY()
							+ info.getnHeight());

			items.itemId = this.itemId;
			items.nCollidindId = info.getnCollidindId();
			items.nZvalue = info.getnZvalue();
			items.sceneId = this.sceneId;
			items.rect = myRect;
			items.mGraphics=this;
			
			mPolygonItem = new PolygonItem(info.getListPoint());
			mPolygonItem.setAlpha(info.getnAlpha());
			mPolygonItem.setBackColor(info.getnBackColor());
			mPolygonItem.setForeColor(info.getnForeColor());
			mPolygonItem.setLineColor(info.getnLineColor());
			mPolygonItem.setLineType(info.geteLineType());
			mPolygonItem.setLineWidth(info.getnLineWidth());
			mPolygonItem.setStyle(info.geteStyle());
		}

	}

	private void init() {
		// Log.d("Scene", "SKPolygon info :" + info);
		if (null == info) {
			return;
		}
		
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
				if (mPolygonItem != null) {
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

}