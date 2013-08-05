package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import com.android.Samkoonhmi.model.RecipeShowInfo;
import com.android.Samkoonhmi.model.StaticTextModel;
import com.android.Samkoonhmi.model.TextInfo;
import com.android.Samkoonhmi.model.alarm.AlarmHisShowInfo;
import com.android.Samkoonhmi.skenum.FLICK_TYPE;
import com.android.Samkoonhmi.skenum.IntToEnum;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;

public class RecipeShowBiz extends DataBase{

	private SKDataBaseInterface db = null;
	
	public RecipeShowBiz(){
		db=SkGlobalData.getProjectDatabase();
	}
	
	/**
	 * 配方控件信息查询
	 */
	public ArrayList<RecipeShowInfo> select(int sid){
		String id = "";
		boolean init = true;
		ArrayList<RecipeShowInfo> list=new ArrayList<RecipeShowInfo>();
		
		if (db!=null) {
			String sql="select * from recipeDisplay where nSceneId="+sid;
			Cursor cursor=db.getDatabaseBySql(sql, null);
			if(cursor!=null){
				while (cursor.moveToNext()) {
					RecipeShowInfo info=new RecipeShowInfo();
					
					info.setnItemId(cursor.getInt(cursor.getColumnIndex("nItemId")));
					info.setnStartPosX(cursor.getShort(cursor.getColumnIndex("nStartPosX")));
					info.setnStartPosY(cursor.getShort(cursor.getColumnIndex("nStartPosY")));
					info.setnWidth(cursor.getShort(cursor.getColumnIndex("nWidth")));
					info.setnHeight(cursor.getShort(cursor.getColumnIndex("nHeight")));
					info.setnRecipeGroupId(cursor.getInt(cursor.getColumnIndex("nRecipeGroupId")));
					boolean showID=cursor.getString(cursor.getColumnIndex("bShowRecipeID")).equals("true")?true:false;
					info.setbShowRecipeID(showID);
					boolean showDes=cursor.getString(cursor.getColumnIndex("bShowDescrip")).equals("true")?true:false;
					info.setbShowDescrip(showDes);
					info.seteType(IntToEnum.getTextPicAlign(
							cursor.getShort(cursor.getColumnIndex("eTextAlignType"))));
					info.setnLanguagId(cursor.getInt(cursor.getColumnIndex("nLanguaId")));
					info.setnRow(cursor.getShort(cursor.getColumnIndex("nRowShowNum")));
					info.setnColum(cursor.getShort(cursor.getColumnIndex("nColumShowNum")));
					
					info.setnHTitleTextColor(cursor.getInt(cursor.getColumnIndex("nHHeadTextColor")));
					info.setnHTitleBackColor(cursor.getInt(cursor.getColumnIndex("nHHeadBackColor")));
					info.setnHTitleFontSize(cursor.getInt(cursor.getColumnIndex("nHHeadFontSize")));
					info.setsHTitleFont(cursor.getString(cursor.getColumnIndex("sHHeadFontFamily")));
					
					info.setnVTitleTextColor(cursor.getInt(cursor.getColumnIndex("nVHeadTextColor")));
					info.setnVTitleBackColor(cursor.getInt(cursor.getColumnIndex("nVHeadBackColor")));
					info.setnVTitleFontSize(cursor.getInt(cursor.getColumnIndex("nVHeadFontSize")));
					info.setsVTitleFont(cursor.getString(cursor.getColumnIndex("sVHeadFontFamily")));
					
					info.setnTextColor(cursor.getInt(cursor.getColumnIndex("nDataTextColor")));
					info.setnTextBackColor(cursor.getInt(cursor.getColumnIndex("nDataBackColor")));
					info.setnTextFontSize(cursor.getInt(cursor.getColumnIndex("nDataFontSize")));
					info.setnLineColor(cursor.getInt(cursor.getColumnIndex("nLineColor")));
					//info.setsTextFont(cursor.getString(cursor.getColumnIndex("")));
					info.setnAlpha(cursor.getShort(cursor.getColumnIndex("nTransparent")));
					info.setnZvalue(cursor.getInt(cursor.getColumnIndex("nZvalue")));
					info.setnCollidindId(cursor.getInt(cursor.getColumnIndex("nCollidindId")));
					info.setmShowInfo(TouchShowInfoBiz.getShowInfoById(info.getnItemId()));
					info.setmTouchInfo(TouchShowInfoBiz.getTouchInfoById(info.getnItemId()));
					list.add(info);
					if (init) {
						id += " nItemId=" + info.getnItemId();
						init = false;
					} else {
						id += " or nItemId=" + info.getnItemId();
					}
				}
			}
			close(cursor);
			if (list.size()>0) {
				getTable(list,id);
				getRecipeNames(list,id);
			}
			
		}
		return list;
	}
	
	private void getRecipeNames(ArrayList<RecipeShowInfo> list,String id){
		Cursor cursor = null;
		String sql = "select * from textProp where "+id;
		cursor = db.getDatabaseBySql(sql,null);
		if(cursor!=null){
			RecipeShowInfo info=null;
			int nItemId=-1;
			while (cursor.moveToNext()) {
				if (nItemId!=cursor.getInt(cursor.getColumnIndex("nItemId"))) {
					nItemId=cursor.getInt(cursor.getColumnIndex("nItemId"));
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getnItemId()==nItemId) {
							info=list.get(i);
							ArrayList<String> mTextList=new ArrayList<String>();
							info.setmRecipeNames(mTextList);
							break;
						}
					}
				}
				if (info!=null&&info.getmRecipeNames()!=null) {
					info.getmRecipeNames().add(cursor.getString(cursor.getColumnIndex("sText"))+"");
				}
				
			}
		}
		close(cursor);
	}
	
	/**
	 * 获取表格信息
	 */
	private void getTable(ArrayList<RecipeShowInfo> list,String id){
		if (db != null) {
			Cursor cursor = null;
			String sql = "select * from tableProp where "+id;
			cursor = db.getDatabaseBySql(sql, null);
			
			int nItemId=-1;
			RecipeShowInfo info=null;
			double width=0;
			double height=0;
			
			if (cursor != null) {
				while (cursor.moveToNext()) {
					if (nItemId != cursor.getInt(cursor.getColumnIndex("nItemId"))) {
						nItemId = cursor.getInt(cursor.getColumnIndex("nItemId"));
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getnItemId() == nItemId) {
								info = list.get(i);
								//行的高
								ArrayList<Double> mRowHeight=new ArrayList<Double>();
								//行的宽
								ArrayList<Double> mRowWidth=new ArrayList<Double>();
								info.setmRowWidht(mRowWidth);
								info.setmRowHeight(mRowHeight);
								width=0;
								height=0;
								break;
							}
						}

					}
					if (cursor.getInt(cursor.getColumnIndex("nIsRow"))==0) {
						//行的宽
						width+=cursor.getDouble(cursor.getColumnIndex("nWidth"));
						info.getmRowWidht().add(cursor.getDouble(cursor.getColumnIndex("nWidth")));
					}else {
						//行的高
						height+=cursor.getDouble(cursor.getColumnIndex("nWidth"));
						info.getmRowHeight().add(cursor.getDouble(cursor.getColumnIndex("nWidth")));
					}
					if (width>0) {
						info.setnWidth((short)width);
					}
					if(height>0){
						info.setnHeight((short)height);
					}
				}
				cursor.close();
			}
		}
	}
}
