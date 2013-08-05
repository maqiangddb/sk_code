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

//	/**
//	 * 复制整个文件夹内容
//	 */
//	public void copyFolder(FileInfo info) {
//
//		String oldPath = "/data/data/com.android.Samkoonhmi/update/"
//				+ info.name;
//	
//		delAllFile(info.path);
//		
//		try {
//			File aa=new File(info.path);
//			if(!aa.exists()){
//				aa.mkdir(); // 如果文件夹不存在 则建立新文件夹
//			}
//			File a = new File(oldPath);
//			String[] file = a.list();
//			if (file==null) {
//				return;
//			}
//			File temp = null;
//			for (int i = 0; i < file.length; i++) {
//				if (oldPath.endsWith(File.separator)) {
//					temp = new File(oldPath + file[i]);
//				} else {
//					temp = new File(oldPath + File.separator + file[i]);
//				}
//				if (temp.isFile()) {
//					FileInputStream input = new FileInputStream(temp);
//					String name=info.path+ (temp.getName()).toString();
//					FileOutputStream output = new FileOutputStream(name);
//					byte[] b = new byte[1024 * 5];
//					int len;
//					while ((len = input.read(b)) != -1) {
//						output.write(b, 0, len);
//					}
//					output.flush();
//					output.close();
//					input.close();
//				}
//
//			}
//			
//			//删除文件
//			delAllFile(oldPath+info.name);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}

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

}
