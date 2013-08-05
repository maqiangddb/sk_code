package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.database.Cursor;

import com.android.Samkoonhmi.model.RecipeOprop;
import com.android.Samkoonhmi.model.RecipeSelectInfo;
import com.android.Samkoonhmi.model.RecipectItemInfo;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.skenum.IntToEnum;
import com.android.Samkoonhmi.skenum.RECIPESELECT_TYPE;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;

/**
 * 配方选择器
 * 
 * @author Administrator
 * 
 */
public class RecipeSelectBiz extends DataBase {
	private SKDataBaseInterface db;
	private SKDataBaseInterface dbdata = null;

	public RecipeSelectBiz() {
		db = SkGlobalData.getProjectDatabase();
		dbdata = SkGlobalData.getRecipeDatabase();

	}

	public ArrayList<RecipeSelectInfo> selectRecipeSelectInfo(int sceneId) {
		if (null == db) {
			db = SkGlobalData.getProjectDatabase();
		}
		Cursor cursor = db.getDatabaseBySql(
				"select * from recipeSelect where nSceneId=? ",
				new String[] { sceneId + "" });
		ArrayList<RecipeSelectInfo> infoList = null;
		if (null != cursor) {
			infoList = new ArrayList<RecipeSelectInfo>();
			while (cursor.moveToNext()) {
				RecipeSelectInfo info = new RecipeSelectInfo();
				info.setId(cursor.getInt(cursor.getColumnIndex("nItemId")));
				if (null != cursor
						.getString(cursor.getColumnIndex("bUseMacro"))) {
					info.setbUseMacro(cursor.getString(
							cursor.getColumnIndex("bUseMacro")).equals("true") ? true
							: false);
				}
				RECIPESELECT_TYPE eShowType = IntToEnum
						.getRecipeSelectType(cursor.getInt(cursor
								.getColumnIndex("eShowType")));
				info.seteShowType(eShowType);
				info.setnBackColor(cursor.getInt(cursor
						.getColumnIndex("nBackColor")));
				info.setnCurrShowRow(cursor.getInt(cursor
						.getColumnIndex("nCurrShowRow")));
				info.setnFontSize(cursor.getInt(cursor
						.getColumnIndex("nFontSize")));
				info.setnHeight(cursor.getInt(cursor.getColumnIndex("nHeight")));
				info.setnMacroId(cursor.getInt(cursor
						.getColumnIndex("nMacroId")));
				info.setnShowPropId(cursor.getInt(cursor
						.getColumnIndex("nShowPropId")));
				info.setnStartPosX(cursor.getInt(cursor
						.getColumnIndex("nStartPosX")));
				info.setnStartPosY(cursor.getInt(cursor
						.getColumnIndex("nStartPosY")));
				info.setnTextColor(cursor.getInt(cursor
						.getColumnIndex("nTextColor")));
				info.setnTouchPropId(cursor.getInt(cursor
						.getColumnIndex("nTouchPropId")));
				info.setnWidth(cursor.getInt(cursor.getColumnIndex("nWidth")));
				info.setsFontFamily(cursor.getString(cursor
						.getColumnIndex("sFontFamily")));
				info.setsShowRecipeId(cursor.getInt(cursor
						.getColumnIndex("sShowRecipeId")));
				info.setnZvalue(cursor.getInt(cursor.getColumnIndex("nZvalue")));
				info.setnCollidindId(cursor.getInt(cursor
						.getColumnIndex("nCollidindId")));
				info.setTouchInfo(TouchShowInfoBiz.getTouchInfoById(info
						.getId()));
				info.setShowInfo(TouchShowInfoBiz.getShowInfoById(info.getId()));

				info.setnTransparent(cursor.getInt(cursor
						.getColumnIndex("nTransparent")));
				infoList.add(info);
			}
			close(cursor);
		}
		return infoList;
	}

	public List<RecipectItemInfo> getSearchList(int gId, String name)

	{
		if (null == dbdata) {
			dbdata = SkGlobalData.getRecipeDatabase();
		}
		List<RecipectItemInfo> list = null;
		String sql = "select * from recipeNameML where sRecipeName like '%"
				+ name + "%' and  nGroupId = " + gId + " and nLanguageId = "
				+ SystemInfo.getCurrentLanguageId();
		Cursor cursor = dbdata.getDatabaseBySql(sql, null);
		if (null != cursor) {
			list = new ArrayList<RecipectItemInfo>();
			while (cursor.moveToNext()) {
				RecipectItemInfo itemInfo = new RecipectItemInfo();
				itemInfo.setChecked(false);
				itemInfo.setnGroupId(gId);
				itemInfo.setnRecipeId(cursor.getInt(cursor
						.getColumnIndex("nRecipeId")));
				itemInfo.setsRecipeName(cursor.getString(cursor
						.getColumnIndex("sRecipeName")));
				list.add(itemInfo);
			}
			cursor.close();
		}
		return list;
	}

	public List<RecipeOprop> getSearchList2(int gId, String name)

	{
		if (null == dbdata) {
			dbdata = SkGlobalData.getRecipeDatabase();
		}
		List<RecipeOprop> list = null;
		String sql = "select * from recipeNameML where sRecipeName like '%"
				+ name + "%' and  nGroupId = " + gId + " and nLanguageId = "
				+ SystemInfo.getCurrentLanguageId();
		Cursor cursor = dbdata.getDatabaseBySql(sql, null);
		if (null != cursor) {
			list = new ArrayList<RecipeOprop>();
			while (cursor.moveToNext()) {
				RecipeOprop item = new RecipeOprop();
				int id = cursor.getInt(cursor.getColumnIndex("nRecipeId"));
				item.setnRecipeId(id);
				// 根据配方号获取多语言的名字
				Vector<String> sRecipeName = getRecipeNames(gId, id);
				if (null != sRecipeName) {
					item.setsRecipeName(sRecipeName);
				}
				list.add(item);
			}
			cursor.close();
		}
		return list;
	}

	/**
	 * 获取一个配方不同语言下面的名字数组
	 * 
	 * @param gId
	 * @param pId
	 * @return
	 */
	private Vector<String> getRecipeNames(int gId, int pId) {
		Vector<String> sRecipeName = null;
		if (null == dbdata) {
			dbdata = SkGlobalData.getRecipeDatabase();
		}
		String sql = "select sRecipeName from recipeNameML where nGroupId = ? and nRecipeId =?";
		Cursor cursor = dbdata.getDatabaseBySql(sql, new String[] { gId + "",
				pId + "" });
		if (null != cursor) {
			sRecipeName = new Vector<String>();
			while (cursor.moveToNext()) {
				String name = cursor.getString(cursor
						.getColumnIndex("sRecipeName"));
				sRecipeName.add(name);
			}
		}
		close(cursor);
		return sRecipeName;
	}
}
