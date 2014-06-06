package com.android.Samkoonhmi.databaseinterface;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.SKSaveThread.AlamSaveProp;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.model.alarm.AlarmConditionInfo;
import com.android.Samkoonhmi.model.alarm.AlarmContolInfo;
import com.android.Samkoonhmi.model.alarm.AlarmDataInfo;
import com.android.Samkoonhmi.model.alarm.AlarmGroupInfo;
import com.android.Samkoonhmi.model.alarm.AlarmHisShowInfo;
import com.android.Samkoonhmi.model.alarm.AlarmMessageInfo;
import com.android.Samkoonhmi.model.alarm.AlarmSlipInfo;
import com.android.Samkoonhmi.skenum.ARRAY_ORDER;
import com.android.Samkoonhmi.skenum.ConditionType;
import com.android.Samkoonhmi.skenum.DATE_FORMAT;
import com.android.Samkoonhmi.skenum.IntToEnum;
import com.android.Samkoonhmi.skenum.TIME_FORMAT;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;
import com.android.Samkoonhmi.skwindow.AKHintDialog;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.skwindow.SKToast;
import com.android.Samkoonhmi.util.DateStringUtil;

/**
 * 报警模块
 */
public class AlarmBiz extends DataBase {

	private SKDataBaseInterface db = null;
	/**
	 * key1-语言id
	 * key2-组号id
	 * key3-序号id
	 */
	private static HashMap<Integer, HashMap<Integer, HashMap<Integer, String>>> mAlarmText=new HashMap<Integer, HashMap<Integer,HashMap<Integer,String>>>();

	public AlarmBiz() {
		db=SkGlobalData.getProjectDatabase();
	}

	/**
	 * 报警控件
	 */
	public ArrayList<AlarmContolInfo> selectControl(int sid) {
		ArrayList<AlarmContolInfo> mList=new ArrayList<AlarmContolInfo>();
		String id="";
		boolean init=true;
		if (db != null) {
			Cursor cursor = null;
			String sql = "select * from alarmControl where nSceneId="+sid;
			cursor = db.getDatabaseBySql(sql, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					AlarmContolInfo info = new AlarmContolInfo();
					info.setnItemId(cursor.getInt(cursor
							.getColumnIndex("nItemId")));
					info.setnFrameColor(cursor.getInt(cursor
							.getColumnIndex("nFrameColor")));
					info.setnTableColor(cursor.getInt(cursor
							.getColumnIndex("nTableColor")));
					info.setnTitleBackcolor(cursor.getInt(cursor
							.getColumnIndex("nTitleBackcolor")));
					info.setnTitleColor(cursor.getInt(cursor
							.getColumnIndex("nTitleColor")));
					boolean b = cursor.getString(
							cursor.getColumnIndex("bShowTime")).equals("true") ? true
							: false;
					info.setbShowTime(b);
					info.seteTimeFormat(IntToEnum.getTimeType(cursor
							.getShort(cursor.getColumnIndex("eTimeFormat"))));
					boolean bb = cursor.getString(
							cursor.getColumnIndex("bShowDate")).equals("true") ? true
							: false;
					info.setbShowDate(bb);
					info.seteDateFormat(IntToEnum.getDateType(cursor
							.getShort(cursor.getColumnIndex("eDateFormat"))));
					info.setnFontSize(cursor.getShort(cursor
							.getColumnIndex("nFontSize")));
					info.setnTextColor(cursor.getInt(cursor
							.getColumnIndex("nTextColor")));
					info.setnRowCount(cursor.getShort(cursor
							.getColumnIndex("nRowCount")));
					boolean bbb = cursor.getString(
							cursor.getColumnIndex("bShowall")).equals("true") ? true
							: false;
					info.setbShowwall(bbb);
					if (!bbb) {
						String sgroup=cursor.getString(cursor.getColumnIndex("sNames"));
    					if (sgroup==null||sgroup.equals("")) {
    						info.setmGroupId(null);
    					}else {
    						String array[]=sgroup.split(",");
    						ArrayList<String> list=new ArrayList<String>();
    						for (int i = 0; i < array.length; i++) {
    							list.add(array[i]);
    						}
    						info.setmGroupId(list);
    					}
					}
					info.setnLeftTopX(cursor.getShort(cursor
							.getColumnIndex("nLeftTopX")));
					info.setnLeftTopY(cursor.getShort(cursor
							.getColumnIndex("nLeftTopY")));
					info.setnWidth(cursor.getShort(cursor
							.getColumnIndex("nWidth")));
					info.setnHeight(cursor.getShort(cursor
							.getColumnIndex("nHeight")));
					info.setnZvalue(cursor.getInt(cursor
							.getColumnIndex("nZvalue")));
					info.setnCollidindId(cursor.getInt(cursor
							.getColumnIndex("nCollidindId")));
					info.setmShowInfo(TouchShowInfoBiz.getShowInfoById(info.getnItemId()));
					info.setmTouchInfo(TouchShowInfoBiz.getTouchInfoById(info.getnItemId()));
					mList.add(info);
					if (init) {
						id += " nItemId=" + info.getnItemId();
						init = false;
					} else {
						id += " or nItemId=" + info.getnItemId();
					}

				}
				close(cursor);
				
				if(mList.size()>0){
					getTitleInfo(mList,id);
					getTable(mList,id);
				}
			}
		}

		return mList;
	}
	
	/**
	 * 获取报警控件的标题信息
	 */
	public void getTitleInfo(ArrayList<AlarmContolInfo> list,String id){
		if (db!=null) {
			Cursor cursor = null;
			String sql = "select * from alarmTitle where "+id;
			cursor = db.getDatabaseBySql(sql,null);
			int nItemId=-1;
			AlarmContolInfo info=null;
			if(cursor!=null){
				while (cursor.moveToNext()) {
					if (nItemId != cursor.getInt(cursor.getColumnIndex("nItemId"))) {
						nItemId = cursor.getInt(cursor.getColumnIndex("nItemId"));
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getnItemId() == nItemId) {
								info = list.get(i);
								ArrayList<Short> mSize=new ArrayList<Short>();//字体大小
								ArrayList<String> mType=new ArrayList<String>();//字体类型
								ArrayList<String> mTime=new ArrayList<String>();//时间字段
								ArrayList<String> mDate=new ArrayList<String>();//日期字段
								ArrayList<String> mMessage=new ArrayList<String>();//消息
								info.setnTitleFontSizes(mSize);
								info.setsTitleFontTypes(mType);
								info.setsTimeStrs(mTime);
								info.setsDateStrs(mDate);
								info.setsMessages(mMessage);
								break;
							}
						}

					}
					info.getnTitleFontSizes().add(cursor.getShort(cursor.getColumnIndex("nFontSize")));
					info.getsTitleFontTypes().add(cursor.getString(cursor.getColumnIndex("sFont")));
					info.getsTimeStrs().add(cursor.getString(cursor.getColumnIndex("sTime")));
					info.getsDateStrs().add(cursor.getString(cursor.getColumnIndex("sDate")));
					info.getsMessages().add(cursor.getString(cursor.getColumnIndex("sMessage")));
					
				}
			
			}
			close(cursor);
		}
	}

	/**
	 * 获取表格信息
	 */
	private void getTable(ArrayList<AlarmContolInfo> list,String id){
		if (db != null) {
			Cursor cursor = null;
			String sql = "select * from tableProp where "+id;
			cursor = db.getDatabaseBySql(sql, null);
			
			int nItemId=-1;
			AlarmContolInfo info=null;
			double width=0;
			double height=0;
			
			if (cursor != null) {
				while (cursor.moveToNext()) {
					if (nItemId != cursor.getInt(cursor.getColumnIndex("nItemId"))) {
						nItemId = cursor.getInt(cursor.getColumnIndex("nItemId"));
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getnItemId() == nItemId) {
								info = list.get(i);
								//行的高
								ArrayList<Double> mRowHeight=new ArrayList<Double>();
								//行的宽
								ArrayList<Double> mRowWidth=new ArrayList<Double>();
								info.setmRowWidht(mRowWidth);
								info.setmRowHeight(mRowHeight);
								width=0;
								height=0;
								break;
							}
						}

					}
					if (cursor.getInt(cursor.getColumnIndex("nIsRow"))==0) {
						//行的宽
						width+=cursor.getDouble(cursor.getColumnIndex("nWidth"));
						info.getmRowWidht().add(cursor.getDouble(cursor.getColumnIndex("nWidth")));
					}else {
						//行的高
						height+=cursor.getDouble(cursor.getColumnIndex("nWidth"));
						info.getmRowHeight().add(cursor.getDouble(cursor.getColumnIndex("nWidth")));
					}
					if (width>0) {
						info.setnWidth((short)width);
					}
					if(height>0){
						info.setnHeight((short)height);
					}
				}
				cursor.close();
			}
		}
	}
	
	/**
	 * 报警条
	 */
	public ArrayList<AlarmSlipInfo> selectAlarmSlip(int sid) {
		ArrayList<AlarmSlipInfo> mList=new ArrayList<AlarmSlipInfo>();
		
		if (db != null) {
			Cursor cursor = null;
			String sql = "select * from alarmSlip where nSceneId="+sid;
			cursor = db.getDatabaseBySql(sql, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					AlarmSlipInfo info = new AlarmSlipInfo();
					info.setnItemId(cursor.getInt(cursor
							.getColumnIndex("nItemId")));
					info.setnStyle(cursor.getShort(cursor.getColumnIndex("sApperIndex")));
					info.setnFrameColor(cursor.getInt(cursor.getColumnIndex("nFramecolor")));
					info.setnBackcolor(cursor.getInt(cursor
							.getColumnIndex("nBackcolor")));
					info.setnForecolor(cursor.getInt(cursor.getColumnIndex("nForecolor")));
					info.setnFontSize(cursor.getShort(cursor
							.getColumnIndex("nFontSize")));
					info.setnTextColor(cursor.getInt(cursor
							.getColumnIndex("nTextColor")));
					boolean b = cursor.getString(
							cursor.getColumnIndex("bSelectall")).equals("true") ? true
							: false;
					info.setbSelectall(b);
                    if (!b) {
                    	String sgroup=cursor.getString(cursor.getColumnIndex("sNames"));
    					if (sgroup==null||sgroup.equals("")) {
    						info.setmGroupId(null);
    					}else {
    						String array[]=sgroup.split(",");
    						ArrayList<String> list=new ArrayList<String>();
    						for (int i = 0; i < array.length; i++) {
    							list.add(array[i]);
    						}
    						info.setmGroupId(list);
    					} 
					}
					
					info.setnSpeed(cursor.getShort(cursor
							.getColumnIndex("nSpeed")));
					int type=cursor.getShort(cursor.getColumnIndex("eDirection"));
					if (type==1) {
						info.seteDirection(ARRAY_ORDER.RIGHT_TO_LEFT);
					}else {
						info.seteDirection(ARRAY_ORDER.LEFT_TO_RIGHT);
					}
					int temp=cursor.getShort(cursor.getColumnIndex("eSort"));
					if (temp==1) {
						info.seteSort(ARRAY_ORDER.CLOCK_WISE);
					}else{
						info.seteSort(ARRAY_ORDER.COUNTER_CLOCK_WISE);
					}
					info.setnLeftTopX(cursor.getShort(cursor
							.getColumnIndex("nLeftTopX")));
					info.setnLeftTopY(cursor.getShort(cursor
							.getColumnIndex("nLeftTopY")));
					info.setnWidth(cursor.getShort(cursor
							.getColumnIndex("nWidth")));
					info.setnHeight(cursor.getShort(cursor
							.getColumnIndex("nHeight")));
					info.setnTextLeftTopX(cursor.getShort(cursor
							.getColumnIndex("nTextLeftTopX")));
					info.setnTextLeftTopY(cursor.getShort(cursor
							.getColumnIndex("nTextLeftTopY")));
					info.setnTextWidth(cursor.getShort(cursor
							.getColumnIndex("nTextWidth")));
					info.setnTextHeight(cursor.getShort(cursor
							.getColumnIndex("nTextHeight")));
					info.setnZvalue(cursor.getShort(cursor
							.getColumnIndex("nZvalue")));
					info.setnCollidindId(cursor.getShort(cursor
							.getColumnIndex("nCollidindId")));
					info.setmShowInfo(TouchShowInfoBiz.getShowInfoById(info.getnItemId()));
					
					mList.add(info);
				}
				close(cursor);
			}
		}
		return mList;
	}

	/**
	 * 历史报警显示器
	 */
	public ArrayList<AlarmHisShowInfo> selectHistoryAlarm(int sid) {
		String id="";
		boolean init=true;
		ArrayList<AlarmHisShowInfo> list=new ArrayList<AlarmHisShowInfo>();
		
		if (db != null) {
			Cursor cursor = null;
			String sql = "select * from hisAlarmShow where nSceneId="+sid;
			cursor = db.getDatabaseBySql(sql,null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					AlarmHisShowInfo info = new AlarmHisShowInfo();
					info.setnItemId(cursor.getInt(cursor
							.getColumnIndex("nItemId")));
					info.setnFrameColor(cursor.getInt(cursor
							.getColumnIndex("nFrameColor")));
					info.setnTableColor(cursor.getInt(cursor
							.getColumnIndex("nTableColor")));
					boolean b = cursor.getString(
							cursor.getColumnIndex("bControl")).equals("true") ? true: false;
					info.setbControl(b);
					if (b) {
						info.setmControlAddr(AddrPropBiz.selectById(cursor.getInt(cursor.getColumnIndex("eControlAddr"))));
					}
//					info.setsTileFontType(cursor.getString(cursor
//							.getColumnIndex("sTitleFontType")));
					info.setnTitleBackcolor(cursor.getInt(cursor
							.getColumnIndex("nTitleBackcolor")));
					info.setnTitleColor(cursor.getInt(cursor
							.getColumnIndex("nTitleColor")));
					boolean btime = cursor.getString(
							cursor.getColumnIndex("bShowTime")).equals("true") ? true
							: false;
					info.setbShowTime(btime);
					info.seteTimeFormat(IntToEnum.getTimeType(cursor
							.getShort(cursor.getColumnIndex("eTimeFormat"))));
					
					boolean bdate = cursor.getString(
							cursor.getColumnIndex("bShowDate")).equals("1") ? true: false;
					info.setbShowDate(bdate);
					info.seteDateFormat(IntToEnum.getDateType(cursor
							.getShort(cursor.getColumnIndex("eDateFormat"))));
					boolean bnum = cursor.getString(
							cursor.getColumnIndex("bNumber")).equals("true") ? true
							: false;
					info.setbNumber(bnum);
					info.setnFontSize(cursor.getShort(cursor
							.getColumnIndex("nFontSize")));
					info.setnTextColor(cursor.getInt(cursor.getColumnIndex("nTextColor")));
					info.setnRowCount(cursor.getShort(cursor
							.getColumnIndex("nRowCount")));
					boolean bClearDate = cursor.getString(
							cursor.getColumnIndex("bClearDate")).equals("true") ? true
							: false;
					info.setbClearDate(bClearDate);
					info.seteClearDateFormat(IntToEnum.getDateType(cursor
							.getShort(cursor.getColumnIndex("eClearDateFormat"))));
					boolean bClearTime = cursor.getString(
							cursor.getColumnIndex("bClearTime")).equals("true") ? true
							: false;
					info.setbClearTime(bClearTime);
					info.seteClearTimeFormat(IntToEnum.getTimeType(cursor
							.getShort(cursor.getColumnIndex("eClearTimeFormat"))));
					info.setnClearColor(cursor.getInt(cursor
							.getColumnIndex("nClearColor")));
					info.setnConfirmColor(cursor.getInt(cursor
							.getColumnIndex("nConfirmColor")));
					info.setnLeftTopX(cursor.getShort(cursor
							.getColumnIndex("nLeftTopX")));
					info.setnLeftTopY(cursor.getShort(cursor
							.getColumnIndex("nLeftTopY")));
					info.setnWidth(cursor.getShort(cursor
							.getColumnIndex("nWidth")));
					info.setnHeight(cursor.getShort(cursor
							.getColumnIndex("nHeight")));
					info.setnZvalue(cursor.getInt(cursor
							.getColumnIndex("nZvalue")));
					info.setnCollidindId(cursor.getInt(cursor
							.getColumnIndex("nCollidindId")));
					info.setmShowInfo(TouchShowInfoBiz.getShowInfoById(info.getnItemId()));
					info.setmTouchInfo(TouchShowInfoBiz.getTouchInfoById(info.getnItemId()));
					list.add(info);
					
					if (init) {
						id += " nItemId=" + info.getnItemId();
						init = false;
					} else {
						id += " or nItemId=" + info.getnItemId();
					}
				}
			}
			close(cursor);
			if (list.size()>0) {
				getTitle(list,id);
				getTableForHis(list,id);
			}
			
		}
		return list;
	}
	
	/**
	 * 获取历史报警控件的标题信息
	 */
	public void getTitle(ArrayList<AlarmHisShowInfo> list,String id){
		if (db!=null) {
			
			AlarmHisShowInfo info=null;
			int nItemId=-1;
			Cursor cursor = null;
			String sql = "select * from alarmTitle where "+id;
			cursor = db.getDatabaseBySql(sql, null);
			
			if(cursor!=null){
				while (cursor.moveToNext()) {
					if (nItemId != cursor.getInt(cursor.getColumnIndex("nItemId"))) {
						nItemId = cursor.getInt(cursor.getColumnIndex("nItemId"));
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getnItemId() == nItemId) {
								info = list.get(i);
								ArrayList<Short> mSize=new ArrayList<Short>();//字体大小
								ArrayList<String> mType=new ArrayList<String>();//字体类型
								ArrayList<String> mTime=new ArrayList<String>();//时间字段
								ArrayList<String> mDate=new ArrayList<String>();//日期字段
								ArrayList<String> mClearTime=new ArrayList<String>();//消除时间
								ArrayList<String> mClearDate=new ArrayList<String>();//消除日期
								ArrayList<String> mMessage=new ArrayList<String>();//消息
								
								info.setnTitleFontSizes(mSize);
								info.setsTitleFontTypes(mType);
								info.setsTimeStrs(mTime);
								info.setsDateStrs(mDate);
								info.setsClearTimeStrs(mClearTime);
								info.setsClearDateStrs(mClearDate);
								info.setsMessages(mMessage);
								break;
							}
						}

					}
					info.getnTitleFontSizes().add(cursor.getShort(cursor.getColumnIndex("nFontSize")));
					info.getsTitleFontTypes().add(cursor.getString(cursor.getColumnIndex("sFont")));
					info.getsTimeStrs().add(cursor.getString(cursor.getColumnIndex("sTime")));
					info.getsDateStrs().add(cursor.getString(cursor.getColumnIndex("sDate")));
					info.getsMessages().add(cursor.getString(cursor.getColumnIndex("sMessage")));
					info.getsClearTimeStrs().add(cursor.getString(cursor.getColumnIndex("sClearTime")));
					info.getsClearDateStrs().add(cursor.getString(cursor.getColumnIndex("sClearDate")));
				}
			}
			
			close(cursor);
		}
	}

	/**
	 * 获取表格信息
	 */
	private void getTableForHis(ArrayList<AlarmHisShowInfo> list,String id){
		if (db != null) {
			Cursor cursor = null;
			String sql = "select * from tableProp where "+id;
			cursor = db.getDatabaseBySql(sql, null);
			
			int nItemId=-1;
			AlarmHisShowInfo info=null;
			double width=0;
			double height=0;
			
			if (cursor != null) {
				while (cursor.moveToNext()) {
					if (nItemId != cursor.getInt(cursor.getColumnIndex("nItemId"))) {
						nItemId = cursor.getInt(cursor.getColumnIndex("nItemId"));
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getnItemId() == nItemId) {
								info = list.get(i);
								//行的高
								ArrayList<Double> mRowHeight=new ArrayList<Double>();
								//行的宽
								ArrayList<Double> mRowWidth=new ArrayList<Double>();
								info.setmRowWidht(mRowWidth);
								info.setmRowHeight(mRowHeight);
								width=0;
								height=0;
								break;
							}
						}

					}
					if (cursor.getInt(cursor.getColumnIndex("nIsRow"))==0) {
						//行的宽
						width+=cursor.getDouble(cursor.getColumnIndex("nWidth"));
						info.getmRowWidht().add(cursor.getDouble(cursor.getColumnIndex("nWidth")));
					}else {
						//行的高
						height+=cursor.getDouble(cursor.getColumnIndex("nWidth"));
						info.getmRowHeight().add(cursor.getDouble(cursor.getColumnIndex("nWidth")));
					}
					if (width>0) {
						info.setnWidth((short)width);
					}
					if(height>0){
						info.setnHeight((short)height);
					}
				}
				cursor.close();
			}
		}
	}
	
	/**
	 * 获取当前处于报警or确定的信息
	 */
	public Vector<AlarmDataInfo> setAlarmData(){
		Vector<AlarmDataInfo> list =new Vector<AlarmDataInfo>();
		SKDataBaseInterface data=SkGlobalData.getAlarmDatabase();
		if (data!=null) {
			String sql="select * from  dataAlarm where  nClear=0 or nClear=1  ";
			Cursor cursor=data.getDatabaseBySql(sql,null);
			if (cursor!=null) {
				while(cursor.moveToNext()){
					AlarmDataInfo info = new AlarmDataInfo();
					info.setnGroupId(cursor.getInt(cursor.getColumnIndex("nGroupId")));
					info.setnAlarmIndex(cursor.getInt(cursor.getColumnIndex("nAlarmIndex")));
					info.setnDateTime(cursor.getLong(cursor.getColumnIndex("nDateTime")));
					info.setnClearDT(cursor.getLong(cursor.getColumnIndex("nClearDT")));
					info.setnClear(cursor.getShort(cursor.getColumnIndex("nClear")));
					list.add(info);
				}
				close(cursor);
			}
		}
		return list;
	}
	
	/**
	 * 报警登录
	 */
	public ArrayList<AlarmGroupInfo> selectAlarmGroup(Vector<AlarmDataInfo> mAlarmState) {
		ArrayList<AlarmGroupInfo> list = null;
		if (db != null) {
			Cursor cursor = null;
			String sql = "select * from alarmGroup ";
			cursor = db.getDatabaseBySql(sql, null);
			if (cursor != null) {
				list = new ArrayList<AlarmGroupInfo>();
				while (cursor.moveToNext()) {
					AlarmGroupInfo info = new AlarmGroupInfo();
					info.setnGroupId(cursor.getInt(cursor
							.getColumnIndex("nGroupId")));
					info.setsName(cursor.getString(cursor
							.getColumnIndex("sName")));
					info.setnTime(cursor.getShort(cursor
							.getColumnIndex("nTime")));
					list.add(info);
				}
				close(cursor);
			}
			
			for (int i = 0; i < list.size(); i++) {
				AlarmGroupInfo info=list.get(i);
				// 报警条件
				selectAlarmCondition(info,mAlarmState);
				// 报警消息
				selectAlarmMessage(info);
			}
			
			//把报警登录多语言信息，复制到alarm.db
			alarmMsgCopy(list);
			setAlarmText();
		}
		return list;
	}
	
	private String getAlarmFileTitle()
	{
		ArrayList<String> title=new ArrayList<String>();
		if (db!=null) {
			Cursor cursor = null;
			String sql = "select * from alarmTitle where nLanguageIndex=? limit 1";
			cursor = db.getDatabaseBySql(sql, new String[] { SystemInfo.getCurrentLanguageId()+ ""});
			if (cursor!=null) {
				while (cursor.moveToNext()) {
					title.add(cursor.getString(cursor.getColumnIndex("sDate")));
					title.add(cursor.getString(cursor.getColumnIndex("sTime")));
					title.add(cursor.getString(cursor.getColumnIndex("sMessage")));
					title.add(cursor.getString(cursor.getColumnIndex("sClearDate")));
					title.add(cursor.getString(cursor.getColumnIndex("sClearTime")));
					
				}
			}
			close(cursor);
		}
		
		String emailTitle = null;
		if (title.size() > 0) {
			emailTitle = title.get(0) + "," + title.get(1) + "," + title.get(2) + "," + title.get(3) + "," + title.get(4);
		}
		title.clear();
		title = null;

		return emailTitle;
		
	}
	
	
	/**
	 * 
	 * @param groupId 组ID
	 * @param groupName 组名称
	 * @param peroid - 间隔时间  向前推 以小时为单位
	 */
	public void writeAlarmToFiles(int groupId, String groupName, int peroid){
		// 报警时间   报警消除 报警状态   报警消息
		
		//保存路径
		String path ="/data/data/com.android.Samkoonhmi/files/email_files/" ;
		String filePath = path + "alarm_"+groupId+ ".csv";//防止文件名是中文的情况
		//查询条件
		String sqlString ="select * from dataAlarm where nGroupId = " + groupId;
		if(peroid > 0){
			long startTime = System.currentTimeMillis() - peroid*60*60*1000;
			sqlString = sqlString + " and nDateTime >= " + startTime  ;
		}
		
		Cursor cursor = SkGlobalData.getAlarmDatabase().getDatabaseBySql(sqlString, null);
		if (cursor != null && cursor.getColumnCount() > 0) {
			File outpFile = new File(filePath);
			if (outpFile.exists()) {//首先进行删除
				outpFile.delete();
			}
			File pathFile = new File(path);
			if (!pathFile.exists()) {//创建目录
				pathFile.mkdirs();
			}
			outpFile = new File(filePath);
			if (!outpFile.exists()) {//创建导出文件
				try {
					outpFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (!outpFile.exists())
			{
				close(cursor);
				 return ;
			}
			
			try {
				DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(outpFile));
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "GBK"));
				//文件头
				String  title = getAlarmFileTitle();
				if (TextUtils.isEmpty(title)) {
					title="报警日期,报警时间,报警消息,报警消除日期,报警消除时间";
				}
				bufferedWriter.write(title);
				
				String content;
				/**
				 * 获取当前语言报警文本
				 */
				HashMap<Integer, HashMap<Integer, String>> mGText=null;
				if (mAlarmText.containsKey(SystemInfo.getCurrentLanguageId())) {
					mGText=mAlarmText.get(SystemInfo.getCurrentLanguageId());
				}else {
					mGText=mAlarmText.get(0);
				}
				while (cursor.moveToNext()) {
					int alarmIndex = cursor.getInt(cursor.getColumnIndex("nAlarmIndex"));
					long bornTime = cursor.getLong(cursor.getColumnIndex("nDateTime"));
					long clearTime = cursor.getLong(cursor.getColumnIndex("nClearDT"));
					
					String nBornDate, nBornTime, nClearDate,nClearTime,nMessage;
					nMessage = mGText.get(groupId).get(alarmIndex);
					if (bornTime > 0) {//报警时间
						Date date = new Date(bornTime);
						nBornDate = DateStringUtil.convertDate(DATE_FORMAT.YYYYMMDD_SLASH, date);
						nBornTime = DateStringUtil.converTime(TIME_FORMAT.HHMM_COLON, date);
					}
					else {
						nBornDate ="--";
						nBornTime ="--";
					}
					if (clearTime > 0) {//报警消除时间
						Date date = new Date(clearTime);
						nClearDate = DateStringUtil.convertDate(DATE_FORMAT.YYYYMMDD_SLASH, date);
						nClearTime = DateStringUtil.converTime(TIME_FORMAT.HHMM_COLON, date);
					}
					else {
						nClearDate ="--";
						nClearTime ="--";
					}
					
					//写入数据
					content = nBornDate + "," + nBornTime + "," + nMessage + ","+nClearDate+ ","+ nClearTime;
					bufferedWriter.newLine();
					bufferedWriter.write(content);
				}
				
				close(cursor);
				
				//
				bufferedWriter.flush();
				outputStream.close();
				bufferedWriter.close();
			} catch (Exception e) {
				e.printStackTrace();
				
				return;
			}
			
			/*休眠2秒钟，等文件写入成功*/
			try {
				Thread.sleep(2000);  
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return ;
		}
		
	}
	
	public void writeAlarmToFiles(int groupId, String groupName, long startTime, long endtime){
		// 报警时间   报警消除 报警状态   报警消息
		
		//保存路径
		String path ="/data/data/com.android.Samkoonhmi/files/email_files/" ;
		String filePath = path + "alarm_"+groupId+ ".csv";//防止文件名是中文的情况
		//查询条件
		String sqlString ="select * from dataAlarm where nGroupId = " + groupId;
		if (startTime > 0 && endtime > 0) {
			sqlString = sqlString + " and (nDateTime >= " + startTime + " and nDateTime <= " + endtime + ")";
		}
		else if (startTime > 0) {
			sqlString = sqlString + " and nDateTime >=" + startTime;
		}
		else if (endtime > 0) {
			sqlString = sqlString + " and nDateTime <= " + endtime;
		}
		Cursor cursor = SkGlobalData.getAlarmDatabase().getDatabaseBySql(sqlString, null);
		if (cursor != null && cursor.getColumnCount() > 0) {
			File outpFile = new File(filePath);
			if (outpFile.exists()) {//首先进行删除
				outpFile.delete();
			}
			File pathFile = new File(path);
			if (!pathFile.exists()) {//创建目录
				pathFile.mkdirs();
			}
			outpFile = new File(filePath);
			if (!outpFile.exists()) {//创建导出文件
				try {
					outpFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (!outpFile.exists())
			{
				close(cursor);
				 return ;
			}
			
			try {
				DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(outpFile));
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "GBK"));
				//文件头
				String  title = getAlarmFileTitle();
				if (TextUtils.isEmpty(title)) {
					title="报警日期,报警时间,报警消息,报警消除日期,报警消除时间";
				}
				bufferedWriter.write(title);
				
				String content;
				/**
				 * 获取当前语言报警文本
				 */
				HashMap<Integer, HashMap<Integer, String>> mGText=null;
				if (mAlarmText.containsKey(SystemInfo.getCurrentLanguageId())) {
					mGText=mAlarmText.get(SystemInfo.getCurrentLanguageId());
				}else {
					mGText=mAlarmText.get(0);
				}
				while (cursor.moveToNext()) {
					int alarmIndex = cursor.getInt(cursor.getColumnIndex("nAlarmIndex"));
					long bornTime = cursor.getLong(cursor.getColumnIndex("nDateTime"));
					long clearTime = cursor.getLong(cursor.getColumnIndex("nClearDT"));
					
					String nBornDate, nBornTime, nClearDate,nClearTime,nMessage;
					nMessage = mGText.get(groupId).get(alarmIndex);
					if (bornTime > 0) {//报警时间
						Date date = new Date(bornTime);
						nBornDate = DateStringUtil.convertDate(DATE_FORMAT.YYYYMMDD_SLASH, date);
						nBornTime = DateStringUtil.converTime(TIME_FORMAT.HHMM_COLON, date);
					}
					else {
						nBornDate ="--";
						nBornTime ="--";
					}
					if (clearTime > 0) {//报警消除时间
						Date date = new Date(clearTime);
						nClearDate = DateStringUtil.convertDate(DATE_FORMAT.YYYYMMDD_SLASH, date);
						nClearTime = DateStringUtil.converTime(TIME_FORMAT.HHMM_COLON, date);
					}
					else {
						nClearDate ="--";
						nClearTime ="--";
					}
					
					//写入数据
					content = nBornDate + "," + nBornTime + "," + nMessage + ","+nClearDate+ ","+ nClearTime;
					bufferedWriter.newLine();
					bufferedWriter.write(content);
				}
				
				close(cursor);
				
				//
				bufferedWriter.flush();
				outputStream.close();
				bufferedWriter.close();
			} catch (Exception e) {
				e.printStackTrace();
				
				return;
			}
			
			/*休眠2秒钟，等文件写入成功*/
			try {
				Thread.sleep(2000);  
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return ;
		}
		
	}
	
	public void setAlarmText(){
		mAlarmText.clear();
		if (db!=null) {
			Cursor cursor = null;
			String sql = "select * from alarmMessage  order by  nLanguage,nGroupId,nAlarmIndex";
			cursor = db.getDatabaseBySql(sql, null);
			if (cursor!=null) {
				int nLId=-1;
				int nGId=-1;
				HashMap<Integer, HashMap<Integer, String>> mGMap=null;
				HashMap<Integer, String> mAMap=null;
				while (cursor.moveToNext()) {
					if (nLId!=cursor.getInt(3)) {
						nLId=cursor.getInt(3);
						HashMap<Integer, HashMap<Integer, String>> gMap=new HashMap<Integer, HashMap<Integer,String>>();
						mGMap=gMap;
						mAlarmText.put(nLId, gMap);
						nGId=-1;
					}
					if (nGId!=cursor.getInt(1)) {
						nGId=cursor.getInt(1);
						HashMap<Integer, String> aMap=new HashMap<Integer, String>();
						mAMap=aMap;
						mGMap.put(nGId, aMap);
					}
					mAMap.put(cursor.getInt(2), cursor.getString(4));
					
				}
				close(cursor);
			}
		}
	}

	/**
	 * 把报警登录多语言插入到数据库dataCollectSave.db ,表alarmMsg
	 */
	public boolean alarmMsgCopy(ArrayList<AlarmGroupInfo> list){
		boolean result=false;
		if (list!=null) {
			SKDataBaseInterface dataDB = SkGlobalData.getAlarmDatabase();
			if (dataDB!=null) {
				
				//删除旧数据
				dataDB.deleteByUserDef("alarmText", null, null);
				
				try {
					/**
					 * 报警组
					 */
					for (int i = 0; i < list.size(); i++) {
						
						AlarmGroupInfo info=list.get(i);
						if (info!=null) {
							/**
							 * 每一组报警
							 */
							HashMap<Integer, ArrayList<AlarmMessageInfo>> map = info.getmMessageMaps();
							if (map==null) {
								break;
							}
							for (Iterator<Entry<Integer, ArrayList<AlarmMessageInfo>>> iters = map
									.entrySet().iterator(); iters.hasNext();) {
								
								//每一组报警的报警条件
								Map.Entry<Integer, ArrayList<AlarmMessageInfo>> entry = iters.next();
								ArrayList<AlarmMessageInfo> item = entry.getValue();
								if (item!=null) {
									//多语言文本
									for (int j = 0; j < item.size(); j++) {
										AlarmMessageInfo mInfo=item.get(j);
										
										ContentValues values = new ContentValues();
										values.put("nGroupId", mInfo.getnGroupId());
										values.put("nAlarmIndex", mInfo.getnAlarmIndex());
										values.put("nLanguageId", j);
										values.put("sMessage", mInfo.getsMessage());
										dataDB.insertData("alarmText", values);
									}
								}
								
							}
						}
					}
					//写入当前语言号
					setLanguageId(0);
					
					result=true;
				} catch (Exception e) {
					e.printStackTrace();
					Log.e("AlarmBiz", "init alarmMsg fail!");
				}
			}
		}
		return result;
	}
	
	/**
	 * 设置当前语言号
	 */
	public void setLanguageId(int lid){
		SKDataBaseInterface dataDB = SkGlobalData.getAlarmDatabase();
		if (dataDB!=null) {
			ContentValues values = new ContentValues();
			values.put("nLanguageId", lid);
			dataDB.insertData("alarmLan", values);
		}
	}
	
	/**
	 * 查询报警登录条件
	 */
	private void selectAlarmCondition(AlarmGroupInfo info,Vector<AlarmDataInfo> data) {
		if (db != null) {
			HashMap<Integer, AlarmConditionInfo> map = new HashMap<Integer, AlarmConditionInfo>();
			Cursor cursor = null;
			String sql = "select * from alarm where nGroupId=?";
			cursor = db.getDatabaseBySql(sql, new String[] { info.getnGroupId()
					+ "" });
			if (cursor != null) {
				while (cursor.moveToNext()) {
					AlarmConditionInfo mInfo = new AlarmConditionInfo();
					mInfo.setnGroupId(cursor.getInt(cursor
							.getColumnIndex("nGroupId")));
					mInfo.setnAlarmIndex(cursor.getInt(cursor
							.getColumnIndex("nAlarmIndex")));
					int addId = cursor
							.getInt(cursor.getColumnIndex("nAddress"));
					mInfo.setmAddress(AddrPropBiz.selectById(addId));
					mInfo.seteCondition(ConditionType
							.getConditionType(cursor.getShort(cursor
									.getColumnIndex("eCondition"))));
					mInfo.setmDataType(IntToEnum.getDataType(
							cursor.getShort(cursor.getColumnIndex("eDataType"))));
					mInfo.setnRangLow(cursor.getDouble(cursor.getColumnIndex("nRangLow")));
					mInfo.setnRangHigh(cursor.getDouble(cursor.getColumnIndex("nRangHigh")));
					mInfo.setnTargetPage(cursor.getInt(cursor.getColumnIndex("nTargetPage")));
					mInfo.setnSceneType(cursor.getShort(cursor.getColumnIndex("nSceneType")));
					boolean b=cursor.getString(cursor.getColumnIndex("bOpenScene")).equals("1")?true:false;
					mInfo.setbOpenScene(b);
					if (b) {
						if (mInfo.getnSceneType()==0) {
							//画面
							mInfo.setnTargetPage(DBTool.getInstance().getmSceneBiz().getSceneId(mInfo.getnTargetPage()+1));
						}else {
							mInfo.setnTargetPage(mInfo.getnTargetPage()+1);
						}
					}
					b = cursor.getString(cursor.getColumnIndex("bSeneMsg")).equals("1")? true:false;
					mInfo.setbSendMsg(b);
					mInfo.setmPhoneNum(cursor.getString(cursor.getColumnIndex("sPhoneNum")));
					b = cursor.getShort(cursor.getColumnIndex("bAddtoDB")) == 0 ? false:true;
					mInfo.setbAddtoDB(b);
					
					b = cursor.getShort(cursor.getColumnIndex("bPrint")) == 0 ? false:true;
					if (!b) {
						mInfo.setPrintState(0);
					}
					else {
						int state = 1;
						b =  cursor.getShort(cursor.getColumnIndex("bPrintDate")) == 0 ? false:true;
						if (b) {
							state += 2;
						}
						b =  cursor.getShort(cursor.getColumnIndex("bPrintTime")) == 0 ? false:true;
						if (b) {
							state += 4;
						}
						mInfo.setPrintState(state);
					}
					
					alarmExist(mInfo,data);
					map.put(mInfo.getnAlarmIndex(), mInfo);
				}
				close(cursor);
			}
			info.setmConditionMap(map);
		}
	}
	

	/**
	 * 查询报警消息
	 */
	private void selectAlarmMessage(AlarmGroupInfo info) {
		if (db != null) {
			Cursor cursor = null;
			HashMap<Integer, ArrayList<AlarmMessageInfo>> map =new HashMap<Integer, ArrayList<AlarmMessageInfo>>();
			String sql = "select * from alarmMessage where nGroupId=?";
			cursor = db.getDatabaseBySql(sql, new String[] { info.getnGroupId()+ "" });
			if (cursor != null) {
				int state=-1;
				int index=0;
				while (cursor.moveToNext()) {
					if (state!=cursor.getInt(cursor
							.getColumnIndex("nAlarmIndex"))) {
						ArrayList<AlarmMessageInfo> list=new ArrayList<AlarmMessageInfo>();
						
						state=cursor.getInt(cursor
								.getColumnIndex("nAlarmIndex"));
 						index=state;
						AlarmMessageInfo mInfo = new AlarmMessageInfo();
						mInfo.setnGroupId(cursor.getInt(cursor
								.getColumnIndex("nGroupId")));
						mInfo.setnAlarmIndex(cursor.getInt(cursor
								.getColumnIndex("nAlarmIndex")));
						mInfo.setnLanguage(cursor.getShort(cursor
								.getColumnIndex("nLanguage")));
						mInfo.setsMessage(cursor.getString(cursor
								.getColumnIndex("sMessage")));
						list.add(mInfo);
						map.put(index, list);
					}else {
						ArrayList<AlarmMessageInfo> list=map.get(index);
						AlarmMessageInfo mInfo = new AlarmMessageInfo();
						mInfo.setnGroupId(cursor.getInt(cursor
								.getColumnIndex("nGroupId")));
						mInfo.setnAlarmIndex(cursor.getInt(cursor
								.getColumnIndex("nAlarmIndex")));
						mInfo.setnLanguage(cursor.getShort(cursor
								.getColumnIndex("nLanguage")));
						mInfo.setsMessage(cursor.getString(cursor
								.getColumnIndex("sMessage")));
						list.add(mInfo);
					}
					
				}
				close(cursor);
			}
			info.setmMessageMaps(map);
		}
	}
	
	public void getAlarmMessage(Vector<AlarmDataInfo> group){
		if (group == null || group.size() == 0) {
			return ;
		}
		for(AlarmDataInfo info:group){
			if (info != null && info.getnClear() == 0 && TextUtils.isEmpty(info.getsMessage())) {
				String select = "select sMessage from alarmMessage where nGroupId=? and nAlarmIndex=?";
				Cursor cursor = db.getDatabaseBySql(select, new String[] { info.getnGroupId()+ "",info.getnAlarmIndex()+"" });
				if (cursor != null ) {
					if (cursor.moveToFirst()) {
						info.setsMessage(cursor.getString(cursor.getColumnIndex("sMessage")));
					}
					
					close(cursor);
				}
			}
			
		}
	}
	
//	/**
//	 * 
//	 * @param groupId- 报警组iD
//	 * @param alarmIndex- 报警ID
//	 * @return 报警消息
//	 */
//	private String getAlarmMessage(int groupId, int alarmIndex){
//		String message = "";
//		String select = "select sMessage from alarmMessage where nGroupId=? and nAlarmIndex=?";
//		Cursor cursor = db.getDatabaseBySql(select, new String[] { groupId+ "", alarmIndex+"" });
//		if (cursor != null) {
//			cursor.moveToFirst();
//			message = cursor.getString(cursor.getColumnIndex("sMessage"));
//			close(cursor);
//		}
//
//		return message;
//	}
	
	public void updateAlarmDate(Vector<AlarmDataInfo> group){
		if (group == null || group.size() == 0) {
			return ;
		}
		// 添加message
		for(AlarmDataInfo info:group){
			if (info != null && info.getnClear() == 0 && TextUtils.isEmpty(info.getsMessage())) {
				String select = "select sMessage from alarmMessage where nGroupId=? and nAlarmIndex=?";
				Cursor cursor = db.getDatabaseBySql(select, new String[] { info.getnGroupId()+ "",info.getnAlarmIndex()+"" });
				if (cursor != null ) {
					if (cursor.moveToFirst()) {
						info.setsMessage(cursor.getString(cursor.getColumnIndex("sMessage")));
					}
					close(cursor);
				}
			}
		}
		
		for(AlarmDataInfo info :group){
			if (info != null) {
				String select = "select bAddtoDB from alarm where nGroupId=? and nAlarmIndex=?";
				Cursor cursor = db.getDatabaseBySql(select, new String[] { info.getnGroupId()+ "",info.getnAlarmIndex()+"" });
				if (cursor != null ) {
					if (cursor.moveToFirst()) {
						boolean b = cursor.getShort(cursor.getColumnIndex("bAddtoDB")) == 0 ? false:true;
						info.setbAddtoDB(b); 
					}
					close(cursor);
				}
			}
		}
	}
	
	

	/**
	 * 查询报警是否存在
	 * @param gId-组id
	 * @param aId-报警在组中的序号
	 */
	private boolean alarmExist(AlarmConditionInfo info,Vector<AlarmDataInfo> mAlarmState){
		
		boolean result=true;
		for (int i = 0; i < mAlarmState.size(); i++) {
			AlarmDataInfo data=mAlarmState.get(i);
			if (info.getnGroupId()==data.getnGroupId()&&info.getnAlarmIndex()==data.getnAlarmIndex()) {
				info.setnClear(data.getnClear());
				result=false;
				break;
			}
		}
		if (result) {
			info.setnClear(-1);
		}
		return result;
	}
		
	/**
	 * 查找所有未处理报警消息
	 */
	public ArrayList<AlarmDataInfo> selectDataByGId(ArrayList<String> gid,boolean all) {
		String wsql="";
		if (!all) {
			if (gid!=null) {
				String id="";
				for (int i = 0; i < gid.size(); i++) {
					if (i==gid.size()-1) {
						id+=gid.get(i);
					}else {
						id+=gid.get(i)+",";
					}
				}
				wsql=" and nGroupId in("+id+") ";
			}
		}
		
		String sql="";
		sql = "select * from  dataAlarm "
			+ "where nClear=0 "+wsql;
		
		return selectDataListBySql(sql, null);
	}
	
	/**
	 * 查找所有未处理报警消息
	 */
	public ArrayList<AlarmDataInfo> selectDataByGId(ArrayList<String> gid,boolean all,int top,int count) {
		String wsql="";
		if (!all) {
			if (gid!=null) {
				String id="";
				for (int i = 0; i < gid.size(); i++) {
					if (i==gid.size()-1) {
						id+=gid.get(i);
					}else {
						id+=gid.get(i)+",";
					}
				}
				wsql=" and nGroupId in("+id+") "+" limit "+(top-1)+","+count;
			}
		}else {
			wsql=" limit "+(top-1)+","+count;
		}
		
		String sql="";
		sql = "select * from  dataAlarm "
			+ "where nClear=0 "+wsql;
		
		return selectDataListBySql(sql, null);
	}

	public int getAlarmData(ArrayList<String> gid,boolean all) {
		int result=0;
		String wsql="";
		if (!all) {
			if (gid!=null) {
				String id="";
				for (int i = 0; i < gid.size(); i++) {
					if (i==gid.size()-1) {
						id+=gid.get(i);
					}else {
						id+=gid.get(i)+",";
					}
				}
				wsql=" and nGroupId in("+id+") ";
			}
		}
		
		String sql="";
		sql = "select count(*) from  dataAlarm "
			+ "where nClear=0 "+wsql;
		
		SKDataBaseInterface data=SkGlobalData.getAlarmDatabase();
		Cursor cursor = null;
		try {
			if (data != null) {
				cursor = data.getDatabaseBySql(sql, null);
				if (cursor != null) {
					if(cursor.moveToNext()){
						result=cursor.getInt(0);
					}
				}
				close(cursor);
			}
		} catch (Exception e) {
			close(cursor);
			e.printStackTrace();
			Log.d("AlarmBiz", "Alarm getAlarmData error!");
		}
		
		return result;
	}

	/**
	 * 根据sql,获取报警数据
	 */
	private ArrayList<AlarmDataInfo> selectDataListBySql(String sql,
			String param[]) {
		long start=System.currentTimeMillis();
		
		/**
		 * 获取当前语言报警文本
		 */
		HashMap<Integer, HashMap<Integer, String>> mGText=null;
		if (mAlarmText.containsKey(SystemInfo.getCurrentLanguageId())) {
			mGText=mAlarmText.get(SystemInfo.getCurrentLanguageId());
		}else {
			mGText=mAlarmText.get(0);
		}
		
		ArrayList<AlarmDataInfo> list = null;
		SKDataBaseInterface data=SkGlobalData.getAlarmDatabase();
		Cursor cursor = null;
		
		try {
			if (data != null) {
				cursor = data.getDatabaseBySql(sql, param);
				if (cursor != null) {
					list = new ArrayList<AlarmDataInfo>();
					while (cursor.moveToNext()) {
						AlarmDataInfo info = new AlarmDataInfo();
						info.setId(cursor.getInt(0));
						info.setnGroupId(cursor.getInt(1));
						info.setnAlarmIndex(cursor.getInt(2));
						info.setnDateTime(cursor.getLong(3));
						info.setnClearDT(cursor.getLong(4));
						info.setnClear(cursor.getShort(5));
						list.add(info);
						if (mGText!=null) {
							info.setsMessage(mGText.get(info.getnGroupId()).get(info.getnAlarmIndex())+"");
						}else {
							info.setsMessage("");
						}
					}
					close(cursor);
				}
			}
		} catch (Exception e) {
			close(cursor);
			e.printStackTrace();
			Log.d("AlarmBiz", "Alarm get data error!");
		}
		
		//Log.d("SKScene", "alarm select time:"+(System.currentTimeMillis()-start));
		return list;
	}
	
	
	/**
	 * - 进行校正 报警信息的 语言
	 * @param slist
	 */
	public ArrayList<AlarmDataInfo>  modifyLanguage(ArrayList<AlarmDataInfo> slist){
		if(slist == null || slist.size() == 0){
			return slist;
		}
		
		HashMap<Integer, HashMap<Integer, String>> mGText=null;
		if (mAlarmText.containsKey(SystemInfo.getCurrentLanguageId())) {
			mGText=mAlarmText.get(SystemInfo.getCurrentLanguageId());
		}else {
			mGText=mAlarmText.get(0);
		}
		if(mGText == null){
			return slist;
		}
		
		ArrayList<AlarmDataInfo> list = new ArrayList<AlarmDataInfo>();
		for(int i = 0; i < slist.size(); i++){
			AlarmDataInfo info = slist.get(i);
			info.setsMessage(mGText.get(info.getnGroupId()).get(info.getnAlarmIndex())+"");
			
			list.add(info);
		}
		
		return list;
		
	}
	
	public ArrayList<AlarmDataInfo> selectAllMsg(long start,long end,int top,int count){
		
		String sql="";
		if (start==0&&end==0) {
			sql="select * from  dataAlarm limit "+(top-1)+","+count;
		}else {
			sql="select * from dataAlarm where nDateTime>="+start
				+" and nDateTime<="+end
				+" limit "+(top-1)+","+count;
		}
		
		return selectDataListBySql(sql, null);
		
	}
	
	/**
     * 获取报警数据
     * @param aName-报警组名称
     * @param nTop-起始序号
     * @param num-获取行数
     * @param sTime-开始时间 ，格式为 “2013-11-24 22:12:00”
     * @param eTime-结束时间，格式为“2013-11-25 12:00:00”
     */
    public ArrayList<String[]> getAlarmData(String aName,int nTop,int num,String sTime,String eTime){
		ArrayList<String[]> data=null;
		return data;
	}
	
	/**
	 * 查询总共有多少条报警
	 * @param clear=-1 查询所有
	 *        clear=0 查询报警总数
	 *        clear=1 查询确定报警总数
	 *        clear=2 查询消除报警总数
	 * @param time=0 不启动时间范围 1=启动
	 * @param star=开始时间
	 * @param end=结束时间
	 */
	public int selectAlarmCount(int clear,int time,long star,long end){
		SKDataBaseInterface data=SkGlobalData.getAlarmDatabase();
		Cursor cursor=null;
		int count=0;
		if (data!=null) {
			try {
				String sql="";
				String sTime="";
				if (clear==-1) {
					if (time==1) {
						sTime=" where nDateTime>="+star+" and nDateTime<="+end;
					}
					sql="select count(*) from  dataAlarm "+sTime;
				}else {
					if (time==1) {
						sTime=" and nDateTime>="+star+" and nDateTime<="+end;
					}
					sql="select count(*) from  dataAlarm where nClear="+clear+sTime;
				}
				cursor=data.getDatabaseBySql(sql, null);
				if (cursor!=null) {
					while(cursor.moveToNext()){
						count=cursor.getInt(0);
					}
				}
				close(cursor);
			} catch (Exception e) {
				close(cursor);
				
				e.printStackTrace();
				Log.e("AlarmBiz", "AlarmBiz selectAlarmCount error!");
			}
		}
		return count;
	}

	/**
	 * 报警确定
	 * @param gId-报警组id
	 * @param aId-报警在报警组中的序号
	 */
	public void updateData(){
		SKDataBaseInterface data=SkGlobalData.getAlarmDatabase();
		if (data != null) {
			try {
				ContentValues values=new ContentValues();
				values.put("nClear", 1);
				data.updateByUserDef("dataAlarm",values,"nClear=0",null);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("AlarmBiz", "alarm update data error!");
			}
		}
	}
	
	/**
	 * 报警确定
	 * @param gId-报警组id
	 * @param aId-报警在报警组中的序号
	 */
	public void updateData(String gid,String aid){
		SKDataBaseInterface data=SkGlobalData.getAlarmDatabase();
		if (data != null) {
			try {
				if (gid.equals("")||aid.equals("")) {
					return;
				}
				ContentValues values=new ContentValues();
				values.put("nClear", 1);
				data.updateByUserDef("dataAlarm",values,"nGroupId=? and nAlarmIndex=?",new String[]{gid,aid});
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("AlarmBiz", "alarm update data error!");
			}
		}
	}

    /**
     * 报警数据保存
     * @param data-报警信息
     * @param clear-ture，存满清除数据
     * @param type=1 确定所有报警，type=2 删除所有报警，type=3，清除历史报警
     * @param group 要进行信息处理的报警组
     */
	private SQLiteStatement insert;
	public synchronized int saveAlarmData(HashMap<Integer, Vector<AlarmDataInfo>> data,boolean clear,int type, ArrayList<Integer> group){
		int count=0;
		int changeCount = 0;
		SKDataBaseInterface dataDB = SkGlobalData.getAlarmDatabase();
		if (dataDB!=null) {
			try {
				
				if (clear) {
					//采满删除数据,删除一定要放到事务之外，因为事务是全部执行完才提交，先删除数据，可以减少数据量
					int num=0;
					if(data!=null){
						if(data.containsKey(0)){
							num+=data.get(0).size();
						}
						if (data.containsKey(1)) {
							num+=data.get(1).size();
						}
					}
					deleteData(dataDB,num);
				}
				
				//先删除处于报警和确定的信息，再插入
				dataDB.deleteByUserDef("dataAlarm", "nClear=0 or nClear=1 ", null);
				
				dataDB.beginTransaction();//开始事务
				
				//插入语句
				String insertSql="insert into dataAlarm(nGroupId,nAlarmIndex,nDateTime,nClearDT,nClear) values(?,?,?,?,?)";
				insert=dataDB.compileStatement(insertSql);
				
				
				Vector<AlarmDataInfo> temp=null;
				
				if (data!=null) {
					if (data.containsKey(0)) {
						temp=data.get(0);
						count=temp.size();
						//Log.d("AlarmBiz", "temp0:"+temp.size());
					}
					if (temp!=null) {
						for (int i = 0; i < temp.size(); i++) {
							insert(temp.get(i), dataDB);
							temp.remove(i);
							i--;
						}
						temp.clear();
					}
					
					if (data.containsKey(1)) {
						temp=data.get(1);
						count+=temp.size();
						//Log.d("AlarmBiz", "temp1:"+temp.size());
					}
					if (temp!=null) {
						for (int i = 0; i < temp.size(); i++) {
							insert(temp.get(i), dataDB);
							temp.remove(i);
							i--;
						}
						temp.clear();
					}
				}
				
		
				dataDB.commitTransaction();
				//dataDB.endTransaction();//结束事务。
				
				if (type==1) {
					//确定报警
					ContentValues values=new ContentValues();
					values.put("nClear", 1);
					
					if (group != null) {
						changeCount = dataDB.updateByUserDef("dataAlarm",values,"nClear=0 and nGroupId in (" +arry2String(group)+")",null);

					}
					else {
						changeCount = dataDB.updateByUserDef("dataAlarm",values,"nClear=0",null);
					}
					
					
				}else if (type==2) {
					//清除报警
					int num = 0;
					if (group != null) {
						changeCount = num = dataDB.deleteByUserDef("dataAlarm", "nClear=0 and nGroupId in ("+arry2String(group)+")",null);
					}else {
						changeCount = num = dataDB.deleteByUserDef("dataAlarm", "nClear=0",null);
					}
					
		    		if (num>0) {
		    			if(SKSceneManage.getInstance().mContext!=null){
		    				SKToast.makeText(SKSceneManage.getInstance().mContext, 
		    						SKSceneManage.getInstance().mContext.getString(R.string.alarm_delete),
									Toast.LENGTH_SHORT).show();
						}
					}
				}else if(type==3){
					//删除历史报警
					int num = 0;
				
					if (group != null) {
						changeCount=num=dataDB.deleteByUserDef("dataAlarm", "(nClear=1 or nClear=2 )and nGroupId in ("+arry2String(group)+")", null);
						changeCount = num;
					}else {
						changeCount=num=dataDB.deleteByUserDef("dataAlarm", "nClear=1 or nClear=2", null);
					}
					
					String msg="";
					if (num>0) {
						msg=SKSceneManage.getInstance().mContext.getString(R.string.his_alarm_delete);
					}else {
						msg=SKSceneManage.getInstance().mContext.getString(R.string.no_data);
					}
					if(SKSceneManage.getInstance().mContext!=null){
						SKToast.makeText(SKSceneManage.getInstance().mContext, msg,
								Toast.LENGTH_SHORT).show();
					}
				}else if (type == 4  && group != null) {
					//进行删除单个报警
					int gid = group.get(0);
					int aid = group.get(1);
					changeCount=dataDB.deleteByUserDef("dataAlarm", "(nClear=1 or nClear=2 )and nGroupId ="+gid + " and nAlarmIndex = " + aid , null);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("AlarmBiz", "save alarm data fail!");
			}finally{
				dataDB.endTransaction();//结束事务。
			}
		}
		return changeCount ;
	}
	
	private String arry2String(ArrayList<Integer> list){
		StringBuffer buffer = new StringBuffer();
		for(int bean : list){
			buffer.append(bean).append(",");
		}
		buffer.deleteCharAt(buffer.length()-1);
		
		return buffer.toString();
		
	}
	
	/**
	 * 插入数据
	 */
	@SuppressLint("NewApi")
	private void insert(AlarmDataInfo info,SKDataBaseInterface dataDB){
		int index=1;
		if (insert!=null) {
			insert.bindLong(index++, info.getnGroupId());
			insert.bindLong(index++, info.getnAlarmIndex());
			insert.bindLong(index++, info.getnDateTime());
			insert.bindLong(index++, info.getnClearDT());
			insert.bindLong(index++, info.getnClear());
			insert.executeInsert();
		}
	}
	
	/**
	 * 存满15w，删除旧数据
	 */
	private void deleteData(SKDataBaseInterface dataDB,int count){
		String sql="select min(id) as id from dataAlarm";
		Cursor cursor=dataDB.getDatabaseBySql(sql, null);
		if (cursor!=null) {
			int id=0;
			while (cursor.moveToNext()) {
				id=cursor.getInt(cursor.getColumnIndex("id"));
			}
			Log.d("SKScene", "alarm data full min id:"+id);
			close(cursor);
			dataDB.deleteByUserDef("dataAlarm", " id between ? and ? ", new String[]{id+"",(id+count)+""});
		}
	}
	
	/**
	 * 掉电数据恢复
	 */
	public void restoreData(HashMap<Long, AlamSaveProp> data){
		SKDataBaseInterface dataDB = SkGlobalData.getAlarmDatabase();
		if (dataDB!=null) {
			try {
				dataDB.beginTransaction();//开始事务
				Iterator<Entry<Long, AlamSaveProp>> iter=data.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry<Long, AlamSaveProp> entry=(Map.Entry<Long, AlamSaveProp>)iter.next();
					AlamSaveProp val = entry.getValue();
					dataDB.deleteByUserDef("dataAlarm", "nGroupId=? and nAlarmIndex=? and nDateTime=?", 
							new String[]{val.nGroupId+"",val.nIndex+"",val.nAlamTime+""});
					ContentValues values = new ContentValues();
					values.put("nGroupId", val.nGroupId);
					values.put("nAlarmIndex", val.nIndex);
					values.put("nDateTime", val.nAlamTime);
					values.put("nClearDT", val.nRemoveAlamTime);
					values.put("nClear", val.nStatus);
					dataDB.insertData("dataAlarm", values);
					//Log.d("SKScene", "gid"+val.nGroupId+",adi:"+val.nIndex+",time:"+val.nAlamTime);
				}
				
				dataDB.commitTransaction();//提交事务，保存
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("AlarmBiz", "restore alarm data error!");
			}finally{
				dataDB.endTransaction();//结束事务。
			}
		}
	}
	
	/**
	 * 存储路径
	 */
	private boolean bWrite;
	private AKHintDialog dialog;
	public boolean exportData(String path){
		
		if (bWrite) {
			return false;
		}
		bWrite = true;
		boolean reuslt = false;
		
		try {

			Context context = SKSceneManage.getInstance().mContext;
			if (context != null) {
				dialog = new AKHintDialog(context);
				dialog.showPopWindow("正在导出数据...");
			}

			Calendar c = Calendar.getInstance();
			String time = c.get(Calendar.YEAR) + "-"
					+ (c.get(Calendar.MONTH) + 1) + "-"
					+ c.get(Calendar.DAY_OF_MONTH) + "_" + c.get(Calendar.HOUR)
					+ "-" + c.get(Calendar.MINUTE) + "-"
					+ c.get(Calendar.SECOND);

			String folderName = "ak_alarm";
			String fileName = "alarm_" + time;
			String name = "";

			File folerFile = new File(path + folderName);
			if (!folerFile.exists()) {
				folerFile.mkdir();
			}

			name = path + folderName + "/" + fileName + ".csv";
			File file = new File(name);

			// 标题
			ArrayList<String> title = new ArrayList<String>();
			if (db != null) {
				Cursor cursor = null;
				String sql = "select id,sTime,sDate,sClearDate,sClearTime,sMessage from alarmTitle where nLanguageIndex=? limit 1";
				cursor = db
						.getDatabaseBySql(
								sql,
								new String[] { SystemInfo
										.getCurrentLanguageId() + "" });
				if (cursor != null) {
					while (cursor.moveToNext()) {
						title.add(cursor.getString(cursor.getColumnIndex("id")));
						title.add(cursor.getString(cursor
								.getColumnIndex("sTime")));
						title.add(cursor.getString(cursor
								.getColumnIndex("sDate")));
						title.add(cursor.getString(cursor
								.getColumnIndex("sClearDate")));
						title.add(cursor.getString(cursor
								.getColumnIndex("sClearTime")));
						title.add(cursor.getString(cursor
								.getColumnIndex("sMessage")));
					}
				}
				close(cursor);
			}
			if (title.size() < 6) {
				title.clear();
				title.add("id");
				title.add("时间");
				title.add("日期");
				title.add("消除日期");
				title.add("消除时间");
				title.add("消息");
			}

			// 保存标题信息

			DataOutputStream fileWriteHand = new DataOutputStream(
					new FileOutputStream(file));
			BufferedWriter writeBuffer = new BufferedWriter(
					new OutputStreamWriter(fileWriteHand, "GBK"));

			/* 先写标题 */
			writeBuffer.newLine();
			String sTitle = "";
			for (int i = 0; i < title.size(); i++) {
				if (i < title.size() - 1) {
					sTitle += title.get(i) + ",";
				} else {
					sTitle += title.get(i);
				}
			}
			writeBuffer.write(sTitle);

			/**
			 * 获取当前语言报警文本
			 */
			HashMap<Integer, HashMap<Integer, String>> mGText = null;
			if (mAlarmText.containsKey(SystemInfo.getCurrentLanguageId())) {
				mGText = mAlarmText.get(SystemInfo.getCurrentLanguageId());
			} else {
				mGText = mAlarmText.get(0);
			}

			SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat mTimeFormat = new SimpleDateFormat("HH:mm:ss");

			// 写数据
			SKDataBaseInterface dataDB = SkGlobalData.getAlarmDatabase();
			if (dataDB != null) {
				String sql = "";
				sql = "select * from  dataAlarm ";
				Cursor cursor = null;
				cursor = dataDB.getDatabaseBySql(sql, null);
				int id = 0;
				if (cursor != null) {
					String data = "";
					String sDate = "";
					String sTime = "";
					int gid;
					int aid;
					while (cursor.moveToNext()) {
						/* 写一行数据 */
						data = "";
						id++;
						data += id + ",";
						gid = cursor.getInt(1);
						aid = cursor.getInt(2);

						long date = cursor.getLong(cursor
								.getColumnIndex("nDateTime"));
						Date date2 = new Date(date);
						sDate = mDateFormat.format(date2);
						sTime = mTimeFormat.format(date2);
						if (sTime == null) {
							data += " , ";
						} else {
							data += sTime + ",";
						}
						if (sDate == null) {
							data += " ,";
						} else {
							data += sDate + ",";
						}

						long cdate = cursor.getLong(cursor
								.getColumnIndex("nClearDT"));
						if (cdate == 0) {
							// 未消除的
							sDate = "--";
							sTime = "--";
						} else {
							// 已经消除的
							Date date3 = new Date(cdate);
							sDate = mDateFormat.format(date3);
							sTime = mTimeFormat.format(date3);
						}

						if (sDate == null) {
							data += " ,";
						} else {
							data += sDate + ",";
						}

						if (sTime == null) {
							data += " ,";
						} else {
							data += sTime + ",";
						}

						if (mGText != null) {
							data += mGText.get(gid).get(aid) + "";
						} else {
							data += " ";
						}

						writeBuffer.newLine();
						writeBuffer.write(data);
					}
				}
				close(cursor);
			}
			
			writeBuffer.flush();
			writeBuffer.close();
			fileWriteHand.close();

			reuslt = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bWrite = false;
			if (dialog != null) {
				dialog.hidePopWindow();
			}
		}
		return reuslt;
	}
	
}
