//import SKGraphCmnShow;
package com.android.Samkoonhmi.skgraphics.noplc;

import com.android.Samkoonhmi.graphicsdrawframe.SectorItem;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.ShapInfo;
import com.android.Samkoonhmi.skgraphics.noplc.base.SKGraphicsBase;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * 扇形
 * 
 * @author 瞿丽平
 * 
 */
public class SKSector extends SKGraphicsBase {
	private ShapInfo info;
	private Paint mPaint;
	private SKItems items;
	private Rect myRect;
	private int itemId;
	private int sceneId;
	private SectorItem mSectorItem;

	public SKSector(int itemId, int sceneId, ShapInfo info) {
		this.sceneId = sceneId;
		this.itemId = itemId;
		mPaint = new Paint();
		items = new SKItems();
		this.info = info;
		
		if (info!=null) {
			myRect = new Rect(info.getnPointX(), info.getnPointY(),
					info.getnPointX() + 2 * info.getnRadius(), info.getnPointY()
							+ 2 * info.getnRadius());
			items.itemId = this.itemId;
			items.nCollidindId = info.getnCollidindId();
			items.nZvalue = info.getnZvalue();
			items.sceneId = this.sceneId;
			items.rect = myRect;
			items.mGraphics=this;
			
			mSectorItem = new SectorItem(myRect);
			mSectorItem.setAlpha(info.getnAlpha());
			mSectorItem.setBackColor(info.getnBackColor());
			mSectorItem.setForeColor(info.getnForeColor());
			mSectorItem.setLineColor(info.getnLineColor());
			mSectorItem.setLineType(info.geteLineType());
			mSectorItem.setLineWidth(info.getnLineWidth());
			mSectorItem.setSpanAngle(info.getnHeight());
			mSectorItem.setStartAngle(info.getnWidth());
			mSectorItem.setStyle(info.geteStyle());
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
		// TODO Auto-generated method stub
		if (null == info) {
			return false;
		}
		if (this.itemId == itemId) {
			if (mSectorItem != null) {
				mSectorItem.draw(mPaint, canvas);
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