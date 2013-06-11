package de.rwth.ti.common;

import java.io.File;
import java.util.Locale;

import android.os.Environment;

public class Constants {

	public static final String SD_APP_DIR = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ File.separator
			+ "WPS";
	public static final String LOCAL_DB_NAME = "local.sqlite";
	public static final Locale LOCALE = Locale.GERMAN;
	public static final int FILE_BUFFER_SIZE = 10240;

}
