package com.android.Samkoonhmi.util;

import java.io.File;
import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import com.android.Samkoonhmi.model.CacheInfo;
import com.android.Samkoonhmi.skwindow.SKSceneManage;

public class ImageFileTool {

	/**
	 * 最大缓存50M数据，如果超出删除缓缓存
	 */
	private static final int nMaxValue=52428800;
	private static int nValue=0;
	
	/**
	 * 存储当前获取所有图片
	 */
	private static Vector<CacheInfo> mImages=new Vector<CacheInfo>();
	
	/**
	 * @param fileName-图片路径
	 * 获取图片
	 */
	public static Bitmap getBitmap(String fileName){
		if (fileName == null||fileName.equals("")) {
			return null;  
		}
		Bitmap mBitmap = null;
		mBitmap=getCacheBitmap(fileName);
		if (mBitmap==null) {
			mBitmap=getBitmapFile(fileName);
			if(mBitmap!=null){
				int temp=mBitmap.getWidth()*mBitmap.getHeight()*4;
				if (temp>4*1024*1024) {
					Matrix matrix=new Matrix(); 
					float fx=((float)mBitmap.getWidth())/SKSceneManage.nSceneWidth;
					float fy=((float)mBitmap.getHeight())/SKSceneManage.nSceneHeight;
					matrix.postScale(fx, fy); 
					Bitmap tBitmap=Bitmap.createBitmap(mBitmap,0,0,mBitmap.getWidth(),mBitmap.getHeight(),matrix,true); 
					mBitmap.recycle();
					mBitmap=tBitmap;
					temp=mBitmap.getWidth()*mBitmap.getHeight()*4;
				}
				isClear(temp);
				
				CacheInfo info=new CacheInfo();
				info.name=fileName;
				info.mBitmap=mBitmap;
				info.clear=true;
				mImages.add(info);
			}
		}
		return mBitmap;
	}
	
	/**
	 * 从文件读取图片
	 * @param fileName-文件名
	 * 
	 */
	private synchronized static Bitmap getBitmapFile(String fileName) {
		
		Bitmap mBitmap = null;
		try {
			File mFile = new File(fileName);
			if (mFile.exists()) {
				mBitmap = BitmapFactory.decodeFile(fileName);
			}
		} catch (Exception e) {
			Log.e("ImageFileTool", "get biamap file error");
			e.printStackTrace();
		}
		
		return mBitmap;
	}
	
	/**
	 * 根据id获取图片
	 * @param id-资源id
	 */
	public static Bitmap getBitmap(int id,Context context){
		Bitmap mBitmap = null;
		mBitmap=getCacheBitmap(id+"");
		if (mBitmap==null) {
			mBitmap=BitmapFactory.decodeResource(context.getResources(), id);
			
			if(mBitmap!=null){
				
				int temp=mBitmap.getWidth()*mBitmap.getHeight()*4;
				if (temp>4*1024*1024) {
					mBitmap.recycle();
					return null;
				}
				isClear(temp);
				
				CacheInfo info=new CacheInfo();
				info.name=id+"";
				info.mBitmap=mBitmap;
				info.clear=true;
				mImages.add(info);
			}
		}
		return mBitmap;
	}
	
	/**
	 * 根据宽高创建图片
	 * 创建三个缓存图片
	 */
	private static int mark=1;
	/**
	 * @param width=场景宽；
	 * @param height=场景高
	 * @param type=0 画面,1窗口
	 */
	public synchronized static Bitmap getBitmap(int width,int height,int type,Context context){
		
		if(width<=0||height<=0){
			return null;
		}
		
		Bitmap mBitmap = null;
		mBitmap=getCacheBitmap(width+"_"+height+"_"+type+"_"+mark);
		if (mBitmap==null) {
			mBitmap = Bitmap.createBitmap(width, height,Config.ARGB_8888);
			
			if (mBitmap!=null) {
				int temp=mBitmap.getWidth()*mBitmap.getHeight()*4;
				if (temp>4*1024*1024) {
					mBitmap.recycle();
					return null;
				}
				isClear(temp);
				
				CacheInfo info=new CacheInfo();
				info.name=width+"_"+height+"_"+type+"_"+mark;
				info.mBitmap=mBitmap;
				info.clear=false;
				mImages.add(info);
			}
		}
		mark++;
		if (mark>3) {
			mark=1;
		}
		return mBitmap;
	}
	
	private static int titleMark = 1;
	public static Bitmap getTitleBgBitmap(int width, int high , Context context)
	{
		if(width<=0||high<=0){
			return null;
		}
		
		Bitmap mTitleBitmap = null;
		String path ="wd_title_" + width + "_" + high + titleMark;
		mTitleBitmap = getCacheBitmap(path);
		if (mTitleBitmap == null) {
			mTitleBitmap = Bitmap.createBitmap(width, high, Config.ARGB_8888);

			int imageLegth = mTitleBitmap.getWidth() * mTitleBitmap.getHeight() * 4;
			isClear(imageLegth);
			
			CacheInfo info = new CacheInfo();
			info.clear = false;
			info.mBitmap = mTitleBitmap;
			info.name = path;
			mImages.add(info);
		}
		
		titleMark = (titleMark == 1) ? 2:1;
		return mTitleBitmap;
	}
	
	public static void setBitmap(Bitmap bitmap,String name){
		int temp=bitmap.getWidth()*bitmap.getHeight()*4;
		isClear(temp);
		CacheInfo info=new CacheInfo();
		info.name=name;
		info.mBitmap=bitmap;
		info.clear=false;
		mImages.add(info);
	}
	
	/**
	 * 清空缓存图片
	 * 说明:不能使用recycle(),因为该图片有可能存在其他引用。
	 */
	private synchronized static void isClear(int value){
		nValue+=value;
		if(nValue>=nMaxValue){
			for (int i = 0; i < mImages.size(); i++) {
				CacheInfo info=mImages.get(i);
				if (info.mBitmap!=null) {
					if (info.clear) {
						//info.mBitmap.recycle();
						//info.mBitmap = null;
						mImages.remove(i);
						i--;
					}
				}
			}
			nValue=0;
			Log.d("SKScene", "clear bitmap");
		}
	}
	
	/**
	 * 清除图片信息
	 */
	public void clearBitmap(){
		nValue=0;
		for(CacheInfo info : mImages)
		{
			if (info != null && info.mBitmap != null) {
				info.mBitmap.recycle();
				info = null;
			}
		}
		mImages.clear();
	}
	
	/**
	 * 获取缓存图片
	 */
	private static Bitmap getCacheBitmap(String name){
		Bitmap mBitmap=null;
		for (int i = 0; i < mImages.size(); i++) {
			CacheInfo info=mImages.get(i);
			if (info.name.equals(name)) {
				return info.mBitmap;
			}
		}
		return mBitmap;
	}
	
}
