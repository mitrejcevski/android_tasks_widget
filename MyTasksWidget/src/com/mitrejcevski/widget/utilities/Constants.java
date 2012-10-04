package com.mitrejcevski.widget.utilities;

import java.text.SimpleDateFormat;

public class Constants {

	private static final String DATE_FORMAT = "dd/MM/yyyy";
	private static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm";

	public static final SimpleDateFormat FORMATTER = new SimpleDateFormat(
			DATE_FORMAT);
	public static final SimpleDateFormat DT_FORMATTER = new SimpleDateFormat(
			DATE_TIME_FORMAT);
}
