package com.android.Samkoonhmi.skzip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.android.Samkoonhmi.util.SavaInfo;

/**
 * 复制升级文件
 */
public class AKFileUpdate {

	private Context mContext;
	private static AKFileUpdate sInstance=null;
	public synchronized static AKFileUpdate getInstance(Context context){
		if (sInstance==null) {
			sInstance=new AKFileUpdate(context);
		}
		return sInstance;
	}
	
	public AKFileUpdate(Context context){
		mContext=context;
	}
	
	/**
	 * 更新状态
	 */
	public void update() {
		
		try {
			// 删除报警数据库
			File ff=new File("/data/data/com.android.Samkoonhmi/fileMap.bin");
			if (ff.exists()) {
				ArrayList<FileInfo> list = readFileByLines("/data/data/com.android.Samkoonhmi/fileMap.bin");
				if (list != null) {
					for (int i = 0; i < list.size(); i++) {
						FileInfo info = list.get(i);
						if (info != null) {
							if (info.name.equals("ClearAlarm")) {
								//删除配方数据库
								if (info.path!=null) {
									if (info.path.equals("true")) {
										delAllFile("/data/data/com.android.Samkoonhmi/alarm");
										Log.d("SKScene", "--del alarm--");
										break;
									}
								}
							}
						}
					}
				}
			}

			File file=new File("/data/data/com.android.Samkoonhmi/fileMap.bin");
			if (file.exists()) {
				file.delete();
			}
			
		} catch (Exception e) {
			SavaInfo.setState(2);//更新完毕，把ak状态设置为YES
			e.printStackTrace();
			Log.e("AKFileUpdate", "ak update error!");
		}
	}

	/**
	 * 文件拷贝
	 */
	public void fileCopy(String oldPath,String newPath) {
		
		try {
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (!oldfile.exists()) {
				return;
			}
			
			File newFile=new File(newPath);
			if (newFile.exists()) {
				newFile.delete();
			}
			
			InputStream inStream = new FileInputStream(oldPath); // 读入原文件
			FileOutputStream fs = new FileOutputStream(newPath);
			byte[] buffer = new byte[1444];
			while ((byteread = inStream.read(buffer)) != -1) {
				fs.write(buffer, 0, byteread);
			}
			inStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 复制整个文件夹内容
	 */
	public void copyFolder(String sourcePath,String desPath) {

		String sourcefolder = sourcePath;
		String desfolder = desPath;
	
		delAllFile(desfolder);
		
		try {
			File aa=new File(desfolder);
			if(!aa.exists()){
				aa.mkdirs(); // 如果文件夹不存在 则建立新文件夹
			}
			File a = new File(sourcefolder);
			String[] file = a.list();
			if (file==null) {
				return;
			}
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (sourcefolder.endsWith(File.separator)) {
					temp = new File(sourcefolder + file[i]);
				} else {
					temp = new File(sourcefolder + File.separator + file[i]);
				}
				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					String name=desfolder;
					if (name.endsWith(File.separator)) {
						name = name +  file[i];
					} else {
						name = name + File.separator+ file[i];
					}
					FileOutputStream output = new FileOutputStream(name);
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}

			}
			
			//删除文件
			delAllFile(sourcefolder);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 读取文件
	 */
	public ArrayList<FileInfo> readFileByLines(String fileName) {
		ArrayList<FileInfo> list = new ArrayList<FileInfo>();

		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				if (!tempString.equals("")) {
					FileInfo info = new FileInfo();
					String name = tempString.substring(0,
							tempString.lastIndexOf(",")).toString();
					String path = tempString.substring(
							tempString.lastIndexOf(",") + 1,
							tempString.length()).toString();
					info.name = name;
					info.path = path;
					list.add(info);
				}
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
		return list;
	}

	/**
	 * 删除文件夹
	 */
	public void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			//filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	/**
	 * 删除文件夹里面的所有文件
	 */
	public void delAllFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			return;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				//delFolder(path + "/" + tempList[i]);// 再删除空文件夹
			}
		}
		
		File myFilePath = new File(path);
		myFilePath.delete(); // 删除空文件夹
	}
	
	public class FileInfo {
		public String name;
		public String path;
	}
	
	public static int flag=0;
	public void linkFileToEmu(){
		Log.v("linkFileToEmu","linkFileToEmu");

		// 删除采集数据库
		File aa = new File(
				"/data/data/com.android.Samkoonhmi/databases/dataCollectSave.db");
		if (aa.exists()) {
			aa.delete();
		}

		File bb = new File(
				"/data/data/com.android.Samkoonhmi/databases/dataCollectSave.db-journal");
		if (bb.exists()) {
			bb.delete();
		}

		File gg = new File(
				"/data/data/com.android.Samkoonhmi/shared_prefs/hmiprotct.xml");
		if (gg.exists()) {
			gg.delete();
		}
		
		//删除库文件夹，以便软连接
		runCommand("rmdir /data/data/com.android.Samkoonhmi/lib");			

		// 删除开机动画
		File ee = new File("/data/data/com.android.Samkoonhmi/usranipro/bootanimation.zip");
		
		if (ee.exists()) {
			ee.delete();
		}
		
		File ll = new File("/data/data/com.android.Samkoonhmi/usranipro");
		if (!ll.exists()) {
			ll.mkdir();
		}
		
		String sAkPath = "/data/data/com.android.Samkoonhmi/";
		String sourcePath="/mnt/shared/esd/Udisk";
		String desPath="";
		String fileName="";
		File emuDir = new File(sourcePath);

		if(emuDir.isDirectory()){
			String[] fileList = emuDir.list();
			if(null == fileList){
				Log.v("AKFileUpdate","linkFileToEmu:fileList is null");
				return;
			}
			for(int i=0;i<fileList.length;i++){
				fileName=fileList[i];
				File thisFile = new File(sourcePath+java.io.File.separator+fileName);
				if(thisFile.isDirectory()){
					if(fileName.equals("armeabi")||fileName.equals("x86")){
						desPath=sAkPath + "lib";
					}else if(fileName.equals("resource")){
						desPath=sAkPath + "pictures";
					}
					runCommand("ln -s "+sourcePath+File.separator+fileName+" "+desPath);
				}else{
					if (fileName.equals("sd.dat")) {
						desPath = sAkPath + "databases/";
					} else if (fileName.equals("ml.jar")) {
						desPath = sAkPath + "macro/";
					} else if (fileName.equals("recipe.dat")) {
						desPath = sAkPath + "formula/";
					}else if (fileName.equals("fileMap.bin")) {
						desPath = sAkPath;
					}else if(fileName.equals("vXkIp.m")){
//						File file = new File(sourcePath+java.io.File.separator+fileName);
//						if(file.exists()){
//							file.delete();
//						}
						continue;
					}else if(fileName.equals("bootanimation.zip")){
//						File file = new File(sourcePath+java.io.File.separator+fileName);
//						if(file.exists()){
//							file.delete();
//						}
						continue;
					}else {
						// 字体
						desPath = sAkPath + "fonts/";
					}
					File desfolder = new File(desPath);
					if(!desfolder.exists()){
						desfolder.mkdirs();
					}
//					else{
//						File dst = new File(desPath+fileName);
//						if(dst.exists()){
//							dst.delete();
//						}
//					}
					runCommand("ln -s "+sourcePath+File.separator+fileName+" "+desPath+fileName);
				}
			}
		}
		
		File file = new File("/data/data/com.android.Samkoonhmi/samkoonhmi.akz");
		if (file.exists()) {
			file.delete();
		}

		try{
			Thread.sleep(200);
		}catch(Exception e){
			
		}
		// end of func

	}
	
//	public void copyFileToEmu(){
//
//		try {
//			// 删除采集数据库
//			File aa = new File(
//					"/data/data/com.android.Samkoonhmi/databases/dataCollectSave.db");
//			if (aa.exists()) {
//				aa.delete();
//			}
//
//			File bb = new File(
//					"/data/data/com.android.Samkoonhmi/databases/dataCollectSave.db-journal");
//			if (bb.exists()) {
//				bb.delete();
//			}
//
//			File gg = new File(
//					"/data/data/com.android.Samkoonhmi/shared_prefs/hmiprotct.xml");
//			if (gg.exists()) {
//				gg.delete();
//			}
//
//			// 删除图片
//			File dd = new File("/data/data/com.android.Samkoonhmi/pictures");
//			if (dd.exists()) {
//				delAllFile("/data/data/com.android.Samkoonhmi/pictures");
//			}
//
//			// 删除字体
//			File ff = new File("/data/data/com.android.Samkoonhmi/fonts");
//			if (ff.exists()) {
//				delAllFile("/data/data/com.android.Samkoonhmi/fonts");
//			}
//
//			// 删除开机动画
//			File ee = new File("/data/data/com.android.Samkoonhmi/usranipro/bootanimation.zip");
//			
//			if (ee.exists()) {
//				ee.delete();
//			}
//			
//			File ll = new File("/data/data/com.android.Samkoonhmi/usranipro");
//			if (!ll.exists()) {
//				ll.mkdir();
//			}
//			
//			String sAkPath = "/data/data/com.android.Samkoonhmi/";
//			String sourcePath="/mnt/shared/esd/Udisk";
//			String desPath="";
//			String fileName="";
//			android.util.Log.v("AKFileUpdate", "copyFileToEmu()");
//			File emuDir = new File(sourcePath);
//
//			if(emuDir.isDirectory()){
//				String[] fileList = emuDir.list();
//				if(null == fileList){
//					Log.v("AKFileUpdate","copyFileToEmu:fileList is null");
//					return;
//				}
//				for(int i=0;i<fileList.length;i++){
//					fileName=fileList[i];
//					File thisFile = new File(sourcePath+java.io.File.separator+fileName);
//					if(thisFile.isDirectory()){
//						if(fileName.equals("armeabi")||fileName.equals("x86")){
//							desPath=sAkPath + "lib";
//						}else if(fileName.equals("resource")){
//							desPath=sAkPath + "pictures";
//						}
//						File desfolder = new File(desPath);
//						if(!desfolder.exists()){
//							desfolder.mkdirs();
//						}
//						if(sourcePath.endsWith(File.separator)){
//							copyFolder(sourcePath+fileName,desPath);
//						}else{
//							copyFolder(sourcePath+File.separator+fileName,desPath);
//						}
//					}else{
//						if (fileName.equals("sd.dat")) {
//							desPath = sAkPath + "databases/";
//						} else if (fileName.equals("ml.jar")) {
//							desPath = sAkPath + "macro/";
//						} else if (fileName.equals("recipe.dat")) {
//							desPath = sAkPath + "formula/";
//						}else if (fileName.equals("fileMap.bin")) {
//							desPath = sAkPath;
//						}else if(fileName.equals("vXkIp.m")){
//							File file = new File(sourcePath+java.io.File.separator+fileName);
//							if(file.exists()){
//								file.delete();
//							}
//							continue;
//						}else if(fileName.equals("bootanimation.zip")){
//							File file = new File(sourcePath+java.io.File.separator+fileName);
//							if(file.exists()){
//								file.delete();
//							}
//							continue;
//						}else {
//							// 字体
//							desPath = sAkPath + "fonts/";
//						}
//						File desfolder = new File(desPath);
//						if(!desfolder.exists()){
//							desfolder.mkdirs();
//						}
//						fileCopy(sourcePath+java.io.File.separator+fileName,desPath+fileName);
//						File file = new File(sourcePath+java.io.File.separator+fileName);
//						if(file.exists()){
//							file.delete();
//						}
//					}
//				}
//			}
//			
//			File file = new File("/data/data/com.android.Samkoonhmi/samkoonhmi.akz");
//			if (file.exists()) {
//				file.delete();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			Log.e("AKFileUpdate", "ak zip error!!!");
//			//一般解压错误，是由于往sklauncher写文件没权限造成
//			File file=new File("/data/data/com.samkoon.sklauncher/vFiHpd/vXkIp.m");
//			if (file.exists()) {
//				file.delete();
//			}
//			
//			File dir=new File("/data/data/com.samkoon.sklauncher/vFiHpd/");
//			if (dir.exists()) {
//				dir.delete();
//			}
//			
//			File libFile=new File("/data/data/com.android.Samkoonhmi/lib");
//			if (libFile.exists()) {
//				libFile.delete();
//				Log.d("AKFileUpdate", "ak lib error!!!");
//			}
//		}
//		// end of func
//	}
	private static String com="";
	private static boolean runCommand(String command) {
		
		com=command;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("su");
			process = Runtime.getRuntime().exec(com);
			process.waitFor();
			Log.d("AKFileUpdate", "command:"+com);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("AKFileUpdate", "run "+com+" error!");
		}finally{
			if (process!=null) {
				process.destroy();
			}
		} 
		
		return true;
	}
}
