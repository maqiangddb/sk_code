package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;
import android.database.Cursor;
import com.android.Samkoonhmi.model.StaticTextModel;
import com.android.Samkoonhmi.model.TextInfo;
import com.android.Samkoonhmi.skenum.CSS_TYPE;
import com.android.Samkoonhmi.skenum.IntToEnum;
import com.android.Samkoonhmi.skenum.TEXT_PIC_ALIGN;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;

public class StaticTextBiz extends DataBase{
	SKDataBaseInterface db=null;
	Cursor cursor=null;
	public StaticTextBiz(){
		db = SkGlobalData.getProjectDatabase();
	}
	
	/**
	 * 查找静态文本
	 */
	public ArrayList<StaticTextModel> getStaticText(int sceneId){
		
		StringBuffer id=new StringBuffer();
		boolean init=true;
		
		ArrayList<StaticTextModel>  list = new ArrayList<StaticTextModel>();
		if(null == db){
			db = SkGlobalData.getProjectDatabase();
		}
		cursor = db.getDatabaseBySql("select * from staticText where nSceneId=? ", new String[]{sceneId+""});
		if(null!=cursor){
			while(cursor.moveToNext()){
				StaticTextModel	info=new StaticTextModel();
				info.setId(cursor.getInt(cursor.getColumnIndex("nItemId")));
				info.setLineColor(cursor.getInt(cursor.getColumnIndex("nLineColor")));
				info.setLineWidth(cursor.getInt(cursor.getColumnIndex("nLineWidth")));
				info.setM_alphaPadding(cursor.getInt(cursor.getColumnIndex("nAlphaPadding")));
				info.setM_backColorPadding(cursor.getInt(cursor.getColumnIndex("nBackColorPadding")));
				TEXT_PIC_ALIGN textAlign=IntToEnum.getTextPicAlign(cursor.getInt(cursor.getColumnIndex("eTextAlign")));
				info.setM_eTextAlign(textAlign);
				info.setM_foreColorPadding(cursor.getInt(cursor.getColumnIndex("nForeColorPadding")));
				info.setM_fristLanguage(cursor.getString(cursor.getColumnIndex("bFristLanguage")).equals("true")?true:false);
				info.setM_nFontColor(cursor.getInt(cursor.getColumnIndex("nFontColor")));
				info.setM_nFontSize(cursor.getInt(cursor.getColumnIndex("nFontSize")));
				info.setM_nFontSpace(cursor.getInt(cursor.getColumnIndex("sFontSpace")));
				info.setM_sFontFamly(cursor.getString(cursor.getColumnIndex("sFontFamly")));
				info.setM_sTextStr(cursor.getString(cursor.getColumnIndex("sStextStr")));
				CSS_TYPE m_stylePadding=IntToEnum.getCssType(cursor.getInt(cursor.getColumnIndex("nStylePadding")));
				info.setM_stylePadding(m_stylePadding);
				info.setM_textLanguageId(cursor.getInt(cursor.getColumnIndex("nLanguageId")));
				info.setM_textPro((short) cursor.getInt(cursor.getColumnIndex("eTextPro")));
				info.setnCollidindId(cursor.getInt(cursor.getColumnIndex("nCollidindId")));
				info.setnZvalue(cursor.getInt(cursor.getColumnIndex("nZvalue")));
				info.setRectHeight(cursor.getInt(cursor.getColumnIndex("nRectHeight")));
				info.setRectWidth(cursor.getInt(cursor.getColumnIndex("nRectWidth")));
				info.setStartX(cursor.getInt(cursor.getColumnIndex("nStartX")));
				info.setStartY(cursor.getInt(cursor.getColumnIndex("nStartY")));
				info.setShowInfo(TouchShowInfoBiz.getShowInfoById(info.getId()));
				list.add(info);
				if(init){
					id.append(" nItemId in("+info.getId());
					init=false;
				}else {
					id.append(","+info.getId());
				}
			}
		}
		id.append(")");
		String sId=id.toString();
		textSelect(sId, list);
		close(cursor);
		
		
		return list;
	}
	
	/**
	 * 文本
	 */
	private void textSelect(String id, ArrayList<StaticTextModel> list){
		Cursor cursor = null;
		String sql = "select * from textProp where "+id;
		cursor = db.getDatabaseBySql(sql,null);
		if(cursor!=null){
			StaticTextModel info=null;
			int nItemId=-1;
			int index=0;
			
			while (cursor.moveToNext()) {
				if (nItemId!=cursor.getInt(cursor.getColumnIndex("nItemId"))) {
					nItemId=cursor.getInt(cursor.getColumnIndex("nItemId"));
					if (list.get(index).getId()==nItemId) {
						info=list.get(index);
						ArrayList<TextInfo> mTextList=new ArrayList<TextInfo>();
						info.setmTextList(mTextList);
					}else {
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getId()==nItemId) {
								info=list.get(i);
								ArrayList<TextInfo> mTextList=new ArrayList<TextInfo>();
								info.setmTextList(mTextList);
								break;
							}
						}
					}
					index++;
				}
				
				//静态文本只有一个状态
				if (info.getmTextList().size()==0) {
					//状态文本，多语言
					TextInfo tinfo=new TextInfo();
					ArrayList<String> mTexts=new ArrayList<String>();
					mTexts.add(cursor.getString(cursor.getColumnIndex("sText")));
					tinfo.setmTextList(mTexts);
					info.getmTextList().add(tinfo);
					
				}else {
					TextInfo tinfo=info.getmTextList().get(0);
					ArrayList<String> mTexts=tinfo.getmTextList();
					if (mTexts!=null) {
						mTexts.add(cursor.getString(cursor.getColumnIndex("sText")));
					}
				}
				
			}
		}
		close(cursor);
	}
}
