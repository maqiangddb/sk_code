//import SKGraphCmnShow;
package com.android.Samkoonhmi.skgraphics.plc.show;

import java.util.Vector;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import com.android.Samkoonhmi.SKThread;
import com.android.Samkoonhmi.databaseinterface.DBTool;
import com.android.Samkoonhmi.databaseinterface.StaticTextBiz;
import com.android.Samkoonhmi.graphicsdrawframe.ImageDrawItem;
import com.android.Samkoonhmi.graphicsdrawframe.TextItem;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.StaticTextModel;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.model.TextInfo;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skenum.TEXT_PIC_ALIGN;
import com.android.Samkoonhmi.skgraphics.plc.show.base.SKGraphCmnShow;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.MODULE;
import com.android.Samkoonhmi.util.SKLanguage;
import com.android.Samkoonhmi.util.TASK;
import com.android.Samkoonhmi.util.TextAttribute;

/**
 * 静态文本
 * 
 * @author Eisen
 * 
 */
public class SKStaticText extends SKGraphCmnShow {

	private StaticTextModel text;
	private Paint paint;
	private boolean flag;
	private Rect totalRect;// 外矩形
	private SKItems skItem;// item
	private int sceneid;
	private int itemId;
	private boolean isShowFlag;
	private boolean showByUser;
	private boolean showByAddr;
	private int nLanId;// 语言id

	/**
	 * 构造方法
	 */
	public SKStaticText(int sceneid, int itemId, StaticTextModel text) {
		super();
		this.sceneid = sceneid;
		this.itemId = itemId;
		totalRect = new Rect();
		paint = new Paint();
		skItem = new SKItems();
		flag = true;
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
			// 初始化画笔
			 setPaint();
		}

	}

	public void init() {
		if (text == null) {
			// 数据为空
			return;
		}
		flag = true;

		if (SystemInfo.getCurrentLanguageId() != nLanId) {
			setPaint();
		}
		if (null != text.getShowInfo()) {
			if (null != text.getShowInfo().getShowAddrProp()) {
				showByAddr = true;
			}
			if (text.getShowInfo().isbShowByUser()) {
				showByUser = true;
			}
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
	
		SKLanguage.getInstance().getBinder().onRegister(lCallback);
		if (showByAddr) {
			ADDRTYPE addrType = text.getShowInfo().geteAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				SKPlcNoticThread.getInstance().addNoticProp(
						text.getShowInfo().getShowAddrProp(), showCall, true);
			} else {
				SKPlcNoticThread.getInstance().addNoticProp(
						text.getShowInfo().getShowAddrProp(), showCall, false);
			}
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

	private ImageDrawItem item;

	public void drawGraphics(Canvas mcanvas) {
		// 清空所有属性
		// 调用方法
		item = new ImageDrawItem(text.getM_sTextStr(), totalRect);
		item.draw(paint, mcanvas);
	}

	private void setPaint() {

		// 多语言
		
			if (text.getmTextList() != null) {
				for (int i = 0; i < text.getmTextList().size(); i++) {
					TextInfo tInfo = text.getmTextList().get(i);
					// 文本图片地址
					if (tInfo.getmTextList().size() > 0) {
						if (tInfo.getmTextList().size() > SystemInfo
								.getCurrentLanguageId()) {
							nLanId = SystemInfo.getCurrentLanguageId();
							String str = "";
							if(text.isM_fristLanguage()){ //虽然有多语言 但是静态文本设置了所有都与第一种语言相同
								 str = tInfo.getmTextList().get(
											0);
							}else{
								str = tInfo.getmTextList().get(
										SystemInfo.getCurrentLanguageId());
							}
							if (!str.equals("")) {
								text.setM_sTextStr(str);
							} else {
								text.setM_sTextStr(" ");
							}
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
		// flag=true;
		/* 注销通知接口 */
		SKPlcNoticThread.getInstance().destoryCallback(showCall);// 显现
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
			// isFlick = false;
			 setPaint();
			SKSceneManage.getInstance().onRefresh(skItem);

		}
	};
}