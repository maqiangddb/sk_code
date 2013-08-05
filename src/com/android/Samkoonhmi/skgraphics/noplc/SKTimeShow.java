//import ITimerUpdate;
//import SKGraphicsBase;
package com.android.Samkoonhmi.skgraphics.noplc;

import java.util.Date;

import com.android.Samkoonhmi.SKThread;
import com.android.Samkoonhmi.databaseinterface.DBTool;
import com.android.Samkoonhmi.databaseinterface.TimeShowBiz;
import com.android.Samkoonhmi.graphicsdrawframe.ImageDrawItem;
import com.android.Samkoonhmi.graphicsdrawframe.TextItem;
import com.android.Samkoonhmi.model.DateTimeShowInfo;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.StaticTextModel;
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
public class SKTimeShow extends SKGraphicsBase {
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
	private boolean initFlag;
	private boolean regiter;
	private ImageDrawItem imageItem;
	private static  final String  UPDATETIME = "com.samkoon.settime";

	public SKTimeShow(int itemId, int sceneId, Context context,DateTimeShowInfo info) {
		initFlag = true;
		this.sceneId = sceneId;
		this.itemId = itemId;
		mPaint = new Paint();
		items = new SKItems();
		mContext = context;
		this.info=info;
		
		if (info!=null) {
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
			text.setM_alphaPadding(info.getnTransparent());//设置透明度
			text.setM_sFontFamly(info.getsFontStyle());
//			if(info.getnTransparent() == 0)
//			{
//				text.setBorderAlpha(255);
//				text.setLineColor(Color.BLACK);
//				text.setLineWidth(1);
//			}
			
			textItem = new TextItem(text);
			textItem.initTextPaint();
			textItem.initRectBoderPaint();
			textItem.initRectPaint();
			
			items.itemId = this.itemId;
			items.nCollidindId = info.getnCollidindId();
			items.nZvalue = info.getnZvalue();
			items.sceneId = sceneId;
			items.rect = mRect;
			items.mGraphics=this;
			
			imageItem = new ImageDrawItem(info.getnShapId(), mRect);
		}

	}

	private void init() {
		if (null == info) {
			return;
		}
		initFlag = true;
		
		flag = true;

		mFlag = true;
		broadCast = new BroadCast();
		mFilter = new IntentFilter();
		mFilter.addAction(Intent.ACTION_TIME_TICK); // 更新分钟的广播
		mFilter.addAction(UPDATETIME);//按钮设置系统时间的广播
		mContext.registerReceiver(broadCast, mFilter);// 注册广播
		regiter = true;
		 //时间格式选择了秒钟的 就启动秒表线程
		if(info.geteShowTime()== TIME_FORMAT.HHMMSS_ACROSS || info.geteShowTime() == TIME_FORMAT.HHMMSS_COLON)
		{
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
			draw(mPaint, canvas);
			initFlag = true;
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
			}else if(intent.getAction() == UPDATETIME)
			{
				SKSceneManage.getInstance().onRefresh(items);
			}
		}

	}

	private void draw(Paint paint, Canvas canvas) {

		// 外形不为空画外形
		if (null != info.getnShapId()) {
			if (imageItem!=null) {
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
//			Log.d("SKScene", "broadCast:" + broadCast);
			if (regiter) {
				mContext.unregisterReceiver(broadCast);
				regiter = false;
			}
		}
		mFlag = false;
//		initFlag = true;
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

}
