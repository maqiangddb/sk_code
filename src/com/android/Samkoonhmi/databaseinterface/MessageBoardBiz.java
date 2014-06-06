package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.ContentValues;
import android.database.Cursor;
import com.android.Samkoonhmi.model.MessageBoardInfo;
import com.android.Samkoonhmi.model.MessageDetailInfo;
import com.android.Samkoonhmi.skenum.DATE_FORMAT;
import com.android.Samkoonhmi.skenum.IntToEnum;
import com.android.Samkoonhmi.skenum.TIME_FORMAT;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;

public class MessageBoardBiz extends DataBase {
	private SKDataBaseInterface db;

	public MessageBoardBiz() {
		db = SkGlobalData.getProjectDatabase();

	}

	public ArrayList<MessageBoardInfo> selectMessageBoard(int sid) {
		if (null == db) {
			db = SkGlobalData.getProjectDatabase();
		}
		Cursor cursor = db.getDatabaseBySql(
				"select * from messageBoard where nSceneId=?",
				new String[] { sid + "" });
		
		ArrayList<MessageBoardInfo> list=new ArrayList<MessageBoardInfo>();
		String id = "";
		boolean init = true;
		
		if (null != cursor) {
			while (cursor.moveToNext()) {
				MessageBoardInfo info = new MessageBoardInfo();
				if (null != cursor
						.getString(cursor.getColumnIndex("bShowDate"))) {
					info.setbShowDate(cursor.getString(
							cursor.getColumnIndex("bShowDate")).equals("true") ? true
							: false);
				}
				if (null != cursor.getString(cursor.getColumnIndex("bShowId"))) {
					info.setbShowId(cursor.getString(
							cursor.getColumnIndex("bShowId")).equals("true") ? true
							: false);
				}
				if(null != cursor.getString(
						cursor.getColumnIndex("bShowTime")))
				{
				info.setbShowTime(cursor.getString(
						cursor.getColumnIndex("bShowTime")).equals("true") ? true
						: false);
				}
				info.setnAlpha(cursor.getInt(cursor.getColumnIndex("nAlpha")));
				info.setnBackColor(cursor.getInt(cursor
						.getColumnIndex("nBackColor")));
				info.setnCollidindId(cursor.getInt(cursor
						.getColumnIndex("nCollidindId")));
				DATE_FORMAT dateType = IntToEnum.getDateType(cursor
						.getInt(cursor.getColumnIndex("nDateType")));
				info.setnDateType(dateType);
				// CSS_TYPE cssType = IntToEnum.getCssType(cursor.getInt(cursor
				// .getColumnIndex("nFillCss")));
				// info.setnFillCss(cssType);
				info.setnFontColor(cursor.getInt(cursor
						.getColumnIndex("nFontColor")));
				info.setnFontSize(cursor.getInt(cursor
						.getColumnIndex("nFontSize")));
				info.setnInnerLineColor(cursor.getInt(cursor
						.getColumnIndex("nForeColor")));
				info.setnHeight(cursor.getInt(cursor.getColumnIndex("nHeight")));
				info.setnItemId(cursor.getInt(cursor.getColumnIndex("nItemId")));
				info.setnLineColor(cursor.getInt(cursor
						.getColumnIndex("nLineColor")));
				info.setnStartX(cursor.getInt(cursor.getColumnIndex("nStartX")));
				info.setnStartY(cursor.getInt(cursor.getColumnIndex("nStartY")));
				TIME_FORMAT timeType = IntToEnum.getTimeType(cursor
						.getInt(cursor.getColumnIndex("nTimeType")));
				info.setnTimeType(timeType);
				info.setnWidth(cursor.getInt(cursor.getColumnIndex("nWidth")));
				info.setnZvalue(cursor.getInt(cursor.getColumnIndex("nZvalue")));
				info.setsFontType(cursor.getString(cursor
						.getColumnIndex("sFontType")));
				info.setnTBackColor(cursor.getInt(cursor
						.getColumnIndex("nTBackColor")));
				info.setnTFontColor(cursor.getInt(cursor
						.getColumnIndex("nTFontColor")));
				info.setnTFontSize(cursor.getInt(cursor
						.getColumnIndex("nTFontSize")));
				info.setsTFontType(cursor.getString(cursor
						.getColumnIndex("sTFontType")));
				info.setShowInfo(TouchShowInfoBiz.getShowInfoById(info.getnItemId()));
				info.setTouchInfo(TouchShowInfoBiz.getTouchInfoById(info.getnItemId()));
				info.setnRowCount(cursor.getInt(cursor
						.getColumnIndex("nRowNumber")));
			
				// 查询留言表信息
				//info.setMessageDetails(getMessageList(itemId));
				
				list.add(info);
				if (init) {
					id += " nItemId=" + info.getnItemId();
					init = false;
				} else {
					id += " or nItemId=" + info.getnItemId();
				}

			}
			close(cursor);
			if (list.size()>0) {
				
				getTitle(list,id);
				
				// 获取表格信息
				getTableForHis(list,id);
			}
		}
		
		return list;
	}

	/**
	 * 获取表格信息
	 */
	private void getTableForHis(ArrayList<MessageBoardInfo> list,String id) {
		if (db != null) {
			Cursor cursor = null;
			String sql = "select * from tableProp where "+id;
			cursor = db.getDatabaseBySql(sql, null);
			
			int nItemId=-1;
			MessageBoardInfo info=null;
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
	
	private void getTitle(ArrayList<MessageBoardInfo> list,String id){
		Cursor cursor = null;
		if (null == db) {
			db = SkGlobalData.getProjectDatabase();
		}
		// 查找编号多语言
		cursor = db.getDatabaseBySql(
				"select * from messageBoardLanguage where "+id,null);
		MessageBoardInfo info=null;
		int nItemId=-1;
		if (cursor!=null) {
			while (cursor.moveToNext()) {
				if (nItemId != cursor.getInt(cursor.getColumnIndex("nItemId"))) {
					nItemId = cursor.getInt(cursor.getColumnIndex("nItemId"));
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getnItemId() == nItemId) {
							info = list.get(i);
							Map<Integer, String> msgMap=new HashMap<Integer, String>();
							Map<Integer, String> dateMap=new HashMap<Integer, String>();
							Map<Integer, String> timeMap=new HashMap<Integer, String>();
							Map<Integer, String> idMap=new HashMap<Integer, String>();
							info.setsMessageInfoList(msgMap);
							info.setsDateNameList(dateMap);
							info.setsTimeNameList(timeMap);
							info.setsNumberNameList(idMap);
							break;
						}
					}
				}
				if (info!=null) {
					int languageId = cursor.getInt(cursor
							.getColumnIndex("nLanguageIndex"));
					String sNumberName = cursor.getString(cursor
							.getColumnIndex("sNumber"));
					info.getsNumberNameList().put(languageId, sNumberName);
					
					String sDateName = cursor.getString(cursor
							.getColumnIndex("sDateName"));
					info.getsDateNameList().put(languageId, sDateName);
					
					String sTimeName = cursor.getString(cursor
							.getColumnIndex("sTimeName"));
					info.getsTimeNameList().put(languageId, sTimeName);
					
					String sMessageName = cursor.getString(cursor
							.getColumnIndex("sMessageName"));
					info.getsMessageInfoList().put(languageId, sMessageName);
				}
			}
			close(cursor);
		}
	}
	

	/**
	 * 查询所有的留言信息
	 * 
	 * @param itemId
	 * @return
	 */
	public List<MessageDetailInfo> getMessageList(int itemId,int top,int count) {
		List<MessageDetailInfo> list = new ArrayList<MessageDetailInfo>();
		MessageDetailInfo detail = null;
		Cursor cursor = null;
		if (null == db) {
			db = SkGlobalData.getProjectDatabase();
		}
		// 查找留言信息多语言
		String sql="select * from messageInfo where nItemId  =? order by nTime desc limit ?,?";
		cursor = db.getDatabaseBySql(sql,new String[] { itemId + "",(top-1)+"",count+"" });
		if (cursor != null) {
			while (cursor.moveToNext()) {
				detail = new MessageDetailInfo();
				detail.setnId(cursor.getInt(cursor.getColumnIndex("id")));
				detail.setnItemId(cursor.getInt(cursor
						.getColumnIndex("nItemId")));
				detail.setnTime(cursor.getLong(cursor.getColumnIndex("nTime")));
				detail.setsMessage(cursor.getString(cursor
						.getColumnIndex("sMessage")));
				// detail.setsTitle(cursor.getString(cursor
				// .getColumnIndex("sTitle")));
				list.add(detail);
			}
		}
		return list;
	}
	
	/**
	 * 查询留言总数
	 */
	public int getDataCount(int itemId) {
		int result=0;
		Cursor cursor = null;
		if (null == db) {
			db = SkGlobalData.getProjectDatabase();
		}
		
		// 查找留言信息多语言
		String sql="select count(*) from messageInfo where nItemId="+itemId;
		cursor = db.getDatabaseBySql(sql,null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				result=cursor.getInt(0);
			}
			close(cursor);
		}
		return result;
	}

	/**
	 * 新增留言信息
	 * 
	 * @param info
	 * @return
	 */
	public boolean insertMessage(MessageDetailInfo info) {
		boolean b = false;
		if (null == db) {
			db = SkGlobalData.getProjectDatabase();
		}
		try {
			ContentValues values = new ContentValues();
			values.put("nItemId", info.getnItemId());
			values.put("nTime", info.getnTime());
			values.put("sMessage", info.getsMessage());
			values.put("id", info.getnId());
			// values.put("sTitle", info.getsTitle());
			db.insertData("messageInfo", values);
			b = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return b;
	}

	/**
	 * 修改留言信息
	 * 
	 * @param info
	 * @return
	 */
	public boolean updateMessage(MessageDetailInfo info) {
		if (null == db) {
			db = SkGlobalData.getProjectDatabase();
		}
		ContentValues values = new ContentValues();
		values.put("nTime", info.getnTime());
		values.put("sMessage", info.getsMessage());
		// values.put("sTitle", info.getsTitle());
		values.put("nItemId", info.getnItemId());
		String[] whereColoum = new String[] { "" + info.getnId() };
		int i = db
				.updateByUserDef("messageInfo", values, "id = ?", whereColoum);
		if (i > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 删除留言信息
	 * 
	 * @param info
	 * @return
	 */
	public boolean deleteMessage(int id) {
		boolean result = false;
		if (null == db) {
			db = SkGlobalData.getProjectDatabase();
		}
		int num = 0;
		num = db.deleteByUserDef("messageInfo", "id=?",
				new String[] { id + "" });
		if (num > 0) {
			result = true;
		}
		return result;
	}

	public int getMaxId() {
		int maxId = 0;
		if (null == db) {
			db = SkGlobalData.getProjectDatabase();
		}
		Cursor cursor = db.getDatabaseBySql(
				"select max(id) as mId from messageinfo", null);
		if (null != cursor) {
			while (cursor.moveToNext()) {
				maxId = cursor.getInt(cursor.getColumnIndex("mId"));
			}
		}
		return maxId;
	}
	/**
	 * 清除所有的留言信息
	 * @return
	 */
	public boolean deleteMessage()
	{
		boolean result = false;
		if (null == db) {
			db = SkGlobalData.getProjectDatabase();
		}
		int num = 0;
		num = db.deleteByUserDef("messageInfo", null, null);
		if(num > 0)
		{
			result = true;
		}
		return result;
	}
	/**
	 * 是否有留言信息
	 * @return
	 */
	public boolean hasMessage()
	{
		int result=0;
		Cursor cursor = null;
		if (null == db) {
			db = SkGlobalData.getProjectDatabase();
		}
		
		// 查找留言信息多语言
		String sql="select count(*) from messageInfo ";
		cursor = db.getDatabaseBySql(sql,null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				result=cursor.getInt(0);
			}
			close(cursor);
		}
		if(result>0)
		{
			return true;
		}else{
			return false;
		}
	}
	/**
	 * 查找所有的留言板信息
	 * @return
	 */
	public List<MessageDetailInfo> getAllMessageContent()
	{
		List<MessageDetailInfo> list = new ArrayList<MessageDetailInfo>();
		MessageDetailInfo detail = null;
		Cursor cursor = null;
		if (null == db) {
			db = SkGlobalData.getProjectDatabase();
		}
		// 查找留言信息多语言
		String sql="select * from messageInfo order by nTime desc";
		cursor = db.getDatabaseBySql(sql,null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				detail = new MessageDetailInfo();
				detail.setnId(cursor.getInt(cursor.getColumnIndex("id")));
				detail.setnItemId(cursor.getInt(cursor
						.getColumnIndex("nItemId")));
				detail.setnTime(cursor.getLong(cursor.getColumnIndex("nTime")));
				detail.setsMessage(cursor.getString(cursor
						.getColumnIndex("sMessage")));
				list.add(detail);
			}
		}
		return list;
	}
}
