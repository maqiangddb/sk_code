package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;

import android.database.Cursor;

import com.android.Samkoonhmi.model.timeSchedule.TimeScheduleControlInfo;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;
import com.android.Samkoonhmi.util.AddrProp;

public class TimeScheduleBiz extends DataBase{
	private SKDataBaseInterface db = null;
	
	public TimeScheduleBiz(){
		db=SkGlobalData.getProjectDatabase();
	}

	public ArrayList<TimeScheduleControlInfo> getTimeScheduleInfo(){
		ArrayList<TimeScheduleControlInfo> infoList = new ArrayList<TimeScheduleControlInfo>();
		if(null!=db){
			String sql = "select * from scheduler";
			Cursor cursor = db.getDatabaseBySql(sql,null);
			if(cursor!=null){
				while(cursor.moveToNext()){
					TimeScheduleControlInfo info = new TimeScheduleControlInfo();
					//所在行
					int index = cursor.getInt(cursor.getColumnIndex("actionIndex"));
					info.setActionIndex(index);
					//运行时间点是否受地址控制
					boolean isTimeControl = cursor.getShort(cursor.getColumnIndex("eTimeType"))==1;
					info.setTimeControl(isTimeControl);
					//运行的时间点
					if(isTimeControl){
						int timeAddrId = cursor.getInt(cursor.getColumnIndex("nTimeAddr"));
						AddrProp addr = SkGlobalData.getProjectDatabase().getAddrById(timeAddrId);
						info.setAddrTime(addr);
					}else{
						String time = cursor.getString(cursor.getColumnIndex("actionTime"));
//						info.setConstTime(time);
						String[] timeList = time.trim().split(":");
						if(timeList!=null && timeList.length>=3){
							try{
								int hour = Integer.parseInt(timeList[0]);
								int minute = Integer.parseInt(timeList[1]);
								int second = Integer.parseInt(timeList[2]);
								info.setActionTimeStamp(hour*10000+minute*100+second);
							}catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					//运行的日子
					short day = cursor.getShort(cursor.getColumnIndex("weekday"));
					info.setWeekDate(day);
					//操作类型
					short type = cursor.getShort(cursor.getColumnIndex("eActionType"));
					info.setActionType(type);
					//操作地址
					int actionAddrId = cursor.getInt(cursor.getColumnIndex("nActionAddr"));
					AddrProp actionAddr = SkGlobalData.getProjectDatabase().getAddrById(actionAddrId);
					info.setActionAddr(actionAddr);
					//数据类型
					short dataType = cursor.getShort(cursor.getColumnIndex("eDataType"));
					info.setDataType(getDataType(dataType));
					//数据是否受控
					boolean valueControl = cursor.getShort(cursor.getColumnIndex("eValueType"))==1;
					info.setValueControl(valueControl);
					//数据值
					double valueAddrIdD = cursor.getDouble(cursor.getColumnIndex("nValue"));
					if(valueControl){
						int valueAddrId = (int)valueAddrIdD;
						AddrProp valueAddr = SkGlobalData.getProjectDatabase().getAddrById(valueAddrId);
						info.setAddrValue(valueAddr);
					}else{
						info.setConstValue(valueAddrIdD);
					}
					//操作是否受地址控制
					boolean isControl = cursor.getString(cursor.getColumnIndex("bControl")).equals("true");
					info.setActionControl(isControl);
					if(isControl){
						int controlAddrId = cursor.getInt(cursor.getColumnIndex("nCtlAddr"));
						AddrProp controlAddr = SkGlobalData.getProjectDatabase().getAddrById(controlAddrId);
						info.setActionControlAddr(controlAddr);
					}
					infoList.add(info);
				}
				close(cursor);
			}
		}
		return infoList;
	}
	
	private DATA_TYPE getDataType(short dataType){
		DATA_TYPE type=DATA_TYPE.OTHER_DATA_TYPE;
		switch(dataType){
			case 0:
				type = DATA_TYPE.INT_16;
				break;
			case 1:
				type = DATA_TYPE.INT_32;
				break;
			case 2:
				type = DATA_TYPE.POSITIVE_INT_16;
				break;
			case 3:
				type = DATA_TYPE.POSITIVE_INT_32;
				break;
//			case 4:
//				type = DATA_TYPE.BCD_16;
//				break;
//			case 5:
//				type = DATA_TYPE.BCD_32;
//				break;
			case 4:
				type = DATA_TYPE.FLOAT_32;
				break;
		}
		return type;
	}
}
