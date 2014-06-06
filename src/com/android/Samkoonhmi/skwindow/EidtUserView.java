package com.android.Samkoonhmi.skwindow;

import java.util.ArrayList;
import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.adapter.UserAdapter;
import com.android.Samkoonhmi.adapter.UserAdapter.HolerView;
import com.android.Samkoonhmi.databaseinterface.UserInfoBiz;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.model.UserInfo;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 编辑用户主界面
 * @author 刘伟江
 * 创建时间 2012-7-15
 */
public class EidtUserView {

	private Context mContext;
	private LayoutInflater inflater;
	private View view;
	//添加按钮
	private Button mAddBtn;
	//更新按钮
	private Button mUpdateBtn;
	//删除按钮
	private Button mDeleteBtn;
	//退出按钮
	private Button mExitBtn;
	private IClickListener listener;
	//用户view
	private ListView mUserListView;
	//组view
	private ListView mGroupListView;
	//管理员view
	private ListView mMasterListView;
	//用户列表
	private ArrayList<UserInfo> mUserList;
	//组列表
	private ArrayList<UserInfo> mGroupList;
	private UserAdapter userAdapter;
	private UserAdapter groupAdapter;
	private UserAdapter masterAdapter;
	private UserInfoBiz biz;
	private int nCurrentId=0;
	//是否可以点击
	private boolean click;
	private Handler loadHandler;
	private static final int LOAD=0;
	private static final int DELETE=1;
	private static final int UPDATE=2;
	private static final int UPDATEDATA=3;
	private static final int UPDATEGROUP=4;
	
	public EidtUserView(Context context,IClickListener listener){
		this.mContext=context;
		inflater=LayoutInflater.from(mContext);
		this.listener=listener;
		loadHandler = new Handler(Looper.getMainLooper()){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch(msg.what){
					case LOAD:
//						System.out.println("edit load start");
						LoadDataByThread();
						setAdapter();
						break;
					case DELETE:
						deleteByThread(msg.arg1);
						break;
					case UPDATE:
						boolean result = (msg.arg1==1);
						boolean updateId = (msg.arg2==1);
						updateByThread(result, updateId);
						break;
					case UPDATEDATA:
						updateDataByThread();
						break;
					case UPDATEGROUP:
						updateGroupByThread(msg.arg1);
						break;
				}
			}
		};
	}
	
	public boolean LoadData(){
//		System.out.println("edit load need");
		return loadHandler.sendEmptyMessage(LOAD);
	}
	
	private boolean LoadDataByThread(){
		boolean result=true;
		this.click=true;
		
		//当前用户
		UserInfo info=SystemInfo.getGloableUser();
	
		if (biz==null) {
			biz=new UserInfoBiz();
		}
		
		//获取所有用户
		mUserList=biz.getUserListInGroup(info);
		
		if (info==null||mUserList==null||mUserList.size()==0) {
			Log.e("EidtUserView", "EidtUserView data=null.");
			mGroupList=new ArrayList<UserInfo>();
			Toast.makeText(mContext, R.string.user_not_master, Toast.LENGTH_SHORT).show();
			mAddBtn.setVisibility(View.GONE);
			mUpdateBtn.setVisibility(View.GONE);
			mDeleteBtn.setVisibility(View.GONE);
			return result=false;
		}
		
		for (int i = 0; i < mUserList.size(); i++) {
			if (info.getId()==mUserList.get(i).getId()) {
				//当前用户标示选中状态
				mUserList.get(i).setCheck(true);
			}
		}
		
		//组
		mGroupList=biz.getGroupList(info.getId(),true);
		if(mGroupList == null){
			mGroupList=new ArrayList<UserInfo>();
		}
		nCurrentId=info.getId();
		
		return result;
	}
	
	public View addView(int layout,int width,int heigth){
		view=inflater.inflate(layout, null);
		view.setLayoutParams(new LayoutParams(width, heigth));
		mAddBtn=(Button)view.findViewById(R.id.btn_edit_add);
		mUpdateBtn=(Button)view.findViewById(R.id.btn_edit_update);
		mDeleteBtn=(Button)view.findViewById(R.id.btn_edit_delete);
		mExitBtn=(Button)view.findViewById(R.id.btn_edit_exit);
		mUserListView=(ListView)view.findViewById(R.id.edit_user_list);
		mGroupListView=(ListView)view.findViewById(R.id.edit_group_list);
		mMasterListView=(ListView)view.findViewById(R.id.show_master_list);
		//添加监听事件
		addListener();
		System.out.println("edit addview");
		//获取数据
//		if(LoadData()){
//			//设置适配器
//			setAdapter();
//		}else {
//			Toast.makeText(mContext, R.string.user_not_master, Toast.LENGTH_SHORT).show();
//		}
		LoadData();
		
		return view;
	}
	
	/**
	 * 设置适配器
	 */
	private void setAdapter(){
		userAdapter=new UserAdapter(mContext, mUserList, true);
		groupAdapter=new UserAdapter(mContext, mGroupList, false);
		masterAdapter=new UserAdapter(mContext,mGroupList,true,true,true);
		
		mUserListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		mUserListView.setAdapter(userAdapter);
		
		//点击用户切换，用户信息
		mUserListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				HolerView holerView=(HolerView)view.getTag();
				if (mUserList!=null) {
					for (int i = 0; i < mUserList.size(); i++) {
						UserInfo info=mUserList.get(i);
						if (info.getId()==holerView.id) {
							info.setCheck(true);
							if (nCurrentId!=info.getId()) {
								updateGroup(info.getId());
							}
						}else {
							info.setCheck(false);
						}
					}
					userAdapter.notifyDataSetChanged();
					masterAdapter.notifyDataSetChanged();
				}
			}
		});
		
		mGroupListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		mGroupListView.setAdapter(groupAdapter);
		mGroupListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
			}
		});
		
		mMasterListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		mMasterListView.setAdapter(masterAdapter);
		mMasterListView.setClickable(false);
		mMasterListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
			}
		});
	}
	
	
	private void updateGroup(int id){
		Message msg = Message.obtain();
		msg.what=UPDATEGROUP;
		msg.arg1 = id;
		loadHandler.sendMessage(msg);
	}
	
	/**
	 * 根据不同用户显示相应的用户组
	 */
	private void updateGroupByThread(int id){
		nCurrentId=id;
		mGroupList.clear();
		if(mUserList.isEmpty()){
			groupAdapter.notifyDataSetChanged();
			masterAdapter.notifyDataSetChanged();
			return;
		}
		ArrayList<UserInfo> list=biz.getGroupList(id, true);
		if (list==null||list.size()==0) {
			list=new ArrayList<UserInfo>();
		}
		
		if (mGroupList != null) {
			
			mGroupList.clear();
			for (int i = 0; i < list.size(); i++) {
				mGroupList.add(list.get(i));
			}
			groupAdapter.notifyDataSetChanged();
		}
		
	masterAdapter.notifyDataSetChanged();
	}
	
	/**
	 * 添加点击事件
	 */
	private void addListener(){
		//添加用户
		mAddBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SKSceneManage.getInstance().time=0;
				if(listener!=null){
					if (click) {
						//防止频繁点击
						click=false;
						listener.onNext(CHANG_TYPE.EDIT_TO_ADD,true);
					}
				}
			}
		});
		
		//更新用户
		mUpdateBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SKSceneManage.getInstance().time=0;
				if(listener!=null){
					if (click) {
						click=false;
						listener.onPre(CHANG_TYPE.EDIT_TO_UPDATE,true);
					}
				}
			}
		});
		
		//删除用户
		mDeleteBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SKSceneManage.getInstance().time=0;
				if (click) {
					click=false;
					delete(nCurrentId);
				}
			}
		});
		
		//退出
		mExitBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SKSceneManage.getInstance().time=0;
				if (listener!=null) {
					listener.onExit();
				}
			}
		});
		
	}
	
	private void delete(int id){
		Message msg = Message.obtain();
		msg.what = DELETE;
		msg.arg1 = id;
		loadHandler.sendMessage(msg);
	}
	
	/**
	 * 删除用户
	 */
	private void deleteByThread(int id){
		if (id==0||id==SystemInfo.getGloableUser().getId()) {
			Toast.makeText(mContext, R.string.hint_delete_msg, Toast.LENGTH_SHORT).show();
			click=true;
			return;
		}
		boolean result=biz.deleteUser(id);
		
		if (result) {
			Toast.makeText(mContext, R.string.hint_delete_succeed, Toast.LENGTH_SHORT).show();
		}else {
			Toast.makeText(mContext, R.string.hint_delete_fail, Toast.LENGTH_SHORT).show();
		}
		
		update(result,true);
		click=true;
	} 
	
	public void update(boolean result,boolean updateId){
		Message msg = Message.obtain();
		msg.what=UPDATE;
		if(result){
			msg.arg1=1;
		}else{
			msg.arg1=0;
		}
		if(updateId){
			msg.arg2=1;
		}else{
			msg.arg2=0;
		}
		loadHandler.sendMessage(msg);
	}
	
	/**
	 * 更新or添加or删除,成功时更新界面
	 * @param result-更新的结果
	 * @param updateId-是否更新当前id,删除的时候需要更新当前id
	 */
	public void updateByThread(boolean result,boolean updateId){
		if(result){
			//添加成功or修改成功or删除成功
			if (userAdapter!=null) {
					
				UserInfo info=SystemInfo.getGloableUser();
				if (updateId) {
					if (info!=null) {
						nCurrentId=info.getId();
					}
				}
				
				mUserList.clear();
				ArrayList<UserInfo> list=biz.getUserListInGroup(info);
				if (list==null||list.size()==0) {
					//清空组列表
					updateGroup(nCurrentId);
					userAdapter.notifyDataSetChanged();
					return;
				}
				
				for (int i = 0; i < list.size(); i++) {
					mUserList.add(list.get(i));
                    if (list.get(i).getId()==nCurrentId) {
                    	mUserList.get(i).setCheck(true);
					}
				}
				userAdapter.notifyDataSetChanged();
			}
			
			updateGroup(nCurrentId);
		}
	}
	
	
	public void updateData(){
		loadHandler.sendEmptyMessage(UPDATEDATA);
	}
	
	/**
	 * 更新数据
	 */
	public void updateDataByThread(){
		UserInfo info=SystemInfo.getGloableUser();
		if (info.getId()!=nCurrentId) {
			for (int i = 0; i < mUserList.size(); i++) {
				if (info.getId()==mUserList.get(i).getId()) {
					//当前用户标示选中状态
					mUserList.get(i).setCheck(true);
				}else{
					mUserList.get(i).setCheck(false);
				}
			}
			userAdapter.notifyDataSetChanged();
			updateGroup(info.getId());
		}
	}
	
	/**
	 * 防止多次点击
	 */
	public void setClick(){
		click=true;
	}
	
	/**
	 * 切换到编辑界面的类型 
	 */
	public enum CHANG_TYPE{
		EDIT_TO_ADD,    //从编辑界面进入添加界面
		EDIT_TO_UPDATE, //从编辑界面进入更新界面
		ADD_TO_EDIT,    //从添加界面进入编辑界面
		UPDATE_TO_EDIT  //从更新界面进入编辑界面
	}
	
	public interface IClickListener{
		//退出
		void onExit();
		/**
		 * 后退
		 * @return true,表示已经切换成功
		 * @param result-更新or添加的结果
		 */
		void onPre(CHANG_TYPE type,boolean result);
		
		/**
		 * 前进
		 * @return true,表示已经切换成功
		 * @param result-更新or添加的结果
		 */
		void onNext(CHANG_TYPE type,boolean result);
	}
	
	public int getnCurrentId() {
		return nCurrentId;
	}

}
