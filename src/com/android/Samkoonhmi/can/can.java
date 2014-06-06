package com.android.Samkoonhmi.can;

import java.util.ArrayList;
import android.util.Log;

import com.android.Samkoonhmi.can.model.bittiming;
import com.android.Samkoonhmi.can.model.canframe;

public class can {
	private static final String TAG="CAN";
	private static ArrayList<CanInfo> mList=null;
	private static boolean isDump=false;

	//单例
	private static can sInstance = null;
	public synchronized static can getInstance() {
		if (sInstance == null) {
			sInstance = new can();
		}
		return sInstance;
	}
	
	public can(){
		mList=new ArrayList<CanInfo>();
	}
	
	/**
	 * 没启动成功，重新设置
	 * @param comId-COM序号
	 */
	private int reset(int comId){
		int result=-1;
		for (int i = 0; i < mList.size(); i++) {
			CanInfo info=mList.get(i);
			if(info.comId==comId){
				if (System.currentTimeMillis()-info.time>2000) {
					//启动时间间隔，大于2s，防止频繁启动
					if (info.bittiming!=null) {
						setbitrate(info.bittiming, comId);
					}
					result=onStart(comId);
					info.state=result;
					info.time=System.currentTimeMillis();
				}
				break;
			}
		}
		return result;
	}
	
	/**
	 * CanBus 状态监视
	 * @param comId-COM序号
	 */
	private int check(int comId){
		int result=-1;
		for (int i = 0; i < mList.size(); i++) {
			CanInfo info=mList.get(i);
			if (info.comId==comId) {
				result=info.state;
				if (result!=0) {
					result=reset(comId);
					break;
				}
			}
		}
		return result;
	}
	
	/**
	 * 重启那个COM的can
	 */
	public int onRestart(int comId){
		int result=-1;
		try {
			result=restart(comId);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "CanBus onRestart error!! ");
		}
		//Log.d(TAG, "onRestart..."+comId);
		
		return result;
		
	}
	
	/**
	 * 启动那个COM的can
	 */
	public int onStart(int comId){
		int result=-1;
		try {
			
			result=start(comId);
			boolean rt=false;
			CanInfo info=null;
			for (int i = 0; i < mList.size(); i++) {
				CanInfo temp=mList.get(i);
				if (temp.comId==comId) {
					info=temp;
					rt=true;
					break;
				}
			}
			if (!rt) {
				info=new CanInfo();
				mList.add(info);
			}
			info.comId=comId;
			info.state=result;
			info.time=System.currentTimeMillis();
			
			onStartDump(comId);
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "CanBus onStart error!!");
		}
		//Log.d(TAG, "onStart result"+result+",comid="+comId+",size="+mList.size());
		return result;
		
	}
	
	/**
	 * 设置波特率，设置之后，需要启动CAN
	 * @param bittiming 波特率
	 * @param comId COM 序号
	 */
	public int setBitrate(bittiming bTiming,int comId){
		//Log.d(TAG, "setBitrate..."+comId);
		//设置波特率之前先停止
		int result=-1;
		try {
			stop(comId);
			result=setbitrate(bTiming, comId);
			for (int i = 0; i < mList.size(); i++) {
				CanInfo info=mList.get(i);
				if (info.comId==comId) {
					info.bittiming=bTiming;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "CanBus setBitrate error!!");
		}
		return result;
	}
	
	/**
	 *  CANBus 数据发送
	 *  @param data-数据,最大长度为8
	 *  @param frameextended-扩展，0=不扩展，1=扩展
	 *  @param comId COM序号
	 */
	public int onSend(byte[] data,int frameextended, int comId){
		int result=-1;
		try {
			
			//检测
			int temp=check(comId);
			if (temp==0) {
				canframe frame=new canframe();
				//每次最多只能发送8个字节
				byte []dt=new byte[8]; 
				int len=data.length;
				if (len>8) {
					len=8;
				}
				frame.can_id=0;
				frame.data=dt;//数据
				frame.can_dlc=(byte)len;//数据长度
				for (int j = 0; j < len; j++) {
					dt[j]=data[j];
				}
				result=send(frame, frameextended, comId);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "CanBus onSend error!!");
		}
		
		return result;
	}
	
	/**
	 *  CANBus 数据发送
	 *  @param data-数据,长度最大为8
	 *  @param frameextended-扩展，0=不扩展，1=扩展
	 *  @param canId-CANBus id
	 *  @param comId-COM 序号
	 */
	public int onSend(byte[] data,int frameextended, int canId,int comId){
		
		int result=-1;
		try {
			
			//检测
			int temp=check(comId);
			if (temp==0) {
				canframe frame=new canframe();
				//每次最多只能发送8个字节
				byte []dt=new byte[8]; 
				frame.data=dt;//数据
				int len=data.length;
				if (len>8) {
					len=8;
				}
				
				byte dlc=(byte)len;//数据长度
				for (int j = 0; j < len; j++) {
					dt[j]=data[j];
				}
				frame.can_id=canId;
				frame.can_dlc=dlc;
				result=send(frame, frameextended, comId);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "CanBus onSend error!!");
		}
		
		return result;
	}
	
	
	/**
	 * 启动接收线程
	 */
	private int nComId=1;
	private void onStartDump(int comId){
		if (!isDump) {
			isDump=true;
			nComId=comId;
			new Thread(){

				@Override
				public void run() {
					super.run();
					startdump(nComId);
				}
				
			}.start();
		}
	}

	/**
	 * 接收数据
	 * @param data,长度最大为8
	 * @param com口序号，COM1-1,COM2-0
	 * @return 成功返回0,失败返回-1
	 */
	public int onDump(byte []data, int comId){
		int result=-1;
		try {
			
			//检测
			int temp=check(comId);
			if (temp==0) {
				canframe frame=new canframe();
				result=dump(frame,comId);
				if (result==0) {
					//读取成功
					//can 设备id
					int len=frame.can_dlc;
					if (len>data.length) {
						len=data.length;
					}
					for (int j = 0; j < len; j++) {
						data[j]=frame.data[j];
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "CanBus onDump error!!");
		}
		
		//Log.d(TAG, "onDump..."+comId+",result = "+result);
		
		return result;
	}
	
	/**
	 * 接收数据
	 * @param data,最长10个长度，0-can序号，1-数据长度
	 * @param com口序号，COM1-1,COM2-0
	 * @return 成功返回0,失败返回-1
	 */
	public int onDump(int []data, int comId){
		int size=data.length;
		if (size<3) {
			return -1;
		}
		int result=-1;
		
		try {
			
			//检测
			int rs=check(comId);
			if (rs==0) {
				byte[] temp=new byte[8];
				canframe frame=new canframe();
				frame.data=temp;
				result=dump(frame,comId);
				if (result==0) {
					//读取成功
					//can 设备id
					data[0]=frame.can_id;
					//can 数据长度
					data[1]=frame.can_dlc;
					int len=frame.data.length;
					if (len>data.length-2) {
						len=data.length-2;
					}
					for (int j = 0; j < len; j++) {
						data[j+2]=frame.data[j];
					}
				}
				//Log.d(TAG, "onDump..."+comId+",dd = "+temp[0]);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "CanBus onDump error!!");
		}
		
		return result;
	}
	
	/**
	 * 设置缓存长度
	 * 默认长度600帧
	 */
	public int setCacheLen(int len){
		return cacheLen(len);
	}
	
	/**
	 * CanBus 状态
	 */
	class CanInfo{
		int comId;//COM 序号
		bittiming bittiming;//波特率
		int state;//当前状态，0-启动成功，其他则表示失败
		long time;//启动时间，启动时间至少隔5s
	}
	
	/**
	 * 暂停设备
	 */
	public int onStop(int comId){
		isDump=false;
		return stop(comId);
	}
	
	public int filter(int id, int mask, int comId){
		return addfilter(id, mask, comId);
	}

	//重启
	private native int restart(int comId);
	//启动
	private native int start(int comId);
	//设置波特率
	private native int setbitrate(bittiming bitTiming, int comId);
	//发送数据
	private native int send(canframe frame, int frameextended, int comId);
	//启动接收线程
	private native int startdump(int comId);
	//接收
	private native int dump(canframe frame, int comId);
	//过滤
	private native int addfilter(int id, int mask, int comId);
	//停止
	private native int stop(int comId);
	//设置缓存长度
	private native int cacheLen(int len);
	
	static {
		System.loadLibrary("can_port");
	}
}
