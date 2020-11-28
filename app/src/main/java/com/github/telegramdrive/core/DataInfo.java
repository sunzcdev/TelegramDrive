package com.github.telegramdrive.core;

import java.util.ArrayList;
import java.util.List;

public class DataInfo {
	private List<DataFragment> fragments = new ArrayList<>();
	private long timeStamp;
	private long fileLength;

	DataInfo(long fileLength) {
		this.fileLength = fileLength;
		this.timeStamp = System.currentTimeMillis();
	}

	public void addFragment(DataFragment dataFragment) {
		fragments.add(dataFragment);
	}

	public List<DataFragment> getFragments() {
		return fragments;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public long getFileLength() {
		return fileLength;
	}

	public static class DataFragment {
		private final String name;
		private final long length;
		private final long fileOffset;

		public DataFragment(long fileOffset, String name, long length) {
			this.fileOffset = fileOffset;
			this.name = name;
			this.length = length;
		}

		public long getLength() {
			return length;
		}

		public String getName() {
			return name;
		}

		public long getFileOffset() {
			return fileOffset;
		}
	}

}
