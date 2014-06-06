package com.android.Samkoonhmi.macro;

import java.util.HashMap;
import java.util.List;
import com.android.Samkoonhmi.databaseinterface.ItemBiz;
import com.android.Samkoonhmi.model.IItem;
import com.android.Samkoonhmi.model.ItemsInfo;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.skwindow.SKSceneManage.ItemInfo;

public class ItemTool {

	
	private static HashMap<Integer, ItemsInfo> mMap=new HashMap<Integer, ItemsInfo>();
	
	
	// 单例
	private static ItemTool sInstance = null;
	public synchronized static ItemTool getInstance() {
		if (sInstance == null) {
			sInstance = new ItemTool();
		}
		return sInstance;
	}
	
	public IItem getItem(int id){
		return getIItem(id);
	}
	
	/**
	 * 获取控件对外接口
	 */
	private IItem getIItem(int id){
		ItemsInfo item=mMap.get(id);
		List<ItemInfo> list=SKSceneManage.getInstance().getItemList(item.nSid);
		if (list!=null) {
			for (int i = 0; i < list.size(); i++) {
				ItemInfo info=list.get(i);
				if (info.nItemId==item.nItemId) {
					return info.mItem.getIItem();
				}
			}
		}
		return null;
	}
	
	/**
	 * 加载控件和画面对应信息
	 */
	public void loadData(){
		ItemBiz biz=new ItemBiz();
		biz.select(mMap);
	}
	
}
