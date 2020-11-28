package com.github.telegramdrive.core;

import java.io.File;

public abstract class DataNode {
	public static final File DATA_ROOT = new File("./data");
	public static final long FILE_FRAGMENTATION = 8 * 8 * 1024 * 1024;

	static {
		if (!DATA_ROOT.exists()) {
			DATA_ROOT.mkdirs();
		}
	}

	public abstract void split(String hashTag, File localFile, Callback<DataInfo, Void> callback);

	public abstract void merge(DataInfo dataInfo, File localFile);

}
