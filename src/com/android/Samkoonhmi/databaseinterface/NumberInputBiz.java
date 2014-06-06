package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;

import android.database.Cursor;

import com.android.Samkoonhmi.model.ExpressModel;
import com.android.Samkoonhmi.model.NumberDisplayInfo;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.INPUT_TYPE;
import com.android.Samkoonhmi.skenum.IntToEnum;
import com.android.Samkoonhmi.skenum.SHOWAREA;
import com.android.Samkoonhmi.skenum.TEXT_PIC_ALIGN;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.SKKeyPopupWindow;
import com.android.Samkoonhmi.util.AddrProp;

/***
 * 数值输入显示器
 * 
 * @author Administrator
 * 
 */
public class NumberInputBiz extends DataBase {
	private SKDataBaseInterface db;

	public NumberInputBiz() {

	}

	/**
	 * 查找数值输入显示对象
	 * @return
	 */
	public ArrayList<NumberDisplayInfo> selectNumberDisplayInfo(int sid) {
		ArrayList<NumberDisplayInfo> list = new ArrayList<NumberDisplayInfo>();
		StringBuffer id = new StringBuffer();
		boolean init = true;
		if (null == db) {
			db = SkGlobalData.getProjectDatabase();
		}
		Cursor cursor = db.getDatabaseBySql(
				"select * from dataShow where eItemType=1 and nSceneId=" + sid,
				null);
		if (null != cursor) {
			while (cursor.moveToNext()) {
				NumberDisplayInfo info = new NumberDisplayInfo();
				info.seteFontCss(cursor.getShort(cursor
						.getColumnIndex("eFontCss")));
				info.setId(cursor.getInt(cursor.getColumnIndex("nItemId")));
				info.setnFontSize(cursor.getInt(cursor
						.getColumnIndex("nFontSize")));
				info.setsFontType(cursor.getString(cursor
						.getColumnIndex("sFontStyle")));
				info.setnHeight(cursor.getInt(cursor.getColumnIndex("nHeight")));
				info.setsShapId(cursor.getString(cursor
						.getColumnIndex("sShapId")));
				// info.setnShowPropId(cursor.getInt(cursor
				// .getColumnIndex("nShowPropId")));
				info.setnStartX(cursor.getInt(cursor.getColumnIndex("nStartX")));
				info.setnStartY(cursor.getInt(cursor.getColumnIndex("nStartY")));
				info.setnTextHeight(cursor.getInt(cursor
						.getColumnIndex("nTextHeight")));
				info.setnTextStartX(cursor.getInt(cursor
						.getColumnIndex("nTextStartX")));
				info.setnTextStartY(cursor.getInt(cursor
						.getColumnIndex("nTextStartY")));
				info.setnTextWidth(cursor.getInt(cursor
						.getColumnIndex("nTextWidth")));
				// info.setnTouchPropId(cursor.getInt(cursor
				// .getColumnIndex("nTouchPropId")));
				info.setnWidth(cursor.getInt(cursor.getColumnIndex("nWidth")));
				info.setnZvalue(cursor.getInt(cursor.getColumnIndex("nZvalue")));
				info.setnCollidindId(cursor.getInt(cursor
						.getColumnIndex("nCollidindId")));
				info.setbIsStartStatement(cursor.getString(
						cursor.getColumnIndex("bIsStartStatement")).equals(
						"true") ? true : false);
				info.setnScriptId(cursor.getInt(cursor
						.getColumnIndex("nScriptId")));
				info.setnTransparent(cursor.getInt(cursor
						.getColumnIndex("nTransparent")));
				
				int nAddrId=cursor.getInt(cursor.getColumnIndex("nOffsetAddrID"));
				if (nAddrId>-1) {
					info.setmOffSetAddr(AddrPropBiz.selectById(nAddrId));
				}	
				
				list.add(info);
				if (init) {
					id.append(" nItemId in(" + info.getId());
					init = false;
				} else {
					id.append("," + info.getId());
				}
			}
		}
		close(cursor);
		
		id.append(")");
		String sId=id.toString();
		NumberDisplayInfo info = null;
		int nItemId = -1;
		cursor = db.getDatabaseBySql("select * from  number where " + sId, null);
		int index=0;
		
		if (null != cursor) {
			while (cursor.moveToNext()) {
				if (nItemId != cursor.getInt(cursor.getColumnIndex("nItemId"))) {
					nItemId = cursor.getInt(cursor.getColumnIndex("nItemId"));
					if (list.get(index).getId()==nItemId) {
						info = list.get(index);
					}else {
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getId() == nItemId) {
								info = list.get(i);
								break;
							}
						}

					}
					index++;
				}
				info.setnAddress(AddrPropBiz.selectById(cursor.getInt(cursor
						.getColumnIndex("nAddress"))));
				
				if (null != cursor.getString(cursor.getColumnIndex("bIsInput"))) {
					info.setbIsInput(cursor.getString(
							cursor.getColumnIndex("bIsInput")).equals("true") ? true
							: false);// cursor.getColumnIndex("bIsInput"))>0);
				}
				if (null != cursor.getString(cursor.getColumnIndex("bIsScale"))) {
					info.setbIsScale(cursor.getString(
							cursor.getColumnIndex("bIsScale")).equals("true") ? true
							: false);
				}
				DATA_TYPE eNumberType = IntToEnum.getDataType(cursor
						.getInt(cursor.getColumnIndex("eNumberType")));
				info.seteNumberType(eNumberType);
				info.setnByteLength(cursor.getInt(cursor
						.getColumnIndex("nByteLength")));
				SHOWAREA eSourceArea = IntToEnum.getShowArea(cursor
						.getInt(cursor.getColumnIndex("eSourceArea")));
				info.seteSourceArea(eSourceArea);
				info.setnSourceMax(cursor.getDouble(cursor
						.getColumnIndex("nSourceMax")));
				info.setnSourceMin(cursor.getDouble(cursor
						.getColumnIndex("nSourceMin")));
				SHOWAREA nShow = IntToEnum.getShowArea(cursor.getInt(cursor
						.getColumnIndex("nShow")));
				info.setnShow(nShow);
				if (null != cursor.getString(cursor.getColumnIndex("bRound"))) {
					info.setbRound(cursor.getString(
							cursor.getColumnIndex("bRound")).equals("true") ? true
							: false);
				}
				info.setnShowMax(cursor.getDouble(cursor
						.getColumnIndex("nShowMax")));
				info.setnShowMin(cursor.getDouble(cursor
						.getColumnIndex("nShowMin")));
				info.setnAllbytelength(cursor.getInt(cursor
						.getColumnIndex("nAllbytelength")));
				SHOWAREA eDecimalType = IntToEnum.getShowArea(cursor
						.getInt(cursor.getColumnIndex("eDecimalType")));
				info.seteDecimalType(eDecimalType);
				info.setnDecimalLength(cursor.getInt(cursor
						.getColumnIndex("nDecimalLength")));
				TEXT_PIC_ALIGN eShowStyle = IntToEnum.getTextPicAlign(cursor
						.getInt(cursor.getColumnIndex("eShowStyle")));
				info.seteShowStyle(eShowStyle);
				info.setnFontColor(cursor.getInt(cursor
						.getColumnIndex("nFontColor")));
				info.setnBackColor(cursor.getInt(cursor
						.getColumnIndex("nBackColor")));
				info.setnHightColor(cursor.getInt(cursor
						.getColumnIndex("nHightColor")));
				info.setnLowerColor(cursor.getInt(cursor
						.getColumnIndex("nLowerColor")));
				INPUT_TYPE eInputTypeId = IntToEnum.getInputType(cursor
						.getInt(cursor.getColumnIndex("eInputTypeId")));
				info.seteInputTypeId(eInputTypeId);
				info.setnKeyId(cursor.getInt(cursor.getColumnIndex("nKeyId")));
				if (info.getnKeyId()!=-1) {
					boolean reulst=SKKeyPopupWindow.existKeyBroad(info.getnKeyId());
					if (!reulst) {
						//如果自定义键盘不存在，则调用系统键盘
						info.setnKeyId(-1);
					}
				}
				if (info.geteInputTypeId() == INPUT_TYPE.BIT) {
					info.setsBitAddress(AddrPropBiz.selectById(cursor
							.getInt(cursor.getColumnIndex("sBitAddress"))));
				}
				info.setnLowerNumber(cursor.getDouble(cursor
						.getColumnIndex("nLowerNumber")));
				info.setnHightNumber(cursor.getDouble(cursor
						.getColumnIndex("nHightNumber")));
				info.setShowInfo(TouchShowInfoBiz.getShowInfoById(info.getId()));
				info.setTouchInfo(TouchShowInfoBiz.getTouchInfoById(info
						.getId()));
				if (SHOWAREA.ADDRESS == info.geteSourceArea()) {
					info.setSourceMaxAddrProp(AddrPropBiz.selectById((int) info
							.getnSourceMax()));
					info.setSourceMinAddrProp(AddrPropBiz.selectById((int) info
							.getnSourceMin()));
				}
				if (SHOWAREA.ADDRESS == info.getnShow()) {
					info.setShowMaxAddrProp(AddrPropBiz.selectById((int) info
							.getnShowMax()));
					info.setShowMinAddrProp(AddrPropBiz.selectById((int) info
							.getnShowMin()));
				}
				if (info.geteDecimalType() == SHOWAREA.ADDRESS) {
					info.setDecimaNumberAddrProp(AddrPropBiz.selectById(info
							.getnDecimalLength()));
				}
				if (null != cursor.getString(cursor
						.getColumnIndex("bInputSign"))) {
					info.setbInputSign(cursor.getString(
							cursor.getColumnIndex("bInputSign")).equals("true") ? true
							: false);
				}
				info.setnBoardX(cursor.getInt((cursor.getColumnIndex("nBoardX"))));
				info.setnBoardY(cursor.getInt(cursor.getColumnIndex("nBoardY")));
				SHOWAREA eInputArea = IntToEnum.getShowArea(cursor
						.getInt(cursor.getColumnIndex("eInputAreaType")));
				info.seteInputAreaType(eInputArea);
				if (eInputArea == SHOWAREA.ADDRESS) {
					info.setInputMaxAddr(AddrPropBiz.selectById(cursor
							.getInt(cursor.getColumnIndex("nInputMax"))));
					info.setInputMinAddr(AddrPropBiz.selectById(cursor
							.getInt(cursor.getColumnIndex("nInputMin"))));
				} else {
					info.setnInputMax(cursor.getDouble(cursor
							.getColumnIndex("nInputMax")));
					info.setnInputMin(cursor.getDouble(cursor
							.getColumnIndex("nInputMin")));
				}
				if (null != cursor.getString(cursor
						.getColumnIndex("bAutoChangeBit"))) {
					info.setbAutoChangeBit(cursor.getString(
							cursor.getColumnIndex("bAutoChangeBit")).equals(
							"true") ? true : false);
				}
				boolean inputIsShow = false;
				if(null != cursor.getString(cursor.getColumnIndex("bInputIsShow"))){
					 inputIsShow = cursor.getString(cursor.getColumnIndex("bInputIsShow")).equals("true") ? true
							: false;
					info.setInputIsShow(inputIsShow);
				}
				//输入地址跟显示地址不一致
                if(!inputIsShow)
                {
            		int nInputAddrId=cursor.getInt(cursor.getColumnIndex("nInputAddr"));
    				if (nInputAddrId>-1) {
    					info.setInputAddr(AddrPropBiz.selectById(nInputAddrId));
    				}	
                }
                //显示表达式
                if(null!=cursor.getString(cursor.getColumnIndex("bShowExp"))){
                	info.setbShowExp(cursor.getString(cursor.getColumnIndex("bShowExp")).equals("true")?true:false);
                }
                if(info.isbShowExp()){
                	//如果选择了显示表达式
                	int nExpId = cursor.getInt(cursor.getColumnIndex("nShowExpId"));
                	ArrayList<ExpressModel> expInfo = DBTool.getInstance().getmExpressBiz().getExpressInfo(nExpId);
                	info.setShowExpModel(expInfo);
                }
                
                //输入表达式
                if(null!=cursor.getString(cursor.getColumnIndex("bInputExp"))){
                	info.setbInputExp(cursor.getString(cursor.getColumnIndex("bInputExp")).equals("true")?true:false);
                }
                if(info.isInputIsShow()){
                	//如果选择了输入
                	int nExpId = cursor.getInt(cursor.getColumnIndex("nInputExpId"));
                	ArrayList<ExpressModel> expInfo = DBTool.getInstance().getmExpressBiz().getExpressInfo(nExpId);
                	info.setInputExpModel(expInfo);
                }
			}
			close(cursor);
		}

		return list;
	}

}
