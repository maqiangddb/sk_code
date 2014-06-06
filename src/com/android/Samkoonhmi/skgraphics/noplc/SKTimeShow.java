//import ITimerUpdate;
//import SKGraphicsBase;
package com.android.Samkoonhmi.skgraphics.noplc;

import java.util.Date;
import java.util.Vector;

import com.android.Samkoonhmi.SKThread;
import com.android.Samkoonhmi.databaseinterface.DBTool;
import com.android.Samkoonhmi.databaseinterface.TimeShowBiz;
import com.android.Samkoonhmi.graphicsdrawframe.ImageDrawItem;
import com.android.Samkoonhmi.graphicsdrawframe.TextItem;
import com.android.Samkoonhmi.model.DateTimeShowInfo;
import com.android.Samkoonhmi.model.IItem;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.StaticTextModel;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skenum.TEXT_PIC_ALIGN;
import com.android.Samkoonhmi.skenum.TIME_FORMAT;
import com.android.Samkoonhmi.skgraphics.noplc.base.SKGraphicsBase;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.DateStringUtil;
import com.android.Samkoonhmi.util.MODULE;
import com.android.Samkoonhmi.util.TASK;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

/**
 * 日期时间显示器
 * 
 * @author 瞿丽平
 * 
 */
public class SKTimeShow extends SKGraphicsBase implements IItem {
	private DateTimeShowInfo info;
	private Rect mRect;
	private StaticTextModel text;
	private Paint mPaint;
	private String showValue;
	private BroadCast broadCast;
	private TextItem textItem;
	private IntentFilter mFilter;
	private clockThread clock = null;
	private SKItems items;
	private int itemId;
	public static boolean mFlag = true; // 启动秒钟显示线程
	private int sceneId;
	private boolean flag;
	private Context mContext;
	private boolean regiter;
	private ImageDrawItem imageItem;
	private static final String UPDATETIME = "com.samkoon.settime";
	private boolean isShowFlag;
	private boolean showByUser;
	private boolean showByAddr;
	private int currentColor = 0;

	public SKTimeShow(int itemId, int sceneId, Context context,
			DateTimeShowInfo info) {
		this.sceneId = sceneId;
		this.itemId = itemId;
		mPaint = new Paint();
		items = new SKItems();
		mContext = context;
		this.info = info;
		isShowFlag = true;
		showByUser = false;
		showByAddr = false;
		if (info != null) {
			mRect = new Rect();
			mRect.left = info.getnStartX();
			mRect.right = info.getnStartX() + info.getnWidth();
			mRect.top = info.getnStartY();
			mRect.bottom = info.getnStartY() + info.getnHeight();

			text = new StaticTextModel();
			text.setM_backColorPadding(info.getnBackground());
			text.setM_eTextAlign(TEXT_PIC_ALIGN.CENTER);
			text.setM_nFontColor(info.getnFontColor());
			text.setM_nFontSize(info.getnFontSize());
			text.setM_textLanguageId(1);
			text.setM_textPro((short) (info.geteFontCss()));
			text.setStartX(info.getnTextStartX());
			text.setStartY(info.getnTextStartY());
			text.setRectHeight(info.getnTextHeight());
			text.setRectWidth(info.getnTextWidth());
			text.setM_alphaPadding(info.getnTransparent());// 设置透明度
			text.setM_sFontFamly(info.getsFontStyle());
			// if(info.getnTransparent() == 0)
			// {
			// text.setBorderAlpha(255);
			// text.setLineColor(Color.BLACK);
			// text.setLineWidth(1);
			// }

			textItem = new TextItem(text);
			textItem.initTextPaint();
			textItem.initRectBoderPaint();
			textItem.initRectPaint();

			items.itemId = this.itemId;
			items.nCollidindId = info.getnCollidindId();
			items.nZvalue = info.getnZvalue();
			items.sceneId = sceneId;
			items.rect = mRect;
			items.mGraphics = this;

			imageItem = new ImageDrawItem(info.getnShapId(), mRect);

			if (null != info.getShowInfo()) {
				if (-1 != info.getShowInfo().getnAddrId()
						&& info.getShowInfo().isbShowByAddr()) {
					showByAddr = true;
				}
				if (info.getShowInfo().isbShowByUser()) {
					showByUser = true;
				}
			}
			// 注册显现地址值
			registAddr();
		}

	}

	private void init() {
		if (null == info) {
			return;
		}
		flag = true;
		mFlag = true;

		timeIsShow();

		broadCast = new BroadCast();
		mFilter = new IntentFilter();
		mFilter.addAction(Intent.ACTION_TIME_TICK); // 更新分钟的广播
		mFilter.addAction(UPDATETIME);// 按钮设置系统时间的广播
		mContext.registerReceiver(broadCast, mFilter);// 注册广播
		regiter = true;
		// 时间格式选择了秒钟的 就启动秒表线程
		if (info.geteShowTime() == TIME_FORMAT.HHMMSS_ACROSS
				|| info.geteShowTime() == TIME_FORMAT.HHMMSS_COLON) {
			clock = new clockThread();
			clock.start();// 启动刷新秒钟的线程
		}

		SKSceneManage.getInstance().onRefresh(items);

	}

	public void updateStatus() {
		// TODO put your implementation here.
	}

	@Override
	public void initGraphics() {

		init();

	}

	@Override
	public boolean drawGraphics(Canvas canvas, int itemId) {
		if (null == info) {
			return false;
		}
		if (this.itemId == itemId) {
			// TODO Auto-generated method stub
			if (isShowFlag) {
				draw(mPaint, canvas);
			}
			return true;
		}
		return false;

	}

	/**
	 * 更新日期和分钟的广播
	 * 
	 * @author Administrator
	 * 
	 */
	class BroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction() == Intent.ACTION_TIME_TICK) {
				SKSceneManage.getInstance().onRefresh(items);
			} else if (intent.getAction() == UPDATETIME) {
				SKSceneManage.getInstance().onRefresh(items);
			}
		}

	}

	private void draw(Paint paint, Canvas canvas) {

		// 外形不为空画外形
		if (null != info.getnShapId()) {
			if (imageItem != null) {
				imageItem.draw(paint, canvas);
			}
		}
		// 画文本
		drawTextString(paint, canvas);
	}

	/**
	 * 画文本
	 * 
	 * @param paint
	 * @param canvas
	 */
	private void drawTextString(Paint paint, Canvas canvas) {
		showValue = getShowValue();
		text.setM_sTextStr(showValue);
		textItem.draw(canvas);
	}

	@Override
	public void getDataFromDatabase() {

	}

	/**
	 * 得到要显示的日期内容
	 */
	private String getShowValue() {
		StringBuffer stringBuffer = new StringBuffer();
		if (null != info.geteShowDate()) {
			String date = DateStringUtil.convertDate(info.geteShowDate(),
					new Date());
			stringBuffer.append(date);
		}
		if (null != info.geteShowWeek()) {
			String week = DateStringUtil.converWeek(info.geteShowWeek(),
					new Date());
			if (null == info.geteShowDate()) {
				stringBuffer.append(week);
			} else {
				stringBuffer.append("   " + week);
			}
		}
		if (null != info.geteShowTime()) {
			String time = DateStringUtil.converTime(info.geteShowTime(),
					new Date());
			if (null == info.geteShowWeek() && null == info.geteShowDate()) {
				stringBuffer.append(time);
			} else {
				stringBuffer.append("   " + time);
			}
		}
		showValue = stringBuffer.toString();
		return showValue;
	}

	@Override
	public void setDataToDatabase() {
		// TODO Auto-generated method stub

	}

	@Override
	public void realseMemeory() {
		// TODO Auto-generated method stub
		// 注销广播
		if (null != broadCast) {
			if (regiter) {
				mContext.unregisterReceiver(broadCast);
				regiter = false;
			}
		}
		mFlag = false;
	}

	/**
	 * 刷新秒钟的线程
	 * 
	 * @author Administrator
	 * 
	 */
	private class clockThread extends Thread {
		@Override
		public void run() {

			while (mFlag) {

				SKSceneManage.getInstance().onRefresh(items);
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					return;
				}
			}

		}
	}

	@Override
	public boolean isShow() {
		timeIsShow();
		SKSceneManage.getInstance().onRefresh(items);
		return isShowFlag;

	}

	private void timeIsShow() {
		if (showByAddr || showByUser) {
			isShowFlag = popedomIsShow(info.getShowInfo());
		}
	}

	private void registAddr() {
		// 注册显现地址值
		if (showByAddr && null != info.getShowInfo().getShowAddrProp()) {
			ADDRTYPE addrType = info.getShowInfo().geteAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				SKPlcNoticThread.getInstance().addNoticProp(
						info.getShowInfo().getShowAddrProp(), showCall, true,
						sceneId);
			} else {
				SKPlcNoticThread.getInstance().addNoticProp(
						info.getShowInfo().getShowAddrProp(), showCall, false,
						sceneId);
			}

		}
	}

	/**
	 * 显现地址值改变通知
	 */
	SKPlcNoticThread.IPlcNoticCallBack showCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			isShow();
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
		if (info != null) {
			return info.getnStartX();
		}
		return -1;
	}

	@Override
	public int getItemTop(int id) {
		// TODO Auto-generated method stub
		if (info != null) {
			return info.getnStartY();
		}
		return -1;
	}

	@Override
	public int getItemWidth(int id) {
		// TODO Auto-generated method stub
		if (info != null) {
			return info.getnWidth();
		}
		return -1;
	}

	@Override
	public int getItemHeight(int id) {
		// TODO Auto-generated method stub
		if (info != null) {
			return info.getnHeight();
		}
		return -1;
	}

	@Override
	public short[] getItemForecolor(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public short[] getItemBackcolor(int id) {
		// TODO Auto-generated method stub
		if (info != null) {
			return getColor(info.getnBackground());
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
		// TODO Auto-generated method stubZ
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
		if (info != null) {
			if (x == info.getnStartX()) {
				return true;
			}
			if (x < 0
					|| x > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneWidth()) {
				return false;
			}
			int l = items.rect.left;
			info.setnStartX(x);
			items.rect.left = x;
			items.rect.right = x - l + items.rect.right;
			items.mMoveRect = new Rect();
			text.setStartX(x);
			items.mMoveRect = new Rect();
			SKSceneManage.getInstance().onRefresh(items);
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean setItemTop(int id, int y) {
		// TODO Auto-generated method stub
		if (info != null) {
			if (y == info.getnStartX()) {
				return true;
			}
			if (y < 0
					|| y > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneHeight()) {
				return false;
			}
			info.setnStartY(y);
			int t = items.rect.top;
			items.rect.top = y;
			items.rect.bottom = y - t + items.rect.bottom;
			items.mMoveRect = new Rect();
			text.setStartY(y);
			SKSceneManage.getInstance().onRefresh(items);
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean setItemWidth(int id, int w) {
		// TODO Auto-generated method stub
		if (info != null) {
			if (w == info.getnWidth()) {
				return true;
			}
			if (w < 0
					|| w > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneWidth()) {
				return false;
			}
			info.setnWidth((short) w);
			items.rect.right = w - items.rect.width() + items.rect.right;
			items.mMoveRect = new Rect();
			text.setRectWidth(items.rect.width());
			SKSceneManage.getInstance().onRefresh(items);
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean setItemHeight(int id, int h) {
		// TODO Auto-generated method stub
		if (info != null) {
			if (h == info.getnHeight()) {
				return true;
			}
			if (h < 0
					|| h > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneHeight()) {
				return false;
			}
			info.setnHeight((short) h);
			items.rect.bottom = h - items.rect.height() + items.rect.bottom;
			items.mMoveRect = new Rect();
			text.setRectHeight(items.rect.height());
			SKSceneManage.getInstance().onRefresh(items);
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean setItemForecolor(int id, short r, short g, short b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setItemBackcolor(int id, short r, short g, short b) {
		// TODO Auto-generated method stub
		if (info != null) {
			int color = Color.rgb(r, g, b);
			if (color == info.getnBackground()) {
				return true;
			}
			info.setnBackground(color);
			currentColor=color;
			text.setM_backColorPadding(color);
			textItem.resetColor(color, 2);
			SKSceneManage.getInstance().onRefresh(items);
			return true;
		}
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
		SKSceneManage.getInstance().onRefresh(items);
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
		if (info==null||alpha<0||alpha>255) {
			return false;
		}
		if (info.getnTransparent()==alpha) {
			return true;
		}
		info.setnTransparent(alpha);
		text.setM_alphaPadding(alpha);// 设置透明度
		textItem.resetAlpha(alpha);
		SKSceneManage.getInstance().onRefresh(items);
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
