/*
 * Copyright (C) 2014 Saravan Pantham
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.psaravan.filebrowserview.lib.FileBrowserEngine;

import com.github.drive.Callback;
import com.github.telegram.ActionCallback;
import com.github.telegram.TelegramClient;

import org.drinkless.td.libcore.telegram.DriveFile;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Convenience class that exposes the Android file system and provides a
 * way to browse it via {@link com.psaravan.filebrowserview.lib.View.FileBrowserView}.
 *
 * @author Saravan Pantham
 */
public class FileBrowserEngine {

	//Current dir instance.
	private DriveFile mCurrentDir;
	private TelegramClient client;
	//File type constants.
	public static final int FILE_AUDIO = 0;
	public static final int FILE_VIDEO = 1;
	public static final int FILE_PICTURE = 2;
	public static final int FILE_GENERIC = 3;
	public static final int FOLDER = 4;

	//File size/unit dividers
	private final long KILOBYTES = 1024;
	private final long MEGABYTES = KILOBYTES * KILOBYTES;
	private final long GIGABYTES = MEGABYTES * KILOBYTES;
	private final long TERABYTES = GIGABYTES * KILOBYTES;
	//Default directory to display.
	public static final DriveFile ROOT = new DriveFile();

	//Flag to show/hide hidden files.
	private boolean mShouldShowFolders = true;
	//FileExtensionFilter instance and whether or not to show other dirs in the current dir.
	private HashMap<String, Boolean> mFilterMap = new HashMap<>();

	public FileBrowserEngine(TelegramClient client) {
		this.client = client;
	}

	/**
	 * Prevents the browser from displaying files that match any of the file extensions that are
	 * passed in via the param fileExtensions. Calling this method is optional; not calling it or
	 * passing in an empty list will cause all files to be displayed regardless of their extensions.
	 *
	 * @param fileExtensions An ArrayList that contains string representations of the
	 *                       extensions of the files to hide in the browser. The file
	 *                       extension must be formatted like this: ".xxx", where xxx is
	 *                       the file extension.
	 * @param showFolders    Sets whether folders should be displayed in the view or not. Useful for
	 *                       displaying a static view of the current folder with no way to navigate
	 *                       away from it.
	 * @return An instance of this FileBrowserView to allow method chaining.
	 */
	public FileBrowserEngine excludeFileTypes(ArrayList<String> fileExtensions, boolean showFolders) {
		if (fileExtensions == null)
			return this;

		setShouldShowFolders(showFolders);
		for (String fileExtension : fileExtensions) {
			if (fileExtension.startsWith("."))
				throw new IllegalArgumentException("Invalid file extension format. You must " +
						"start the extension with a period (.), " +
						"followed by the actual extension itself. " +
						"Exception thrown for the following extension: " +
						fileExtension);
			mFilterMap.put(fileExtension, false);
		}
		return this;
	}

	/**
	 * @return A File object that points to the default directory that should be
	 * displayed for this FileBrowserView instance.
	 */
	public DriveFile getDefaultDirectory() {
		return ROOT;
	}

	public void setShouldShowFolders(boolean shouldShowFolders) {
		this.mShouldShowFolders = shouldShowFolders;
	}

	/**
	 * @return Whether or not folders should be shown in the browser.
	 */
	public boolean shouldShowFolders() {
		return mShouldShowFolders;
	}

	/**
	 * Loads the specified folder.
	 *
	 * @param directory The file object to points to the directory to load.
	 * @return An {@link AdapterData} object that holds the data of the specified directory.
	 */
	public void loadDir(DriveFile directory, Callback<AdapterData,Void> callback) {
		mCurrentDir = directory;
		//Init the directory's data arrays.
		//Grab a list of all files/subdirs within the specified directory.
		client.listFiles(directory, files -> {
			ArrayList<String> namesList = new ArrayList<String>();
			ArrayList<DriveFile> pathsList = new ArrayList<>();
			ArrayList<Integer> typesList = new ArrayList<Integer>();
			ArrayList<String> sizesList = new ArrayList<String>();
			if (files != null) {
				//TODO 对文件进行排序
//					Arrays.sort(files, NameFileComparator.NAME_INSENSITIVE_COMPARATOR);
				for (DriveFile file : files) {
					if (!file.isFile() && shouldShowFolders()) {
						pathsList.add(file);
						namesList.add(file.getName());
						//TODO 如何做到一次遍历就可以获取一级子文件夹的内容
//							File[] listOfFiles = file.listFiles();
//
							typesList.add(FOLDER);
//							if (listOfFiles != null) {
//								if (listOfFiles.length == 1) {
//									sizesList.add("" + listOfFiles.length + " item");
//								} else {
//									sizesList.add("" + listOfFiles.length + " items");
//								}
//							} else {
						sizesList.add("");
//							}
					} else {
						String path = file.getCaption();
						//Check if the file ends with an excluded extension.
						String[] splits = path.split("\\.");
						if (mFilterMap.containsKey("." + splits[splits.length - 1])) {
							continue;
						}
						pathsList.add(file);
						namesList.add(file.getName());
						String fileName = file.getName();
						//Add the file element to typesList based on the file type.
						if (getFileExtension(fileName).equalsIgnoreCase("mp3") ||
								getFileExtension(fileName).equalsIgnoreCase("3gp") ||
								getFileExtension(fileName).equalsIgnoreCase("mp4") ||
								getFileExtension(fileName).equalsIgnoreCase("m4a") ||
								getFileExtension(fileName).equalsIgnoreCase("aac") ||
								getFileExtension(fileName).equalsIgnoreCase("ts") ||
								getFileExtension(fileName).equalsIgnoreCase("flac") ||
								getFileExtension(fileName).equalsIgnoreCase("mid") ||
								getFileExtension(fileName).equalsIgnoreCase("xmf") ||
								getFileExtension(fileName).equalsIgnoreCase("mxmf") ||
								getFileExtension(fileName).equalsIgnoreCase("midi") ||
								getFileExtension(fileName).equalsIgnoreCase("rtttl") ||
								getFileExtension(fileName).equalsIgnoreCase("rtx") ||
								getFileExtension(fileName).equalsIgnoreCase("ota") ||
								getFileExtension(fileName).equalsIgnoreCase("imy") ||
								getFileExtension(fileName).equalsIgnoreCase("ogg") ||
								getFileExtension(fileName).equalsIgnoreCase("mkv") ||
								getFileExtension(fileName).equalsIgnoreCase("wav")) {

							//The file is an audio file.
							typesList.add(FILE_AUDIO);
							sizesList.add("" + getFormattedFileSize(file.length()));

						} else if (getFileExtension(fileName).equalsIgnoreCase("jpg") ||
								getFileExtension(fileName).equalsIgnoreCase("gif") ||
								getFileExtension(fileName).equalsIgnoreCase("png") ||
								getFileExtension(fileName).equalsIgnoreCase("bmp") ||
								getFileExtension(fileName).equalsIgnoreCase("webp")) {

							//The file is a picture file.
							typesList.add(FILE_PICTURE);
							sizesList.add("" + getFormattedFileSize(file.length()));

						} else if (getFileExtension(fileName).equalsIgnoreCase("3gp") ||
								getFileExtension(fileName).equalsIgnoreCase("mp4") ||
								getFileExtension(fileName).equalsIgnoreCase("3gp") ||
								getFileExtension(fileName).equalsIgnoreCase("ts") ||
								getFileExtension(fileName).equalsIgnoreCase("webm") ||
								getFileExtension(fileName).equalsIgnoreCase("mkv")) {

							//The file is a video file.
							typesList.add(FILE_VIDEO);
							sizesList.add("" + getFormattedFileSize(file.length()));

						} else {

							//We don't have an icon for this file type so give it the generic file flag.
							typesList.add(FILE_GENERIC);
							sizesList.add("" + getFormattedFileSize(file.length()));

						}

					}

				}

			}
			callback.call(new AdapterData(namesList, typesList, pathsList, sizesList));
			return null;
		});
	}


	public void openFile(DriveFile file) {

	}

	/**
	 * Resolves the file extension for the specified file.
	 *
	 * @param fileName The name of the file (including its extension).
	 * @return The extension of the file (excluding the dot ".").
	 */
	private String getFileExtension(String fileName) {
		String[] fileNameArray = fileName.split("\\.");
		return fileNameArray[fileNameArray.length - 1];
	}

	/**
	 * Formats the input value in terms of file size.
	 *
	 * @param value The value to convert to a file size.
	 * @return The formatted size of the input file.
	 */
	private String getFormattedFileSize(final long value) {

		final long[] dividers = new long[]{TERABYTES, GIGABYTES, MEGABYTES, KILOBYTES, 1};
		final String[] units = new String[]{"TB", "GB", "MB", "KB", "bytes"};

		if (value < 1) {
			return "";
		}

		String result = null;
		for (int i = 0; i < dividers.length; i++) {
			final long divider = dividers[i];
			if (value >= divider) {
				result = formatFileSizeString(value, divider, units[i]);
				break;
			}

		}

		return result;
	}

	/**
	 * Formats the input value as a file size string.
	 *
	 * @param value   The value to format.
	 * @param divider The value to divide the input value by.
	 * @param unit    The output units.
	 * @return The formatted file size string.
	 */
	private String formatFileSizeString(final long value, final long divider, final String unit) {
		final double result = divider > 1 ? (double) value / (double) divider : (double) value;

		return new DecimalFormat("#,##0.#").format(result) + " " + unit;
	}

	public DriveFile getCurrentDir() {
		return mCurrentDir;
	}

}
