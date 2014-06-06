package com.android.Samkoonhmi.plccommunicate;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;

import com.android.Samkoonhmi.system.SystemVariable;
import com.android.Samkoonhmi.util.PlcSampInfo;




public class PrintProtocol {
	
	/**
	 * 打印文本字符串. 
	 * @param @in Vector<String > data : 需要打印的文本字符串数组
	 * @param @in port：串口参数： 3 串口1， 4 串口2
	 * @return
	 */
	public synchronized static boolean printTextData(Vector<String > data,int model ,int port)
	{
		port=port+2;
		PlcSampInfo PlcInfo = new PlcSampInfo();
		PlcInfo.eConnectType = port;

	    String sTmp;
	    Vector<Byte> returnAry = new Vector<Byte>();
	    Vector<Byte> header = new Vector<Byte>();
	    header.clear();
	    returnAry.clear();
	    	    
	    header.add((byte)0x1b);
	    header.add((byte)0x31);
	    header.add((byte)0x03);
	    
	    Vector<Vector<Byte>> pPrintData = new Vector<Vector<Byte>>();
	    //pPrintData.add(header);
	    
	    
	    returnAry.add((byte) 0x0d);
	    returnAry.add((byte) 0x0a);
	    pPrintData.add(returnAry);
	    for (int i=0;i<data.size();i++)
	    {
	        sTmp = data.get(i);
	        /*编码转换*/
	        Vector<Byte> ary = new Vector<Byte>() ;
	        byte[] byteTemp;
			try {
				ary.clear();
//				ary.add((byte)0x1b);
//				ary.add((byte)0x38);
//				ary.add((byte)0x00);//16*16点阵汉字
			    
				byteTemp = sTmp.getBytes("gbk");
				for(int j = 0; j < byteTemp.length; j++)
		        {
		        	ary.add(byteTemp[j]);
		        }
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
	        
	        pPrintData.add(ary);
	        pPrintData.add(returnAry);      //增加换行
	    }
	    
	    for(int k = 0; k < pPrintData.size(); k++)
	    {
	    	Vector<Byte> bTemp = pPrintData.get(k);
	    	int size = bTemp.size();
	    	byte[] data1 = new byte[size];
	    	for(int n = 0; n < size; n++)
	    	{
	    		data1[n] = bTemp.get(n);
	    	}
	    	boolean bSend = CmnPortManage.getInstance().sendData(PlcInfo, data1);
	    	if(!bSend)
	    	{
	    		return false;
	    	}
	    }
	    return true;
	}
	
	
	/**
	 * 打印pBitMap图片. 
	 * @param @in pBitMap : 需要打印的图片
	 * @param @in port：串口参数： 3 串口1， 4 串口2
	 * @return
	 */
	private synchronized static boolean printBitmapDataA5(Bitmap pBitMap,int model ,int port){	
		//Bitmap pBitMap = BitmapFactory.decodeFile("/data/k.jpg"); 
		port=port+2;
		//换行符
		Vector<Byte> tmpEnd = new Vector<Byte>();
	    tmpEnd.add((byte) 0x0D);
	    tmpEnd.add((byte) 0x0A);
	    
		Vector<Vector<Byte>> pPrintData = new Vector<Vector<Byte>>();
		pPrintData.clear();
		
		int height = pBitMap.getHeight();
		int width = pBitMap.getWidth();
		
		int nRowsize = (height + 7)/8;//所有行数
		int size = nRowsize * width;
		byte[] data = new byte[size];
		int[][] gray=new int[width][height];
		
		getGrayMap(gray,pBitMap,width,height,model);
		
//		打印灰值图，直观查看图片的灰度处理是否合适
//		for(int i=0;i<height;i++){
//			for(int j=0;j<width;j++){
//				if(gray[j][i]>127){
//					System.out.print("-");
//				}else{
//					System.out.print("*");
//				}
//			}
//			System.out.println();
//		}
		
		//把灰值图转换成字节流
		changeGraymapToBytedata(gray,data,width,height,model);
//		
//		//数据转换，对应A5此步在转换灰值图时已经完成
//		data = ConverData(data,height,nRowsize,model);
//		
//		//获得byte流，用于模拟打印
//		for(int i=0;i<nRowsize;i++){
//			for(int j=0;j<width;j++){
//				System.out.print("(byte)0x"+Integer.toHexString(data[i*width+j]&0xff)+",");
//			}
//			System.out.println();
//		}
	
		
		//初始化打印机
		Vector<Byte> init = new Vector<Byte>();
		init.add((byte)0x1B);
		init.add((byte)0x40);
		 //设置行间距
	    Vector<Byte> rowGap0 = new Vector<Byte>();
	    rowGap0.add((byte)0x1B);
	    rowGap0.add((byte)0x31);
	    rowGap0.add((byte)0x00);//行间距为0
	    Vector<Byte> rowGap = new Vector<Byte>();
	    rowGap.add((byte)0x1B);
	    rowGap.add((byte)0x31);
	    rowGap.add((byte)0x03);//行间距为3
		
	    //初始化
	    pPrintData.add(init);
		//先把行间距设成0
		pPrintData.add(rowGap0);
		
//		int maxWidth = 0xF0;
		//页数
//		int page = (width+(maxWidth-1))/maxWidth;
//		//画图指令
//		for(int i=0;i<page;i++){
//			for(int nRow=0;nRow < nRowsize; nRow++){
//				Vector<Byte> tmp = new Vector<Byte>();
//		    	tmp.clear();
//		    	
//		        if(i==page-1){
//		        	//打印图形命令
//			        tmp.add((byte)0x1B);
//			        tmp.add((byte)0x4B);
//		        	
//		        	//行数
//			        tmp.add((byte)(width-(page-1)*maxWidth));
//			        tmp.add((byte)(0x00));
//			        
//		        	for(int nCol = i*maxWidth;nCol < width;nCol++){
//			        	tmp.add(data[nRow*width + nCol]);
//			        }
//		        }else{
//		        
//			      //打印图形命令
//			        tmp.add((byte)0x1B);
//			        tmp.add((byte)0x4B);
//			        
//		        	//行数
//			        tmp.add((byte)(maxWidth));
//			        tmp.add((byte)(0x00));
//		        	
//			        for(int nCol = i*maxWidth;nCol < (i+1)*maxWidth;nCol++){
//			        	tmp.add(data[nRow*width + nCol]);
//			        }
//		        }
//		        pPrintData.add(tmp); //添加一串发送代码
//		        pPrintData.add(tmpEnd);                     //打印完毕添加回车换行
//			}
//		}
		
		//把字节流转成打印机能识别的形式，主要是加命令头、限制字节数及换行处理
		for(int nRow=0;nRow < nRowsize; nRow++){
			Vector<Byte> tmp = new Vector<Byte>();
			tmp.clear();
			
			//打印图形命令
	        tmp.add((byte)0x1B);
	        tmp.add((byte)0x4B);
			
	        //行数
	        tmp.add((byte)(0xF0));
	        tmp.add((byte)(0x00));
	        int startPoint=0;
	        
	        //限制最大宽度
	        if(width > 0xF0){
	        	startPoint = width-0xF0;
	        }
	        //8行图点数据
	        for(int nCol = startPoint;nCol < width;nCol++){
	        	tmp.add(data[nRow*width + nCol]);
	        }
	        //限制最大宽度
	        // tmp.setSize(0xF4);
	        //换行
	        tmp.add((byte) 0x0D);
	        tmp.add((byte) 0x0A);
	        
	        //添加一串发送代码
	        pPrintData.add(tmp); 
		}
		//再把行间距设回默认值
		pPrintData.add(rowGap);    
		//打印完毕添加回车换行
//		for (int j=0;j<4;j++)
//	    {
//	        pPrintData.add(tmpEnd);                     
//	    }
		//把打印机能识别的字节流通过设定的端口传输出去
	    PlcSampInfo PlcInfo = new PlcSampInfo();
	    PlcInfo.eConnectType = port;
	    for(int k = 0; k < pPrintData.size(); k++)
	    {
	    	Vector<Byte> bTemp = pPrintData.get(k);
	    	int size1 = bTemp.size();
	    	if(size1==0){
	    		Log.e("print", "0 size!");
	    		continue;
	    	}
	    	byte[] data1 = new byte[size1];
	    	for(int n = 0; n < size1; n++)
	    	{
	    		data1[n] = bTemp.get(n);
	    	}
	    	
	    	boolean bSend = CmnPortManage.getInstance().sendData(PlcInfo, data1);
	    	if(!bSend)
	    	{
	    		return false;
	    	}
	    }
		return true;
	}
	
	
	private synchronized static boolean printBitmapDataE19(Bitmap pBitMap,int model ,int port){	
		//Bitmap pBitMap = BitmapFactory.decodeFile("/data/k.jpg"); 
		port=port+2;
		//换行符
		Vector<Byte> tmpEnd = new Vector<Byte>();
	    tmpEnd.add((byte) 0x0D);
	    tmpEnd.add((byte) 0x0A);
	    
		Vector<Vector<Byte>> pPrintData = new Vector<Vector<Byte>>();
		pPrintData.clear();
		
		int height = pBitMap.getHeight();
		int width = pBitMap.getWidth();
		
		int nRowsize = (width + 7)/8;//一行所需的字节数 一个字节存储8个像素点
		int size = nRowsize * height;
		byte[] data = new byte[size];
		int[][] gray=new int[height][width];
		
		getGrayMap(gray,pBitMap,width,height,model);
		
		changeGraymapToBytedata(gray,data,width,height,model);
		
		data = ConverData(data,height,nRowsize,model);
		
//		for(int i=0;i<nRowsize;i++){
//			for(int j=0;j<height;j++){
//				System.out.print("(byte)0x"+Integer.toHexString(data[i*height+j]&0xff)+",");
//			}
//			System.out.println();
//		}
		
	    for (int nRow = 0; nRow < height; nRow++)
	    {
	    	Vector<Byte> tmp = new Vector<Byte>();
	    	tmp.clear();
	    	
			//打印图形命令
	        tmp.add((byte)0x1B);
	        tmp.add((byte)0x56);
	        //行数
	        tmp.add((byte)(0x00));
	        tmp.add((byte)0x48);
			 
	        for(int k = 0; k  < 72 - nRowsize; k++)
	        {
	        	tmp.add((byte)(0x00));
	        }
	        for (int nCol = 0; nCol < nRowsize; nCol++)
	        {
	            tmp.add((byte)data[nRow*nRowsize + nCol]);
	        }
	        tmp.setSize(76);
	        pPrintData.add(tmp); //添加一串发送代码
	    }
		
		for (int j=0;j<8;j++)
	    {
	        pPrintData.add(tmpEnd);                     //打印完毕添加回车换行
	    }
		
	    PlcSampInfo PlcInfo = new PlcSampInfo();
	    PlcInfo.eConnectType = port;
	    for(int k = 0; k < pPrintData.size(); k++)
	    {
	    	Vector<Byte> bTemp = pPrintData.get(k);
	    	int size1 = bTemp.size();
	    	if(size1==0){
	    		Log.e("print", "0 size!");
	    		continue;
	    	}
	    	byte[] data1 = new byte[size1];
	    	for(int n = 0; n < size1; n++)
	    	{
	    		data1[n] = bTemp.get(n);
	    	}
	    	
	    	boolean bSend = CmnPortManage.getInstance().sendData(PlcInfo, data1);
	    	if(!bSend)
	    	{
	    		return false;
	    	}
	    }
		return true;
		
	}
	
	public synchronized static boolean printBitmapData(Bitmap pBitMap,int model ,int port) {
		boolean result=false;
		switch(model){
			case 1:
				printBitmapDataE19( pBitMap, model , port);
				break;
			case 2:
				printBitmapDataA5( pBitMap, model , port);
				break;
		
		}
		return result;
	}
	
	//getAverageColor(gray, j, k, height, width)
	public static int  getAverageColor(int[][] gray, int x, int y, int w, int h)  
    {  
        int rs = gray[x][y]  
                        + (x == 0 ? 255 : gray[x - 1][y])  
                        + (x == 0 || y == 0 ? 255 : gray[x - 1][y - 1])  
                        + (x == 0 || y == h - 1 ? 255 : gray[x - 1][y + 1])  
                        + (y == 0 ? 255 : gray[x][y - 1])  
                        + (y == h - 1 ? 255 : gray[x][y + 1])  
                        + (x == w - 1 ? 255 : gray[x + 1][ y])  
                        + (x == w - 1 || y == 0 ? 255 : gray[x + 1][y - 1])  
                        + (x == w - 1 || y == h - 1 ? 255 : gray[x + 1][y + 1]);  
        return rs / 9;  
    }  
	
	//生成灰值图
	public static void getGrayMap(int[][] gray,Bitmap pBitMap,int width, int height,int model){
		if(model==1){
			for(int i = 0; i < height; i++)//y
			{
				for(int j = 0; j < width; j++)//x
				{
					int color = pBitMap.getPixel(j,i); //取该位置像素的RGB值 
					int r = Color.red(color);
					int g = Color.green(color);
					int b = Color.blue(color);
					gray[i][j] = (r + g + b)/3;
				}
			}
		}else if(model==2){
			for(int i = 0; i < width; i++)//x
			{
				for(int j = 0; j < height; j++)//y
				{
					int color = pBitMap.getPixel(i,j); //取该位置像素的RGB值 
					int r = Color.red(color);
					int g = Color.green(color);
					int b = Color.blue(color);
					gray[i][j] = (r + g + b)/3;
				}
			}
		}
	}
	
	//生成对应的byte字节流
	static void changeGraymapToBytedata(int[][] gray ,byte[] data ,int width, int height ,int model){
		if(model==1){
			int SW=160; 
			int nRowsize = (width + 7)/8;//一行所需的字节数 一个字节存储8个像素点 
			for(int j = 0; j < height; j++)
			{
				for(int k = 0 ; k < width; k++)
				{
					int index = j * nRowsize + k/8;
					//模糊图片
					if(getAverageColor(gray, j, k, height, width)>SW)
					{  
						  
	                }
					else
					{  
						byte t = 1;
						t = (byte) (t << (8 - (k%8 + 1)));
						data[index] = (byte) (data[index] |  t);
	                } 
				}
			}
		}else if(model==2){
			int SW = SystemVariable.getInstance().getGrayThredsholdA5();
			for(int j = 0; j < width; j++)
			{
				for(int k = 0 ; k < height; k++)
				{
					int index = (width-1-j) + k/8*width;// (width-1-j) 实现左右颠倒
					
					//实值判断
					if(gray[j][k]>SW){
						
					}else{
						byte t = 1;
						t = (byte) (t << k%8);//先打印低位，直接地位灰点存地位，不用做Conver处理
						data[index] = (byte) (data[index] |  t);
					}
						
				}
			}
		}
	}
	
	static byte[] ConverData(byte[] praw,int cols,int rows,int model)
	{
		byte[] tmp = new byte[cols * rows];
	    byte cTmp;
		/*先上下颠倒*/
	    int ipos;
	    for (int i=0;i<cols ;i++)
	    {
	        ipos = (cols - i - 1)*rows;
	        for (int j=0;j<rows;j++)
	        {
	        	tmp[i * rows + j] = praw[ipos + rows - 1 - j];
	            //tmp.append(praw->at(ipos + rows - 1 - j));
	        }
	    }
	    /*高低颠倒*/
	    byte[] c8Bit = new byte[8];
	    for (int i=0;i<tmp.length;i++)
	    {
	        cTmp = tmp[i];
	        for (int j=0;j<8;j++)
	        {
	            c8Bit[j] = (byte)((cTmp >> j)&0x01);
	        }
	        cTmp = 0x00;
	        for (int j=0;j<8;j++)
	        {
	            cTmp = (byte) (cTmp + (c8Bit[7-j] << j));
	        }
	        tmp[i] = cTmp;
	    }
	    return tmp;
	}
	
	public static Bitmap toGrayscale(Bitmap bmpOriginal) {
		int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();    

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
		    }
}
