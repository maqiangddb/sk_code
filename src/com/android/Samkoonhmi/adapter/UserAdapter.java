package com.android.Samkoonhmi.adapter;

import java.util.ArrayList;
import java.util.List;
import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.model.UserInfo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 用户和组的适配器
 */
public class UserAdapter extends ArrayAdapter<UserInfo>{

	//用户和组的列表
	private ArrayList<UserInfo> list;
	private LayoutInflater inflater;
	//是否显示选择框
	private boolean show;
	
	public UserAdapter(Context context, ArrayList<UserInfo> list,boolean showCheckBox) {
		super(context,R.layout.eidt_user_item, list);
		this.list=list;
		this.show=showCheckBox;
		inflater=LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		if (list==null) {
			return 0;
		}
		return list.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HolerView holerView=null;
		if (convertView==null) {
			holerView=new HolerView();
			convertView=inflater.inflate(R.layout.eidt_user_item, null);
			holerView.txtName=(TextView)convertView.findViewById(R.id.item_name);
			holerView.txtDes=(TextView)convertView.findViewById(R.id.item_message);
			holerView.checkBox=(ImageView)convertView.findViewById(R.id.item_checkbox);
			convertView.setTag(holerView);
		}else {
			holerView=(HolerView)convertView.getTag();
		}
		
		holerView.id=list.get(position).getId();
		holerView.list=list.get(position).getGroupId();
		holerView.txtName.setText(list.get(position).getName());
		holerView.txtDes.setText(list.get(position).getDescript());
		if(show){
			holerView.checkBox.setVisibility(View.VISIBLE);
			if (list.get(position).isCheck()) {
				holerView.checkBox.setBackgroundResource(R.drawable.btn_check_on);
			}else {
				holerView.checkBox.setBackgroundResource(R.drawable.btn_check_off);
			}
		}else {
			holerView.checkBox.setVisibility(View.INVISIBLE);
			
		}
		
		return convertView;
	}

	public final class HolerView{
		public int id;             //id
		public List<Integer> list; //用户拥有的组
		public TextView txtName;   //名称
		public TextView txtDes;    //描述
		public ImageView checkBox; //是否选中
	}

}
