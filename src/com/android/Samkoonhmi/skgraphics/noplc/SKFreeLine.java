//import SKGraphicsBase;
package com.android.Samkoonhmi.skgraphics.noplc;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.android.Samkoonhmi.SKThread;
import com.android.Samkoonhmi.databaseinterface.DBTool;
import com.android.Samkoonhmi.graphicsdrawframe.FreeLineItem;
import com.android.Samkoonhmi.model.LineInfo;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.skenum.LINE_CLASS;
import com.android.Samkoonhmi.skgraphics.noplc.base.SKGraphicsBase;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.MODULE;
import com.android.Samkoonhmi.util.TASK;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;

/**
 * 自由直线
 * 
 * @author Administrator
 * 
 */
public class SKFreeLine extends SKGraphicsBase {
	private LineInfo info = null;
	private Vector<Point> pointList;
	private Paint mPaint;
	private SKItems items;
	private Rect mRect;
	private int itemId;
	private int sceneId;
	private FreeLineItem mFreeLineItem;

	public SKFreeLine(int itemId, int sceneId,LineInfo info) {
		// TODO Auto-generated constructor stub
		this.sceneId = sceneId;
		this.itemId = itemId;
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
			
			mFreeLineItem=new FreeLineItem(info.getfPointList(),info.geteLineArrow());
			mFreeLineItem.setAlpha(info.getnAlpha());
			mFreeLineItem.setEndArrowType(info.geteLineArrow());
			mFreeLineItem.setLineColor(info.getnLineColor());
			mFreeLineItem.setLineType(info.geteLineType());
			mFreeLineItem.setLineWidth(info.getnLineWidth());
			mFreeLineItem.setEndPointType(info.getEndPointType());
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
		// TODO Auto-generated method stub
		if(null == info)
		{
			return false;
		}
		if (itemId == info.getId()) {
			if (mFreeLineItem!=null) {
				mFreeLineItem.draw(mPaint, canvas);
			}
			return true;
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