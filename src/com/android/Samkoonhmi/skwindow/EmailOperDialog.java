package com.android.Samkoonhmi.skwindow;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import android.R.integer;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.network.CollentFileServer;
import com.android.Samkoonhmi.skglobalcmn.RecipeDataCentre;
import com.android.Samkoonhmi.util.AlarmGroup;
import com.android.Samkoonhmi.util.ContextUtl;
import com.android.Samkoonhmi.util.GMailSender;
import com.android.Samkoonhmi.util.MessageBoardUtil;

public class EmailOperDialog extends Dialog{
	
	private ViewFlipper flipper;
	private Activity activity;
	private EmailMainView mainView;
	private EmailSetView setView;
	private EmailDelView delView;
	private EmailAddView addView;
	private EmailFilesView filesView;
	
	
	public static final int DIALOG_MAINVIEW = 0; //主界面
	public static final int DIALOG_SETVIEW = 1;  //设置界面
	public static final int DIALOG_DELVIEW = 2;  //删除联系人界面
	public static final int DIALOG_ADDVIEW = 3;  //添加联系人界面
	public static final int DIALOG_FILEVIEW = 4;  //添加附件界面
	public static int SendCount = 0;
	public static int CompleteCount = 0;
	
	public static final String SET_FROM = "set_from";
	public static final String SET_TO = "set_to";
	public static final String SET_PASSWORD = "set_password";
	public static final String SET_SERVER = "set_server";
	
	public static final String FROM_DEL = "from_del";//
	
	public EmailOperDialog(Activity activity) {
		super(activity);
		this.activity = activity;
		mainView = new EmailMainView(activity, mIClickListener);
		setView = new EmailSetView(activity, mIClickListener);
		delView = new EmailDelView(activity, mIClickListener);
		addView = new EmailAddView(activity, mIClickListener);
		filesView = new EmailFilesView(activity, mIClickListener);
		
        this.setCanceledOnTouchOutside(false);
		
		//进行初始化数据
		SendCount = 0; 
		CompleteCount = 0;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.email_group);

		flipper = (ViewFlipper) findViewById(R.id.email_viewflipper);
		flipper.addView(mainView.addView(getEmailInfo()));
		flipper.addView(setView.addView(getEmailInfo()));
		flipper.addView(delView.addView(getEmailInfo()));
		flipper.addView(addView.addView(getEmailInfo()));
		flipper.addView(filesView.addView());
	}
	
	
	public void showDialog(){
		if (isShowing()) {
			return;
		}
		show();
	}
	



	public interface IClickListener {
		
		public void onJumpTo(int index, Bundle bundle);
		public void onExit();
		public void sendEmail(String content);
	}
	
	public IClickListener mIClickListener = new IClickListener() {
		
		@Override
		public void onJumpTo(int index, Bundle bundle) {
			// TODO Auto-generated method stub
			if (flipper != null) {
				flipper.setDisplayedChild(index);
				saveEmailInfo(bundle);
				updateChildView(index, getEmailInfo());
			}
		}

		@Override
		public void onExit() {
			// TODO Auto-generated method stub
			cancel();
		}

		@Override
		public void sendEmail(String content) {
			// TODO Auto-generated method stub
			sendFiles(content);
		}
	};
	
	//进行更新childview
	private void updateChildView(int index, Bundle bundle){
		if (bundle == null) {
			return ;
		}
		
		if (index == DIALOG_MAINVIEW) {
			mainView.updateView(bundle);
		}
		else if (index == DIALOG_DELVIEW) {
			delView.updateView(bundle);
		}
		else if (index == DIALOG_ADDVIEW) {
			addView.updateView(bundle);
		}
	}
	
	
	  
		//保存配置信息
		public void saveEmailInfo(Bundle bundle){
			if (bundle == null) {
				return;
			}
			SharedPreferences sharedPreferences = activity.getSharedPreferences("email_information", Context.MODE_PRIVATE);
			Editor editor = sharedPreferences.edit();
			if (!TextUtils.isEmpty(bundle.getString(SET_FROM))) {//保存发件人
				editor.putString(SET_FROM, bundle.getString(SET_FROM));
			}
			if (!TextUtils.isEmpty(bundle.getString(SET_SERVER))) { //保存服务器
				editor.putString(SET_SERVER, bundle.getString(SET_SERVER));
			} 
			if (!TextUtils.isEmpty(bundle.getString(SET_PASSWORD))) { //保存密码
				editor.putString(SET_PASSWORD, bundle.getString(SET_PASSWORD));
			} 
			boolean fromDel = bundle.getBoolean(FROM_DEL, false);
			if (!TextUtils.isEmpty(bundle.getString(SET_TO)) || fromDel) {
				editor.putString(SET_TO, bundle.getString(SET_TO));  //保存收件人
			}
			
			
			editor.commit();
		}
		
		//读取配置信息
		public Bundle getEmailInfo(){
			Bundle bundle = new Bundle();
			SharedPreferences shPreferences = activity.getSharedPreferences("email_information", Context.MODE_PRIVATE);
			String from =  shPreferences.getString(SET_FROM, "example@163.com");
			String to = shPreferences.getString(SET_TO, "");
			String server = shPreferences.getString(SET_SERVER, "");
			String password = shPreferences.getString(SET_PASSWORD, "");
			
			bundle.putString(SET_FROM, from);
			bundle.putString(SET_TO, to);
			bundle.putString(SET_SERVER, server);
			bundle.putString(SET_PASSWORD, password);
			
			return bundle;
		}
		
		
		
		//邮件发送的相关接口
		private  static Bundle emailInfo = new Bundle();  
		/**
		 * 获取邮件内容
		 * @return
		 */
		public static synchronized Bundle getScriptInfo(){
			if(emailInfo == null)
			{
				emailInfo = new Bundle();
			}
			return emailInfo;
		}
		
		/**
		 * 
		 * @param fromName 发件邮箱，如：example@163.com
		 * @param fromPassWd 发件人密码 
		 * @param fromSever 发件人服务器，smtp.163.com
		 * @param toName   收件人邮箱，如果有多个用逗号隔开， 如： "aa@163.com,bb@163.com"
		 */
		public static void setEmailInfo(String fromName, String fromPassWd, String fromSever, String toName){
			if(TextUtils.isEmpty(fromName) || TextUtils.isEmpty(fromPassWd) || TextUtils.isEmpty(fromSever)
					||  TextUtils.isEmpty(toName)){
				return ;
			}
			
			getScriptInfo().putString(SET_FROM, fromName);
			getScriptInfo().putString(SET_TO, toName);
			getScriptInfo().putString(SET_SERVER, fromSever);
			getScriptInfo().putString(SET_PASSWORD, fromPassWd);
		}
		
		static class SendBean{
			public int sendtype; // 传输类型  1代表 添加邮件附件 、10 代表配方、100代表历史数据，1000代表留言信息。
			public ArrayList<String> sendNams; //发送名称
			public int sendperoid; //发送时间间隔 以小时为单位， 
			public String emailContent; //发送的邮件内容
		}
		
		public static Vector<SendBean> sendList = new Vector<SendBean>();
		
		/**
		 * 发送报警相关信息
		 * @param sendName 报警组名称， 可多组报警用 , 分开
		 * @param period 报警时间段 以小时为单位， 从当前的时间前推，如 ：10 代表从现在 和  现在-10h  之间的数据 
		 */
		public static void sendAlarm(String sendName, int period){
			if(TextUtils.isEmpty(sendName)){
				return;
			}
			SendBean bean = new SendBean();
			bean.sendtype = 1; 
			bean.sendperoid = period;
			bean.emailContent = " ";
			
			String[] names = sendName.split(",");
			if(names != null && names.length > 0){
				bean.sendNams = new ArrayList<String>();
				for(int i = 0; i < names.length; i++){
					bean.sendNams.add(names[i]);
				}
			}
			
			// 进行发送
			sendFiles(bean);
		}
		
		/**
		 * 发送配方附件
		 * @param sendName配方组组名称，可多组配方用 , 分开
		 */
		public static void sendRecipe(String sendName){
			if(TextUtils.isEmpty(sendName)){
				return ;
			}
			SendBean bean = new SendBean();
			bean.sendtype = 10 ;
			bean.sendperoid = 0;
			bean.emailContent =" ";
			
			String [] names = sendName.split(",");
			if(names != null && names.length > 0){
				bean.sendNams = new ArrayList<String>();
				for(int i = 0; i < names.length; i++){
					bean.sendNams.add(names[i]);
				}
			}
			
			//进行发送
			sendFiles(bean);
		}
		
		
		private static Handler mHandler = new Handler(Looper.getMainLooper());
		
		/**
		 * 发送历史数据附件
		 * @param sendName 历史数据名称，多组可以用,分开
		 * @param period  历史时间段   以小时为单位， 从当前的时间前推，如 ：10 代表从现在 和  现在-10h  之间的数据 
		 */
		public static void sendHistory(String sendName, int period){
			if(TextUtils.isEmpty(sendName)){
				return ;
			}
			
			SendBean bean = new SendBean();
			bean.sendtype = 100;
			bean.sendperoid = period;
			bean.emailContent = " ";
			
			String [] names = sendName.split(",");
			if(names != null && names.length > 0){
				bean.sendNams = new ArrayList<String>();
				for(int i = 0; i < names.length; i++){
					bean.sendNams.add(names[i]);
				}
			}

			//进行发送
			sendFiles(bean);
		}
		
		/**
		 * 发送留言信息
		 */
		public static void sendMessage(){
			if(!MessageBoardUtil.getInstance().hasMessage()){
				return ;
			}
			
			SendBean bean = new SendBean();
			bean.sendtype = 1000;
			bean.sendperoid = 0;
			bean.emailContent = " ";
			
			//进行发送
			sendFiles(bean);
		}
		
		/**
		 * 发送文字内容
		 */
		
		public static void sendTextContent(String textContent)
		{
			if(TextUtils.isEmpty(textContent)){
				textContent = " ";
			}
			
			SendBean bean = new SendBean();
			bean.sendtype = 0;
			bean.sendperoid = 0;
			bean.emailContent = textContent;
			
			sendFiles(bean);
		}
		
		/**
		 * 脚本发送邮件的接口
		 * @param bean
		 */
		public static synchronized void sendFiles(SendBean bean){
			//判断基本资料是否合理
			if(TextUtils.isEmpty(getScriptInfo().getString(SET_FROM)) || TextUtils.isEmpty(getScriptInfo().getString(SET_PASSWORD))
					||TextUtils.isEmpty(getScriptInfo().getString(SET_SERVER)) || TextUtils.isEmpty(getScriptInfo().getString(SET_TO))){
				return ;
			}
			
			sendList.add(bean);
			if(isSending){
				return;
			}
			else {
				isSending = true;
			}
			send();
		}
		
		static void send(){
			deletFiles();//删除缓存文件
			//初始化参数
			SendCount = 0 ;
			CompleteCount = 0;
			final SendBean bean = sendList.get(0);
			
			//生成相关附件
			if(bean.sendtype % 10 == 1 && EmailFilesView.getScriptAlarm(bean.sendNams).size() > 0){
				 // 生成报警数据文件
				SendCount += 1;
				AlarmGroup.getInstance().emailScript(EmailFilesView.getScriptAlarm(bean.sendNams), bean.sendperoid);
			}
			if((bean.sendtype/10)%10 == 1 && EmailFilesView.getScriptRecipe(bean.sendNams).size() > 0){
				//生成配方数据
				SendCount += 1;
				RecipeDataCentre.getInstance().msgWriteRecipeSToFiles(EmailFilesView.getScriptRecipe(bean.sendNams));
			}
			if((bean.sendtype/100)%10 == 1 && EmailFilesView.getScriptHistory(bean.sendNams).size() > 0){
				//生成历史数据
				SendCount += 1;
				CollentFileServer fileServer = new CollentFileServer();
				fileServer.startEmailScript(EmailFilesView.getScriptHistory(bean.sendNams), bean.sendperoid);
			}
			if((bean.sendtype/1000) == 1 && MessageBoardUtil.getInstance().hasMessage()){
				//生成留言信息
				MessageBoardUtil.getInstance().getMessageContent();
			}
			
			//创建线程  发送邮件
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					while (true) {
						if (CompleteCount >= SendCount) { // 添加附件完成之后，进入发送的线程， 包含没有附件的情况
							try {
								File fileDir = new File("/data/data/com.android.Samkoonhmi/files/email_files/");
								File []fileList  = fileDir.listFiles();
								ArrayList<String> filePath = new ArrayList<String>();
								if (fileList != null && fileList.length > 0) {
									for(int i = 0; i < fileList.length; i++) {
										File file = fileList[i];
										filePath.add(file.getPath());
									}
								}
								
								GMailSender sender =  new GMailSender(getScriptInfo(), bean.emailContent, filePath);
								 sender.sendMail();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							//邮件发送完成之后 进行的操作
							isSending = false;
							sendList.remove(0);
							
							//如果还存在没有发送的邮件， 那么就继续发送
							if(sendList.size() > 0){
								mHandler.post(new Runnable() {
									
									@Override
									public void run() {
										// TODO 让递归调用回到主界面
										send();
									}
								});
							}
							
							break;
							
						}
						
					}
					
				}
			}).start();
		}
		private static boolean isSending = false;
		
		
		/**
		 * 
		 * @param emailContent -邮件内容
		 */
		public synchronized void sendFiles(final String emailContent){
			//首先删除文件
			deletFiles();
			showSendToast(0);
			if (filesView.getAlarmSelectList() != null && filesView.getAlarmSelectList().size() > 0) {//生成报警附件
				SendCount += 1;
				AlarmGroup.getInstance().emailFiles(filesView.getAlarmSelectList());
			}
			if (filesView.getRecipeSelectList() != null && filesView.getRecipeSelectList().size() > 0) {//生成配方附件
				SendCount += 1;
				RecipeDataCentre.getInstance().msgWriteRecipeSToFiles(filesView.getRecipeSelectList());
			}
	
			if (filesView.getHistorySelectList() != null && filesView.getHistorySelectList().size() > 0) {//生成历史数据附件
				SendCount += 1;
				CollentFileServer fileServer = new CollentFileServer();
				fileServer.startEmailFiles(filesView.getHistorySelectList());
			}
			
			if (filesView.haveMessages()) {//生成
				MessageBoardUtil.getInstance().getMessageContent();
			}
			
	
			//创建线程  发送邮件
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					int toastIdex = 1;
					while (true) {
						if (CompleteCount >= SendCount) { // 添加附件完成之后，进入发送的线程， 包含没有附件的情况
							try {
								File fileDir = new File("/data/data/com.android.Samkoonhmi/files/email_files/");
								File []fileList  = fileDir.listFiles();
								ArrayList<String> filePath = new ArrayList<String>();
								if (fileList != null && fileList.length > 0) {
									for(int i = 0; i < fileList.length; i++) {
										File file = fileList[i];
										filePath.add(file.getPath());
									}
								}
								
								GMailSender sender =  new GMailSender(getEmailInfo(), emailContent, filePath);
								toastIdex = sender.sendMail();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								toastIdex = 2;
							}
							//邮件发送完成之后 进行的操作
							showSendToast(toastIdex);
							cancel();
							
							break;
							
						}
						
					}
					
				}
			}).start();
			
		}
		
		//删除指定文件夹下的文件
		private static void  deletFiles(){
			File fileDir = new File("/data/data/com.android.Samkoonhmi/files/email_files/");
			File []fileList  = fileDir.listFiles();
			if (fileList == null || fileList.length == 0 ) {
				return;
			}
			
			for(int i = 0; i < fileList.length; i++){
				File file = fileList[i];
				if (file != null) {
					file.delete();
					file = null;
				}
			}
			
		}
		
		//发送 进行提示
		private int showId = 0;
		private void showSendToast(int index){
			switch(index){
			case 0: //开始发送
				showId = R.string.email_start_send;
				break;
			case 1://发送成功
				showId = R.string.email_send_sucess;
				break;
			case 2://发送失败
				showId = R.string.email_send_filed;
				break;
			}
			
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					SKToast.makeText(activity.getString(showId), Toast.LENGTH_SHORT).show();
				}
			});
		}
		
	
	

}
