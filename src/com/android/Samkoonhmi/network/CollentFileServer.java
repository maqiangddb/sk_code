package com.android.Samkoonhmi.network;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

import com.android.Samkoonhmi.SKThread;
import com.android.Samkoonhmi.SKThread.ICallback;
import com.android.Samkoonhmi.model.CurrentRecipe;
import com.android.Samkoonhmi.model.RecipeOGprop;
import com.android.Samkoonhmi.model.skglobalcmn.CollectDataInfo;
import com.android.Samkoonhmi.model.skglobalcmn.EditDataCollectProp;
import com.android.Samkoonhmi.model.skglobalcmn.HistoryDataCollect;
import com.android.Samkoonhmi.model.skglobalcmn.OperCall;
import com.android.Samkoonhmi.model.skglobalcmn.RecipeDataProp;
import com.android.Samkoonhmi.skenum.STORAGE_MEDIA;
import com.android.Samkoonhmi.skglobalcmn.RecipeDataCentre;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalBackThread;
import com.android.Samkoonhmi.skwindow.EmailOperDialog;
import com.android.Samkoonhmi.util.MODULE;
import android.util.Log;

/**
 * 历史数据采集文件传送服务
 */
public class CollentFileServer {

	private static final String TAG = "CollentFileServer";
	// 连接socket
	private Socket mClient = null;
	// 输入流
	private DataInputStream in;
	// 输出流
	private DataOutputStream out;
	

	public CollentFileServer(Socket socket, DataInputStream in,DataOutputStream out) {
		this.mClient = socket;
		this.in = in;
		this.out=out;
	}
	
	public CollentFileServer(){
		
	}

	/**
	 * 
	 */
	public void startUpLoadRecFile(){
		if(mClient != null){

			try {
				boolean isClose=false;
				String recipeName="";
				//out = new DataOutputStream(new BufferedOutputStream(mClient.getOutputStream()));
				
				//返回采集组数
				//int size = CollectDataInfo.getInstance().getmHistoryInfoList().size();
				int size = RecipeDataProp.getInstance().getmRecipeGroupList().size();
				out.writeInt(size);
				out.flush();
				
				if (size>0) {
					Vector<RecipeOGprop> data=RecipeDataProp.getInstance().getmRecipeGroupList();
					
					//返回采集所有组名称
					for (int i = 0; i < size; i++) {
						RecipeOGprop item=data.get(i);
						out.writeUTF(item.getsRecipeGName());
					}
					out.flush();
					
					//读取用户输入的采集序号
					int gid=in.readInt();
					Log.d(TAG, "get gid:"+gid);
					
					if (gid>-1&&gid<size) {
						//告诉客户端正在导出
						out.writeUTF("START");
						out.flush();
						Log.d(TAG, "start export file...");
						
						//发消息,导出csv文件
						
						
						RecipeDataCentre.getInstance().upLoadFile(gid, recipeCall);
						
					}else {
						//错误返回
						out.writeUTF("ERROR");
					}
					
				}else {
					isClose=true;
				}
				
				//关闭
				if (isClose) {
					in.close();
					out.close();
					mClient.close();
				}
				
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "Collent File Server upload error!");
			}
		}
	}
	
	private ICallback recipeCall = new ICallback() {
		
		@Override
		public void onUpdate(Object msg, int taskId) {
			CurrentRecipe cinfo = (CurrentRecipe)msg;
			RecipeOGprop item = RecipeDataProp.getInstance().getmRecipeGroupList().get(cinfo.getCurrentGroupRecipeId());
			sendFile(item.getsRecipeGName(),item.geteSaveMedia());
		}
		
		@Override
		public void onUpdate(int msg, int taskId) {
			RecipeOGprop item = RecipeDataProp.getInstance().getmRecipeGroupList().get(msg);
			sendFile(item.getsRecipeGName()+".csv",item.geteSaveMedia());
		}
		
		@Override
		public void onUpdate(String msg, int taskId) {
			Log.e(TAG, "recipeCall String");
		}
	};
	
	/**
	 * 上传历史数据文件
	 */
	private int nGid;
	public void startUpLoadFile() {
		if (mClient != null) {
			try {
				boolean isClose=false;
				//out = new DataOutputStream(new BufferedOutputStream(mClient.getOutputStream()));
				
				//返回采集组数
				int size = CollectDataInfo.getInstance().getmHistoryInfoList().size();
				out.writeInt(size);
				out.flush();
				
				if (size>0) {
					Vector<HistoryDataCollect> data=CollectDataInfo.getInstance().getmHistoryInfoList();
					
					//返回采集所有组名称
					for (int i = 0; i < size; i++) {
						HistoryDataCollect item=data.get(i);
						out.writeUTF(item.getnGroupId()+". "+item.getsName());
					}
					out.flush();
					
					//读取用户输入的采集序号
					int gid=in.readInt();
					Log.d(TAG, "get gid:"+gid);
					
					if (gid>-1&&gid<size) {
						//告诉客户端正在导出
						out.writeUTF("START");
						out.flush();
						Log.d(TAG, "start export file...");
						
						//发消息,导出csv文件
						EditDataCollectProp mEditData = new EditDataCollectProp();
						mEditData.nGroupId = gid;//组号
						nGid=gid;
						mEditData.nRecordTimeLen = 0;//导出数据，0-导出全部
						mEditData.mOperCall=call;//操作回调
						mEditData.mMedia=STORAGE_MEDIA.INSIDE_DISH;//保存路径
						mEditData.bShowPress=false;//不显示对话框

						/* 发送消息 */
						SkGlobalBackThread.getInstance().getGlobalBackHandler()
								.obtainMessage(MODULE.WRITE_HISTORY_TO_FILE, mEditData)
								.sendToTarget();
					}else {
						//错误返回
						out.writeUTF("ERROR");
					}
					
				}else {
					isClose=true;
				}
				
				//关闭
				if (isClose) {
					in.close();
					out.close();
					mClient.close();
				}
				
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "Collent File Server upload error!");
			}

		}
	}
	
	private int nEmailCount = 0;
	public void startEmailFiles(ArrayList<Integer> group){
		int size = CollectDataInfo.getInstance().getmHistoryInfoList().size();
		nEmailCount = group.size();
		if (size > 0) {
			for(int i = 0; i < group.size(); i++){
				//发消息,导出csv文件
				EditDataCollectProp mEditData = new EditDataCollectProp();
				mEditData.nGroupId = group.get(i);//组号
				mEditData.nRecordTimeLen = 0;//导出数据，0-导出全部
				mEditData.mOperCall=emailCall;//操作回调                                                                        //
				mEditData.mMedia=STORAGE_MEDIA.OTHER_STORAGE_MEDIA;//保存路径  //
				mEditData.bShowPress=false;//不显示对话框
				
				/* 发送消息 */
				SkGlobalBackThread.getInstance().getGlobalBackHandler()
						.obtainMessage(MODULE.WRITE_HISTORY_TO_FILE, mEditData)
						.sendToTarget();
			}
		}
		else {
			EmailOperDialog.CompleteCount +=1;
		}
	}
	
	public void startEmailScript(ArrayList<Integer> group , int peroid){
		int size = CollectDataInfo.getInstance().getmHistoryInfoList().size();
		nEmailCount = group.size();
		if (size > 0) {
			for(int i = 0; i < group.size(); i++){
				//发消息,导出csv文件
				EditDataCollectProp mEditData = new EditDataCollectProp();
				mEditData.nGroupId = group.get(i);//组号
				mEditData.nRecordTimeLen = peroid;//导出数据，0-导出全部
				mEditData.mOperCall=emailCall;//操作回调                                                                        //
				mEditData.mMedia=STORAGE_MEDIA.OTHER_STORAGE_MEDIA;//保存路径  //
				mEditData.bShowPress=false;//不显示对话框
				
				/* 发送消息 */
				SkGlobalBackThread.getInstance().getGlobalBackHandler()
						.obtainMessage(MODULE.WRITE_HISTORY_TO_FILE, mEditData)
						.sendToTarget();
			}
		}
		else {
			EmailOperDialog.CompleteCount +=1;
		}
	}
	
	/**
	 * 操作返回
	 */
	OperCall call=new OperCall(){

		@Override
		public void result(boolean result, String name) {
			sendFile(result,name);
		}
		
	};
	
	//进行统计回调的次数
	OperCall emailCall = new OperCall() {
		
		@Override
		public void result(boolean result, String name) {
			// TODO Auto-generated method stub
			//有回调说明 有文件生成
			nEmailCount--;
			if (nEmailCount <= 0) {//历史数据文件已生成完成 
				EmailOperDialog.CompleteCount +=1;
			}
			
		}
	};
	
	
	public void sendFile(String name,STORAGE_MEDIA mediaType) {
		try {
			//导出结束
			out.writeUTF("STOP");
			out.flush();
			Log.d(TAG, "export file stop...");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			Log.d(TAG, "name:"+name);
			String path=name;
			switch(mediaType){
				case INSIDE_DISH:
					path = "/data/data/com.android.Samkoonhmi/formula/recipe/"+name+".csv";
					break;
				case U_DISH:
					path = "/mnt/usb2/"+name;
					break;
				case SD_DISH:
					path = "/mnt/sdcard/"+name;
					break;
			
			}
			Log.d(TAG, "path:"+path);
			File file=new File(path);
			if (file.exists()) {
				
				out.writeUTF("SUCCEED");
				out.flush();
				Log.d(TAG, "export file succed...");
				
				long size=file.length();
				Log.d(TAG, "size"+size);
				out.writeLong(size);//文件长度
				out.flush();
				
				out.writeUTF(name+"_"+System.currentTimeMillis()+".csv");//文件名称
				out.flush();
				
				//每次传送8KB
				byte[] buffer = new byte[8*1024];
				DataInputStream fis = null;
				fis=new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
				out.writeUTF("START");
				Log.d(TAG, "send file start...");
				
				out.flush();
				
				while (true) {
					int len=0;
					len=fis.read(buffer);
					if (len==-1) {
						break;
					}
					out.write(buffer,0,len);
				}
				out.flush();
				fis.close();
				
				//结束
				//删除文件
				if(mediaType==STORAGE_MEDIA.INSIDE_DISH){
					file.delete();
				}
				
				Log.d(TAG, "send file stop...");
				
				
			}else{
				//文件不存在
				out.writeUTF("NO FILE");
				out.flush();
				Log.d(TAG, "export file error...");
			}
			
			in.close();
			out.close();
			mClient.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	};
		
	private void sendFile(boolean result,String name){
		
		try {
			//导出结束
			out.writeUTF("STOP");
			out.flush();
			Log.d(TAG, "export file stop...");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		if (name==null||name.equals("")||!result) {
			//导出失败
			try {
				out.writeUTF("NO FILE");
				out.flush();
				Log.d(TAG, "export file error...");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		
		try {
			
			Log.d(TAG, "name:"+name);
			String path="/data/data/com.android.Samkoonhmi/AK_History/"+name;
			Log.d(TAG, "path:"+path);
			File file=new File(path);
			if (file.exists()) {
				
				out.writeUTF("SUCCEED");
				out.flush();
				Log.d(TAG, "export file succed...");
				
				long size=file.length();
				Log.d(TAG, "size"+size);
				out.writeLong(size);//文件长度
				out.flush();
				
				String ss=CollectDataInfo.getInstance().getmHistoryInfoList()
						.get(nGid).getsName();
				out.writeUTF(ss+"_"+System.currentTimeMillis()+".csv");//文件名称
				out.flush();
				
				//每次传送8KB
				byte[] buffer = new byte[8*1024];
				DataInputStream fis = null;
				fis=new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
				out.writeUTF("START");
				Log.d(TAG, "send file start...");
				
				out.flush();
				
				while (true) {
					int len=0;
					len=fis.read(buffer);
					if (len==-1) {
						break;
					}
					out.write(buffer,0,len);
				}
				out.flush();
				fis.close();
				
				//结束
				//删除文件
				file.delete();
				
				Log.d(TAG, "send file stop...");
				
				
			}else{
				//文件不存在
				out.writeUTF("NO FILE");
				out.flush();
				Log.d(TAG, "export file error...");
			}
			
			in.close();
			out.close();
			mClient.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
