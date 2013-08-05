package com.android.Samkoonhmi.skwindow;

import java.util.ArrayList;
import java.util.List;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.adapter.UserAdapter;
import com.android.Samkoonhmi.adapter.UserAdapter.HolerView;
import com.android.Samkoonhmi.databaseinterface.UserInfoBiz;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.model.UserInfo;

import android.content.Context;
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
	//用户列表
	private ArrayList<UserInfo> mUserList;
	//组列表
	private ArrayList<UserInfo> mGroupList;
	private UserAdapter userAdapter;
	private UserAdapter groupAdapter;
	private UserInfoBiz biz;
	private int nCurrentId=0;
	//是否可以点击
	private boolean click;
	
	public EidtUserView(Context context,IClickListener listener){
		this.mContext=context;
		inflater=LayoutInflater.from(mContext);
		this.listener=listener;
	}
	
	private boolean LoadData(){
		boolean result=true;
		this.click=true;
		
		//当前用户
		UserInfo info=SystemInfo.getGloableUser();
	
		if (biz==null) {
			biz=new UserInfoBiz();
		}
		
		//获取所有用户
		mUserList=biz.getUserList();
		
		if (info==null||mUserList==null||mUserList.size()==0) {
			Log.e("EidtUserView", "EidtUserView data=null.");
			return result=false;
		}
		
		for (int i = 0; i < mUserList.size(); i++) {
			if (info.getId()==mUserList.get(i).getId()) {
				//当前用户标示选中状态
				mUserList.get(i).setCheck(true);
			}
		}
		
		//组
		mGroupList=new ArrayList<UserInfo>();
		mGroupList=biz.getGroupList(info.getId(),true);
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
	
		//添加监听事件
		addListener();
		//获取数据
		if(LoadData()){
			//设置适配器
			setAdapter();
		}else {
			Toast.makeText(mContext, R.string.no_user, Toast.LENGTH_SHORT).show();
		}
		return view;
	}
	
	/**
	 * 设置适配器
	 */
	private void setAdapter(){
		userAdapter=new UserAdapter(mContext, mUserList, true);
		groupAdapter=new UserAdapter(mContext, mGroupList, false);
		
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
	}
	
	/**
	 * 根据不同用户显示相应的用户组
	 */
	private void updateGroup(int id){
		nCurrentId=id;
		ArrayList<UserInfo> list=biz.getGroupList(id, true);
		if (list==null||list.size()==0) {
			list=new ArrayList<UserInfo>();
		}
		
		mGroupList.clear();
		for (int i = 0; i < list.size(); i++) {
			mGroupList.add(list.get(i));
		}
		groupAdapter.notifyDataSetChanged();
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
	
	/**
	 * 删除用户
	 */
	private void delete(int id){
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
	
	/**
	 * 更新or添加or删除,成功时更新界面
	 * @param result-更新的结果
	 * @param updateId-是否更新当前id,删除的时候需要更新当前id
	 */
	public void update(boolean result,boolean updateId){
		if(result){
			//添加成功or修改成功or删除成功
			if (userAdapter!=null) {
				ArrayList<UserInfo> list=biz.getUserList();
				if (list==null||list.size()==0) {
					return;
				}
				
				if (updateId) {
					UserInfo info=SystemInfo.getGloableUser();
					if (info!=null) {
						nCurrentId=info.getId();
					}
				}
				
				mUserList.clear();
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
	
	/**
	 * 更新数据
	 */
	public void updateData(){
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
