package com.android.Samkoonhmi.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.android.Samkoonhmi.databaseinterface.DBTool;
import com.android.Samkoonhmi.model.MessageDetailInfo;

/**
 * 留言板工具类
 * 
 * @author 瞿丽平
 * 
 */
public class MessageBoardUtil {
	private static MessageBoardUtil instance = null;
	// 留言板内容保存路径
	private final String path = "/data/data/com.android.Samkoonhmi/files/email_files/message.txt";

	public static MessageBoardUtil getInstance() {
		if (null == instance) {
			instance = new MessageBoardUtil();
		}
		return instance;
	}

	/**
	 * 是否有留言
	 * 
	 * @return
	 */
	public boolean hasMessage() {
		return DBTool.getInstance().getMessageBoard().hasMessage();
	}

	/**
	 * 生成留言板内容文件
	 */
	public void getMessageContent() {
		List<MessageDetailInfo> list = DBTool.getInstance().getMessageBoard()
				.getAllMessageContent();
		try {
			File file = new File(path);
			boolean b = createFile(file);
			if (b) {
				writeTxtFile(list, file);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 创建文件
	 * 
	 * @param fileName
	 * @return
	 */
	private boolean createFile(File fileName) throws Exception {
		boolean flag = false;
		File f = new File("/data/data/com.android.Samkoonhmi/files");
		if (!f.exists()) {
			f.mkdir();
		}

		File ff = new File(
				"/data/data/com.android.Samkoonhmi/files/email_files/");
		if (!ff.exists()) {
			ff.mkdir();
		}
		try {
			if (fileName.exists()) {
				fileName.delete();
			}
			if (!fileName.exists()) {
				fileName.createNewFile();
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	private boolean writeTxtFile(List<MessageDetailInfo> list, File fileName)
			throws Exception {
		RandomAccessFile mm = null;
		boolean flag = false;
		FileOutputStream o = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			o = new FileOutputStream(fileName);
			for (int i = 0; i < list.size(); i++) {
				String time = format.format(new Date(list.get(i).getnTime()));
				String content = time + "   "+list.get(i).getsMessage()+"\r\n";
				o.write(content.getBytes("GBK"));
			}

			o.close();
			flag = true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (mm != null) {
				mm.close();
			}
		}
		return flag;
	}
}
