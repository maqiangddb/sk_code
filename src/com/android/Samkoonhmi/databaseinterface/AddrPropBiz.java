package com.android.Samkoonhmi.databaseinterface;

import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;
import com.android.Samkoonhmi.util.AddrProp;

public class AddrPropBiz extends DataBase{

	private static SKDataBaseInterface db=null;
	private static AddrProp pro=null;
	
	public AddrPropBiz()
	{
	}
	
	public static AddrProp selectById(int id)
	{
		if (db==null) {
			db=SkGlobalData.getProjectDatabase();
		}
		pro=db.getAddrById(id);
		return pro;
	}
}
