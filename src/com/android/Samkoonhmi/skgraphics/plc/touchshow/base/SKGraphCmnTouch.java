//import SKGraphicsCmn;
//import boolean;
//import uchar;
package com.android.Samkoonhmi.skgraphics.plc.touchshow.base;

import java.util.Vector;

import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.READ_WRITE_COM_TYPE;
import com.android.Samkoonhmi.skgraphics.plc.SKGraphicsCmn;
import com.android.Samkoonhmi.util.AddrProp;
import com.android.Samkoonhmi.util.SEND_DATA_STRUCT;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.renderscript.Element.DataType;
import android.view.MotionEvent;
import android.view.View;

/**
 * 与plc通讯，既有触碰，又有显示功能的控件基类
 * 
 * @author Administrator
 * 
 */
public abstract class SKGraphCmnTouch extends SKGraphicsCmn {
	public SKGraphCmnTouch() {
		// TODO Auto-generated constructor stub
	}

	protected boolean m_bCanTouch;

	public void addrNoticStatus(double nStatus) {
		// TODO put your implementation here.
	}

	public void setTouchVioce(boolean bBeep) {
		// TODO put your implementation here.
	}

	public void addrNoticInvalidFlag(boolean bTouch) {
		// TODO put your implementation here.
	}

	public void userLevelNoticInvalidFlag(boolean bTouch) {
		// TODO put your implementation here.
	}

	/**
	 * 
	 * @param dataType 位
	 *            16位整数 16位正整数 32位整数 32位正整数 32位浮点数
	 * 
	 * @param noticeAddrId
	 * @param noticeValue
	 */
	public void addNotice(DATA_TYPE dataType, AddrProp addr,
			double noticeValue) {
		Vector<Double > dataList = new Vector<Double >();
		dataList.add(noticeValue);
		SEND_DATA_STRUCT mSendData = new SEND_DATA_STRUCT();
		mSendData.eDataType = dataType;
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;
		PlcRegCmnStcTools.setRegDoubleData(addr, dataList, mSendData);
	
	}

	public <uchar> void noticWordReg(uchar[] nWordValue) {
		// TODO put your implementation here.
	}

	public void clickItem() {
		// TODO put your implementation here.
	}

}