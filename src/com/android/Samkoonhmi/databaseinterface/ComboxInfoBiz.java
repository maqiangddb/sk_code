package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.auth.NTCredentials;

import android.R.string;
import android.database.Cursor;
import android.util.Log;

import com.android.Samkoonhmi.model.ComboBoxInfo;
import com.android.Samkoonhmi.model.ComboxItemInfo;
import com.android.Samkoonhmi.model.FunInfo;
import com.android.Samkoonhmi.model.skbutton.BitButtonInfo;
import com.android.Samkoonhmi.model.skbutton.FunSwitchInfo;
import com.android.Samkoonhmi.model.skbutton.PeculiarButtonInfo;
import com.android.Samkoonhmi.model.skbutton.SceneButtonInfo;
import com.android.Samkoonhmi.model.skbutton.WordButtonInfo;
import com.android.Samkoonhmi.skenum.BUTTON;
import com.android.Samkoonhmi.skenum.IntToEnum;
import com.android.Samkoonhmi.skenum.BUTTON.BUTTON_TYPE;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.SKButton;

/**
 * 下拉框信息转换类
 * 
 * @author 瞿丽平
 * 
 */
public class ComboxInfoBiz extends DataBase {
	SKDataBaseInterface base = null;
	private ComboxItemInfo itemInfo;

	public ComboxInfoBiz() {
		base = SkGlobalData.getProjectDatabase();

	}

	/**
	 * 查找下拉框数据
	 * 
	 * @return
	 */
	public ArrayList<ComboBoxInfo> select(int sid) {
		
		//long start=System.currentTimeMillis();
		boolean init=true;
		String sId="";
		String sFunId="";
		Cursor cursor = null;
		if (null == base) {
			base = SkGlobalData.getProjectDatabase();
		}
		// 查找下拉框的属性
		cursor = base.getDatabaseBySql(
				"select * from combobox where nSceneId =?", new String[] { sid
						+ "" });
		ArrayList<ComboBoxInfo> list = new ArrayList<ComboBoxInfo>();
		if (cursor != null) {
			while (cursor.moveToNext()) {
				List<ComboxItemInfo> functionList = new ArrayList<ComboxItemInfo>();
				ComboBoxInfo info = new ComboBoxInfo();
				info.setId(cursor.getInt(cursor.getColumnIndex("nItemId")));
				info.seteFontCss(cursor.getInt(cursor
						.getColumnIndex("eFontCss")));
				info.setnBackColor(cursor.getInt(cursor
						.getColumnIndex("nBackColor")));
				info.setnFontColor(cursor.getInt(cursor
						.getColumnIndex("nFontColor")));
				info.setNfontSize(cursor.getInt(cursor
						.getColumnIndex("nfontSize")));
				info.setsFontType(cursor.getString(cursor
						.getColumnIndex("sFontType")));
				info.setnHeight(cursor.getInt(cursor.getColumnIndex("nHeight")));
				int languageId = cursor.getInt(cursor
						.getColumnIndex("nLanguageTypeId"));
				info.setnLanguageTypeId(IntToEnum.getText_Language(languageId));
				info.setnShowNumber(cursor.getInt(cursor
						.getColumnIndex("nShowNumber")));
				info.setnShowPropId(cursor.getInt(cursor
						.getColumnIndex("nShowPropId")));
				info.setnStartX(cursor.getInt(cursor.getColumnIndex("nStartX")));
				info.setnStartY(cursor.getInt(cursor.getColumnIndex("nStartY")));
				info.setnTouchPropId(cursor.getInt(cursor
						.getColumnIndex("nTouchPropId")));
				info.setnWidth(cursor.getInt(cursor.getColumnIndex("nWidth")));
				info.setnZvalue(cursor.getInt(cursor.getColumnIndex("nZvalue")));
				info.setnCollidindId(cursor.getInt(cursor
						.getColumnIndex("nCollidindId")));
				if (null != cursor.getString(cursor
						.getColumnIndex("bIsStartStatement"))) {
					boolean isStartStatement = cursor.getString(
							cursor.getColumnIndex("bIsStartStatement")).equals(
							"true") ? true : false;
					info.setbIsStartStatement(isStartStatement);
				}

				int sids = cursor.getInt(cursor.getColumnIndex("nScriptId"));
				info.setnScriptId(sids);
				info.setTouchInfo(TouchShowInfoBiz.getTouchInfoById(info
						.getId()));
				info.setShowInfo(TouchShowInfoBiz.getShowInfoById(info.getId()));
				info.setnAlpha(cursor.getInt(cursor.getColumnIndex("nAlpha")));
				info.setbIsUsePic(cursor.getString(cursor.getColumnIndex("bUsePic")).equals("true")?true:false);
				
				//getFunList(info, functionList);
				if (init) {
					sId+=" nItemId= "+info.getId();
					sFunId+=info.getId();
					init=false;
				}else {
					sFunId+=" ,"+ info.getId();
					sId+=" or nItemId= "+info.getId();
				}
				info.setFunctionList(functionList);
				list.add(info);
			}
			getFunList(sId, list,sFunId);
			close(cursor);
		
		}
		//Log.d("SKScene", "......time:"+(System.currentTimeMillis()-start));
		
		return list;
	}

	private HashMap<BUTTON_TYPE, ArrayList<FunInfo>> mFunList=null;
	private void getFunList(String id,ArrayList<ComboBoxInfo> list,String fid) {
		// 查找下拉框的功能
		Cursor cursor = null;
		cursor = base.getDatabaseBySql("select * from comboboxFun where "+id,null);
		ComboBoxInfo info=null;
		int nItemId=-1;
		if (null != cursor) {
			
			if (mFunList==null) {
				mFunList=new HashMap<BUTTON_TYPE, ArrayList<FunInfo>>();
			}else {
				mFunList.clear();
			}
			
			while (cursor.moveToNext()) {
				if (nItemId!=cursor.getInt(cursor.getColumnIndex("nItemId"))) {
					nItemId=cursor.getInt(cursor.getColumnIndex("nItemId"));
					for (int i = 0; i < list.size(); i++) {
						if (nItemId==list.get(i).getId()) {
							info=list.get(i);
							break;
						}
					}
				}
				itemInfo = new ComboxItemInfo();
				int buttonTypeValue = cursor.getInt(cursor
						.getColumnIndex("eFunctionType"));
				BUTTON.BUTTON_TYPE buttonType = IntToEnum
						.getButtonType(buttonTypeValue);
				itemInfo.seteFunctionType(buttonType);
				itemInfo.setFunctionId(cursor.getInt(cursor
						.getColumnIndex("nFunctionId")));
				itemInfo.setFunctionNameId(cursor.getInt(cursor
						.getColumnIndex("sFunctionName")));
				Map<Integer, String> functionNames = getManyLanguage(itemInfo
						.getFunctionNameId());
				itemInfo.setFunctionNames(functionNames);
				itemInfo.setId(cursor.getInt(cursor.getColumnIndex("id")));
				itemInfo.setChecked(false);
				int funId=cursor.getInt(cursor
						.getColumnIndex("nFunctionId"));
				SKButton button = new SKButton(funId, buttonType);
				itemInfo.setButton(button);
				String picPath=cursor.getString(cursor.getColumnIndex("sPath"));
				itemInfo.setPicPath(picPath);
				if(info!=null){
					info.getFunctionList().add(itemInfo);
					 if (mFunList.containsKey(buttonType)) {
					    	ArrayList<FunInfo> flist=mFunList.get(buttonType);
					    	FunInfo fInfo=new FunInfo();
					    	fInfo.nFunId=funId;
					    	fInfo.nItemId=nItemId;
					    	flist.add(fInfo);
						}else {
							ArrayList<FunInfo> flist=new ArrayList<FunInfo>();
							FunInfo fInfo=new FunInfo();
					    	fInfo.nFunId=funId;
					    	fInfo.nItemId=nItemId;
					    	flist.add(fInfo);
					    	mFunList.put(buttonType, flist);
						}
				}
			}
			selectFunById(list,fid);
			close(cursor);
		}
	}
	
	private void selectFunById(ArrayList<ComboBoxInfo> list,String fid){
		if (mFunList.size()==0) {
			return;
		}
		if (mFunList.containsKey(BUTTON_TYPE.BIT)) {
			//查询所有位开关
			bitButtonSelects(list,fid);
		}
		
		if (mFunList.containsKey(BUTTON_TYPE.WORD)) {
			//查询所有字开关
			wordButtonSelects(list, fid);
		}
		
		if (mFunList.containsKey(BUTTON_TYPE.SCENE)) {
			//查询所有画面开关
			sceneButtonSelects(list, fid);
		}
	}
	
	/**
	 * 开关,位功能查询
	 */
	private void bitButtonSelects(ArrayList<ComboBoxInfo> list, String itemId) {
		Cursor cursor = null;
		String sql = " select a.nItemId as fid,a.nFunctionId,b.* from  comboboxfun a  "
                   + " left join bitswitch b " 
                   + " where a.nFunctionId=b.nItemId  "
                   + " and a.nItemId in("+itemId+")";
		
		cursor = base.getDatabaseBySql(sql, null);
		BitButtonInfo info=null;
		int nItemId=-1;
		ComboBoxInfo fInfo=null;
		if (cursor != null) {
			while (cursor.moveToNext()) {
				if (nItemId!=cursor.getInt(cursor.getColumnIndex("fid"))) {
					nItemId=cursor.getInt(cursor.getColumnIndex("fid"));
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getId()==nItemId) {
							fInfo=list.get(i);
							break;
						}
					}
				}
				if (fInfo!=null) {
					for (int i = 0; i < fInfo.getFunctionList().size(); i++) {
						if (fInfo.getFunctionList().get(i).getButton().getInfo().getnFunId()==
								cursor.getInt(cursor.getColumnIndex("nFunctionId"))) {
							info= (BitButtonInfo)fInfo.getFunctionList().get(i).getButton().getInfo();
							break;
						}
					}
				}
				if (info!=null) {
					info.seteOperType(BUTTON.getBitOperType(cursor.getShort(cursor
							.getColumnIndex("eOperType"))));
					boolean b = cursor.getString(cursor.getColumnIndex("bDownZero")).equals("true") ? true
							: false;
					info.setbDownZero(b);
					info.setmBitAddress(AddrPropBiz.selectById(cursor.getInt(cursor
							.getColumnIndex("nBitAddress"))));
					info.setnTimeOut(cursor.getInt(cursor.getColumnIndex("nTimeout")));
					
					boolean bb=false;
					String confirm=cursor.getString(cursor.getColumnIndex("bConfirm"));
					if (confirm!=null) {
						bb=confirm.equals("true") ? true: false;
					}
					info.setbConfirm(bb);
				}
			}
		}
		close(cursor);
	}

	/**
	 * 开关,字功能查询
	 */
	private void wordButtonSelects(ArrayList<ComboBoxInfo> list, String itemId) {
		Cursor cursor = null;
		String sql = " select a.nItemId as fid,a.nFunctionId,b.* from  comboboxfun a  "
                + " left join wordSwitch b " 
                + " where a.nFunctionId=b.nItemId  "
                + " and a.nItemId in("+itemId+")";
		cursor = base.getDatabaseBySql(sql,null);
		
		WordButtonInfo info=null;
		int nItemId=-1;
		ComboBoxInfo fInfo=null;
		
		if (cursor != null) {
			while (cursor.moveToNext()) {
				
				if (nItemId!=cursor.getInt(cursor.getColumnIndex("fid"))) {
					nItemId=cursor.getInt(cursor.getColumnIndex("fid"));
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getId()==nItemId) {
							fInfo=list.get(i);
							break;
						}
					}
				}
				if (fInfo!=null) {
					for (int i = 0; i < fInfo.getFunctionList().size(); i++) {
						if (fInfo.getFunctionList().get(i).getButton().getInfo().getnFunId()==
								cursor.getInt(cursor.getColumnIndex("nFunctionId"))) {
							info= (WordButtonInfo)fInfo.getFunctionList().get(i).getButton().getInfo();
							break;
						}
					}
				}
				
				if (info!=null) {
					info.seteOperType(BUTTON.getWordOperType(cursor.getShort(cursor
							.getColumnIndex("eOperType"))));
					info.setmAddrProp(AddrPropBiz.selectById( cursor.getInt(cursor
							.getColumnIndex("nAddress"))));
					info.seteDataType(IntToEnum.getDataType(cursor.getInt(cursor
							.getColumnIndex("nDataType"))));
					
					boolean b =false;
					String cycle=cursor.getString(cursor.getColumnIndex("bCycle"));
					if (cycle!=null) {
						b=cycle.equals("true") ? true: false;
					}
					info.setbCycle(b);
					info.setnMax(cursor.getDouble(cursor.getColumnIndex("nMax")));
					info.setnMin(cursor.getDouble(cursor.getColumnIndex("nMin")));
					info.setnFinalValue(cursor.getDouble(cursor
							.getColumnIndex("nFinalValue")));
//					b = false;
//					String bDynamic = cursor.getString(cursor.getColumnIndex("bDynamicControl"));
//					if (bDynamic != null) {
//						b = bDynamic.equals("true")?true:false;
//					}
//					info.setbDynamicControl(b);
//					info.setmDynamicAddrProp(AddrPropBiz.selectById( cursor.getInt(cursor
//							.getColumnIndex("nAddressConst"))));
				}
				
			}
		}
		close(cursor);
	}

	/**
	 * 开关,画面功能查询
	 * 
	 * @param itemId
	 *            -控件关联Id
	 */
	private void sceneButtonSelects(ArrayList<ComboBoxInfo> list, String itemId) {
		Cursor cursor = null;
		String sql = " select a.nItemId as fid,a.nFunctionId,b.* from  comboboxfun a  "
                + " left join screenSwitch b " 
                + " where a.nFunctionId=b.nItemId  "
                + " and a.nItemId in("+itemId+")";
		cursor = base.getDatabaseBySql(sql, null);
		if (cursor != null) {
			
			SceneButtonInfo info=null;
			int nItemId=-1;
			ComboBoxInfo fInfo=null;
			
			while (cursor.moveToNext()) {
				
				if (nItemId!=cursor.getInt(cursor.getColumnIndex("fid"))) {
					nItemId=cursor.getInt(cursor.getColumnIndex("fid"));
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getId()==nItemId) {
							fInfo=list.get(i);
							break;
						}
					}
				}
				if (fInfo!=null) {
					for (int i = 0; i < fInfo.getFunctionList().size(); i++) {
						if (fInfo.getFunctionList().get(i).getButton().getInfo().getnFunId()==
								cursor.getInt(cursor.getColumnIndex("nFunctionId"))) {
							info= (SceneButtonInfo)fInfo.getFunctionList().get(i).getButton().getInfo();
							break;
						}
					}
				}
				
				if (info!=null) {
					info.seteOperScene(BUTTON.getOPerScene(cursor.getShort(cursor
							.getColumnIndex("eOperScene"))));
					info.setnTargetPage(cursor.getInt(cursor
							.getColumnIndex("nTargetPage")));
					boolean b = cursor.getString(cursor.getColumnIndex("bLogout")).equals("true") ? true
							: false;
					info.setnSceneType(cursor.getShort(cursor.getColumnIndex("nSceneType")));
					info.setbLogout(b);
				}
				
			}
		}
		close(cursor);
	}
	

	private Map<Integer, String> getManyLanguage(int itemId) {
		Map<Integer, String> map = null;
		Cursor cursor = null;
		if (null == base) {
			base = SkGlobalData.getProjectDatabase();
		}
		// 查找下拉框的属性
		cursor = base.getDatabaseBySql(
				"select * from itemMutilLanguage where nItemId  =?",
				new String[] { itemId + "" });
		if (cursor != null) {
			map = new HashMap<Integer, String>();
			while (cursor.moveToNext()) {
				int languageId = cursor.getInt(cursor
						.getColumnIndex("nLanguageId"));
				String functionName = cursor.getString(cursor
						.getColumnIndex("sText"));
				map.put(languageId, functionName);
			}
			close(cursor);
		}
		return map;
	}

}
