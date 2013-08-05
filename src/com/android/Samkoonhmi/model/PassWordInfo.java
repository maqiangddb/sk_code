package com.android.Samkoonhmi.model;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

/**
 * 密码表
 * @author Administrator
 *
 */
public class PassWordInfo  implements Parcelable{
	private  int Id;       //id
	private String sPwdStr;//密码串
	private String sTimeLimit;//时间段
	private boolean isUser;
	private String sTimeOut;//超出实效提示字符
	
	
	public String getsTimeOut() {
		return sTimeOut;
	}
	public void setsTimeOut(String sTimeOut) {
		this.sTimeOut = sTimeOut;
	}
	public boolean isUser() {
		return isUser;
	}
	public void setUser(boolean isUser) {
		this.isUser = isUser;
	}
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public String getsPwdStr() {
		return sPwdStr;
	}
	public void setsPwdStr(String sPwdStr) {
		this.sPwdStr = sPwdStr;
	}
	public String getsTimeLimit() {
		return sTimeLimit;
	}
	public void setsTimeLimit(String sTimeLimit) {
		this.sTimeLimit = sTimeLimit;
	}
	public PassWordInfo(int id, String sPwdStr, String sTimeLimit) {
		super();
		Id = id;
		this.sPwdStr = sPwdStr;
		this.sTimeLimit = sTimeLimit;
	}
	public PassWordInfo() {
		super();
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(Id);
		dest.writeString(sPwdStr);
		dest.writeString(sTimeLimit);
		
	}
public static final Parcelable.Creator<PassWordInfo> CREATOR = new Creator<PassWordInfo>() {

        
        public PassWordInfo createFromParcel(Parcel source) {
        	PassWordInfo contact = new PassWordInfo();
            contact.Id = source.readInt();
            contact.sPwdStr = source.readString();
            contact.sTimeLimit= source.readString();
            
            return contact;
        
        }

		@Override
		public PassWordInfo[] newArray(int size) {
			// TODO Auto-generated method stub
			return new PassWordInfo[size];
		}};
	



}
