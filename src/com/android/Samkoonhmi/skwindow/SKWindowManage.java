package com.android.Samkoonhmi.skwindow;

import java.util.HashMap;
import android.content.Context;
import android.util.Log;

import com.android.Samkoonhmi.SKScene;
import com.android.Samkoonhmi.databaseinterface.DBTool;
import com.android.Samkoonhmi.databaseinterface.WindowBiz;
import com.android.Samkoonhmi.model.ScenceInfo;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.model.WindowInfo;
import com.android.Samkoonhmi.skwindow.SKSceneManage.SHOW_TYPE;
import com.android.Samkoonhmi.util.AlarmGroup;

/**
 * 窗口管理
 */
public class SKWindowManage {

	public static final int nTitleHeight=30;
	public WindowInfo wInfo;
	public int nWindowId;
	private ScenceInfo sInfo;
	private boolean result;
	private SKDialog dialog;
	private Context mContext;
	public boolean show;
	private static HashMap<Integer, WindowInfo> mWindowList;
	private WindowBiz biz;
	
	private static SKWindowManage sInstance=null;
	public synchronized static SKWindowManage getInstance(Context context){
		if (sInstance==null) {
			sInstance=new SKWindowManage(context);
		}
		return sInstance;
	}
	
	private SKWindowManage(Context context){
		this.nWindowId=0;
		this.result=false;
		this.mContext=context;
		this.show=false;
	}
	
	/**
	 * 加载窗口数据
	 */
	public void loadWindowList(){
		if (biz==null) {
			biz=DBTool.getInstance().getmWindowBiz();
		}
		mWindowList=biz.loadWindow();
	}
	
	/**
	 * 打开窗口
	 * @param id-数据库序号
	 */
	private boolean loading;//防止操作过快，多次点击
	public void showWindows(int id){
		//Log.d("SKSceneManage", "id="+id);
		
		if (show||loading) {
			SKSceneManage.getInstance().isStarting=false;
			return;
		}
		SKSceneManage.getInstance().time=0;
		loading=true;
		show=true;
		nWindowId=id;
		loadData(nWindowId);
		//Log.d("SKSceneManage", "showWindows..."+result);
		if (result) {
			loadWindow();
		}else {
			SKSceneManage.getInstance().isStarting=false;
			show=false;
		}
	}
	
	/**
	 * 关闭窗口
	 * @param type-1 表示要移除窗口的，主要用于报警
	 */
	public void closeWindow(int type){
		Log.d("SKScene", "type="+type);
		loading=false;
		if (show) {
			if (type==1) {
				//移除弹出窗口
				AlarmGroup.getInstance().removeWindow(1, nWindowId);
			}
			nWindowId=-1;
			show=false;
			SKSceneManage.getInstance().exitSceneMacros(wInfo.getnSceneId());
			if (dialog!=null) {
				if (wInfo!=null) {
					SKSceneManage.getInstance().removeSKcene(wInfo.getnSceneId(),1);
				}
				dialog.dismiss();
				//dialog.cancel();
			}
		}
	}
	
	/**
	 * 加载数据
	 */
	private void loadData(int windowId){
		if (mWindowList.containsKey(windowId)) {
			//Log.d("SKScene", "-----id:"+windowId);
			wInfo=mWindowList.get(windowId);
		}else {
			if (biz==null) {
				biz=DBTool.getInstance().getmWindowBiz();
			}
			wInfo=biz.select(windowId);
		}
		if(wInfo!=null){
			sInfo=new ScenceInfo();
			sInfo.setnSceneId(wInfo.getnSceneId());
			sInfo.setnNum(wInfo.getnNum());
			sInfo.setnBackColor(wInfo.getnBackColor());
			sInfo.setnSceneWidth(wInfo.getnWindownWidth());
			sInfo.setnSceneHeight(wInfo.getnWindownHeight());
			sInfo.setnLeftX(wInfo.getnShowPosX());
			sInfo.setnLeftY(wInfo.getnShowPosY());
			sInfo.setsScreenName(wInfo.getsScreenName());
			sInfo.setnForeColor(wInfo.getnForeColor());
			sInfo.seteBackType(wInfo.geteBackType());
			sInfo.seteDrawStyle(wInfo.geteDrawStyle());
			sInfo.setsPicturePath(wInfo.getsPicturePath());
			sInfo.setbLogout(wInfo.isbLogout());
			sInfo.seteType(SHOW_TYPE.FLOATING);
			sInfo.setSceneMacroIDList(DBTool.getInstance().getmSceneBiz().selectMacroIDListBySceneID(sInfo.getnSceneId()));					
			sInfo.setbShowTitle(wInfo.isbShowTitle());
			SKSceneManage.getInstance().addSceneInfo(sInfo);
			result=true;
			
		}else {
			result=false;
		}
	}
	
	/**
	 * 加载窗口
	 */
	private void loadWindow(){
		//Log.d("SKSceneManage", "loadWindow...");
		if (SKSceneManage.getInstance().getActivity()==null) {
			Log.e("SKWindowMange", "loadwindow activity=null");
			SKSceneManage.getInstance().isStarting=false;
			nWindowId=-1;
			show=false;
			return;
		}
		dialog=new SKDialog(SKSceneManage.getInstance().getActivity());
		SKSceneManage.getInstance().nSceneId=wInfo.getnSceneId();
		SKSceneManage.getInstance().setiSceneUpdate(callback,SHOW_TYPE.FLOATING);
		SKSceneManage.getInstance().loadView(mContext, null, SHOW_TYPE.FLOATING);
		SystemInfo.setCurrentScenceId(wInfo.getnSceneId());
	}
	
	SKSceneManage.ISKSceneUpdate callback=new SKSceneManage.ISKSceneUpdate() {
		
		@Override
		public void onUpdateView(SKScene scene) {
			if (dialog!=null) {
				scene.setListener(listener);
				dialog.onCreate(scene,wInfo);
				dialog.setCanceledOnTouchOutside(false);
				dialog.showDialog(wInfo.getnShowPosX(), wInfo.getnShowPosY(),wInfo.isbShowMiddle());
			}
			loading=false;
		}
		
		@Override
		public void onChange(int in, int out,int type) {
			
		}

	};
	
	ITitleListener listener=new ITitleListener() {
		
		@Override
		public void onClose() {
			show=false;
			loading=false;
			if (dialog!=null&&wInfo!=null) {
				nWindowId=-1;
				SKSceneManage.getInstance().exitSceneMacros(wInfo.getnSceneId());
				SKSceneManage.getInstance().removeSKcene(wInfo.getnSceneId(),1);
				dialog.dismiss();
				//dialog.cancel();
			}
		}
	};
	
	/**
	 * 关闭窗口
	 */
	public interface ITitleListener{
		void onClose();
	}
}
