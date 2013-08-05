package com.android.Samkoonhmi.skgraphics;

import java.util.List;
import java.util.Vector;
import com.android.Samkoonhmi.model.ShowInfo;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.model.TouchInfo;
import com.android.Samkoonhmi.model.UserInfo;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.READ_WRITE_COM_TYPE;
import com.android.Samkoonhmi.util.AddrProp;
import com.android.Samkoonhmi.util.SEND_DATA_STRUCT;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;

/**
 * 所有控件的基类
 * 
 * @author Administrator
 * 
 */
public abstract class SKGraphics {

	private int m_typeId;

	public SKGraphics() {
		// TODO put your implementation here.
	}

	protected int getTypeId() {
		return m_typeId;
		// TODO put your implementation here.
	}

	protected void setTypeId(int nTypeId) {
		// TODO put your implementation here.
		this.m_typeId = nTypeId;
	}

	/**
	 * 控件初始化
	 */
	public abstract void initGraphics();

	/**
	 * 绘制控件
	 * 
	 * @param canvas
	 *            -系统画布
	 * @param itemId
	 *            -控件id，当次参数与自己的控件id相同时，刷新控件，并且返回true 其他情况返回false。
	 */
	public abstract boolean drawGraphics(Canvas canvas, int itemId);

	/**
	 * 释放对象所占内存
	 */
	public abstract void realseMemeory();

	/**
	 * 取得数据库里面的数据
	 */
	public abstract void getDataFromDatabase();

	/**
	 * 将数据写入数据库
	 */
	public abstract void setDataToDatabase();

	/**
	 * 控件是否显示
	 * 
	 * @return
	 */
	public abstract boolean isShow();

	/**
	 * 判断 控件是否可以触控
	 * 
	 * @param
	 * @return
	 */
	public abstract boolean isTouch();

	/**
	 * 按钮触控事件
	 * 
	 * @param event
	 * @return
	 */
	public abstract boolean onTouchEvent(MotionEvent event);

	/**
	 * 高级页面触控
	 * 
	 * @return
	 */
	public boolean popedomIsTouch(TouchInfo touchInfo) {

		boolean flag = false;
		if (null != touchInfo) {
			boolean isTouchByAddr = touchInfo.isbTouchByAddr();
			boolean isTouchByUser = touchInfo.isbTouchByUser();
			if (isTouchByAddr) {
				flag = showOrTouchByAddr(touchInfo.getTouchAddrProp(),
						touchInfo.getnValidStatus(), touchInfo.getnAddrId(),
						touchInfo.geteCtlAddrType(),
						touchInfo.getnWordPosition());
			}
			if (isTouchByUser) {
				UserInfo user = SystemInfo.getGloableUser();
				flag = showOrTouchByUser(touchInfo.getnGroupValueF(),
						touchInfo.getnGroupValueL(), user.getGroupId());

			}

		}

		return flag;
	}

	public void noticeAddr(TouchInfo touchInfo, boolean flag) {
		if (null != touchInfo) {
			// 选择了操作通知
			if (touchInfo.isbNoticAddr()) {
				// 是解开的状态,则写入通知地址值 ，否则写入 0 （默认）
				double noticeValue = 0;
				if (flag) {
					noticeValue = touchInfo.getnNoticValue(); // 通知写入地址的值
					writeNoticeValue(noticeValue, touchInfo.geteDataType(),
							touchInfo.getnNoticAddrId());
				}

			}
		}
	}

	/**
	 * 写入解锁通知地址值
	 * 
	 * @param noticeValue
	 */
	private void writeNoticeValue(double noticeValue, DATA_TYPE dataType,
			AddrProp addrProp) {
		SEND_DATA_STRUCT mSendData = new SEND_DATA_STRUCT();
		// 16位整数 或者 32位整数 或者32 位正整数 位地址
		if (dataType == DATA_TYPE.INT_16 || dataType == DATA_TYPE.INT_32
				|| dataType == DATA_TYPE.POSITIVE_INT_16
				|| dataType == DATA_TYPE.BIT_1) {
			Vector<Integer> dataList = new Vector<Integer>();
			int inputStringInt = (int) noticeValue;
			dataList.add(inputStringInt);
			mSendData.eDataType = dataType;
			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;
			PlcRegCmnStcTools.setRegIntData(addrProp, dataList, mSendData);
		} else if (dataType == DATA_TYPE.POSITIVE_INT_32)// 32位正整数
		{
			Vector<Long> dataList = new Vector<Long>();
			long inputStringDouble = (long) noticeValue;
			dataList.add(inputStringDouble);
			mSendData.eDataType = dataType;
			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;
			PlcRegCmnStcTools.setRegLongData(addrProp, dataList, mSendData);
		} else if (dataType == DATA_TYPE.FLOAT_32)// 32位浮点数
		{
			Vector<Double> dataList = new Vector<Double>();
			dataList.add(noticeValue);

			mSendData.eDataType = dataType;
			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;
			PlcRegCmnStcTools.setRegDoubleData(addrProp, dataList, mSendData);
		}
	}

	/**
	 * 显现页面
	 * 
	 * @return
	 */
	public boolean popedomIsShow(ShowInfo showInfo) {

		boolean flag = false;
		if (null != showInfo) {
			boolean isShowByAddr = showInfo.isbShowByAddr();
			boolean isShowByUser = showInfo.isbShowByUser();
			// 受地址控制
			if (isShowByAddr) {
				flag = showOrTouchByAddr(showInfo.getShowAddrProp(),
						showInfo.getnValidStatus(), showInfo.getnAddrId(),
						showInfo.geteAddrType(), showInfo.getnBitPosition());
			}
			if (isShowByUser) {
				UserInfo user = SystemInfo.getGloableUser();
				flag = showOrTouchByUser(showInfo.getnGroupValueF(),
						showInfo.getnGroupValueL(), user.getGroupId());
			}
		}
		return flag;
	}

	/**
	 * 通过地址控制显现和触控
	 * 
	 * @param showInfo
	 * @return
	 */
	private Vector<Integer> dataList;
	private SEND_DATA_STRUCT mSendData;

	private boolean showOrTouchByAddr(AddrProp addr, int availableValue,
			int addrId, ADDRTYPE addrType, int bitPosition) {
		boolean flag = false;

		// 有效状态 0 或1
		// int availableValue = showInfo.getnValidStatus();
		// 地址

		if (dataList == null) {
			dataList = new Vector<Integer>();
		} else {
			dataList.clear();
		}
		if (mSendData == null) {
			mSendData = new SEND_DATA_STRUCT();
		}
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_R;
		int addrValue = 1;
		// 位地址
		if (ADDRTYPE.BITADDR == addrType) {

			mSendData.eDataType = DATA_TYPE.BIT_1;

			PlcRegCmnStcTools.getRegIntData(addr, dataList, mSendData);

			if (!dataList.isEmpty()) {
				addrValue = dataList.get(0);
			}
			if (addrValue == availableValue) {
				flag = true;
			}
		} else {
			// 字地址的位控制
			// int bitPosition = showInfo.getnBitPosition();// 字的第几位

			mSendData.eDataType = DATA_TYPE.INT_16;
			PlcRegCmnStcTools.getRegIntData(addr, dataList, mSendData);
			if (!dataList.isEmpty()) {
				addrValue = dataList.get(0);
			}
			int ss = 1 << bitPosition;

			int newValue = 0;
			if ((ss & addrValue) != 0) {
				newValue = 1;
			}

			if (newValue == availableValue) {
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * 权限组控制控件显隐
	 * 
	 * @param showInfo
	 * @param userGroupIds
	 * @return
	 */
	private boolean showOrTouchByUser(int nGroupValueF, int nGroupValueL,
			List<Integer> userGroupIds) {

		boolean flag = false;
		if (!userGroupIds.isEmpty()) {
			// int skGrapicGroup = showInfo.getnGroupValueF();
			// int skGrapicGroup2 = showInfo.getnGroupValueL();
			for (int i = 0; i < userGroupIds.size(); i++) {
				int userGroupId = (Integer) userGroupIds.get(i);
				int userGroupIdValue = 1 << userGroupId;// myPow(userGroupId);
				if (userGroupId <= 31) {
					if ((userGroupIdValue & nGroupValueF) != 0) {
						flag = true;
						break;
					} else {
						flag = false;
					}
				} else {
					if ((userGroupIdValue & nGroupValueL) != 0) {
						flag = true;
						break;
					} else {
						flag = false;
					}
				}
			}
		}
		return flag;

	}

	private static int myPow(int size) {
		int returnValue = 1;
		for (int i = 0; i < size; i++) {
			returnValue = returnValue * 2;
		}
		return returnValue;
	}

}