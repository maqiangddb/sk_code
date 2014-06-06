package com.android.Samkoonhmi.util;

import android.media.AudioManager;
import android.media.SoundPool;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.skwindow.SKSceneManage;

public class Voice {
	private static SoundPool snd;
	private static int hitOkSfx;
	private static Voice voice = null;
	public static Voice getInstance(){
		if(null == voice){
			voice = new Voice();
			
		}
		return voice;
	}
	public void play(){
		if(null == snd){
			snd = new SoundPool(10, AudioManager.STREAM_SYSTEM,5);
		    //载入音频流
			hitOkSfx = snd.load(SKSceneManage.getInstance().mContext,R.raw.beep_once, 0);
		}
		snd.play(hitOkSfx, 0, 1, 0, 0, 1);
	}
	

}
