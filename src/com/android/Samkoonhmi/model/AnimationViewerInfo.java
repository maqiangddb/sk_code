package com.android.Samkoonhmi.model;

import java.util.ArrayList;

import android.util.Log;

import com.android.Samkoonhmi.util.AddrProp;

/**
 * 动画显示器数据实体类
 * @author 魏 科
 * @date   2012-06-12
 * */
public class AnimationViewerInfo {
	//布局属性
	private int nItemId;      //控件Id
	private short nLp;        //控件左上角X坐标
	private short nTp;        //控件左上角Y坐标
	private short nWidth;     //控件宽度
	private short nHeigth;    //控件高度
	private short nZvalue;    //控件层序号

	private int   nCollidindId;    //组合ID 

	private short nTrackType;       //轨迹点类型，约定:0 散点轨迹，1区域轨迹


	//散点轨迹移动相关属性
	private short nMoveType;         //移动类型，约定：0，循环移动；1，往返移动;
	private short nMoveCondition;    //移动条件，约定：0，时间间隔；1，预设值；
	private short nMoveTimeInterval; //移动时间间隔

	private short nTrackPointTotal;  //轨迹点总数	
	private short nStartTrackPoint;  //起始轨迹点序号

	private AddrProp mMoveCtrlAddr;  //移动控制地址

	private ArrayList<TrackPointInfo> mTrackPointArray; //轨迹点列表
	private ArrayList<TPMoveInfo>     mTPMoveList;      //移动条件列表

	//区域轨迹移动相关的字段，保留字段，不可删除
	private short nAreaOrigXPos; //轨迹区域左上X坐标
	private short nAreaOrigYPos; //轨迹区域左上Y坐标
	private short nAreaWidth;    //轨迹区域宽度
	private short nAreaHeight;   //轨迹区域高度
	private int   nBackColor;    //背景色

	private float nXMoveStepScale;    //X坐标移动比例
	private float nYMoveStepScale;    //Y坐标移动比例

	private AddrProp mXPosCtrlAddr;     //轨迹点X坐标数据地址
	private AddrProp mYPosCtrlAddr;     //轨迹点Y坐标数据地址

	//状态相关的字段
	private short nStateTotal;  	     //状态总数
	private short nChangeType;           //状态改变类型，约定:0,循环切换；1，往复切换
	private short nChangeCondition;      //状态改变的条件，约定：0，时间间隔切换；1，预设值切换； 
	private short nChangeTimeinterval;   //状态切换的时间间隔
	private short nInitState;            //初始状态序号
	
	 //显现
    private ShowInfo mShowInfo;    //显现属性

	private AddrProp mChangeCtrlAddr;    //状态控制地址	

	private ArrayList<StakeoutInfo> mSPreSetVList;     //状态条件列表

	private ArrayList<PictureInfo>  mPicPathArray;     //图片路径列表

	private ArrayList<TextInfo>     mTextInfoList; //文本数据列表


	/**
	 * 设置控件左上角X坐标
	 * */
	public void setLp(short Lp){
		if(0 < Lp){
			this.nLp = Lp;
		}
	}

	/**
	 * 获得控件左上角X坐标
	 * */
	public short getLp(){
		return this.nLp;
	}

	/**
	 *  设置控件左上角y坐标
	 * */
	public void setTp(short Tp){
		if(0 < Tp){
			this.nTp = Tp;
		}
	}

	/**
	 *  获得控件左上角y坐标
	 * */
	public short getTp(){
		return this.nTp;
	}

	/**
	 * 设置控件宽度
	 * */
	public void setWidth(short width){
		if(0 < width){
			this.nWidth = width;
		}
	}

	/**
	 * 获得控件宽度
	 * */
	public short getWidth(){
		return this.nWidth;
	}

	/**
	 * 设置控件高度
	 * */
	public void setHeight(short height){
		if(0 < height){
			this.nHeigth = height;
		}
	}

	/**
	 * 获得控件高度
	 * */
	public short getHeight(){
		return this.nHeigth;
	}


	/**
	 * 设置轨迹类型
	 * */
	public void setTrackType(short ttype){
		if((0 != ttype) && (1 != ttype)){
			Log.e("AnimationViewerInfo","setTrackType: Invalid track type: " + ttype);
			return;
		}
		this.nTrackType = ttype;
	}

	/**
	 * 获得轨迹类型
	 * */
	public short getTrackType(){
		return this.nTrackType;
	}

	/**
	 * 设置移动类型
	 * */
	public void setMoveType(short mtype){
		if((0 != mtype)&&(1!=mtype)){
			Log.e("AnimationViewerInfo","setMoveType: Invalid move type: " + mtype);
			return;
		}

		this.nMoveType = mtype;
	}

	/**
	 * 获得移动类型
	 * */
	public short getMoveType(){
		return this.nMoveType;
	}

	/**
	 * 设置移动条件
	 * */
	public void setMoveCondition(short mcond){
		if( (0!=mcond)&&(1!=mcond)){
			Log.e("AnimationViewerInfo","setMoveCondition: Invalid move condition : " + mcond);
			return;	 
		}
		this.nMoveCondition = mcond;
	}

	/**
	 * 获得移动条件
	 * */
	public short getMoveCondition(){
		return this.nMoveCondition;
	}

	/**
	 * 设置时间间隔
	 * */
	public void setMoveTimeInterval(short tinterval){
		if(0 > tinterval){
			Log.e("AnimationViewerInfo","setMoveTimeInterval: Invalid time interval : " + tinterval);
			return;	 
		}
		this.nMoveTimeInterval = tinterval;
	}
	/**
	 * 获得时间间隔
	 * */
	public short getMoveTimeInterval(){
		return this.nMoveTimeInterval;
	}

	/**
	 * 设置轨迹点总数
	 * */
	public void setTrackPointTotal(short tptotal){
		if(0 > tptotal){
			Log.e("AnimationViewerInfo","setTrackPointTotal: Invalid track point total : " + tptotal);
			return;	 	 
		}
		this.nTrackPointTotal = tptotal;
	}

	/**
	 * 获得轨迹点总数
	 * */
	public short getTrackPointTotal(){
		return this.nTrackPointTotal;
	}

	/**
	 * 设置起始轨迹点ID
	 * */
	public void setStartTrackPoint(short sttpid){
		if(0 > sttpid){
			Log.e("AnimationViewerInfo","setStartTrackPoint: Invalid start track point id : " + sttpid);
			return; 
		}
		this.nStartTrackPoint = sttpid;
	}

	/**
	 * 获得起始轨迹点ID
	 * */
	public short getStartTrackPoint(){
		return this.nStartTrackPoint;
	}

	/**
	 * 设置移动控制地址
	 * */
	public void setMoveCtrlAddr(AddrProp mctrladdr){
		if(null == mctrladdr){
			Log.e("AnimationViewerInfo","setMoveCtrlAddr: move control address prop is null ");
			return;  
		}
		this.mMoveCtrlAddr = mctrladdr;
	}

	/**
	 * 获得移动控制地址属性
	 * */
	public AddrProp getMoveCtrlAddr(){
		return this.mMoveCtrlAddr;
	};

	/**
	 * 设置移X坐标控制地址
	 * */
	public void setXPosCtrlAddr(AddrProp mctrladdr){
		if(null == mctrladdr){
			Log.e("AnimationViewerInfo","setXPosCtrlAddr: xpos control address prop is null ");
			return;  
		}
		this.mXPosCtrlAddr = mctrladdr;
	}

	/**
	 * 获得X坐标控制地址属性
	 * */
	public AddrProp getXPosCtrlAddr(){
		return this.mXPosCtrlAddr;
	};

	/**
	 * 设置移Y坐标控制地址
	 * */
	public void setYPosCtrlAddr(AddrProp mctrladdr){
		if(null == mctrladdr){
			Log.e("AnimationViewerInfo","setYPosCtrlAddr: ypos control address prop is null ");
			return;  
		}
		this.mYPosCtrlAddr = mctrladdr;
	}

	/**
	 * 获得Y坐标控制地址属性
	 * */
	public AddrProp getYPosCtrlAddr(){
		return this.mYPosCtrlAddr;
	};

	/**
	 * 设置轨迹点列表
	 * */
	public void setTrackPointArray(ArrayList<TrackPointInfo> tparray){
		if(null == tparray){
			Log.e("AnimationViewerInfo","setTrackPointArray: track point array is null ");
			return; 
		}
		this.mTrackPointArray = tparray;
	}

	/**
	 * 获得轨迹点列表
	 * */
	public ArrayList<TrackPointInfo> getTrackPointArray(){
		return this.mTrackPointArray;
	}

	/**
	 * 设置移动条件列表
	 * */
	public void setTPMoveList(ArrayList<TPMoveInfo> tpmovelist){
		if(null == tpmovelist){
			Log.e("AnimationViewerInfo","setTPMoveList: track point move list is null ");
			return; 
		}
		this.mTPMoveList = tpmovelist;
	}

	/**
	 * 获得移动列表
	 * */
	public ArrayList<TPMoveInfo> getTPMoveList(){
		return this.mTPMoveList;
	}

		 /**
		  * 设置区域左上角X坐标
		  * */
		 public void setAreaOrigXPos(short x){
			 if(0 > x){
				 Log.e("AnimationViewerInfo","setAreaOrigXPos: invalid area x pos");
				 return; 
			 }
			 this.nAreaOrigXPos = x;
		 }
		 
		 /**
		  * 获得区域左上角X坐标
		  * */
		 public short getAreaOrigXPos(){
			 return this.nAreaOrigXPos;
		 }
		 
		 /**
		  * 设置区域左上角Y坐标
		  * */
		 public void setAreaOrigYPos(short y){
			 if(0 > y){
				 Log.e("AnimationViewerInfo","setAreaOrigYPos: invalid area y pos");
				 return; 
			 }
			 this.nAreaOrigYPos = y;
		 }
		 
		 /**
		  * 获得区域左上角Y坐标
		  * */
		 public short getAreaOrigYPos(){
			 return this.nAreaOrigYPos;
		 }
		 
		 /**
		  * 设置区域宽度
		  * */
		 public void setAreaWidth(short width){
			 if(0 > width){
				 Log.e("AnimationViewerInfo","setAreaWidth: invalid area width");
				 return; 
			 }
			 this.nAreaWidth = width;
		 }
		 
		 /**
		  * 获得区域宽度
		  * */
		 public short getAreaWidth(){
			 return this.nAreaWidth;
		 }
		 
		 /**
		  * 设置区域高度
		  * */
		 public void setAreaHeight(short height){
			 if(0 > height){
				 Log.e("AnimationViewerInfo","setAreaHeight: invalid area height");
				 return; 
			 }
			 this.nAreaHeight = height;
		 }
		 
		 /**
		  * 获得区域高度
		  * */
		 public short getAreaHeight(){
			 return this.nAreaHeight;
		 }

		 /**
		  * 设置背景色
		  * */
		 public void setBackColor(int color){
			 this.nBackColor = color;
		 }
		 
		 /**
		  * 获得背景色
		  * */
		 public int getBackColor(){
			 return this.nBackColor;
		 }

	/**
	 * 设置X方向移动比例
	 * */
	public void setXMoveStepScale(float xscal){
		this.nXMoveStepScale = xscal;
	}

	/**
	 * 获得X方向移动比例
	 * */
	public float getXMoveStepScale(){
		return this.nXMoveStepScale;
	}

	/**
	 * 设置Y方向移动比例
	 * */
	public void setYMoveStepScale(float yscal){
		this.nYMoveStepScale = yscal;
	}

	/**
	 * 获得Y方向移动比例
	 * */
	public float getYMoveStepScale(){
		return this.nYMoveStepScale;
	}

	/**
	 * 设置状态总数
	 * */
	public void setStateTotal(short stotal){
		if(0>stotal){
			Log.e("AnimationViewerInfo","setStateTotal: invalid state total:"+stotal);
			return; 
		}
		this.nStateTotal = stotal;
	}
	/**
	 * 获得状态总数
	 * */
	public short getStateTotal(){
		return this.nStateTotal;
	}

	/**
	 * 设置状态改变类型
	 * */
	public void setChangeType(short ctype){
		if(0>ctype){
			Log.e("AnimationViewerInfo","setChangeType: invalid change type:"+ctype);
			return; 
		}
		this.nChangeType = ctype;
	}

	/**
	 * 获得状态改变类型
	 * */
	public short getChangeType(){
		return this.nChangeType;
	}

	/**
	 * 设置状态改变条件
	 * */
	public void setChangeCondition(short cond){
		if(0>cond){
			Log.e("AnimationViewerInfo","setChangeCondition: invalid change condition:"+cond);
			return; 
		}
		this.nChangeCondition = cond;
	}

	/**
	 * 获得状态改变条件
	 * */
	public short getChangeCondition(){
		return this.nChangeCondition;
	}

	/**
	 * 设置状态改变条件
	 * */
	public void setChangeTimeinterval(short interval){
		if(0>interval){
			Log.e("AnimationViewerInfo","setChangeTimeinterval: invalid time interval:"+interval);
			return; 
		}
		this.nChangeTimeinterval = interval;
	}

	/**
	 * 获得状态改变条件
	 * */
	public short getChangeTimeinterval(){
		return this.nChangeTimeinterval;
	}

	/**
	 * 设置初始状态号
	 * */
	public void setStartState(short sstate){
		if(0>sstate){
			Log.e("AnimationViewerInfo","setStartState: invalid start state:"+sstate);
			return; 
		}
		this.nInitState = sstate;
	}

	/**
	 * 获得初始状态号
	 * */
	public short getStartState(){
		return this.nInitState;
	}

	/**
	 * 设置状态控制地址
	 * */
	public void setChangeCtrlAddr(AddrProp sctrladdr){
		if(null == sctrladdr){
			Log.e("AnimationViewerInfo","setChangeCtrlAddr: state control address is null");
			return; 
		}
		this.mChangeCtrlAddr = sctrladdr;
	}

	/**
	 * 获得状态控制地址
	 * */
	public AddrProp getChangeCtrlAddr(){
		return this.mChangeCtrlAddr;
	}

	/**
	 * 设置状态条件列表
	 * */
	public void setSPreSetVList(ArrayList<StakeoutInfo> vlist){
		if(null == vlist){
			Log.e("AnimationViewerInfo","setSPreSetVList: preset value list is null");
			return; 
		}
		this.mSPreSetVList = vlist;
	}

	/**
	 * 获得状态条件列表
	 * */
	public ArrayList<StakeoutInfo> getSPreSetVList(){
		return this.mSPreSetVList;
	}

	/**
	 * 设置图片路径列表
	 * */
	public void setPicPathArray(ArrayList<PictureInfo> list){
		if(null == list){
			Log.e("AnimationViewerInfo","setPicPathArray: pictrue path list is null");
			return; 
		}
		this.mPicPathArray = list;
	}

	/**
	 * 获得图片路径表
	 * */
	public ArrayList<PictureInfo> getPicPathArray(){
		return this.mPicPathArray;
	}	

	/**
	 * 设置文本信息列表
	 * */
	public void setTextInfoList(ArrayList<TextInfo> list){
		if(null == list){
			Log.e("AnimationViewerInfo","setPicPathArray: pictrue path list is null");
			return; 
		}
		this.mTextInfoList = list;
	}

	/**
	 * 获得文本信息列表
	 * */
	public ArrayList<TextInfo> getTextInfoList(){
		return  this.mTextInfoList;
	}

	/**
	 * 设置层序号
	 * */
	public void setZvalue(short zvalue){
		this.nZvalue = zvalue;
	}

	/**
	 * 获得层序号
	 * */
	public short getZValue(){
		return nZvalue;
	}

	/**
	 * 设置组合ID
	 * */
	public void setCollidindId(int cid){
		this.nCollidindId = cid;
	}

	/**
	 * 获得组合ID
	 * */
	public int getCollidindId(){
		return this.nCollidindId;
	}

	 /**
	  * 获得显现属性
	  * */
	 public ShowInfo getmShowInfo() {
		 return mShowInfo;
	 }
	 /**
	  * 设置显现属性
	  * */
	 public void setmShowInfo(ShowInfo mShowInfo) {
		 this.mShowInfo = mShowInfo;
	 }
	 
	 public int getnItemId() {
		return nItemId;
	 }

	public void setnItemId(int nItemId) {
		this.nItemId = nItemId;
	}
	
}//End of class
