package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;
import java.util.Vector;
import android.database.Cursor;

import com.android.Samkoonhmi.model.RecipeOprop;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.model.skglobalcmn.RecipeDataProp;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.IntToEnum;
import com.android.Samkoonhmi.skenum.STORAGE_MEDIA;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;
import com.android.Samkoonhmi.util.AddrProp;

/**
 * 配方数据操作
 */
public class RecipeDataBiz {
	
	private SKDataBaseInterface db = null;
	
	public RecipeDataBiz(){
		db = SkGlobalData.getRecipeDatabase();
	}
	
	public static boolean select() 
	{
		/* 按照id的升序查询 */
		SKDataBaseInterface dbObj = SkGlobalData.getRecipeDatabase();
		if(null == dbObj) return false;
		
		Cursor dataProp = dbObj.getDatabaseBySql(
				"select * from recipeCollectGroup order by nGroupId", null);
		if (null == dataProp) return false;

		RecipeDataProp.recipeOGprop tmpGroupData = null;
		RecipeDataProp.getInstance().getmRecipeGroupList().clear();

		int nTmpEnum = 0;
		while (dataProp.moveToNext()) {
			tmpGroupData = RecipeDataProp.getInstance().new recipeOGprop();
			
			/* 配方组的ID */
			tmpGroupData.setnGRecipeID(dataProp.getInt(dataProp.getColumnIndex("nGroupId")));

			/* 配方组的名字 */
			tmpGroupData.setsRecipeGName(dataProp.getString(dataProp.getColumnIndex("sRecipeGName")));

			/* 配方组的描述 */
			tmpGroupData.setsRecipeGDescri(dataProp.getString(dataProp.getColumnIndex("sRecipeGDescri")));

			/* 配方组的配方数量 */
			tmpGroupData.setnRecipeNum(dataProp.getInt(dataProp.getColumnIndex("nRecipeNum")));

			/* 配方组的配方地址长度 */
			tmpGroupData.setnRecipeLen(dataProp.getInt(dataProp.getColumnIndex("nRecipeLen")));
			
			/*是否是连续地址*/
			nTmpEnum = dataProp.getShort(dataProp.getColumnIndex("nContinue"));
			if(nTmpEnum == 1)
			{
				tmpGroupData.setbContinue(true);
			}
			else
			{
				tmpGroupData.setbContinue(false);
			}
			
			/*自定义键盘*/
			tmpGroupData.setnKeyId(dataProp.getInt(dataProp.getColumnIndex("nKeyId")));
			
			/*自定义键盘X坐标*/
			tmpGroupData.setnBoardX(dataProp.getInt(dataProp.getColumnIndex("nBoardX")));
			
			/*自定义键盘Y坐标*/
			tmpGroupData.setnBoardY(dataProp.getInt(dataProp.getColumnIndex("nBoardY")));

			/* 保存的媒介 */
			nTmpEnum = dataProp.getShort(dataProp.getColumnIndex("eSaveMedia"));
			switch (nTmpEnum) {
			case 1: {
				tmpGroupData.seteSaveMedia(STORAGE_MEDIA.INSIDE_DISH);
				break;
			}
			case 2: {
				tmpGroupData.seteSaveMedia(STORAGE_MEDIA.U_DISH);
				break;
			}
			case 3: {
				tmpGroupData.seteSaveMedia(STORAGE_MEDIA.SD_DISH);
				break;
			}
			default: {
				break;
			}
			}
			
			/* 是否需要控制地址 */
			nTmpEnum = dataProp.getShort(dataProp.getColumnIndex("bNeedCtlAddr"));
			if(nTmpEnum == 1)
			{
				tmpGroupData.setbNeedCtlAddr(true);
			}
			else
			{
				tmpGroupData.setbNeedCtlAddr(false);
			}

			/* 控制地址ID */
			int nCtlAddrId = dataProp.getInt(dataProp.getColumnIndex("mCtlAddrId"));
			AddrProp mTmpAddr = null;
			mTmpAddr = dbObj.getAddrById(nCtlAddrId);
			tmpGroupData.setmCtlAddr(mTmpAddr);

			/* 传输完成是否需要通知 */
			nTmpEnum = dataProp.getShort(dataProp.getColumnIndex("bCompleteNotic"));
			if(nTmpEnum == 1)
			{
				tmpGroupData.setbCompleteNotic(true);
			}
			else
			{
				tmpGroupData.setbCompleteNotic(false);;
			}

			/* 完成通知地址 */
			int nNoticAddrId = dataProp.getInt(dataProp.getColumnIndex("mComNoticAddrId"));
			mTmpAddr = dbObj.getAddrById(nNoticAddrId);
			tmpGroupData.setmComNoticAddr(mTmpAddr);

			RecipeDataProp.getInstance().getmRecipeGroupList().add(tmpGroupData);
		}
		dataProp.close();

		/* 查询单个配方 */
		int nGroupSize = RecipeDataProp.getInstance().getmRecipeGroupList().size();
		for (int i = 0; i < nGroupSize; i++) {
			RecipeOprop tmpRecipeData = new RecipeOprop();
			RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getmRecipePropList().clear();
			
			/*配方组ID*/
			int nGRecipeId = RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getnGRecipeID();

			dataProp = dbObj.getDatabaseBySql(
					"select * from recipeNameML where nGroupId = " + nGRecipeId + " order by nRecipeId, nLanguageId ", null);

			if(null == dataProp) continue;

			Vector<String> nameList = new Vector<String>();
			Vector<String> descriList = new Vector<String>();
			boolean bFirst = true;
			while (dataProp.moveToNext()) {
				/* 语言号 */
				int nLanguageId = dataProp.getShort(dataProp.getColumnIndex("nLanguageId"));
				if (nLanguageId == 0) {
					if (!bFirst) {
						/* 配方的名字 */
						tmpRecipeData.setsRecipeName(nameList);

						/* 配方的描述 */
						tmpRecipeData.setsRecipeDescri(descriList);

						RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getmRecipePropList().add(tmpRecipeData);

						nameList = new Vector<String>();
						descriList = new Vector<String>();
						tmpRecipeData = new RecipeOprop();
						nameList.clear();
						descriList.clear();
					}

					/* 配方的ID */
					tmpRecipeData.setnRecipeId(dataProp.getInt(dataProp.getColumnIndex("nRecipeId")));
				}
				bFirst = false;
				nameList.add(dataProp.getString(dataProp.getColumnIndex("sRecipeName")));
				descriList.add(dataProp.getString(dataProp.getColumnIndex("sRecipeDescri")));
			}

			/* 最后一次添加 , 配方的名字*/
			tmpRecipeData.setsRecipeName(nameList);

			/* 配方的描述 */
			tmpRecipeData.setsRecipeDescri(descriList);
			RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getmRecipePropList().add(tmpRecipeData);
			dataProp.close();
		}

		/* 查询单个配方元素 */
		for (int i = 0; i < nGroupSize; i++) 
		{
			/*配方组ID*/
			int nGRecipeId = RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getnGRecipeID();
			dataProp = dbObj.getDatabaseBySql(
					"select * from recipeElemML where nGroupId = " + nGRecipeId + " order by nElemIndex , nLanguageId ", null);

			if (null == dataProp) continue;
			Vector<String> nameList = new Vector<String>();
			boolean bFirst = true;
			
			/*先清除数据*/
			RecipeDataProp.getInstance().getmRecipeGroupList().get(i).geteDataTypeList().clear();
			RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getsElemNameList().clear();
			while (dataProp.moveToNext())
			{
				/* 语言号 */
				int nLanguageId = dataProp.getShort(dataProp.getColumnIndex("nLanguageId"));
				if (nLanguageId == 0) {
					if(!bFirst)
					{
						/* 配方的元素名称 */
						RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getsElemNameList().add(nameList);
						nameList = new Vector<String>();
						nameList.clear();
					}
					
					/* 添加数据类型 */
					nTmpEnum = dataProp.getShort(dataProp.getColumnIndex("eDataType"));
					DATA_TYPE eDataType = IntToEnum.getDataType(nTmpEnum);
					RecipeDataProp.getInstance().getmRecipeGroupList().get(i).geteDataTypeList().add(eDataType);
				}
				bFirst = false;
				nameList.add(dataProp.getString(dataProp.getColumnIndex("sElemName")));
			}

			/* 配方的元素名称,最后一次添加 */
			RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getsElemNameList().add(nameList);
			dataProp.close();
			
			/* 查询采集地址集 */
			Vector<AddrProp> mTmpAddrList = null;
			mTmpAddrList = dbObj.getAddrListBySql(
					"select * from addr where nItemId = " + i + " and nAddrNum >= 0 order by nAddrNum", null);
			RecipeDataProp.getInstance().getmRecipeGroupList().get(i).setnValueAddrList(mTmpAddrList);
		}

		/* 查询配方组的值 */
//		for (int i = 0; i < nGroupSize; i++) 
//		{
//			/*配方组ID*/
//			int nGRecipeId = RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getnGRecipeID();
//			int nRecipeNum = RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getmRecipePropList().size();
//			for (int j = 0; j < nRecipeNum; j++)
//			{
//				int nRecipeId = RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getmRecipePropList().get(j).getnRecipeId();
//				dataProp = dbObj.getDatabaseBySql(
//						"select * from recipeCollectData where nGroupId = " + nGRecipeId + " and nRecipeId = " + nRecipeId + " order by nElemIndex", null);
//
//				if (null == dataProp) continue;
//
//				RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getmRecipePropList().get(j).getnRecipeValueList().clear();
//				while (dataProp.moveToNext()) {
//					double value = dataProp.getDouble(dataProp.getColumnIndex("nRecipeValue"));
//
//					/* 配方的值 */
//					RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getmRecipePropList().get(j).getnRecipeValueList().add(value);
//				}
//				dataProp.close();
//			}
//		}
		
//		for (int i = 0; i < nGroupSize; i++) 
//		{
//			/*配方组ID*/
//			int nGRecipeId = RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getnGRecipeID();
//			int nRecipeNum = RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getmRecipePropList().size();
//			for (int j = 0; j < nRecipeNum; j++)
//			{
//				long nCurrMillis = System.currentTimeMillis();
//				
//				int nRecipeId = RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getmRecipePropList().get(j).getnRecipeId();
//				dataProp = dbObj.getDatabaseBySql(
//						"select * from recipegroup" + nGRecipeId + " where nRecipeId = " + nRecipeId , null);
//
//				if (null == dataProp) continue;
//
//				if(dataProp.moveToNext())
//				{
//					String sValues = dataProp.getString(dataProp.getColumnIndex("elems"));
//					if(sValues != null )
//					{
//						String[] sValueList = sValues.split("[+]");
//						RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getmRecipePropList().get(j).setnRecipeValueList(sValueList);
//					}
//				}
//				
//				System.out.println("get group" + i + " recipe" + j + " need time :" + (System.currentTimeMillis() - nCurrMillis) + "ms");
//				
//				dataProp.close();
//			}
//		}

		if(nGroupSize > 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
//	/**
//	 * 添加配方
//	 * @param gid-配方组id
//	 * @param pid-配方id
//	 * @param data-配方数据
//	 * @param len-配方长度
//	 */
//	public boolean addRecipe(recipeOprop data,int gid,int len){
//		if (db!=null) {
//			if (data==null) {
//				return false;
//			}
//			try {
//				//插入元素-数据
//				int nRecipeIndex=getRecipeIndex();
//				int nElemIndex=0;
//				Vector<Double> dataVector=data.getnRecipeValueList();
//				if (dataVector!=null) {
//					ArrayList<Integer> aList=getRecipeAddr(gid);
//					//nElemIndex=getElemIndex(gid);
//					if (aList!=null) {
//						for (int i = 0; i < len; i++) {
//							ContentValues values=new ContentValues();
//							values.put("nGroupId", gid);
//							values.put("nRecipeId", data.getnRecipeId());
//							values.put("nRecipeIndex", nRecipeIndex);
//							values.put("nElemIndex", nElemIndex);
//							if(aList.size()<=len){
//								values.put("nValueAddrId", aList.get(i));
//							}
//							if (i<dataVector.size()) {
//								values.put("nRecipeValue", dataVector.get(i));
//							}else {
//								values.put("nRecipeValue", 0);
//							}
//							db.insertData("recipeCollectData", values);
//							nElemIndex++;
//						}
//					}
//				}
//				
//				//插入配方名称和配方描述
//				for (int i = 0; i < SystemInfo.getLanguageNumber(); i++) {
//					ContentValues value=new ContentValues();
//					value.put("nLanguageId", i);
//					value.put("nGroupId", gid);
//					value.put("nRecipeIndex", nRecipeIndex);
//					value.put("nRecipeId", data.getnRecipeId());
//					value.put("sRecipeName", data.getsRecipeName().get(i));
//					value.put("sRecipeDescri", data.getsRecipeDescri().get(i));
//					db.insertData("recipeNameML", value);
//				}
//				return true;
//				
//			} catch (Exception e) {
//				e.printStackTrace();
//				Log.e("RecipeDataBiz", "insert recipe error!");
//			}
//			
//		}
//		return false;
//	}
//	
//	/**
//	 * 删除配方
//	 */
//	public boolean deleteRecipe(int gid,int pid){
//		if (db==null) {
//			return false;
//		}
//		//Log.d("SKScene", "gid:"+gid+",pid:"+pid+",lid:"+SystemInfo.getCurrentLanguageId()+"");
//		int num=db.deleteByUserDef("recipeCollectData", "nGroupId=? and nRecipeId=?", new String[]{gid+"",pid+""});
//		int nums=db.deleteByUserDef("recipeNameML", "nGroupId=? and nRecipeId=? and nLanguageId=?",
//				new String[]{gid+"",pid+"",SystemInfo.getCurrentLanguageId()+""});
//		if(num>0&&nums>0){
//			return true;
//		}else {
//			return false;
//		}
//	}
//	
//	/**
//	 * 删除配方
//	 */
//	public boolean deleteGroupRecipe(int gid){
//		if (db==null) {
//			return false;
//		}
//		int num=db.deleteByUserDef("recipeCollectGroup", "nGroupId=? ", new String[]{gid+""});
//		int num1=db.deleteByUserDef("recipeCollectData", "nGroupId=? ", new String[]{gid+""});
//		int num2=db.deleteByUserDef("recipeNameML", "nGroupId=? ", new String[]{gid+""});
//		int num3=db.deleteByUserDef("recipeElemML", "nGroupId=? ",new String[]{gid+""});
//		if(num>0&&num1>0&&num2>0&&num3>0){
//			return true;
//		}else {
//			return false;
//		}
//	}
//	
//	public int getGroupLanguage(int gid){
//		int result=0;
//		String sql="select count(sRecipeName) as num  from  recipeNameML where  nGroupId=? and sRecipeName=?";
//		Cursor cursor=db.getDatabaseBySql(sql, new String[]{gid+""});
//		if (cursor!=null) {
//			while (cursor.moveToNext()) {
//				result=cursor.getInt(cursor.getColumnIndex("num"));
//			}
//		}
//		cursor.close();
//		return result;
//	}
//	
//	/**
//	 * 修改配方
//	 */
//	public boolean updateRecipe(TableTextInfo info,int gid){
//		if (db==null||info==null) {
//			return false;
//		}
//		boolean reuslt=false;
//		if (info.nRow==0) {
//			//修改的第一行，第一行是标题行
//			String sql="update recipeElemML  set sElemName='"+info.sText+
//					"' where  nGroupId="+gid+" and nElemIndex="+info.index+
//					" and nLanguageId="+SystemInfo.getCurrentLanguageId();
//			reuslt=db.execSql(sql);
//		}else if (info.tag.equals("name")) {
//			//配方名称
//			String sql="update recipeNameML set sRecipeName='" +info.sText+
//					"' where nGroupId="+gid+" and nRecipeId="+info.id;
//			reuslt=db.execSql(sql);
//			
//		}else if (info.tag.equals("id")) {
//			//配方ID
//			String sql="update recipeNameML set nRecipeId='" +info.id+
//					"' where nGroupId="+gid+" and nRecipeId="+info.sText
//					+" and nLanguageId="+SystemInfo.getCurrentLanguageId();
//			reuslt=db.execSql(sql);
//		}else if(info.tag.equals("descrip")){
//			//配方描述
//			String sql="update recipeNameML set sRecipeDescri='" +info.sText+
//					"' where nGroupId="+gid+" and nRecipeId="+info.id
//					+" and nLanguageId="+SystemInfo.getCurrentLanguageId();
//			reuslt=db.execSql(sql);
//		}else {
//			String sql="select  nElemIndex from  recipecollectdata " +
//					"where nGroupId=? and nRecipeId=? order by  nElemIndex  limit ?,1";
//			
//			//先获取要修改元素的序号
//			int nElemIndex=0;
//			Cursor cursor=db.getDatabaseBySql(sql, new String[]{gid+"",info.id+"",+info.index+""});
//			if (cursor!=null) {
//				while (cursor.moveToNext()) {
//					nElemIndex=cursor.getInt(cursor.getColumnIndex("nElemIndex"));
//				}
//			}
//			cursor.close();
//			
//			String usql="update recipeCollectData set nRecipeValue=" +info.sText+
//					" where  nGroupId="+gid+" and  nRecipeId="+info.id+" and  nElemIndex="+nElemIndex;
//			reuslt=db.execSql(usql);
//		}
//		
//		return reuslt;
//	}
//	
//	/**
//	 * 修改配方
//	 * @param info-配方对象
//	 * @param gid-配方组id
//	 * @param pid-旧的配方id
//	 */
//	public boolean updateRecipe(recipeOprop info,int gid,int pid){
//		boolean result=true;
//		if (db==null||info==null) {
//			return false;
//		}
//		String sql1="update recipeNameML set nRecipeId='"+info.getnRecipeId()
//				+"' and sRecipeName='"+info.getsRecipeName().get(SystemInfo.getCurrentLanguageId())
//				+"' and sRecipeDescri='"+info.getsRecipeDescri().get(SystemInfo.getCurrentLanguageId())
//				+"' where nGroupId="+gid+" and nRecipeId="+pid
//					+" and nLanguageId="+SystemInfo.getCurrentLanguageId();
//		db.execSql(sql1);
//		
//		Vector<Double> list=info.getnRecipeValueList();
//		if (list!=null) {
//			for (int i = 0; i < list.size(); i++) {
//				String sql2="update recipeCollectData set nRecipeValue=" +list.get(i)+
//						" where  nGroupId="+gid+" and  nRecipeId="+pid+" and  nElemIndex="+i;
//				db.execSql(sql2);
//			}
//		}
//		if (info.getnRecipeId()!=pid) {
//			//如果更新配方id，更新关联的表
//			String sql3="update recipeCollectData set nRecipeId=" +info.getnRecipeId()+
//					" where  nGroupId="+gid+" and  nRecipeId="+pid;
//			db.execSql(sql3);
//		}
//		return result;
//	}
//	
	/**
	 * 获取配方id
	 * @param gid-配方组id
	 */
	public int getRecipeId(int gid){
		if(db==null){
			return 0;
		}
		int id=-1;
		String sql="select max(nRecipeId) as nRecipeId  from  recipeNameML where  nGroupId=?";
		Cursor cursor=db.getDatabaseBySql(sql,new String[]{gid+""});
		if (cursor!=null) {
			while (cursor.moveToNext()) {
				id=cursor.getInt(cursor.getColumnIndex("nRecipeId"));
				id++;
			}
			cursor.close();
		}
		if(id>32767||id==-1){
			id=getAllId(gid);
		}
		return id;
	}
	
	private int getAllId(int gid){
		ArrayList<Integer> mList=new ArrayList<Integer>();
		String sql="select nRecipeId  from  recipeNameML where  nGroupId=? order by nRecipeId";
		Cursor cursor=db.getDatabaseBySql(sql,new String[]{gid+""});
		if (cursor!=null) {
			while (cursor.moveToNext()) {
				int id=cursor.getInt(cursor.getColumnIndex("nRecipeId"));
				mList.add(id);
			}
			cursor.close();
		}
		
		int index=1;
		for (int i = 0; i < mList.size(); i++) {
			if (index!=mList.get(i)) {
				break;
			}else {
				index++;
			}
		}
		return index;
	}
	
	/**
	 * 是否存在配方名称
	 */
	public boolean existRecipeName(String name,int gid){
		if (name==null||name.equals("")||db==null) {
			return false;
		}
		boolean result=false;
		String sql="select count(sRecipeName) as num  from  recipeNameML where  nGroupId=? and sRecipeName=?";
		Cursor cursor=db.getDatabaseBySql(sql, new String[]{gid+"",name});
		if (cursor!=null) {
			while (cursor.moveToNext()) {
				int num=cursor.getInt(cursor.getColumnIndex("num"));
				if (num>0) {
					result=true;
				}
			}
			cursor.close();
		}
		return result;
	}
	
	/**
	 * 是否存在配方名称
	 */
	public boolean existRecipeName(String name,int gid,int id){
		if (name==null||name.equals("")||db==null) {
			return false;
		}
		boolean result=false;
		String sql="select count(sRecipeName) as num  from  recipeNameML where  nGroupId=? and sRecipeName=? and nRecipeId!=?";
		Cursor cursor=db.getDatabaseBySql(sql, new String[]{gid+"",name,id+""});
		if (cursor!=null) {
			while (cursor.moveToNext()) {
				int num=cursor.getInt(cursor.getColumnIndex("num"));
				if (num>0) {
					result=true;
				}
			}
			cursor.close();
		}
		return result;
	}
	
	/**
	 * 是否存在配方id
	 */
	public boolean existRecipeID(String gid,String id){
		if(db==null||gid==null||id==null){
			return false;
		}
		boolean result=false;
		String sql="select count(nRecipeId) as num  from  recipeNameML where  nGroupId=? and nRecipeId=?";
		Cursor cursor=db.getDatabaseBySql(sql, new String[]{gid,id});
		if (cursor!=null) {
			while (cursor.moveToNext()) {
				int num=cursor.getInt(cursor.getColumnIndex("num"));
				if (num>0) {
					result=true;
				}
			}
			cursor.close();
		}
		return result;
	}
	
	/**
	 * 配方元素
	 */
	public boolean existRecipeElement(int gid,String element){
		if(db==null||element==null||element.equals("")){
			return false;
		}
		boolean result=false;
		String sql="select count(sElemName) as num  from  recipeElemML where  nGroupId=0 and sElemName=?";
		Cursor cursor=db.getDatabaseBySql(sql, new String[]{gid+"",element});
		if (cursor!=null) {
			while (cursor.moveToNext()) {
				int num=cursor.getInt(cursor.getColumnIndex("num"));
				if (num>0) {
					result=true;
				}
			}
			cursor.close();
		}
		return result;
	}
	
	/**
	 * 获取配方元素个数
	 */
	public int getRecipeElementCount(int gid){
		if(db==null){
			return 0;
		}
		int num=0;
		String sql="select count(*) as num from  recipeElemML where  nGroupId=? and nLanguageId=?";
		Cursor cursor=db.getDatabaseBySql(sql, new String[]{gid+"",SystemInfo.getCurrentLanguageId()+""});
		if (cursor!=null) {
			while (cursor.moveToNext()) {
				num=cursor.getInt(cursor.getColumnIndex("num"));
			}
			cursor.close();
		}
		
		return num;
	}
	
	/**
	 * 获取配方序号
	 */
	public int getRecipeIndex(){
		int id=0;
		String sql="select max(nRecipeIndex) as nRecipeIndex  from  recipeNameML";
		Cursor cursor=db.getDatabaseBySql(sql, null);
		if (cursor!=null) {
			while (cursor.moveToNext()) {
				id=cursor.getInt(cursor.getColumnIndex("nRecipeIndex"));
				id++;
			}
			cursor.close();
		}
		return id;
	}
//	
//	/**
//	 * 获取配方元素最大id
//	 */
//	public int getElemIndex(int gid){
//		int id=0;
//		String sql="select max(nElemIndex) as nElemIndex  from  recipeCollectData" +
//				" where  nGroupId=? ";
//		Cursor cursor=db.getDatabaseBySql(sql, new String[]{gid+""});
//		if (cursor!=null) {
//			while (cursor.moveToNext()) {
//				id=cursor.getInt(cursor.getColumnIndex("nElemIndex"));
//			}
//		}
//		cursor.close();
//		return id;
//	}
//	
//	/**
//	 * 获取配方地址id
//	 */
//	public ArrayList<Integer> getRecipeAddr(int gid){
//		ArrayList<Integer> list=new ArrayList<Integer>();
//		String sql="select nValueAddrId From recipeCollectData " +
//				"where  nGroupId=? and nRecipeId " +
//				"in(select nRecipeId from recipecollectdata " +
//				"where nGroupId=? limit 1) order by nElemIndex ";
//		Cursor cursor=db.getDatabaseBySql(sql, new String[]{gid+"",gid+""});
//		if (cursor!=null) {
//			while (cursor.moveToNext()) {
//				int aid=cursor.getInt(cursor.getColumnIndex("nValueAddrId"));
//				list.add(aid);
//			}
//		}
//		cursor.close();
//		return list;
//	}
}
