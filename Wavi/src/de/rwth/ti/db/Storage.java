package de.rwth.ti.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

/**
 * This class is responsible for creating and upgrading the database
 * 
 */
public class Storage extends SQLiteOpenHelper {

	private final static String DB_NAME = "local";
	private static final int DB_VERSION = 1;

	public Storage(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	/** Called when the database is created for the first time. */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(Building.TABLE_CREATE);
		db.execSQL(Map.TABLE_CREATE);
		db.execSQL(MeasurePoint.TABLE_CREATE);
		db.execSQL(Scan.TABLE_CREATE);
		db.execSQL(AccessPoint.TABLE_CREATE);
	}

	/** Called when the database needs to be upgraded */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// FIXME all data will be lost
		db.execSQL(AccessPoint.TABLE_DROP);
		db.execSQL(Scan.TABLE_DROP);
		db.execSQL(MeasurePoint.TABLE_DROP);
		db.execSQL(Map.TABLE_DROP);
		db.execSQL(Building.TABLE_DROP);
		onCreate(db);
	}

	public void exportDatabase(String filename) throws IOException {
		File sd = Environment.getExternalStorageDirectory();
		File data = Environment.getDataDirectory();
		String currentDBPath = "//data//" + "de.rwth.ti" + "//databases//"
				+ DB_NAME;
		// String backupDBPath = "/backup/" + filename;
		String backupDBPath = "/" + filename;
		File currentDB = new File(data, currentDBPath);
		File backupDB = new File(sd, backupDBPath);

		FileChannel src = new FileInputStream(currentDB).getChannel();
		FileChannel dst = new FileOutputStream(backupDB).getChannel();
		dst.transferFrom(src, 0, src.size());
		src.close();
		dst.close();
	}

}
