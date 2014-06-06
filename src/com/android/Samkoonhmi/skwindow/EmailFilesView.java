package com.android.Samkoonhmi.skwindow;

import java.util.ArrayList;
import java.util.Vector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.adapter.AlarmDateAdapter;
import com.android.Samkoonhmi.adapter.AlarmEmailAdapter;
import com.android.Samkoonhmi.adapter.AlarmEmailAdapter.Email_AlarmBean;
import com.android.Samkoonhmi.model.RecipeOGprop;
import com.android.Samkoonhmi.model.alarm.AlarmGroupInfo;
import com.android.Samkoonhmi.model.skglobalcmn.CollectDataInfo;
import com.android.Samkoonhmi.model.skglobalcmn.HistoryDataCollect;
import com.android.Samkoonhmi.skglobalcmn.RecipeDataCentre;
import com.android.Samkoonhmi.skwindow.EmailOperDialog.IClickListener;
import com.android.Samkoonhmi.util.AlarmGroup;
import com.android.Samkoonhmi.util.MessageBoardUtil;

public class EmailFilesView {
	
	private View view;
	private Context mContext;
	private IClickListener mIClickListener = null;
	private TabHost nTabHost = null;
	private AlarmEmailAdapter mAlarmEmailAdapter = null ; //报警适配器
	private AlarmDateAdapter mRecipeAdapter = null;   // 配方适配器
	private AlarmDateAdapter mHistoryAdapter = null;  //历史采集数据 
	private CheckBox mMessageCheck = null;
	
	public EmailFilesView(Context context, IClickListener i){
		mIClickListener = i;
		mContext = context;
		
		//历史报警数据
		ArrayList<AlarmGroupInfo> alarmList = AlarmGroup.getInstance().getAlarmGroupList();
		if (alarmList!= null && alarmList.size() > 0 ) {
			mAlarmEmailAdapter = new AlarmEmailAdapter(context, alarmList);
		}
		
		//配方数据
		Vector<RecipeOGprop> recipelist=RecipeDataCentre.getInstance().getRecipeDataProp().getmRecipeGroupList();
		if (recipelist != null && recipelist.size() > 0) {
			mRecipeAdapter = new AlarmDateAdapter(context);
			mRecipeAdapter.setRecipeList(recipelist);
		}
		
		//历史采集数据
		Vector<HistoryDataCollect> hislist=CollectDataInfo.getInstance().getmHistoryInfoList();
		if (hislist != null && hislist.size() > 0) {
			mHistoryAdapter = new AlarmDateAdapter(context);
			mHistoryAdapter.setHistoryList(hislist);
		}
	}
	
	public View addView( ){
		view = LayoutInflater.from(mContext).inflate(R.layout.email_addfiles, null);
		nTabHost = (TabHost) view.findViewById(R.id.email_tabhost);
		nTabHost.setup();
		
		//报警tab
		View alarmview = LayoutInflater.from(mContext).inflate(R.layout.email_tab_item, null);
		alarmview.setBackgroundResource(R.drawable.email_alarm);
		((TextView)alarmview.findViewById(R.id.tab_item_text)).setText(R.string.email_alarm);
		nTabHost.addTab(nTabHost.newTabSpec("tab1").setIndicator(alarmview).setContent(R.id.email_alarm));
		
		//配方tab
		View recipeview = LayoutInflater.from(mContext).inflate(R.layout.email_tab_item, null);
		recipeview.setBackgroundResource(R.drawable.email_recipe);
		((TextView)recipeview.findViewById(R.id.tab_item_text)).setText(R.string.email_recipe);
		nTabHost.addTab(nTabHost.newTabSpec("tab2").setIndicator(recipeview).setContent(R.id.email_recipe));
		
		//历史tab
		View historyview = LayoutInflater.from(mContext).inflate(R.layout.email_tab_item, null);
		historyview.setBackgroundResource(R.drawable.email_history);
		((TextView)historyview.findViewById(R.id.tab_item_text)).setText(R.string.email_history);
		nTabHost.addTab(nTabHost.newTabSpec("tab3").setIndicator(historyview).setContent(R.id.email_history));
		
		//留言tab
		View messageView = LayoutInflater.from(mContext).inflate(R.layout.email_tab_item, null);
		messageView.setBackgroundResource(R.drawable.email_message);
		((TextView)messageView.findViewById(R.id.tab_item_text)).setText(R.string.email_message);
		nTabHost.addTab(nTabHost.newTabSpec("tab4").setIndicator(messageView).setContent(R.id.email_message));
		
		view.findViewById(R.id.setemail_sure).setOnClickListener(mListener);
		view.findViewById(R.id.setemail_cancel).setOnClickListener(mListener);
		
		initChildView();
		return view;
	}
	
	private void initChildView(){
		//报警TAB
		View view1 = nTabHost.getChildAt(0);
		ListView alarmView = (ListView) view1.findViewById(R.id.email_alarm_content);
		if (mAlarmEmailAdapter != null) {
			view1.findViewById(R.id.email_alarm_content).setVisibility(View.VISIBLE);
			view1.findViewById(R.id.email_alarm_nodata).setVisibility(View.GONE);
			
			alarmView.setAdapter(mAlarmEmailAdapter);
		}
		else {
			view1.findViewById(R.id.email_alarm_content).setVisibility(View.GONE);
			view1.findViewById(R.id.email_alarm_nodata).setVisibility(View.VISIBLE);
		}
		
		//配方TAB
		ListView recipeView = (ListView) view1.findViewById(R.id.email_recipe_content);
		if (mRecipeAdapter != null) {
			view1.findViewById(R.id.email_recipe_content).setVisibility(View.VISIBLE);
			view1.findViewById(R.id.email_recipe_nodata).setVisibility(View.GONE);
			
			recipeView.setAdapter(mRecipeAdapter);
		}
		else {
			view1.findViewById(R.id.email_recipe_content).setVisibility(View.GONE);
			view1.findViewById(R.id.email_recipe_nodata).setVisibility(View.VISIBLE);
		}
		
		//历史TAB
		ListView historyView = (ListView) view1.findViewById(R.id.email_history_content);
		if (mHistoryAdapter != null) {
			view1.findViewById(R.id.email_history_content).setVisibility(View.VISIBLE);
			view1.findViewById(R.id.email_history_nodata).setVisibility(View.GONE);
			
			historyView.setAdapter(mHistoryAdapter);
		}
		else {
			view1.findViewById(R.id.email_history_content).setVisibility(View.GONE);
			view1.findViewById(R.id.email_history_nodata).setVisibility(View.VISIBLE);
		}
		
		//留言信息
		mMessageCheck = (CheckBox) view1.findViewById(R.id.email_message_check);
		if (MessageBoardUtil.getInstance().hasMessage()) {
			view1.findViewById(R.id.email_message_content).setVisibility(View.VISIBLE);
			view1.findViewById(R.id.email_message_nodata).setVisibility(View.GONE);
			mMessageCheck.setVisibility(View.VISIBLE);
		}
		else{
			view1.findViewById(R.id.email_message_content).setVisibility(View.GONE);
			view1.findViewById(R.id.email_message_nodata).setVisibility(View.VISIBLE);
			mMessageCheck.setVisibility(View.GONE);
		}
		
	}
	
	
	private OnClickListener mListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v.getId() == R.id.setemail_sure) {
				getAlarmSelectList();
				mIClickListener.onJumpTo(EmailOperDialog.DIALOG_MAINVIEW, null);
			}
			else if(v.getId() == R.id.setemail_cancel){
				mIClickListener.onJumpTo(EmailOperDialog.DIALOG_MAINVIEW, null);
			}
			
		}
	};
	
	
	/**
	 * 获取选中的报警
	 * @return
	 */
	public ArrayList<Email_AlarmBean> getAlarmSelectList(){
		if (mAlarmEmailAdapter != null) {
			return mAlarmEmailAdapter.getSelectArray();
		}
		return null;
	}

	
	/**
	 * 获取脚本选中报警
	 * @param inputList - 脚本选中列表
	 * @return
	 */
	public static ArrayList<Email_AlarmBean> getScriptAlarm(ArrayList<String> inputList){
		
		ArrayList<Email_AlarmBean> list = new ArrayList<Email_AlarmBean>();
		ArrayList<AlarmGroupInfo> alarmList = AlarmGroup.getInstance().getAlarmGroupList();
		if(alarmList != null && alarmList.size() > 0){
			for(AlarmGroupInfo bean: alarmList){
				
				if(inputList.contains(bean.getsName())){
					Email_AlarmBean emailBean = new Email_AlarmBean();
					emailBean.nGroupId = bean.getnGroupId();
					emailBean.nGroupName = bean.getsName();
					emailBean.nStartTime = 0;
					emailBean.nEndTime = 0;
					
					list.add(emailBean);
				}
			}
		}
		
		return list;
	}
	
	

	
	/**
	 * 获取选中的配方
	 * @return
	 */
	public ArrayList<Integer> getRecipeSelectList(){
		if (mRecipeAdapter != null) {
			return mRecipeAdapter.getSelectRecipeList();
		}
		return null;
	}
	

	/**
	 * 获取脚本选中的配方
	 * @param inputList - 脚本选中列表
	 * @return
	 */
	public static  ArrayList<Integer> getScriptRecipe(ArrayList<String> inputList){
		//配方数据
		ArrayList<Integer> list = new ArrayList<Integer>();
		Vector<RecipeOGprop> recipelist=RecipeDataCentre.getInstance().getRecipeDataProp().getmRecipeGroupList();
		if( recipelist != null  &&  recipelist.size() > 0){
			for(RecipeOGprop bean : recipelist){
				
				if(inputList.contains(bean.getsRecipeGName())){
					list.add(bean.getnGRecipeID());
				}
			}
		}
		
		return list;
	}
	
	
	
	
	/**
	 * 获取选中的历史数据
	 * @return
	 */
	public ArrayList<Integer> getHistorySelectList(){
		if (mHistoryAdapter != null) {
			return mHistoryAdapter.getHistoryList();
		}
		return null;
	}
	

	/**
	 * 获取脚本选中的历史数据
	 * @param inputList  -脚本选中的历史数据名称
	 * @return
	 */
	public static ArrayList<Integer> getScriptHistory(ArrayList<String> inputList){
		ArrayList<Integer> list = new ArrayList<Integer>();
		Vector<HistoryDataCollect> hislist=CollectDataInfo.getInstance().getmHistoryInfoList();
		if(hislist != null && hislist.size() > 0){
			for(HistoryDataCollect bean : hislist){
				
				if(inputList.contains(bean.getsName())){
					list.add((int)bean.getnGroupId());
				}
			}
		}
		
		return list;
	}
	
	public boolean haveMessages(){
		return MessageBoardUtil.getInstance().hasMessage() && mMessageCheck.isChecked();
	}
	
}
