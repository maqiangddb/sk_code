package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;
import java.util.List;

import com.android.Samkoonhmi.model.FlowBlockModel;
import com.android.Samkoonhmi.model.ShowInfo;
import com.android.Samkoonhmi.skenum.Direction;
import com.android.Samkoonhmi.skenum.Direction.DIRECTION;
import com.android.Samkoonhmi.skenum.CSS_TYPE;
import com.android.Samkoonhmi.skenum.IntToEnum;
import com.android.Samkoonhmi.skenum.SPEED;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;

import android.database.Cursor;
import android.util.Log;


public class FlowBlockBiz extends DataBase {
	SKDataBaseInterface db=null;
	Cursor cursor=null;
	
	public FlowBlockBiz(){
		db=SkGlobalData.getProjectDatabase();
		
	}
	
	public ArrayList<FlowBlockModel> getFlowBlock(int sceneId){
		ArrayList<FlowBlockModel> list = new ArrayList<FlowBlockModel>();
		if (db==null) {
			db=SkGlobalData.getProjectDatabase();
		}
		cursor = db.getDatabaseBySql("select * from floawBar where nSceneId=? ", new String[]{sceneId+""});
		if(null!=cursor){
			while(cursor.moveToNext()){
				FlowBlockModel info=new FlowBlockModel();
				info.setbSizeLine(cursor.getString(cursor.getColumnIndex("bSideLine")).equals("true")?true:false);
				info.setbTouchAddress(cursor.getString(cursor.getColumnIndex("bTouchAddress")).equals("true")?true:false);
				SPEED speedTyle=IntToEnum.getFlowSpeed(cursor.getInt(cursor.getColumnIndex("eFixedFlowSpeed")));
				info.seteFixedFlowSpeed(speedTyle);
				//Log.d("flow", "getDirection:"+cursor.getInt(cursor.getColumnIndex("eFlowDirection")));
				DIRECTION eFlowDirection=Direction.getDirection(cursor.getInt(cursor.getColumnIndex("eFlowDirection")));
				//Log.d("flow", "DIRECTION:"+eFlowDirection);
				info.seteFlowDirection(eFlowDirection);
				DIRECTION eShowWay=Direction.getDirection(cursor.getInt(cursor.getColumnIndex("eShowWay")));
				info.seteShowWay(eShowWay);
				SPEED eSpeedType=IntToEnum.getFlowSpeed(cursor.getInt(cursor.getColumnIndex("eFlowSpeedType")));
				info.seteSpeedType(eSpeedType);
				CSS_TYPE eStyle=IntToEnum.getCssType(cursor.getInt(cursor.getColumnIndex("eStyle")));
				info.seteStyle(eStyle);
				info.setId(cursor.getInt(cursor.getColumnIndex("nItemId")));
				info.setnCollidindId(cursor.getInt(cursor.getColumnIndex("nCollidindId")));
				info.setnZvalue(cursor.getInt(cursor.getColumnIndex("nZvalue")));
				info.setnDBackColor(cursor.getInt(cursor.getColumnIndex("nDBackColor")));
				info.setnDForeColor(cursor.getInt(cursor.getColumnIndex("nDForeColor")));
				info.setnFBackColor(cursor.getInt(cursor.getColumnIndex("nFBackColor")));
				info.setnFForeColor(cursor.getInt(cursor.getColumnIndex("nFForeColor")));
				info.setnFlowNum(cursor.getInt(cursor.getColumnIndex("nFlowNum")));
				info.setnTriggerAddress(AddrPropBiz.selectById(cursor.getInt(cursor.getColumnIndex("nTriggerAddress"))));
				info.setnFrameColor(cursor.getInt(cursor.getColumnIndex("nFrameColor")));
				info.setnTouchAddress(AddrPropBiz.selectById(cursor.getInt(cursor.getColumnIndex("nTouchAddress"))));
				info.setnTrendFlowAddress(AddrPropBiz.selectById(cursor.getInt(cursor.getColumnIndex("nTrendFlowSpeed"))));
				info.setnValidState(cursor.getInt(cursor.getColumnIndex("nValidState")));
				info.setRectHeight(cursor.getInt(cursor.getColumnIndex("nRectHeight")));
				info.setRectWidth(cursor.getInt(cursor.getColumnIndex("nRectWidth")));
				info.setStartX(cursor.getInt(cursor.getColumnIndex("nStartX")));
				info.setStartY(cursor.getInt(cursor.getColumnIndex("nStartY")));
				ShowInfo showInfo = TouchShowInfoBiz.getShowInfoById(info.getId());
				info.setShowInfo(showInfo);
				list.add(info);
			}
		}
		close(cursor);
		return list;
	}
}
