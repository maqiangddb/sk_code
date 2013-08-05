package com.android.Samkoonhmi.skgraphics.noplc;

import java.util.Vector;

import com.android.Samkoonhmi.graphicsdrawframe.CurveArcItem;
import com.android.Samkoonhmi.model.LineInfo;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.skenum.POINT_TYPE;
import com.android.Samkoonhmi.skgraphics.noplc.base.SKGraphicsBase;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

//import SKGraphicsBase;
/**
 * 画曲线圆弧
 * 
 * @author 瞿丽平
 * 
 */
public class SKCurveArc extends SKGraphicsBase {
	private LineInfo line;
	private Vector<Point> listPoint;
	private Vector<POINT_TYPE> pointTypeList;
	private Paint mPaint;
	private SKItems items;
	private Rect mRect;
	private int itemId;
	private int scenId;
	private CurveArcItem mCurveArcItem;

	public SKCurveArc(int itemId, int scenId,LineInfo line) {
		this.scenId = scenId;
		this.itemId = itemId;
		listPoint = new Vector<Point>();
		pointTypeList = new Vector<POINT_TYPE>();
		mPaint = new Paint();
		items = new SKItems();
		this.line=line;
		
		if(line!=null){
			mRect = new Rect(line.getnStartX(), line.getnStartY(),
					line.getnStartX() + line.getnWidth(), line.getnStartY()
							+ line.getnHeight());

			items.itemId = this.itemId;
			items.nCollidindId = line.getnCollidindId();
			items.nZvalue = line.getnZvalue();
			items.sceneId = this.scenId;
			items.rect = mRect;
			items.mGraphics=this;
			
			mCurveArcItem=new CurveArcItem(line.getPointList(), line.getPointTypeList());
			mCurveArcItem.setAlpha(line.getnAlpha());
			mCurveArcItem.setEndArrowType(line.geteLineArrow());
			mCurveArcItem.setLineColor(line.getnLineColor());
			mCurveArcItem.setLineType(line.geteLineType());
			mCurveArcItem.setLineWidth(line.getnLineWidth());
		}

	}

	private void init() {

		if (null == line) {
			return;
		}
		
		SKSceneManage.getInstance().onRefresh(items);
	}

	@Override
	public boolean drawGraphics(Canvas canvas, int itemId) {
		if(null == line)
		{
			return false;
		}
		if (this.itemId == itemId ) {
			if (mCurveArcItem!=null) {
				mCurveArcItem.draw(mPaint, canvas);
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void realseMemeory() {
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
	public void initGraphics() {
		init();

	}
	
}