package com.android.Samkoonhmi.model;

import com.android.Samkoonhmi.util.AddrProp;

/**
 * 曲线回调返回参数
 * 
 * @author Administrator
 * 
 */
	public class TrendsDataInfo  
	{
		private AddrProp nNumofAddr;  //地址
		private Integer	nNumOfValue;	//地址值
		
		public AddrProp getnNumofAddr() {
			return nNumofAddr;
		}
		public void setnNumofAddr(AddrProp nNumofAddr) {
			this.nNumofAddr = nNumofAddr;
		}
		public Integer getnNumOfValue() {
			return nNumOfValue;
		}
		public void setnNumOfValue(Integer nNumOfValue) {
			this.nNumOfValue = nNumOfValue;
		}		
	};