package com.android.Samkoonhmi.service;

import java.util.Vector;

import android.os.Parcel;
import android.os.Parcelable;

//import com.android.Samkoonhmi.skenum.READ_WRITE_COM_TYPE;
//import com.nwd.android.weather.WeatherInfo;

public class AddrInfo implements Parcelable{
	
	public int eConnectType;         			// 连接方式  3:Com1  , 4:Com2
	public int nRegIndex;              			// PLC寄存器的索引
	public int nPlcStationIndex;            	// PLC的站号
	public int nAddrValue;                		// PLC的起始地址值
	public int nAddrLen;                     	// 地址的长度,位地址及16位数据类型长度为1,32位数据长度为2
	
	
	public static final Parcelable.Creator<AddrInfo> CREATOR = new Creator<AddrInfo>() {
		@Override
		public AddrInfo createFromParcel(Parcel source) {
			AddrInfo info = new AddrInfo();
			info.eConnectType = source.readInt();
			info.nRegIndex = source.readInt();
			info.nPlcStationIndex = source.readInt();
			info.nAddrValue = source.readInt();
			info.nAddrLen = source.readInt();
			return info;
		}

		@Override
		public AddrInfo[] newArray(int size) {
			// TODO Auto-generated method stub
			return new AddrInfo[size];
		}
	};
	

	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(eConnectType);
		dest.writeInt(nRegIndex);
		dest.writeInt(nPlcStationIndex);
		dest.writeInt(nAddrValue);
		dest.writeInt(nAddrLen);
	}



	@Override
	public int describeContents() {
		return 0;
	}

}
