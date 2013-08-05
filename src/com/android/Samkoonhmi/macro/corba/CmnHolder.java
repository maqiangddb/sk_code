package com.android.Samkoonhmi.macro.corba;

import java.util.Vector;

import com.android.Samkoonhmi.plccommunicate.CmnPortManage;
import com.android.Samkoonhmi.plccommunicate.SKCommThread;
import com.android.Samkoonhmi.skenum.CONNECT_TYPE;
import com.android.Samkoonhmi.util.TurnDataProp;

public class CmnHolder extends PHolder{
	
	public static final int BufLen = 2048;      //缓冲区长度 
	
	private  int portclean = 0;                //是否需要清楚串口缓冲区
	private  int wlen      = 0;                //写入长度	
	public  byte[] wv     = new byte[BufLen]; //写缓存	
	private  Vector<Byte> rv = new Vector<Byte>(); //读缓冲区
	protected long wact = 0; //写访问时间 
	private static boolean isSimpleCmn=false;
	
	public long gett()
	{
		return wact;
	}
	public void sett(long act){
		wact = act;
	}
	
	/**
	 * 数据写入到写缓存的长度
	 * */
	public void setwlen(int len){
		if(len > this.BufLen){
			len = this.BufLen;
		}
		this.wlen =len;
	}
	
	/**
	 * 获得写入到写缓存的长度
	 * */
	public int getwlen(){
		return this.wlen;
	}
	
	/**
	 * 清除读缓存
	 * */
	public void rclear(){
		this.rv.clear();
	}
	
	/**
	 * 获得读缓存引用
	 * */
	
	public Vector<Byte> getrvRef(){
		return this.rv;
	}
	
	/**
	 * 获得读缓存长度
	 * */
	
	public int getrvRefLength(){
		return this.rv.size();
	}

	/**
	 * 清除写缓存
	 * */
	public void wclear(){
		if(null ==  this.wv){
			return;
		}
		this.wlen = 0;
		for(int i = 0; i < wv.length; i++){
			wv[i] = 0;
		}
	}
	
	/**
	 * 获得写缓存引用
	 * */
	public byte[] getwvRef(){
		return this.wv;
	}
		
	/**
	 * 设置需要刷新自由口缓存
	 * */
    public void setPortClear(){
    	this.portclean = 1;
    }
    
    /**
	 * 复位需要刷新自由口缓存
	 * */
    public void resetPortClear(){
    	this.portclean = 0;
    }
    
    /**
     * 获得自由口缓存刷新标识
     * */
    public int getPortClear(){
    	return this.portclean;
    }
	
	/**
	 * 从读缓存中出读出数据
	 * */
	public int get(byte[] userbuf){
		
		if(null == userbuf){
			return -1;
		}
		
		int rlen = userbuf.length;
		
		if(rlen > this.rv.size()){//若取数据长度大于读缓存有效数据长度
		   rlen = this.rv.size();
		}
		
		if(rlen > this.BufLen){//读取数据超过读缓存长度
			rlen = this.BufLen;
		}
		
		for(int i = 0; i<rlen; i++){
			userbuf[i] = rv.get(i);
		}
		return rlen;
	}
		
	/**
	 * 获得缓存长度
	 * */
	public int length(){
		return this.BufLen;
	}
	
	/**
	 * 写入数据到写缓存
	 * */
	public void set(byte[] s){
		
		int len = s.length;
		if(len > this.wv.length){
			len = this.wv.length;
		}
		for(int i = 0; i < len; i++){
			this.wv[i] = s[i];	
		}
		this.wlen = len;
	}
	
	/**
	 * 同步端口，写读缓存
	 * @param bArray 写入缓存数据
	 * @param vArray 读取数据输出
	 * @param nDelayTime 定时
	 * @param bInputFlag 条件判断标示--	0：收到bStopChar所指定的字符马上结束
										1：收到nReadLength个字符后马上结束
										2：延时nDelayTime后接收串口所有数据，结束
										3：满足0或1中的条件马上结束。如果操作的时间超过了nDelayTime设定的时间则无条件退出。
	 * @param nReadLength 需要读取的字符长度
	 * @param bStopChar 停止字符
	 * @return
	 */
	public synchronized int WriteAndReadComm(byte bArray[],int nDelayTime,int bInputFlag,int nReadLength,byte bStopChar){
		isSimpleCmn = true;
		int ret=-1;
		if(bArray.length==0){
			System.out.println("bArray is Null");
			return ret;
		}
		Vector<Byte> buff=new Vector<Byte>();
		
		TurnDataProp ComDP = new TurnDataProp();
		//只可以指定端口使用
		if(this.getName().equals("CmnCom1")){
			ComDP.eConnect = CONNECT_TYPE.COM1;
		}else if(this.getName().equals("CmnCom2")){
			ComDP.eConnect = CONNECT_TYPE.COM2;
		}else if(this.getName().equals("CmnNet0")){
			ComDP.eConnect = CONNECT_TYPE.NET0;
		}else{
			return ret;
		}
		//发送数据
		WriteImmediately(ComDP,bArray);
		//获得数据
		buff = ReadInTime(ComDP,nDelayTime,bInputFlag,nReadLength,bStopChar);
		//写入返回数据
		ret=bArray.length>buff.size()?buff.size():bArray.length;
		if(((bInputFlag==1)||(bInputFlag==3))&&(nReadLength<ret)){
			ret=nReadLength;
		}
		for(int i=0;i<bArray.length;i++){
			if(ret>i){
				bArray[i]=buff.get(i);
			}else{
				bArray[i]=0;
			}
		}
		isSimpleCmn=false;
		return ret;
	}
	
	/**
	 * 写缓存并同步到端口
	 * @param ComDP 端口参数
	 * @param bArray 写入数据
	 */
	public synchronized void WriteImmediately(TurnDataProp ComDP,byte bArray[]){
		set(bArray);
		sett(1);
		byte tmpBytes[] = new byte[getwlen()]; // 获得写入字节长度
		for (int i = 0; i < tmpBytes.length; i++) {
			tmpBytes[i] = wv[i]; // 转储数据
		}
		ComDP.nSendData=tmpBytes;
		if (this.getPortClear() > 0) {// 需要刷新自由口缓存
			this.resetPortClear();
			CmnPortManage.getInstance().clearFreePort(ComDP.eConnect);
		}
		SKCommThread.turnDataToOtherPort(ComDP);
		this.sett(0); // 清除写标记
		this.setwlen(0); // 清除写入长度
		this.wclear(); // 清除写出缓存
	}
	
	/**
	 * 实时读取端口缓存
	 * @param ComDP 端口信息
	 * @param nDelayTime 定时
	 * @param bInputFlag 判断标示
	 * @param nReadLength 读取长度
	 * @param bStopChar 停止字符
	 * @return
	 */
	public synchronized Vector<Byte> ReadInTime(TurnDataProp ComDP,int nDelayTime,int bInputFlag,int nReadLength,byte bStopChar){
				
		Vector<Byte> Buff=new Vector<Byte>();//接收
		switch(bInputFlag){
		case 0:
			Buff=ReadUntilChar(ComDP,nDelayTime,bStopChar);
			break;
		case 1:
			Buff=ReadUntilLen(ComDP,nDelayTime,nReadLength);
			break;
		case 2:
			Buff=ReadUntilTime(ComDP,nDelayTime);
			break;
		case 3:
			Buff=ReadMain(ComDP,nDelayTime,nReadLength,bStopChar);
			break;
		default:
			break;
		}
		return Buff;
	}
	
	/**
	 * 实时读取端口数据，到指定字符为止
	 * @param ComDP
	 * @param bStopChar
	 * @return
	 */
	private Vector<Byte> ReadUntilChar(TurnDataProp ComDP,int nDelayTime,byte bStopChar){
		Vector<Byte> Buff=new Vector<Byte>();//接收
		long sleeptime = 20,pretime,nowtime,starttime;
		int index=0;
		nowtime=starttime=System.currentTimeMillis();
		while((nowtime-starttime)<nDelayTime){
			pretime=System.currentTimeMillis();
			CmnPortManage.getInstance().getFreePortData(ComDP.eConnect,rv);
			CmnPortManage.getInstance().clearFreePort(ComDP.eConnect);
			
			while(rv.size()>index){
				Buff.add(rv.get(index));
				index++;
				
				if(Buff.lastElement()==bStopChar){
					for(int i=0;i<Buff.size();i++){
						rv.remove(0);
					}
					return Buff;
				}
			}
			nowtime=System.currentTimeMillis();
			try{
				//System.out.printf("^^^now:%d,pre:%d,sleep:%d\r\n",nowtime,pretime,(nowtime-pretime));
				Thread.currentThread().sleep(sleeptime-(nowtime-pretime));
				
			}catch(Exception e){
			}
			nowtime=System.currentTimeMillis();
		}
		return new Vector<Byte>();
	}
	
	/**
	 * 实时读取端口数据，到指定长度为止
	 * @param ComDP
	 * @param nReadLength
	 * @return
	 */
	private Vector<Byte> ReadUntilLen(TurnDataProp ComDP,int nDelayTime,int nReadLength){
		Vector<Byte> Buff=new Vector<Byte>();//接收
		//int count=10;
		long sleeptime = 20,pretime,nowtime,starttime;
		nowtime=starttime=System.currentTimeMillis();
		while((nowtime-starttime)<nDelayTime){
			pretime = System.currentTimeMillis();
			CmnPortManage.getInstance().getFreePortData(ComDP.eConnect,rv);
			CmnPortManage.getInstance().clearFreePort(ComDP.eConnect);
			if(rv.size()>=nReadLength){
				Buff.addAll(rv.subList(0, nReadLength));
				for(int i=0;i<nReadLength;i++){
					rv.remove(0);
				}
				return Buff;
			}
			nowtime=System.currentTimeMillis();
			try{
				Thread.currentThread().sleep(sleeptime-(nowtime-pretime));
			}catch(Exception e){
				
			}
		}
		return new Vector<Byte>();
	}
	
	/**
	 * 实时读取端口数据，到指定时间为止
	 * @param ComDP
	 * @param nDelayTime
	 * @return
	 */
	private Vector<Byte> ReadUntilTime(TurnDataProp ComDP,int nDelayTime){
		Vector<Byte> Buff=new Vector<Byte>();//接收
		try{
			Thread.currentThread().sleep(nDelayTime);
		}catch(Exception e){
			
		}
		CmnPortManage.getInstance().getFreePortData(ComDP.eConnect,rv);
		CmnPortManage.getInstance().clearFreePort(ComDP.eConnect);
		Buff.addAll(rv);
		rv.clear();
		return Buff;
	}
	/**
	 * 实时读取端口数据，到指定条件为止
	 * @param ComDP
	 * @param nDelayTime
	 * @param nReadLength
	 * @param bStopChar
	 * @return
	 */
	private Vector<Byte> ReadMain(TurnDataProp ComDP,int nDelayTime,int nReadLength,byte bStopChar){
		
		Vector<Byte> Buff=new Vector<Byte>();//接收
		//退出条件
		long sleeptime = 20,start,pretime,nowtime;
		int index=0;
		
		nowtime=start=System.currentTimeMillis();
		while(nowtime-start<nDelayTime){
			pretime=System.currentTimeMillis();
			CmnPortManage.getInstance().getFreePortData(ComDP.eConnect,rv);
			CmnPortManage.getInstance().clearFreePort(ComDP.eConnect);
			while(rv.size()>index){
				Buff.add(rv.get(index));
				index++;
				//rv.remove(0);
				if(Buff.lastElement()==bStopChar){
					for(int i=0;i<Buff.size();i++){
						rv.remove(0);
					}
					return Buff;
				}else if(Buff.size()>=nReadLength){
					for(int i=0;i<Buff.size();i++){
						rv.remove(0);
					}
					return Buff;
				}
			}
			nowtime=System.currentTimeMillis();
			try{
				Thread.currentThread().sleep(sleeptime-(nowtime-pretime));
			}catch(Exception e){
				
			}
			nowtime=System.currentTimeMillis();
		}
		return Buff;
	}
	
	public boolean getSimpleCmnState(){
		return isSimpleCmn;
	}
}
