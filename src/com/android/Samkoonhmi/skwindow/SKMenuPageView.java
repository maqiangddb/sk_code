package com.android.Samkoonhmi.skwindow;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.android.Samkoonhmi.databaseinterface.DBTool;
import com.android.Samkoonhmi.model.SceneItemPosInfo;
import com.android.Samkoonhmi.model.skmenu.SKMenuPageInfo;

/**
 * 画面菜单
 * 菜单页
 */
public class SKMenuPageView extends ViewGroup{

	public ArrayList<SKMenuPageInfo> list;
	private int nItemWidth;
	private int nItemHeight;


	public SKMenuPageView(Context context,ArrayList<SKMenuPageInfo> list,int width,int height) {
		super(context);
		this.list=list;
		this.nItemWidth=width;
		this.nItemHeight=height;
		addItemView();
	}

	private void addItemView(){
		if(list!=null){
			for (int i = 0; i < list.size(); i++) {
				addView(list.get(i).view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
		                ViewGroup.LayoutParams.WRAP_CONTENT));
			}
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		
		//根据位置进行布局 ,默认有4行
		for(SKMenuPageInfo info: list){
			
			if (info != null) {
				int pos = info.nPagePos;
				int left = (pos%4)*nItemWidth;
				int top = (pos/4)*nItemHeight;
				info.view.layout(left, top, left + nItemWidth, top +nItemHeight);
			}
		}
	}
	
	public ArrayList<SKMenuPageInfo> getPageInfo(){
		return list;
	}
	
	/**
	 * 在当前页中设置itemview的新位置
	 * @param info
	 * @param pos 新位置
	 * @param pageIndex 当前页码
	 */
	public void setPageInfo(SKMenuPageInfo info,  int pos , int pageIndex){
		
		for(SKMenuPageInfo fo: list){
			
			if (info.view.getSceneId() == fo.view.getSceneId()) {
			     fo.nPagePos = pos;	
			     fo.nPageIndex = pageIndex;
			     
			     //同步到itemview中
			     fo.view.setPageIndex(pageIndex);
			     fo.view.setPagePos(pos);
			     //同步到数据库中
			     updateDB(fo.view.getPosInfo());
			     break;
			}
		}
		invalidate();
	}
	
	/*
	 * 从当前页中中移除itemview的信息
	 */
	public void removePageInfoItem(SKMenuPageInfo info){
		
		for(SKMenuPageInfo itemIfo : list){
			if (info.view.getSceneId() == itemIfo.view.getSceneId()){
				
				list.remove(itemIfo);
				removeView(info.view);
				break;
			}
		}
		invalidate();
	}
	
	/**
	 * 添加新的itemview到当前页面中
	 * @param info
	 * @param pagePos 添加到的位置
	 * @param pageIndex 当前的页面
	 */
	public void addPageInfoItem(SKMenuPageInfo info, int pagePos, int pageIndex){
		
		info.nPageIndex = pageIndex;
		info.nPagePos = pagePos;
		list.add(info);
		//进行同步
		info.view.setPageIndex(pageIndex);
		info.view.setPagePos(pagePos);
		//同步到数据库中
		updateDB(info.view.getPosInfo());
		
		addView(info.view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
		invalidate();
	}
	
	/*
	 * 更新信息
	 */
	private void updateDB(SceneItemPosInfo info){
		
		DBTool.getInstance().getScenePosInfoBiz().updateInfo(info);
	}
}
