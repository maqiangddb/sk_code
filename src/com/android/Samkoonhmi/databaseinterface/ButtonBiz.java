package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.android.Samkoonhmi.model.FunInfo;
import com.android.Samkoonhmi.model.PriorLoadInfo;
import com.android.Samkoonhmi.model.StakeoutInfo;
import com.android.Samkoonhmi.model.TextInfo;
import com.android.Samkoonhmi.model.skbutton.BitButtonInfo;
import com.android.Samkoonhmi.model.skbutton.ButtonInfo;
import com.android.Samkoonhmi.model.skbutton.FunSwitchInfo;
import com.android.Samkoonhmi.model.skbutton.PeculiarButtonInfo;
import com.android.Samkoonhmi.model.skbutton.SceneButtonInfo;
import com.android.Samkoonhmi.model.skbutton.WordButtonInfo;
import com.android.Samkoonhmi.skenum.BUTTON;
import com.android.Samkoonhmi.skenum.BUTTON.BUTTON_TYPE;
import com.android.Samkoonhmi.skenum.BUTTON.OPER_SCENE;
import com.android.Samkoonhmi.skenum.FLICK_TYPE;
import com.android.Samkoonhmi.skenum.IntToEnum;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.SKButton;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.skwindow.SKSceneManage.SHOW_TYPE;

/**
 * 开关数据查询类
 */
public class ButtonBiz extends DataBase {

	private SKDataBaseInterface db = null;
	private BUTTON.BUTTON_TYPE type;

	public ButtonBiz() {
		db = SkGlobalData.getProjectDatabase();
	}
	
	private static HashMap<Integer, BitButtonInfo> mBitMap=null;
	private static HashMap<Integer, WordButtonInfo> mWorkMap=null;
	
	/**
	 * 获取某个画面的所有开关
	 * @param sid=场景id
	 * @param mLoadList=需要加载的画面
	 */
	public ArrayList<ButtonInfo> selectBySid(int sid, ArrayList<PriorLoadInfo> mLoadList){
		
		//Log.d("ButtonBiz", "button load sid="+sid);
		long time=System.currentTimeMillis();
		
		StringBuffer id=new StringBuffer();
		StringBuffer bit=null;
		StringBuffer word=null;
		StringBuffer scene=null;
		StringBuffer pec=null;
		ArrayList<ButtonInfo> list=new ArrayList<ButtonInfo>();
		
		Cursor cursor = null;
		String sql = "select * from switchButton where nSceneId="+sid;
		cursor = db.getDatabaseBySql(sql,null);
		int funId=0;
		if (cursor != null) {
			/**
			 * 参数：文本和状态、位、字、画面、功能
			 */
			
			boolean[] init=new boolean[]{true,true,true,true,true};
			//触控显示查询
			while (cursor.moveToNext()) {
				ButtonInfo info = null;
				type = IntToEnum.getButtonType(cursor.getShort(cursor.getColumnIndex("eButtonType")));
				int nItemId=cursor.getInt(cursor.getColumnIndex("nItemId"));
				funId=cursor.getInt(cursor.getColumnIndex("nSwitchFunId"));
				
				switch (type) {
				case BIT:
					info = new BitButtonInfo();
					if(init[1]){
						bit=new StringBuffer();
						bit.append(" nItemId in("+funId);
						init[1]=false;
					}else {
						bit.append(","+funId);
					}
					break;
				case WORD:
					info=new WordButtonInfo();
					if(init[2]){
						word=new StringBuffer();
						word.append(" nItemId in("+funId);
						init[2]=false;
					}else {
						word.append(","+funId);
					}
					break;
				case SCENE:
					info=new SceneButtonInfo();
					if(init[3]){
						scene=new StringBuffer();
						scene.append(" nItemId in("+funId);
						init[3]=false;
					}else {
						scene.append(","+funId);
					}
					break;
				case PECULIAR:
					info=new PeculiarButtonInfo();
					if(init[4]){
						pec=new StringBuffer();
						pec.append(" nItemId in("+funId);
						init[4]=false;
					}else {
						pec.append(","+funId);
					}
					break;
				case BIT_LIGHT:
					info=new ButtonInfo();
					break;
				}
				info.setnButtonId(nItemId);
				info.setnFunId(funId);
				info.seteButtonType(type);
				info.seteWatchType(BUTTON.getWatchType(cursor.getShort(cursor
						.getColumnIndex("eWatchType"))));
				info.setnLp(cursor.getShort(cursor.getColumnIndex("nLp")));
				info.setnTp(cursor.getShort(cursor.getColumnIndex("nTp")));
				info.setnWidth(cursor.getShort(cursor.getColumnIndex("nWidth")));
				info.setnHeight(cursor.getShort(cursor.getColumnIndex("nHeight")));
				info.setnShowLp(cursor.getShort(cursor.getColumnIndex("nShowLp")));
				info.setnShowTp(cursor.getShort(cursor.getColumnIndex("nShowTp")));
				info.setnShowWidth(cursor.getShort(cursor.getColumnIndex("nShowWidth")));
				info.setnShowHeight(cursor.getShort(cursor.getColumnIndex("nShowHeight")));
				String lan=cursor.getString(cursor.getColumnIndex("bSameLanguage"));
				boolean b=false;
				if (lan!=null) {
					b=lan.equals("true")? true : false;
				}
				info.setbSameLanguage(b);
				boolean bb = cursor.getString(cursor
						.getColumnIndex("bIsStartStatement")).equals("true") ? true: false;
				info.setbIsStartStatement(bb);
				String slid=cursor.getString(cursor.getColumnIndex("bSlid"));
				boolean bSlid=false;
				if (slid!=null) {
					bSlid=slid.equals("true")? true : false;
				}
			
				info.setbSlid(bSlid);
				info.setnStatementId(cursor.getShort(cursor.getColumnIndex("nStatementId")));
				int wid=cursor.getInt(cursor.getColumnIndex("nWatchAddr"));
				if (wid>0) {
					info.setmWatchAddress(AddrPropBiz.selectById(wid));
				}else {
					info.setmWatchAddress(null);
				}
				
				info.seteWatchDataType(IntToEnum.getDataType(
						cursor.getShort(cursor.getColumnIndex("eWatchDataType"))));
				boolean bType=cursor.getString(cursor.getColumnIndex("nAddrType")).equals("true")?true:false;
				info.setbAddrType(bType);
				info.setnBitIndex(cursor.getShort(cursor.getColumnIndex("nBitIndex")));
				info.setnCondition(cursor.getShort(cursor.getColumnIndex("nCondition")));
				info.setnZvalue(cursor.getShort(cursor.getColumnIndex("nZvalue")));
				info.setnCollidindId(cursor.getShort(cursor.getColumnIndex("nCollidindId")));
				info.setmShowInfo(TouchShowInfoBiz.getShowInfoById(info.getnButtonId()));
				info.setmTouchInfo(TouchShowInfoBiz.getTouchInfoById(info.getnButtonId()));
				boolean isUpRun=false;
				String s=cursor.getString(cursor.getColumnIndex("bIsStartStatementUp"));
				if (s!=null) {
					isUpRun=s.equals("true")? true : false;
				}
				info.setUpRun(isUpRun);
				if (isUpRun) {
					info.setnStatemenUpId(cursor.getInt(cursor.getColumnIndex("nStatementIdUp")));
				}
				if(init[0]){
					id.append(" nItemId in("+nItemId);
					init[0]=false;
				}else {
					id.append(","+nItemId);
				}
				list.add(info);
			}
		}
		close(cursor);
		
		if (list.size()>0) {
			
			if (bit!=null&&bit.length()>0) {
				//位开关
				bitButtonSelect(list, bit.toString()+")");
			}
			if (word!=null&&word.length()>0) {
				//字开关
				wordButtonSelect(list, word.toString()+")");
			}
			if (scene!=null&&scene.length()>0) {
				//画面开关
				sceneButtonSelect(list, scene.toString()+")",mLoadList,sid);
			}
			if (pec!=null&&pec.length()>0) {
				//功能开关
				peculiarSelect(list, pec.toString()+")");
			}
			
			//获取文本
			id.append(")");
			String temp=id.toString();
			stateSelect(list,temp);
			textSelect(list,temp);
		}
		//Log.d("DataBase", "button time  =  "+(System.currentTimeMillis()-time)+",sid="+sid);
		return list;
	}
	
	/**
	 * 开关,位功能查询
	 */
	public void bitButtonSelect(ArrayList<ButtonInfo> list, String id) {
		
		
		Cursor cursor = null;
		String sql = "select * from bitSwitch where "+id;
		cursor = db.getDatabaseBySql(sql, null);
		int nItemId=-1;
		BitButtonInfo info=null;
		int index=0;
		if (cursor != null) {
			while (cursor.moveToNext()) {
				if (nItemId!=cursor.getInt(cursor.getColumnIndex("nItemId"))) {
					nItemId=cursor.getInt(cursor.getColumnIndex("nItemId"));
					if (list.get(index).getnFunId()==nItemId) {
						info=(BitButtonInfo)list.get(index);
					}else {
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getnFunId()==nItemId) {
								info=(BitButtonInfo)list.get(i);
								break;
							}
						}
					}
					index++;
				}
				info.seteOperType(BUTTON.getBitOperType(cursor.getShort(cursor.getColumnIndex("eOperType"))));
				boolean b = cursor.getString(cursor.getColumnIndex("bDownZero")).equals("true") ? true: false;
				info.setbDownZero(b);
				info.setmBitAddress(AddrPropBiz.selectById(cursor.getInt(cursor.getColumnIndex("nBitAddress"))));
				info.setnTimeOut(cursor.getInt(cursor.getColumnIndex("nTimeout")));
				
				boolean bb=false;
				String confirm=cursor.getString(cursor.getColumnIndex("bConfirm"));
				if (confirm!=null) {
					bb=confirm.equals("true") ? true: false;
				}
				info.setbConfirm(bb);
			}
		}
		close(cursor);
	}

	/**
	 * 开关,字功能查询
	 */
	public void wordButtonSelect(ArrayList<ButtonInfo> list, String id) {
		Cursor cursor = null;
		String sql = "select * from wordSwitch where "+id;
		cursor = db.getDatabaseBySql(sql, null);
		int nItemId=-1;
		WordButtonInfo info=null;
		int index=0;
		
		if (cursor != null) {
			while (cursor.moveToNext()) {
				if (nItemId!=cursor.getInt(cursor.getColumnIndex("nItemId"))) {
					nItemId=cursor.getInt(cursor.getColumnIndex("nItemId"));
					
					if (list.get(index).getnFunId()==nItemId) {
						info=(WordButtonInfo)list.get(index);
					}else {
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getnFunId()==nItemId) {
								info=(WordButtonInfo)list.get(i);
								break;
							}
						}
					}
					index++;
				}
				info.seteOperType(BUTTON.getWordOperType(cursor.getShort(cursor.getColumnIndex("eOperType"))));
				info.setmAddrProp(AddrPropBiz.selectById( cursor.getInt(cursor.getColumnIndex("nAddress"))));
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
				b = false;
				String bDynamic = cursor.getString(cursor.getColumnIndex("bDynamicControl"));
				if (bDynamic != null) {
					b = bDynamic.equals("true")?true:false;
				}
				info.setbDynamicControl(b);
				info.setmDynamicAddrProp(AddrPropBiz.selectById(cursor.getInt(cursor.getColumnIndex("nAddressConst"))));
			}
		}
		close(cursor);
	}
	
	public void getWordSwitchDynAddress(int itemId, WordButtonInfo info){
		Cursor cursor = null;
		String sql = "select bDynamicControl, nAddressConst from wordSwitch where nItemId = "+itemId;
		cursor = db.getDatabaseBySql(sql, null);
		if (cursor != null && cursor.moveToFirst()) {
			boolean b = false;
			String bDynamic = cursor.getString(cursor.getColumnIndex("bDynamicControl"));
			if (bDynamic != null) {
				b = bDynamic.equals("true")?true:false;
			}
			info.setbDynamicControl(b);
			info.setmDynamicAddrProp(AddrPropBiz.selectById(cursor.getInt(cursor.getColumnIndex("nAddressConst"))));
		}
		
		close(cursor);
	}

	/**
	 * 开关,画面功能查询
	 * @param itemId-控件关联Id
	 */
	public void sceneButtonSelect(ArrayList<ButtonInfo> list, String id,ArrayList<PriorLoadInfo> mLoadList,int sid) {
		Cursor cursor = null;
		String sql = "select * from screenSwitch where "+id;
		cursor = db.getDatabaseBySql(sql, null);
		int nItemId=-1;
		SceneButtonInfo info=null;
		int index=0;
		if (cursor != null) {
			while (cursor.moveToNext()) {
				if (nItemId!=cursor.getInt(cursor.getColumnIndex("nItemId"))) {
					nItemId=cursor.getInt(cursor.getColumnIndex("nItemId"));
					
					if (list.get(index).getnFunId()==nItemId) {
						info=(SceneButtonInfo)list.get(index);
					}else{
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getnFunId()==nItemId) {
								info=(SceneButtonInfo)list.get(i);
								break;
							}
						}
					}
					index++;
				}
				info.seteOperScene(BUTTON.getOPerScene(cursor.getShort(cursor.getColumnIndex("eOperScene"))));
				info.setnTargetPage(cursor.getInt(cursor.getColumnIndex("nTargetPage")));
				boolean b = cursor.getString(cursor.getColumnIndex("bLogout")).equals("true") ? true: false;
				info.setnSceneType(cursor.getShort(cursor.getColumnIndex("nSceneType")));
				info.setnEnterType(cursor.getInt(cursor.getColumnIndex("nSlideStyle")));
				info.setbLogout(b);
				if (mLoadList!=null) {
					int nSid=-1;
					if (info.getnSceneType()==0) {
						if (info.geteOperScene()==OPER_SCENE.OPEN) {
							//打开画面
							nSid=SKSceneManage.getInstance().getSidById(SHOW_TYPE.DEFAULT,info.getnTargetPage()+1);
						}else if (info.geteOperScene()==OPER_SCENE.NEXT) {
							//下一个画面
							nSid=sid+1;
						}
					}else {
						if (info.geteOperScene()==OPER_SCENE.OPEN_WINDOW) {
							//打开窗口
							ArrayList<Integer> list2=SKSceneManage.getInstance().getWindowList();
							if (list2!=null&&list2.size()>info.getnTargetPage()) {
								nSid=list2.get(info.getnTargetPage());
							}
						}
					}
					
					if (nSid>0) {
						PriorLoadInfo pInfo=new PriorLoadInfo();
						pInfo.nLoadSid=nSid;
						pInfo.nSid=sid;
						mLoadList.add(pInfo);
					}
				}
			}
		}
		close(cursor);
	}

	/**
	 * 开关,特殊开关查询
	 */
	public void peculiarSelect(ArrayList<ButtonInfo> list, String id) {
		Cursor cursor = null;
		String sql = "select * from funSwitch where "+id;
		cursor = db.getDatabaseBySql(sql, null);
		int nItemId=-1;
		PeculiarButtonInfo info=null;
		int index=0;
		
		if (cursor != null) {
			while (cursor.moveToNext()) {
				if (nItemId!=cursor.getInt(cursor.getColumnIndex("nItemId"))) {
					nItemId=cursor.getInt(cursor.getColumnIndex("nItemId"));
					if (list.get(index).getnFunId()==nItemId) {
						info=(PeculiarButtonInfo)list.get(index);
					}else {
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getnFunId()==nItemId) {
								info=(PeculiarButtonInfo)list.get(i);
								break;
							}
						}
					}
					index++;
				}
				info.setePeculiarType(BUTTON.getPeculiarType(cursor
						.getShort(cursor.getColumnIndex("ePeculiarType"))));
				info.setnActionId(BUTTON.getPeculiarOper(cursor.getShort(cursor
						.getColumnIndex("nActionId"))));
				info.setnWindowID(cursor.getInt(cursor
						.getColumnIndex("nWindowID")));
				info.setnLanguageId(cursor.getInt(cursor
						.getColumnIndex("nActionId")));
				boolean bZoomX = cursor.getString(cursor.getColumnIndex("bX")).equals("true") ? true: false;
				boolean bZoomY = cursor.getString(cursor.getColumnIndex("bY")).equals("true") ? true: false;
				info.setbZoomX(bZoomX);
				info.setbZoomY(bZoomY);
			}
		}
		close(cursor);
	}

	/**
	 * 开关,位功能查询
	 */
	private void bitButtonSelects(ArrayList<FunSwitchInfo> list, String itemId) {
		Cursor cursor = null;
		String sql = " select a.nItemId as fid,a.nFunctionId,b.* from  comboboxfun a  "
                   + " left join bitswitch b " 
                   + " where a.nFunctionId=b.nItemId  "
                   + " and a.nItemId in("+itemId+")";
		
		cursor = db.getDatabaseBySql(sql, null);
		BitButtonInfo info=null;
		int nItemId=-1;
		FunSwitchInfo fInfo=null;
		if (cursor != null) {
			while (cursor.moveToNext()) {
				if (nItemId!=cursor.getInt(cursor.getColumnIndex("fid"))) {
					nItemId=cursor.getInt(cursor.getColumnIndex("fid"));
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getnItemId()==nItemId) {
							fInfo=list.get(i);
							break;
						}
					}
				}
				if (fInfo!=null) {
					for (int i = 0; i < fInfo.getmSkButtons().size(); i++) {
						if (fInfo.getmSkButtons().get(i).getInfo().getnFunId()==
								cursor.getInt(cursor.getColumnIndex("nFunctionId"))) {
							info= (BitButtonInfo)fInfo.getmSkButtons().get(i).getInfo();
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
	private void wordButtonSelects(ArrayList<FunSwitchInfo> list, String itemId) {
		Cursor cursor = null;
		String sql = " select a.nItemId as fid,a.nFunctionId,b.* from  comboboxfun a  "
                + " left join wordSwitch b " 
                + " where a.nFunctionId=b.nItemId  "
                + " and a.nItemId in("+itemId+")";
		cursor = db.getDatabaseBySql(sql,null);
		
		WordButtonInfo info=null;
		int nItemId=-1;
		FunSwitchInfo fInfo=null;
		
		if (cursor != null) {
			while (cursor.moveToNext()) {
				
				if (nItemId!=cursor.getInt(cursor.getColumnIndex("fid"))) {
					nItemId=cursor.getInt(cursor.getColumnIndex("fid"));
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getnItemId()==nItemId) {
							fInfo=list.get(i);
							break;
						}
					}
				}
				if (fInfo!=null) {
					for (int i = 0; i < fInfo.getmSkButtons().size(); i++) {
						if (fInfo.getmSkButtons().get(i).getInfo().getnFunId()==
								cursor.getInt(cursor.getColumnIndex("nFunctionId"))) {
							info= (WordButtonInfo)fInfo.getmSkButtons().get(i).getInfo();
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
	private void sceneButtonSelects(ArrayList<FunSwitchInfo> list, String itemId) {
		Cursor cursor = null;
		String sql = " select a.nItemId as fid,a.nFunctionId,b.* from  comboboxfun a  "
                + " left join screenSwitch b " 
                + " where a.nFunctionId=b.nItemId  "
                + " and a.nItemId in("+itemId+")";
		cursor = db.getDatabaseBySql(sql, null);
		if (cursor != null) {
			
			SceneButtonInfo info=null;
			int nItemId=-1;
			FunSwitchInfo fInfo=null;
			
			while (cursor.moveToNext()) {
				
				if (nItemId!=cursor.getInt(cursor.getColumnIndex("fid"))) {
					nItemId=cursor.getInt(cursor.getColumnIndex("fid"));
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getnItemId()==nItemId) {
							fInfo=list.get(i);
							break;
						}
					}
				}
				if (fInfo!=null) {
					for (int i = 0; i < fInfo.getmSkButtons().size(); i++) {
						if (fInfo.getmSkButtons().get(i).getInfo().getnFunId()==
								cursor.getInt(cursor.getColumnIndex("nFunctionId"))) {
							info= (SceneButtonInfo)fInfo.getmSkButtons().get(i).getInfo();
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


	
	/**
	 * 开关状态 itemId-控件id
	 */
	private void stateSelect(ArrayList<ButtonInfo> list,String id) {
		
		Cursor cursor = null;
		String sql = "select * from switchStatusProp where "+id;
		cursor = db.getDatabaseBySql(sql, null);
		if (cursor != null) {
			ButtonInfo info=null;
			int itemId=-1;
			int index=0;
			while (cursor.moveToNext()) {
				if (itemId!=cursor.getInt(cursor.getColumnIndex("nItemId"))) {
					itemId=cursor.getInt(cursor.getColumnIndex("nItemId"));
					if (list.get(index).getnButtonId()==itemId) {
						info=list.get(index);
						ArrayList<StakeoutInfo> mStakeoutList = new ArrayList<StakeoutInfo>();
						info.setmStakeoutList(mStakeoutList);
					}else {
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getnButtonId()==itemId) {
								info=list.get(i);
								ArrayList<StakeoutInfo> mStakeoutList = new ArrayList<StakeoutInfo>();
								info.setmStakeoutList(mStakeoutList);
								break;
							}
						}
					}
					index++;
					
				}
				short stateId = cursor.getShort(cursor
						.getColumnIndex("nStatusIndex"));
				
				StakeoutInfo sInfo = new StakeoutInfo();
				sInfo.setnStatusId(stateId);
				sInfo.setnCmpFactor((short) cursor.getDouble(cursor
						.getColumnIndex("statusValue")));
				sInfo.setnStatusId(stateId);
				sInfo.seteFlickType(IntToEnum.getFlickType(cursor.getInt(cursor.getColumnIndex("eflick"))));
				sInfo.setnAlpha(cursor.getInt(cursor.getColumnIndex("nAlpha")));
				sInfo.setnColor(cursor.getInt(cursor.getColumnIndex("nColor")));
				sInfo.setnShapeType(cursor.getShort(cursor.getColumnIndex("eLib")));
				sInfo.setsPath(cursor.getString(cursor
						.getColumnIndex("sPath")));
				info.getmStakeoutList().add(sInfo);
			}
			
		}
		close(cursor);
	}
	
	/**
	 * 文本
	 */
	private void textSelect(ArrayList<ButtonInfo> list,String id){
		Cursor cursor = null;
		String sql = "select * from textProp where "+id;
		cursor = db.getDatabaseBySql(sql,null);
		if(cursor!=null){
			ButtonInfo info=null;
			
			int nItemId=-1;
			int state=-1;
			int index=0;
			int k=0;
			while (cursor.moveToNext()) {
				if (nItemId!=cursor.getInt(cursor.getColumnIndex("nItemId"))) {
					nItemId=cursor.getInt(cursor.getColumnIndex("nItemId"));
					if (list.get(k).getnButtonId()==nItemId) {
						info=list.get(k);
						ArrayList<TextInfo> mTextList=new ArrayList<TextInfo>();
						info.setmTextList(mTextList);
						state=-1;
						index=0;
					}else {
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getnButtonId()==nItemId) {
								info=list.get(i);
								ArrayList<TextInfo> mTextList=new ArrayList<TextInfo>();
								info.setmTextList(mTextList);
								state=-1;
								index=0;
								break;
							}
						}
					}
					k++;
				}
				if (state!=cursor.getShort(cursor
						.getColumnIndex("nStatusIndex"))) {
					
					state=cursor.getShort(cursor
							.getColumnIndex("nStatusIndex"));
					TextInfo tInfo = new TextInfo();
					tInfo.setnStatusId( cursor.getShort(cursor
							.getColumnIndex("nStatusIndex")));
					tInfo.setnBColor(cursor.getInt(cursor.getColumnIndex("nColor")));
					tInfo.setnLangugeId(cursor.getShort(cursor
							.getColumnIndex("nLangIndex")));
					tInfo.seteFlickType(FLICK_TYPE.NO_FLICK);
					
					//字体类型
					ArrayList<String> mFonts=new ArrayList<String>();
					mFonts.add(cursor.getString(cursor
							.getColumnIndex("sFont")));
					tInfo.setmFonts(mFonts);
					
					//字体大小
					ArrayList<Integer> mSize=new ArrayList<Integer>();
					mSize.add(cursor.getInt(cursor.getColumnIndex("nSize")));
					tInfo.setmSize(mSize);
					
					//位置和闪烁
					ArrayList<Integer> mAligns=new ArrayList<Integer>();
					mAligns.add(cursor.getInt(cursor.getColumnIndex("nShowProp")));
					tInfo.setmStyle(mAligns);
					
					ArrayList<Integer> mColors=new ArrayList<Integer>();
					mColors.add(cursor.getInt(cursor.getColumnIndex("nColor")));
					tInfo.setmColors(mColors);
					
					//文本
					ArrayList<String> mTexts=new ArrayList<String>();
					mTexts.add(cursor.getString(cursor.getColumnIndex("sText")));
					tInfo.setmTextList(mTexts);
					
					index=info.getmTextList().size();
					info.getmTextList().add(tInfo);
					
				}else {
					TextInfo tinfo=info.getmTextList().get(index);
					
					ArrayList<String> mFonts=tinfo.getmFonts();
					if (mFonts!=null) {
						mFonts.add(cursor.getString(cursor
								.getColumnIndex("sFont")));
					}
					
					ArrayList<Integer> mSize=tinfo.getmSize();
					if (mSize!=null) {
						mSize.add(cursor.getInt(cursor.getColumnIndex("nSize")));
					}
					
					ArrayList<Integer> mStyle=tinfo.getmStyle();
					if (mStyle!=null) {
						mStyle.add(cursor.getInt(cursor.getColumnIndex("nShowProp")));
					}
					
					ArrayList<Integer> mColor=tinfo.getmColors();
					if (mColor!=null) {
						mColor.add(cursor.getInt(cursor.getColumnIndex("nColor")));
					}
					
					ArrayList<String> mTexts=tinfo.getmTextList();
					if (mTexts!=null) {
						mTexts.add(cursor.getString(cursor.getColumnIndex("sText")));
					}
				}
				
			}
		}
		close(cursor);
	}

	
	/**
	 * 多功能开关
	 */
	public ArrayList<FunSwitchInfo> getFunSwitch(int sid,Context context){
		
		ArrayList<FunSwitchInfo> list=new ArrayList<FunSwitchInfo>();
		
		String id = "";
		String sFunId="";
		boolean init = true;
		Cursor cursor = null;
		String sql = "select * from MFbtn where nSceneId="+sid;
		if (db!=null) {
			cursor = db.getDatabaseBySql(sql,null);
			if (cursor!=null) {
				while (cursor.moveToNext()) {
					FunSwitchInfo info=new FunSwitchInfo();
					info.setnItemId(cursor.getInt(cursor.getColumnIndex("nItemId")));
					info.setnLp(cursor.getShort(cursor.getColumnIndex("nStartX")));
					info.setnTp(cursor.getShort(cursor.getColumnIndex("nStartY")));
					info.setnWidth(cursor.getShort(cursor.getColumnIndex("nWidth")));
					info.setnHeight(cursor.getShort(cursor.getColumnIndex("nHeight")));
					info.seteFlickType(IntToEnum.getFlickType(cursor.getShort(cursor.getColumnIndex("eflick"))));
					info.setsApeaPath(cursor.getString(cursor.getColumnIndex("sPath")));
					info.setnAlpha(cursor.getInt(cursor.getColumnIndex("nAlpha")));
					info.setnColor(cursor.getInt(cursor.getColumnIndex("nColor")));
					info.setnShapeType(cursor.getShort(cursor.getColumnIndex("eLib")));
					info.setnZvalue(cursor.getInt(cursor.getColumnIndex("nZvalue")));
					info.setnCollidindId(cursor.getInt(cursor.getColumnIndex("nCollidindId")));
					info.setmShowInfo(TouchShowInfoBiz.getShowInfoById(info.getnItemId()));
					info.setmTouchInfo(TouchShowInfoBiz.getTouchInfoById(info.getnItemId()));
					String temp=cursor.getString(cursor.getColumnIndex("bIsStartStatement"));
					boolean bb=false;
					if (temp!=null) {
						bb=temp.equals("true") ? true: false;
					}
					info.setbIsStartStatement(bb);
					info.setnStatementId(cursor.getShort(cursor.getColumnIndex("nScriptId")));
					
					list.add(info);
					if (init) {
						sFunId+=info.getnItemId();
						id += " nItemId=" + info.getnItemId();
						init = false;
					} else {
						sFunId+=" ,"+ info.getnItemId();
						id += " or nItemId=" + info.getnItemId();
					}
					
					//新加的数据
					info.seteWatchType(BUTTON.getWatchType(cursor.getShort(cursor
							.getColumnIndex("eWatchType"))));
					info.setmWatchAddress(AddrPropBiz.selectById(cursor.getShort(cursor
							.getColumnIndex("nWatchAddr"))));
					boolean bType=cursor.getString(cursor.getColumnIndex("nAddrType")).equals("true")?true:false;
					info.setbAddrType(bType);
					info.setnBitIndex(cursor.getShort(cursor.getColumnIndex("nBitIndex")));
					info.seteWatchDataType(IntToEnum.getDataType(
							cursor.getShort(cursor.getColumnIndex("eWatchDataType"))));
					info.setnCondition(cursor.getShort(cursor.getColumnIndex("nCondition")));
				}
			}
			close(cursor);
			if (list.size()>0) {
				addFun(list, context,id,sFunId,sid);
				setText(list,id);
				stateSelectFun(list,id);
				//textSelect(list,id);
			}

		}
		return list;
	}
	

	/**
	 * 开关状态 itemId-控件id
	 */
	private void stateSelectFun(ArrayList<FunSwitchInfo> list,String id) {
		
		Cursor cursor = null;
		String sql = "select * from switchStatusProp where "+id;
		cursor = db.getDatabaseBySql(sql, null);
		if (cursor != null) {
			FunSwitchInfo info=null;
			int itemId=-1;
			int index=0;
			while (cursor.moveToNext()) {
				if (itemId!=cursor.getInt(cursor.getColumnIndex("nItemId"))) {
					itemId=cursor.getInt(cursor.getColumnIndex("nItemId"));
					if (list.get(index).getnItemId()==itemId) {
						info=list.get(index);
						ArrayList<StakeoutInfo> mStakeoutList = new ArrayList<StakeoutInfo>();
						info.setmStakeoutList(mStakeoutList);
					}else {
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getnItemId()==itemId) {
								info=list.get(i);
								ArrayList<StakeoutInfo> mStakeoutList = new ArrayList<StakeoutInfo>();
								info.setmStakeoutList(mStakeoutList);
								break;
							}
						}
					}
					index++;
					
				}
				short stateId = cursor.getShort(cursor
						.getColumnIndex("nStatusIndex"));
				
				StakeoutInfo sInfo = new StakeoutInfo();
				sInfo.setnStatusId(stateId);
				sInfo.setnCmpFactor((short) cursor.getDouble(cursor
						.getColumnIndex("statusValue")));
				sInfo.setnStatusId(stateId);
				sInfo.seteFlickType(IntToEnum.getFlickType(cursor.getInt(cursor.getColumnIndex("eflick"))));
				sInfo.setnAlpha(cursor.getInt(cursor.getColumnIndex("nAlpha")));
				sInfo.setnColor(cursor.getInt(cursor.getColumnIndex("nColor")));
				sInfo.setnShapeType(cursor.getShort(cursor.getColumnIndex("eLib")));
				sInfo.setsPath(cursor.getString(cursor
						.getColumnIndex("sPath")));
				info.getmStakeoutList().add(sInfo);
			}
			
		}
		close(cursor);
	}
	
	/**
	 * 添加功能
	 */
	private HashMap<BUTTON_TYPE, ArrayList<FunInfo>> mFunList=null;
	private void addFun(ArrayList<FunSwitchInfo> list,Context context,String id,String fid,int sid){
		if (mFunList==null) {
			mFunList=new HashMap<BUTTON_TYPE, ArrayList<FunInfo>>();
		}else {
			mFunList.clear();
		}
		
		if (db!=null) {
			Cursor cursor = null;
			String sql = "select * from comboboxFun where "+id;
			cursor = db.getDatabaseBySql(sql, null);
			FunSwitchInfo info=null;
			int nItemId=-1;
			if (cursor!=null) {
				int funId;
				while (cursor.moveToNext()) {
					if (nItemId != cursor.getInt(cursor.getColumnIndex("nItemId"))) {
						nItemId = cursor.getInt(cursor.getColumnIndex("nItemId"));
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getnItemId() == nItemId) {
								info = list.get(i);
								ArrayList<SKButton> alist=new ArrayList<SKButton>();
								info.setmSkButtons(alist);
								break;
							}
						}

					}
					funId=cursor.getInt(cursor.getColumnIndex("nFunctionId"));
					type = IntToEnum.getButtonType(cursor.getShort(cursor.getColumnIndex("eFunctionType")));

					SKButton button=new SKButton(sid,funId, type);
				    info.getmSkButtons().add(button);
				    if (mFunList.containsKey(type)) {
				    	ArrayList<FunInfo> flist=mFunList.get(type);
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
				    	mFunList.put(type, flist);
					}
				}
				close(cursor);
				selectFunById(list,fid);
				
			}
		}
	}
	
	private void selectFunById(ArrayList<FunSwitchInfo> list,String fid){
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
	 * 设置文本
	 */
	private void setText(ArrayList<FunSwitchInfo> list,String id){
		Cursor cursor = null;
		String sql = "select * from textProp where "+id;
		cursor = db.getDatabaseBySql(sql, null);
		if(cursor!=null){
			int state=-1;
			int index=0;
			int nItemId = -1;
			FunSwitchInfo info=null;
			while (cursor.moveToNext()) {
				if (nItemId != cursor.getInt(cursor.getColumnIndex("nItemId"))) {
					nItemId = cursor.getInt(cursor.getColumnIndex("nItemId"));
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getnItemId()== nItemId) {
							info = list.get(i);
							ArrayList<TextInfo> mTextList = new ArrayList<TextInfo>();
							info.setmTextList(mTextList);
							state=-1;
							index=0;
							break;
						}
					}

				}
				if (state!=cursor.getShort(cursor
						.getColumnIndex("nStatusIndex"))) {
					state=cursor.getShort(cursor
							.getColumnIndex("nStatusIndex"));
					TextInfo tInfo = new TextInfo();
					tInfo.setnStatusId( cursor.getShort(cursor
							.getColumnIndex("nStatusIndex")));
					tInfo.setnBColor(cursor.getInt(cursor.getColumnIndex("nColor")));
					tInfo.setnLangugeId(cursor.getShort(cursor
							.getColumnIndex("nLangIndex")));
					tInfo.seteFlickType(FLICK_TYPE.NO_FLICK);
					
					//字体类型
					ArrayList<String> mFonts=new ArrayList<String>();
					mFonts.add(cursor.getString(cursor
							.getColumnIndex("sFont")));
					tInfo.setmFonts(mFonts);
					
					//字体大小
					ArrayList<Integer> mSize=new ArrayList<Integer>();
					mSize.add(cursor.getInt(cursor.getColumnIndex("nSize")));
					tInfo.setmSize(mSize);
					
					//位置和闪烁
					ArrayList<Integer> mAligns=new ArrayList<Integer>();
					mAligns.add(cursor.getInt(cursor.getColumnIndex("nShowProp")));
					tInfo.setmStyle(mAligns);
					
					ArrayList<Integer> mColors=new ArrayList<Integer>();
					mColors.add(cursor.getInt(cursor.getColumnIndex("nColor")));
					tInfo.setmColors(mColors);
					
					//文本
					ArrayList<String> mTexts=new ArrayList<String>();
					mTexts.add(cursor.getString(cursor.getColumnIndex("sText")));
					tInfo.setmTextList(mTexts);
					
					index=info.getmTextList().size();
					info.getmTextList().add(tInfo);
				}else {
					TextInfo tinfo=info.getmTextList().get(index);
					
					ArrayList<String> mFonts=tinfo.getmFonts();
					if (mFonts!=null) {
						mFonts.add(cursor.getString(cursor
								.getColumnIndex("sFont")));
					}
					
					ArrayList<Integer> mSize=tinfo.getmSize();
					if (mSize!=null) {
						mSize.add(cursor.getInt(cursor.getColumnIndex("nSize")));
					}
					
					ArrayList<Integer> mStyle=tinfo.getmStyle();
					if (mStyle!=null) {
						mStyle.add(cursor.getInt(cursor.getColumnIndex("nShowProp")));
					}
					
					ArrayList<Integer> mColor=tinfo.getmColors();
					if (mColor!=null) {
						mColor.add(cursor.getInt(cursor.getColumnIndex("nColor")));
					}
					
					ArrayList<String> mTexts=tinfo.getmTextList();
					if (mTexts!=null) {
						mTexts.add(cursor.getString(cursor.getColumnIndex("sText")));
					}
				}
			}
		}
		close(cursor);
	}
}
