package com.psaravan.filebrowserview.lib.db;

import android.content.Context;

import java.io.File;

public class FileBrowser {
	public FileBrowser(Context context) {

	}

	public static FileBrowser getBrowser(Context context) {
		return new FileBrowser(context);
	}

	public void upload(File file) {
	}
}
