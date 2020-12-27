package com.psaravan.filebrowserview.lib.db;

import android.content.Context;

import java.io.File;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {DriveFileEntity.class}, version = 2, exportSchema = false)
@TypeConverters({ConversionFactory.class})
public abstract class DriveDBManager extends RoomDatabase {

	public static DriveDBManager getDefault(Context context, File file) {
		if (file != null && file.exists()) {
			return buildDBByFile(context, file);
		}
		return buildDb(context);
	}

	private static DriveDBManager buildDBByFile(Context context, File file) {
		return Room.databaseBuilder(context.getApplicationContext(), DriveDBManager.class, "file_browser.db")
				.createFromFile(file)
				.allowMainThreadQueries()
				.build();
	}

	private static DriveDBManager buildDb(Context context) {
		return Room.databaseBuilder(context.getApplicationContext(), DriveDBManager.class, new File(context.getExternalFilesDir("db"), "file_browser.db").getAbsolutePath())
				.allowMainThreadQueries()
				.build();
	}

	public abstract FileBrowserDao getFileBrowserDao();
}
