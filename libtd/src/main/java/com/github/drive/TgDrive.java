package com.github.drive;

import java.io.File;

public class TgDrive {


	private DataNode mDataNode = new TelegramDataNode(new TelegramDrive());
	private NameNode mNameNode = new NameNode();

	public void upload(String localFilePath, final String tdPath) {
		File localFile = new File(localFilePath);
		mDataNode.split("" + System.currentTimeMillis(), localFile, new Callback<DataInfo, Void>() {
			@Override
			public Void call(DataInfo dataInfo) {
				mNameNode.createFile(tdPath, dataInfo);
				return null;
			}
		});

	}

	public void download(String tdPath, String localFile) {
		DataInfo dataInfo = mNameNode.readFile(tdPath);
		mDataNode.merge(dataInfo, new File(localFile));
	}
}
