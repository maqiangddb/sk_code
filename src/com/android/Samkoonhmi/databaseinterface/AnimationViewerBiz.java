package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;

import android.database.Cursor;
import android.util.Log;

import com.android.Samkoonhmi.model.AnimationViewerInfo;
import com.android.Samkoonhmi.model.PictureInfo;
import com.android.Samkoonhmi.model.StakeoutInfo;
import com.android.Samkoonhmi.model.TPMoveInfo;
import com.android.Samkoonhmi.model.TextInfo;
import com.android.Samkoonhmi.model.TrackPointInfo;
import com.android.Samkoonhmi.skenum.FLICK_TYPE;
import com.android.Samkoonhmi.skenum.IntToEnum;
import com.android.Samkoonhmi.skenum.TEXT_PIC_ALIGN;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;
import com.android.Samkoonhmi.util.AddrProp;
import com.android.Samkoonhmi.util.TextAttribute;

/**
 * 动画显示器数据库查询
 * 
 * @author 魏 科
 * @date 2012-06-20
 * */
public class AnimationViewerBiz extends DataBase {

	// 数据库句柄
	SKDataBaseInterface mDB = null;

	// 数据表查询语句
	String mSearchTableStr = new String(
			"select * from animation where nSceneId=?");

	// 轨迹点查询语句
	String mSearchTPStr = new String(
			"select * from animationOrbit where nItemId=?");

	// 语言信息查询语句
	String mSearchLanguageStr = new String(
			"select * from itemMutilLanguage where nItemId=? and nStatusId=? and nLanguageId=0");

	// 状态信息查询语句
	String mSearchStateStr = new String(
			"select * from switchStatusProp where nItemId=?");

	// 控制地址查询语句
	String mSearchAddrStr = new String("select * from addr where nAddrId=?");

	public AnimationViewerBiz() {
		// Do nothing
	}

	/**
	 * 从数据库中查找指定控件的数据实体
	 * 
	 * @param itemID
	 *            控件ID
	 * @return 动画显示器数据实体
	 * */
	public ArrayList<AnimationViewerInfo> select(int sid) {

		mDB = SkGlobalData.getProjectDatabase();
		if (null == mDB) {// 获得数据库失败
			Log.e("AnimationViewerBiz", "select: Get database failed!");
			return null;
		}

		ArrayList<AnimationViewerInfo> list=new ArrayList<AnimationViewerInfo>();
		
		StringBuffer id =new StringBuffer();
		boolean init = true;
		Cursor tmpCursor = null;
		tmpCursor = mDB.getDatabaseBySql(mSearchTableStr,new String[] { Integer.toString(sid) });
		if (null == tmpCursor) {// 获取游标失败
			Log.e("AnimationViewerBiz", "select: Get cursor failed!");
			return null;
		}
		
		while (tmpCursor.moveToNext()) {
			AnimationViewerInfo dstAVInfo = new AnimationViewerInfo();

			dstAVInfo.setnItemId(tmpCursor.getInt(tmpCursor.getColumnIndex("nItemId")));
			// 设置层信息
			dstAVInfo.setZvalue(tmpCursor.getShort(tmpCursor.getColumnIndex("nZvalue")));

			dstAVInfo.setCollidindId(tmpCursor.getInt(tmpCursor.getColumnIndex("nCollidindId")));

			dstAVInfo.setAreaOrigXPos(tmpCursor.getShort(tmpCursor.getColumnIndex("nAreaOrigXPos")));
			dstAVInfo.setAreaOrigYPos(tmpCursor.getShort(tmpCursor.getColumnIndex("nAreaOrigYPos")));
			dstAVInfo.setAreaWidth(tmpCursor.getShort(tmpCursor.getColumnIndex("nAreaWidth")));
			dstAVInfo.setAreaHeight(tmpCursor.getShort(tmpCursor.getColumnIndex("nAreaHeight")));			
			dstAVInfo.setBackColor(tmpCursor.getShort(tmpCursor.getColumnIndex("nBackColor"))); // 注意通知修改

			// 初始坐标信息
			dstAVInfo.setLp(tmpCursor.getShort(tmpCursor.getColumnIndex("nLp")));
			dstAVInfo.setTp(tmpCursor.getShort(tmpCursor.getColumnIndex("nTp")));
			dstAVInfo.setWidth(tmpCursor.getShort(tmpCursor.getColumnIndex("nWidth")));
			dstAVInfo.setHeight(tmpCursor.getShort(tmpCursor.getColumnIndex("nHeight")));

			dstAVInfo.setStateTotal(tmpCursor.getShort(tmpCursor.getColumnIndex("nStateTotal"))); // 设置状态总数
			dstAVInfo.setStartState(tmpCursor.getShort(tmpCursor.getColumnIndex("nInitState"))); // 设置起始/默认图片编号
			dstAVInfo.setChangeCondition(tmpCursor.getShort(tmpCursor.getColumnIndex("nChangeCondition")));// 设置状态切换条件 
			
			switch (dstAVInfo.getChangeCondition()) {
			case 0: // 按时间切换
				dstAVInfo.setChangeType(tmpCursor.getShort(tmpCursor.getColumnIndex("nChangeType"))); // 设置图片切换循环类型
				dstAVInfo.setChangeTimeinterval(tmpCursor.getShort(tmpCursor.getColumnIndex("nChangeTimeinterval")));// 设置图片切换间隔
				break;

			case 1: // 按控制地址切换
				dstAVInfo.setSPreSetVList(selectSPreSetVList(dstAVInfo.getnItemId())); // 读取状态预设值列表
				int tmpChangeCtrlAddr = tmpCursor.getInt(tmpCursor.getColumnIndex("mChangeCtrlAddr"));
				AddrProp mTmpAddr = SkGlobalData.getProjectDatabase().getAddrById(tmpChangeCtrlAddr);
				dstAVInfo.setChangeCtrlAddr(mTmpAddr);
				break;
			}// End of switch

			dstAVInfo.setTrackType((short)0);
			dstAVInfo.setTrackType(tmpCursor.getShort(tmpCursor.getColumnIndex("nTrackType"))); // 获得轨迹点类型

			switch (dstAVInfo.getTrackType()) {
			case 0: // 散点轨迹
				dstAVInfo.setMoveCondition(tmpCursor.getShort(tmpCursor.getColumnIndex("nMoveCondition"))); // 获得移动条件
				dstAVInfo.setTrackPointArray(selectTrackPointList(dstAVInfo.getnItemId())); // 读取轨迹点列表
				dstAVInfo.setTrackPointTotal(tmpCursor.getShort(tmpCursor.getColumnIndex("nTrackPointTotal")));
				switch (dstAVInfo.getMoveCondition()) {
				case 0: // 按时间移动
					dstAVInfo.setStartTrackPoint(tmpCursor.getShort(tmpCursor.getColumnIndex("nStartTrackPoint")));
					dstAVInfo.setMoveType(tmpCursor.getShort(tmpCursor.getColumnIndex("nMoveType")));
					dstAVInfo.setMoveTimeInterval(tmpCursor.getShort(tmpCursor.getColumnIndex("nMoveTimeInterval")));
					break;
				case 1: // 按控制地址移动
					dstAVInfo.setTPMoveList(selectTPMoveList(dstAVInfo.getnItemId())); // 读取移动预设值列表
					int tmpMoveCtrlAddr = tmpCursor.getInt(tmpCursor.getColumnIndex("mMoveCtrlAddr"));
					AddrProp mTmpAddr = SkGlobalData.getProjectDatabase().getAddrById(tmpMoveCtrlAddr);
					dstAVInfo.setMoveCtrlAddr(mTmpAddr);
					break;
				}// End of switch(dstAVInfo.getMoveCondition())
				break;

			case 1: // 区域轨迹
				
				int tmpXPosCtrlAddr = tmpCursor.getInt(tmpCursor.getColumnIndex("mXPosCtrlAddr"));
				AddrProp mTmpAddr = SkGlobalData.getProjectDatabase().getAddrById(tmpXPosCtrlAddr);
				dstAVInfo.setXPosCtrlAddr(mTmpAddr);

				int tmpYPosCtrlAddr = tmpCursor.getInt(tmpCursor.getColumnIndex("mYPosCtrlAddr"));
				AddrProp mTmpAddrs = SkGlobalData.getProjectDatabase().getAddrById(tmpYPosCtrlAddr);
				dstAVInfo.setYPosCtrlAddr(mTmpAddrs);

				dstAVInfo.setXMoveStepScale(tmpCursor.getFloat(tmpCursor.getColumnIndex("nXMoveStepScale")));
				dstAVInfo.setYMoveStepScale(tmpCursor.getFloat(tmpCursor.getColumnIndex("nYMoveStepScale")));
				break;
			}// End of switch(dstAVInfo.getTrackType())

			dstAVInfo.setmShowInfo(TouchShowInfoBiz.getShowInfoById(dstAVInfo.getnItemId()));
			
			list.add(dstAVInfo);
			if (init) {
				id.append(" nItemId in(" + dstAVInfo.getnItemId());
				init = false;
			} else {
				id.append("," + dstAVInfo.getnItemId());
			}

		}// End of while(tmpCursor.moveToNext())
		close(tmpCursor);

		id.append(")");
		String sId=id.toString();
		
		if (list.size()>0) {
			if (sId.length()>0) {
				// 读取文本列表
				selectTextInfoList(list,sId);
				// 读取图片列表
				selectPicPathList(list,sId);
			}
		}

		return list;
	}

	/**
	 * 查询状态预设值列表
	 */
	private ArrayList<StakeoutInfo> selectSPreSetVList(int itemID) {

		if (null == mDB) {// 数据库为空
			Log.e("AnimationViewerBiz", "selectSPreSetVList: Database is null!");
			return null;
		}

		Cursor tmpCursor = null;
		tmpCursor = mDB.getDatabaseBySql(mSearchStateStr,
				new String[] { Integer.toString(itemID) });
		if (null == tmpCursor) {// 获取游标失败
			Log.e("AnimationViewerBiz", "selectSPreSetVList: cursor failed!");
			return null;
		}

		ArrayList<StakeoutInfo> list = new ArrayList<StakeoutInfo>();
		while (tmpCursor.moveToNext()) {

			// 新建实例
			StakeoutInfo node = new StakeoutInfo();

			// 获得状态号
			node.setnStatusId(tmpCursor.getShort(tmpCursor.getColumnIndex("nStatusIndex")));

			// 获得轨迹点信息
			node.setnCmpFactor((short) tmpCursor.getDouble(tmpCursor.getColumnIndex("statusValue")));

			// 将实例添加到列表
			list.add(node);
		}// End of while(tmpCursor.moveToNext())
		close(tmpCursor);
		return list;
	}

	/**
	 * 查询图片列表
	 */
	private void selectPicPathList(ArrayList<AnimationViewerInfo> alist,String id) {

		if (null == mDB) {// 数据库为空
			Log.e("AnimationViewerBiz", "selectPicPathList: Database is null!");
		}

		// 图片信息查询语句
		String mSearchPicStr = "select * from switchStatusProp where "+id;
		Cursor tmpCursor = null;
		tmpCursor = mDB.getDatabaseBySql(mSearchPicStr,null);
		if (null == tmpCursor) {// 获取游标失败
			Log.e("AnimationViewerBiz", "selectPicPathList: cursor failed!");
		}

		AnimationViewerInfo info=null;
		int nItemId=-1;
		while (tmpCursor.moveToNext()) {
			
			if (nItemId != tmpCursor.getInt(tmpCursor.getColumnIndex("nItemId"))) {
				nItemId = tmpCursor.getInt(tmpCursor.getColumnIndex("nItemId"));
				for (int i = 0; i < alist.size(); i++) {
					if (alist.get(i).getnItemId() == nItemId) {
						info = alist.get(i);
						ArrayList<PictureInfo> list = new ArrayList<PictureInfo>();
						info.setPicPathArray(list);
						break;
					}
				}

			}
			// 新建实例
			PictureInfo node = new PictureInfo();
			// 获得状态号
			node.setnStatusId(tmpCursor.getShort(tmpCursor
					.getColumnIndex("nStatusIndex")));
			// 获得轨迹点信息
			node.setsPath(tmpCursor.getString(tmpCursor
					.getColumnIndex("sPath")));
			// 将实例添加到列表
			info.getPicPathArray().add(node);
		}// End of while(tmpCursor.moveToNext())
		close(tmpCursor);
	}

	/**
	 * 查询移动预设值列表
	 */
	private ArrayList<TPMoveInfo> selectTPMoveList(int itemID) {

		if (null == mDB) {// 数据库为空
			Log.e("AnimationViewerBiz", "selectTPMoveList: Database is null!");
			return null;
		}

		Cursor tmpCursor = null;
		tmpCursor = mDB.getDatabaseBySql(mSearchTPStr,
				new String[] { Integer.toString(itemID) });
		if (null == tmpCursor) {// 获取游标失败
			Log.e("AnimationViewerBiz", "selectTPMoveList: cursor failed!");
			return null;
		}

		ArrayList<TPMoveInfo> list = new ArrayList<TPMoveInfo>();
		while (tmpCursor.moveToNext()) {
			// 新建实例
			TPMoveInfo node = new TPMoveInfo();
			// 获得状态号
			node.setTPNo(tmpCursor.getShort(tmpCursor
					.getColumnIndex("nOrbitId")));
			// 获得轨迹点信息
			node.setCmpFactor(tmpCursor.getShort(tmpCursor
					.getColumnIndex("nMCmpFactor")));
			// 将实例添加到列表
			list.add(node);
		}// End of while(tmpCursor.moveToNext())
		close(tmpCursor);
		return list;
	}

	/**
	 * 查询轨迹点列表
	 */
	private ArrayList<TrackPointInfo> selectTrackPointList(int itemID) {

		if (null == mDB) {// 数据库为空
			Log.e("AnimationViewerBiz",
					"selectTrackPointList: Database is null!");
			return null;
		}

		Cursor tmpCursor = null;
		tmpCursor = mDB.getDatabaseBySql(mSearchTPStr,
				new String[] { Integer.toString(itemID) });
		if (null == tmpCursor) {// 获取游标失败
			Log.e("AnimationViewerBiz", "selectTrackPointList: cursor failed!");
			return null;
		}

		ArrayList<TrackPointInfo> list = new ArrayList<TrackPointInfo>();
		while (tmpCursor.moveToNext()) {

			// 新建实例
			TrackPointInfo node = new TrackPointInfo();

			// 获得状态号
			node.setID(tmpCursor.getShort(tmpCursor.getColumnIndex("nOrbitId")));

			// 获得轨迹点信息
			node.setXPos(tmpCursor.getShort(tmpCursor.getColumnIndex("nXPos")));
			node.setYPos(tmpCursor.getShort(tmpCursor.getColumnIndex("nYPos")));

			// 将实例添加到列表
			list.add(node);
		}// End of while(tmpCursor.moveToNext())
		close(tmpCursor);
		return list;
	}

	/**
	 * 查询文本信息列表
	 */
	private void selectTextInfoList(ArrayList<AnimationViewerInfo> alist,String id) {

		if (null == mDB) {// 数据库为空
			Log.e("AnimationViewerBiz", "selectTextInfoList: Database is null!");
		}

		Cursor tmpCursor = null;
		// 文本信息查询语句
		String mSearchTextStr ="select * from textProp where "+id;
		tmpCursor = mDB.getDatabaseBySql(mSearchTextStr,null);
		if (null == tmpCursor) {// 获取游标失败
			Log.e("AnimationViewerBiz", "selectTextInfoList: cursor failed!");
		}

		AnimationViewerInfo info=null;
		int nItemId=-1;
		while (tmpCursor.moveToNext()) {

			if (nItemId != tmpCursor.getInt(tmpCursor.getColumnIndex("nItemId"))) {
				nItemId = tmpCursor.getInt(tmpCursor.getColumnIndex("nItemId"));
				for (int i = 0; i < alist.size(); i++) {
					if (alist.get(i).getnItemId() == nItemId) {
						info = alist.get(i);
						ArrayList<TextInfo> list = new ArrayList<TextInfo>();
						info.setTextInfoList(list);
						break;
					}
				}

			}
			// 新建实例
			TextInfo node = new TextInfo();

			// 获得状态号			
			node.setnStatusId( tmpCursor.getShort(tmpCursor.getColumnIndex("nStatusIndex")));
			node.setsFontFamily(tmpCursor.getString(tmpCursor.getColumnIndex("sFont")));
			node.setnSize(tmpCursor.getInt(tmpCursor.getColumnIndex("nSize")));
			node.setnColor(tmpCursor.getInt(tmpCursor.getColumnIndex("nColor")));			
			node.setnLangugeId(tmpCursor.getShort(tmpCursor.getColumnIndex("nLangIndex")));
			node.setsText(tmpCursor.getString(tmpCursor.getColumnIndex("sText")));
			node.setnStyle(tmpCursor.getShort(tmpCursor.getColumnIndex("nShowProp")));

			//配置对齐属性
			short style = node.getnStyle();
			if(TextAttribute.CENTER == (style&TextAttribute.CENTER)){
				node.seteAlign(TEXT_PIC_ALIGN.CENTER);
			}else if (TextAttribute.LEFT == (style&TextAttribute.LEFT)){
				node.seteAlign(TEXT_PIC_ALIGN.LEFT);
			}else if(TextAttribute.RIGHT == (style&TextAttribute.RIGHT)){
				node.seteAlign(TEXT_PIC_ALIGN.RIGHT);
			}
			
			node.seteFlickType(FLICK_TYPE.NO_FLICK);

			//			node.setnColor(tmpCursor.getShort(tmpCursor
			//					.getColumnIndex("nTextColor")));
			//			node.setnBColor(tmpCursor.getShort(tmpCursor
			//					.getColumnIndex("nTextBackColor")));
			//			node.setnStyle(tmpCursor.getShort(tmpCursor
			//					.getColumnIndex("nTextStyle")));
			//			node.seteAlign(IntToEnum.getTextPicAlign(tmpCursor
			//					.getShort(tmpCursor.getColumnIndex("nTextAlign"))));
			//			node.setnSpace(tmpCursor.getShort(tmpCursor.getColumnIndex("nFontSpace")));
			//			node.setnStatusId(tmpCursor.getShort(tmpCursor.getColumnIndex("nStatusId")));

			info.getTextInfoList().add(node);
		}// End of while(tmpCursor.moveToNext())
		close(tmpCursor);

		//		TextInfo tmpNode = null;
		//		Cursor tmpCursor2 = null;
		//
		//		for (int i = 0; i < list.size(); i++) {
		//			tmpNode = list.get(i);
		//			tmpCursor2 = mDB.getDatabaseBySql(mSearchLanguageStr,
		//					new String[] { Integer.toString(itemID),
		//							Integer.toString(tmpNode.getnStatusId()) });
		//			if (null == tmpCursor2) {// 获取游标失败
		//				Log.e("AnimationViewerBiz",
		//						"selectTextInfoList: Language cursor failed!");
		//				return null;
		//			}
		//			while (tmpCursor2.moveToNext()) {
		////				tmpNode.setsText(tmpCursor2.getString(tmpCursor2
		////						.getColumnIndex("sText")));
		////				tmpNode.setnSize(tmpCursor2.getShort(tmpCursor2
		////						.getColumnIndex("nFontSize")));
		////				tmpNode.setsFontFamily(tmpCursor2.getString(tmpCursor2
		////						.getColumnIndex("sFontType")));
		//			}
		//		}// End of: for(int i = 0; i<list.size(); i++)
		//		close(tmpCursor2);
	}

}// End of class
