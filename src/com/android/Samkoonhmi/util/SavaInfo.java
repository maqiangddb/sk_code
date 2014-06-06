package com.android.Samkoonhmi.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import android.util.Log;
import com.android.Samkoonhmi.model.SystemInfo;

/**
 * 保存apk版本
 */
public class SavaInfo {

	private final static String NOTE_PATH = "/data/data/com.android.Samkoonhmi/information.txt";
	private final static String MODEL_PATH = "/data/data/com.android.Samkoonhmi/model.txt";
	private final static String STATE = "/data/data/com.android.Samkoonhmi/ak_state.txt";
	private final static String TIMESAVE = "/data/data/com.android.Samkoonhmi/timesave.txt";
	private static String version = "VERSIONCODE=58";

	/**
	 * 
	 */
	public static void save() {
		FileOutputStream fos = null;

		try {
			// runCommand("chmod 777 /data/data/com.android.Samkoonhmi");

			File mFile = new File(NOTE_PATH);
			if (mFile.exists()) {
				mFile.delete();
			}

			File file = new File(NOTE_PATH);
			fos = new FileOutputStream(file);
			fos.write(version.getBytes());
			fos.flush();

			// runCommand("chmod 777 /data/data/com.android.Samkoonhmi/information.txt");

		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * @type=1 NO, 2-YES
	 */
	public static void setState(int type) {
		FileOutputStream fos = null;
		try {

			String info = "";
			if (type == 1) {
				info = "NO";
				//不自动重启
			} else if (type == 2) {
				info = "YES";
				//自动重启
			}else if (type==3) {
				info="SYSTEMUI";
				//显示系统UI
			}

			File mFile = new File(STATE);
			if (mFile.exists()) {
				mFile.delete();
			}

			File file = new File(STATE);
			fos = new FileOutputStream(file);
			fos.write(info.getBytes());
			fos.flush();

			runCommand("chmod 777 /data/data/com.android.Samkoonhmi/ak_state.txt");

		} catch (Exception e) {
			e.printStackTrace();
			Log.d("SavaInfo", "set restart state error!!!");
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 触摸屏型号
	 */
	public static void model() {
		FileOutputStream fos = null;

		try {
			runCommand("chmod 777 /data/data/com.android.Samkoonhmi");

			File mFile = new File(MODEL_PATH);
			if (mFile.exists()) {
				mFile.delete();
			}

			File file = new File(MODEL_PATH);
			fos = new FileOutputStream(file);
			fos.write(version.getBytes());
			fos.flush();

			runCommand("chmod 777 /data/data/com.android.Samkoonhmi/model.txt");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 改变文件权限
	 */
	public static void runCommand(final String command) {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Process process = null;
				try {
					process = Runtime.getRuntime().exec("su");
					process = Runtime.getRuntime().exec(command);
					process.waitFor();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		
		
	}

	/**
	 * 将是否启用授权保护写入文件
	 */
	public static void saveTimeInfo() {
		FileOutputStream fos = null;

		try {
			File mFile = new File(TIMESAVE);
			if (mFile.exists()) {
				mFile.delete();
			}
			File file = new File(TIMESAVE);
			fos = new FileOutputStream(file);
			
			FileInputStream fis=new FileInputStream(file);
			//采用了时效授权
			if ((SystemInfo.getnSetBoolParam() & SystemParam.HMI_PROTECT) == SystemParam.HMI_PROTECT) {
				if (null != SystemInfo.getPassWord()) {
					if (0 != SystemInfo.getPassWord().size()) {
						//至少启用了一条授权
						fos.write("yes".getBytes());
					} else {
						fos.write("no".getBytes());
					}
				} else {
					fos.write("no".getBytes());
				}

			} else {
				fos.write("no".getBytes());
			}

			fos.flush();
			
			// 修改文件权限
			runCommand("chmod 777 /data/data/com.android.Samkoonhmi/timesave.txt");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
