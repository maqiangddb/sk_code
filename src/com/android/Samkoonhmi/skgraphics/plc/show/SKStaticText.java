//import SKGraphCmnShow;
package com.android.Samkoonhmi.skgraphics.plc.show;

import java.util.ArrayList;
import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import com.android.Samkoonhmi.graphicsdrawframe.ImageDrawItem;
import com.android.Samkoonhmi.model.IItem;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.StaticTextModel;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.model.TextInfo;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skgraphics.plc.show.base.SKGraphCmnShow;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.ImageFileTool;
import com.android.Samkoonhmi.util.SKLanguage;

/**
 * 静态文本
 * 
 * @author Eisen
 */
public class SKStaticText extends SKGraphCmnShow implements IItem {

	private StaticTextModel text;
	private Paint paint;
	private Rect totalRect;// 外矩形
	private SKItems skItem;// item
	private int itemId;
	private boolean isShowFlag;
	private boolean showByUser;
	private boolean showByAddr;
	private ArrayList<Bitmap> mTextPicList;

	/**
	 * 构造方法
	 */
	public SKStaticText(int sceneid, int itemId, StaticTextModel text) {
		super();
		this.itemId = itemId;
		totalRect = new Rect();
		paint = new Paint();
		skItem = new SKItems();
		isShowFlag = true;
		showByUser = false;
		showByAddr = false;
		this.text = text;

		if (text != null) {
			totalRect.set(text.getStartX(), text.getStartY(),
					(int) (text.getStartX() + text.getRectWidth()),
					(int) (text.getStartY() + text.getRectHeight()));
			skItem.nCollidindId = text.getnCollidindId();
			skItem.nZvalue = text.getnZvalue();
			skItem.rect = totalRect;
			skItem.sceneId = sceneid;
			skItem.itemId = itemId;
			skItem.mGraphics = this;
			mTextPicList = new ArrayList<Bitmap>();
			// 初始化画笔
			setPaint();

			if (null != text.getShowInfo()) {
				if (null != text.getShowInfo().getShowAddrProp()) {
					showByAddr = true;
				}
				if (text.getShowInfo().isbShowByUser()) {
					showByUser = true;
				}
			}

			if (showByAddr) {
				ADDRTYPE addrType = text.getShowInfo().geteAddrType();
				if (addrType == ADDRTYPE.BITADDR) {
					SKPlcNoticThread.getInstance().addNoticProp(
							text.getShowInfo().getShowAddrProp(), showCall,
							true, sceneid);
				} else {
					SKPlcNoticThread.getInstance().addNoticProp(
							text.getShowInfo().getShowAddrProp(), showCall,
							false, sceneid);
				}
			}
		}

	}

	public void init() {
		if (text == null) {
			// 数据为空
			return;
		}

		// 注册地址
		registerAddr();
		// 初始化显现标志
		textIsShow();

		SKSceneManage.getInstance().onRefresh(skItem);
	}

	private void registerAddr() {
		// TODO Auto-generated method stub
		// 注册语言改变通知
		if (SystemInfo.getLanguageNumber() > 1) {
			SKLanguage.getInstance().getBinder().onRegister(lCallback);
		}
	}

	/**
	 * 是否显示控件
	 */
	@Override
	public boolean isShow() {
		// TODO Auto-generated method stub
		textIsShow();
		SKSceneManage.getInstance().onRefresh(skItem);
		return isShowFlag;
	}

	@Override
	public boolean drawGraphics(Canvas canvas, int itemId) {
		if (this.itemId == itemId && (null != text)) {
			if (isShowFlag) {
				drawGraphics(canvas);
			}
			return true;
		}
		return false;
	}

	public void drawGraphics(Canvas mcanvas) {
		if (mTextPicList.size() > SystemInfo.getCurrentLanguageId()) {
			Bitmap bitmap = mTextPicList.get(SystemInfo.getCurrentLanguageId());
			if (bitmap != null) {
				mcanvas.drawBitmap(bitmap, text.getStartX(), text.getStartY(),
						paint);
			}
		}
	}

	private void setPaint() {

		// 多语言
		if (text.getmTextList() != null) {
			if (text.getmTextList().size() > 0) {
				TextInfo tInfo = text.getmTextList().get(0);
				if (tInfo.getmTextList() != null) {
					for (int i = 0; i < tInfo.getmTextList().size(); i++) {
						String name = tInfo.getmTextList().get(i);
						Bitmap mBitmap = ImageFileTool.getBitmap(name);
						mTextPicList.add(mBitmap);
					}
				}
			}
		}
	}

	/**
	 * 释放内存，将所有属性清空
	 */
	@Override
	public void realseMemeory() {

	}

	/**
	 * 从数据库中得到数据
	 */
	@Override
	public void getDataFromDatabase() {

	}

	/**
	 * 设置数据到数据库中
	 */
	@Override
	public void setDataToDatabase() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initGraphics() {
		// TODO Auto-generated method stub
		init();
	}

	/**
	 * 显现地址值改变通知
	 */
	SKPlcNoticThread.IPlcNoticCallBack showCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			isShowFlag = isShow();
		}
	};

	private void textIsShow() {
		if (showByAddr || showByUser) {
			isShowFlag = popedomIsShow(text.getShowInfo());
		}
	}

	/**
	 * 语言改变回调
	 */
	SKLanguage.ICallback lCallback = new SKLanguage.ICallback() {

		@Override
		public void onLanguageChange(int languageId) {
			SKSceneManage.getInstance().onRefresh(skItem);
		}
	};

	@Override
	public IItem getIItem() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public int getItemLeft(int id) {
		// TODO Auto-generated method stub
		if (text != null) {
			return text.getStartX();
		}
		return -1;
	}

	@Override
	public int getItemTop(int id) {
		// TODO Auto-generated method stub
		if (text != null) {
			return text.getStartY();
		}
		return -1;
	}

	@Override
	public int getItemWidth(int id) {
		// TODO Auto-generated method stub
		if (text != null) {
			return (int) text.getRectWidth();
		}
		return -1;
	}

	@Override
	public int getItemHeight(int id) {
		// TODO Auto-generated method stub
		if (text != null) {
			return (int) text.getRectHeight();
		}
		return -1;
	}

	@Override
	public short[] getItemForecolor(int id) {
		// TODO Auto-generated method stub
		if (text != null) {
			return getColor(text.getM_foreColorPadding());
		}
		return null;
	}

	@Override
	public short[] getItemBackcolor(int id) {
		// TODO Auto-generated method stub
		if (text != null) {
			return getColor(text.getM_backColorPadding());
		}
		return null;
	}

	@Override
	public short[] getItemLineColor(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getItemVisible(int id) {
		// TODO Auto-generated method stub
		return isShowFlag;
	}

	@Override
	public boolean getItemTouchable(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setItemLeft(int id, int x) {
		// TODO Auto-generated method stub
		if (text != null) {
			if (x == text.getStartX()) {
				return true;
			}
			if (x < 0
					|| x > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneWidth()) {
				return false;
			}
			text.setStartX(x);
			int l = skItem.rect.left;
			skItem.rect.left = x;
			skItem.rect.right = x - l + skItem.rect.right;
			skItem.mMoveRect = new Rect();
			SKSceneManage.getInstance().onRefresh(skItem);
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean setItemTop(int id, int y) {
		// TODO Auto-generated method stub
		if (text != null) {
			if (y == text.getStartY()) {
				return true;
			}
			if (y < 0
					|| y > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneHeight()) {
				return false;
			}
			text.setStartY(y);
			int t = skItem.rect.top;
			skItem.rect.top = y;
			skItem.rect.bottom = y - t + skItem.rect.bottom;
			skItem.mMoveRect = new Rect();
			SKSceneManage.getInstance().onRefresh(skItem);
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean setItemWidth(int id, int w) {
		// TODO Auto-generated method stub
		if (text != null) {
			if (w == text.getRectWidth()) {
				return true;
			}
			if (w < 0
					|| w > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneWidth()) {
				return false;
			}
			text.setRectWidth(w);
			skItem.rect.right = w - skItem.rect.width() + skItem.rect.right;
			skItem.mMoveRect = new Rect();
			SKSceneManage.getInstance().onRefresh(skItem);
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean setItemHeight(int id, int h) {
		// TODO Auto-generated method stub
		if (text != null) {
			if (h == text.getRectHeight()) {
				return true;
			}
			if (h < 0
					|| h > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneHeight()) {
				return false;
			}
			text.setRectHeight(h);
			skItem.rect.bottom = h - skItem.rect.height() + skItem.rect.bottom;
			skItem.mMoveRect = new Rect();
			SKSceneManage.getInstance().onRefresh(skItem);
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean setItemForecolor(int id, short r, short g, short b) {
		// TODO Auto-generated method stub
		if (text == null) {
			return false;
		}
		int color = Color.rgb(r, g, b);
		if (color == text.getM_foreColorPadding()) {
			return true;
		}
		text.setM_foreColorPadding(color);
		SKSceneManage.getInstance().onRefresh(skItem);
		return false;
	}

	@Override
	public boolean setItemBackcolor(int id, short r, short g, short b) {
		// TODO Auto-generated method stub
		if (text == null) {
			return false;
		}

		int color = Color.rgb(r, g, b);

		if (color == text.getM_backColorPadding()) {
			return true;
		}
		text.setM_backColorPadding(color);
		SKSceneManage.getInstance().onRefresh(skItem);
		return false;
	}

	@Override
	public boolean setItemLineColor(int id, short r, short g, short b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setItemVisible(int id, boolean v) {
		// TODO Auto-generated method stub
		if (v == isShowFlag) {
			return true;
		}
		isShowFlag = v;
		SKSceneManage.getInstance().onRefresh(skItem);
		return true;
	}

	@Override
	public boolean setItemTouchable(int id, boolean v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setItemPageUp(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setItemPageDown(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setItemFlick(int id, boolean v, int time) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setItemHroll(int id, int w) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setItemVroll(int id, int h) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setGifRun(int id, boolean v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setItemText(int id, int lid, String text) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setItemAlpha(int id, int alpha) {
		// TODO Auto-generated method stub
		if (text == null || alpha < 0 || alpha > 255) {
			return false;
		}
		if (text.getM_alphaPadding() == alpha) {
			return true;
		}
		text.setM_alphaPadding(alpha);
		SKSceneManage.getInstance().onRefresh(skItem);
		return false;
	}

	@Override
	public boolean setItemStyle(int id, int style) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 颜色取反
	 */
	private short[] getColor(int color) {
		short[] c = new short[3];
		c[0] = (short) ((color >> 16) & 0xFF); // RED
		c[1] = (short) ((color >> 8) & 0xFF);// GREEN
		c[2] = (short) (color & 0xFF);// BLUE
		return c;
	}
}