package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;

import android.database.Cursor;

import com.android.Samkoonhmi.model.ExpressModel;
import com.android.Samkoonhmi.skenum.EXPRESS_NUM_TYPE;
import com.android.Samkoonhmi.skenum.EXPRESS_SIGN;
import com.android.Samkoonhmi.skenum.IntToEnum;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;

/**
 * 表达式内容查找
 * @author Administrator
 *
 */
public class ExpressBiz extends DataBase{
	private SKDataBaseInterface db;

	public ExpressBiz() {
		db = SkGlobalData.getProjectDatabase();
	}
	
	public ArrayList<ExpressModel> getExpressInfo(int id){
		ArrayList<ExpressModel> list=new ArrayList<ExpressModel>();
		ExpressModel ex1 = new ExpressModel();
		ExpressModel ex2 = new ExpressModel();
		ExpressModel ex3 = new ExpressModel();
		
		String sql = "select * from express where nItemId = "+id;
		if (null == db) {
			db = SkGlobalData.getProjectDatabase();
		}
		Cursor cursor = db.getDatabaseBySql(sql, null);
		if(null != cursor){
			while (cursor.moveToNext()) {
				
				//表达式第一个数
				short nFirstSignId = cursor.getShort(cursor.getColumnIndex("nFirstSign"));
				EXPRESS_SIGN firstSign = IntToEnum.getExpressSign(nFirstSignId);
				if(firstSign!=EXPRESS_SIGN.NONE){
					ex1.setId(cursor.getInt(cursor.getColumnIndex("id")));
					ex1.setnItemId(cursor.getInt(cursor.getColumnIndex("nItemId")));
					ex1.setnSign(firstSign);
					short nFirstNumberType = cursor.getShort(cursor.getColumnIndex("nFirstNumberType"));
					EXPRESS_NUM_TYPE firstType = IntToEnum.getExpType(nFirstNumberType);
					ex1.seteType(firstType);
					if(firstType == EXPRESS_NUM_TYPE.CONSTANT){
						//第一个数为常数
						ex1.setnVaule(cursor.getDouble(cursor.getColumnIndex("nFirstNumber")));
					}else{
						int nAddrId=(int) cursor.getDouble(cursor.getColumnIndex("nFirstNumber"));
						if (nAddrId>-1) {
							ex1.setmAddProp(AddrPropBiz.selectById(nAddrId));
						}	
					}
					list.add(ex1);
				}
				
				
				//表达式第二个数
				short nSecondSignId = cursor.getShort(cursor.getColumnIndex("nSecondSign"));
				EXPRESS_SIGN nSecondSign = IntToEnum.getExpressSign(nSecondSignId);
				if(nSecondSign != EXPRESS_SIGN.NONE){
					ex2.setnSign(nSecondSign);
					short nSecondNumberType = cursor.getShort(cursor.getColumnIndex("nSecondNumberType"));
					EXPRESS_NUM_TYPE secondType = IntToEnum.getExpType(nSecondNumberType);
					ex2.seteType(secondType);
					if(secondType == EXPRESS_NUM_TYPE.CONSTANT){
						//第二个数为常数
						ex2.setnVaule(cursor.getDouble(cursor.getColumnIndex("nSecondNumber")));
					}else{
						int nAddrId=(int) cursor.getDouble(cursor.getColumnIndex("nSecondNumber"));
						if (nAddrId>-1) {
							ex2.setmAddProp(AddrPropBiz.selectById(nAddrId));
						}	
						
					}
					list.add(ex2);
				}
				
				//表达式第三个数
				short nThirdSignId = cursor.getShort(cursor.getColumnIndex("nThirdSign"));
				EXPRESS_SIGN nThirdSign = IntToEnum.getExpressSign(nThirdSignId);
				if(nThirdSign != EXPRESS_SIGN.NONE){
					ex3.setnSign(nThirdSign);
					short nThirdNumberType = cursor.getShort(cursor.getColumnIndex("nThirdNumberType"));
					EXPRESS_NUM_TYPE thirdType = IntToEnum.getExpType(nThirdNumberType);
					ex3.seteType(thirdType);
					if(thirdType == EXPRESS_NUM_TYPE.CONSTANT){
						//第三个数为常量
						ex3.setnVaule(cursor.getDouble(cursor.getColumnIndex("nThirdNumber")));
					}else{
						int nAddrId=(int) cursor.getDouble(cursor.getColumnIndex("nThirdNumber"));
						if (nAddrId>-1) {
							ex3.setmAddProp(AddrPropBiz.selectById(nAddrId));
						}	
					}
					list.add(ex3);
				}
			}
		}
		close(cursor);
		return list;
	}

}
