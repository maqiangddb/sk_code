package com.android.Samkoonhmi.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.model.ComboBoxInfo;
import com.android.Samkoonhmi.model.ComboxItemInfo;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.TextAlignUtil;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * ] 下拉框的list的适配
 * 
 * @author Administrator
 * 
 */
public class ComboxListAdapt extends ArrayAdapter<ComboxItemInfo> {
	private static List<ComboxItemInfo> data = null;
	private LayoutInflater inflater;
	private ComboBoxInfo info;

	public ComboxListAdapt(Context context, List<ComboxItemInfo> list,
			ComboBoxInfo info) {
		super(context, R.layout.comboxlistitem, list);
		data = list;
		inflater = LayoutInflater.from(context);
		this.info = info;
	}

	public int getCount() {
		return data.size();
	}

	public ArrayList<ComboxItemInfo> getList() {
		return (ArrayList<ComboxItemInfo>) data;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		HolerView holerView = null;

		if (null == convertView) {
			holerView = new HolerView();
			// 获取listViewItem的布局
			convertView = LayoutInflater.from(
					SKSceneManage.getInstance().mContext).inflate(
					R.layout.comboxlistitem, null);
			holerView.functionName = (TextView) convertView
					.findViewById(R.id.functionName);
			holerView.radioButton = (ImageView) convertView
					.findViewById(R.id.radio);
			convertView.setTag(holerView);
		} else {
			holerView = (HolerView) convertView.getTag();
		}
		if (null != data && !data.isEmpty()) {

			// 根据选中的值确定图片
			if (data.get(position).isChecked() == true) {
				holerView.radioButton
						.setBackgroundResource(R.drawable.btn_check_on);
			} else {
				holerView.radioButton
						.setBackgroundResource(R.drawable.btn_check_off);
			}
			holerView.functionName.setTextColor(info.getnFontColor());
			holerView.functionName.setTextSize(info.getNfontSize());
			ComboxItemInfo itemInfo = data.get(position);
			if (null != itemInfo) {
				Map<Integer, String> functionNames = itemInfo
						.getFunctionNames();
				if (null != functionNames) {
					holerView.functionName.setText(functionNames.get(SystemInfo
							.getCurrentLanguageId()));
				}
			}
			Typeface typeFace = TextAlignUtil.getTypeFace(info.getsFontType());
			holerView.functionName.setTypeface(typeFace);

		}
		return convertView;
	}


	public class HolerView {
		TextView functionName;
		ImageView radioButton;
	}

}
