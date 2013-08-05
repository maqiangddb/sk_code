package com.android.Samkoonhmi.skzip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.skwindow.SKToast;
import com.android.Samkoonhmi.util.AkZipService;
import com.android.Samkoonhmi.util.SavaInfo;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class SkLoad<name> {
	private static final String TAG = "SkLoad";
	ArrayList name;
	// 单例
	private static SkLoad sInstance = null;

	public synchronized static SkLoad getInstance() {
		if (sInstance == null) {
			sInstance = new SkLoad();
		}
		return sInstance;
	}

	private SkLoad() {
		name = new ArrayList();
	}

	public boolean isUpdateFileExist(int i) {
		String USB_PATH = "/mnt/usb2/samkoonhmi.akz";
		String SDCARD_PATH = "/mnt/sdcard/samkoonhmi.akz";

		if (i == 1) {
			File mUsbAkzFile = new File(USB_PATH);
			Log.v(TAG, "mUsbAkzFile " + mUsbAkzFile);
			if (mUsbAkzFile.exists())
				return true;
		} else if (i == 2) {
			File mSdcardAkzFile = new File(SDCARD_PATH);
			Log.v(TAG, "mSdcardAkzFile " + mSdcardAkzFile);
			if (mSdcardAkzFile.exists())
				return true;
		}
		return false;

	}

	public void update_from_udisk() {
		/* 检查文件是否存在 */
		String NOTE_PATH = "/mnt/usb2/samkoonhmi.akz";
		Log.v(TAG, "UpZip udisk");

		File mAkzFile = new File(NOTE_PATH);
		Log.v(TAG, "mAkzFile " + mAkzFile);
		if (mAkzFile.canRead())
			Log.v(TAG, "sd very bad");
		if (mAkzFile.canWrite())
			Log.v(TAG, "sd very good");
//		if (!mAkzFile.exists()) {
//			SKToast.makeText("udisk akz file not exists", Toast.LENGTH_LONG)
//					.show();
//			return;
//		}
		Log.v(TAG, "UpZip(String, String)");
		try {
			// skzip.getInstance().UnZipFolder("/mnt/usb2/samkoonhmi.akz");
			if (SKSceneManage.getInstance().mContext != null) {
				AKFileUpdate
						.getInstance(SKSceneManage.getInstance().mContext)
						.fileCopy("/mnt/usb2/samkoonhmi.akz",
								"/data/data/com.android.Samkoonhmi/samkoonhmi.akz");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//SKToast.makeText("update success,reboot now", Toast.LENGTH_LONG).show();

		Intent intent = new Intent();
		intent.setAction("com.samkoon.reboot");

		SKSceneManage.getInstance().mContext.sendBroadcast(intent);

		Log.v(TAG, "reboot");
		/*
		 * String MAP_PATH ="/mnt/sdcard/samkoonhmi/fileMap.bin"; File MapFile =
		 * new File(MAP_PATH); if (!MapFile.exists()) {
		 * SKToast.makeText("map file not exists", Toast.LENGTH_LONG) .show();
		 * return; } readFileByLines(MAP_PATH);
		 */
	}

	public void update_from_sdcard() {
		/* 检查文件是否存在 */
		String NOTE_PATH = "/mnt/sdcard/samkoonhmi.akz";
		Log.v(TAG, "SDPATH " + NOTE_PATH);
		File mAkzFile = new File(NOTE_PATH);
		if (mAkzFile.canRead())
			Log.v(TAG, "sd very bad");
		if (mAkzFile.canWrite())
			Log.v(TAG, "sd very good");
//		if (!mAkzFile.exists()) {
//			SKToast.makeText("akz file not exists", Toast.LENGTH_LONG).show();
//			return;
//		}

		Log.v(TAG, "UpZip(String, String)");
		try {
			// skzip.getInstance().UnZipFolder("/mnt/sdcard/samkoonhmi.akz");
			if (SKSceneManage.getInstance().mContext != null) {
				AKFileUpdate
						.getInstance(SKSceneManage.getInstance().mContext)
						.fileCopy("/mnt/sdcard/samkoonhmi.akz",
								"/data/data/com.android.Samkoonhmi/samkoonhmi.akz");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//SKToast.makeText("update success,reboot now", Toast.LENGTH_LONG).show();

		Intent intent = new Intent();
		intent.setAction("com.samkoon.reboot");
		SKSceneManage.getInstance().mContext.sendBroadcast(intent);
		Log.v(TAG, "reboot");

		/*
		 * String MAP_PATH ="/mnt/sdcard/samkoonhmi/fileMap.bin"; File MapFile =
		 * new File(MAP_PATH); if (!MapFile.exists()) {
		 * SKToast.makeText("map file not exists", Toast.LENGTH_LONG) .show();
		 * return; } readFileByLines(MAP_PATH);
		 */
	}
	
	/**
	 * 模拟器更新
	 */
	public void updateFile(Context context){
		
		
		AKFileUpdate.getInstance(context).fileCopy(
				"/mnt/shared/esd/samkoonhmi.akz",
				"/data/data/com.android.Samkoonhmi/samkoonhmi.akz");

		Log.d(TAG, "updateFile.......");
		
		Intent intent = new Intent();
		intent.setClass(context, AkZipService.class);
		intent.putExtra("update", "true");
		context.startService(intent);
	}

	/**
	 * 更新主态
	 */
	public boolean update_from_release(Context context,String path) {
		boolean result = true;
		try {
			final ActivityManager activityManager = (ActivityManager) context
					.getSystemService(Service.ACTIVITY_SERVICE);
			ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
			activityManager.getMemoryInfo(info);
			if (info != null) {
				if (info.lowMemory) {
					Log.e("SKScene", "System lowMemory....");
					Intent intent = new Intent();
					intent.setAction("com.samkoon.reboot");
					SKSceneManage.getInstance().mContext.sendBroadcast(intent);
				}
				long mem = (info.availMem >> 10);
				if (mem < 51200) {
					Log.e("SKScene", "mem:" + (info.availMem >> 10) + "k");
					Intent intent = new Intent();
					intent.setAction("com.samkoon.reboot");
					SKSceneManage.getInstance().mContext.sendBroadcast(intent);
				}
			}

			skzip.getInstance().UnZipFolder(path);
		} catch (Exception e) {
			result = false;
			SavaInfo.setState(2);// 更新完毕，把ak状态设置为YES
			e.printStackTrace();
			Log.e(TAG, "update file error!!!");
		}
		return result;
	}

	private void getFileName(File[] files) {
		Log.v(TAG, "files1 " + files);
		if (files != null) {// 先判断目录是否为空，否则会报空指针
			for (File file : files) {
				if (file.isDirectory()) {
					Log.i(TAG, "若是文件目录。继续读1" + file.getName().toString()
							+ file.getPath().toString());

					getFileName(file.listFiles());
					Log.i(TAG, "若是文件目录。继续读2" + file.getName().toString()
							+ file.getPath().toString());
				} else {
					String fileName = file.getName();
					// if (fileName.endsWith(".txt"))
					{
						HashMap map = new HashMap();
						String s = fileName.substring(0,
								fileName.lastIndexOf(".")).toString();
						Log.i(TAG, "文件名txt：：   " + s);
						map.put("Name", fileName.substring(0,
								fileName.lastIndexOf(".")));
						name.add(map);
					}
				}
			}
		}
		for (int i = 0; i < name.size(); i++) {
			Log.i(TAG, "list.  name:  " + name.get(i));
		}
	}

	public static boolean runCommand(String command) {
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("su");
			process = Runtime.getRuntime().exec(command);
		} catch (Exception e) {
			return false;
		} finally {
			try {
				process.destroy();
			} catch (Exception e) {
			}
		}
		return true;
	}

	public static void readFileByLines(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			System.out.println("以行为单位读取文件内容，一次读一整行：");
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int line = 1;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				// 显示行号
				System.out.println("line " + line + ": " + tempString);
				String src = tempString.substring(0,
						tempString.lastIndexOf(",")).toString();
				String dts = tempString.substring(
						tempString.lastIndexOf(",") + 1, tempString.length())
						.toString();
				System.out.println("src: " + src + "  dts: " + dts);
				line++;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}

}
