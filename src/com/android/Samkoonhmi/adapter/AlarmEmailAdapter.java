package com.android.Samkoonhmi.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.model.alarm.AlarmGroupInfo;
import com.android.Samkoonhmi.skwindow.SKToast;

import android.R.integer;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AlarmEmailAdapter extends BaseAdapter{
	
	private ArrayList<AlarmGroupInfo> alarmlist = null;
	private LayoutInflater inflater;
	private DatePickerDialog pickerDialog = null;
	private Button curButton = null; // 当前点击的按钮
	private final int START_BUTTON = 100;
	private final int END_BUTTON = 200;
	private int CURRENT_BUTTON = 0;
	private ArrayList<TimeRange>mTimeRanges = null;
	private ArrayList<Boolean> bCheckList =  new ArrayList<Boolean>();
	private Context mContext;

	public AlarmEmailAdapter(Context context, ArrayList<AlarmGroupInfo> list){
		mContext = context;
		alarmlist = list;
		inflater = LayoutInflater.from(context);
		mTimeRanges = new ArrayList<AlarmEmailAdapter.TimeRange>();
		for(int i = 0; i < list.size(); i++ ){
			mTimeRanges.add(new TimeRange());
			bCheckList.add(false);
		}
		
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		Date myDate = new Date();
		calendar.setTime(myDate);
		pickerDialog = new DatePickerDialog(context, mDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return alarmlist.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return alarmlist.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.email_alarm_adpter, null);
			holder = new ViewHolder();
			holder.checkBox = (CheckBox) convertView.findViewById(R.id.alarm_checkbox) ;
			holder.titleView = (TextView) convertView.findViewById(R.id.item_message);
			holder.layout = (LinearLayout) convertView.findViewById(R.id.email_alarm_data);
			holder.startButton = (Button) convertView.findViewById(R.id.email_alarm_start);
			holder.endButton = (Button) convertView.findViewById(R.id.email_alarm_end);
			
			holder.startButton.setOnClickListener(mClickListener);
			holder.endButton.setOnClickListener(mClickListener);
			holder.checkBox.setOnClickListener(mClickListener);

			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag(); 
		}
		
		TimeRange range = mTimeRanges.get(position);
		holder.startButton.setText(range.mStartTime);
		holder.endButton.setText(range.mEndTime);
		holder.startButton.setTag(position);
		holder.endButton.setTag(position);
		holder.checkBox.setTag(position);
		holder.checkBox.setChecked(bCheckList.get(position));
		holder.titleView.setText(alarmlist.get(position).getsName());
		
		return convertView;
	}
	
	
	private OnClickListener mClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v.getId() == R.id.email_alarm_start){
				if (pickerDialog != null && !pickerDialog.isShowing()) {
					 pickerDialog.show();
				}
				
				CURRENT_BUTTON = START_BUTTON;
				curButton = (Button)v;
			}
			else if (v.getId() == R.id.email_alarm_end) {
				if (pickerDialog != null && !pickerDialog.isShowing()) {
					 pickerDialog.show();
				}
				
				CURRENT_BUTTON = END_BUTTON;
				curButton = (Button)v;
			}
			else if (v.getId() == R.id.alarm_checkbox) {
				int nTag = (Integer) v.getTag();
				bCheckList.set(nTag, !bCheckList.get(nTag));
			}
		}
	};
	
	private OnDateSetListener mDateSetListener = new OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			if (curButton != null) {
				TimeRange range = (TimeRange) mTimeRanges.get((Integer)curButton.getTag());
				String content = year + "/" + (monthOfYear+1) + "/" + dayOfMonth;
				Date mDate = new Date(year, monthOfYear, dayOfMonth);
				long time = mDate.getTime();
				if (CURRENT_BUTTON == START_BUTTON && range.nEndTime != 0 &&  time >= range.nEndTime) {
					SKToast.makeText(mContext.getString(R.string.email_reset_time), Toast.LENGTH_SHORT).show();
					return ;
				}
				else if (CURRENT_BUTTON == END_BUTTON && range.nStartTime != 0 && time <= range.nStartTime) {
					SKToast.makeText(mContext.getString(R.string.email_reset_time), Toast.LENGTH_SHORT).show();
					return;
				}
				
				if (CURRENT_BUTTON == START_BUTTON) {
					range.nStartTime = time;
					range.mStartTime = content;
				}
				else {
					range.nEndTime = time;
					range.mEndTime = content;
				}
				
				curButton.setText(content);
			}
		}
	};
	
	
	private class ViewHolder{
		public LinearLayout layout;
		public CheckBox checkBox;
		public TextView titleView;
		public Button startButton, endButton;
	}
	
	public class TimeRange{
		public long nStartTime = 0, nEndTime = 0;
		public String mStartTime = mContext.getString(R.string.email_start_time), mEndTime = mContext.getString(R.string.email_end_time);
	}
	
	public static class Email_AlarmBean{
		public int nGroupId;
		public String nGroupName;
		public long nStartTime;
		public long nEndTime;
	}
	
	/**
	 * 返回选中的报警
	 * @return
	 */
	public ArrayList<Email_AlarmBean> getSelectArray(){
		ArrayList<Email_AlarmBean> list = new ArrayList<Email_AlarmBean>();
		for(int i = 0; i < bCheckList.size(); i++){
			if (bCheckList.get(i)) {
				Email_AlarmBean bean = new Email_AlarmBean();
				bean.nGroupId = alarmlist.get(i).getnGroupId();
				bean.nGroupName = alarmlist.get(i).getsName();
				bean.nStartTime = mTimeRanges.get(i).nStartTime;
				bean.nEndTime = mTimeRanges.get(i).nEndTime;
				
				list.add(bean);
			}
		}
		
		return list;
	}
	


}
