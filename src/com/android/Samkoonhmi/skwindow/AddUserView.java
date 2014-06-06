package com.android.Samkoonhmi.skwindow;

import java.util.ArrayList;
import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.adapter.UserAdapter;
import com.android.Samkoonhmi.adapter.UserAdapter.HolerView;
import com.android.Samkoonhmi.databaseinterface.UserInfoBiz;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.model.UserInfo;
import com.android.Samkoonhmi.skwindow.EidtUserView.CHANG_TYPE;
import com.android.Samkoonhmi.skwindow.EidtUserView.IClickListener;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 添加和修改用户
 * 
 * @author 刘伟江 创建时间 2012-7-15
 */
public class AddUserView {

	private Context mContext;
	private LayoutInflater inflater;
	private IClickListener listener;
	private View view;
	private Button mAddUserBtn;
	private Button mCancelBtn;
	private CHANG_TYPE type;
	private TextView mTitleName;
	private EditText mEditName; // 用戶名
	private EditText mEditPwd; // 密碼
	private EditText mEditPwds; // 确定密码
	private EditText mEditDes; // 描述
	private ListView mGroupListView; // 用户组
	private ArrayList<UserInfo> mGroupList;
	private ArrayList<UserInfo> mHideList;
	private UserInfoBiz biz;
	//List的Adapter，adapter和adapterMaster处理的数据是同一个
	private UserAdapter adapter;
	private UserAdapter adapterMaster;//用户权限
	private boolean click;
	private UserInfo mUpdateInfo;
	private ListView mGroupMasterView;
	public boolean updateCurrentUser=false;
	private Handler loadHandler;
	private static final int LOAD=0;
	private static final int ADD=1;
	private static final int RESET=2;
	private static final int UPDATEDATA=3;
	private static final int UPDATEUSER=4;

	public AddUserView(Context context, IClickListener listener) {
		this.mContext = context;
		this.listener = listener;
		this.click = true;
		biz = new UserInfoBiz();
		inflater = LayoutInflater.from(mContext);
		loadHandler = new Handler(Looper.getMainLooper()){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch(msg.what){
					case LOAD:
						loadData();
						// 设置adapter
						setAdapter();
						break;
					case ADD:
						addUserByThread();
						break;
					case RESET:
						resetByThread((CHANG_TYPE) msg.obj, msg.arg1);
						break;
					case UPDATEDATA:
						updateDataByThread();
						break;
					case UPDATEUSER:
						updateUserByThread();
						break;
				}
			}
		};
	}

	/**
	 * 加载view
	 */
	public View addView(int layout, int width, int height) {
		view = inflater.inflate(layout, null);
		view.setLayoutParams(new LayoutParams(width, height));
		mAddUserBtn = (Button) view.findViewById(R.id.btn_add_user);
		mCancelBtn = (Button) view.findViewById(R.id.btn_cancel_user);
		mTitleName = (TextView) view.findViewById(R.id.title_name);
		mEditName = (EditText) view.findViewById(R.id.add_name);
		mEditPwd = (EditText) view.findViewById(R.id.add_pwd);
		mEditPwds = (EditText) view.findViewById(R.id.add_pwd_confirm);
		mEditDes = (EditText) view.findViewById(R.id.add_user_message);
		mGroupListView = (ListView) view.findViewById(R.id.add_group_list);
		mGroupMasterView = (ListView) view.findViewById(R.id.set_group_master);
		// 添加监听事件
		addListener();
		// 添加数据
		
		loadHandler.sendEmptyMessage(LOAD);
		
		return view;
	}

	public void load(){
		loadHandler.sendEmptyMessage(LOAD);
	}
	
	/**
	 * 加载数据
	 */
	private void loadData() {
		UserInfo info = SystemInfo.getGloableUser();
		if (info != null) {
			mGroupList = biz.getGroupList(info.getId(),false);
		}
		if(mGroupList == null){
			mGroupList = new ArrayList<UserInfo>();
		}else{
			for(int i=0;i<mGroupList.size();i++){
				if(mGroupList.get(i).isMasterSet()){
					if(mGroupList.get(i).getMaster()==false){
						mGroupList.remove(i);
						i--;
					}
				}
			}
		}
	}

	/**
	 * 设置adapter
	 */
	private void setAdapter() {
		adapter = new UserAdapter(mContext, mGroupList, true);
		mGroupListView.setAdapter(adapter);
		mGroupListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				SKSceneManage.getInstance().time=0;
				if (mGroupList==null) {
					return;
				}
				HolerView hodlerView = (HolerView) view.getTag();
				for (int i = 0; i < mGroupList.size(); i++) {
					UserInfo info = mGroupList.get(i);
					if (hodlerView.id == info.getId()) {
						if (info.isCheck()) {
							info.setCheck(false);
						} else {
							info.setCheck(true);
						}
						break;
					}
				}
				adapter.notifyDataSetChanged();
				if(adapterMaster != null){
					adapterMaster.notifyDataSetChanged();
				}
			}
		});
		adapterMaster = new UserAdapter(mContext, mGroupList, true ,true);
		mGroupMasterView.setAdapter(adapterMaster);
		mGroupMasterView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				SKSceneManage.getInstance().time=0;
				if (mGroupList==null) {
					return;
				}
				if(mGroupList.get(position).isCheck()){
					if(mGroupList.get(position).isMasterSet()){
						if(mGroupList.get(position).getMaster()){
							mGroupList.get(position).setMaster(false);
						}else{
							mGroupList.get(position).setMaster(true);
						}
					}else{
						Toast.makeText(mContext, R.string.group_no_master, Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(mContext, R.string.usr_no_in_group, Toast.LENGTH_SHORT).show();
				}
				adapterMaster.notifyDataSetChanged();
			}
		});
	}

	private boolean addUser(){
		return loadHandler.sendEmptyMessage(ADD);
	}
	
	// 添加用户
	private boolean addUserByThread() {
		boolean result = false;
		
		if (mEditName.getText().toString().trim().matches("^\\s*$")) {
			// 用户名为空
			Toast.makeText(mContext, R.string.hint_name_entry,
					Toast.LENGTH_SHORT).show();
			return result;
		} else if (biz.existUserName(mEditName.getText().toString())) {
			// 用户名已经存在
			Toast.makeText(mContext, R.string.hint_name, Toast.LENGTH_SHORT)
					.show();
			return result;
		} else if (!mEditPwd.getText().toString().equals("")) {
			// 如果密码不为空
			if (mEditPwds.getText().toString().equals("")) {
				// 确定密码为空
				Toast.makeText(mContext, R.string.hint_pwds_entry,
						Toast.LENGTH_SHORT).show();
				return result;
			} else if (!mEditPwd.getText().toString().trim()
					.equals(mEditPwds.getText().toString().trim())) {
				// 密码不一致
				Toast.makeText(mContext, R.string.hint_pwd_repeat,
						Toast.LENGTH_SHORT).show();
				return result;
			}
		}
		UserInfo info = new UserInfo();
		info.setName(mEditName.getText().toString());
		info.setPassword(mEditPwd.getText().toString() + "");
		info.setDescript(mEditDes.getText().toString() + "");

		ArrayList<UserInfo> list = new ArrayList<UserInfo>();
		if (mGroupList!=null) {
			for (int i = 0; i < mGroupList.size(); i++) {
				if (mGroupList.get(i).isCheck()) {
					list.add(mGroupList.get(i));
				}
			}
		}
		
		if (list.size() == 0) {
			Toast.makeText(mContext, R.string.hint_select_group,
					Toast.LENGTH_SHORT).show();
			return result;
		}

		result = biz.insertUser(info, list, true);

		if (result) {
			Toast.makeText(mContext, R.string.hint_result_succeed,
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(mContext, R.string.hint_result_fail,
					Toast.LENGTH_SHORT).show();
		}

		return result;
	}

	private boolean updateUser(){
		return loadHandler.sendEmptyMessage(UPDATEUSER);
	}
	
	// 更新用户信息
	private boolean updateUserByThread() {
		boolean result = false;
		if (!mEditPwd.getText().toString().equals("")) {
			// 如果密码不为空
			if (mEditPwds.getText().toString().equals("")) {
				// 确定密码为空
				Toast.makeText(mContext, R.string.hint_pwds_entry,
						Toast.LENGTH_SHORT).show();
				return result;
			} else if (!mEditPwd.getText().toString().trim()
					.equals(mEditPwds.getText().toString().trim())) {
				// 密码不一致
				Toast.makeText(mContext, R.string.hint_pwd_repeat,
						Toast.LENGTH_SHORT).show();
				return result;
			}
		}

		if (mUpdateInfo != null) {
			mUpdateInfo.setPassword(mEditPwd.getText().toString());
			mUpdateInfo.setDescript(mEditDes.getText().toString());
			
			ArrayList<UserInfo> list = new ArrayList<UserInfo>();
			if (mGroupList!=null) {
				for (int i = 0; i < mGroupList.size(); i++) {
					if (mGroupList.get(i).isCheck()) {
						list.add(mGroupList.get(i));
					}
				}
			}
			//
			if(mHideList != null){
				for(int i=0;i<mHideList.size();i++){
					list.add(mHideList.get(i));
				}
			}
			if (list.size() == 0) {
				Toast.makeText(mContext, R.string.hint_select_group,
						Toast.LENGTH_SHORT).show();
				return result;
			}

			result = biz.updateUser(mUpdateInfo, list, mUpdateInfo.getId());

			if (result) {
				Toast.makeText(mContext, R.string.hint_update_succeed,
						Toast.LENGTH_SHORT).show();
				if(mUpdateInfo.getId()==SystemInfo.getGloableUser().getId()){
					SystemInfo.setGloableUser(biz.getUserInfoById(mUpdateInfo.getId()));
					updateCurrentUser=true;
				}
			} else {
				Toast.makeText(mContext, R.string.hint_update_fail,
						Toast.LENGTH_SHORT).show();
			}
		}

		return result;
	}

	/**
	 * 添加点击事件
	 */
	private void addListener() {

		// 添加or修改用户
		mAddUserBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SKSceneManage.getInstance().time=0;
				if (listener != null) {
					if (click) {
						click = false;
						if (type == CHANG_TYPE.EDIT_TO_ADD) {
							if (addUser()) {
								// 添加成功，页面跳转
								listener.onPre(CHANG_TYPE.ADD_TO_EDIT, true);
							} else {
								click = true;
							}
						} else if (type == CHANG_TYPE.EDIT_TO_UPDATE) {
							if (updateUser()) {
								// 更新成功，页面跳转
								listener.onNext(CHANG_TYPE.UPDATE_TO_EDIT, true);
							} else {
								click = true;
							}
						}
					}
				}
			}
		});

		// 取消
		mCancelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SKSceneManage.getInstance().time=0;
				if (listener != null) {
					if (click) {
						click = false;
						if (type == CHANG_TYPE.EDIT_TO_ADD) {
							listener.onPre(CHANG_TYPE.ADD_TO_EDIT, false);
						} else if (type == CHANG_TYPE.EDIT_TO_UPDATE) {
							listener.onNext(CHANG_TYPE.UPDATE_TO_EDIT, false);
						}
					}
				}
			}
		});

	}

	/**
	 * 防止多次点击
	 */
	public void setClick() {
		click = true;
	}

	/**
	 * @param type-添加or更新
	 * @param id-更新用户的id
	 */
	public void setType(CHANG_TYPE type, int id) {
		this.type = type;
		reset(type, id);
	}
	
	public void updateData(){
		loadHandler.sendEmptyMessage(UPDATEDATA);
	}
	
	private void updateDataByThread() {
		// 当前登录用户所用于的权限
		UserInfo info = SystemInfo.getGloableUser();
		if (info != null) {
			ArrayList<UserInfo> list = biz.getGroupList(info.getId(), false);
			if (list != null) {
				mGroupList.clear();
				for (int i = 0; i < list.size(); i++) {
					mGroupList.add(list.get(i));
				}
				adapter.notifyDataSetChanged();
				adapterMaster.notifyDataSetChanged();
			}
		}
	}
	
	private void reset(CHANG_TYPE type, int id){
		Message msg = Message.obtain();
		msg.arg1 = id;
		msg.obj = type;
		msg.what = RESET;
		loadHandler.sendMessage(msg);
	}
	// 重新设置显示内容
	private void resetByThread(CHANG_TYPE type, int id) {

		if (type == CHANG_TYPE.EDIT_TO_ADD) {
			// 添加
			mAddUserBtn.setText(R.string.btn_add_user);
			mTitleName.setText(R.string.add_user);
			mEditName.setEnabled(true);
			mEditName.setText("");
			mEditPwd.setText("");
			mEditPwds.setText("");
			mEditDes.setText("");
			for (int i = 0; i < mGroupList.size(); i++) {
				mGroupList.get(i).setMaster(false);
				mGroupList.get(i).setCheck(false);
			}
			adapter.notifyDataSetChanged();
			adapterMaster.notifyDataSetChanged();
		} else if (type == CHANG_TYPE.EDIT_TO_UPDATE) {
			// 更新
			mAddUserBtn.setText(R.string.confirm);
			mTitleName.setText(R.string.update_name);
			if (biz == null) {
				biz = new UserInfoBiz();
			}

			mUpdateInfo = biz.getUserInfoById(id);
			if (mUpdateInfo != null) {
				mEditName.setEnabled(false);
				mEditName.setText(mUpdateInfo.getName());
				if (mUpdateInfo.getPassword() != null) {
					if (!mUpdateInfo.getPassword().equals("")) {
						mEditPwd.setText(mUpdateInfo.getPassword());
						mEditPwds.setText(mUpdateInfo.getPassword());
					}
				}

				if (mUpdateInfo.getDescript() != null) {
					if (!mUpdateInfo.getDescript().equals("")) {
						mEditDes.setText(mUpdateInfo.getDescript());
					}
				}

				ArrayList<UserInfo> list = biz.getGroupList(id, true);
				if (list == null || list.size() == 0) {
					return;
				}
				mHideList = new ArrayList<UserInfo>(list);
				
				if (mGroupList != null) {
					for (int i = 0; i < mGroupList.size(); i++) {
						mGroupList.get(i).setCheck(false);
						mGroupList.get(i).setMaster(false);
						for (int j = 0; j < list.size(); j++) {
							if (mGroupList.get(i).getId() == list.get(j)
									.getId()) {
								// 把该用户拥有的组显示出来
								mGroupList.get(i).setCheck(true);
								//显示是否管理员
								mGroupList.get(i).setMaster(list.get(j).getMaster());
								//被管理的用户所属但不属于登录用户的组
								mHideList.remove(list.get(j));
							}
						}
					}	

					adapter.notifyDataSetChanged();
					adapterMaster.notifyDataSetChanged();
				}
			}
		}
	}
}
