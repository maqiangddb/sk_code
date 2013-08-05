package com.android.Samkoonhmi.macro;
import com.android.Samkoonhmi.macro.corba.PHolder;
import com.android.Samkoonhmi.model.MParamInfo;

public class MParamDesc {
	public PHolder     PHolder;//变量值持有器
	public MParamInfo  PInfo;  //变量信息持有器

	public MParamDesc(){

	}
	public MParamDesc(PHolder PH, MParamInfo PI){
         this.PHolder = PH;
         this.PInfo   = PI;
	}
}
