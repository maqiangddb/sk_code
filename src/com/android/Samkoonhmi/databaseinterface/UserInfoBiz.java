package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.model.UserInfo;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;

/**
 * 查找登录用户信息
 * @author Administrator
 */
public class UserInfoBiz extends DataBase {
	private SKDataBaseInterface db;
	private UserInfo info;

	public UserInfoBiz() {
		db = SkGlobalData.getProjectDatabase();

	}

	public UserInfo getUser(String userName, String passWord) {
		Cursor cursor=null;
		StringBuffer sqlBuffer=new StringBuffer("select * from userList where sUserName=?");
		if(null != passWord  && !"".equals(passWord))
		{
			sqlBuffer.append("  and  sPassword=?");
			 cursor = db.getDatabaseBySql(
					sqlBuffer.toString(),
					new String[] { userName, passWord });
		}else{
			sqlBuffer = new StringBuffer("select * from userList where sUserName=? and sPassword='' ");
			 cursor = db.getDatabaseBySql(
						sqlBuffer.toString(),
						new String[] { userName});
		}
		
		if (null != cursor) {
			info = new UserInfo();
			ArrayList<Integer> groupList = new ArrayList<Integer>();
			while (cursor.moveToNext()) {

				info.setId(cursor.getInt(cursor.getColumnIndex("sUserId")));
				info.setName(cursor.getString(cursor
						.getColumnIndex("sUserName")));
				info.setPassword(cursor.getString(cursor
						.getColumnIndex("sPassword")));
				int gId = cursor.getInt(cursor.getColumnIndex("nGroupId"));
				groupList.add(gId);

			}
			info.setGroupId(groupList);
		}
		close(cursor);
		return info;
	}

	/**
	 * 根据userId 获取该用户拥有的组
	 */
	public ArrayList<UserInfo> getGroupList(int userId,boolean check) {
		ArrayList<UserInfo> list = null;
		if (db != null) {
			String sql = "select * from userList where sUserId=?";
			Cursor cursor = db.getDatabaseBySql(sql,
					new String[] { userId + "" });
			if (cursor != null) {
				list = new ArrayList<UserInfo>();
				while (cursor.moveToNext()) {
					UserInfo info = new UserInfo();
					info.setId(cursor.getShort(cursor
							.getColumnIndex("nGroupId")));
					info.setName(cursor.getString(cursor
							.getColumnIndex("sGroupName")));
					info.setDescript(cursor.getString(cursor
							.getColumnIndex("sGroupDescript")));
					info.setCheck(check);
					list.add(info);
				}
			}
			close(cursor);
		}
		return list;
	}
	
	/**
	 * 获取所有组
	 */
	public ArrayList<UserInfo> getGroupList() {
		ArrayList<UserInfo> list = new ArrayList<UserInfo>();
		if (db != null) {
			String sql = "select * from userList group by nGroupId";
			Cursor cursor = db.getDatabaseBySql(sql,null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					UserInfo info = new UserInfo();
					info.setId(cursor.getShort(cursor
							.getColumnIndex("nGroupId")));
					info.setName(cursor.getString(cursor
							.getColumnIndex("sGroupName")));
					info.setDescript(cursor.getString(cursor
							.getColumnIndex("sGroupDescript")));
					info.setCheck(false);
					list.add(info);
				}
			}
			close(cursor);
		}
		return list;
	}
	
	/**
	 * 获取默认用户
	 */
	public UserInfo selectDefaultUser() {
		Cursor cursor = db.getDatabaseBySql(
				"select * from userList where sUserId=0", null);
		if (null != cursor) {
			info = new UserInfo();
			ArrayList<Integer> groupList = new ArrayList<Integer>();
			while (cursor.moveToNext()) {

				info.setId(cursor.getInt(cursor.getColumnIndex("sUserId")));
				info.setName(cursor.getString(cursor
						.getColumnIndex("sUserName")));
				info.setPassword(cursor.getString(cursor
						.getColumnIndex("sPassword")));
				int gId = cursor.getInt(cursor.getColumnIndex("nGroupId"));
				groupList.add(gId);

			}
			info.setGroupId(groupList);
		}
		close(cursor);
		return info;
	}

	/**
	 * 查询用户名是否存在
	 */
	public boolean existUserName(String name){
		boolean b=false;
		Cursor cursor = db.getDatabaseBySql(
				"select count(*) as num from userList where sUserName=?", new String[]{name});
		if (null != cursor) {
			while (cursor.moveToNext()) {
				int num=cursor.getInt(cursor.getColumnIndex("num"));
				if(num>0){
					b=true;
				}
			}
		}
		close(cursor);
		return b;
	}
	
	/**
	 * 根据用户id 获取用户
	 */
	public UserInfo getUserInfoById(int userId) {
		UserInfo info = null;
		Cursor cursor = db.getDatabaseBySql(
				"select * from userList where sUserId=?", new String[] { userId
						+ "" });
		if (null != cursor) {
			info = new UserInfo();
			ArrayList<Integer> groupList = new ArrayList<Integer>();
			while (cursor.moveToNext()) {
				info.setId(cursor.getInt(cursor.getColumnIndex("sUserId")));
				info.setName(cursor.getString(cursor
						.getColumnIndex("sUserName")));
				info.setPassword(cursor.getString(cursor
						.getColumnIndex("sPassword"))+"");
				info.setDescript(cursor.getString(cursor.getColumnIndex("sUserDescript"))+"");
				int gId = cursor.getInt(cursor.getColumnIndex("nGroupId"));
				groupList.add(gId);
			}
			info.setGroupId(groupList);
		}
		close(cursor);
		return info;
	}
	
	/**
	 * 获取所有用户信息
	 */
	public ArrayList<UserInfo> getUserList(){
		ArrayList<UserInfo> list = new ArrayList<UserInfo>();
		//用户id集合
		ArrayList<Integer> mUserIdList=null;
		if (db != null) {
			String sql = "select distinct sUserId  from  userList";
			Cursor cursor = db.getDatabaseBySql(sql,null);
			if (cursor != null) {
				mUserIdList = new ArrayList<Integer>();
				while (cursor.moveToNext()) {
					int id=cursor.getInt(0);
					if(id>=0){
						mUserIdList.add(id);
					}
				}
			}
			close(cursor);
		}
		if (mUserIdList==null||mUserIdList.size()==0) {
			Log.e("UserInfoBiz", "get all user, user=null!");
			return null;
		}
		
		//根据用户id，获取用户信息
		for (int i = 0; i < mUserIdList.size(); i++) {
			UserInfo info=getUserInfoById(mUserIdList.get(i));
			if (info!=null) {
				list.add(info);
			}
		}
		return list;
	}
	public ArrayList<String> getUserNameList()
	{
		ArrayList<UserInfo> userInfoList = getUserList();
		ArrayList<String> userNameList = new ArrayList<String>();
		if(null != userInfoList)
		{
			if(userInfoList.size()!=0)
			{
				for(int i=0;i<userInfoList.size();i++)
				{
					UserInfo info = userInfoList.get(i);
					if(null != info)
					{
						userNameList.add(info.getName());
					}
				}
			}
		}
		return userNameList;
	}
	
	/**
	 * 插入用户
	 * @param info-用户信息
	 * @param list-用户组信息
	 * @param addId-用户表id是否要加一
	 */
	public boolean insertUser(UserInfo info,ArrayList<UserInfo> list,boolean addId){
		boolean b=false;
		if (list==null||list.size()==0||db==null) {
			return b;
		}
		
		int id=0;
		String sql="select max(sUserId) as id from userList";
		try {
			Cursor cursor=db.getDatabaseBySql(sql, null);
			if (cursor!=null) {
				while (cursor.moveToNext()) {
					id=cursor.getInt(cursor.getColumnIndex("id"));
				}
			}
			close(cursor);
			if (addId) {
				//插入新的用户
				id++;
			}else {
				//用于更新用户信息
				id=info.getId();
			}
			
			for (int i = 0; i < list.size(); i++) {
				ContentValues values = new ContentValues();
				values.put("nGroupId",list.get(i).getId());
				values.put("sGroupName",list.get(i).getName());
				values.put("sGroupDescript",list.get(i).getDescript()+"");
				values.put("sUserId",id);
				values.put("sUserName", info.getName());
				values.put("sUserDescript",info.getDescript()+"");
				values.put("sPassword",info.getPassword()+"");
				db.insertData("userList", values);
			}
			b=true;
			
			SystemInfo.setUserNameList(getUserNameList());
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("UserInfoBiz", "insert user error!");
		}
		return b;
	}
	
	/**
	 * 更新用户信息
	 * @param info-用户信息
	 * @param list-用户组信息
	 * @param adminId-更新用户信息的用户id
	 */
	public boolean updateUser(UserInfo info,ArrayList<UserInfo> list,int adminId){
		boolean result=false;
		//先删除旧的用户信息
		if (list==null||list.size()==0) {
			return result;
		}
		result=deleteUser(info.getId(), adminId);
		if (result) {
			//插入新的用户信息
			result=insertUser(info,list,false);
		}
		return result;
	}
	
	public void updateUser(UserInfo info){
		if (db==null||info==null) {
			return;
		}
		String sql="update userList set sPassword='"+info.getPassword()
				+"' , sUserDescript='"+info.getDescript()+"'  where sUserName='"+info.getName()+"'";
		db.execSql(sql);
	}
	
	/**
	 * 删除用户
	 */
	public boolean deleteUser(int id){
		boolean result=false;
		if (db!=null) {
			int num=0;
			num=db.deleteByUserDef("userList", "sUserId=?", new String[]{id+""});
			if (num>0) {
				result=true;
				SystemInfo.setUserNameList(getUserNameList());
			}
		}
		return result;
	}
	
	/**
	 * 更加用户id和组id 删除用户信息
	 * @param id-用户id
	 * @param list-用户组信息
	 */
	public boolean deleteUser(int id,int adminId){
		boolean result=false;
		
		try {
			ArrayList<Integer> list=new ArrayList<Integer>();
			if (db!=null) {
				String sql="select nGroupId from  userlist where sUserId=?";
				Cursor cursor=db.getDatabaseBySql(sql, new String[]{adminId+""});
				if(cursor!=null){
					while (cursor.moveToNext()) {
						int gId=cursor.getInt(cursor.getColumnIndex("nGroupId"));
						list.add(gId);
					}
				}
				close(cursor);
			}
			for (int i = 0; i < list.size(); i++) {
				db.deleteByUserDef("userList", "sUserId=? and nGroupId=?", new String[]{id+"",list.get(i)+""});
			}
			result=true;
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("UserInfoBiz", "delete user error!");
		}
		
		return result;
	}
}
