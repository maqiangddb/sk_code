package com.android.Samkoonhmi.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils.TruncateAt;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.databaseinterface.SKDataBaseInterface;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;


/**
 * 文件操作
 */
public class FileOper {

	/**
	 * 对外API
	 * 存储用户设置的信息
	 * key-密匙
	 * info-信息
	 * extend-扩展信息
	 */
	private static String CREATETABLE="Create TABLE IF NOT EXISTS[userInfoTable] (id integer PRIMARY KEY AUTOINCREMENT NOT NULL"
			+",key nvarchar"
	        +",info nvarchar"
	        +")";
	private SKDataBaseInterface db;
	private static HashMap<String, String> map=new HashMap<String, String>();
	private static FileOper sInstance = null;
	public static FileOper getInstance() {
		if (sInstance == null) {
			sInstance = new FileOper();
		}
		return sInstance;
	}
	
	public FileOper(){
		db=SkGlobalData.getProjectDatabase();
		db.execSql(CREATETABLE);
	}
	
	/**
	 * 保存信息到文件
	 * @param key-用于获取信息key
	 * @param info-保存信息
	 */
	public synchronized boolean saveInfoToFile(String key,String info){
		boolean result=false;
		if (db==null||key==null) {
			return false;
		}
		
		try {
			
			key=key.trim();
			info=info.trim();
			
			//Log.d("FileOper", " ... key="+key+",info="+info);
			String ss="select * from userInfoTable where key='"+key+"'";
			Cursor cursor = db.getDatabaseBySql(ss,null);
			if (cursor!=null) {
				if(cursor.moveToNext()){
					String sql="update userInfoTable set info='"+info+"' where key='"+key+"'";
					db.execSql(sql);
					cursor.close();
				}else{
					ContentValues lockvalues = new ContentValues();
					lockvalues.put("key", key);
					lockvalues.put("info", info);
					db.insertData("userInfoTable",lockvalues);
				}
			}else {
				ContentValues lockvalues = new ContentValues();
				lockvalues.put("key", key);
				lockvalues.put("info", info);
				db.insertData("userInfoTable",lockvalues);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 从文件中读取对应的信息
	 * @param key
	 */
	public synchronized String readInfoFromFile(String key){
		if (db==null||key==null) {
			return "";
		}
		
		
		String result="";
		
		try {
			
			key=key.trim();
			String sql="select * from userInfoTable where key='"+key+"'";
			Cursor cursor = db.getDatabaseBySql(sql,null);
			if (cursor==null) {
				return "";
			}
			
			while (cursor.moveToNext()) {
				result=cursor.getString(cursor.getColumnIndex("info"));
				cursor.close();
			}
			//map.put(key, result);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 从文件中读取对应的信息
	 * @param key
	 */
	public synchronized String readCollectFile(String name){
		if (db==null) {
			return "";
		}

		String result="";
		
		try {
			Cursor cursor = db.getDatabaseBySql("select * from userInfoTable where key=?",new String[]{name});
			if (cursor==null) {
				return "";
			}
			
			if(cursor.moveToNext()){
				result=cursor.getString(cursor.getColumnIndex("info"));
				cursor.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	/**********************对外接口 存储用户创建的表************************/
	private SQLiteDatabase mUserDB=null;
	/**
	 * 创建数据库表
	 * @param sql-语句
	 */
	public synchronized boolean sqlCreateTable(String sql){
		boolean result=false;
		if (mUserDB==null) {
			openDatabase();
		}
		if (mUserDB!=null) {
			mUserDB.execSQL(sql);
			result=true;
		}
		return result;
	}
	
	/**
	 * 创建数值
	 * @param sql-语句
	 */
	public synchronized boolean sqlExec(String sql) {
		boolean result=false;
		if(sql==null||sql.equals("")){
			return false;
		}
		if (mUserDB==null) {
			openDatabase();
		}
		if (mUserDB!=null) {
			mUserDB.execSQL(sql);
			result=true;
		}
		return result;
	}
	
	/**
	 * 查询数据
	 * @param sql-语句
	 */
	public synchronized Cursor sqlSelect(String sql){
		if(sql==null||sql.equals("")){
			return null;
		}
		if (mUserDB==null) {
			openDatabase();
		}
		Cursor cursor=null;
		if (mUserDB!=null) {
			cursor=mUserDB.rawQuery(sql, null);
		}
		return cursor;
	}
	
	/**
	 * 打开用户数据库
	 */
	public void openDatabase() {
		try {
			String dbFilename = "/data/data/com.android.Samkoonhmi/databases/user.dat";
			File dir = new File("/data/data/com.android.Samkoonhmi/databases/");
			if (!dir.exists()) {
				dir.mkdir();
			}
			if (!(new File(dbFilename)).exists()) {
				InputStream is =ContextUtl.getInstance().getResources().openRawResource(
						R.raw.user);
				FileOutputStream fos = new FileOutputStream(dbFilename);
				byte[] buffer = new byte[8192];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
			}
			mUserDB = SQLiteDatabase.openOrCreateDatabase(dbFilename,null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
