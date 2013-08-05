package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;
import java.util.List;
import android.database.Cursor;
import com.android.Samkoonhmi.model.KeyBoardButtonInfo;
import com.android.Samkoonhmi.model.KeyBoardInfo;
import com.android.Samkoonhmi.skenum.CSS_TYPE;
import com.android.Samkoonhmi.skenum.IntToEnum;
import com.android.Samkoonhmi.skenum.KEYBOARD_OPERATION;
import com.android.Samkoonhmi.skenum.KEY_STYLE;
import com.android.Samkoonhmi.skenum.TEXT_PIC_ALIGN;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;

public class KeyBroadBiz extends DataBase {
	private SKDataBaseInterface db = null;
	private KeyBoardInfo info;
	private KeyBoardButtonInfo buttonInfo;
	private Cursor cursor;

	public KeyBroadBiz() {
		db = SkGlobalData.getProjectDatabase();
	}

	
	public KeyBoardInfo selectKeyBorad(int id) {
		if(null == db){
			db = SkGlobalData.getProjectDatabase();
		}
		cursor = db.getDatabaseBySql("select * from keyBoard where nSceneId=?",new String[] { id+"" });
		if (null != cursor) {
			info = new KeyBoardInfo();
			while (cursor.moveToNext()) {
				KEY_STYLE style = IntToEnum.getKeyType(cursor.getInt(cursor.getColumnIndex("eBackType")));
				info.seteBackType(style);
				CSS_TYPE css = IntToEnum.getCssType(cursor.getInt(cursor.getColumnIndex("ekeyStyle")));
				info.setEkeyStyle(css);
				info.setId(cursor.getInt(cursor.getColumnIndex("id")));
				info.setNkeyBackColor(cursor.getInt(cursor.getColumnIndex("nkeyBackColor")));
				info.setNkeyForeColor(cursor.getInt(cursor.getColumnIndex("nkeyForeColor")));
				info.setNkeyHeight(cursor.getInt(cursor.getColumnIndex("nkeyHeight")));
				info.setNkeyWidth(cursor.getInt(cursor.getColumnIndex("nkeyWidth")));
				info.setSkeyName(cursor.getString(cursor.getColumnIndex("skeyName")));
				info.setsPicturePath(cursor.getString(cursor.getColumnIndex("sPicturePath")));
				
				info.setnMaxStartX(cursor.getInt(cursor.getColumnIndex("nMaxStartX")));
				info.setnMaxStartY(cursor.getInt(cursor.getColumnIndex("nMaxStartY")));
				info.setnMaxWidth(cursor.getInt(cursor.getColumnIndex("nMaxWidth")));
				info.setnMaxHeight(cursor.getInt(cursor.getColumnIndex("nMaxHeight")));
				if(info.getnMaxWidth()!=0 && info.getnMaxHeight()!=0){
					info.setnMaxAdapt(cursor.getString(cursor.getColumnIndex("nMaxAdapt")).equals("true")?true:false);
					TEXT_PIC_ALIGN nMaxAligns=IntToEnum.getTextPicAlign(cursor.getInt(cursor.getColumnIndex("nMaxAlign")));
					info.setnMaxAlign(nMaxAligns);
					info.setnMaxAlpha(cursor.getInt(cursor.getColumnIndex("nMaxAlpha")));
					info.setnMaxBackColor(cursor.getInt(cursor.getColumnIndex("nMaxBackColor")));
					info.setnMaxFont(cursor.getString(cursor.getColumnIndex("nMaxFont")));
					info.setnMaxFontColor(cursor.getInt(cursor.getColumnIndex("nMaxFontColor")));
					info.setnMaxFontPro(cursor.getShort(cursor.getColumnIndex("nMaxFontPro")));
					info.setnMaxFontSize(cursor.getInt(cursor.getColumnIndex("nMaxFontSize")));
					info.setnMaxForeColor(cursor.getInt(cursor.getColumnIndex("nMaxForeColor")));
					CSS_TYPE nMaxStyles=IntToEnum.getCssType(cursor.getInt(cursor.getColumnIndex("nMaxStyle")));
					info.setnMaxStyle(nMaxStyles);
				}
				
				info.setnMinStartX(cursor.getInt(cursor.getColumnIndex("nMinStartX")));
				info.setnMinStartY(cursor.getInt(cursor.getColumnIndex("nMinStartY")));
				info.setnMinWidth(cursor.getInt(cursor.getColumnIndex("nMinWidth")));
				info.setnMinHeight(cursor.getInt(cursor.getColumnIndex("nMinHeight")));
				if(info.getnMinWidth()!=0 && info.getnMinHeight()!=0){
					info.setnMinAdapt(cursor.getString(cursor.getColumnIndex("nMinAdapt")).equals("true")?true:false);
					TEXT_PIC_ALIGN nMinAligns=IntToEnum.getTextPicAlign(cursor.getInt(cursor.getColumnIndex("nMinAlign")));
					info.setnMinAlign(nMinAligns);
					info.setnMinAlpha(cursor.getInt(cursor.getColumnIndex("nMinAlpha")));
					info.setnMinBackColor(cursor.getInt(cursor.getColumnIndex("nMinBackColor")));
					info.setnMinFont(cursor.getString(cursor.getColumnIndex("nMinFont")));
					info.setnMinFontColor(cursor.getInt(cursor.getColumnIndex("nMinFontColor")));
					info.setnMinFontPro(cursor.getShort(cursor.getColumnIndex("nMinFontPro")));
					info.setnMinFontSize(cursor.getInt(cursor.getColumnIndex("nMinFontSize")));
					info.setnMinForeColor(cursor.getInt(cursor.getColumnIndex("nMinForeColor")));
					CSS_TYPE nMinStyles=IntToEnum.getCssType(cursor.getInt(cursor.getColumnIndex("nMinStyle")));
					info.setnMinStyle(nMinStyles);
				}
				
				info.setnTextStartX(cursor.getInt(cursor.getColumnIndex("nTextStartX")));
				info.setnTextStartY(cursor.getInt(cursor.getColumnIndex("nTextStartY")));
				info.setnTextWidth(cursor.getInt(cursor.getColumnIndex("nTextWidth")));
				info.setnTextHeight(cursor.getInt(cursor.getColumnIndex("nTextHeight")));
				if(info.getnTextWidth()!=0 && info.getnTextHeight()!=0){
					info.setnTextAdapt(cursor.getString(cursor.getColumnIndex("nTextAdapt")).equals("true")?true:false);
					TEXT_PIC_ALIGN nTextAligns=IntToEnum.getTextPicAlign(cursor.getInt(cursor.getColumnIndex("nTextAlign")));
					info.setnTextAlign(nTextAligns);
					info.setnTextAlpha(cursor.getInt(cursor.getColumnIndex("nTextAlpha")));
					info.setnTextBackColor(cursor.getInt(cursor.getColumnIndex("nTextBackColor")));
					info.setnTextFont(cursor.getString(cursor.getColumnIndex("nTextFont")));
					info.setnTextFontColor(cursor.getInt(cursor.getColumnIndex("nTextFontColor")));
					info.setnTextFontPro(cursor.getShort(cursor.getColumnIndex("nTextFontPro")));
					info.setnTextFontSize(cursor.getInt(cursor.getColumnIndex("nTextFontSize")));
					info.setnTextForeColor(cursor.getInt(cursor.getColumnIndex("nTextForeColor")));
					CSS_TYPE nTextStyles=IntToEnum.getCssType(cursor.getInt(cursor.getColumnIndex("nTextStyle")));
					info.setnTextStyle(nTextStyles);				
				}
			}
		}
		close(cursor);
		return info;

	}
	
	public List<KeyBoardButtonInfo> getKeyButton(int id){
		List<KeyBoardButtonInfo> list=new ArrayList<KeyBoardButtonInfo>();
		cursor = db.getDatabaseBySql("select * from UDFKkeyBoard where nSceneId=?", new String[]{id+""});
		if (null != cursor) {
			while (cursor.moveToNext()) {
				buttonInfo = new KeyBoardButtonInfo();
				buttonInfo.setId(cursor.getInt(cursor.getColumnIndex("nItemId")));
				int itemId=cursor.getInt(cursor.getColumnIndex("nItemId"));
				buttonInfo.setShowInfo(TouchShowInfoBiz.getShowInfoById(itemId));
				buttonInfo.setTouchInfo(TouchShowInfoBiz.getTouchInfoById(itemId));
				CSS_TYPE csstype = IntToEnum.getCssType(cursor.getInt(cursor.getColumnIndex("eDownStyle")));
				buttonInfo.seteDownStyle(csstype);
				TEXT_PIC_ALIGN textAlign = IntToEnum.getTextPicAlign(cursor.getInt(cursor.getColumnIndex("eFontAlign")));
				buttonInfo.seteFontAlign(textAlign);
				CSS_TYPE uptype = IntToEnum.getCssType(cursor.getInt(cursor.getColumnIndex("eUpStyle")));
				buttonInfo.seteUpStyle(uptype);
				buttonInfo.setId(cursor.getInt(cursor.getColumnIndex("id")));
				KEYBOARD_OPERATION operation = IntToEnum.getKeyOperation(cursor.getInt(cursor.getColumnIndex("eKeyOperation")));
				buttonInfo.setKeyOperation(operation);
				buttonInfo.setnDownBackColor(cursor.getInt(cursor.getColumnIndex("nDownBackColor")));
				buttonInfo.setnDownForeColor(cursor.getInt(cursor.getColumnIndex("nDownForeColor")));
				buttonInfo.setnDownFrameColor(cursor.getInt(cursor.getColumnIndex("nDownFrameColor")));
				buttonInfo.setnFontColor(cursor.getInt(cursor.getColumnIndex("nFontColor")));
				buttonInfo.setnFontPro(cursor.getShort(cursor.getColumnIndex("nFontPro")));
				buttonInfo.setnFontSize(cursor.getInt(cursor.getColumnIndex("nFontSize")));
				buttonInfo.setsFontFamly(cursor.getString(cursor.getColumnIndex("sFontFamily")));
				buttonInfo.setnHeight(cursor.getInt(cursor.getColumnIndex("nHeight")));
				buttonInfo.setsImagePath(cursor.getString(cursor.getColumnIndex("sImagePath")));
				buttonInfo.setnStartX(cursor.getInt(cursor.getColumnIndex("nStartX")));
				buttonInfo.setnStartY(cursor.getInt(cursor.getColumnIndex("nStartY")));
				buttonInfo.setnUpBackColor(cursor.getInt(cursor.getColumnIndex("nUpBackColor")));
				buttonInfo.setnUpForeColor(cursor.getInt(cursor.getColumnIndex("nUpForeColor")));
				buttonInfo.setnUpFrameColor(cursor.getInt(cursor.getColumnIndex("nUpFrameColor")));
				buttonInfo.setnWidth(cursor.getInt(cursor.getColumnIndex("nWidth")));
				buttonInfo.setsText(cursor.getString(cursor.getColumnIndex("sText")));
				buttonInfo.setASCIIStr(cursor.getString(cursor.getColumnIndex("sASCIIText")));
				list.add(buttonInfo);
			}
		}
		close(cursor);
		return list;
	}

	int number=0;
	public int getButtonNum(){
		if(db!=null){
			cursor = db.getDatabaseBySql("select count(*) as num from UDFKkeyBoard where nSceneId=?",new String[]{"1"});
			if (cursor!=null) {
				while (cursor.moveToNext()) {
					number=cursor.getInt(cursor.getColumnIndex("num"));
				}
			}
		}
		close(cursor);	
		return number;
	}
	
	/**
	 * 获取所有键盘id
	 */
	public ArrayList<Integer> getAllKeyBroadId(){
		ArrayList<Integer> mAllId=new ArrayList<Integer>();
		
		if(db!=null){
			int id=0;
			cursor = db.getDatabaseBySql("select  nSceneId from  keyBoard",null);
			if (cursor!=null) {
				while (cursor.moveToNext()) {
					id=cursor.getInt(cursor.getColumnIndex("nSceneId"));
					mAllId.add(id);
				}
			}
		}
		close(cursor);
		return mAllId;
	}
}
