package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;
import android.database.Cursor;
import com.android.Samkoonhmi.model.AcillInputInfo;
import com.android.Samkoonhmi.model.ShowInfo;
import com.android.Samkoonhmi.model.TouchInfo;
import com.android.Samkoonhmi.skenum.INPUT_TYPE;
import com.android.Samkoonhmi.skenum.IntToEnum;
import com.android.Samkoonhmi.skenum.TEXT_LANGUAGE;
import com.android.Samkoonhmi.skenum.TEXT_PIC_ALIGN;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.SKKeyPopupWindow;
import com.android.Samkoonhmi.util.AddrProp;

/**
 * Acill输入显示器
 * @author Administrator
 */
public class AcillInputBiz extends DataBase {
	private SKDataBaseInterface db;

	public AcillInputBiz() {
		db = SkGlobalData.getProjectDatabase();

	}

	/**
	 * 查找acill输入显示对象
	 * 
	 * @return
	 */
	public ArrayList<AcillInputInfo> selectAcillInputInfo(int sceneId) {
		ArrayList<AcillInputInfo> list = new ArrayList<AcillInputInfo>();
		if (null == db) {
			db = SkGlobalData.getProjectDatabase();
		}
		
		StringBuffer id = new StringBuffer();
		boolean init = true;
		Cursor cursor = db.getDatabaseBySql(
				"select * from dataShow where eItemType=2 and  nSceneId=?", new String[] { sceneId
						+ "" });

		if (null != cursor) {
			while (cursor.moveToNext()) {
				AcillInputInfo	info = new AcillInputInfo();
				info.seteFontCss(cursor.getShort(cursor
						.getColumnIndex("eFontCss")));
				info.setId(cursor.getInt(cursor.getColumnIndex("nItemId")));
				info.setnFontsize(cursor.getInt(cursor
						.getColumnIndex("nFontSize")));
				info.setsFontStyle(cursor.getString(cursor
						.getColumnIndex("sFontStyle")));
				info.setnHeight(cursor.getInt(cursor.getColumnIndex("nHeight")));
				info.setsShapId(cursor.getString(cursor
						.getColumnIndex("sShapId")));
				info.setnShowPropId(cursor.getInt(cursor
						.getColumnIndex("nShowPropId")));
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
				info.setnTouchPropId(cursor.getInt(cursor
						.getColumnIndex("nTouchPropId")));
				info.setnWidth(cursor.getInt(cursor.getColumnIndex("nWidth")));
				info.setnZvalue(cursor.getInt(cursor.getColumnIndex("nZvalue")));
				info.setnCollidindId(cursor.getInt(cursor
						.getColumnIndex("nCollidindId")));
				if (null != cursor.getString(cursor
						.getColumnIndex("bIsStartStatement"))) {
					info.setbIsStartStatement(cursor.getString(
							cursor.getColumnIndex("bIsStartStatement")).equals(
							"true") ? true : false);
				}
				info.setnScriptId(cursor.getInt(cursor
						.getColumnIndex("nScriptId")));
				info.setnTransparent(cursor.getInt(cursor.getColumnIndex("nTransparent")));
				
				//地址偏移
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
		AcillInputInfo info = null;
		int nItemId = -1;
		cursor = db.getDatabaseBySql("select * from  ascii where " + sId, null);
		int index=0;
		
		if (null != cursor) {
			while (cursor.moveToNext()) {
				if (nItemId != cursor.getInt(cursor.getColumnIndex("nItemId"))) {
					nItemId = cursor.getInt(cursor.getColumnIndex("nItemId"));
					if (list.get(index).getId()==nItemId) {
						info = list.get(index);
					}else{
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getId() == nItemId) {
								info = list.get(i);
								break;
							}
						}
					}
					index++;
				}
				if (null != cursor.getString(cursor.getColumnIndex("bIsinput"))) {
					info.setbIsinput(cursor.getString(
							cursor.getColumnIndex("bIsinput")).equals("true") ? true
							: false);
				}
				info.setnBackColor(cursor.getInt(cursor
						.getColumnIndex("nBackColor")));
				info.setnCode(cursor.getShort(cursor.getColumnIndex("nCode")));
				info.setnFontColor(cursor.getInt(cursor
						.getColumnIndex("nFontColor")));
				TEXT_LANGUAGE textLanguage = IntToEnum.getText_Language(cursor
						.getInt(cursor.getColumnIndex("nLanguageTypeId")));
				info.setnLanguageTypeId(textLanguage);
				info.setnShowCharNumber(cursor.getInt(cursor
						.getColumnIndex("nShowCharNumber")));
				TEXT_PIC_ALIGN nShowStyle = IntToEnum.getTextPicAlign(cursor
						.getInt(cursor.getColumnIndex("nShowStyle")));
				info.setnShowStyle(nShowStyle);
				AddrProp addrPropValue = AddrPropBiz.selectById(cursor
						.getInt(cursor.getColumnIndex("nAddress")));
				info.setnAddress(addrPropValue);
				
				info.setnKeyId(cursor.getInt(cursor.getColumnIndex("nKeyId")));
				
				if (info.getnKeyId()!=-1) {
					boolean reulst=SKKeyPopupWindow.existKeyBroad(info.getnKeyId());
					if (!reulst) {
						//如果自定义键盘不存在，则调用系统键盘
						info.setnKeyId(-1);
					}
				}
				
				INPUT_TYPE eInputTypeId = IntToEnum.getInputType(cursor
						.getInt(cursor.getColumnIndex("eInputTypeId")));
				info.seteInputTypeId(eInputTypeId);
				if (info.geteInputTypeId() == INPUT_TYPE.BIT) {
					info.setsBitAddress(AddrPropBiz.selectById(cursor
							.getInt(cursor.getColumnIndex("sBitAddress"))));
				}
				TouchInfo touchInfo = TouchShowInfoBiz.getTouchInfoById(info.getId());
				ShowInfo showInfo = TouchShowInfoBiz.getShowInfoById(info.getId());
				info.setmShowInfo(showInfo);
				info.setmTouchinInfo(touchInfo);
				if (null != cursor.getString(cursor.getColumnIndex("bInputSign"))) {
					info.setbInputSign(cursor.getString(
							cursor.getColumnIndex("bInputSign")).equals("true") ? true
							: false);
				}
				info.setnBoardX(cursor.getInt((cursor.getColumnIndex("nBoardX"))));
				info.setnBoardY(cursor.getInt(cursor.getColumnIndex("nBoardY")));
				if (null != cursor.getString(cursor
						.getColumnIndex("bAutoChangeBit"))) {
					info.setbAutoChangeBit(cursor.getString(
							cursor.getColumnIndex("bAutoChangeBit")).equals(
							"true") ? true : false);
				}
				boolean inputIsShow = cursor.getString(
						cursor.getColumnIndex("bInputIsShow")).equals("true") ? true
						: false;
				info.setInputIsShow(inputIsShow);
                if(!inputIsShow)
                {
            		int nInputAddrId=cursor.getInt(cursor.getColumnIndex("nInputAddr"));
    				if (nInputAddrId>-1) {
    					info.setInputAddr(AddrPropBiz.selectById(nInputAddrId));
    				}	
                }

			}
		}
		close(cursor);

		return list;
	}
}
