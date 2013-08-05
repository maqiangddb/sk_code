/**
 * 李镇,2012/06/19,这个是曲线的计算
 */


package com.android.Samkoonhmi.util;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

import com.android.Samkoonhmi.graphicsdrawframe.FreeLineItem;
import com.android.Samkoonhmi.model.sk_historytrends.ChannelGroupInfo;
import com.android.Samkoonhmi.model.sk_historytrends.HistoryTrendsInfo;
import com.android.Samkoonhmi.skenum.CURVE_TYPE;
import com.android.Samkoonhmi.skenum.DATE_FORMAT;
import com.android.Samkoonhmi.skenum.END_ARROW_TYPE;
import com.android.Samkoonhmi.skenum.TIMERANGE_TYPE;
import com.android.Samkoonhmi.skenum.TIME_FORMAT;
import com.android.Samkoonhmi.skglobalcmn.DataCollect;
import com.android.Samkoonhmi.util.DateStringUtil;





public class SKTrendsCal{
	private static final String TAG="SKTrendsCal";
	private static HistoryTrendsInfo mTrendsInfo;

	private  List<FreeLineItem> freeLineItemGroups;
	private FreeLineItem[] freeLineItemList;
	private  List<ChannelGroupInfo> channelGroups;
	private float	Real_Scale;//实时曲线采样总数跟实际显示的比例值
	private  int Real_Index;	//实时采样索引	
	private  int Real_DataSample;  //实际采样数
	private  int Real_ScrollSample;  //实际滚动数
	private float	Scroll_Scale;//实际滚动与实际采样的比例值
	private float	Sample_Scale;//样本采样跟实际显示的比例值
	private  boolean Sample_Full_Flag;	//采样满标志
	private  int Sample_Index;	//采样索引
	private int point_x,point_y; //采集的点转换的坐标值
	private int	nOldCollectRate;
	private int total_second;
	private int total_dot;
	private	short nCurveX;	//控件曲线区域左上角X坐标	 
	private short nCurveY;	//控件曲线区域左上角Y坐标	 
	private short nCurveWd;	//控件曲线区域宽度	 
	private short nCurveHt;	//控件曲线区域高度	 
	Vector<Vector<String > > historydata;
	private double current_hisdata; //
	HistoryCollectProp Collect_Value;
	Vector<Integer> nChannelList;
	private  boolean RealTimeRefresh_Flag;	//实时曲线刷新标志
	private  boolean TimeRange_Flag;	//是否获得开始时间
	
   public SKTrendsCal(HistoryTrendsInfo mInfo) {
   	mTrendsInfo=mInfo;
   	channelGroups=mTrendsInfo.getchannelGroups();
   	CalCurve();
   	init();
  }
  
  private void CalCurve()
	{
		nCurveX=(short) (mTrendsInfo.getnLp()+mTrendsInfo.getnCurveX());
		nCurveY=(short) (mTrendsInfo.getnTp()+mTrendsInfo.getnCurveY());
		nCurveWd=(short) (mTrendsInfo.getnCurveWd());
		nCurveHt=(short) (mTrendsInfo.getnCurveHt());
	}
  
  private void init(){  //曲线的初始化
	  nChannelList=new Vector<Integer>();
	  Collect_Value=new HistoryCollectProp();
	  freeLineItemGroups=new ArrayList<FreeLineItem>();
	  freeLineItemList=new FreeLineItem[mTrendsInfo.getnChannelNum()];
	  if(mTrendsInfo.getnDataSample()<nCurveWd)
	  {
		  Real_DataSample=mTrendsInfo.getnDataSample();
		  Real_ScrollSample=mTrendsInfo.getnScrollSample();
	  }
	  else
	  {
		  Real_DataSample=nCurveWd;
		  Real_ScrollSample=(short) (mTrendsInfo.getnScrollSample()*nCurveWd/mTrendsInfo.getnDataSample());
	  }
	  Scroll_Scale=(float)Real_ScrollSample/Real_DataSample;
	  Sample_Scale=(float)nCurveWd/Real_DataSample;
	//  Log.d(TAG, "SKTrendsCal mTrendsInfo.getnChannelNum()"+mTrendsInfo.getnChannelNum());	
	  for(short i=0;i<mTrendsInfo.getnChannelNum();i++)
	  {
		  Vector<PointF> m_pointList=new Vector<PointF>();
/*
		  for(short k = 0; k < Real_DataSample; k++)
		  {
			  Point start= new Point();
			  start.set((int) (nCurveX+k*Sample_Scale),nCurveY+nCurveHt);
			  m_pointList.add(start);				
			}
*/
			freeLineItemList[i]=new FreeLineItem(m_pointList,END_ARROW_TYPE.STYLE_NONE);
			freeLineItemGroups.add(freeLineItemList[i]);
		}
		Sample_Full_Flag=false;
		Sample_Index=0;//Real_DataSample-1;
		if (mTrendsInfo.getnCurveType()==CURVE_TYPE.REALTIME_CURVE)  //默认单位是秒
			total_second=mTrendsInfo.getnRecentMinute()*60;
		nOldCollectRate=0;
  	Real_Index=0;
		Real_Scale=0;
  }


  private void MoveWholeRealCurve()
  { 
	  FreeLineItem freeLineItem;
	  Vector<Point> m_pointList;
	  int newx,newy;
	
	  Log.d(TAG, "SKTrendsCal mTrendsInfo.getnChannelNum()"+mTrendsInfo.getnChannelNum());	
	  for(short j=0;j<mTrendsInfo.getnChannelNum();j++)
	  {
		  freeLineItem=freeLineItemGroups.get(j); //第几通道的曲线
		  m_pointList=freeLineItem.getM_pointList(); 
		  if(Sample_Full_Flag==true)
		  {
	//			System.out.println("m_pointList.size() "+m_pointList.size());

			  for(int i=0;i<Real_DataSample-Real_ScrollSample;i++)
			  {
					newy=m_pointList.get(i+Real_ScrollSample).y;
					newx=(int) (nCurveX+i*Sample_Scale);
					m_pointList.get(i).set(newx,newy);					
			  }	
				for(int i=0;i<Real_ScrollSample;i++)
					m_pointList.remove(Real_DataSample-1-i);
		  }
  	}
   	if(Sample_Full_Flag==true)
   	{
   		Sample_Index=Real_DataSample-Real_ScrollSample;
   		Real_Index=(int)(Sample_Index*Real_Scale); //重新定位数据
  // 		System.out.println("Real_Index "+Real_Index);
   		Sample_Full_Flag=false;
   	}
  }
  
  private void AddSample_Index()
  {
 		Sample_Index++;
 		if(Sample_Index==Real_DataSample)//-1)
 		{
 			Sample_Full_Flag=true;  	
  //  	Real_Index=(int)(Sample_Index*Real_Scale);// (total_dot*(1-Scroll_Scale));   //重新定位数据
 // 		System.out.println("Sample_Full_Flag "+Sample_Full_Flag);
  	}
  }
  private void MoveRealTimeCurve(short index_channel)  //实时曲线，点的移动
  {
		FreeLineItem freeLineItem;
		Vector<Point> m_pointList;
		Point start= new Point();
				  	
		freeLineItem=freeLineItemGroups.get(index_channel); //第几通道的曲线
		m_pointList=freeLineItem.getM_pointList();
		point_x=(int) (nCurveX+Sample_Index*Sample_Scale);
		start.set(point_x,point_y);
		m_pointList.add(start);
  }
 
   public List<FreeLineItem> GetfreeLineItemGroups()
  {
	  return freeLineItemGroups;
  }
 
	public boolean GetSample_Full_Flag()
	{
		return Sample_Full_Flag;
	}
	public float GetScroll_Scale()
	{
		return Scroll_Scale;
	}
	public float GetReal_Scale()
	{
		return Real_Scale;
	}
	public boolean GetRealTimeRefresh_Flag()
	{
		return RealTimeRefresh_Flag;
	}
	
	public boolean GetTimeRange_Flag()
	{
		return TimeRange_Flag;
	}
 /**
	 * 线程回调处理函数，计算实时曲线曲线变化值 
	 * */
	public  void HandleRealCurveData(Vector<String> nValueList,int nRealCollectRate){
	//	newDataCollect.getGroupCollectData(mZoomIn, mZoomIn, sTaskName);
		int channel,nLen; //= nValueList.size();
		String value;
		double convet_value=0;
		int error_num;

//		System.err.println("nValueList"+nValueList); 
		if(null == nValueList || nValueList.isEmpty()) return;
		nLen = nValueList.size();
		if(nOldCollectRate!=nRealCollectRate)
		{
			nOldCollectRate=nRealCollectRate;
			total_dot=5*total_second/nRealCollectRate;
	//		System.out.println("nOldCollectRate "+nOldCollectRate+"total_dot"+total_dot);
	 	 	if(total_dot<Real_DataSample)
	 	 	{
	 	 		Real_DataSample=total_dot;
	 	 		Real_ScrollSample=mTrendsInfo.getnScrollSample()*total_dot/mTrendsInfo.getnDataSample();
	 	 		Scroll_Scale=(float)Real_ScrollSample/Real_DataSample;
	 	 		Sample_Scale=(float)nCurveWd/Real_DataSample;		
			}
			Real_Scale=(float)total_dot/Real_DataSample;	
			System.out.println("nOldCollectRate "+nOldCollectRate+"total_dot"+total_dot+"Real_Scale "+Real_Scale);
		}
		if(Real_Scale>1.0)
		{
			Real_Index++;
			RealTimeRefresh_Flag=false;
//			System.out.println("Real_Index "+Real_Index+"Sample_Index"+Sample_Index+"Real_Scale "+Real_Scale);
			if(Real_Index<Sample_Index*Real_Scale)
				return;
		}
		RealTimeRefresh_Flag=true;
		MoveWholeRealCurve();
		for(short i=0;i<mTrendsInfo.getnChannelNum();i++)
		{
			channel=channelGroups.get(i).getnNumOfChannel();
			value=nValueList.get(channel);
			try
			{
				convet_value=Double.valueOf(value);
				error_num=0;
			}
			catch(NumberFormatException Event)
			{
				System.err.println("convet_value error");  
				error_num=1;
			}
			if(error_num==1)
				point_y=(int) (nCurveY+nCurveHt);
			else
				ConvertDataToPoint(convet_value);
			MoveRealTimeCurve(i);
		}
		AddSample_Index();
} 
 
	public  void HandleDataGroup(Vector<Vector<String>> groupdata){
	
		FreeLineItem freeLineItem;
		Vector<Point> m_pointList;
		Vector<String >  nValueList;
		String value;
		int nLen;// = nValueList.size();
		int newx,newy;
		double convet_value=0;
		int error_num;
/*
		Integer[] nTmpBytes = new Integer[nLen];
		for(int i = 0; i < nLen; i++)
		{
			nTmpBytes[i] = nValueList.get(i);
		}
	*/
	//	System.err.println("groupdata.size() "+groupdata.size());
		if(null == groupdata || groupdata.isEmpty()) return;

		for( short j=0;j<groupdata.size();j++)
		{
			nValueList=groupdata.get(j);
	//		System.err.println("nValueList"+nValueList); 
			if(null == nValueList || nValueList.isEmpty())
				continue;
			nLen=nValueList.size();
			if(nLen>Real_DataSample)
				nLen=Real_DataSample;
			freeLineItem=freeLineItemGroups.get(j); //第几通道的曲线
			m_pointList=freeLineItem.getM_pointList();
			m_pointList.clear();
	//		System.err.println("nLen   "+nLen);  

			for(short i=0;i<nLen;i++)
			{
				Point start= new Point();
				value=nValueList.get(i);
				if(value==null)
					continue;
				try
				{
					convet_value=Double.valueOf(value);
					error_num=0;
				}
				catch(NumberFormatException Event)
				{
					System.err.println("convet_value error");  
					error_num=1;
				}
				if(error_num==1)
					point_y=(int) (nCurveY+nCurveHt);
				else
					ConvertDataToPoint(convet_value);
				newy=point_y;
				newx=(int) (nCurveX+i*Sample_Scale);
		//			System.out.println("newx "+newx+"newy"+newy);
		//		m_pointList.get(i).set(newx,newy);					
				start.set(newx,newy);
			 	m_pointList.add(start);
			}
		}

} 


//	public void HandleHistoryData(short channel_num,TrendCalendar start_list,TrendCalendar end_list, ZoomAttr current_Zoom,boolean read_dataflag) //计算历史曲线数据
	public void HandleHistoryData(TrendCalendar start_list,TrendCalendar end_list) //计算历史曲线数据
	{
		Calendar start_date=start_list.C_Date;
		Calendar end_date=end_list.C_Date;		
		String oldest_date=DateStringUtil.getDateString(DATE_FORMAT.YYYYMMDD_ACROSS, start_date.get(Calendar.YEAR), start_date.get(Calendar.MONTH), start_date.get(Calendar.DAY_OF_MONTH));
		String oldest_time=DateStringUtil.getTimeString(TIME_FORMAT.HHMMSS_COLON,  start_date.get(Calendar.HOUR_OF_DAY),  start_date.get(Calendar.MINUTE),  0);
		String oldest_null=" ";
		String oldest_s=oldest_date+oldest_null+oldest_time;
			
		String recent_date=DateStringUtil.getDateString(DATE_FORMAT.YYYYMMDD_ACROSS, end_date.get(Calendar.YEAR), end_date.get(Calendar.MONTH), end_date.get(Calendar.DAY_OF_MONTH));
		String recent_time=DateStringUtil.getTimeString(TIME_FORMAT.HHMMSS_COLON,  end_date.get(Calendar.HOUR_OF_DAY),   end_date.get(Calendar.MINUTE),  0);
		String recent_null=" ";
		String recent_s=recent_date+recent_null+recent_time;
		
		int channel;
		
		TimeRange_Flag=false;
		nChannelList.clear();
		for( short i=0;i<mTrendsInfo.getnChannelNum();i++)
		{
			channel=channelGroups.get(i).getnNumOfChannel();
//			System.out.println("channel "+channel);
			nChannelList.add(channel);
		}
//		System.out.println("recent_s"+recent_s);
//		System.out.println("oldest_s"+oldest_s);

		if(mTrendsInfo.getnTimeRange().equals(TIMERANGE_TYPE.STORE_BEGIN))//TIMERANGE_TYPE.RECENT_BEGIN)
		{
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	
				Date date=new Date();
				historydata=DataCollect.getInstance().getGroupCollectData(mTrendsInfo.getnGroupNum(), Real_DataSample,nChannelList, Collect_Value);
				if(Collect_Value.sFirstTimeStr==null)
					return;
				try {
					date=format.parse(Collect_Value.sFirstTimeStr);
		//			System.out.println("Collect_Value.sFirstTimeStr "+Collect_Value.sFirstTimeStr);
					start_date.set(Calendar.DAY_OF_MONTH,date.getDate());
					start_date.set(Calendar.MONTH,date.getMonth());
					start_date.set(Calendar.YEAR,date.getYear()+1900);
					start_date.set(Calendar.HOUR_OF_DAY,date.getHours());
					start_date.set(Calendar.MINUTE, date.getMinutes());
					start_list.C_Date=start_date;
					TimeRange_Flag=true;
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					System.out.println("can not get start time");
					e.printStackTrace();
				}
		}
		else
				historydata=DataCollect.getInstance().getGroupCollectDataByTime(mTrendsInfo.getnGroupNum(), Real_DataSample,nChannelList, oldest_s, recent_s, Collect_Value);

		for(short i=0;i<mTrendsInfo.getnChannelNum();i++)
		{
			ConvertHistoryDataToPoint(i);
		}
	}
	
	private void ConvertDataToPoint(Double double1){   //将点的值转换成实际坐标
			double convertdata;	 //实际在曲线中的点
			double Ylen;	 
			float showMax = mTrendsInfo.getnDisplayMax(); // 显示最大值
			float showMin = mTrendsInfo.getnDisplayMin(); // 显示最小值

			Ylen=showMax-showMin; //Y轴长度
			convertdata=double1;

			if(convertdata>showMax)
				convertdata=showMax;
			if(convertdata<showMin)
				convertdata=showMin;
//			System.out.println("mTrendsInfo.getnCurveType() "+mTrendsInfo.getnCurveType()+"showMax"+showMax+"showMin"+showMin);				
			point_y=(int) (-(convertdata-showMin)/Ylen*nCurveHt+(nCurveY+nCurveHt));
		}
	

//	private void ConvertHistoryDataToPoint(short channelnum, ZoomAttr current_Zoom){	
	private void ConvertHistoryDataToPoint(short channelnum){	
			FreeLineItem freeLineItem;
			freeLineItem=freeLineItemGroups.get(channelnum);  //第几通道的曲线
			Vector<Point> mPoint=freeLineItem.getM_pointList(); //曲线的点
			int	x_aix,size;
			int error_num;
			Vector<String >  nValueList;
			String value;
		
	//		System.err.println("historydata"+historydata);  			
			mPoint.clear();
			if(null == historydata || historydata.isEmpty()) return;

			size=historydata.size();
			if(size==0)
				return;
	//		System.err.println("size"+size);  
			for(short i=0;i<size;i++)
			{
				Point start= new Point();
				nValueList=historydata.get(i);
				if(null == nValueList || nValueList.isEmpty()) 
					continue;
				value=nValueList.get(channelnum);
				if(value==null)
					continue;
				try
				{
					current_hisdata=Double.valueOf(value);
					error_num=0;
				}
				catch(NumberFormatException Event)
				{
					System.err.println("convet_value error");  
					error_num=1;
				}
				if(error_num==1)
					point_y=(int) (nCurveY+nCurveHt);
				else
					ConvertDataToPoint(current_hisdata);	
				x_aix=(int) (nCurveX+i*Sample_Scale); //x轴坐标
				start.set(x_aix,point_y);
			 	mPoint.add(start);
			}
	}

}

