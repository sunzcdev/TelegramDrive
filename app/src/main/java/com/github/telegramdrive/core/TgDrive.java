package com.github.telegramdrive.core;

import java.io.File;

public class TgDrive {


	private DataNode mDataNode = new LocalSystemDataNode();
	private NameNode mNameNode = new NameNode();

	public void upload(String localFilePath, String tdPath) {
		File localFile = new File(localFilePath);
		DataInfo dataInfo = mDataNode.split(localFile);
		mNameNode.createFile(tdPath, dataInfo);
	}

	public void download(String tdPath, String localFile) {
		DataInfo dataInfo = mNameNode.readFile(tdPath);
		mDataNode.merge(dataInfo, new File(localFile));
	}
}
