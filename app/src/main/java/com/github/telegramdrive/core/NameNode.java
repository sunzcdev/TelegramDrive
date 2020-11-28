package com.github.telegramdrive.core;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class NameNode {
	public static final File DIR_ROOT = new File("./dir");

	static {
		if (!DIR_ROOT.exists()) {
			DIR_ROOT.mkdirs();
		}
	}

	public void createFile(String tdPath, DataInfo dataInfo) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File(DIR_ROOT, tdPath));
			writer.write(new Gson().toJson(dataInfo));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public DataInfo readFile(String tdPath) {
		File tdFile = new File(DIR_ROOT, tdPath);
		try {
			return new Gson().fromJson(new FileReader(tdFile), DataInfo.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
