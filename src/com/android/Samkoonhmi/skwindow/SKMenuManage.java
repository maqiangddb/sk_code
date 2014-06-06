package com.android.Samkoonhmi.skwindow;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;

import com.android.Samkoonhmi.SKThread;
import com.android.Samkoonhmi.databaseinterface.DBTool;
import com.android.Samkoonhmi.model.SceneItemPosInfo;
import com.android.Samkoonhmi.model.skmenu.SKMenuPageInfo;
import com.android.Samkoonhmi.model.skmenu.SceneMenuInfo;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;
import com.android.Samkoonhmi.util.MODULE;
import com.android.Samkoonhmi.util.TASK;

/**
 * 菜单管理
 * @author 刘伟江
 * @version v 1.0.0.1
 * 创建时间 2012-6-8
 */
public class SKMenuManage {

	private static final int nItemNum=12;
	private String nTaskName="";
	public int nPage; 
	public int nItemWidth;
	public int nItemHeight;
	private boolean bTouch;//是否可以滑动进入
	private ArrayList<SceneItemPosInfo> infoList;
	public boolean isbTouch() {
		return bTouch;
	}


	public HashMap<Integer, ArrayList<SceneItemPosInfo>> mPageMap;
	// 单例
	private static SKMenuManage sInstance = null;
	public synchronized static SKMenuManage getInstance() {
		if (sInstance == null) {
			sInstance = new SKMenuManage();
		}
		return sInstance;
	}
	
	private SKMenuManage(){
		nPage=0;
		mPageMap=new HashMap<Integer, ArrayList<SceneItemPosInfo>>();
		nTaskName=SKThread.getInstance().getBinder().onRegister(callback);
	}
	
	/**
	 * 加载菜单数据
	 */
	public void loadData(){
		bTouch=DBTool.getInstance().getmSceneBiz().getTouchMenu();
		if (bTouch) {
			SkGlobalData.createMenuTable();
		}
		SKThread.getInstance().getBinder().onTask(MODULE.SCENE, TASK.ALL_SCENE, nTaskName);
	}
	
	public void setData(ArrayList<SceneMenuInfo> list)
	{
		if (list==null) {
			loadData();
			return;
		}
		
		nItemWidth=SKSceneManage.nSceneWidth/4;
	    nItemHeight=SKSceneManage.nSceneHeight/3;
		
		if (infoList != null) 
		{
			for(SceneItemPosInfo info : infoList)
			{
				addPageMapItem(info.nPageId, info);			
			}
		}
		else 
		{
			//首次使用，进行初始化数据
			if(list != null)
			{
				for(int i = 0; i < list.size(); i++)
				{
					SceneItemPosInfo info = new SceneItemPosInfo();
					info.nFontSize = 16;
					info.width = nItemWidth;
					info.height = nItemHeight;
					info.mSceneName = list.get(i).getName();
					info.mSceneId = list.get(i).getId();
					info.mScenePath = list.get(i).getPic();
					info.nPageId = i / nItemNum + 1;
					info.nPagePos = i % nItemNum;
					
					addPageMapItem(info.nPageId, info);
					// 将初始化的数据插入数据库
					DBTool.getInstance().getScenePosInfoBiz().insertItemInfo(info);	
				}
				
			}
		}
	}
	

	private void addPageMapItem(int page,  SceneItemPosInfo info){
		ArrayList<SceneItemPosInfo> list = mPageMap.get(page);
		if (list == null) {
			list = new ArrayList<SceneItemPosInfo>();
			mPageMap.put(page, list);
		}
		list.add(info);
	}
	
	/**
	 * 更新mPageMap数组
	 * @param info
	 * @param isChgPage 判断是否从一个页移到另外一个页面    是=true
	 */
	public void updateMapItem(SKMenuPageInfo info, int orgPage,  boolean isChgPage){
		
		ArrayList<SceneItemPosInfo> list = mPageMap.get(orgPage);
		if (list != null) {
			
			for(SceneItemPosInfo itemPosInfo : list)
			{
				if (itemPosInfo.mSceneId == info.view.nSceneId) {
					
					if (isChgPage)
					{
						//从原来的list中删除
						list.remove(itemPosInfo);
						itemPosInfo.nPagePos = info.nPagePos;
						itemPosInfo.nPageId = info.nPageIndex;
						//添加到新的list中
						addPageMapItem(itemPosInfo.nPageId, itemPosInfo);
					}
					else
					{
						itemPosInfo.nPagePos = info.nPagePos;
					}
					break;
				}
			}
		}
		
	}
	
	
	SKThread.ICallback callback=new SKThread.ICallback() {
		
		@Override
		public void onUpdate(Object msg, int taskId) {
			
		}
		
		@Override
		public void onUpdate(int msg, int taskId) {
			
		}
		
		@Override
		public void onUpdate(String msg, int taskId) {
			if(taskId==TASK.ALL_SCENE){
				ArrayList<SceneMenuInfo> mList=DBTool.getInstance().getmSceneBiz().select();
				infoList = DBTool.getInstance().getScenePosInfoBiz().getALLInfo();
				setData(mList);
			}
		}
	};
	
	/**
	 * 将dip转化为PX
	 */
	public static int dip2px(Context context, float dip){
		float scale = context.getResources().getDisplayMetrics().density;
		return (int)(scale*dip + 0.5);
	}

}
