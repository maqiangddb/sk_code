//import SKGraphicsBase;
package com.android.Samkoonhmi.skgraphics.noplc;

import com.android.Samkoonhmi.graphicsdrawframe.RoundRectItem;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.ShapInfo;
import com.android.Samkoonhmi.skgraphics.noplc.base.SKGraphicsBase;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * 圆角矩形
 * 
 * @author Administrator
 * 
 */
public class SKRoundRect extends SKGraphicsBase {
	private ShapInfo info;
	private Paint mPaint;
	private SKItems items;
	private Rect myRect;
	private int itemId;
	private int sceneId;
	private RoundRectItem mRoundRectItem;

	public SKRoundRect(int itemId, int sceneId, ShapInfo info) {
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
			
			mRoundRectItem = new RoundRectItem(myRect);
			mRoundRectItem.setAlpha(info.getnAlpha());
			mRoundRectItem.setBackColor(info.getnBackColor());
			mRoundRectItem.setBendRadiuX(info.getnRadius());
			mRoundRectItem.setBendRadiuY(info.getRoundRectRadiusY());
			mRoundRectItem.setForeColor(info.getnForeColor());
			mRoundRectItem.setLineColor(info.getnLineColor());
			mRoundRectItem.setLineType(info.geteLineType());
			mRoundRectItem.setLineWidth(info.getnLineWidth());
			mRoundRectItem.setStyle(info.geteStyle());
			mRoundRectItem.setWidth(info.getnWidth());
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
		init();

	}

	@Override
	public boolean drawGraphics(Canvas canvas, int itemId) {
		// TODO Auto-generated method stub
		if (null == info) {
			return false;
		}
		if (itemId == info.getId()) {
			if (mRoundRectItem != null) {
				mRoundRectItem.draw(mPaint, canvas);
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