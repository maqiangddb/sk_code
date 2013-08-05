package com.android.Samkoonhmi.skzip;

import java.io.File;
import java.io.FileOutputStream;

import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.plccommunicate.CmnPortManage;
import com.android.Samkoonhmi.skenum.CONNECT_TYPE;
import com.android.Samkoonhmi.skwindow.SKToast;
import com.android.Samkoonhmi.util.SavaInfo;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class skzip {

	// 单例
	private static skzip sInstance = null;
	public synchronized static skzip getInstance() {
		if (sInstance == null) {
			sInstance = new skzip();
		}
		return sInstance;
	}

	private skzip() {}

	/**
	 * 取得压缩包中的 文件列表(文件夹,文件自选)
	 * @param zipFileString压缩包名字
	 * @param bContainFolder是否包括 文件夹
	 * @param bContainFile是否包括 文件
	 * @return
	 * @throws Exception
	 */
	public java.util.List<java.io.File> GetFileList(String zipFileString,
			boolean bContainFolder, boolean bContainFile) throws Exception {

		android.util.Log.v("skzip", "GetFileList(String)");

		java.util.List<java.io.File> fileList = new java.util.ArrayList<java.io.File>();
		java.util.zip.ZipInputStream inZip = new java.util.zip.ZipInputStream(
				new java.io.FileInputStream(zipFileString));
		java.util.zip.ZipEntry zipEntry;
		String szName = "";

		while ((zipEntry = inZip.getNextEntry()) != null) {
			szName = zipEntry.getName();

			if (zipEntry.isDirectory()) {

				// get the folder name of the widget
				szName = szName.substring(0, szName.length() - 1);
				java.io.File folder = new java.io.File(szName);
				if (bContainFolder) {
					fileList.add(folder);
				}

			} else {
				java.io.File file = new java.io.File(szName);
				if (bContainFile) {
					fileList.add(file);
				}
			}
			// end of while
		}

		inZip.close();

		return fileList;
	}

	/**
	 * 返回压缩包中的文件InputStream
	 * @param zipFileString压缩文件的名字
	 * @param fileString解压文件的名字
	 * @return InputStream
	 * @throws Exception
	 */
	public java.io.InputStream UpZip(String zipFileString, String fileString)
			throws Exception {
		android.util.Log.v("skzip", "UpZip(String, String)");
		java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(zipFileString);
		java.util.zip.ZipEntry zipEntry = zipFile.getEntry(fileString);

		return zipFile.getInputStream(zipEntry);

	}

	/**
	 * 解压一个压缩文档 到指定位置
	 * @param zipFileString压缩包的名字
	 * @param outPathString指定的路径
	 * @throws Exception
	 */
	private boolean bPlcDown=false;
	public void UnZipFolder(String zipFileString) throws Exception {

		try {
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

			// 删除图片
			File dd = new File("/data/data/com.android.Samkoonhmi/pictures");
			if (dd.exists()) {
				delAllFile("/data/data/com.android.Samkoonhmi/pictures");
			}

			// 删除字体
			File ff = new File("/data/data/com.android.Samkoonhmi/fonts");
			if (ff.exists()) {
				delAllFile("/data/data/com.android.Samkoonhmi/fonts");
			}

			// 删除开机动画
			File ee = new File("/data/data/com.android.Samkoonhmi/usranipro/bootanimation.zip");
			
			if (ee.exists()) {
				ee.delete();
				File ll = new File("/data/data/com.android.Samkoonhmi/usranipro");
				if (ll.exists()) {
					ll.delete();
				}
			}
			

			String sAkPath = "/data/data/com.android.Samkoonhmi/";
			String sLauncherPath = "/data/data/com.samkoon.sklauncher/";
			String outPathString = "";

			android.util.Log.v("skzip", "UnZipFolder(String, String)");
			java.util.zip.ZipInputStream inZip = new java.util.zip.ZipInputStream(
					new java.io.FileInputStream(zipFileString));
			java.util.zip.ZipEntry zipEntry;
			String szName = "";
			String newfolder = "";
			String newfile = "";
			int index;

			boolean isEmu=false;//是不是模拟器
			File emufile = new File(sLauncherPath);
			if (!emufile.exists()) {
				isEmu=true;
			}
			
			while ((zipEntry = inZip.getNextEntry()) != null) {
				szName = zipEntry.getName();

				boolean write = true;

				if (zipEntry.isDirectory()) {

					android.util.Log.v("skzip", "mkfolder");
					szName = szName.substring(0, szName.length() - 1);
					java.io.File folder = new java.io.File(outPathString
							+ java.io.File.separator + szName);
					folder.mkdirs();

				} else {

					index = szName.indexOf("/");
					if (index != -1) {
						newfolder = szName.substring(0, szName.lastIndexOf("/"));
						if (newfolder != "") {
							newfile = szName.substring(szName.lastIndexOf("/") + 1,
									szName.length()).toString();
						}
					}

					FileOutputStream out = null;// =new FileOutputStream(file);
					if (newfolder != "") {
						if (newfolder.equals("armeabi") || newfolder.equals("x86")) {
							outPathString = sAkPath + "lib/";
						} else if (newfolder.equals("resource")) {
							outPathString = sAkPath + "pictures/";
						}else if (newfolder.equals("soar")) {
							outPathString = sAkPath + "soar/";
						}

						newfolder = "";
						makeRootDirectory(outPathString);
						File file = getFilePath(outPathString
								+ java.io.File.separator + newfolder, newfile);
						if (file.exists()) {
							file.delete();
						}
						out = new FileOutputStream(file);
					} else {

						if (szName.equals("sd.dat")) {
							outPathString = sAkPath + "databases/";
						} else if (szName.equals("ml.jar")) {
							outPathString = sAkPath + "macro/";
						} else if (szName.equals("recipe.dat")) {
							outPathString = sAkPath + "formula/";
						} else if (szName.equals("vXkIp.m")) {
							if (isEmu) {
								// 模拟器不写
								write = false;
							}
							outPathString = sLauncherPath + "vFiHpd/";
						} else if (szName.equals("fileMap.bin")) {
							outPathString = sAkPath;
						} else if (szName.equals("bootanimation.zip")) {
							// 开机动画
							outPathString = sAkPath + "usranipro/";
							if (isEmu) {
								write=false;
							}
							
						} else {
							// 字体
							outPathString = sAkPath + "fonts/";
						}

						if (write) {
							makeRootDirectory(outPathString);

							File file = getFilePath(outPathString, szName);
							if (file.exists()) {
								file.delete();
							}
							out = new FileOutputStream(file);
						}
					}

					if (write) {
						int len;
						byte[] buffer = new byte[1024];
						while ((len = inZip.read(buffer)) != -1) {
							out.write(buffer, 0, len);
							out.flush();
						}
						out.close();
					}
					newfolder = "";
					newfile = "";

				}
				// end of while
			}
			
			inZip.close();

			File file = new File("/data/data/com.android.Samkoonhmi/samkoonhmi.akz");
			if (file.exists()) {
				file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("AKZIP", "ak zip error!!!");
			//一般解压错误，是由于往sklauncher写文件没权限造成
			File file=new File("/data/data/com.samkoon.sklauncher/vFiHpd/vXkIp.m");
			if (file.exists()) {
				file.delete();
			}
			
			File dir=new File("/data/data/com.samkoon.sklauncher/vFiHpd/");
			if (dir.exists()) {
				dir.delete();
			}
			SavaInfo.setState(2);
		}
		
		// end of func

	}


	/**
	 * 压缩文件,文件夹
	 * @param srcFileString 要压缩的文件/文件夹名字
	 * @param zipFileString 指定压缩的目的和名字
	 * @throws Exception
	 */
	public void ZipFolder(String srcFileString, String zipFileString)
			throws Exception {
		android.util.Log.v("skzip", "ZipFolder(String, String)");

		// 创建Zip包
		java.util.zip.ZipOutputStream outZip = new java.util.zip.ZipOutputStream(
				new java.io.FileOutputStream(zipFileString));

		// 打开要输出的文件
		java.io.File file = new java.io.File(srcFileString);

		// 压缩
		ZipFiles(file.getParent() + java.io.File.separator, file.getName(),
				outZip);

		// 完成,关闭
		outZip.finish();
		outZip.close();
		// end of func
	}

	/**
	 * 压缩文件
	 * @param folderString
	 * @param fileString
	 * @param zipOutputSteam
	 * @throws Exception
	 */
	private void ZipFiles(String folderString, String fileString,
			java.util.zip.ZipOutputStream zipOutputSteam) throws Exception {
		android.util.Log
				.v("skzip", "ZipFiles(String, String, ZipOutputStream)");

		if (zipOutputSteam == null)
			return;

		java.io.File file = new java.io.File(folderString + fileString);

		// 判断是不是文件
		if (file.isFile()) {

			java.util.zip.ZipEntry zipEntry = new java.util.zip.ZipEntry(
					fileString);
			java.io.FileInputStream inputStream = new java.io.FileInputStream(
					file);
			zipOutputSteam.putNextEntry(zipEntry);

			int len;
			byte[] buffer = new byte[4096];

			while ((len = inputStream.read(buffer)) != -1) {
				zipOutputSteam.write(buffer, 0, len);
			}

			zipOutputSteam.closeEntry();
		} else {

			// 文件夹的方式,获取文件夹下的子文件
			String fileList[] = file.list();

			// 如果没有子文件, 则添加进去即可
			if (fileList.length <= 0) {
				java.util.zip.ZipEntry zipEntry = new java.util.zip.ZipEntry(
						fileString + java.io.File.separator);
				zipOutputSteam.putNextEntry(zipEntry);
				zipOutputSteam.closeEntry();
			}

			// 如果有子文件, 遍历子文件
			for (int i = 0; i < fileList.length; i++) {
				ZipFiles(folderString, fileString + java.io.File.separator
						+ fileList[i], zipOutputSteam);
			}

		}

	}

	public static File getFilePath(String filePath, String fileName) {
		File file = null;
		makeRootDirectory(filePath);
		try {
			file = new File(filePath + java.io.File.separator + fileName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file;
	}

	public static void makeRootDirectory(String filePath) {
		File file = null;
		try {
			file = new File(filePath);
			if (!file.exists()) {
				file.mkdir();
			}
		} catch (Exception e) {

		}
	}

	public void finalize() throws Throwable {

	}

	/**
	 * 删除文件夹
	 */
	public void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			// filePath = filePath.toString();
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
				// delFolder(path + "/" + tempList[i]);// 再删除空文件夹
			}
		}

		File myFilePath = new File(path);
		myFilePath.delete(); // 删除空文件夹
	}

}