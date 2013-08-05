package com.android.Samkoonhmi.plccommunicate;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import com.android.Samkoonhmi.util.PlcSampInfo;




public class PrintProtocol {
	
	/**
	 * 打印文本字符串. 
	 * @param @in Vector<String > data : 需要打印的文本字符串数组
	 * @param @in port：串口参数： 3 串口1， 4 串口2
	 * @return
	 */
	public synchronized static boolean printTextData(Vector<String > data,int port)
	{
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
	public synchronized static boolean printBitmapData(Bitmap pBitMap,int port) {	
		//Bitmap pBitMap = BitmapFactory.decodeFile("/data/k.jpg"); 
			
		Vector<Vector<Byte>> pPrintData = new Vector<Vector<Byte>>();
		pPrintData.clear();
		int height = pBitMap.getHeight();
		int width = pBitMap.getWidth();
		
		int nRowsize = (width + 7)/8;//一行所需的字节数 一个字节存储8个像素点
		int size = nRowsize * height;
		byte[] data = new byte[size];
		int[][] gray=new int[height][width]; 
		
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
		
		int SW=160;  
		for(int j = 0; j < height; j++)
		{
			for(int k = 0 ; k < width; k++)
			{
				int index = j * nRowsize + k/8;
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
		
		data = ConverData(data,height,nRowsize);
		
	    for (int nRow = 0; nRow < height; nRow++)
	    {
	    	Vector<Byte> tmp = new Vector<Byte>();
	    	tmp.clear();
	        tmp.add((byte)0x1B);
	        tmp.add((byte)0x56);

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
	    	Vector<Byte> tmpEnd = new Vector<Byte>();
		    tmpEnd.add((byte) 0x0d);
		    tmpEnd.add((byte) 0x0a);
	        pPrintData.add(tmpEnd);                     //打印完毕添加回车换行
	    }
	    
	    PlcSampInfo PlcInfo = new PlcSampInfo();
	    PlcInfo.eConnectType = port;
	    for(int k = 0; k < pPrintData.size(); k++)
	    {
	    	Vector<Byte> bTemp = pPrintData.get(k);
	    	int size1 = bTemp.size();
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
	
	static byte[] ConverData(byte[] praw,int cols,int rows)
	{
	    /*先上下颠倒*/
	    byte[] tmp = new byte[cols * rows];
	    byte cTmp;
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
	    /*同一个字节内高4位和低4位颠倒*/
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
