package com.widget.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public final class AppSettings {

    private static final String DATE_FORMAT = "dd/MM/yyyy";

    private static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm";

    public static final SimpleDateFormat FORMATTER = new SimpleDateFormat(DATE_FORMAT, Locale.US);

    public static final SimpleDateFormat DT_FORMATTER = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.US);
}
