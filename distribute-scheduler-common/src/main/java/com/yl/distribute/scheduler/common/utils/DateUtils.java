package com.yl.distribute.scheduler.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
	
	public static final String dateTimeStr = "yyyy-MM-dd HH:mm:ss";
	
	private DateUtils() {
		
	}
	
	public static String getDateAsString(Date date,String format) {		
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

}
