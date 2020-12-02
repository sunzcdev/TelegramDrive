package com.github.drive;

import java.util.List;

public class TelegramClient {

	public void upload(String hashTag, List<DataInfo.DataFragment> fragments, Callback<List<DataInfo.DataFragment>, Void> callback) {
		System.out.println("开始上传");
		for (DataInfo.DataFragment fragment : fragments) {
			String s = "_" + hashTag + "_" + fragment.getName();
			System.out.println("上传：" + s);
			fragment.setName(s);
		}
		System.out.println("上传完成");
		callback.call(fragments);
	}

	public void download(String hashTag, List<DataInfo.DataFragment> fragments, Callback<List<DataInfo.DataFragment>, Void> callback) {
		System.out.println("开始搜索:_" + hashTag);
		System.out.println("搜索到" + fragments.size() + "个文件");
		System.out.println("开始下载……");
		for (DataInfo.DataFragment fragment : fragments) {
			fragment.setName(fragment.getName().split("_")[2]);
			System.out.println("下载：" + fragment.getName());
		}
		System.out.println("下载完成");
		callback.call(fragments);
	}
}
