package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;

import android.database.Cursor;
import android.util.Log;
import com.android.Samkoonhmi.model.MessageInfo;
import com.android.Samkoonhmi.model.MsgTextInfo;
import com.android.Samkoonhmi.skenum.IntToEnum;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;

/**
 * 消息显示数据库操作
 * @author 刘伟江
 * 创建时间 2012-6-19
 */
public class MessageDisplayBiz extends DataBase {

	private SKDataBaseInterface db=null;
	public MessageDisplayBiz(){
		
	}
	
	/**
	 * 是否采用第一状态bStateZero
	 * nStateMessageId,sStateMessage,nStyle
	 * 字符 外形nShape 要改成String
	 */
	public ArrayList<MessageInfo> select(int sid){
		
		Cursor cursor=null;
		String id = "";
		boolean init = true;
		
		db = SkGlobalData.getProjectDatabase();
		if(db==null){
			return null;
		}
		
		ArrayList<MessageInfo> list=new ArrayList<MessageInfo>();
		String sql="select * from messageShow where nSceneId="+sid;
		cursor=db.getDatabaseBySql(sql, null);
		
		if (cursor!=null) {
			while (cursor.moveToNext()) {
				MessageInfo info=new MessageInfo();
				info.setnItemId(cursor.getInt(cursor.getColumnIndex("nItemId")));
				info.setnAlpha(cursor.getShort(cursor.getColumnIndex("nAlpha")));
				info.setnLeftTopX(cursor.getShort(cursor.getColumnIndex("nLeftTopX")));
				info.setnLeftTopY(cursor.getShort(cursor.getColumnIndex("nLeftTopY")));
				info.setnWidth(cursor.getShort(cursor.getColumnIndex("nWidth")));
				info.setnHeight(cursor.getShort(cursor.getColumnIndex("nHeight")));
				info.setnShowLeftTopX((short)(info.getnLeftTopX()+cursor.getShort(cursor.getColumnIndex("nShowLeftTopX"))));
				info.setnShowLeftTopY((short)(info.getnLeftTopY()+cursor.getShort(cursor.getColumnIndex("nShowLeftTopY"))));
				info.setnShowWidth(cursor.getShort(cursor.getColumnIndex("nShowWidth")));
				info.setnShowHeight(cursor.getShort(cursor.getColumnIndex("nShowHeight")));
				info.setsShape(cursor.getString(cursor.getColumnIndex("sShape")));
				info.setnStateCount(cursor.getShort(cursor.getColumnIndex("nStateCount")));
				//info.setnState(cursor.getShort(cursor.getColumnIndex("nState")));
				info.seteDataType(IntToEnum.getDataType(
						cursor.getShort(cursor.getColumnIndex("eDataType"))));
				info.seteAddress(AddrPropBiz.selectById(cursor.getInt(cursor.getColumnIndex("eAddress"))));
				info.setnStateCount(cursor.getShort(cursor.getColumnIndex("nStateCount")));
				boolean b=cursor.getString(cursor.getColumnIndex("bFirstLanguage")).equals("true")?true:false;
				info.setnZvalue(cursor.getInt(cursor.getColumnIndex("nZvalue")));
				info.setnCollidindId(cursor.getInt(cursor.getColumnIndex("nCollidindId")));
				info.setbFirstLanguage(b);
				info.setmShowInfo(TouchShowInfoBiz.getShowInfoById(info.getnItemId()));
				
				list.add(info);
				if (init) {
					id += " nItemId=" + info.getnItemId();
					init = false;
				} else {
					id += " or nItemId=" + info.getnItemId();
				}
			}
			close(cursor);
			
			if (list.size()>0) {
				selectText(list, id);
			}
		}
		return list;
	}
	
	/**
	 * 状态文本信息
	 * 
	 */
	private void selectText(ArrayList<MessageInfo> list,String id){
		Cursor cursor=null;
		db = SkGlobalData.getProjectDatabase();
		if(db==null){
			Log.e("MessageDisplayBiz", "ProjectDatabase=null");
			return;
		}
		String sql="select * from msgStatusProp where "+id;
		cursor=db.getDatabaseBySql(sql, null);
		if (cursor!=null) {
			short state=-1;
			int index=0;
			int nItemId=-1;
			MessageInfo info=null;
			while(cursor.moveToNext()){
				if (nItemId != cursor.getInt(cursor.getColumnIndex("nItemId"))) {
					nItemId = cursor.getInt(cursor.getColumnIndex("nItemId"));
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getnItemId() == nItemId) {
							info = list.get(i);
							ArrayList<MsgTextInfo> mTextList=new ArrayList<MsgTextInfo>();
							info.setmTextList(mTextList);
							state=-1;
							index=0;
							break;
						}
					}

				}
				if (state!=cursor.getShort(cursor.getColumnIndex("nStatusIndex"))) {
					MsgTextInfo tInfo=new MsgTextInfo();
					state=cursor.getShort(cursor.getColumnIndex("nStatusIndex"));
					tInfo.setnStatusId(state);
					tInfo.setnLangugeId(cursor.getShort(cursor.getColumnIndex("nLanguage")));
					//tInfo.setnColor(cursor.getInt(cursor.getColumnIndex("nFontColor")));
					tInfo.seteRemove(IntToEnum.getArrayOrder(
							cursor.getShort(cursor.getColumnIndex("eRemove"))));
					tInfo.setnSpeed(cursor.getShort(cursor.getColumnIndex("nSpeed")));
					tInfo.setnFrameColor(cursor.getInt(cursor.getColumnIndex("nFrameColor")));
					tInfo.setnForecolor(cursor.getInt(cursor.getColumnIndex("nForecolor")));
					tInfo.setnBackcolor(cursor.getInt(cursor.getColumnIndex("nBackcolor")));
					tInfo.setnStyle(cursor.getShort(cursor.getColumnIndex("nStyle")));
				
					//字体
					ArrayList<String> mFonts=new ArrayList<String>();
					mFonts.add(cursor.getString(cursor.getColumnIndex("sFontType")));
					tInfo.setmFonts(mFonts);
					
					//字体大小
					ArrayList<Integer> mSize=new ArrayList<Integer>();
					mSize.add(cursor.getInt(cursor.getColumnIndex("nFontSize")));
					tInfo.setmSize(mSize);
					
					//文本
					ArrayList<String> mTexts=new ArrayList<String>();
					mTexts.add(cursor.getString(cursor.getColumnIndex("sStateMessage")));
					tInfo.setmTextList(mTexts);
				
					//颜色
					ArrayList<Integer> mColors=new ArrayList<Integer>();
					mColors.add(cursor.getInt(cursor.getColumnIndex("nFontColor")));
					tInfo.setmColors(mColors);
					
					index=info.getmTextList().size();
					info.getmTextList().add(tInfo);
				}else {
					MsgTextInfo tinfo=info.getmTextList().get(index);
					
					//字体
					ArrayList<String> mFonts=tinfo.getmFonts();
					if (mFonts!=null) {
						mFonts.add(cursor.getString(cursor.getColumnIndex("sFontType")));
					}
					
					//字体大小
					ArrayList<Integer> mSize=tinfo.getmSize();
					if (mSize!=null) {
						mSize.add(cursor.getInt(cursor.getColumnIndex("nFontSize")));
					}
					
					//文本
					ArrayList<String> mTexts=tinfo.getmTextList();
					if (mTexts!=null) {
						mTexts.add(cursor.getString(cursor.getColumnIndex("sStateMessage")));
					}
					
					//颜色
					ArrayList<Integer> mColors=tinfo.getmColors();
					if (mColors!=null) {
						mColors.add(cursor.getInt(cursor.getColumnIndex("nFontColor")));
					}
				}
			}
			close(cursor);
		}
	}
}
