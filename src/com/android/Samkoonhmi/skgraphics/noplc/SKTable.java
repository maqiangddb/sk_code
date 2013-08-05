//import SKGraphicsBase;
package com.android.Samkoonhmi.skgraphics.noplc;

import java.util.ArrayList;
import java.util.Vector;
import com.android.Samkoonhmi.graphicsdrawframe.LineItem;
import com.android.Samkoonhmi.graphicsdrawframe.RectItem;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.TableModel;
import com.android.Samkoonhmi.skgraphics.noplc.base.SKGraphicsBase;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * 表格控件
 * @author Administrator
 */
public class SKTable extends SKGraphicsBase {
	
	private Paint paint;
	private TableModel table;// 实例化表格
	private Rect totalRect;// 外矩形
	private SKItems skItem;// item
	private int itemId;
	private RectItem mRectItem;
	public SKTable(int itemId, int sceneid, TableModel model) {
		super();
		this.itemId = itemId;
		totalRect = new Rect();
		paint = new Paint();
		skItem = new SKItems();
		table = model;
		if (table != null) {
			totalRect.set((int) table.getnLeftTopX(),
					(int) table.getnLeftTopY(), (int) table.getnLeftTopX()
							+ (int) table.getnTableWidth(),
					(int) table.getnLeftTopY() + (int) table.getnTableHeight());
			skItem.nCollidindId = table.getnCollidindId();
			skItem.nZvalue = table.getnZvalue();
			skItem.rect = totalRect;
			skItem.itemId = itemId;
			skItem.sceneId = sceneid;
			skItem.mGraphics = this;

			Rect rect = this.getFrameLineRect();// 画矩形
			mRectItem = new RectItem(rect);
			mRectItem.setBackColor(table.getnBackColor());
			mRectItem.setAlpha(table.getAlpha());
			mRectItem.setLineAlpha(255);

			// 是否描边
			if (table.isbShowFrameLine()) {
				mRectItem.setLineWidth(table.getnWLineWidth());
				mRectItem.setLineColor(table.getnWShowColor());
			} else {
				mRectItem.setLineWidth(0);
			}
			mRectItem.init();

			// 横线
			if (table.isbShowOrientationLine()) {
				ArrayList<LineItem> items = new ArrayList<LineItem>();
				table.setmRowItems(items);
				double y = table.getnLeftTopY();
				for (int i = 0; i < table.getnRows().size() - 1; i++) {
					y += table.getnRows().get(i);
					Vector<Point> Points = getOrientationLine(y);
					LineItem lineItem = new LineItem(Points);
					lineItem.setLineColor(table.getnNShowColor());
					lineItem.setLineType(table.geteNLineType());
					items.add(lineItem);
				}
			}

			// 画垂直线
			if (table.isbShowPortraitCount()) {
				if (table.getmColumsItems() == null) {
					ArrayList<LineItem> items = new ArrayList<LineItem>();
					table.setmColumsItems(items);
					double x = table.getnLeftTopX();
					for (int i = 0; i < table.getnColums().size() - 1; i++) {
						x += table.getnColums().get(i);
						Vector<Point> Points = getPortraitLine(x);
						LineItem lineItem = new LineItem(Points);
						lineItem.setLineColor(table.getnNShowColor());
						lineItem.setLineType(table.geteNLineType());
						items.add(lineItem);
					}
				}
			}

		}
	}

	@Override
	public void initGraphics() {
		if (table == null) {
			// 数据为空
			return;
		}
		SKSceneManage.getInstance().onRefresh(skItem);
	}

	@Override
	public boolean drawGraphics(Canvas canvas, int itemId) {
		if (null == table) {
			return false;
		}
		if (this.itemId == itemId) {
			drawGraphics(canvas);
			return true;
		}
		// TODO Auto-generated method stub
		return false;
	}

	public void drawGraphics(Canvas canvas) {

		// 画矩形背景
		mRectItem.draw(paint, canvas);

		// 画横向线
		if (table.isbShowOrientationLine()) {
			if (table.getmRowItems() != null) {
				for (int i = 0; i < table.getmRowItems().size(); i++) {
					table.getmRowItems().get(i).draw(paint, canvas);
				}
			}
		}

		// 画垂直线
		if (table.isbShowPortraitCount()) {
			if (table != null) {
				for (int i = 0; i < table.getmColumsItems().size(); i++) {
					table.getmColumsItems().get(i).draw(paint, canvas);
				}
			}
		}
	}

	/**
	 * 画边框矩形
	 * @return
	 */
	private Rect getFrameLineRect() {
		Rect rect = new Rect((int) table.getnLeftTopX(),
				(int) table.getnLeftTopY(), (int) table.getnLeftTopX()
						+ table.getnTableWidth(), (int) table.getnLeftTopY()
						+ table.getnTableHeight());
		return rect;
	}

	/**
	 * 得到外边框线条
	 */
	private Vector<Point> getWLine() {
		Vector<Point> pointList = new Vector<Point>();
		return pointList;
	}

	/**
	 * 得到横向的线条
	 */
	private Vector<Point> getOrientationLine(double y) {
		Vector<Point> pointList = new Vector<Point>();
		Point mPoint = new Point();
		mPoint.x = (int) table.getnLeftTopX();
		// if(table.getnOrientationCount()<5)
		// mPoint.y=(int)(y+2);
		// else
		// mPoint.y=(int)(y+4);
		mPoint.y = (int) (y);
		pointList.add(mPoint);

		Point mPoint1 = new Point();
		mPoint1.x = (int) table.getnLeftTopX() + table.getnTableWidth();
		// if(table.getnOrientationCount()<5)
		// mPoint1.y=(int)(y+2);
		// else
		// mPoint1.y=(int)(y+4);
		mPoint1.y = (int) (y);
		pointList.add(mPoint1);

		return pointList;

	}

	/**
	 * 得到纵向的线条
	 * @return
	 */
	private Vector<Point> getPortraitLine(double x) {
		Vector<Point> pointList = new Vector<Point>();
		Point mPoint = new Point();
		// if(table.getnPortraitCount()<5)
		// mPoint.x=(int)(x+2);
		// else
		// mPoint.x=(int)(x+4);
		mPoint.x = (int) (x);
		mPoint.y = (int) table.getnLeftTopY();
		pointList.add(mPoint);

		Point mPoint1 = new Point();
		// if(table.getnPortraitCount()<5)
		// mPoint1.x=(int)(x+2);
		// else
		// mPoint1.x=(int)(x+4);
		mPoint1.x = (int) (x);
		mPoint1.y = (int) table.getnLeftTopY() + table.getnTableHeight();
		pointList.add(mPoint1);
		return pointList;
	}

	/**
	 * 从数据库中读取数据
	 */
	@Override
	public void getDataFromDatabase() {
		// TODO Auto-generated method stub
	}

	/**
	 * 设置数据库中的值
	 */
	@Override
	public void setDataToDatabase() {
		// TODO Auto-generated method stub

	}

	/**
	 * 清空所有属性
	 */
	@Override
	public void realseMemeory() {

	}

}