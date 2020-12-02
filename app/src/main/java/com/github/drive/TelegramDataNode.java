package com.github.drive;

import java.io.File;
import java.util.List;

public class TelegramDataNode extends LocalSystemDataNode {
	private TelegramClient client;

	public TelegramDataNode(TelegramClient client) {
		this.client = client;
	}

	@Override
	public void split(final String hashTag, final File localFile, final Callback<DataInfo, Void> callback) {
		super.split(hashTag, localFile, new Callback<DataInfo, Void>() {
			@Override
			public Void call(final DataInfo dataInfo) {
				client.upload(hashTag, dataInfo.getFragments(), new Callback<List<DataInfo.DataFragment>, Void>() {
					@Override
					public Void call(List<DataInfo.DataFragment> fragments) {
						dataInfo.setFragments(fragments);
						callback.call(dataInfo);
						return null;
					}
				});
				return null;
			}
		});
		// 将整个文件切分成碎片

		// 给每个碎片打个所属标签，上传每个碎片
		// 记录每个碎片的id，及在文件中的位置
	}

	@Override
	public void merge(final DataInfo dataInfo, final File localFile) {
		// 根据tag标签下载所需要的所有文件碎片
		// 从这些碎片列表中，根据每个碎片id及碎片在整个文件中的位置来归位每个碎片
		client.download(dataInfo.getHashTag(), dataInfo.getFragments(), new Callback<List<DataInfo.DataFragment>, Void>() {
			@Override
			public Void call(List<DataInfo.DataFragment> fragments) {
				dataInfo.setFragments(fragments);
				TelegramDataNode.super.merge(dataInfo, localFile);
				return null;
			}
		});
	}
}
