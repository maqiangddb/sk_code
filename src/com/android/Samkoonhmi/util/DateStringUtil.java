package com.android.Samkoonhmi.util;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.android.Samkoonhmi.skenum.DATE_FORMAT;
import com.android.Samkoonhmi.skenum.TIME_FORMAT;
import com.android.Samkoonhmi.skenum.WEEK_FORMAT;

public class DateStringUtil {
	/**
	 * 转换日期格式
	 * 
	 * @return
	 */
	public static String convertDate(DATE_FORMAT dateType,Date date) {
		SimpleDateFormat simpleDate = null;
		switch (dateType) {
		case YYYYMMDD_SLASH:
			simpleDate = new SimpleDateFormat("yyyy/MM/dd");
			break;
		case YYYYMMDD_ACROSS:
			simpleDate = new SimpleDateFormat("yyyy-MM-dd");
			break;
		case YYYYMMDD_POINT:
			simpleDate = new SimpleDateFormat("yyyy.MM.dd");
			break;
		case MMDDYYYY_SLASH:
			simpleDate = new SimpleDateFormat("MM/dd/yyyy");
			break;
		case MMDDYYYY_POINT:
			simpleDate = new SimpleDateFormat("MM.dd.yyyy");
			break;
		case MMDDYYYY_ACROSS:
			simpleDate = new SimpleDateFormat("MM-dd-yyyy");
			break;
		case DDMMYYYY_SLASH:
			simpleDate = new SimpleDateFormat("dd/MM/yyyy");
			break;
		case DDMMYYYY_POINT:
			simpleDate = new SimpleDateFormat("dd.MM.yyyy");
			break;
		case DDMMYYYY_ACROSS:
			simpleDate = new SimpleDateFormat("dd-MM-yyyy");
			break;
		default:
			simpleDate = new SimpleDateFormat("yyyy/MM/dd");
			break;
		}
		String dateSrt = simpleDate.format(date);
		return dateSrt;
	}

	/**
	 * 转换时间格式
	 * 
	 * @return
	 */
	public static String converTime(TIME_FORMAT timeType,Date date) {
		SimpleDateFormat simpleDate = null;
		switch (timeType) {
		case HHMM_COLON:
			simpleDate = new SimpleDateFormat("HH:mm");
			break;
		case HHMMSS_COLON:
			simpleDate = new SimpleDateFormat("HH:mm:ss");
			break;
		case HHMM_ACROSS:
			simpleDate = new SimpleDateFormat("HH-mm");
			break;
		case HHMMSS_ACROSS:
			simpleDate = new SimpleDateFormat("HH-mm-ss");
			break;
		default:
			simpleDate = new SimpleDateFormat("HH:mm");
			break;
		}
		String time = simpleDate.format(date);
		return time;
	}

	/**
	 * 转换星期格式
	 * 
	 * @return
	 */
	public static String converWeek(WEEK_FORMAT weekType,Date date) {
		SimpleDateFormat simpleDate = null;
		switch (weekType) {
		case CHINAFORMAT:
			simpleDate = new SimpleDateFormat("EEE", Locale.CHINA);
			break;
		case ENGLISHFORMAT:
			simpleDate = new SimpleDateFormat("EEE", Locale.ENGLISH);
			break;
		default:
			simpleDate = new SimpleDateFormat("EEE", Locale.CHINA);
			break;
		}
		String time = simpleDate.format(date);
		return time;
	}

	public static String getDateString(DATE_FORMAT Date_Format, int year,int month,int day) {
		String str = "";
/*
		StringBuilder result = new StringBuilder();
        // Month is 0 based so add 1  ?
		
		switch (Date_Format) {
		case YYYYMMDD_SLASH:  //  YYYY/MM/DD,
			str =  result
              .append(year).append("/")
              .append(month+1).append("/")
              .append(day).toString()
             ;
			break;
		case YYYYMMDD_POINT:  //YYYY.MM.DD,
			str =  result
              .append(year).append(".")
              .append(month+1).append(".")
              .append(day).toString()
             ;
			break;
		case YYYYMMDD_ACROSS:  //	YYYY-MM-DD,
			str =  result
              .append(year).append("-")
              .append(month+1).append("-")
              .append(day).toString()
             ;
			break;
		case MMDDYYYY_SLASH:    //	MM/DD/YYYY,
			str =  result
              .append(month+1).append("/")
              .append(day).append("/")
              .append(year)
              .toString()
             ;
			break;
		case MMDDYYYY_POINT:    //	MM.DD.YYYY,
			str =  result
              .append(month+1).append(".")
              .append(day).append(".")
              .append(year)
              .toString()
             ;
			break;
		case MMDDYYYY_ACROSS:   //	MM-DD-YYYY,
			str =  result
              .append(month+1).append("-")
              .append(day).append("-")
              .append(year)
              .toString()
             ;
			break;
		case DDMMYYYY_SLASH:   //	DD/MM/YYYY,
			str =  result
		       .append(day).append("/")
		       .append(month+1).append("/")
              .append(year)
              .toString()
             ;
			break;
		case DDMMYYYY_POINT:   //	DD.MM.YYYY,
			str =  result
		      .append(day).append(".")
		      .append(month+1).append(".")
              .append(year)
              .toString()
             ;
			break;
		case DDMMYYYY_ACROSS:   //	DD-MM-YYYY
			str =  result
		      .append(day).append("-")
		      .append(month+1).append("-")
              .append(year)
              .toString()
             ;
			break;	

		default:
			break;
		}
*/
		Date date=new Date();
		date.setDate(day);
		date.setMonth(month);
		date.setYear(year-1900);
		str=convertDate(Date_Format,date);
		return str;
	}

	public static String getTimeString(TIME_FORMAT Time_Format, int hour,int minute,int second) {
		String str = "";
/*
		StringBuilder result = new StringBuilder();
        // Month is 0 based so add 1
		switch (Time_Format) {
		case HHMM_COLON:   //   HH:MM,
			str =  result
		      .append(hour).append(":")
		      .append(minute)
		      .toString()
		      ;
			break;	
		case HHMMSS_COLON:  //	HH:MM:SS
			str =  result
		      .append(hour).append(":")
		      .append(minute).append(":")
		      .append(second)
		      .toString()
		      ;
			break;	
		case HHMM_ACROSS:  //	HH-MM 
			str =  result
		      .append(hour).append("-")
		      .append(minute)
		      .toString()
		      ;
			break;	
		case HHMMSS_ACROSS:   //	HH-MM-SS
			str =  result
		      .append(hour).append("-")
		      .append(minute).append("-")
		      .append(second)
		      .toString()
		      ;
			break;				
			default:
				break;
		}
*/
		Date date=new Date();
		date.setHours(hour);
		date.setMinutes(minute);
		date.setSeconds(second);
		str=converTime(Time_Format,date);		
		return str;
	}
}
