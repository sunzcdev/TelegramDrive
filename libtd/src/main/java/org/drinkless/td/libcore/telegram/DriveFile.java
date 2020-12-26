package org.drinkless.td.libcore.telegram;


import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DriveFile {
	private TdApi.Message message;
	private long id;
	private String parentDir = "";
	private String currentDir = "";
	private int currentDirNo = -1;
	private boolean isFile;
	private String name;
	private long length;
	public static final String ROOT = "root";
	public static final String PATH_SEPARATOR = "/";
	public static final String CAPTION_SEPARATOR = " ";
	public static final String DIRECTORY_TAG = "d", FILE_TAG = "f";
	public static final String NO_SEPARATOR = "_";
	public static final String TAG_SEPARATOR = "#";

	/**
	 * root/软件 #插件_2 #f  文档.zip
	 * root/软件/开发 #java_3 #d idea
	 * parentDir currentDir isFile name
	 */
	public DriveFile() {
		name = ROOT;
	}

	public DriveFile(TdApi.Message message) {
		this.message = message;
		this.id = message.id;
		if (message.content instanceof TdApi.MessageDocument) {
			this.isFile = true;
			TdApi.MessageDocument document = (TdApi.MessageDocument) message.content;
			String caption = document.caption.text;
			this.length = document.document.document.size;
			parseCaption(caption);
		} else if (message.content instanceof TdApi.MessageText) {
			this.isFile = false;
			TdApi.MessageText text = (TdApi.MessageText) message.content;
			this.length = 0;
			parseCaption(text.text.text);
		} else if (message.content instanceof TdApi.MessageAnimation) {
			this.isFile = true;
			TdApi.MessageAnimation document = (TdApi.MessageAnimation) message.content;
			String caption = document.caption.text;
			this.length = document.animation.animation.size;
			parseCaption(caption);
		} else if (message.content instanceof TdApi.MessageAudio) {
			this.isFile = true;
			TdApi.MessageAudio document = (TdApi.MessageAudio) message.content;
			String caption = document.caption.text;
			this.length = document.audio.audio.size;
			parseCaption(caption);
		} else if (message.content instanceof TdApi.MessagePhoto) {
			this.isFile = true;
			TdApi.MessagePhoto document = (TdApi.MessagePhoto) message.content;
			String caption = document.caption.text;
			this.length = document.photo.sizes[0].photo.size;
			parseCaption(caption);
		} else if (message.content instanceof TdApi.MessageVideo) {
			this.isFile = true;
			TdApi.MessageVideo document = (TdApi.MessageVideo) message.content;
			String caption = document.caption.text;
			this.length = document.video.video.size;
			parseCaption(caption);
		}
	}

	public DriveFile(String caption) {
		parseCaption(caption);
	}

	public DriveFile(File localFile) {
		isFile = true;
		this.length = localFile.length();
		this.parentDir = "";
		this.currentDir = ROOT;
		this.name = localFile.getName();
	}

	private void parseCaption(String caption) {
		String[] capArr = splitCaption(caption);

		this.name = capArr[capArr.length - 1];
		this.isFile = capArr[capArr.length - 2].contains(FILE_TAG);
		if (capArr.length >= 3) {
			//_0
			String s = capArr[capArr.length - 3];
			int index = s.lastIndexOf(NO_SEPARATOR);
			this.currentDir = s.substring(1, index);
			this.currentDirNo = getDirNo(s);
		}
		if (capArr.length >= 4) {
			this.parentDir = capArr[capArr.length - 4].replace(TAG_SEPARATOR, "");
		}
	}

	private String[] splitCaption(String caption) {
		String[] arr = caption.split(CAPTION_SEPARATOR);
		List<String> newList = new ArrayList<>();
		for (String s : arr) {
			if (!TextUtils.isEmpty(s)) {
				newList.add(s);
			}
		}
		return newList.toArray(new String[]{});
	}

	private static int getDirNo(String currentDir) {
		if (TextUtils.isEmpty(currentDir) || !currentDir.contains(NO_SEPARATOR)) {
			return 0;
		}
		int index = currentDir.lastIndexOf(NO_SEPARATOR);
		String noStr = currentDir.substring(index + 1);
		return Integer.parseInt(noStr);
	}

	public boolean isFile() {
		return this.isFile;
	}

	public long getId() {
		return this.id;
	}

	public TdApi.Message getMessage() {
		return this.message;
	}

	public String parentDirectory() {
		return this.parentDir;
	}

	public String currentDirectory() {
		return this.currentDir;
	}

	public String getName() {
		return this.name;
	}

	/**
	 * root/软件| #_破解 #f #c jadx.zip
	 *
	 * @return
	 */
	public String getCaption() {
		String caption = "";
		if (!TextUtils.isEmpty(parentDir)) {
			caption += parentDir + CAPTION_SEPARATOR;
		}
		if (!TextUtils.isEmpty(currentDir)) {
			caption += TAG_SEPARATOR + currentDir + NO_SEPARATOR + currentDirNo + CAPTION_SEPARATOR;
		}
		if (isFile) {
			caption += TAG_SEPARATOR + FILE_TAG + CAPTION_SEPARATOR;
		} else {
			caption += TAG_SEPARATOR + DIRECTORY_TAG + CAPTION_SEPARATOR;
		}
		caption += name;
		return caption;
	}

	public String listAllChildren() {
		return TAG_SEPARATOR + name + NO_SEPARATOR + (currentDirNo + 1) + CAPTION_SEPARATOR + TAG_SEPARATOR;
	}

	public String listChildrenFiles() {
		return listAllChildren() + FILE_TAG;
	}

	public String listChildrenDirs() {
		return listAllChildren() + DIRECTORY_TAG;
	}

	public String listUncles() {
		return TAG_SEPARATOR + currentDir + NO_SEPARATOR + currentDirNo + CAPTION_SEPARATOR + TAG_SEPARATOR;
	}

	public String listUnclesFile() {
		return listUncles() + FILE_TAG;
	}

	public String listUnclesDir() {
		return listUncles() + DIRECTORY_TAG;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DriveFile driveFile = (DriveFile) o;

		if (isFile != driveFile.isFile) return false;
		if (length != driveFile.length) return false;
		if (!currentDir.equals(driveFile.currentDir)) return false;
		if (!parentDir.equals(driveFile.parentDir)) return false;
		return name.equals(driveFile.name);
	}

	@Override
	public int hashCode() {
		int result = (isFile ? 1 : 0);
		result = 31 * result + currentDir.hashCode();
		result = 31 * result + parentDir.hashCode();
		result = 31 * result + name.hashCode();
		result = 31 * result + (int) (length ^ (length >>> 32));
		return result;
	}

	public String getFilePath() {
		String filePath = "";
		if (!TextUtils.isEmpty(parentDir)) {
			filePath += parentDir + PATH_SEPARATOR;
		}
		if (!TextUtils.isEmpty(currentDir)) {
			filePath += currentDir + PATH_SEPARATOR;
		}
		if (!TextUtils.isEmpty(name)) {
			filePath += name;
		}
		return filePath;
	}

	@Override
	public String toString() {
		return getCaption();
	}

	public void rename(String newName) {
		this.name = newName;
	}

	/**
	 * root/软件/开发 #java_3 #f idea.zip
	 * root #软件_1 #d 开发
	 * root/软件 #开发_2 #f idea.zip
	 *
	 * @param destDir
	 * @return
	 */
	public boolean move(DriveFile destDir) {
		if (destDir.equals(this)) {
			return false;
		}
		if (!TextUtils.isEmpty(destDir.name)) {
			this.currentDir = destDir.name;
			this.currentDirNo = destDir.currentDirNo + 1;
		} else {
			return false;
		}
		if (!TextUtils.isEmpty(destDir.parentDir)) {
			this.parentDir = destDir.parentDir + PATH_SEPARATOR + destDir.currentDir;
		} else {
			this.parentDir = "";
		}
		if (!TextUtils.isEmpty(destDir.currentDir)) {
			this.parentDir = destDir.currentDir;
		} else {
			this.parentDir = "";
		}
		return true;
	}

	public long length() {
		return length;
	}

	/**
	 * root/软件/开发 #java_3 #f idea.zip
	 * root/软件 #开发_2 #d java
	 * #root_0 #d 软件
	 * #d root
	 *
	 * @return
	 */
	public DriveFile getParentFile() {
		if (TextUtils.isEmpty(currentDir)) {
			return this;
		}
		DriveFile parent = new DriveFile();
		parent.name = this.currentDir;
		if (TextUtils.isEmpty(parentDir)) {
			parent.parentDir = "";
			parent.currentDir = "";
			return parent;
		}
		String[] arr = this.parentDir.split(PATH_SEPARATOR);
		parent.currentDir = arr[arr.length - 1];
		parent.currentDirNo = this.currentDirNo - 1;
		if (arr.length == 1) {
			parent.parentDir = "";
		} else {
			String[] newParent = new String[arr.length - 1];
			System.arraycopy(arr, 0, newParent, 0, newParent.length);
			parent.parentDir = TextUtils.join(PATH_SEPARATOR, newParent);
		}
		return parent;
	}
}
