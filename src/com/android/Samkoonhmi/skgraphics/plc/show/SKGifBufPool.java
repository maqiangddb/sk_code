package com.android.Samkoonhmi.skgraphics.plc.show;

import java.util.HashMap;

import android.graphics.Movie;
import android.util.Log;

/**
 * GIF动画缓存池
 * @author 魏 科
 * @date   2012-01-10
 * */
public class SKGifBufPool {
	private static SKGifBufPool           mSelfInstance; //自持单例
	private static HashMap<String, Movie> mMovieBufPool;
	private int                           mPoolSize = 20;    //缓缓存池大小

	public SKGifBufPool(){
		mMovieBufPool =  new HashMap<String, Movie>();
	}

	/**
	 * 获取自持单例
	 * */
	public synchronized static SKGifBufPool getInstance(){
		if(null ==  mSelfInstance){//第一次获取单例
			mSelfInstance = new SKGifBufPool();
			if(null == mSelfInstance){
				Log.e("SKGifBufPool", "getInstance: Instance create failed!");
				return null;	
			}
		}
		return mSelfInstance;
	}

	public void addToPool(String path, Movie  gfnode){
		if(null == path){
			return ;
		}
		if(null == gfnode){
			return;
		}
		if(null == mMovieBufPool){
			return ;
		}
		
		if(!mMovieBufPool.containsKey(path)){//若缓存池中没有包含该节点
			if(mMovieBufPool.size() < mPoolSize){//若缓存池未满
				mMovieBufPool.put(path, gfnode);
			}else{                               //若缓存池满
				mMovieBufPool.clear();           //清除缓存池
				mMovieBufPool.put(path, gfnode); //放入新节点
			}
		}
	}

	public Movie getFromPool(String path){
		if(null == path){
			return null;
		}
		if(null == mMovieBufPool){
			return null;
		}
		
		if(mMovieBufPool.containsKey(path)){//若缓存池中没有包含该节点
			return mMovieBufPool.get(path); //返回节点
		}
		return null;
	}
}
