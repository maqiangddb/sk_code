package com.android.Samkoonhmi.model.skglobalcmn;

import java.util.Vector;

import com.android.Samkoonhmi.skenum.STORAGE_MEDIA;
import com.android.Samkoonhmi.skglobalcmn.DataCollect.HistoryDataProp;

/**
 * 数据库操作，传递的属性类
 * @author latory
 */
public class EditDataCollectProp {
	//是否显示对话框
	public boolean bShowPress = true;
	//采集组号
	public int nGroupId = 0;
	//导出时间，0-导出全部
	public long nRecordTimeLen = 0;  //单位是小时
	//历史数据采集属性集合
	public Vector<Vector<HistoryDataProp>> nHistoryDataList = null;
	//操作回调
	public OperCall mOperCall;
	//存储路径
	public STORAGE_MEDIA mMedia;
}
