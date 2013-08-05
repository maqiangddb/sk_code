package com.android.Samkoonhmi.skwindow;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.adapter.AlarmDateAdapter;
import com.android.Samkoonhmi.adapter.AlarmDateAdapter.ViewHolder;
import com.android.Samkoonhmi.adapter.CollectAdapter;
import com.android.Samkoonhmi.adapter.CollectAdapter.HolerView;
import com.android.Samkoonhmi.adapter.ComboxListAdapt;
import com.android.Samkoonhmi.adapter.DateSettingAdapter;
import com.android.Samkoonhmi.databaseinterface.DBTool;
import com.android.Samkoonhmi.databaseinterface.SystemInfoBiz;
import com.android.Samkoonhmi.model.MessageDetailInfo;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.model.alarm.AlarmGroupInfo;
import com.android.Samkoonhmi.model.sk_historytrends.CollectItem;
import com.android.Samkoonhmi.model.skglobalcmn.CollectDataInfo;
import com.android.Samkoonhmi.model.skglobalcmn.HistoryDataCollect;
import com.android.Samkoonhmi.skglobalcmn.DataCollect;
import com.android.Samkoonhmi.util.AlarmGroup;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class DateTimeSetting  extends Dialog implements OnClickListener {

	// 日期设置
	public static final int DATE_PICKER_ID = 0;
	// 时间设置
	public static final int TIME_PICKER_ID = 1;
	// 日期和时间
	public static final int DATA_TIME = 2;

	public boolean showFlag = false;
	private Context mContext;
	private View view;
	private LayoutInflater inflater;
	private ListView listView;
	private ArrayList<Item> items = null;
	//private PopupWindow mPopupWindow;
	private DateSettingAdapter adapter;
	private Button btnClose;
	private Button btnCancel;
	private Button btnOk;
	private TextView btnPre;
	private TextView btnNext;
	private EditText mEditText;
	private TextView mStartTime;// 开始时间
	private TextView mEndTime;// 结束时间
	private TextView mStartDate;// 开始日期
	private TextView mEndDate;// 结束日期
	private Button btnStartTime;// 开始时间点击按钮
	private Button btnEndTime;// 结束时间点击按钮
	private Button btnStartDate;// 开始日期点击按钮
	private Button btnEndDate;// 结束日期点击按钮
	private CheckBox cbxAll;//显示所有数据
	private CheckBox cbxStart;//显示开始日期和时间
	private CheckBox cbxEnd;//显示结束日期和时间
	private boolean bShowALl;
	private boolean bShowStart;
	private boolean bShowEnd;
	private LinearLayout mLayoutSetting;//显示所有
	private LinearLayout mLayoutStartDate;//显示开始日期
	private LinearLayout mLayoutStartTime;//显示开始时间
	private LinearLayout mLayoutEndDate;//显示结束日期
	private LinearLayout mLayoutEndTime;//显示结束时间
	private TextView mTextStart;
	private TextView mTextEnd;
	private TYPE mType;
	private String sSetType = "";// 设置类型。开始or结束
	private IDateCallback iDateCallback;
	private IMessageAddCallBack iMessageAddCallBack;
	private IMessageEditCallBack iMessageEditCallBack;
	private String sDate = "";
	private String eDate = "";
	private String sTime = "";
	private String eTime = "";
//	private EditText message_title; // 留言标题
	private EditText message_content;// 留言内容
	private Button message_sure;// 留言新增确定按钮
	private Button message_cancle;// 留言新增取消按钮
//	private EditText message_edit_title; // 修改留言标题
	private EditText message_edit_content;// 修改留言内容
	private Button message_edit_sure;// 留言修改确定按钮
	private Button message_edit_cancle;// 留言修改取消按钮
	private TextView contentTex;// 查看留言信息
	private Button message_see_cancle;// 查看留言信息取消
	private MessageDetailInfo messageDetailInfo;
	private int messageBoardId;
	private Window window = null;
	private String HistoryShowId="";
	private CollectAdapter mAdapter;
	private ArrayList<CollectItem> data;
	private AlarmDateAdapter alarmAdapter;
	private BroadcastReceiver TimeTickReceiver;
	
	public String getHistoryShowId() {
		return HistoryShowId;
	}

	public void setHistoryShowId(String historyShowId) {
		HistoryShowId = historyShowId;
	}

	public int getMessageBoardId() {
		return messageBoardId;
	}

	public void setMessageBoardId(int messageBoardId) {
		this.messageBoardId = messageBoardId;
	}

	public MessageDetailInfo getMessageDetailInfo() {
		return messageDetailInfo;
	}

	public void setMessageDetailInfo(MessageDetailInfo messageDetailInfo) {
		this.messageDetailInfo = messageDetailInfo;
	}

	public DateTimeSetting(Context context) {
		super(context, R.style.custom_dialog_style);
		this.mContext = context;
		this.showFlag = false;
		inflater = LayoutInflater.from(context);
		init();
	}


	public void onCreate(TYPE type, int width, int height) {

		this.mType = type;
		switch (type) {
		case ADD_MESSAGE: {
			// 添加留言信息
			view = inflater.inflate(R.layout.add_message, null);
//			message_title = (EditText) view.findViewById(R.id.add_title);
			message_content = (EditText) view.findViewById(R.id.add_content);
			message_sure = (Button) view.findViewById(R.id.btn_add_message);
			message_cancle = (Button) view
					.findViewById(R.id.btn_cancel_message);
			message_sure.setOnClickListener(this);
			message_cancle.setOnClickListener(this);

			break;
		}
		case EDIT_MESSAGE: {
			// 修改留言信息
			view = inflater.inflate(R.layout.edit_message, null);
			// message_edit_title = (EditText)
			// view.findViewById(R.id.edit_title);
			message_edit_content = (EditText) view
					.findViewById(R.id.edit_content);
			// message_edit_title.setText(getMessageDetailInfo().getsTitle());
			if (null != getMessageDetailInfo()) {
				message_edit_content.setText(getMessageDetailInfo()
						.getsMessage());
			}
			message_edit_sure = (Button) view
					.findViewById(R.id.btn_sure_editmessage);
			message_edit_cancle = (Button) view
					.findViewById(R.id.btn_cancel_editmessage);
			message_edit_sure.setOnClickListener(this);
			message_edit_cancle.setOnClickListener(this);
			break;
		}
		case SEE_MESSAGE: {
			//查看留言信息
			view = inflater.inflate(R.layout.see_message, null);
			contentTex = (TextView) view.findViewById(R.id.see_content);
			message_see_cancle = (Button) view
					.findViewById(R.id.btn_cancel_seemessage);
			message_see_cancle.setOnClickListener(this);
			if (null != getMessageDetailInfo()) {
				contentTex.setText(getMessageDetailInfo().getsMessage());
			}
			break;
		}
		case SYSTEM_TIME: {
			// 系统时间设置
			view = inflater.inflate(R.layout.date_setting, null);
			listView = (ListView) view.findViewById(R.id.date_setting_list);
			adapter = new DateSettingAdapter(mContext, items);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(onItemClickListener);
			btnClose = (Button) view.findViewById(R.id.btn_close);
			btnClose.setOnClickListener(this);
			TimeTickReceiver = new  BroadcastReceiver(){
					@Override
					public void onReceive(Context context, Intent intent) {
						// TODO Auto-generated method stub
						if (intent.getAction() == Intent.ACTION_TIME_TICK) {
							items.get(1).sContent = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" + Calendar.getInstance().get(Calendar.MINUTE);
							adapter.notifyDataSetChanged();
						}
					}
				};
			IntentFilter mFilter = new IntentFilter();
			mFilter.addAction(Intent.ACTION_TIME_TICK); // 更新分钟的广播
			mContext.registerReceiver(TimeTickReceiver, mFilter);
			break;
		}
		case SCENE_TIME: {
			// 屏保时间设置
			view = inflater.inflate(R.layout.scene_time_setting, null);
			btnCancel = (Button) view.findViewById(R.id.btn_cancel);
			btnOk = (Button) view.findViewById(R.id.btn_ok);
			btnPre = (TextView) view.findViewById(R.id.btn_pre);
			btnNext = (TextView) view.findViewById(R.id.btn_next);
			mEditText = (EditText) view.findViewById(R.id.scene_time);
			mEditText.setText(SystemInfo.getnScreenTime() + "");
			btnCancel.setOnClickListener(this);
			btnOk.setOnClickListener(this);
			btnPre.setOnClickListener(this);
			btnNext.setOnClickListener(this);
			break;
		}
		case HISTORY_TIME: {

			// 获取用户设置
			getDateAndTime();

			// 历史数据显示器时间设置
			view = inflater.inflate(R.layout.history_time, null);
			btnCancel = (Button) view.findViewById(R.id.btn_cancel);
			btnOk = (Button) view.findViewById(R.id.btn_ok);

			
			cbxAll=(CheckBox)view.findViewById(R.id.cbx_all);
			cbxStart=(CheckBox)view.findViewById(R.id.cbx_start);
			cbxEnd=(CheckBox)view.findViewById(R.id.cbx_end);
			
			mTextStart=(TextView)view.findViewById(R.id.txt_hint_start);
			mTextEnd=(TextView)view.findViewById(R.id.txt_hint_end);
			
			mTextStart.setVisibility(View.GONE);
			mTextEnd.setVisibility(View.GONE);
			
			mLayoutSetting=(LinearLayout)view.findViewById(R.id.layout_setting);
			mLayoutStartDate=(LinearLayout)view.findViewById(R.id.layout_start_date);
			mLayoutStartTime=(LinearLayout)view.findViewById(R.id.layout_start_time);
			mLayoutEndDate=(LinearLayout)view.findViewById(R.id.layout_end_date);
			mLayoutEndTime=(LinearLayout)view.findViewById(R.id.layout_end_time);
			
			// 开始时间
			mStartTime = (TextView) view.findViewById(R.id.time_start);
			mStartTime.setText(sTime);
			// 结束时间
			mEndTime = (TextView) view.findViewById(R.id.time_end);
			mEndTime.setText(eTime);

			// 开始日期
			mStartDate = (TextView) view.findViewById(R.id.date_start);
			mStartDate.setText(sDate);
			// 结束日期
			mEndDate = (TextView) view.findViewById(R.id.date_end);
			mEndDate.setText(eDate);

			btnStartTime = (Button) view.findViewById(R.id.btn_start);
			btnEndTime = (Button) view.findViewById(R.id.btn_end);

			btnStartDate = (Button) view.findViewById(R.id.btn_start_date);
			btnEndDate = (Button) view.findViewById(R.id.btn_end_date);

			
			cbxAll.setOnClickListener(check);
			cbxStart.setOnClickListener(check);
			cbxEnd.setOnClickListener(check);
			
			btnStartTime.setOnClickListener(this);
			btnEndTime.setOnClickListener(this);
			btnStartDate.setOnClickListener(this);
			btnEndDate.setOnClickListener(this);

			btnCancel.setOnClickListener(this);
			btnOk.setOnClickListener(this);
			
			if (bShowALl) {
				cbxAll.setChecked(true);
				cbxStart.setChecked(false);
				cbxEnd.setChecked(false);
				cbxStart.setEnabled(false);
				cbxEnd.setEnabled(false);
				
				mLayoutStartDate.setVisibility(View.GONE);
				mLayoutStartTime.setVisibility(View.GONE);
				mTextStart.setVisibility(View.VISIBLE);
				
				mLayoutEndDate.setVisibility(View.GONE);
				mLayoutEndTime.setVisibility(View.GONE);
				mTextEnd.setVisibility(View.VISIBLE);
				
			}else {
				cbxStart.setEnabled(true);
				cbxEnd.setEnabled(true);
				cbxAll.setChecked(false);
				if (bShowStart) {
					cbxStart.setChecked(true);
					mLayoutStartDate.setVisibility(View.VISIBLE);
					mLayoutStartTime.setVisibility(View.VISIBLE);
					mTextStart.setVisibility(View.GONE);
				}else{
					cbxStart.setChecked(false);
					mLayoutStartDate.setVisibility(View.GONE);
					mLayoutStartTime.setVisibility(View.GONE);
					mTextStart.setVisibility(View.VISIBLE);
				}
				
				if (bShowEnd) {
					cbxEnd.setChecked(true);
					mLayoutEndDate.setVisibility(View.VISIBLE);
					mLayoutEndTime.setVisibility(View.VISIBLE);
					mTextEnd.setVisibility(View.GONE);
				}else {
					cbxEnd.setChecked(false);
					mLayoutEndDate.setVisibility(View.GONE);
					mLayoutEndTime.setVisibility(View.GONE);
					mTextEnd.setVisibility(View.VISIBLE);
				}
			}
		}
		break;
		
		case COLLECT_CLEAR:{
			//清除历史数据
			view = inflater.inflate(R.layout.collect_view, null);
			btnCancel = (Button) view.findViewById(R.id.btn_cancel);
			btnOk = (Button) view.findViewById(R.id.btn_ok);
			
			cbxAll=(CheckBox)view.findViewById(R.id.cbx_all);
			listView=(ListView)view.findViewById(R.id.list_gid);
			
			cbxAll.setOnCheckedChangeListener(checked);
			
			btnCancel.setOnClickListener(this);
			btnOk.setOnClickListener(this);
			
			Vector<HistoryDataCollect> list=CollectDataInfo.getInstance().getmHistoryInfoList();
			data=new ArrayList<CollectItem>();
			if(list!=null){
//				CollectItem items=new CollectItem();
//				items.nGId=-1;
//				items.sGName="清除历史数据";
//				items.isCheck=false;
				
				for (int i = 0; i < list.size(); i++) {
					CollectItem item=new CollectItem();
					item.nGId=list.get(i).getnGroupId();
					item.sGName=list.get(i).getsName();
					item.isCheck=false;
					data.add(item);
				}
			}
			
			mAdapter = new CollectAdapter(mContext,data);
			listView.setAdapter(mAdapter);
			mAdapter.notifyDataSetChanged();
			
		}
			break;
		case ALARM_CLEAR_HISTOTY:
		case ALARM_CLEAR:
		case ALARM_CONFIRM:
		{
			//清除历史报警数据
			ArrayList<AlarmGroupInfo> list = AlarmGroup.getInstance().getAlarmGroupList();
			if (list != null && list.size() > 0)
			{
				view = inflater.inflate(R.layout.collect_view, null);
				((TextView)view.findViewById(R.id.layout_topText)).setText(R.string.alarm_clear);
				btnCancel = (Button) view.findViewById(R.id.btn_cancel);
				btnOk = (Button) view.findViewById(R.id.btn_ok);	
				cbxAll=(CheckBox)view.findViewById(R.id.cbx_all);
				cbxAll.setOnCheckedChangeListener(checked);
				btnCancel.setOnClickListener(this);
				btnOk.setOnClickListener(this);
	
				listView=(ListView)view.findViewById(R.id.list_gid);
				alarmAdapter = new AlarmDateAdapter(list, mContext);
				listView.setAdapter(alarmAdapter);
			}
		}
		break;
		}
		
		view.setLayoutParams(new LayoutParams(width, height));
		setContentView(view);
	}
	
	OnCheckedChangeListener checked=new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (mType == TYPE.COLLECT_CLEAR)
			{
				if(data!=null)
				{
					for (int i = 0; i < data.size(); i++)
					{
						data.get(i).isCheck=isChecked;
					}
					mAdapter.notifyDataSetChanged();
				}
			}
			else if (mType == TYPE.ALARM_CLEAR_HISTOTY || mType == TYPE.ALARM_CLEAR || mType == TYPE.ALARM_CONFIRM) 
			{
				for(int i = 0; i < listView.getCount(); i++)
				{
					View view = listView.getChildAt(i);
					ViewHolder holder = (ViewHolder) view.getTag();
					holder.checkView.setChecked(isChecked);
				}
			}
			
		}
	};

	
	View.OnClickListener check=new View.OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v==cbxAll) {
				if (cbxAll.isChecked()) {
					mLayoutStartDate.setVisibility(View.GONE);
					mLayoutStartTime.setVisibility(View.GONE);
					mLayoutEndDate.setVisibility(View.GONE);
					mLayoutEndTime.setVisibility(View.GONE);
					
					mTextStart.setVisibility(View.VISIBLE);
					mTextEnd.setVisibility(View.VISIBLE);
					cbxStart.setEnabled(false);
					cbxEnd.setEnabled(false);
					
				}else {	
					
					mLayoutStartDate.setVisibility(View.VISIBLE);
					mLayoutStartTime.setVisibility(View.VISIBLE);
					mLayoutEndDate.setVisibility(View.VISIBLE);
					mLayoutEndTime.setVisibility(View.VISIBLE);
					
					mTextStart.setVisibility(View.GONE);
					mTextEnd.setVisibility(View.GONE);
					
					cbxStart.setChecked(true);
					cbxEnd.setChecked(true);
					cbxStart.setEnabled(true);
					cbxEnd.setEnabled(true);
				}
			}else if (v==cbxStart) {
				if (cbxStart.isChecked()) {
					mLayoutStartDate.setVisibility(View.VISIBLE);
					mLayoutStartTime.setVisibility(View.VISIBLE);
					mTextStart.setVisibility(View.GONE);
				}else {
					mLayoutStartDate.setVisibility(View.GONE);
					mLayoutStartTime.setVisibility(View.GONE);
					mTextStart.setVisibility(View.VISIBLE);
					if (!cbxEnd.isChecked()) {
						cbxAll.setChecked(true);
						cbxStart.setEnabled(false);
						cbxEnd.setEnabled(false);
					}
				}
			}else if (v==cbxEnd) {
				if (cbxEnd.isChecked()) {
					mLayoutEndDate.setVisibility(View.VISIBLE);
					mLayoutEndTime.setVisibility(View.VISIBLE);
					mTextEnd.setVisibility(View.GONE);
				}else {
					mLayoutEndDate.setVisibility(View.GONE);
					mLayoutEndTime.setVisibility(View.GONE);
					mTextEnd.setVisibility(View.VISIBLE);
					if (!cbxStart.isChecked()) {
						cbxAll.setChecked(true);
						cbxStart.setEnabled(false);
						cbxEnd.setEnabled(false);
					}
				}
			}
		}
		
	};

	@Override
	public void dismiss() {
		super.dismiss();
		showFlag=false;
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		super.cancel();
		showFlag=false;
	}

	public void showDialog(TYPE type, int x, int y, int width, int height) {
		if (showFlag == true) {
			return;
		}
		showFlag = true;
		window=getWindow();
		window.setWindowAnimations(R.style.PopupAnimation);
		WindowManager.LayoutParams lp=window.getAttributes();
		
		if (x==0&&y==0) {
			window.setGravity(Gravity.CENTER);
		}else {
			window.setGravity(Gravity.LEFT|Gravity.TOP);
			lp.x=x+SKSceneManage.getInstance().getX();
			lp.y=y+SKSceneManage.getInstance().getY();
		}
		lp.width=width;
		lp.height=height;
		window.setAttributes(lp);
		show();
		
	}

	public void closePopWindow(){
		showFlag=false;
		dismiss();
		cancel();
	}
	
	private SKDatePickerDialog mDateDialog = null;
	private SKTimePickerDialog mTimeDialog = null;

	private void onCreateDialog(int id) {
		switch (id) {
		case DATE_PICKER_ID: {
			// 日期
			final Calendar calendar = Calendar.getInstance();
			if (mDateDialog == null) {
				mDateDialog = new SKDatePickerDialog(SKSceneManage.getInstance()
						.getActivity(), onDateSetListener,
						calendar.get(Calendar.YEAR), 10, 10);
			}
			// 如果开始月份小于10，点击+月份大于10时，会显示不全，所有初始化时必须大于等于10，再更新
//			mDateDialog.getDatePicker().mMonthSpinner.setFormatter();
			mDateDialog.show();
			mDateDialog.updateDate(calendar.get(Calendar.YEAR), 10, 10);
			handler.sendEmptyMessageDelayed(DATE_PICKER_ID, 100);
			break;
		}
		case TIME_PICKER_ID: {
			// 时间
			final Calendar calendar = Calendar.getInstance();
			if (mTimeDialog == null) {
				mTimeDialog = new SKTimePickerDialog(SKSceneManage.getInstance()
						.getActivity(), onTimeSetListener,
						calendar.get(Calendar.HOUR_OF_DAY),
						calendar.get(Calendar.MINUTE),
						true);
			}else{
				mTimeDialog.getTimePicker().setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
				mTimeDialog.getTimePicker().setCurrentMinute(calendar.get(Calendar.MINUTE));
			}
			mTimeDialog.show();
			break;
		}
		}
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			final Calendar calendar = Calendar.getInstance();
			if (msg.what == DATE_PICKER_ID) {
				mDateDialog.updateDate(calendar.get(Calendar.YEAR),
						calendar.get(Calendar.MONTH),
						calendar.get(Calendar.DAY_OF_MONTH));
			}
		}

	};

	public void showDialog(int id) {
		onCreateDialog(id);
	}

	/**
	 * 初始化 日期和时间 item
	 */
	private void init() {
		Calendar calendar = Calendar.getInstance();
		items = new ArrayList<DateTimeSetting.Item>();
		Item item = new Item();
		item.sTitle = mContext.getString(R.string.setting_date);
		item.sContent = calendar.get(Calendar.YEAR) + "-"
				+ (calendar.get(Calendar.MONTH) + 1) + "-"
				+ calendar.get(Calendar.DAY_OF_MONTH);
		items.add(item);

		Item item1 = new Item();
		item1.sTitle = mContext.getString(R.string.setting_time);
		item1.sContent = calendar.get(Calendar.HOUR_OF_DAY) + ":"
				+ calendar.get(Calendar.MINUTE);
		items.add(item1);
	}

	/**
	 * 日期设置回调
	 */
	SKDatePickerDialog.OnDateSetListener onDateSetListener = new SKDatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			if (mType == TYPE.SYSTEM_TIME) {
				items.get(0).sContent = year + "-" + (monthOfYear + 1) + "-"
						+ dayOfMonth;
				adapter.notifyDataSetChanged();
				setDate(year, monthOfYear, dayOfMonth);
			} else if (mType == TYPE.HISTORY_TIME) {
				if (sSetType.equals("btnStartDate")) {
					mStartDate.setText(year + "-" + (monthOfYear + 1) + "-"
							+ dayOfMonth);
				} else if (sSetType.equals("btnEndDate")) {
					mEndDate.setText(year + "-" + (monthOfYear + 1) + "-"
							+ dayOfMonth);
				}
			}
		}
	};

	/**
	 * 时间设置回调
	 */
	SKTimePickerDialog.OnTimeSetListener onTimeSetListener = new SKTimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			if (mType == TYPE.SYSTEM_TIME) {
				items.get(1).sContent = hourOfDay + ":" + minute;
				adapter.notifyDataSetChanged();
				setTime(hourOfDay, minute);
			} else if (mType == TYPE.HISTORY_TIME) {
				String hour = hourOfDay + ":";
				String min = minute + "";
				if (hourOfDay < 10) {
					hour = "0" + hourOfDay + ":";
				}
				if (minute < 10) {
					min = "0" + minute;
				}
				if (sSetType.equals("btnStartTime")) {
					mStartTime.setText(hour + min);
				} else if (sSetType.equals("btnEndTime")) {
					mEndTime.setText(hour + min);
				}
			}
		}
	};

	/**
	 * 设置系统日期
	 */
	private void setDate(int year, int month, int day) {
		Calendar c = Calendar.getInstance();

		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		long when = c.getTimeInMillis();

		if (when / 1000 < Integer.MAX_VALUE) {
			// SystemClock.setCurrentTimeMillis(when);
			Intent intent = new Intent();
			intent.setAction("com.samkoon.settime");
			intent.putExtra("time", when);
			mContext.sendBroadcast(intent);
		}
	}

	/**
	 * 设置系统时间
	 */
	private void setTime(int hourOfDay, int minute) {
		Calendar c = Calendar.getInstance();

		c.set(Calendar.HOUR_OF_DAY, hourOfDay);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		long when = c.getTimeInMillis();

		if (when / 1000 < Integer.MAX_VALUE) {
			// SystemClock.setCurrentTimeMillis(when);
			Intent intent = new Intent();
			intent.setAction("com.samkoon.settime");
			intent.putExtra("time", when);
			mContext.sendBroadcast(intent);
		}
	}
	
	public enum TYPE {
		SCENE_TIME,    // 屏保时间
		SYSTEM_TIME,   // 系统时间
		HISTORY_TIME,  // 历史数据显示器时间
		ADD_MESSAGE,   // 添加留言信息
		EDIT_MESSAGE,  // 修改留言信息
		SEE_MESSAGE,   // 查看留言信息
		COLLECT_CLEAR, // 清除历史数据
		ALARM_CLEAR_HISTOTY,//清除历史报警数据
		ALARM_CLEAR,//报警清除
		ALARM_CONFIRM,//报警确定
	}

	/**
	 * 日期和时间设置 item
	 */
	public class Item {
		public String sTitle;
		public String sContent;
	}

	@Override
	public void onClick(View v) {
		
		SKSceneManage.getInstance().time=0;
		
		if (v == btnClose || v == btnCancel || v == message_cancle
				|| v == message_edit_cancle || v == message_see_cancle) {
			closePopWindow();
		} else if (v == btnOk) {
			if (mType == TYPE.SCENE_TIME) {
				SystemInfoBiz biz = new SystemInfoBiz();
				int time = Integer.parseInt(mEditText.getText().toString());
				SystemInfo.setnScreenTime(time);
				biz.updateScreenSaverTime(time);
			} else if (mType == TYPE.HISTORY_TIME) {
				if (iDateCallback != null) {
					sDate = mStartDate.getText().toString();
					eDate = mEndDate.getText().toString();
					sTime = mStartTime.getText().toString();
					eTime = mEndTime.getText().toString();

//					Log.d("SKScene", "sDate:" + sDate + ",eDate:" + eDate
//							+ ",sTime:" + sTime + ",eTime:" + eTime);
					SimpleDateFormat formatStr = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm");
					try {
						if (cbxAll.isChecked()) {
							sDate="1970-1-1";
							sTime="00:00:00";
							eDate="1970-1-1";
							eTime="24:00:00";
							cbxStart.setChecked(false);
							cbxEnd.setChecked(false);
							saveDateAndTime();// 保存用户设置
							iDateCallback.onChange(0, 0);
							
						}else {
							long startTime=0;
							long startEnd=0;
							if (cbxStart.isChecked()) {
								startTime = formatStr.parse(sDate + " " + sTime)
										.getTime();
							}
							
							if (cbxEnd.isChecked()) {
								startEnd = formatStr.parse(eDate + " " + eTime)
										.getTime();
							}
							
							if (cbxStart.isChecked()&&cbxEnd.isChecked()) {
								if (startEnd > startTime) {
									saveDateAndTime();// 保存用户设置
									iDateCallback.onChange(startTime, startEnd);
								} else {
									SKToast.makeText(mContext, R.string.setting_time_error,
											Toast.LENGTH_SHORT).show();
									return;
								}
							}else {
								saveDateAndTime();// 保存用户设置
								iDateCallback.onChange(startTime, startEnd);
							}
							
						}
						
					} catch (ParseException e) {
						e.printStackTrace();
						SKToast.makeText(mContext, R.string.time_error,
								Toast.LENGTH_SHORT).show();
						return;
					}
				}
			}else if (mType==TYPE.COLLECT_CLEAR) {
				//删除历史数据回调
				if(data!=null){
					ArrayList<Integer> list=new ArrayList<Integer>();
					for (int i = 0; i < data.size(); i++) {
						if (data.get(i).isCheck) {
							list.add(data.get(i).nGId);
						}
					}
					if (list.size()>0) {
						DataCollect.getInstance().msgClearAllHistory(list);
					}
				}
				
			}
			else if (mType == TYPE.ALARM_CLEAR_HISTOTY || mType == TYPE.ALARM_CLEAR || mType == TYPE.ALARM_CONFIRM)
			{
				ArrayList<Integer> delList = new ArrayList<Integer>();
				
				for(int i = 0; i < listView.getCount(); i++)
				{
					View itemView = listView.getChildAt(i);
					ViewHolder holder = (ViewHolder) itemView.getTag();
					if (holder.checkView.isChecked()) {
						delList.add(holder.nGroupId);
					}
				}
				
				if (delList.size() > 0) {
					if (mType == TYPE.ALARM_CLEAR_HISTOTY) {
						AlarmGroup.getInstance().clearHisData(delList);
					}
					else if(mType == TYPE.ALARM_CLEAR){
						AlarmGroup.getInstance().deleteAlarmData(delList);
					}
					else if (mType == TYPE.ALARM_CONFIRM) {
						AlarmGroup.getInstance().confirmAlarm(delList);
					}
				}
			}

			closePopWindow();
			// 设值
		} else if (v == btnPre) {
			String values = mEditText.getText().toString();
			int value = Integer.parseInt(values);
			if (value <= 1) {
				mEditText.setText("1");
			} else {
				mEditText.setText((value - 1) + "");
			}

		} else if (v == btnNext) {
			String values = mEditText.getText().toString();
			int value = Integer.parseInt(values);
			mEditText.setText((value + 1) + "");
		} else if (v == btnStartTime) {
			// 开始时间
			sSetType = "btnStartTime";
			showDialog(TIME_PICKER_ID);
		} else if (v == btnEndTime) {
			// 结束时间
			sSetType = "btnEndTime";
			showDialog(TIME_PICKER_ID);
		} else if (v == btnStartDate) {
			// 开始日期
			sSetType = "btnStartDate";
			showDialog(DATE_PICKER_ID);
		} else if (v == btnEndDate) {
			// 结束日期
			sSetType = "btnEndDate";
			showDialog(DATE_PICKER_ID);
		} else if (v == message_sure) {
			// 添加留言信息
			// String titleString = message_title.getText().toString();
			String contentString = message_content.getText().toString().trim();
			if(null == contentString || "".equals(contentString))
			{
				SKToast.makeText(SKSceneManage.getInstance().mContext, R.string.message_notnull, Toast.LENGTH_SHORT).show();
				return ;
			}else if(contentString.getBytes().length>600)
			{
				SKToast.makeText(SKSceneManage.getInstance().mContext, R.string.message_long, Toast.LENGTH_SHORT).show();
				return ;
			}
			long addTime = System.currentTimeMillis();
			MessageDetailInfo info = new MessageDetailInfo();
			info.setnItemId(getMessageBoardId());
			info.setnTime(addTime);
			info.setsMessage(contentString);
			int maxId = DBTool.getInstance().getMessageBoard().getMaxId();
			info.setnId(maxId + 1);
			// info.setsTitle(titleString);
			boolean addBool = DBTool.getInstance().getMessageBoard()
					.insertMessage(info);
			if (addBool) {
				SKToast.makeText(SKSceneManage.getInstance().mContext, R.string.add_new_message_success, Toast.LENGTH_SHORT).show();
				iMessageAddCallBack.addNotice(info, 1);
				closePopWindow();
			} else {
				SKToast.makeText(SKSceneManage.getInstance().mContext,R.string.add_new_message_faild, Toast.LENGTH_SHORT).show();
			}

		} else if (v == message_edit_sure) {
			// 修改留言信息
			// String titleString = message_edit_title.getText().toString();
			String contentString = message_edit_content.getText().toString().trim();
			if(null == contentString || "".equals(contentString))
			{
				SKToast.makeText(SKSceneManage.getInstance().mContext, R.string.message_notnull, Toast.LENGTH_SHORT).show();
				return ;
			}else if(contentString.getBytes().length>600)
			{
				SKToast.makeText(SKSceneManage.getInstance().mContext, R.string.message_long, Toast.LENGTH_SHORT).show();
				return ;
			}
			long editTime = System.currentTimeMillis();
			MessageDetailInfo info = getMessageDetailInfo();
			info.setnTime(editTime);
			info.setsMessage(contentString);
			// info.setsTitle(titleString);
			boolean editBool = DBTool.getInstance().getMessageBoard()
					.updateMessage(info);
			if (editBool) {
				SKToast.makeText(SKSceneManage.getInstance().mContext,R.string.edit_new_message_success, Toast.LENGTH_SHORT).show();
				iMessageEditCallBack.eidtNotice(info, 2);
				closePopWindow();
			} else {
				SKToast.makeText(SKSceneManage.getInstance().mContext,R.string.edit_new_message_faild, Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * 获取用户设置
	 */
	private void getDateAndTime() {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				"information", 0);
		sTime = sharedPreferences.getString("history_sTime"+HistoryShowId, "00:00:00");
		eTime = sharedPreferences.getString("history_eTime"+HistoryShowId, "24:00:00");
		sDate = sharedPreferences.getString("history_sDate"+HistoryShowId, "1970-1-1");
		eDate = sharedPreferences.getString("history_eDate"+HistoryShowId, "");
		bShowALl=sharedPreferences.getBoolean("history_show_all"+HistoryShowId, true);
		
		if (bShowALl) {
			bShowStart=false;
			bShowEnd=false;
		}else {
			bShowStart=sharedPreferences.getBoolean("history_show_start"+HistoryShowId, true);
			bShowEnd=sharedPreferences.getBoolean("history_show_end"+HistoryShowId, true);
		}
		
		if (eDate.equals("")) {
			Calendar calendar = Calendar.getInstance();
			eDate = calendar.get(Calendar.YEAR) + "-"
					+ (calendar.get(Calendar.MONTH) + 1) + "-"
					+ calendar.get(Calendar.DAY_OF_MONTH);
		}
	}

	/**
	 * 保存用户设置
	 */
	private void saveDateAndTime() {
		SharedPreferences.Editor shareEditor = mContext.getSharedPreferences(
				"information", 0).edit();
		shareEditor.putString("history_sTime"+HistoryShowId, sTime);
		shareEditor.putString("history_eTime"+HistoryShowId, eTime);
		shareEditor.putString("history_sDate"+HistoryShowId, sDate);
		shareEditor.putString("history_eDate"+HistoryShowId, eDate);
		if(cbxAll!=null){
			shareEditor.putBoolean("history_show_all"+HistoryShowId, cbxAll.isChecked());
		}
		if (cbxStart!=null) {
			shareEditor.putBoolean("history_show_start"+HistoryShowId, cbxStart.isChecked());
		}
		if(cbxEnd!=null){
			shareEditor.putBoolean("history_show_end"+HistoryShowId, cbxEnd.isChecked());
		}
		shareEditor.commit();
	}

	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if (arg2 == DATE_PICKER_ID) {
				showDialog(DATE_PICKER_ID);
			} else if (arg2 == TIME_PICKER_ID) {
				showDialog(TIME_PICKER_ID);
			}
		}
	};

	/**
	 * 时间设置回调
	 */
	public interface IDateCallback {
		/**
		 * @param start
		 *            -开始时间
		 * @param end
		 *            -结束时间
		 */
		void onChange(long start, long end);
	}

	public void setiDateCallback(IDateCallback iDateCallback) {
		this.iDateCallback = iDateCallback;
	}

	/**
	 * 留言信息添加跟修改成功
	 * 
	 * @author Administrator
	 * 
	 */
	public interface IMessageAddCallBack {
		void addNotice(MessageDetailInfo info, int i);
	}

	public interface IMessageEditCallBack {
		void eidtNotice(MessageDetailInfo info, int i);
	}

	public void setiMessageAddCallBack(IMessageAddCallBack iMessageAddCallBack) {
		this.iMessageAddCallBack = iMessageAddCallBack;
	}

	public void setiMessageEditCallBack(
			IMessageEditCallBack iMessageEditCallBack) {
		this.iMessageEditCallBack = iMessageEditCallBack;
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		SKSceneManage.getInstance().time = 0;
		return super.dispatchTouchEvent(ev);
	}
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		SKSceneManage.getInstance().time = 0;
		return super.dispatchKeyEvent(event);
	}
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		SKSceneManage.getInstance().time = 0;
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(TimeTickReceiver!=null){
			mContext.unregisterReceiver(TimeTickReceiver);
		}
	}
}
