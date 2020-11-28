package com.github.telegramdrive.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LocalSystemDataNode extends DataNode {

	private Executor splitExecutor = Executors.newFixedThreadPool(8);
	private Executor mergeExecutor = Executors.newFixedThreadPool(8);

	@Override
	public DataInfo split(File localFile) {
		long fragmentId = generateFragmentId();
		DataInfo info = new DataInfo(localFile.length());
		System.out.println("文件总体积:" + localFile.length() / 1024 / 1024 + "MB");
		System.out.println("可分" + localFile.length() / 1024 / 1024 / 8 + "份");
		long fileOffset = 0;
		while (fileOffset < localFile.length()) {
			long length = Math.min(FILE_FRAGMENTATION, localFile.length() - fileOffset);
			info.addFragment(new DataInfo.DataFragment(fileOffset, String.valueOf(fragmentId), length));
//			splitExecutor.execute(new WriteRunnable(localFile, fileOffset, length, String.valueOf(fragmentId))
			new WriteRunnable(localFile, fileOffset, length, String.valueOf(fragmentId)).run();
			fragmentId++;
			fileOffset += length;
		}
		return info;
	}

	private long generateFragmentId() {
		File[] files = DATA_ROOT.listFiles();
		if (files == null || files.length == 0) return 0;
		Arrays.sort(files, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				return Long.compare(Long.parseLong(o1.getName()), Long.parseLong(o2.getName()));
			}
		});
		File file = files[files.length - 1];
		return Long.parseLong(file.getName()) + 1;
	}

	private static class WriteRunnable implements Runnable {
		private File srcFile;
		private long srcOffset;
		private long length;
		private String destFilePath;

		WriteRunnable(File srcFile, long srcOffset, long length, String destFilePath) {
			this.srcFile = srcFile;
			this.srcOffset = srcOffset;
			this.length = length;
			this.destFilePath = destFilePath;
		}

		@Override
		public void run() {
			File destFile = new File(DATA_ROOT, destFilePath);
			FileOutputStream fos = null;
			RandomAccessFile fis = null;
			try {
				fos = new FileOutputStream(destFile, true);
				fis = new RandomAccessFile(srcFile, "r");
				byte[] temp;
				int tempLength = 8 * 1024;
				long remain = length;
				while (remain > 0) {
					if (remain < tempLength) {
						temp = new byte[(int) remain];
					} else {
						temp = new byte[tempLength];
					}
					fis.seek((int) srcOffset);
					fis.read(temp);
					fos.write(temp);
					srcOffset += temp.length;
					remain -= temp.length;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private static class ReadRunnable implements Runnable {
		private final File localFile;
		private DataInfo.DataFragment fragment;

		public ReadRunnable(File localFile, DataInfo.DataFragment fragment) {
			this.localFile = localFile;
			this.fragment = fragment;
		}

		@Override
		public void run() {
			File srcFile = new File(DATA_ROOT, String.valueOf(fragment.getName()));
			FileInputStream fis = null;
			RandomAccessFile fos = null;
			try {
				fis = new FileInputStream(srcFile);
				if (!localFile.getParentFile().exists()) {
					localFile.getParentFile().mkdirs();
				}
				if (localFile.exists()) {
					localFile.delete();
				}
				fos = new RandomAccessFile(localFile, "rw");
				long fileOffset = fragment.getFileOffset();
				byte[] temp;
				int tempLength = 8 * 1024;
				long remain = fragment.getLength();
				int offset = 0;
				while (remain > 0) {
					if (remain < tempLength) {
						temp = new byte[(int) remain];
					} else {
						temp = new byte[tempLength];
					}
					fis.read(temp);
					fos.seek((int) fileOffset + offset);
					fos.write(temp);
					offset += temp.length;
					remain -= temp.length;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public void merge(DataInfo dataInfo, File localFile) {
		List<DataInfo.DataFragment> fragments = dataInfo.getFragments();
		try {
			new RandomAccessFile(localFile, "rw").setLength(dataInfo.getFileLength());
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (DataInfo.DataFragment fragment : fragments) {
			new ReadRunnable(localFile, fragment).run();
		}
	}

}
