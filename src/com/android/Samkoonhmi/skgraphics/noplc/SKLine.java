//import SKGraphicsBase;
package com.android.Samkoonhmi.skgraphics.noplc;

import java.util.Vector;
import com.android.Samkoonhmi.graphicsdrawframe.LineItem;
import com.android.Samkoonhmi.model.LineInfo;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.skgraphics.noplc.base.SKGraphicsBase;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * 线
 * 
 * @author Administrator
 * 
 */
public class SKLine extends SKGraphicsBase {
	private LineInfo info;
	private Vector<Point> pointList;
	private Paint mPaint;
	private SKItems items;
	private Rect mRect;
	private int itemId;
	private int sceneId;
	private LineItem mLineItem;

	public SKLine(int itemId, int sceneId,LineInfo info) {
		this.itemId = itemId;
		this.sceneId = sceneId;
		pointList = new Vector<Point>();
		mPaint = new Paint();
		items = new SKItems();
		this.info=info;
		
		if (info!=null) {
			mRect = new Rect(info.getnStartX(), info.getnStartY(),
					info.getnStartX() + info.getnWidth(), info.getnStartY()
							+ info.getnHeight());

			items.itemId = this.itemId;
			items.nCollidindId = info.getnCollidindId();
			items.nZvalue = info.getnZvalue();
			items.sceneId = sceneId;
			items.rect = mRect;
			items.mGraphics=this;
			
			mLineItem=new LineItem(info.getPointList());
			mLineItem.setLineColor(info.getnLineColor());
			mLineItem.setLineType(info.geteLineType());
			mLineItem.setAlpha(info.getnAlpha());
			mLineItem.setEndArrowType(info.geteLineArrow());
			mLineItem.setLineWidth(info.getnLineWidth());
			mLineItem.setM_pointList(info.getPointList());
			mLineItem.setPointTypeList(null);
		}

	}

	private void init() {
		if (null == info) {
			return;
		}
		
		SKSceneManage.getInstance().onRefresh(items);
	}

	@Override
	public void initGraphics() {
		// TODO Auto-generated method stub
		init();

	}

	@Override
	public boolean drawGraphics(Canvas canvas, int itemId) {
		// Log.d("image", "itemId="+itemId);
		if (null == info) {
			return false;
		}
		// Log.d("image", "info.getId="+info.getId());
		if (itemId == info.getId()) {
			if (mLineItem!=null) {
				mLineItem.draw(mPaint, canvas);
			}
			return true;
		}
		return false;
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
	public void realseMemeory() {
		// TODO Auto-generated method stub
	}

}