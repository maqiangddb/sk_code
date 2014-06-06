package com.android.Samkoonhmi.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.model.ComboBoxInfo;
import com.android.Samkoonhmi.model.ComboxItemInfo;
import com.android.Samkoonhmi.model.DragdownboxItemInfo;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.model.TextInfo;
import com.android.Samkoonhmi.skenum.TEXT_PIC_ALIGN;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.TextAlignUtil;
import com.android.Samkoonhmi.util.TextAttribute;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
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
public class DragdownBoxAdapt extends ArrayAdapter<TextInfo> {
	private static List<TextInfo> data = null;
	private LayoutInflater inflater;

	public DragdownBoxAdapt(Context context, List<TextInfo> list) {
		super(context, R.layout.comboxlistitem, list);
		data = list;
		inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return data.size();
	}

	public ArrayList<TextInfo> getList() {
		return (ArrayList<TextInfo>) data;
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
					R.layout.dragdownitme, null);
			holerView.functionName = (TextView) convertView
					.findViewById(R.id.dragdownName);
			convertView.setTag(holerView);
		} else {
			holerView = (HolerView) convertView.getTag();
		}
		if (null != data && !data.isEmpty()) {
			TextInfo info = data.get(position);
			holerView.functionName.setTextColor(info.getmColors().get(SystemInfo.getCurrentLanguageId()));
			holerView.functionName.setTextSize(info.getmSize().get(SystemInfo.getCurrentLanguageId()));
			holerView.functionName.setText(info.getmTextList().get(SystemInfo.getCurrentLanguageId()));
			Typeface typeFace = TextAlignUtil
					.getTypeFace(info.getmFonts().get(SystemInfo.getCurrentLanguageId()));
			holerView.functionName.setTypeface(typeFace);

		}
		return convertView;
	}

	public class HolerView {
		TextView functionName;
	}

}
