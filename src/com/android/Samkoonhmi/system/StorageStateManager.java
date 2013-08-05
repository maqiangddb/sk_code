package com.android.Samkoonhmi.system;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.skzip.SkLoad;


public class StorageStateManager {
	private boolean UsbMounted = false;
	private boolean SDMounted = false;

	private BroadcastReceiver StateMntRecv = null;
	private Context           InnerContent = null;
	private static StorageStateManager mSelfInstance = null; //自持单例

	public StorageStateManager() {
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
	
	public boolean isUSBMounted(){
		return UsbMounted;
	}
	
	public boolean isSDMounted(){
		return SDMounted;
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
	/**
	 * 跳出系统的对话框
	 */
	public void showDialog(final int flag) {
		if (null == dlg) {
			dlg = new AlertDialog.Builder(SKSceneManage.getInstance().getActivity()).create();
		}
		dlg.show();
		LayoutInflater inflate  = LayoutInflater.from(SKSceneManage.getInstance().getActivity());
		View view = inflate.inflate(R.layout.update_system_dialog, null);
		sureButton = (Button) view.findViewById(R.id.update_ok);
		cancleButton = (Button) view.findViewById(R.id.update_cancel);
        sureButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				SKSceneManage.getInstance().time=0;
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
	}
}
