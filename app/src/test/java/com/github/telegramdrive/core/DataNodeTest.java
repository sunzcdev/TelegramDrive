package com.github.telegramdrive.core;

import com.github.drive.Callback;
import com.github.drive.DataInfo;
import com.github.drive.DataNode;
import com.github.drive.LocalSystemDataNode;
import com.google.gson.Gson;

import org.junit.Test;

import java.io.File;

public class DataNodeTest {

	@Test
	public void split() {
		DataNode node = new LocalSystemDataNode();
		node.split("localfile", new File("./localfile.txt"), new Callback<DataInfo, Void>() {
			@Override
			public Void call(DataInfo dataInfo) {
				System.out.println(new Gson().toJson(dataInfo));
				return null;
			}
		});

	}

	@Test
	public void merge() {
		DataNode node = new LocalSystemDataNode();
		node.split("localfile", new File("./localfile.txt"), new Callback<DataInfo, Void>() {
			@Override
			public Void call(DataInfo dataInfo) {
				System.out.println(new Gson().toJson(dataInfo));
				return null;
			}
		});

	}
}