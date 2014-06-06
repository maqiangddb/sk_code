package com.android.Samkoonhmi.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Vector;
import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.activity.PlcDialog;
import com.android.Samkoonhmi.databaseinterface.SystemInfoBiz;
import com.android.Samkoonhmi.model.PlcConnectionInfo;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.plccommunicate.CmnPortManage;
import com.android.Samkoonhmi.skenum.CONNECT_TYPE;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.skwindow.SKToast;
import com.android.Samkoonhmi.skwindow.SKToast.IPlcCallback;
import com.android.Samkoonhmi.skzip.AKFileUpdate;
import com.android.Samkoonhmi.skzip.SkLoad;
import com.android.Samkoonhmi.skzip.skzip;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class AkZipService extends Service{

	private static final int UPDATE=2;
	private static final String TAG="AkZipService";
	private Context mContext;
	private AkZipThread mAkZipThread; 
	private boolean isAkEmu;//是否是模拟器
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		//Log.d("SKScene", "--onCreate--");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		//Log.d("SKScene", "--onStart--");
		mContext=this;
	
		if (intent!=null) {
			String update=intent.getStringExtra("update");
			if (update!=null) {
				if(update.equals("true")){
					Log.d(TAG, "Ak Update the configuration...");
				    //SavaInfo.setState(1);//开始下载，把ak状态设置为NO
					update();
				}else if (update.equals("start")) {
					//设置更新状态
					Log.d(TAG, "ak loadown start...");
					SavaInfo.setState(1);//开始下载，把ak状态设置为NO
					
					SharedPreferences sharedPreferences = mContext.getSharedPreferences("information", 0);
					boolean result = sharedPreferences.getBoolean("update_state", false);
				
					//设置更新状态
					SharedPreferences.Editor shareEditor = mContext.getSharedPreferences("information", 0).edit();
					shareEditor.putBoolean("update_state", false);
					shareEditor.commit();
					
					if (result) {
						SKSceneManage.getInstance().closeDB();
						SKSceneManage.getInstance().destroy();
					}
					//android.os.Process.killProcess(android.os.Process.myPid());
					
				}else if(update.equals("emu_start")){
					super.stopSelf();
					SharedPreferences sharedPreferences = mContext.getSharedPreferences("information", 0);
					boolean result = sharedPreferences.getBoolean("update_state", false);
					//设置更新状态
					SharedPreferences.Editor shareEditor = mContext.getSharedPreferences("information", 0).edit();
					shareEditor.putBoolean("update_state", false);
					shareEditor.commit();
					if(result){
						SKSceneManage.getInstance().closeDB();
						SKSceneManage.getInstance().destroy();
					}else {
						android.os.Process.killProcess(android.os.Process.myPid());
					}
				}else if (update.equals("file")) {
					AKFileUpdate.flag=1;
					Log.d(TAG, "ak emu file update...");
					isAkEmu=true;
					update();
					//SkLoad.getInstance().updateFile(mContext);
				}
				
			}
		}
		
	}
	
	private synchronized void update(){
		
		skToast=SKToast.makeText(this.getApplicationContext(), R.string.ak_update, Toast.LENGTH_LONG, Gravity.CENTER, 0, 60);
		isShow=true;
		mHandler.sendEmptyMessage(SHOW);
		
		mAkZipThread=new AkZipThread();
		mAkZipThread.start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//Log.d("SKScene", "--onDestroy--");
	}

	@Override
	public boolean onUnbind(Intent intent) {
		//Log.d("SKScene", "--onUnbind--");
		return super.onUnbind(intent);
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
		//Log.d("SKScene", "--onRebind--");
	}
	
	private boolean bPlcDown=false;
	private PlcSampInfo mPlcInfo=null;
	class AkZipThread extends Thread{

		@Override
		public void run() {
			long start = System.currentTimeMillis();
			super.run();
			
			Log.d(TAG, "ak update file...");
			bPlcDown=false;
			//SavaInfo.setState(1);//开始下载，把ak状态设置为NO
			upState(false);//上载状态
			
			File file=new File("/data/data/com.android.Samkoonhmi/samkoonhmi.akz");
			//真机
			if (file.exists()) {
				SkLoad.getInstance().update_from_release(mContext,
						"/data/data/com.android.Samkoonhmi/samkoonhmi.akz");
				AKFileUpdate.getInstance(mContext).update();
				upState(true);//上载状态
			}else {
				//模拟器
				File emu=new File("/mnt/shared/esd/Udisk");
				if (emu.exists()) {
					AKFileUpdate.getInstance(mContext).linkFileToEmu();
					AKFileUpdate.getInstance(mContext).update();
					upState(true);//上载状态
				}else {
					startHMI();
					return;
				}
				
			}
			
			File ff=new File("/data/data/com.android.Samkoonhmi/soar");
			if (ff.exists()) {
				bPlcDown=true;
			}
			
			/**
			 * PLC程序下载
			 */
			if (bPlcDown) {
				
				boolean com2=false;
				
				try {
					boolean isOpen=true;
					
					//打开串口
					SystemInfoBiz biz=new SystemInfoBiz();
					biz.selectSystemInfo();//设置系统参数
					
					//com1=CmnPortManage.getInstance().openCmnPort(CONNECT_TYPE.COM1);
					com2=CmnPortManage.getInstance().openCmnPort(CONNECT_TYPE.COM2);
					mPlcInfo=new PlcSampInfo();
				
					if (com2) {
						mPlcInfo.eConnectType=CONNECT_TYPE.COM2;
					}else {
						//打开串口失败
						isOpen=false;
					}
					
					if (isOpen) {
						downLoad();
					}else {
						Log.d(TAG, "plc down open com fail.");
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					Log.d(TAG,"plc down error!");
					
				}finally{
					//关闭串口
					if (com2) {
						CmnPortManage.getInstance().closeCmnPort((short)mPlcInfo.eConnectType);
					}
					Log.d(TAG,"plc down close port !");
				}
				
				//删除文件
				skzip.getInstance().delAllFile("/data/data/com.android.Samkoonhmi/soar");
			}else {
				startHMI();
			}
			//System.out.println("^^^spand time:"+(System.currentTimeMillis()-start));
		}
		
	}
	
	/**
	 * 启动AK组态
	 */
	private void startHMI(){
		Log.d(TAG, "startHMI...");
		
		isShow=false;
		mHandler.removeMessages(SHOW);
		if (skToast==null) {
			skToast.cancel();
		}
		
        //设置更新状态
		SharedPreferences.Editor shareEditor = mContext.getSharedPreferences("information", 0).edit();
		shareEditor.putBoolean("update_state", true);
		shareEditor.commit();
		
		
		//缓存同步到nandflash
		Intent newintent = new Intent();
		newintent.setAction("com.samkoon.sync");
		sendBroadcast(newintent);
		
		for (PackageInfo pack : mContext.getPackageManager()
				.getInstalledPackages(PackageManager.GET_ACTIVITIES)) {
			if (pack.packageName.equals("com.android.Samkoonhmi")) {
				// 包存在了，才启动
				PackageManager packageManager = mContext.getPackageManager();
				Intent intent2 = packageManager
						.getLaunchIntentForPackage("com.android.Samkoonhmi");
				mContext.startActivity(intent2);
				break;
			}
		}
		
		if (isAkEmu) {
			isAkEmu=false;
			//删除文件
			Intent intent = new Intent();
			intent.setAction("com.samkoon.update.restart");
			sendBroadcast(intent);
		}
		
		SavaInfo.setState(2);//更新完毕，把ak状态设置为YES
	}
	
	
	/**
	 * 更新下载状态
	 */
	private void upState(boolean state){
		//Log.d(TAG, "update file state="+state);
		SharedPreferences.Editor shareEditor = mContext.getSharedPreferences("information", 0).edit();
		shareEditor.putBoolean("ak_updte_file", state);
		shareEditor.commit();
	}
	
	/**
	 * 下载
	 */
	private boolean bShowPlcMsg;
	private boolean downLoad(){
		boolean result;
		bShowPlcMsg=false;
		Log.d(TAG, "plc loaddown start...");
		/**
		 * 需要按照顺序下载
		 * 总共5个文件
		 */
		File file=null;
		Vector<Byte> temp=new Vector<Byte>();
		String list[]=new String[]{"","","","",""};
		//指令文件   0x7b
		list[0]="/data/data/com.android.Samkoonhmi/soar/codefile.dat";
		//modbus 表格文件  0x7f
		list[1]="/data/data/com.android.Samkoonhmi/soar/modbusfile.dat";
		//注释文件   0x7e
		list[2]="/data/data/com.android.Samkoonhmi/soar/commfile.dat";
		//初始化文件  0x7c
		list[3]="/data/data/com.android.Samkoonhmi/soar/elemInitfile.dat";
		//配置文件  0x7d
		list[4]="/data/data/com.android.Samkoonhmi/soar/settingfile.dat";
		byte mType[]=new byte[]{0x7b,0x7f,0x7e,0x7c,0x7d};
		
		for (int i = 0; i < list.length; i++) {
			file=new File(list[i]);
			if (!file.exists()) {
				errorMsg("file no exsit...",false);
				return false;
			}
			result=readFile(file,temp);
			if (result) {
				//发送下载命令
				result=startCmd("DOWN");
				if (result) {
					//通知PLC下载某个文件
					result=startDownFile(temp.size(),mType[i]);
					Log.d(TAG, "update file:"+list[i]+",type:"+mType[i]+",file:"+file.length()+",result:"+result);
					if (result) {
						//下载文件数据
						result=writeToPlc(temp,mType[i]);
						if (!result) {
							errorMsg("write to plc fail...",false);
							return false;
						}else {
							//修改波特率
							if (mType[i]==0x7d) {
								//配置文件
								changeRate(temp);
							}
						}
					}else {
					}
				}else {
					Log.d(TAG, "start cmd fail...");
				}
			}else {
				errorMsg("read file fail......",false);
				Log.d(TAG, "fail file:"+list[i]);
				return false;
			}
		}
		
		//下载完毕
		result=startCmd("WORK");
		if (result) {
			//mHandler.sendEmptyMessage(3);
			errorMsg("plc update succeed!",true);
		}else {
			errorMsg("plc update fail!",false);
		}
		
		return true;
	}
	
	//数据写入PLC
	private boolean writeToPlc(Vector<Byte> temp,byte type){
		int count=0;
		if (temp.size()<SEND_LENGTH) {
			count=1;
		}else {
			count=temp.size()/SEND_LENGTH;
			if (count%SEND_LENGTH>0) {
				count++;
			}
		}
		
		int index=0;
		int nErrorCount=0;
		byte []crc=new byte[2];
		
		for (int i = 0; i < count; i++) {
			
			byte []nSendData=getDownCmd(type, temp.size(), temp, index);
			
			//清除缓存
			CmnPortManage.getInstance().clearRcvBuff(mPlcInfo);
			//往串口写数据
			CmnPortManage.getInstance().sendData(mPlcInfo, nSendData);
			
			try {
				Thread.sleep(150);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			crc[0]=nSendData[nSendData.length-2];
			crc[1]=nSendData[nSendData.length-1];
			switch (getWriteResult(crc)) {
			case 0:
				//发送下一帧
				nErrorCount=0;
				index+=SEND_LENGTH;
				break;
			case 1:
				//写入失败
				i--;
				nErrorCount++;
				if (nErrorCount>3) {
					//下载失败,结束下载
					return false;
				}
				break;
			case 2:
				//下载完成
				break;
			}			
		}
		
		return true;
	}
	
	//获取写入结果
	private int getWriteResult(byte[] data){
		int result=1;
		Vector<Byte> nBackData=new Vector<Byte>();
		CmnPortManage.getInstance().getData(mPlcInfo, nBackData);
		
		//接受缓冲区必须有数值，并且长度大于6个字节
		if (nBackData.size()>=6) {
			//int len=nBackData.size();
			if ((nBackData.get(2)==(byte)0xff)&&(nBackData.get(3)==(byte)0xff)) {
				//下载结束
				//Log.d(TAG, "down result 2");
				result= 2;
			}else if (nBackData.get(4)==0x16) {
				//Log.d(TAG, "down result 1");
				result= 1;
			}else {
				//Log.d(TAG, "down result 0");
				result= 0;
			}
			
		}
		
		String temp="error";
		if (nBackData!=null) {
			temp="";
			for (int i = 0; i < nBackData.size(); i++) {
				temp+=nBackData.get(i)+",";
			}
		}
		return result;
	}
	
	/**
	 * 开始下载文件
	 */
	private boolean startDownFile(int len,byte type){
		
		Log.d(TAG, "request: start down file...");
		char [] c=new char[9];
		byte [] temp=new byte[11];
		
		c[0]=(char)(nPlcStation&0xff);
		c[1]=(char)0x7a;
		c[2]=(char)0x0b;
		c[3]=(char)0x00;
		c[4]=(char)type;
		c[5]=(char)(len&0xff);
		c[6]=(char)((len>>8)&0xff);
		c[7]=(char)((len>>16)&0xff);
		c[8]=(char)((len>>24)&0xff);
		
		int crc=CRC(c,c.length);
		for (int i = 0; i < c.length; i++) {
			temp[i]=(byte)c[i];
		}
		temp[temp.length-2]=(byte)((crc>>0)&0xff);
		temp[temp.length-1]=(byte)((crc>>8)&0xff);
		
		Vector<Byte> nBackData=new Vector<Byte>();
		for (int i = 0; i < 3; i++) {
			
			//清除缓存
			CmnPortManage.getInstance().clearRcvBuff(mPlcInfo);
			
			//往串口写数据
			CmnPortManage.getInstance().sendData(mPlcInfo, temp);
			
			//等待100ms
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			//读取数据
			CmnPortManage.getInstance().getData(mPlcInfo, nBackData);
			if (nBackData.size()==7&&nBackData.get(1)==0x7a) {
				Log.d(TAG, "response: code:"+nBackData.get(1));
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 文件读取
	 */
	private boolean readFile(File file,Vector<Byte> data){
		data.clear();
		byte[] buffer = new byte[1024];
		int len;
		FileInputStream is;
		try {
			is = new FileInputStream(file);
			while ((len=is.read(buffer))!=-1) {
				for (int i = 0; i < len; i++) {
					data.add(buffer[i]);
				}
			}
			is.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 通知PLC进入下载状态
	 * @param msg-下载命令
	 */
	private int nPlcStation;
	private static int SEND_LENGTH=64;//一次下载最大数据量
	private boolean startCmd(String msg){
		
		if (msg==null||msg.equals("")) {
			return false;
		}
		
		byte[] nSendData = null;
		char[] data=null;
		byte temp[]=msg.getBytes();
		
		if (temp!=null) {
			int len=temp.length;
			for (int i = 0; i < len; i++) {
				if (nSendData==null) {
					nSendData=new byte[len+3];
					nSendData[0]=(byte)0xF6;
					
					data=new char[len+3];
					data[0]=0xF6;
				}
				data[i+1]=(char)temp[i];
				nSendData[i+1]=temp[i];
			}
			
			int crc=CRC(data,len+1);
			nSendData[nSendData.length-2]=(byte)((crc>>0)&0xff);
			nSendData[nSendData.length-1]=(byte)((crc>>8)&0xff);
			
			Vector<Byte> nBackData=new Vector<Byte>();
			for (int i = 0; i < 3; i++) {
				
				//清除缓存
				CmnPortManage.getInstance().clearRcvBuff(mPlcInfo);
				
				//往串口写数据
				CmnPortManage.getInstance().sendData(mPlcInfo, nSendData);
				//CmnPortManage.getInstance().sendData(mPlcInfo, sSendData);
				
				//等待500ms
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				//读取数据
				CmnPortManage.getInstance().getData(mPlcInfo, nBackData);
				if (nBackData.size()==2&&nBackData.get(0)==0x06) {
					nPlcStation=nBackData.get(1);
					Log.d(TAG, "response: station num:"+nBackData.get(1)+","+nBackData.get(0));
					return true;
				}
			}
		}
		
		return false;
	}
	
	
	/**
	 * 下载命令
	 */
	private byte[] getDownCmd(byte type,int length,Vector<Byte> data,int index){
		
		int len=SEND_LENGTH;
		if (index+SEND_LENGTH>data.size()) {
			len=data.size()-index;
		}
		
		byte[] temp=new byte[len+6];;
		
		temp[0]=(byte)(nPlcStation&0xff);
		temp[1]=type;
		
		if (len<=0) {
			temp[2]=(byte)0xff;
			temp[3]=(byte)0xff;
		}else {
			temp[2]=(byte)(((len+6)>>0)&0xff);
			temp[3]=(byte)(((len+6)>>8)&0xff);
		}
		
		Log.d(TAG, "request: down cmd len:"+len+",index:"+index+",size:"+data.size());
		
		int k=4;
		for (int i = 0; i < len; i++) {
			if (index<data.size()) {
				temp[k]=data.get(index);
			}
			index++;
			k++;
		}
		
		byte[] ss=CRC16.CheckCRC16(temp, temp.length-2);
		if (ss!=null&&ss.length==2) {
			temp[temp.length-2]=ss[0];
			temp[temp.length-1]=ss[1];
		}
		
		return temp;
	}
	
	
	//错误信息提示
	private String sMsg="";
	private void errorMsg(String msg,boolean result){
		Log.d(TAG, "msg:"+msg);
		if (result) {
			//启动AK状态软件
			startHMI();
		}else {
			//提示错误信息
			isShow=false;
			mHandler.removeMessages(SHOW);
			if (skToast==null) {
				skToast.cancel();
			}
			sMsg=msg;
			mHandler.sendEmptyMessage(PLC_MSG);
		}
	}
	
	
	private int CRC(char[] data, int n) {
		if (null == data) {
			System.out.println("CRC: data is null");
			return -1;
		}

		if (n < 1 || n > data.length) {
			System.out.println("CRC: invalid length : " + n);
			return -1;
		}

		int crc = 0xffff;
		int i = 0;
		int j = 0;

		for (i = 0; i < n; i++) {
			crc ^= data[i];
			for (j = 0; j < 8; j++) {
				if (0x1 == (crc & 1)) {
					crc >>= 1;
					crc ^= 0xA001;
				} else {
					crc >>= 1;
				}
			}
		}

		return crc;
	}
	
	/**
	 * 改变波特率
	 */
	private void changeRate(Vector<Byte> data){
		if (data.size()<14) {
			return;
		}
		
		//波特率
		int baud=9600;
		switch (data.get(10)) {
		case 0:
			baud=4800;
			break;
		case 1:
			baud=9600;
			break;
		case 2:
			baud=19200;
			break;
		case 3:
			baud=38400;
			break;
		case 4:
			baud=57600;
			break;
		case 5:
			baud=115200;
			break;
		}
		
		//数据位
		int len=data.get(11);
		
		//停止位
		int stop;
		if (data.get(12)==0) {
			stop=1;
		}else {
			stop=2;
		}
		
		//校验
		int parity=0;
		if (data.get(13)==0) {
			parity=0;
		}else if (data.get(14)==1) {
			parity=1;
		}else if (data.get(14)==2) {
			parity=2;
		}
		
		boolean result=false;
		PlcConnectionInfo mInfo=null;
		int nConnectSize = SystemInfo.getPlcConnectionList().size();
		int index=0;
		for(int i = 0; i < nConnectSize; i++){
			if(SystemInfo.getPlcConnectionList().get(i).geteConnectPort() == CONNECT_TYPE.COM2)
			{
				mInfo = SystemInfo.getPlcConnectionList().get(i);
				index=i;
			}
		}
		
		if (mInfo!=null) {
			if (baud!=mInfo.getnBaudRate()
					||len!=mInfo.getnDataBits()
					||stop!=mInfo.getnStopBit()
					||parity!=mInfo.getnStopBit()) {
				result=true;
			}
		}
		
//		if (result) {
//			
//			Log.d(TAG, "chang baud:"+baud);
//			CmnPortManage.getInstance().setmSerialPort2(null);
//			SystemInfo.getPlcConnectionList().get(index).setnBaudRate(baud);
//			SystemInfo.getPlcConnectionList().get(index).setnDataBits((short)len);
//			SystemInfo.getPlcConnectionList().get(index).setnStopBit((short)stop);
//			SystemInfo.getPlcConnectionList().get(index).setnCheckType(parity);
//			//CmnPortManage.getInstance().openCmnPort(CONNECT_TYPE.COM2);
//		}
		
	}
	
	/**
	 * 更新主态
	 */
	private static Toast skToast;
	private static final int SHOW=1;
	private static final int PLC_MSG=2;
	private static final int PLC_SHOW=3;
	private boolean isShow;
	Handler mHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what==SHOW) {
				if (isShow) {
					skToast.show();
					mHandler.sendEmptyMessageDelayed(SHOW, 3200);
				}
			}else if (msg.what==PLC_MSG) {
				if (bShowPlcMsg) {
					return;
				}
				bShowPlcMsg=true;
				mHandler.sendEmptyMessage(PLC_SHOW);
				
			}else if (msg.what==PLC_SHOW) {
				
				Intent intent=new Intent();
				intent.setClass(AkZipService.this, PlcDialog.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS); 
				intent.putExtra("msg", sMsg);
				AkZipService.this.startActivity(intent);
			}
		}
		
	};

	IPlcCallback callback=new IPlcCallback(){

		@Override
		public void cancel() {
			isShow=false;
			mHandler.removeMessages(PLC_MSG);
			if (skToast==null) {
				skToast.cancel();
			}
		}
		
	};
	
}
