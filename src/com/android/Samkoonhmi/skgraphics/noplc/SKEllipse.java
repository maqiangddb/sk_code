//import SKGraphicsBase;
package com.android.Samkoonhmi.skgraphics.noplc;

import com.android.Samkoonhmi.graphicsdrawframe.EllipseItem;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.ShapInfo;
import com.android.Samkoonhmi.skgraphics.noplc.base.SKGraphicsBase;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * 圆，椭圆
 * 
 * @author Administrator
 * 
 */
public class SKEllipse extends SKGraphicsBase {
	private ShapInfo info;
	private Paint mPaint;
	private SKItems items;
	private Rect myRect;
	private int itemId;
	private int scenId;
	private EllipseItem mEllipseItem;

	public SKEllipse(int itemId, int scenId,ShapInfo info) {
		// TODO Auto-generated constructor stub
		this.scenId = scenId;
		this.itemId = itemId;
		mPaint = new Paint();
		items = new SKItems();
		this.info=info;
		if (info!=null) {
			myRect = new Rect(info.getnPointX(), info.getnPointY(),
					info.getnPointX() + info.getnWidth(), info.getnPointY()
							+ info.getnHeight());

			items.itemId = this.itemId;
			items.nCollidindId = info.getnCollidindId();
			items.nZvalue = info.getnZvalue();
			items.sceneId = this.scenId;
			items.rect = myRect;
			items.mGraphics=this;
			
			mEllipseItem=new EllipseItem(myRect);
			mEllipseItem.setAlpha(info.getnAlpha());
			mEllipseItem.setBackColor(info.getnBackColor());
			mEllipseItem.setForeColor(info.getnForeColor());
			mEllipseItem.setHeight(info.getnHeight());
			mEllipseItem.setWidth(info.getnWidth());
			mEllipseItem.setLineWidth(info.getnLineWidth());
			mEllipseItem.setLineColor(info.getnLineColor());
			mEllipseItem.setLineType(info.geteLineType());
			mEllipseItem.setStyle(info.geteStyle());
		}

	}

	private void init() {
		if (null == info) {
			return;
		}
		SKSceneManage.getInstance().onRefresh(items);
	}

	@Override
	public boolean drawGraphics(Canvas canvas, int itemId) {
		if(null == info)
		{
			return false;
		}
		if (this.itemId == itemId ) {
			// TODO Auto-generated method stub
			if (mEllipseItem!=null) {
				mEllipseItem.draw(mPaint, canvas);
			}
			return true;
		} else {
			return false;
		}
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

	@Override
	public void initGraphics() {
		// TODO Auto-generated method stub
		init();

	}

}