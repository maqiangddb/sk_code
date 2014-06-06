package com.android.Samkoonhmi.system;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

import android.content.Context;
import android.util.Log;

import com.android.Samkoonhmi.SKTimer;
import com.android.Samkoonhmi.model.timeSchedule.TimeScheduleControlInfo;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.READ_WRITE_COM_TYPE;
import com.android.Samkoonhmi.util.AddrProp;
import com.android.Samkoonhmi.util.DataTypeFormat;
import com.android.Samkoonhmi.util.SEND_DATA_STRUCT;

public class SystemTimeSchedule implements SKTimer.ICallback{
	private static SystemTimeSchedule instance;
	private Context mContext;
	private ArrayList<TimeScheduleControlInfo> timeScheduleControlInfoList;
	private String TAG = "SystemTimeSchedule";
	
	public SystemTimeSchedule(Context mContext) {
		this.mContext = mContext;
		timeScheduleControlInfoList = new ArrayList<TimeScheduleControlInfo>();
	}

	/**
	 * 获取实例
	 * @param mContext
	 * @return
	 */
	public static SystemTimeSchedule getInstance(Context mContext){
		if(instance == null){
			instance = new SystemTimeSchedule(mContext);
		}
		return instance;
	}
	
	/**
	 * schedule 信息
	 * @param list
	 */
	public void setInfoList(ArrayList<TimeScheduleControlInfo> list){
		timeScheduleControlInfoList.clear();
		timeScheduleControlInfoList=list;
	}
	
	public ArrayList<TimeScheduleControlInfo> getInfo(){
		return this.timeScheduleControlInfoList;
	}
	
	/**
	 * 执行schedule
	 * @param info
	 * @param timeStamp 
	 * @param weekDay 
	 */
	private void runSchedule(TimeScheduleControlInfo info, short weekDay, int timeStamp){
		if(isRunTime(info, weekDay, timeStamp)){
//				&&(Math.abs(System.currentTimeMillis()-info.getLastTime())>60*1000)){//一分钟之内不能执行2次
			setAddrValue(info);
			info.setLastTime(System.currentTimeMillis());
			Log.v(TAG,"runSchedule:weekDay = "+weekDay+";timeStamp = "+timeStamp);
		}
	}
	
	//判断是否运行的时间
	private boolean isRunTime(TimeScheduleControlInfo info, short weekDay, int timeStamp){
		boolean result = false;
		if(info.getWeekDate() == weekDay){
			if(info.getTimeControl()){
				SEND_DATA_STRUCT mSendData = new SEND_DATA_STRUCT();
				mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_R;
				mSendData.eDataType = DATA_TYPE.INT_16;
				Vector<Integer> dataList = new Vector<Integer>();
				PlcRegCmnStcTools.getRegIntData(info.getAddrTime(), 
						dataList, mSendData);
				if(dataList.size() == 3){
					int infoTimeStamp = (dataList.get(0)%100)*10000
							+(dataList.get(1)%100)*100
							+(dataList.get(2)%100);
					if(infoTimeStamp == timeStamp){
						result = true;
					}
				}
			}else{
				if(info.getActionTimeStamp() == timeStamp){
					result = true;
				}
			}
		}
		return result;
	}
	
	/**
	 * 地址赋值
	 * @param info
	 */
	private void setAddrValue(TimeScheduleControlInfo info){
		
		SEND_DATA_STRUCT mSendData = new SEND_DATA_STRUCT();
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		mSendData.eDataType = DATA_TYPE.BIT_1;
		switch(info.getActionType()){
			case 0://bit set
				mSendData.eDataType = DATA_TYPE.BIT_1;
				if(info.getValueControl()){//值受控
					//获取值
					Vector<Byte> dataList = new Vector<Byte>();
					mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_R;
					PlcRegCmnStcTools.getRegBytesData(info.getAddrValue(), 
							dataList, mSendData);
					if(!dataList.isEmpty()){
						byte[] targetValue = new byte[1];
						targetValue[0] = dataList.get(0);
						mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
						PlcRegCmnStcTools.setRegBytesData(info.getActionAddr(), targetValue, mSendData);
					}
				}else{
					byte[] targetValue = new byte[1];
					targetValue[0] = (byte) info.getConstValue();
					PlcRegCmnStcTools.setRegBytesData(info.getActionAddr(), targetValue, mSendData);
				}
				break;
//			case 1://bit reset
//				mSendData.eDataType = DATA_TYPE.BIT_1;
//				byte[] targetValue = new byte[1];
//				targetValue[0] = (byte) 0;
//				PlcRegCmnStcTools.setRegBytesData(info.getActionAddr(), targetValue, mSendData);
//				break;
			case 1://bit xor
				mSendData.eDataType = DATA_TYPE.BIT_1;
				Vector<Byte> dataList = new Vector<Byte>();
				PlcRegCmnStcTools.getRegBytesData(info.getActionAddr(), 
						dataList, mSendData);
				if(!dataList.isEmpty()){
					byte[] sourceValue = new byte[1];
					sourceValue[0] = (byte) (dataList.get(0)==0?1:0);
					PlcRegCmnStcTools.setRegBytesData(info.getActionAddr(), sourceValue, mSendData);
				}
				break;
			case 2://word set
				mSendData.eDataType = info.getDataType();
				if(info.getValueControl()){
					Vector<Byte> wordByteList = new Vector<Byte>();
					PlcRegCmnStcTools.getRegBytesData(info.getAddrValue(), 
							wordByteList, mSendData);
					if(!wordByteList.isEmpty()){
						byte[] targetWordByte = new byte[wordByteList.size()];
						for(int i=0;i<wordByteList.size();i++){
							targetWordByte[i] = wordByteList.get(i);
						}
						PlcRegCmnStcTools.setRegBytesData(info.getActionAddr(), targetWordByte, mSendData);
					}
				}else{
					setWordData(info.getActionAddr(), info.getConstValue(), mSendData);
				}
				break;
			default:
				break;
		}
	}
	
	/**
	 * 写入字类型数据
	 * @param mAddrProp
	 * @param value
	 * @param mSendData
	 */
	private void setWordData(AddrProp mAddrProp, double value, SEND_DATA_STRUCT mSendData ){
		switch(mSendData.eDataType){
			case INT_16:
			case INT_32:	
			case POSITIVE_INT_16:
				Vector<Integer> intList = new Vector<Integer>();
				intList.add((int) value);
				PlcRegCmnStcTools.setRegIntData(mAddrProp, intList, mSendData);
				break;
				
			case BCD_16:
				int valueInt = (int)value;
				String bcdString = valueInt+"";
				long bcdValue = 0;
				try{
					bcdValue = DataTypeFormat.bcdStrToInt(bcdString, 16);
				}catch (Exception e) {
					e.printStackTrace();
				}
				
				Vector<Integer> intListBcd = new Vector<Integer>();
				intListBcd.add((int) bcdValue);
				PlcRegCmnStcTools.setRegIntData(mAddrProp, intListBcd, mSendData);
				break;
			case BCD_32:
				System.out.println("BCD 32");
				long valueLong = (long)value;
				String bcdString32 = valueLong+"";
				long bcdValue32 = 0;
				try{
					bcdValue32 = DataTypeFormat.bcdStrToInt(bcdString32, 32);
				}catch (Exception e) {
					e.printStackTrace();
				}

				Vector<Long> longListBcd = new Vector<Long>();
				longListBcd.add(bcdValue32);
				PlcRegCmnStcTools.setRegLongData(mAddrProp, longListBcd, mSendData);
				break;
			
			case POSITIVE_INT_32:
				Vector<Long> longList = new Vector<Long>();
				longList.add((long) value);
				PlcRegCmnStcTools.setRegLongData(mAddrProp, longList, mSendData);
				break;
				
			
				
			case FLOAT_32:
				Vector<Double> doubleList = new Vector<Double>();
				doubleList.add(value);
				PlcRegCmnStcTools.setRegDoubleData(mAddrProp, doubleList, mSendData);
				break;
		}
	}
	
	/**
	 * 注册
	 */
	public void regist(){
		if(this.timeScheduleControlInfoList!=null
				&&this.timeScheduleControlInfoList.size()>0){
			//注册定时器回调
			SKTimer.getInstance().getBinder().onRegister(this,10);
		}
		//注册地址回调
	}
	
	/**
	 * 取消注册
	 */
	public void unregist(){
		SKTimer.getInstance().getBinder().onDestroy(this,10);
	}


	@Override
	/**
	 * timer 回调
	 */
	public void onUpdate(){
		if(null != this.timeScheduleControlInfoList){
			//获得当前时间
			short weekDay = (short) (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1);
			int timeStamp = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)*10000
					+ Calendar.getInstance().get(Calendar.MINUTE)*100
					+ Calendar.getInstance().get(Calendar.SECOND);
			//循环执行schedule
			for(int i=0;i<this.timeScheduleControlInfoList.size();i++){
				TimeScheduleControlInfo info = this.timeScheduleControlInfoList.get(i);
				if(info.getActionControl()){//地址控制
					SEND_DATA_STRUCT mSendData = new SEND_DATA_STRUCT();
					mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_R;
					mSendData.eDataType = DATA_TYPE.BIT_1;
					Vector<Byte> dataList = new Vector<Byte>();
					PlcRegCmnStcTools.getRegBytesData(info.getActionControlAddr(), 
							dataList, mSendData);
					if(!dataList.isEmpty()){
						if(dataList.get(0)!=0){
							runSchedule(info,weekDay,timeStamp);
						}
					}
				}else{
					runSchedule(info,weekDay,timeStamp);
				}
			}
		}
	}
	
}
