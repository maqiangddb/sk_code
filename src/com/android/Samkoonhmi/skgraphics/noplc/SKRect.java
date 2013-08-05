//import SKGraphicsBase;
package com.android.Samkoonhmi.skgraphics.noplc;

import com.android.Samkoonhmi.graphicsdrawframe.RectItem;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.ShapInfo;
import com.android.Samkoonhmi.skgraphics.noplc.base.SKGraphicsBase;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * 矩形
 * 
 * @author Administrator
 * 
 */
public class SKRect extends SKGraphicsBase {
	private ShapInfo info;
	private Paint mPaint;
	private SKItems items;
	private Rect myRect;
	private int itemId;
	private int secneId;
	private RectItem mRectItem;

	public SKRect(int itemId, int secneId, ShapInfo info) {
		this.itemId = itemId;
		this.secneId = secneId;
		mPaint = new Paint();
		items = new SKItems();
		this.info = info;
		if (info!=null) {
			myRect = new Rect(info.getnPointX(), info.getnPointY(),
					info.getnPointX() + info.getnWidth(), info.getnPointY()
							+ info.getnHeight());

			items.itemId = this.itemId;
			items.sceneId = secneId;
			items.nCollidindId = info.getnCollidindId();
			items.nZvalue = info.getnZvalue();
			items.rect = myRect;
			items.mGraphics=this;
			
			mRectItem = new RectItem(myRect);
			mRectItem.setAlpha(info.getnAlpha());
			mRectItem.setLineAlpha(info.getnAlpha());
			mRectItem.setBackColor(info.getnBackColor());
			mRectItem.setForeColor(info.getnForeColor());
			mRectItem.setHeight(info.getnHeight());
			mRectItem.setLineColor(info.getnLineColor());
			mRectItem.setLineType(info.geteLineType());
			mRectItem.setLineWidth(info.getnLineWidth());
			mRectItem.setStyle(info.geteStyle());
			mRectItem.setType(info.geteCornerType());
			mRectItem.init();
		}
	}

	private void init() {
		
		if (info==null) {
			return;
		}
		
		SKSceneManage.getInstance().onRefresh(items);
	}

	@Override
	public void initGraphics() {
		init();
	}

	@Override
	public void getDataFromDatabase() {

	}

	@Override
	public void setDataToDatabase() {

	}

	@Override
	public void realseMemeory() {
		
	}

	@Override
	public boolean drawGraphics(Canvas canvas, int itemId) {
		// TODO Auto-generated method stub
		if (null == info) {
			return false;
		}
		if (itemId == info.getId()) {
			if (mRectItem == null) {
				return false;
			}
			mRectItem.draw(mPaint, canvas);
			return true;
		}
		return false;
	}

}