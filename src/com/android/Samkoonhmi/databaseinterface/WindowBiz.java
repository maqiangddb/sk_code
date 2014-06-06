package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;
import java.util.HashMap;

import android.database.Cursor;

import com.android.Samkoonhmi.model.ScenceInfo;
import com.android.Samkoonhmi.skenum.BACKCSS;
import com.android.Samkoonhmi.skenum.IntToEnum;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;
import com.android.Samkoonhmi.skwindow.SKSceneManage.SHOW_TYPE;

/**
 * 窗口
 * @author 刘伟江
 * @version v 1.0.0.1
 * 创建时间 2012-7-5
 */
public class WindowBiz extends DataBase{
	SKDataBaseInterface db = null;
	
	public ScenceInfo select(int windowId){
		ScenceInfo info=null;
		String sql = "select * from windown where id=?";
		Cursor cursor = null;
		db = SkGlobalData.getProjectDatabase();
		if (db==null) {
			return null;
		}
		cursor=db.getDatabaseBySql(sql, new String[]{windowId+""});
		if (cursor!=null) {
			while (cursor.moveToNext()) {
				info=new ScenceInfo();
				info.setId(cursor.getInt(cursor.getColumnIndex("id")));
				info.setnSceneId(cursor.getInt(cursor.getColumnIndex("nSceneId")));
				info.setsScreenName(cursor.getString(cursor.getColumnIndex("sScreenName")));
				info.setnLeftX(cursor.getShort(cursor.getColumnIndex("nShowPosX")));
				info.setnLeftY(cursor.getShort(cursor.getColumnIndex("nShowPosY")));
				info.setnSceneWidth(cursor.getShort(cursor.getColumnIndex("nWindownWidth")));
				info.setnSceneHeight(cursor.getShort(cursor.getColumnIndex("nWindownHeight")));
				boolean title=cursor.getString(cursor.getColumnIndex("bShowTitle")).equals("true")?true:false;
				info.setbShowTitle(title);
				boolean btn=cursor.getString(cursor.getColumnIndex("bShowShutBtn")).equals("true")?true:false;
				info.setbShowShutBtn(btn);
				info.setsTileName(cursor.getString(cursor.getColumnIndex("sTileName")));
				info.setnBackColor(cursor.getInt(cursor.getColumnIndex("nBackColor")));
				boolean b=cursor.getString(cursor.getColumnIndex("bShowMiddle")).equals("true")?true:false;
				info.setbShowMiddle(b);
				info.seteBackType(getBackcss(cursor.getInt(cursor.getColumnIndex("eBackType"))));
				info.setnForeColor(cursor.getInt(cursor.getColumnIndex("nForeColor")));
				info.seteDrawStyle(IntToEnum.getCssType(cursor.getInt(cursor.getColumnIndex("eDrawStyle"))));
				//info.seteDrawStyle(CSS_TYPE.CSS_SOLIDCOLOR);
				boolean touch=cursor.getString(cursor.getColumnIndex("bLogout")).equals("true")?true:false;
				info.setbLogout(touch);
				info.setsPicturePath(cursor.getString(cursor.getColumnIndex("sPicturePath")));
			}
			close(cursor);
		}
		return info;
	}
	
	public HashMap<Integer, ScenceInfo> loadWindow(ArrayList<ScenceInfo> list){
		String sql = "select * from windown order by sNumber ";
		Cursor cursor = null;
		db = SkGlobalData.getProjectDatabase();
		if (db==null) {
			return null;
		}
		HashMap<Integer, ScenceInfo> map=new HashMap<Integer, ScenceInfo>();
		cursor=db.getDatabaseBySql(sql, null);
		if (cursor!=null) {
			while (cursor.moveToNext()) {
				ScenceInfo info=new ScenceInfo();
				info.setId(cursor.getInt(cursor.getColumnIndex("id")));
				info.setnSceneId(cursor.getInt(cursor.getColumnIndex("nSceneId")));
				info.setnNum(cursor.getInt(cursor.getColumnIndex("sNumber")));
				info.setsScreenName(cursor.getString(cursor.getColumnIndex("sScreenName")));
				info.setnLeftX(cursor.getShort(cursor.getColumnIndex("nShowPosX")));
				info.setnLeftY(cursor.getShort(cursor.getColumnIndex("nShowPosY")));
				info.setnSceneWidth(cursor.getShort(cursor.getColumnIndex("nWindownWidth")));
				info.setnSceneHeight(cursor.getShort(cursor.getColumnIndex("nWindownHeight")));
				boolean title=cursor.getString(cursor.getColumnIndex("bShowTitle")).equals("true")?true:false;
				info.setbShowTitle(title);
				boolean btn=cursor.getString(cursor.getColumnIndex("bShowShutBtn")).equals("true")?true:false;
				info.setbShowShutBtn(btn);
				info.setsTileName(cursor.getString(cursor.getColumnIndex("sTileName")));
				info.setnBackColor(cursor.getInt(cursor.getColumnIndex("nBackColor")));
				boolean b=cursor.getString(cursor.getColumnIndex("bShowMiddle")).equals("true")?true:false;
				info.setbShowMiddle(b);
				info.seteBackType(getBackcss(cursor.getInt(cursor.getColumnIndex("eBackType"))));
				info.setnForeColor(cursor.getInt(cursor.getColumnIndex("nForeColor")));
				info.seteDrawStyle(IntToEnum.getCssType(cursor.getInt(cursor.getColumnIndex("eDrawStyle"))));
				boolean touch=cursor.getString(cursor.getColumnIndex("bLogout")).equals("true")?true:false;
				info.setbLogout(touch);
				info.setsPicturePath(cursor.getString(cursor.getColumnIndex("sPicturePath")));
				info.setSceneMacroIDList(DBTool.getInstance().getmSceneBiz().selectMacroIDListBySceneID(info.getnSceneId()));					
				info.seteType(SHOW_TYPE.FLOATING);
				//加载到画面集合
				list.add(info);
				//加载到窗口集合
				map.put(info.getId(), info);
			}
			close(cursor);
		}
		return map;
	}
	
	/**
	 * 画面背景
	 */
	public BACKCSS getBackcss(int num){
		if (num==1) {
			return BACKCSS.BACK_CSS;
		}else if(num==2){
			return BACKCSS.BACK_IMG;
		}
		return BACKCSS.BACK_CSS;
	}
}
