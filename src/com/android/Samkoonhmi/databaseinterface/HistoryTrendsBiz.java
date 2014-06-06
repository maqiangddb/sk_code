package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;
import java.util.List;
import android.database.Cursor;
import android.util.Log;
import com.android.Samkoonhmi.model.sk_historytrends.ChannelGroupInfo;
import com.android.Samkoonhmi.model.sk_historytrends.HistoryTrendsInfo;
import com.android.Samkoonhmi.skenum.HISTORYSHOW_TYPE;
import com.android.Samkoonhmi.skenum.TIMERANGE_TYPE;
import com.android.Samkoonhmi.skenum.CURVE_TYPE;
import com.android.Samkoonhmi.skenum.IntToEnum;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;

/**
 * 曲线数据读取
 * @author 李镇
 * @version v 1.0.0.1
 */
public class HistoryTrendsBiz extends DataBase {

	SKDataBaseInterface db = null;
	public HistoryTrendsBiz() {

	}

	/**
	 * 查询曲线信息
	 */
	public ArrayList<HistoryTrendsInfo> select(int sid) {

		ArrayList<HistoryTrendsInfo> list = new ArrayList<HistoryTrendsInfo>();
		String id = "";
		boolean init = true;

		db = SkGlobalData.getProjectDatabase();
		short nChannelNum;
		String sql = "select * from trends where nSceneId=" + sid;
		Cursor cursor = db.getDatabaseBySql(sql, null);
		if (null == cursor) {
			Log.e("HistoryTrendsBiz", "trends: cursor failed!");
			return list;
		}

		if (cursor != null) {
			while (cursor.moveToNext()) {
				HistoryTrendsInfo mTrendsInfo = new HistoryTrendsInfo();
				mTrendsInfo.setId(cursor.getInt(cursor.getColumnIndex("nItemId")));
				mTrendsInfo.setnLp((short) cursor.getInt(cursor.getColumnIndex("nTopLeftX")));
				mTrendsInfo.setnTp((short) cursor.getInt(cursor.getColumnIndex("nTopLeftY")));
				mTrendsInfo.setnWidth((short) cursor.getInt(cursor.getColumnIndex("nWidth")));
				mTrendsInfo.setnHeight((short) cursor.getInt(cursor.getColumnIndex("nHeight")));
				mTrendsInfo.setnCurveX((short) cursor.getInt(cursor.getColumnIndex("nCurveX")));
				mTrendsInfo.setnCurveY((short) cursor.getInt(cursor.getColumnIndex("nCurveY")));
				mTrendsInfo.setnCurveWd((short) cursor.getInt(cursor.getColumnIndex("nCurveWd")));
				mTrendsInfo.setnCurveHt((short) cursor.getInt(cursor.getColumnIndex("nCurveHt")));
				mTrendsInfo.setnCurveType(getCurve_type(cursor.getInt(cursor.getColumnIndex("nCurveType"))));
				mTrendsInfo.setnGroupNum((short) cursor.getInt(cursor.getColumnIndex("nGroupNum")));
				nChannelNum = (short) cursor.getInt(cursor.getColumnIndex("nChannelNum"));
				mTrendsInfo.setnChannelNum(nChannelNum);
				mTrendsInfo.setnDisplayMin(cursor.getFloat(cursor.getColumnIndex("nDisplayMin")));
				mTrendsInfo.setnDisplayMax(cursor.getFloat(cursor.getColumnIndex("nDisplayMax")));
				mTrendsInfo.setnCurveAlpha((short) cursor.getInt(cursor.getColumnIndex("nCurveAlpha")));

				mTrendsInfo.setnDataSample((short) cursor.getInt(cursor.getColumnIndex("nDataSample")));
				mTrendsInfo.setnScrollSample((short) cursor.getInt(cursor.getColumnIndex("nScrollSample")));
				mTrendsInfo.setnVertMajorScale((short) cursor.getInt(cursor.getColumnIndex("nVertMajorScale")));
				if (null != cursor.getString(cursor
						.getColumnIndex("bSelectVertMinor"))) {
					boolean b = cursor.getString(
							cursor.getColumnIndex("bSelectVertMinor")).equals(
							"true") ? true : false;
					mTrendsInfo.setbSelectVertMinor(b);
				}
				mTrendsInfo.setnVertMinorScale((short) cursor.getInt(cursor.getColumnIndex("nVertMinorScale")));
				mTrendsInfo.setnHorMajorScale((short) cursor.getInt(cursor.getColumnIndex("nHorMajorScale")));
				if (null != cursor.getString(cursor
						.getColumnIndex("bSelectHorMinor"))) {
					boolean bb = cursor.getString(
							cursor.getColumnIndex("bSelectHorMinor")).equals(
							"true") ? true : false;
					mTrendsInfo.setbSelectHorMinor(bb);
				}
				mTrendsInfo.setnHorMinorScale((short) cursor.getInt(cursor.getColumnIndex("nHorMinorScale")));

				mTrendsInfo.setnBoradColor(cursor.getInt(cursor.getColumnIndex("nBoradColor")));
				mTrendsInfo.setnScaleColor(cursor.getInt(cursor.getColumnIndex("nScaleColor")));
				mTrendsInfo.setnGraphColor(cursor.getInt(cursor.getColumnIndex("nGraphColor")));
				if (null != cursor.getString(cursor
						.getColumnIndex("bSelectNet"))) {
					boolean bbb = cursor.getString(
							cursor.getColumnIndex("bSelectNet")).equals("true") ? true: false;
					mTrendsInfo.setbSelectNet(bbb);
				}

				mTrendsInfo.setnVertNetColor(cursor.getInt(cursor.getColumnIndex("nVertNetColor")));
				mTrendsInfo.setnHorNetColor(cursor.getInt(cursor.getColumnIndex("nHorNetColor")));
				mTrendsInfo.setnRecentYear((short) cursor.getInt(cursor.getColumnIndex("nRecentYear")));
				mTrendsInfo.setnRecentMonth((short) cursor.getInt(cursor.getColumnIndex("nRecentMonth")));
				mTrendsInfo.setnRecentDay((short) cursor.getInt(cursor.getColumnIndex("nRecentDay")));
				mTrendsInfo.setnRecentHour((short) cursor.getInt(cursor.getColumnIndex("nRecentHour")));
				mTrendsInfo.setnRecentMinute((short) cursor.getInt(cursor.getColumnIndex("nRecentMinute")));
				int minute = ((mTrendsInfo.getnRecentMonth() * 30 + mTrendsInfo
						.getnRecentDay()) * 24 + mTrendsInfo.getnRecentHour()) * 60;
				mTrendsInfo.setnRecentMinute((short) (mTrendsInfo.getnRecentMinute() + minute));
				mTrendsInfo.setnStartYear((short) cursor.getInt(cursor.getColumnIndex("nStartYear")));
				mTrendsInfo.setnStartMonth((short) cursor.getInt(cursor.getColumnIndex("nStartMonth")));
				mTrendsInfo.setnStartDay((short) cursor.getInt(cursor.getColumnIndex("nStartDay")));
				mTrendsInfo.setnStartHour((short) cursor.getInt(cursor.getColumnIndex("nStartHour")));
				mTrendsInfo.setnStartMinute((short) cursor.getInt(cursor.getColumnIndex("nStartMinute")));
				mTrendsInfo.setnEndYear((short) cursor.getInt(cursor.getColumnIndex("nEndYear")));
				mTrendsInfo.setnEndMonth((short) cursor.getInt(cursor.getColumnIndex("nEndMonth")));
				mTrendsInfo.setnEndDay((short) cursor.getInt(cursor.getColumnIndex("nEndDay")));
				mTrendsInfo.setnEndHour((short) cursor.getInt(cursor.getColumnIndex("nEndHour")));
				mTrendsInfo.setnEndMinute((short) cursor.getInt(cursor.getColumnIndex("nEndMinute")));
				mTrendsInfo.setsFontType(cursor.getString(cursor.getColumnIndex("sFontType")));
				mTrendsInfo.setnFontSize((short) cursor.getInt(cursor.getColumnIndex("nFontSize")));
				int nDate = cursor.getInt(cursor.getColumnIndex("nDate"));
				mTrendsInfo.setnDate(IntToEnum.getDateType(nDate));
				int nTime = cursor.getInt(cursor.getColumnIndex("nTime"));
				mTrendsInfo.setnTime(IntToEnum.getTimeType(nTime));
				mTrendsInfo.setnMarkColor(cursor.getInt(cursor.getColumnIndex("nMarkColor")));
				if (null != cursor.getString(cursor.getColumnIndex("bXmark"))) {
					boolean bbbb = cursor.getString(
							cursor.getColumnIndex("bXmark")).equals("true") ? true: false;
					mTrendsInfo.setbXmark(bbbb);
				}
				int nTimeRange = cursor.getInt(cursor.getColumnIndex("nTimeRange"));
				mTrendsInfo.setnTimeRange(getTimerange_type(nTimeRange));
				mTrendsInfo.setnOldTimerange(getTimerange_type(nTimeRange));
				mTrendsInfo.setnZvalue(cursor.getShort(cursor.getColumnIndex("nZvalue")));
				mTrendsInfo.setnCollidindId(cursor.getShort(cursor.getColumnIndex("nCollidindId")));
				mTrendsInfo.setmShowInfo(TouchShowInfoBiz.getShowInfoById(mTrendsInfo.getId()));

				if (null != cursor.getString(cursor.getColumnIndex("bMainVer"))) {
					boolean a = cursor.getString(
							cursor.getColumnIndex("bMainVer")).equals("true") ? true: false;
					mTrendsInfo.setbMainVer(a);
				}
				if (null != cursor.getString(cursor.getColumnIndex("bMainHor"))) {
					boolean a = cursor.getString(
							cursor.getColumnIndex("bMainHor")).equals("true") ? true: false;
					mTrendsInfo.setbMainHor(a);
				}

				mTrendsInfo.setmFromAddr(AddrPropBiz.selectById(cursor.getShort(cursor.getColumnIndex("mFromAddr"))));
				mTrendsInfo.setmToAddr(AddrPropBiz.selectById(cursor.getShort(cursor.getColumnIndex("mToAddr"))));
				
				if (null != cursor.getString(cursor.getColumnIndex("bDate"))) {
					boolean a = cursor.getString(cursor.getColumnIndex("bDate")).equals(
									"true") ? true : false;
					mTrendsInfo.setbShowData(a);
				}
				if (null != cursor.getString(cursor.getColumnIndex("bTime"))) {
					boolean a = cursor
							.getString(cursor.getColumnIndex("bTime")).equals(
									"true") ? true : false;
					mTrendsInfo.setbShowTime(a);
				}

				if (init) {
					id += " nItemId=" + mTrendsInfo.getId();
					init = false;
				} else {
					id += " or nItemId=" + mTrendsInfo.getId();
				}
				list.add(mTrendsInfo);
			}
			// cursor.close();
			close(cursor);
		}

		if (list.size() > 0) {
			Cursor result = db.getDatabaseBySql(
					"select * from trendsChannelSet where " + id, null);
			if (null == result) {
				Log.e("HistoryTrendsBiz", "trendsChannelSet: cursor failed!");
				return null;
			}

			int nItemId = -1;
			HistoryTrendsInfo mTrendsInfo = null;

			/* 取记录条数放到容器中 */
			if (result != null) {
				while (result.moveToNext()) {
					if (nItemId != result.getInt(result
							.getColumnIndex("nItemId"))) {
						nItemId = result.getInt(result
								.getColumnIndex("nItemId"));
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getId() == nItemId) {
								mTrendsInfo = list.get(i);
								List<ChannelGroupInfo> channelGroups = new ArrayList<ChannelGroupInfo>();
								mTrendsInfo.setchannelGroups(channelGroups);
								break;
							}
						}

					}

					ChannelGroupInfo Channel = new ChannelGroupInfo();
					short nNumOfChannel;
					nNumOfChannel = (short) result.getInt(result.getColumnIndex("nNumOfChannel"));
					Channel.setnNumOfChannel(nNumOfChannel);
					int nDisplayCondition = result.getInt(result.getColumnIndex("nDisplayCondition"));
					Channel.setnDisplayCondition(getHistoryshow_type(nDisplayCondition));
					Channel.setnDisplayAddr(AddrPropBiz.selectById(result.getShort(result.getColumnIndex("nDisplayAddr"))));
					int nLineType = result.getInt(result.getColumnIndex("nLineType"));
					Channel.setnLineType(IntToEnum.getLineType(nLineType + 1));
					Channel.setnLineThickness((short) result.getInt(result.getColumnIndex("nLineThickness")));
					Channel.setnDisplayColor(result.getInt(result.getColumnIndex("nDisplayColor")));
					mTrendsInfo.getchannelGroups().add(Channel);
				}
				// result.close();
				close(result);
			}

		}

		Log.d("HistoryTrends", "select id:" + id);
		return list;
	}

	/**
	 * 曲线类型
	 */
	public CURVE_TYPE getCurve_type(int num) {
		switch (num) {
		case 1:
			return CURVE_TYPE.REALTIME_CURVE;
		case 2:
			return CURVE_TYPE.HISTORY_CURVE;
		case 3:
			return CURVE_TYPE.DATAGROUP_CURVE;

		}
		return CURVE_TYPE.REALTIME_CURVE;
	}

	/**
	 * 时间选择类型
	 */
	public TIMERANGE_TYPE getTimerange_type(int num) {
		switch (num) {
		case 1:
			return TIMERANGE_TYPE.RECENT_BEGIN;
		case 2:
			return TIMERANGE_TYPE.RANGE_BEGIN;
		case 3:
			return TIMERANGE_TYPE.STORE_BEGIN;
		case 4:
			return TIMERANGE_TYPE.ADDR_BEGIN;
		}
		return TIMERANGE_TYPE.RECENT_BEGIN;
	}

	/**
	 * 历史曲线显示类型
	 */
	public HISTORYSHOW_TYPE getHistoryshow_type(int num) {
		switch (num) {
		case 1:
			return HISTORYSHOW_TYPE.ALWAYS_SHOW;
		case 2:
			return HISTORYSHOW_TYPE.ON_SHOW;
		case 3:
			return HISTORYSHOW_TYPE.OFF_SHOW;
		}
		return HISTORYSHOW_TYPE.ALWAYS_SHOW;
	}
}
