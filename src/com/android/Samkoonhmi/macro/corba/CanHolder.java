package com.android.Samkoonhmi.macro.corba;

import com.android.Samkoonhmi.can.can;
import com.android.Samkoonhmi.can.model.bittiming;


/**
 * Canbus通信
 */
public class CanHolder extends PHolder{

	//CANBus id
	public int nComId;
	
	
	/**
	 * 重启 CANBus
	 * @canid comId COM序号  0=COM2,1=COM1
	 * @return 重启结果 0-成功，其他值则表示失败
	 */
	public int  onRestart(int comId){
		if (comId<0||comId>1) {
			return -1;
		}
		//System.out.println("CANBus onRestart ...");
		nComId=comId;
		return can.getInstance().onRestart(comId);
	}
	
	/**
	 * 启动 CANBus
	 * @canid comId COM序号  0=COM2,1=COM1
	 * @return 启动结果 0-启动成功，其他则表示失败
	 */
	public int onStart(int comId){
		if (comId<0||comId>1) {
			return -1;
		}
		//System.out.println("CANBus onStart ...");
		nComId=comId;
		return can.getInstance().onStart(comId);
	}
	
	/**
	 * 设置波特率
	 * @param bitrate-波特率
	 * @param comId COM序号  0=COM2,1=COM1
	 * @return 设置结果 0-成功，其他值表示失败
	 */
	public int setBitrate(int bitrate,int comId){
		if (bitrate<0||comId<0||comId>1) {
			return -1;
		}
		//System.out.println("CANBus bitrate="+bitrate+",id="+id);
		bittiming bTiming=new bittiming();
		bTiming.bitrate=bitrate;
		return can.getInstance().setBitrate(bTiming, comId);
	}
	
	/**
	 * 发送数据
	 * @param data-数据
	 * @param frameextended-扩展，0-不扩展，1-扩展
	 * @param comId COM序号  0=COM2,1=COM1
	 * @return 发送结果 0-成功，其他值表示失败
	 */
	public int onSend(byte[] data,int frameextended,int comId){
		if (data==null||data.length==0||comId<0||comId>1) {
			return -1;
		}
		//System.out.println("CanHolder onSend...");
		return can.getInstance().onSend(data, frameextended, comId);
	}
	
	/**
	 * 发送数据
	 * @param data-数据
	 * @param frameextended-扩展，0-不扩展，1-扩展
	 * @param comId COM序号  0=COM2,1=COM1
	 * @return 发送结果 0-成功，其他值表示失败
	 */
	public int onSend(byte[] data,int frameextended,int canId,int comId){
		if (data==null||data.length==0||comId<0||comId>1) {
			return -1;
		}
		//System.out.println("CanHolder onSend...");
		return can.getInstance().onSend(data, frameextended,canId, comId);
	}
	
	/**
	 * 接收数据
	 * @param data-数据
	 * @param comId COM序号  0=COM2,1=COM1
	 * @return 返回数据长度
	 */
	public int onDump(byte[] data,int comId){
		if (data==null||data.length==0||comId<0||comId>1) {
			return -1;
		}
		return can.getInstance().onDump(data, comId);
	}
	
	/**
	 * 接收数据
	 * @param data-数据
	 * @param comId COM序号  0=COM2,1=COM1
	 * @return 返回数据长度
	 */
	public int onDump(int[] data,int comId){
		if (data==null||data.length==0||comId<0||comId>1) {
			return -1;
		}
		return can.getInstance().onDump(data, comId);
	}
	
	/**
	 * 接收数据
	 * @param data-数据
	 * @param comId COM序号  0=COM2,1=COM1
	 * @return 返回数据长度
	 */
	public int onStop(int comId){
		if (comId<0||comId>1) {
			return -1;
		}
		return can.getInstance().onStop(comId);
	}
	
	/**
	 * 过滤
	 * @param comId-COM
	 */
	public int fillter(int id,int mask,int comId){
		//System.out.println("CanHolder fillter...");
		if (comId<0||comId>1) {
			return -1;
		}
		return can.getInstance().filter(id, mask, comId);
	}
	
	/**
	 * 设置缓存长度
	 */
	public int setCacheLen(int len){
		return can.getInstance().setCacheLen(len);
	}

}
