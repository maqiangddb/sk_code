package com.android.Samkoonhmi.print;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import com.android.Samkoonhmi.SKScene;
import com.android.Samkoonhmi.model.ScenceInfo;
import com.android.Samkoonhmi.plccommunicate.PrintProtocol;
import com.android.Samkoonhmi.skenum.BACKCSS;
import com.android.Samkoonhmi.skenum.CSS_TYPE;
import com.android.Samkoonhmi.skenum.GOTO_TYPE;
import com.android.Samkoonhmi.skwindow.AKHintDialog;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.skwindow.SKSceneManage.ItemInfo;
import com.android.Samkoonhmi.skwindow.SKSceneManage.SHOW_TYPE;
import com.android.Samkoonhmi.util.FillRender;
import com.android.Samkoonhmi.util.ImageFileTool;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * AK-打印
 */
public class AKPrint {

	private static final String TAG="AKPrint";
	private HandlerThread mThread=null;
	private UIHandler mHandler=null;
	private boolean isPrint;
	private PrintInfo mPrintInfo=null;
	private static AKPrint sInstance = null;
	/**
	 * 玮煌的1-10
	 * 爱普生的11-20
	 */
	private int nModel=0;
	public static AKPrint getInstance() {
		if (sInstance == null) {
			sInstance = new AKPrint();
		}
		return sInstance;
	}
	
	public AKPrint(){
		mPrintInfo=new PrintInfo();
		mThread=new HandlerThread("AK_Print");
		mThread.start();
		mHandler=new UIHandler(mThread.getLooper());
	}
	
	/**
	 * 打印
	 * @param id-画面序号
	 * @param model-打印机类型
	 */
	public boolean printBitmap(int id,int model,int port){
		if (isPrint) {
			return false;
		}
		nModel=model;
		nPort=port;
		isPrint=true;
		int sid=SKSceneManage.getInstance().getSceneByNum(id);
		//System.out.println("^^^^="+sid);
		if (sid<1) {
			isPrint=false;
			return false;
		}
		nSid=sid;
		mHandler.sendEmptyMessage(PRINT);
		
		return true;
	}
	
	/**
	 * 打印
	 * @param name-画面名称
	 * @param model-打印机类型
	 */
	private int nSid=0;
	private int nPort;
	public boolean printBitmap(String name,int model,int port){
		if (isPrint) {
			return false;
		}
		nModel=model;
		nPort=port;
		isPrint=true;
		int sid=SKSceneManage.getInstance().getSceneIdByName(name);
		if (sid<1) {
			isPrint=false;
			return false;
		}
		nSid=sid;
		mHandler.sendEmptyMessage(PRINT);
		return true;
	}
	
	/**
	 * 打印
	 * @param name-画面名称
	 * @param model-打印机类型
	 */
	public boolean printText(String text,int model,int port){
		
		if (text==null||text.length()==0) {
			return false;
		}
		nModel=model;
		nPort=port;
		nModel=model;
		Vector<String> list=new Vector<String>();
		list.add(text);
		
		return printTexts(list,model);
	}
	
	/**
	 * 打印
	 * @param name-画面名称
	 * @param model-打印机类型
	 */
	public boolean printTexts(Vector<String> list,int model,int port){
		if(list==null||list.isEmpty()){
			return false;
		}
		nModel=model;
		nPort=port;
		nModel=model;
		
		return printTexts(list,model);
	}
	
	/**
	 * 文本打印
	 */
	public boolean printTexts(Vector<String> list,int model){
		if (isPrint) {
			return false;
		}
		isPrint=true;
		mHandler.sendEmptyMessage(SHOW);
		if (model<11) {
			//玮煌
			PrintProtocol.printTextData(list,nModel, nPort);
		}else if (model<21) {
			//爱普生
			if(model==11){
				//EPSON K100
				LanUtil.getInstance().printText(list);
			}
			
		}
		mHandler.sendEmptyMessage(HIDE);
		return true;
	}
	
	/**
	 * 关闭打印提示窗口
	 */
	public void closeAKPrintWindow(){
		if (mHandler!=null) {
			mHandler.sendEmptyMessage(HIDE);
		}
	}
	
	private static final int PRINT=1;//进入打印
	private static final int DRAW=2;//绘制图片
	private static final int SHOW=3;//显示
	private static final int HIDE=4;//隐藏
	private static final int GETINFO=5;//获取画面信息
	class UIHandler extends Handler{
		
		public UIHandler(Looper looper){
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what==PRINT) {
				//开始打印
				getScene(nSid);
			}else if (msg.what==DRAW) {
				if (scene!=null) {
					boolean result=scene.isbUpdateing();
					if (result) {
						//画面没有刷新完毕，继续等待
						mHandler.sendEmptyMessageDelayed(DRAW, 500);
					}else {
						//绘制
						mScence=scene.mBitmap;
						draw();
						if (view!=null) {
							if (view.containsKey(nSid)) {
								view.remove(nSid);
							}
						}
						if (mBitmap!=null) {
							if (nModel<11) {
								PrintProtocol.printBitmapData(mBitmap,nModel,nPort);
							}else if (nModel<21) {
								LanUtil.getInstance().printBitmap(mBitmap);
							}
						}
						mHandler.sendEmptyMessage(HIDE);
						
						if (mPrintInfo.reset) {
							//打印的不是当前画面，返回打印之前的画面
							mPrintInfo.reset=false;
							mPrintInfo.nCount=0;
							if (mPrintInfo.nPrintType==1) {
								//打印的是窗口，直接关闭窗口
								SKSceneManage.getInstance().gotoWindow(4, mPrintInfo.nPrintId, false, 0,  GOTO_TYPE.BUTTON);
							}else {
								//打印的是画面
								SKSceneManage.getInstance().gotoWindow(mPrintInfo.nBeforType, mPrintInfo.nBeforId, false, 0,  GOTO_TYPE.BUTTON);
							}
							
						}
						isPrint=false;
					}
				}
			}else if(msg.what==SHOW){
				//显示等待对话框
				mContext=SKSceneManage.getInstance().mContext;
				if (mContext==null) {
					return;
				}
				if (mDialog == null) {
					mDialog = new AKHintDialog(mContext);
				}
				if (mDialog.isShow) {
					return;
				}
				mDialog.showPopWindow("正在打印数据...");
				mHandler.sendEmptyMessageDelayed(HIDE,2*1000);
			}else if (msg.what==HIDE) {
				//隐藏对话框
				if (mDialog!=null) {
					mHandler.removeMessages(HIDE);
					mDialog.hidePopWindow();
				}
			}else if (msg.what==GETINFO) {
				if (mPrintInfo.nCount<3) {
					mPrintInfo.nCount++;
					if (SKSceneManage.getInstance().nSceneId==mPrintInfo.nPrintSid) {
						mHandler.sendEmptyMessage(SHOW);
						scene=SKSceneManage.getInstance().getCurrentScene();
						mHandler.sendEmptyMessageDelayed(DRAW, 100);
					}else {
						mHandler.sendEmptyMessageDelayed(GETINFO, 500);
					}
				}
			}
		}
	}
	
	private Bitmap mScence=null;
	private ScenceInfo info;
	private SKScene scene=null;
	private AKHintDialog mDialog=null;
	private HashMap<Integer, SKScene> view;
	private Context mContext;
	private void getScene(int sid){
		
		scene=null;
		mContext=SKSceneManage.getInstance().mContext;
		Activity mActivity=SKSceneManage.getInstance().getActivity();
		info=SKSceneManage.getInstance().getScenceInfo(sid);
		if (info==null||mActivity==null||mContext==null) {
			isPrint=false;
			return ;
		}
		
		if (sid!=SKSceneManage.getInstance().nSceneId) {
			//打印的不是当前画面,跳转到该画面，再跳转回来
			ScenceInfo cInfo=SKSceneManage.getInstance().getCurrentInfo();
			ScenceInfo tInfo=SKSceneManage.getInstance().getScenceInfo(sid);
			
			mPrintInfo.reset=true;
			if (cInfo!=null) {
				if (cInfo.geteType()==SHOW_TYPE.FLOATING) {
					//窗口
					mPrintInfo.nBeforId=cInfo.getId();
					mPrintInfo.nBeforType=1;
				}else {
					mPrintInfo.nBeforId=SKSceneManage.getInstance().nSceneId;
					mPrintInfo.nBeforType=0;
				}
			}
			if (tInfo!=null) {
				if (tInfo.geteType()==SHOW_TYPE.FLOATING) {
					//窗口
					mPrintInfo.nPrintId=tInfo.getId();
					mPrintInfo.nPrintType=1;
				}else {
					mPrintInfo.nPrintId=sid;
					mPrintInfo.nPrintType=0;
				}
				mPrintInfo.nPrintSid=sid;
				mPrintInfo.nCount=0;
			}
			
			//跳转到打印画面
			SKSceneManage.getInstance().gotoWindow(mPrintInfo.nPrintType, mPrintInfo.nPrintId, false, 0, GOTO_TYPE.BUTTON);
			//获取打印画面信息
			mHandler.sendEmptyMessageDelayed(GETINFO, 500);
			
			
		}else {
			//获取当前画面
			scene=SKSceneManage.getInstance().getCurrentScene();
			mHandler.sendEmptyMessageDelayed(DRAW, 500);
			mHandler.sendEmptyMessage(SHOW);
		}
		return ;
	}
	
	private Bitmap mBitmap=null;
	private void draw(){
		if (mScence==null||(info.getnSceneWidth()<=0)||(info.getnSceneHeight()<=0)) {
			mBitmap=null;
			return;
		}
		mBitmap=Bitmap.createBitmap(info.getnSceneWidth(), info.getnSceneHeight(), Config.ARGB_8888);;
		Paint paint=new Paint();
		FillRender fillRender;
		Shader myShader;
		Canvas canvas=new Canvas(mBitmap);
		canvas.drawBitmap(mBitmap, 0, 0, null);
		Rect rect=new Rect(0, 0, info.getnSceneWidth(), info.getnSceneHeight());
		
		if (info.geteBackType() == BACKCSS.BACK_IMG) {
			Bitmap bg = ImageFileTool
					.getBitmap(info.getsPicturePath());
			if (bg != null) {
				canvas.drawBitmap(bg, null, rect, null);
			}
		} else {
			if (info.geteDrawStyle() == CSS_TYPE.CSS_TRANSPARENCE
					|| info.geteDrawStyle() == CSS_TYPE.CSS_SOLIDCOLOR) {
				canvas.drawColor(info.getnBackColor());
			} else {
				fillRender = new FillRender();
				myShader = fillRender.setRectCss(info.geteDrawStyle(),
						0, 0, info.getnSceneWidth(),
						info.getnSceneHeight(), info.getnForeColor(),
						info.getnBackColor());
				paint.setShader(myShader);
				paint.setStyle(Style.FILL_AND_STROKE);
				canvas.drawRect(rect, paint);
			}
		}
		
		canvas.drawBitmap(mScence, null, rect, null);
		
	}
	
	/**
	 * 打印画面信息
	 */
	private class PrintInfo{
		int nBeforId;//跳转之前的画面id
		int nBeforType;//跳转之前的画面类型
		int nPrintId;//需要打跳转的画面id 或窗口id
		int nPrintType;//需要打印的画面类型
		int nPrintSid;//画面id
		int nCount;//获取信息的次数,不超过3次
		boolean reset;//是否需要复位
	}
}
