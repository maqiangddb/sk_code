package com.android.Samkoonhmi.system;

import java.io.File;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.skzip.SkLoad;


public class StorageStateManager {
	private static final String TAG="StorageStateManager";
	private boolean UsbMounted = false;
	private boolean SDMounted = false;
	private BroadcastReceiver StateMntRecv = null;
	private Context           InnerContent = null;
	private static StorageStateManager mSelfInstance = null; //自持单例

	public StorageStateManager() {
		UsbMounted=isUseUDisk();
		SDMounted=isUseSD();
	}
	
	/**
	 * 获取自持单例
	 * */
	public synchronized static StorageStateManager getInstance(){
		if(null ==  mSelfInstance){//第一次获取单例
			mSelfInstance = new StorageStateManager();
			if(null == mSelfInstance){
				Log.e("StorageStateManager", "getInstance: Instance create failed!");
				return null;	
			}
		}
		return mSelfInstance;
	}

	public void destroyStateMntRecv(){
		if(null != StateMntRecv && null != InnerContent){
			InnerContent.unregisterReceiver(StateMntRecv);
		}
	}
	
	/**
	 * U盘是否可以使用
	 */
	public boolean isUSBMounted(){
		return UsbMounted;
	}
	
	/**
	 * SD卡是否可以使用
	 */
	public boolean isSDMounted(){
		return SDMounted;
	}
	
	
	/**
	 * SD是否可以使用
	 */
	private boolean isUseSD(){
		boolean result=false;
		try { 
			String name=System.currentTimeMillis()+"";
			File file=new File("/mnt/sdcard/"+name);
			file.createNewFile();
			file.delete();
			result=true;
	    } catch (Exception e) { 
	        //e.printStackTrace(); 
	    	Log.e(TAG, "no sdcard...");
	    } 
		SystemVariable.getInstance().setSDCardMntState(result);
		return result;
	}
	
	/**
	 * U盘是否可以使用
	 */
	private boolean isUseUDisk(){
		boolean result=false;
		try { 
			String name=System.currentTimeMillis()+"";
			File file=new File("/mnt/usb2/"+name);
			file.createNewFile();
			file.delete();
			result=true;
	    } catch (Exception e) { 
	        //e.printStackTrace(); 
	    	Log.e(TAG, "no udisk...");
	    } 
		SystemVariable.getInstance().setUDiskMntState(result);
		return result;
	}
	
	private Handler uiHandler  = new Handler(Looper.getMainLooper()){
		public void handleMessage(android.os.Message msg) {
			int flag = msg.what;
			showDialog(flag);
		};
	};
	
	
	
	public void initStateMntRecv(Context content) {

		if(null == content){
			Log.e("StorageStateManager", "initStateMntRecv: content is null!");
			return;
		}
		
		SystemVariable.getInstance().setUDiskMntState(UsbMounted);
		SystemVariable.getInstance().setSDCardMntState(SDMounted);

		if(null == StateMntRecv){
			InnerContent = content;
			StateMntRecv = new BroadcastReceiver(){
				@Override
				public void onReceive(Context context,Intent intent){
					if(intent.getAction().equals("android.intent.action.MEDIA_MOUNTED")){
						if(intent.getData().getPath().equals("/mnt/usb2")){
							UsbMounted = true;
							SystemVariable.getInstance().setUDiskMntState(UsbMounted);
							if(SkLoad.getInstance().isUpdateFileExist(1)==true)
							{
								Message msg = Message.obtain(uiHandler, 1);
								msg.sendToTarget();
							}
						}else if (intent.getData().getPath().equals("/mnt/sdcard")){
							SDMounted = true;
							SystemVariable.getInstance().setSDCardMntState(SDMounted);
							if(SkLoad.getInstance().isUpdateFileExist(2)==true)
							{
								Message msg = Message.obtain(uiHandler, 2);
								msg.sendToTarget();
							}
						}
					}else if (intent.getAction().equals("android.intent.action.MEDIA_EJECT")){
						if(intent.getData().getPath().equals("/mnt/usb2")){
							UsbMounted = false;
							SystemVariable.getInstance().setUDiskMntState(UsbMounted);
						}else if (intent.getData().getPath().equals("/mnt/sdcard")){
							SDMounted = false;
							SystemVariable.getInstance().setSDCardMntState(SDMounted);
						}
					}
				}
			};

			IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
			intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
			intentFilter.addAction(Intent.ACTION_MEDIA_EJECT); 
			intentFilter.addDataScheme("file");
			content.registerReceiver(StateMntRecv,intentFilter);
		}
	}
	
	
	
	
	/**
	 * 对话框
	 */
	private AlertDialog dlg = null;
	private Button sureButton = null;
	private Button cancleButton =  null;
	private ProgressBar mBar=null;
	/**
	 * 跳出系统的对话框
	 */
	public void showDialog(final int flag) {
		try {
			if (null == dlg) {
				if (SKSceneManage.getInstance().getActivity()==null) {
					return;
				}
				dlg = new AlertDialog.Builder(SKSceneManage.getInstance().getActivity()).create();
			}
			dlg.show();
			dlg.setCanceledOnTouchOutside(false);
			LayoutInflater inflate  = LayoutInflater.from(SKSceneManage.getInstance().getActivity());
			View view = inflate.inflate(R.layout.update_system_dialog, null);
			sureButton = (Button) view.findViewById(R.id.update_ok);
			cancleButton = (Button) view.findViewById(R.id.update_cancel);
			mBar=(ProgressBar)view.findViewById(R.id.ak_update_bar);
			mBar.setVisibility(View.GONE);
			
	        sureButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					SKSceneManage.getInstance().time=0;
					sureButton.setEnabled(false);
					cancleButton.setEnabled(false);
					mBar.setVisibility(View.VISIBLE);
					if(flag==1)
					{
						try {
							new Thread(){

								@Override
								public void run() {
									// TODO Auto-generated method stub
									super.run();
									SkLoad.getInstance().update_from_udisk();
								}
								
							}.start();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}				
					}
					else
					{
						try {
							new Thread(){

								@Override
								public void run() {
									// TODO Auto-generated method stub
									super.run();
									SkLoad.getInstance().update_from_sdcard();
								}
								
							}.start();
						} catch (Exception e) {
						// TODO Auto-generated catch block
							e.printStackTrace();
						}					
					}
					
				}
			});
	        cancleButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					SKSceneManage.getInstance().time=0;
					// TODO Auto-generated method stub
					dlg.dismiss();
				}
			});
			dlg.getWindow().setContentView(view);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		
	}
}
