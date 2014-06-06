package com.android.Samkoonhmi.macro.corba;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import com.android.Samkoonhmi.databaseinterface.AlarmBiz;
import com.android.Samkoonhmi.databaseinterface.DBTool;
import com.android.Samkoonhmi.databaseinterface.UserInfoBiz;
import com.android.Samkoonhmi.model.PassWordInfo;
import com.android.Samkoonhmi.model.UserInfo;
import com.android.Samkoonhmi.network.TcpServerManager;
import com.android.Samkoonhmi.plccommunicate.CmnPortManage;
import com.android.Samkoonhmi.print.AKPrint;
import com.android.Samkoonhmi.skenum.SYSTEM_OPER_TYPE;
import com.android.Samkoonhmi.skglobalcmn.DataCollect;
import com.android.Samkoonhmi.skglobalcmn.RecipeDataCentre;
import com.android.Samkoonhmi.skwindow.EmailOperDialog;
import com.android.Samkoonhmi.skwindow.SKMenuManage;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.system.SystemControl;
import com.android.Samkoonhmi.util.ContextUtl;
import com.android.Samkoonhmi.util.FileOper;
import com.android.Samkoonhmi.util.ParameterSet;
import com.android.Samkoonhmi.util.SavaInfo;
import com.android.Samkoonhmi.util.UsbScan;

/**
 * 系统操作API
 */
public class SystemHolder extends PHolder{

	/**
	 * 锁屏
	 * @param pw-设定解锁密码
	 * @param info-提示信息
	 */
	public boolean lockHmi(String pw,String info){
		//Log.d("SystemHolder", "pw="+pw+",info="+info);
		if (pw!=null) {
			pw=pw.trim();
		}
		if (info!=null) {
			info=info.trim();
		}
		boolean result=false;
		PassWordInfo msg=new PassWordInfo();
		msg.setsPwdStr(pw);
		msg.setsTimeOut(info);
		result=SystemControl.peculiarOper(SYSTEM_OPER_TYPE.SYSTEM_LOCK, msg);
		return result;
	}
	
	/**
	 * 解锁
	 * @param pw-解锁密码
	 */
	public boolean unLockHmi(String pw){
		if (pw!=null) {
			pw=pw.trim();
		}
		boolean result=false;
		PassWordInfo msg=new PassWordInfo();
		msg.setsPwdStr(pw);
		result=SystemControl.peculiarOper(SYSTEM_OPER_TYPE.SYSTEM_UNLOCK, msg);
		return result;
	}
	
	/**
	 * 系统重启
	 */
	public void reboot(){
		if (SKSceneManage.getInstance().mContext==null) {
			return;
		}
		Intent intent = new Intent();
		intent.setAction("com.samkoon.reboot");
		SKSceneManage.getInstance().mContext.sendBroadcast(intent);
	}
	
	/**
	 * 重启Hmi组态软件
	 */
	public void rebootHmi(){
		SavaInfo.setState(2);
		android.os.Process.killProcess(android.os.Process.myPid()); 
	}
	
	/**
	 * 保存信息到文件
	 * @param key-用于获取信息key
	 * @param info-保存信息
	 */
	public boolean saveInfoToFile(String key,String info){
		if (key==null||info==null||key.equals("")) {
			return false;
		}
		boolean result=false;
		result=FileOper.getInstance().saveInfoToFile(key, info);
		return result;
	}
	
	/**
	 * 从文件中读取对应的信息
	 * @param key
	 */
	public String readInfoFromFile(String key){
		String result="";
		if (key==null||key.equals("")) {
			return "";
		}
		result=FileOper.getInstance().readInfoFromFile(key);
		return result;
	}
	
	/**
	 * 设置采集导出别名
	 * @param name=采集名称
	 * @param alias=导出别名
	 */
	public boolean saveCollectFile(String name,String alias){
		if (name==null||alias==null||name.equals("")||alias.equals("")) {
			return false;
		}
		boolean result=false;
		result=FileOper.getInstance().saveInfoToFile(name, alias);
		return result;
	}
	
	/**
	 * 打印
	 * @param id-画面序号
	 * @param model-打印机类型
	 */
	public boolean printBitmap(int id,int model,int port){
		if (id<0||model<0) {
			return false;
		}
		
		return AKPrint.getInstance().printBitmap(id, model,port);
	}
	
	/**
	 * 打印
	 * @param name-画面名称
	 * @param model-打印机类型
	 */
	public boolean printBitmap(String name,int model,int port){
		if (name==null||name.equals("")||model<0) {
			return false;
		}
		
		return AKPrint.getInstance().printBitmap(name, model,port);
	}
	
	/**
	 * 打印
	 * @param name-画面名称
	 * @param model-打印机类型
	 */
	public boolean printText(String text,int model,int port){
		if (text==null||text.equals("")||model<0) {
			return false;
		}
		
		return AKPrint.getInstance().printText(text, model, port);
	}
	
	/**
	 *打印
	 * @param name-画面名称
	 * @param model-打印机类型
	 */
	public boolean printTexts(Vector<String> list,int model,int port){
		if(list==null||list.isEmpty()){
			return false;
		}
		
		return AKPrint.getInstance().printTexts(list, model, port);
	}
	
	
	/**
	 * 用户登录
	 * @param usrName
	 * @param usrPassword
	 * @return
	 */
	public boolean login(String usrName,String usrPassword){
		boolean result=false;
		result = ParameterSet.getInstance().getUserFromBase(
				usrName, usrPassword);
		SKSceneManage.getInstance().updateState();
		return result;
	}
	
	/**
	 * 按时段删除
	 * @param startTime
	 * @return
	 */
	public static boolean deleteCollectByTime(int nGroupId, String startTime , String endTime){
		
		if(startTime==null||endTime==null){
			return false;
		}
		
		String timeS[]=startTime.split("-");
		int[] intTime = {0,0,0,0,0,0};
		int startLen = Math.min(intTime.length, timeS.length);
		String timeE[]=endTime.split("-");
		int[] intEndTime = {0,0,0,0,0,0};
		int endLen = Math.min(intEndTime.length, timeE.length);
		boolean hasEnd=false;
		try{
			for(int i=0;i<startLen;i++){
				if(i==1){
					intTime[i]=Integer.parseInt(timeS[i])-1;
				}else{
					intTime[i]=Integer.parseInt(timeS[i]);
				}
			}
			for(int j=0;j<endLen;j++){				
				if(j==1){
					if((intEndTime[j]=Integer.parseInt(timeE[j])-1)!=-1){
						hasEnd=true;
					}
				}else{
					if((intEndTime[j]=Integer.parseInt(timeE[j]))!=0){
						hasEnd=true;
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
		Calendar c = Calendar.getInstance();
		c.set(intTime[0], intTime[1], intTime[2], intTime[3], intTime[4], intTime[5]);
		long start = c.getTimeInMillis();
		long temp = start%1000;
		start-=temp;
		c.set(intEndTime[0], intEndTime[1], intEndTime[2], intEndTime[3], intEndTime[4], intEndTime[5]);
		long end = c.getTimeInMillis();
		temp=end%1000;
		end=end-temp+999;
		ArrayList<Integer> mList = new ArrayList<Integer>();
		mList.add(nGroupId);
		DataCollect.getInstance().msgClearHistoryByTime(mList, start, end,hasEnd);
		
		return true;
	}
	
	/**
	 * 配方导入
	 * @param name-配方组名称
	 * @param type-导入类型
	 *        type=0 U盘
	 *        type=1 SD卡
	 */
	public boolean recipeInfo(String name,int type){
		
		return RecipeDataCentre.getInstance().recipeInfo(name, type);
	}
	
	/**
	 * 配方导出
	 * @param name-配方组名称
	 * @param type-导出类型
	 *        type=0 U盘
	 *        type=1 SD卡
	 */
	public boolean recipeExport(String name,int type){
		
		return RecipeDataCentre.getInstance().recipeExport(name, type);
	}
	
	/**
	 * 配方组保存
	 * @param name-配方组名称
	 * @param data-配方组数据
	 *        ArrayList<E> 存储该组所有配方
	 *        String[] 存储某一个配方数据
	 */
	public boolean recipeGroupSave(String name,ArrayList<String[]> data){
		
		return RecipeDataCentre.getInstance().recipeGroupSave(name, data);
	}
	
	/**
	 * 配方组保存
	 * @param gid-配方组id
	 * @param data-配方组数据
	 *        ArrayList<E> 存储该组所有配方
	 *        String[] 存储某一个配方数据
	 */
	public boolean recipeGroupSave(int gid,ArrayList<String[]> data){
		
		return RecipeDataCentre.getInstance().recipeGroupSave(gid, data);
	}
	
	/**
	 * 配方保存
	 * @param name-配方组名称
	 * @param 配方id,即配方在组里面的序号，从0开始
	 * @param data-配方数据
	 */
	public boolean recipeSave(String name,int id,String[] data){
		
		return RecipeDataCentre.getInstance().recipeSave(name, id, data);
	}
	
	/**
	 * 配方保存
	 * @param gid-配方组id
	 * @param 配方id,即配方在组里面的序号，从0开始
	 * @param data-配方数据
	 */
	public boolean recipeSave(int gid,int id,String[] data){
		
		return RecipeDataCentre.getInstance().recipeSave(gid, id, data);
	}
	
	/**
	 * 配方组数据复制
	 * @param fromName-源配方组名称
	 * @param toName-目标配方组名称
	 * @param type-源配方组文件位置
	 *        type=0 U盘
	 *        type=1 SD卡
	 *        type=2  内部数据库
	 */
	public boolean recipeGroupCopy(String fromName,String toName,int type){
		
		return RecipeDataCentre.getInstance().recipeGroupCopy(fromName, toName, type);
	}
	
	/**
	 * 配方组数据复制
	 * @param fromId-源配方组id
	 * @param toId-目标配方组id
	 * @param type-源配方组文件位置
	 *        type=0 U盘
	 *        type=1 SD卡
	 *        type=2  内部数据库
	 */
	public boolean recipeGroupCopy(int fromId,int toId,int type){
		
		return RecipeDataCentre.getInstance().recipeGroupCopy(fromId, toId, type);
	}
	
	/**
	 * 配方数据复制
	 * @param name-配方组名称
	 * @param fromId-源配方id
	 * @param toId-目标配方id
	 * @param type-源配方文件位置
	 *        type=0 U盘
	 *        type=1 SD卡
	 *        type=2  内部数据库
	 */
	public boolean recipeCopy(String name,int fromId,int toId,int type){
		
		return RecipeDataCentre.getInstance().recipeCopy(name, fromId, toId, type);
	}
	
	/**
	 * 配方数据复制
	 * @param id-配方组id
	 * @param fromId-源配方id
	 * @param toId-目标配方id
	 * @param type-源配方文件位置
	 *        type=0 U盘
	 *        type=1 SD卡
	 *        type=2  内部数据库
	 */
	public boolean recipeCopy(int id,int fromId,int toId,int type){
		
		return RecipeDataCentre.getInstance().recipeCopy(id, fromId, toId, type);
	}
	
	/**
	 * 删除配方组
	 * @param gid-配方组id
	 */
	public boolean recipeGroupDelete(int gid){
		
		return RecipeDataCentre.getInstance().recipeGroupDelete(gid);
	}
	
	/**
	 * 删除配方组
	 * @param gid-配方组名称
	 */
	public boolean recipeGroupDelete(String name){
		
		return RecipeDataCentre.getInstance().recipeGroupDelete(name);
	}
	
	/**
	 * 删除配方
	 * @param gid-配方组id
	 * @param rid-配方id
	 */
	public boolean recipeDelete(int gid,int rid){
		
		return RecipeDataCentre.getInstance().recipeDelete(gid, rid);
	}
	
	/**
	 * 删除配方
	 * @param gid-配方组名称
	 * @param rid-配方id
	 */
	public boolean recipeDelete(String name,int rid){
		
		return RecipeDataCentre.getInstance().recipeDelete(name, rid);
	}
	
	/**
	 * 当前配方写入plc
	 */
	public void currentRecipeWriteToPlc(){
		RecipeDataCentre.getInstance().currentRecipeWriteToPlc();
	}
	
	/**
	 * 从plc读取当前配方
	 */
    public void readCurrentRecipeFromPlc(){
    	RecipeDataCentre.getInstance().readCurrentRecipeFromPlc();
	}
	
    /**
     * 保存当前配方
     */
    public void saveCurrentRecipe(){
    	RecipeDataCentre.getInstance().saveCurrentRecipe();
    }
   
    /**
     * 添加配方
     * @param gName-配方组名称
     * @param rName-配方名称
     */
    public boolean addRecipe(String gName,String rName){
    	if (gName==null||gName.equals("")||rName==null||rName.equals("")) {
			return false;
		}
    	return RecipeDataCentre.getInstance().addRecipe(gName,rName);
    }
    
    /**
     * 添加配方
     * @param gName-配方组名称
     * @param rName-配方名称
     * @param data-配方元素数据
     */
    public boolean addRecipe(String gName,String rName,double data[]){
    	if (gName==null||gName.equals("")||rName==null||rName.equals("")||data==null||data.length==0) {
			return false;
		}
    	return RecipeDataCentre.getInstance().addRecipe(gName, rName,data);
    }
	
    /**
     * 获取配方名称
     * @param gName=配方组名称
     */
    public String[] getRecipeName(String gName,int lId){
    	if(gName==null||gName.equals("")||lId<0){
    		return null;
    	}
    	return RecipeDataCentre.getInstance().getRecipeName(gName,lId);
    }
    
    /**
     * 获取配方元素名称
     * @param gName=配方组名称
     * @param rId=配方序号，从0开始
     * @param lId=语言序号，从0开始
     */
    public String[] getRecipeElement(String gName,int rId,int lId){
    	if(gName==null||gName.equals("")||rId<0){
    		return null;
    	}
    	return RecipeDataCentre.getInstance().getRecipeElement(gName, rId,lId);
    }
    
    /**
     * 获取配方数据
     * @param gName=配方组名称
     */
    public ArrayList<String[]> getRecipeData(String gName){
    	if(gName==null||gName.equals("")){
    		return null;
    	}
    	return RecipeDataCentre.getInstance().getRecipeData(gName);
    }
    
    /**
     * 获取报警数据
     * @param aName-报警组名称
     * @param nTop-起始序号
     * @param num-获取行数
     * @param sTime-开始时间 ，格式为 “2013-11-24 22:12:00”
     * @param eTime-结束时间，格式为“2013-11-25 12:00:00”
     */
    public ArrayList<String[]> getAlarmData(String aName,int nTop,int num,String sTime,String eTime){
    	if (aName==null||aName.equals("")||sTime==null||sTime.equals("")||eTime==null
    			||eTime.equals("")||nTop<0||num<0) {
			return null;
		}
    	
    	AlarmBiz biz=new AlarmBiz();
    	return biz.getAlarmData(aName, nTop, num, sTime, eTime);
    }
    
    /**
     * 获取历史采集数据
     * @param cName-采集组名称
     * @param nTop-起始序号
     * @param num-获取行数
     * @param sTime-开始时间 ，格式为 “2013-11-24 22:12:00”
     * @param eTime-结束时间，格式为“2013-11-25 12:00:00”
     */
    public ArrayList<String[]> getCollectData(String cName,int nTop,int num,String sTime,String eTime){
    	if (cName==null||cName.equals("")||sTime==null||sTime.equals("")||eTime==null
    			||eTime.equals("")||nTop<0||num<0) {
			return null;
		}
    	return DataCollect.getInstance().getCollectData(cName, nTop, num, sTime, eTime);
    }
    
    /**
     * 修改配方名称
     * @param gName=配方组名称
     * @param rId=配方序号，从0开始
     * @param newName=修改后的配方名称
     * @param lId=语言序号，从0开始
     */
    public boolean updateRecipeName(String gName,int rId,String newName,int lId){
    	if (gName==null||gName.equals("")||newName==null||newName.equals("")||rId<0||lId<0) {
			return false;
		}
    	return RecipeDataCentre.getInstance().updateRecipeName(gName, rId, newName, lId);
    }
    
    /**
     * 修改配方元素名称
     * @param gName=配方组名称
     * @param eId=元素序号，从0开始
     * @param eName=修改后的元素名称
     * @param lId=语言序号，从0开始
     */
    public boolean updateRecipeElement(String gName,int eId,String eName,int lId){
    	if (gName==null||gName.equals("")||eName==null||eName.equals("")||eId<0||lId<0) {
			return false;
		}
    	return RecipeDataCentre.getInstance().updateRecipeElement(gName, eId, eName, lId);
    }
    
	/**
	 * 获取条形码
	 */
	public String getScanCode(){
		return UsbScan.getInstance().getScanCode();
	}
	
	/**
	 * 获取所有未读取条形码
	 */
	public  ArrayList<String> getScanCodes(){
		return UsbScan.getInstance().getScanCodes();
	}
	
	/**
	 * 修改用户密码
	 * @param name=用户名称
	 * @param oldPwd=旧密码
	 * @param newPwd=新密码
	 * @param force=true,表示强制更新
	 */
	public boolean updateUserPwd(String name,String oldPwd,String newPwd,boolean force){
		UserInfoBiz biz=DBTool.getInstance().getmUserInfoBiz();
		name=name.trim();
		oldPwd=oldPwd.trim();
		newPwd=newPwd.trim();
		//Log.d("SystemHolder", "name="+name+",newPwd="+newPwd);
		if (!force) {
			//需要匹对密码是否正确
			boolean b=biz.isPwdExist(name,oldPwd);
			if (!b) {
				return false;
			}
		}
		
		//修改用户密码
		UserInfo temp=new UserInfo();
		temp.setName(name);
		temp.setPassword(newPwd);
		biz.updateUserPwd(temp);
		return true;
	}
	
//	public void sendEmail(String fromName, String fromPassWd, String fromSever,  ArrayList<String>toName, int sendType){
//		EmailOperDialog.sendFiles(fromName, fromPassWd, fromSever, toName, sendType);
//	}
//	
	/**
	 * 
	 * @param fromName 发件邮箱，如：example@163.com
	 * @param fromPassWd 发件人密码 
	 * @param fromSever 发件人服务器，smtp.163.com
	 * @param toName   收件人邮箱，如果有多个用逗号隔开， 如： "aa@163.com,bb@163.com"
	 */
	public void setEmailInfo(String fromName, String fromPassWd, String fromSever, String toName){
		EmailOperDialog.setEmailInfo(fromName, fromPassWd,  fromSever, toName);
	}
	
	/**
	 * 发送报警相关信息  注：需要确保调用过setEmailInfo之后 才能使用
	 * @param sendName 报警组名称， 可多组报警用 , 分开
	 * @param period 报警时间段 以小时为单位， 从当前的时间前推，如 ：10 代表从现在 和  现在-10h  之间的数据 ，填0表示所有的历史数据
	 */
	public void sendEmailAlarm(String sendName, int period){
		EmailOperDialog.sendAlarm(sendName, period);
	}
	
	/**
	 * 发送配方附件  注：需要确保调用过setEmailInfo之后 才能使用
	 * @param sendName配方组组名称，可多组配方用 , 分开
	 */
	public void sendEmailRecipe(String sendName){
		EmailOperDialog.sendRecipe( sendName);
	}
	
	/**
	 * 发送历史数据附件  注：需要确保调用过setEmailInfo之后 才能使用
	 * @param sendName 历史数据名称，多组可以用,分开
	 * @param period  历史时间段   以小时为单位， 从当前的时间前推，如 ：10 代表从现在 和  现在-10h  之间的数据 ，填0表示所有的历史数据
	 */
	public void sendHistory(String sendName, int period){
		EmailOperDialog.sendHistory( sendName,  period);
	}
	
	/**
	 * 发送留言信息  注：需要确保调用过setEmailInfo之后 才能使用
	 */
	public void sendMessage(){
		EmailOperDialog.sendMessage();
	}
	
	/**
	 * 发送 文字内容 邮件
	 * @param text - 文字内容
	 */
	public void sendTextContent(String  text){
		EmailOperDialog.sendTextContent(text);
	}
	
	/**
	 * 设置IP地址
	 * @param ip=IP地址，例如 192.168.1.100
	 * @param mask-子网掩码，例如 255.255.255.0
	 * @param gate-网关，例如 192.168.1.1
	 * @param dns-DNS
	 * @param modle-类型，0 为动态ip，1为静态ip
	 * 当modle=0 时，其他4个参数可以不填
	 * @param type-类型，1-以太网，2-wifi
	 */
	public boolean setIp(String ip,String mask,String gate,String dns,int modle,int type){
		if (modle==1) {
			if (ip==null||ip.equals("")||mask==null||mask.equals("")||gate==null||gate.equals("")) {
				return false;
			}
		}
		Intent intent=new Intent();
		intent.setAction("com.samkoon.ethernet.setting");
		if (modle==0) {
			intent.putExtra("eth_type", "DHCP");
		}else {
			boolean isIp=validate(ip);
			if (!isIp) {
				return false;
			}
			boolean route=validate(gate);
			if (!route) {
				return false;
			}
			boolean isMask=validate(mask);
			if (!isMask) {
				return false;
			}
			
			boolean isDns=false;
			if (dns==null||dns.equals("")) {
				isDns=true;
			}else{
				isDns=validate(dns);
				if (!isDns) {
					return false;
				}
			}
			
			if (isIp&&route&&isMask&&isDns) {
				intent.putExtra("eth_type", "STATIC");
				Bundle bundle=new Bundle();
				bundle.putString("eth_info_ip", ip);
				bundle.putString("eth_info_gate", gate);
				bundle.putString("eth_info_mask", mask);
				bundle.putString("eth_info_dns", dns);
				intent.putExtra("eth_info", bundle);
			}else {
				return false;
			}
		}
		ContextUtl.getInstance().sendBroadcast(intent);
		return true;
	}
	
	/**
	 * 启动数据传输服务
	 * @param start-启动服务
	 * @param port-端口
	 */
	public boolean startDataServer(boolean start,int port){
		if (port<0) {
			return false;
		}
		if (start) {
			TcpServerManager.getInstance().onStart(ContextUtl.getInstance());
		}else {
			TcpServerManager.getInstance().onStop();
		}
		saveInfo(start,port+"");
		return true;
	}
	
	/**
	 * 创建数据库表
	 */
	public boolean sqlCreateTable(String sql){
		return FileOper.getInstance().sqlCreateTable(sql);
	}
	
	/**
	 * 数据库 增删改
	 */
	public boolean sqlExec(String sql){
		return FileOper.getInstance().sqlExec(sql);
	}
	
	/**
	 * 查询语句
	 */
	public Cursor sqlSelect(String sql){
		return FileOper.getInstance().sqlSelect(sql);
	}
	
	/**
	 * 修改网络协议ip地址
	 */
	public boolean updateNetProtocol(String ip,int port,String protocolName){
		return CmnPortManage.getInstance().updateNetProtocol(ip, protocolName, port);
	}

	/**
	 * @param count-蜂鸣次数
	 * @param time-时间间隔
	 * @return
	 */
	public boolean beep(int count,int time){
		return SKSceneManage.getInstance().beep(count, time);
	}
	
	/**
	 * 保存信息
	 */
	private void saveInfo(boolean start,String port){
		SharedPreferences.Editor mEditor = ContextUtl.getInstance().getSharedPreferences(
				"information", 0).edit();
		mEditor.putBoolean("net_server", start);
		mEditor.putString("net_port", port);
		mEditor.commit();
	}
	
	/**
	 * @param info
	 * @return
	 */
	private boolean validate(String info) {
		
		Pattern pattern = Pattern
				.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
		Matcher matcher = pattern.matcher(info); // 以验证127.400.600.2为例
		return matcher.matches();
	}

	
}
