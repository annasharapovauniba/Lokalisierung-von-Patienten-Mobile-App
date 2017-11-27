package com.michaelfotiadis.ibeaconscanner.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeFormatter {
	private final static String ISO_FORMAT = "yyyy-MM-dd' 'HH:mm:ss";
	//private final static SimpleDateFormat ISO_FORMATTER = new UtcDateFormatter(ISO_FORMAT, Locale.US);


	//Date date = new Date();
	//SimpleDateFormat dateFormatCN = new SimpleDateFormat("dd-MMM-yyyy", Locale.GERMANY);
	private final static SimpleDateFormat ISO_FORMATTER = new SimpleDateFormat(ISO_FORMAT, Locale.GERMANY);






	public static String getIsoDateTime(Date date){
		return ISO_FORMATTER.format(date);
	}

	public static String getIsoDateTime(long millis){
		return getIsoDateTime(new Date(millis));
	}
}
