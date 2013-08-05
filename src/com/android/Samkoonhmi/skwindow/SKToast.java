package com.android.Samkoonhmi.skwindow;
import java.util.Vector;

import com.android.Samkoonhmi.R;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 弹出提示框
 */
public class SKToast {

	private static Toast mToast = null;
	private static LayoutInflater mLayoutInflater = null;
	private static View mView = null;
	private static TextView mTextView = null;
	private static long nOldCurrTime = 0;
	private static Vector<String > sOldShowTextList = new Vector<String >();
	
	/**
	 * @param text-显示文本
	 * @param duration-显示时间,Toast.LENGTH_LONG or Toast.LENGTH_SHORT
	 */
	public static Toast makeText(String text,int duration){
		Toast toast = new Toast(SKSceneManage.getInstance().mContext);
		LayoutInflater inflater = LayoutInflater.from(SKSceneManage.getInstance().mContext);
		View layout = inflater.inflate(R.layout.toast_view, null);
		TextView mTextView = (TextView) layout.findViewById(R.id.txt_toast);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(duration);
		mTextView.setText(text);
		toast.setView(layout);
		return toast;
	}
	
	/**
	 * 显示提示信息
	 * @param sShowStr： 显示文本
	 * @param nTime：显示时间,Toast.LENGTH_LONG or Toast.LENGTH_SHORT
	 */
	public static synchronized void showText(String sShowStr, int nTime)
	{
		/*判断是否有这个消息*/
		boolean bContains = false;
		int nSize = sOldShowTextList.size();
		for(int i = 0; i < nSize; i++)
		{
			if(sShowStr.equals(sOldShowTextList.get(i)))
			{
				bContains = true ;
				break;
			}
		}
		
		if(bContains == false)
		{
			sOldShowTextList.add(sShowStr);
		}
		
		long nCurrTime = System.currentTimeMillis()/1000;
		if(nCurrTime - nOldCurrTime < 2)
		{
			return ;
		}
		nOldCurrTime = nCurrTime;
		
		/*去掉第一个消息*/
		if(sOldShowTextList.size() > 1)
		{
			sOldShowTextList.remove(0);
		}
		
		/*要显示的值*/
		String sNowShow = sShowStr;
		if(!sOldShowTextList.isEmpty())
		{
			sNowShow = sOldShowTextList.get(0);
		}
		
		if(null == mToast)
		{
			mToast = new Toast(SKSceneManage.getInstance().mContext);
		}
		
		if(null == mLayoutInflater)
		{
			mLayoutInflater = LayoutInflater.from(SKSceneManage.getInstance().mContext);
		}
		
		if(null == mView)
		{
			mView = mLayoutInflater.inflate(R.layout.toast_view, null);
		}
		
		if(null == mTextView)
		{
			mTextView = (TextView) mView.findViewById(R.id.txt_toast);
		}
		
		mToast.setGravity(Gravity.CENTER, 0, 0);
		mToast.setDuration(nTime);
		mTextView.setText(sNowShow);
		mToast.setView(mView);
		mToast.show();
	}
	
	/**
	 * @param context-上下文
	 * @param text-显示文本
	 * @param duration-显示时间,Toast.LENGTH_LONG or Toast.LENGTH_SHORT
	 */
	public static Toast makeText(Context context,String text,int duration){
		Toast toast = new Toast(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		View layout = inflater.inflate(R.layout.toast_view, null);
		TextView mTextView = (TextView) layout.findViewById(R.id.txt_toast);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(duration);
		mTextView.setText(text);
		toast.setView(layout);
		return toast;
	}
	
	/**
	 * @param context-上下文
	 * @param text-显示文本
	 * @param duration-显示时间,Toast.LENGTH_LONG or Toast.LENGTH_SHORT
	 * @param gravity-显示位置  例如:Gravity.CENTER or Gravity.LEFT|Gravity.TOP;
	 */
	public static Toast makeText(Context context,String text,int duration,int gravity){
		Toast toast = new Toast(context);
		LayoutInflater inflater = LayoutInflater.from(context);
    	View layout = inflater.inflate(R.layout.toast_view, null);
    	TextView mTextView = (TextView) layout.findViewById(R.id.txt_toast);
		toast.setGravity(gravity, 0, 0);
		toast.setDuration(duration);
		mTextView.setText(text);
		toast.setView(layout);
		return toast;
	}
	
	/**
	 * @param context-上下文
	 * @param text-显示文本
	 * @param duration-显示时间,Toast.LENGTH_LONG or Toast.LENGTH_SHORT
	 * @param gravity-显示位置  例如:Gravity.CENTER or Gravity.LEFT|Gravity.TOP;
	 * @param xOffset-水平方向偏移量 
	 * @param yOffset-垂直方向偏移量
	 */
    public static Toast makeText(Context context,String text,int duration,int gravity,int xOffset, int yOffset){
    	Toast toast = new Toast(context);
    	LayoutInflater inflater = LayoutInflater.from(context);
    	View layout = inflater.inflate(R.layout.toast_view, null);
    	TextView mTextView = (TextView) layout.findViewById(R.id.txt_toast);
    	toast.setGravity(gravity, xOffset, yOffset);
		toast.setDuration(duration);
		mTextView.setText(text);
		toast.setView(layout);
		return toast;
	}
    
    /**
	 * @param context-上下文
	 * @param text-显示文本
	 * @param duration-显示时间,Toast.LENGTH_LONG or Toast.LENGTH_SHORT
	 */
	public static Toast makeText(Context context,int resId,int duration){
		Toast toast = new Toast(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		View layout = inflater.inflate(R.layout.toast_view, null);
		TextView mTextView = (TextView) layout.findViewById(R.id.txt_toast);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(duration);
		mTextView.setText(context.getString(resId));
		toast.setView(layout);
		return toast;
	}
	
	/**
	 * @param context-上下文
	 * @param text-显示文本
	 * @param duration-显示时间,Toast.LENGTH_LONG or Toast.LENGTH_SHORT
	 * @param gravity-显示位置 例如:Gravity.CENTER or Gravity.LEFT|Gravity.TOP;
	 */
	public static Toast makeText(Context context,int resId,int duration,int gravity){
		Toast toast = new Toast(context);
		LayoutInflater inflater = LayoutInflater.from(context);
    	View layout = inflater.inflate(R.layout.toast_view, null);
    	TextView mTextView = (TextView) layout.findViewById(R.id.txt_toast);
		toast.setGravity(gravity, 0, 0);
		toast.setDuration(duration);
		mTextView.setText(context.getString(resId));
		toast.setView(layout);
		return toast;
	}
	
	/**
	 * @param context-上下文
	 * @param text-显示文本
	 * @param duration-显示时间,Toast.LENGTH_LONG or Toast.LENGTH_SHORT
	 * @param gravity-显示位置  例如:Gravity.CENTER or Gravity.LEFT|Gravity.TOP;
	 * @param xOffset-水平方向偏移量 
	 * @param yOffset-垂直方向偏移量
	 */
    public static Toast makeText(Context context,int resId,int duration,int gravity,int xOffset, int yOffset){
    	Toast toast = new Toast(context);
    	LayoutInflater inflater = LayoutInflater.from(context);
    	View layout = inflater.inflate(R.layout.toast_view, null);
    	TextView mTextView = (TextView) layout.findViewById(R.id.txt_toast);
    	toast.setGravity(gravity, xOffset, yOffset);
		toast.setDuration(duration);
		mTextView.setText(context.getString(resId));
		toast.setView(layout);
		return toast;
	}
    
    
	/**
	 * @param context-上下文
	 * @param text-显示文本
	 * @param duration-显示时间,Toast.LENGTH_LONG or Toast.LENGTH_SHORT
	 */
    private static IPlcCallback mCallback;
    private static Button mBtnOk;
    private static Button mBtnCancel;
	public static Toast makeText(Context context,String title,String text,IPlcCallback callback){
		mCallback=callback;
		Toast toast = new Toast(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		View layout = inflater.inflate(R.layout.plc_dialog, null);
		TextView mTextView = (TextView) layout.findViewById(R.id.txt_title);
		mTextView.setText(title);
		
		TextView mText = (TextView) layout.findViewById(R.id.txt_msg);
		mText.setText(text);
		
		mBtnOk=(Button)layout.findViewById(R.id.btn_ok);
		mBtnCancel=(Button)layout.findViewById(R.id.btn_cancel);
		mBtnOk.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mCallback!=null) {
					mCallback.cancel();
				}
			}
		});
		
		
		mBtnCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mCallback!=null) {
					mCallback.cancel();
				}
			}
		});
		
		
		
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);
		
		return toast;
	}
	
    
    /**
     * 主要用键盘
	 * @param context-上下文
	 * @param text-显示文本
	 * @param size-显示文本字体大小
	 * @param width-显示宽
	 * @param height-显示高
	 * @param duration-显示时间,Toast.LENGTH_LONG or Toast.LENGTH_SHORT
	 * @param gravity-显示位置  例如:Gravity.CENTER or Gravity.LEFT|Gravity.TOP;
	 * @param xOffset-水平方向偏移量 
	 * @param yOffset-垂直方向偏移量
	 */
    private static Toast toast=null;
    private static TextView mText;
    private static final int HIDE=1;
    private SKHandler mHandler=new SKHandler();
    public Toast makeTexts(Context context,String text,int size,int width,int height,int duration,int gravity,int xOffset, int yOffset){
    	
    	if (toast==null) {
    		toast=new Toast(context);
    		LayoutInflater inflater = LayoutInflater.from(context);
        	View layout = inflater.inflate(R.layout.toast_view, null);
        	mText = (TextView) layout.findViewById(R.id.txt_toast);
        	mText.setBackgroundResource(R.drawable.key_button_toask);
        	mText.setTextColor(Color.BLACK);
    		toast.setView(layout);
		}
    	mHandler.removeMessages(HIDE);
    	toast.setGravity(gravity, xOffset, yOffset);
		toast.setDuration(duration);
		mText.setWidth(width);
		mText.setHeight(height);
		mText.setText(text);
    	mText.setTextSize(size);
    	mText.setVisibility(View.VISIBLE);
		return toast;
	}
    
    /**
     * 主要用键盘
     * 隐藏
     * @param type-0 表示不延迟 type-1 表示延迟200毫秒
     */
    public void hideToast(int type){
    	if (type==0) {
    		if (toast!=null) {
    			toast.cancel();
			}
		}else{
			mHandler.sendEmptyMessageDelayed(HIDE, 200);
		}
    }
    
    class  SKHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what==HIDE) {
				if (toast!=null) {
	    			toast.cancel();
				}
			}
		}
    }
    
	public interface IPlcCallback{
		void cancel();
	}
}
