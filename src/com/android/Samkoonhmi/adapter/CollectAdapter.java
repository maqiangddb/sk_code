package com.android.Samkoonhmi.adapter;

import java.security.interfaces.DSAKey;
import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.model.sk_historytrends.CollectItem;

public class CollectAdapter extends ArrayAdapter<CollectItem> {
	private static List<CollectItem> data = null;
	private LayoutInflater inflater;

	public CollectAdapter(Context context, List<CollectItem> list) {
		super(context, R.layout.collect_item, list);
		data = list;
		inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return data.size();
	}

	public ArrayList<CollectItem> getList() {
		return (ArrayList<CollectItem>) data;
	}

	public long getItemId(int position) {
		index=position;
		return position;
	}

	private int index=0;
	public View getView(int position, View convertView, ViewGroup parent) {
		HolerView holerView = null;

		index=position;
		if (null == convertView) {
			holerView = new HolerView();
			// 获取listViewItem的布局
			convertView = inflater.inflate(R.layout.collect_item, null);
			holerView.mCheckBox = (CheckBox) convertView
					.findViewById(R.id.item_id);
			holerView.sName = (TextView) convertView
					.findViewById(R.id.item_content);
			convertView.setTag(holerView);
		} else {
			holerView = (HolerView) convertView.getTag();
		}
		if (null != data) {
			holerView.mCheckBox.setChecked(data.get(position).isCheck);
			holerView.mCheckBox.setTag(data.get(position).nGId);
			holerView.sName.setText(data.get(position).sGName);
			//holerView.sName.setTag(data.get(position).nGId);
		
		}
		holerView.mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				CheckBox cbx=(CheckBox)buttonView;
				String temp=cbx.getTag().toString();
				if (temp!=null&&(!temp.equals(""))) {
					int index=Integer.valueOf(temp);
					if (index<data.size()) {
						data.get(index).isCheck=isChecked;
					}
				}
			}
		});
		
		return convertView;
	}

	public class HolerView {
		public CheckBox mCheckBox;
		public TextView sName;
	}

}