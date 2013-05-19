package de.rwth.ti.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
<<<<<<< HEAD
import android.os.Environment;
import de.rwth.ti.wps.MainActivity;
=======
>>>>>>> origin/dev

/**
 * This class is responsible for creating and upgrading the database
 * 
 */
public class Storage extends SQLiteOpenHelper {

	private static final int DB_VERSION = 1;

	public Storage(Context context, String dbName) {
		super(context, dbName, null, DB_VERSION);
	}

	/** Called when the database is created for the first time. */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(Building.TABLE_CREATE);
		db.execSQL(Floor.TABLE_CREATE);
		db.execSQL(MeasurePoint.TABLE_CREATE);
		db.execSQL(Scan.TABLE_CREATE);
		db.execSQL(AccessPoint.TABLE_CREATE);
	}

	/** Called when the database needs to be upgraded */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(AccessPoint.TABLE_DROP);
		db.execSQL(Scan.TABLE_DROP);
		db.execSQL(MeasurePoint.TABLE_DROP);
		db.execSQL(Floor.TABLE_DROP);
		db.execSQL(Building.TABLE_DROP);
		onCreate(db);
	}

<<<<<<< HEAD
	public void exportDatabase(String filename) throws IOException {
		File sd = Environment.getExternalStorageDirectory();
		File data = Environment.getDataDirectory();
		String srcDBPath = "//data//" + MainActivity.PACKAGE_NAME
				+ "//databases//" + DB_NAME;
		// String dstDBPath = "/backup/" + filename;
		String dstDBPath = "/" + filename;
		File srcDB = new File(data, srcDBPath);
		File dstDB = new File(sd, dstDBPath);
		FileChannel src = new FileInputStream(srcDB).getChannel();
		FileChannel dst = new FileOutputStream(dstDB).getChannel();
		dst.transferFrom(src, 0, src.size());
		src.close();
		dst.close();
	}

	public void importDatabase(String filename) throws IOException {
		File sd = Environment.getExternalStorageDirectory();
		File data = Environment.getDataDirectory();
		String dstDBPath = "//data//" + MainActivity.PACKAGE_NAME
				+ "//databases//" + DB_NAME;
		String srcDBPath = "/" + filename;
		File dstDB = new File(data, dstDBPath);
		File srcDB = new File(sd, srcDBPath);
		FileChannel src = new FileInputStream(srcDB).getChannel();
		FileChannel dst = new FileOutputStream(dstDB).getChannel();
		dst.transferFrom(src, 0, src.size());
		src.close();
		dst.close();
	}
=======
>>>>>>> origin/dev
}
