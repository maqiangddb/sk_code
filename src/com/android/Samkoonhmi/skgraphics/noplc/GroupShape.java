//import SKGraphicsBase;
package com.android.Samkoonhmi.skgraphics.noplc;
import com.android.Samkoonhmi.graphicsdrawframe.ImageDrawItem;
import com.android.Samkoonhmi.model.GroupShapeModel;
import com.android.Samkoonhmi.model.IItem;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.skgraphics.noplc.base.SKGraphicsBase;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.ImageFileTool;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * 组合图形
 * 
 * @author Administrator
 * 
 */
public class GroupShape extends SKGraphicsBase {
	private GroupShapeModel info;
	private Paint mPaint;
	private SKItems items;
	private Rect myRect;
	private int itemId;
	private Bitmap mBitmap;

	public GroupShape(int itemId,int secneId,GroupShapeModel info) {
		this.itemId = itemId;
		mPaint = new Paint();
		items = new SKItems();
		this.info=info;
		if (info!=null) {
			myRect = new Rect(info.getnLp(), info.getnTp(),
					info.getnLp() + info.getnWidth(), info.getnTp()
							+ info.getnHeight());

			items.itemId = this.itemId;
			items.sceneId=secneId;
			items.nCollidindId = info.getnCollidindId();
			items.nZvalue = info.getnZvalue();
			items.rect = myRect;
			items.mGraphics=this;
			mBitmap=ImageFileTool.getBitmap(info.getsPath());
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
		init();
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
		
	}
	
	

	@Override
	public boolean drawGraphics(Canvas canvas, int itemId) {
		// TODO Auto-generated method stub
		if(null == info)
		{
			return false;
		}
		if (itemId==info.getnItemId() ) {
			if (mBitmap!=null) {
				canvas.drawBitmap(mBitmap, info.getnLp(), info.getnTp(), null);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean isShow() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public IItem getIItem() {
		// TODO Auto-generated method stub
		return null;
	}
	
}