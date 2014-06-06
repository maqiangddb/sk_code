package com.android.Samkoonhmi.databaseinterface;

import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;
import com.android.Samkoonhmi.util.AddrProp;
public class AddrPropBiz extends DataBase {

	private static SKDataBaseInterface db = null;

	public AddrPropBiz() {
		
	}

	/**
	 * @param id-地址id
	 * @param type-该地址的控件类型
	 */
	public static AddrProp selectById(int id) {
		if (db == null) {
			db = SkGlobalData.getProjectDatabase();
		}
		return db.getAddrById(id);
	}
}
