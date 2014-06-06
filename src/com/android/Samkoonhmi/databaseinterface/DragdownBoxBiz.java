package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;

import android.database.Cursor;
import android.graphics.Rect;

import com.android.Samkoonhmi.model.DragdownboxItemInfo;
import com.android.Samkoonhmi.model.StakeoutInfo;
import com.android.Samkoonhmi.model.TextInfo;
import com.android.Samkoonhmi.model.skbutton.ButtonInfo;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.FLICK_TYPE;
import com.android.Samkoonhmi.skenum.IntToEnum;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;


/**
 * 下拉框
 * @author 苏逸邦
 *
 */
public class DragdownBoxBiz extends DataBase {
	private SKDataBaseInterface db;

	public DragdownBoxBiz() {
		db = SkGlobalData.getProjectDatabase();
	}
	
	/**
	 * 
	 */
	public ArrayList<DragdownboxItemInfo> selectDragdownInfo(int sceneId){
		ArrayList<DragdownboxItemInfo> list = new ArrayList<DragdownboxItemInfo>();
		StringBuffer id=new StringBuffer();
		boolean init=true;
		if (null == db) {
			db = SkGlobalData.getProjectDatabase();
		}
		Cursor cursor = db.getDatabaseBySql("select * from dragdownBox where nSceneId="+sceneId,null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				DragdownboxItemInfo info = new DragdownboxItemInfo();
				info.setId(cursor.getInt(cursor.getColumnIndex("nItemId")));
				int left=cursor.getShort(cursor.getColumnIndex("nStartX"));
				int top=cursor.getShort(cursor.getColumnIndex("nStartY"));
				int right=left+cursor.getShort(cursor.getColumnIndex("nWidth"));
				int bottom=top+cursor.getShort(cursor.getColumnIndex("nHeight"));
				Rect rect = new Rect(left,top,right,bottom);
				info.setRect(rect);
				info.setMaxState(cursor.getShort(cursor.getColumnIndex("nState")));
				DATA_TYPE eNumberType = IntToEnum.getDataType(cursor
						.getShort(cursor.getColumnIndex("eNumberType")));
				info.setDateType(eNumberType);
				int nAddrId=cursor.getInt(cursor.getColumnIndex("nBaseAddrID"));
				if (nAddrId>-1) {
					info.setBaseAddress(AddrPropBiz.selectById(nAddrId));
				}
				boolean bScriptSet=false;
				String sScriptSet=cursor.getString(cursor.getColumnIndex("bScriptSet"));
				if(sScriptSet!=null){
					bScriptSet=sScriptSet.equals("true");
				}
				info.setScript(bScriptSet);
				info.setScriptId(cursor.getShort(cursor.getColumnIndex("nScriptId")));
				info.setBackgroundImg(cursor.getString(cursor.getColumnIndex("sBackgroundImg")));
				info.setBackgroundColor(cursor.getInt(cursor.getColumnIndex("nBackgroundColor")));
				info.setAlpha(cursor.getShort(cursor.getColumnIndex("Alpha")));
				boolean useFirstLanAttr = false;
				String sUseFirstLanAttr = cursor.getString(cursor.getColumnIndex("nFirstLan"));
				if(sUseFirstLanAttr!=null){
					useFirstLanAttr=sUseFirstLanAttr.equals("true");
				}
				info.setUseFirstLanAttr(useFirstLanAttr);
				info.setnCollidindId(cursor.getInt(cursor.getColumnIndex("nCollidindId")));
				info.setnZvalue(cursor.getInt(cursor.getColumnIndex("nZvalue")));
				info.setTouchInfo(TouchShowInfoBiz.getTouchInfoById(info.getId()));
				info.setShowInfo(TouchShowInfoBiz.getShowInfoById(info.getId()));
				if(init){
					id.append(" nItemId in("+info.getId());
					init=false;
				}else {
					id.append(","+info.getId());
				}
				list.add(info);
			}
		}
		close(cursor);
		
		id.append(")");
		String temp=id.toString();
		if(list!=null){
			stateSelect(list,temp);
			textAttrSelect(list, temp);
		}
		
		return list;
	}


	/**
	 * 选项值
	 */
	private void stateSelect(ArrayList<DragdownboxItemInfo> list,String id) {
		Cursor cursor = null;
		String sql = "select nItemId,statusValue,nStatusIndex from switchStatusProp where "+id;
		cursor = db.getDatabaseBySql(sql, null);
		if (cursor != null) {
			DragdownboxItemInfo info=null;
			int itemId=-1;
			int index=0;
			
			while (cursor.moveToNext()) {
				if (itemId!=cursor.getInt(cursor.getColumnIndex("nItemId"))) {
					itemId=cursor.getInt(cursor.getColumnIndex("nItemId"));
					if (list.get(index).getId()==itemId) {
						info=list.get(index);
						ArrayList<StakeoutInfo> mStakeoutList = new ArrayList<StakeoutInfo>();
						info.setmStakeoutList(mStakeoutList);
					}else {
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getId()==itemId) {
								info=list.get(i);
								ArrayList<StakeoutInfo> mStakeoutList = new ArrayList<StakeoutInfo>();
								info.setmStakeoutList(mStakeoutList);
								break;
							}
						}
					}
					index++;
					
				}
				short stateId = cursor.getShort(cursor
						.getColumnIndex("nStatusIndex"));
				
				StakeoutInfo sInfo = new StakeoutInfo();
				sInfo.setnStatusId(stateId);
				sInfo.setnCmpFactor(cursor.getDouble(cursor.getColumnIndex("statusValue")));
				info.getmStakeoutList().add(sInfo);
			}
		}
		close(cursor);
	}

	/**
	 * 文本属性
	 */
	private void textAttrSelect(ArrayList<DragdownboxItemInfo> list,String id){
		Cursor cursor = null;
		String sql = "select * from textProp where "+id;
		cursor = db.getDatabaseBySql(sql,null);
		if(cursor!=null){
			DragdownboxItemInfo info=null;
			
			int nItemId=-1;
			int state=-1;
			int index=0;
			int k=0;
			while (cursor.moveToNext()) {
				if (nItemId!=cursor.getInt(cursor.getColumnIndex("nItemId"))) {
					nItemId=cursor.getInt(cursor.getColumnIndex("nItemId"));
					if (list.get(k).getId()==nItemId) {
						info=list.get(k);
						ArrayList<TextInfo> mTextList=new ArrayList<TextInfo>();
						info.setmTextAttrList(mTextList);
						state=-1;
						index=0;
					}else {
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getId()==nItemId) {
								info=list.get(i);
								ArrayList<TextInfo> mTextList=new ArrayList<TextInfo>();
								info.setmTextAttrList(mTextList);
								state=-1;
								index=0;
								break;
							}
						}
					}
					k++;
				}
				if (state!=cursor.getShort(cursor
						.getColumnIndex("nStatusIndex"))) {
					
					state=cursor.getShort(cursor
							.getColumnIndex("nStatusIndex"));
					TextInfo tInfo = new TextInfo();
					tInfo.setnStatusId( cursor.getShort(cursor
							.getColumnIndex("nStatusIndex")));
					tInfo.setnBColor(cursor.getInt(cursor.getColumnIndex("nColor")));
					tInfo.setnLangugeId(cursor.getShort(cursor
							.getColumnIndex("nLangIndex")));
					
					//字体类型
					ArrayList<String> mFonts=new ArrayList<String>();
					mFonts.add(cursor.getString(cursor
							.getColumnIndex("sFont")));
					tInfo.setmFonts(mFonts);
					
					//字体大小
					ArrayList<Integer> mSize=new ArrayList<Integer>();
					mSize.add(cursor.getInt(cursor.getColumnIndex("nSize")));
					tInfo.setmSize(mSize);
					
					//位置和闪烁
					ArrayList<Integer> mAligns=new ArrayList<Integer>();
					mAligns.add(cursor.getInt(cursor.getColumnIndex("nShowProp")));
					tInfo.setmStyle(mAligns);
					
					ArrayList<Integer> mColors=new ArrayList<Integer>();
					mColors.add(cursor.getInt(cursor.getColumnIndex("nColor")));
					tInfo.setmColors(mColors);
					
					//文本
					ArrayList<String> mTexts=new ArrayList<String>();
					mTexts.add(cursor.getString(cursor.getColumnIndex("sText")));
					tInfo.setmTextList(mTexts);
					
					index=info.getmTextAttrList().size();
					info.getmTextAttrList().add(tInfo);
					
				}else {
					TextInfo tinfo=info.getmTextAttrList().get(index);
					
					ArrayList<String> mFonts=tinfo.getmFonts();
					if (mFonts!=null) {
						mFonts.add(cursor.getString(cursor
								.getColumnIndex("sFont")));
					}
					
					ArrayList<Integer> mSize=tinfo.getmSize();
					if (mSize!=null) {
						mSize.add(cursor.getInt(cursor.getColumnIndex("nSize")));
					}
					
					ArrayList<Integer> mStyle=tinfo.getmStyle();
					if (mStyle!=null) {
						mStyle.add(cursor.getInt(cursor.getColumnIndex("nShowProp")));
					}
					
					ArrayList<Integer> mColor=tinfo.getmColors();
					if (mColor!=null) {
						mColor.add(cursor.getInt(cursor.getColumnIndex("nColor")));
					}
					
					ArrayList<String> mTexts=tinfo.getmTextList();
					if (mTexts!=null) {
						mTexts.add(cursor.getString(cursor.getColumnIndex("sText")));
					}
				}
				
			}
			
			close(cursor);
		}
	}
	
}
