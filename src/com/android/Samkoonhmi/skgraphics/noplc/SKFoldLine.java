//import SKGraphicsBase;
package com.android.Samkoonhmi.skgraphics.noplc;

import java.util.Vector;
import com.android.Samkoonhmi.graphicsdrawframe.FoldLineItem;
import com.android.Samkoonhmi.model.LineInfo;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.skgraphics.noplc.base.SKGraphicsBase;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * 
 * @author 折线
 * 
 */
public class SKFoldLine extends SKGraphicsBase {
	private LineInfo info;
	private Vector<Point> listPoint;
	private Paint mPaint;
	private SKItems items;
	private Rect mRect;
	private int itemId;
	private int sceneId;
	private FoldLineItem mFoldLineItem;

	public SKFoldLine(int itemId,int sceneId,LineInfo info) {
		this.itemId=itemId;
		this.sceneId=sceneId;
		mPaint = new Paint();
		items = new SKItems();
		this.info=info;
		
		if (info!=null) {
			mRect = new Rect(info.getnStartX(),
					info.getnStartY(), info.getnStartX()
							+ info.getnWidth(), info.getnStartY()
							+ info.getnHeight());
		
			items.itemId = this.itemId;
			items.nCollidindId = info.getnCollidindId();
			items.nZvalue = info.getnZvalue();
			items.sceneId=this.sceneId;
			items.rect = mRect;
			items.mGraphics=this;
			
			mFoldLineItem=new FoldLineItem(info.getPointList());
			mFoldLineItem.setAlpha(info.getnAlpha());
			mFoldLineItem.setEndArrowType(info.geteLineArrow());
			mFoldLineItem.setEndPointType(info.getEndPointType());
			mFoldLineItem.setLineColor(info.getnLineColor());
			mFoldLineItem.setLineType(info.geteLineType());
			mFoldLineItem.setLineWidth(info.getnLineWidth());
		}
	}

	private void init() {
		if(null == info)
		{
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
		if(itemId==info.getId()){
			if(mFoldLineItem!=null){
				mFoldLineItem.draw(mPaint, canvas);
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