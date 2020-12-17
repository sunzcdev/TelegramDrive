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

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.psaravan.filebrowserview.lib.View.FileBrowserView;

import org.apache.commons.io.comparator.NameFileComparator;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Convenience class that exposes the Android file system and provides a
 * way to browse it via {@link com.psaravan.filebrowserview.lib.View.FileBrowserView}.
 *
 * @author Saravan Pantham
 */
public class FileBrowserEngine {

	//Current dir instance.
	private File mCurrentDir;

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
	private File mDefaultDir;

	//Flag to show/hide hidden files.
	private boolean mShowHiddenFiles = false;
	private boolean mShouldShowFolders = true;
	//FileExtensionFilter instance and whether or not to show other dirs in the current dir.
	private HashMap<String, Boolean> mFilterMap;

	public FileBrowserEngine(String defaultDirPath) {
		if (defaultDirPath == null) {
			this.mDefaultDir = Environment.getStorageDirectory();
		} else {
			setDefaultDirectory(new File(defaultDirPath));
		}
	}

	public FileBrowserEngine() {
		this(null);
	}

	/**
	 * Sets the default directory to show when the FileBrowserView is initialized.
	 *
	 * @param directory The file that points to the default directory to display.
	 * @return An instance of this FileBrowserView to allow method chaining.
	 * @throws java.lang.IllegalArgumentException Thrown if the input File argument doesn't
	 *                                            point to a valid directory or the directory can't be read.
	 */
	public FileBrowserEngine setDefaultDirectory(File directory) throws IllegalArgumentException {

		if (directory == null || !directory.isDirectory())
			throw new IllegalArgumentException("You must use a File object that points to a valid, " +
					"accessible directory.");

		if (!directory.canRead())
			throw new IllegalArgumentException("Could not read the specified default directory. Make " +
					"sure you have permission to read the directory.");

		mDefaultDir = directory;
		if (!mDefaultDir.exists()) {
			mDefaultDir.mkdirs();
		}
		return this;
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
	 * Sets whether hidden files should be shown or not.
	 *
	 * @param show Specifies whether hidden files should be shown or not.
	 * @return An instance of this FileBrowserView to allow method chaining.
	 */
	public FileBrowserEngine setShowHiddenFiles(boolean show) {
		mShowHiddenFiles = show;
		return this;
	}

	/**
	 * @return A File object that points to the default directory that should be
	 * displayed for this FileBrowserView instance.
	 */
	public File getDefaultDirectory() {
		return mDefaultDir;
	}

	/**
	 * @return Whether or not hidden files/folders should be displayed.
	 */
	public boolean shouldShowHiddenFiles() {
		return mShowHiddenFiles;
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
	public AdapterData loadDir(File directory) {

		mCurrentDir = directory;

		//Init the directory's data arrays.
		ArrayList<String> namesList = new ArrayList<String>();
		ArrayList<String> pathsList = new ArrayList<String>();
		ArrayList<Integer> typesList = new ArrayList<Integer>();
		ArrayList<String> sizesList = new ArrayList<String>();

		//Grab a list of all files/subdirs within the specified directory.
		File[] files = directory.listFiles();
		if (files != null) {
			//Sort the files/subdirs by name.
			Arrays.sort(files, NameFileComparator.NAME_INSENSITIVE_COMPARATOR);

			for (File file : files) {
				if ((!file.isHidden() || shouldShowHiddenFiles()) && file.canRead()) {
					if (file.isDirectory() && shouldShowFolders()) {
						/*
						 * Starting with Android 4.2, /storage/emulated/legacy/...
						 * is a symlink that points to the actual directory where
						 * the user's files are stored. We need to detect the
						 * actual directory's file path here.
						 */
						String filePath = getRealFilePath(file.getAbsolutePath());
						pathsList.add(filePath);
						namesList.add(file.getName());
						File[] listOfFiles = file.listFiles();

                        typesList.add(FOLDER);
                        if (listOfFiles != null) {
                            if (listOfFiles.length == 1) {
								sizesList.add("" + listOfFiles.length + " item");
							} else {
								sizesList.add("" + listOfFiles.length + " items");
							}
						} else {
                            sizesList.add("Unknown items");
						}
					} else {
						try {
							String path = file.getCanonicalPath();
							//Check if the file ends with an excluded extension.
							String[] splits = path.split("\\.");
							if (mFilterMap.containsKey("." + splits[splits.length - 1]))
								continue;
							pathsList.add(path);
						} catch (IOException e) {
							continue;
						}
						namesList.add(file.getName());
						String fileName = "";
						try {
							fileName = file.getCanonicalPath();
						} catch (IOException e) {
							e.printStackTrace();
						}

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

		}

		return new AdapterData(namesList, typesList, pathsList, sizesList);
	}


	public void openFile(File file) {

	}

	/**
	 * Resolves the /storage/emulated/legacy paths to
	 * their true folder path representations. Required
	 * for Nexii and other devices with no SD card.
	 *
	 * @return The true, resolved file path to the input path.
	 */
	@SuppressLint("SdCardPath")
	private String getRealFilePath(String filePath) {

		if (filePath.equals("/storage/emulated/0") ||
				filePath.equals("/storage/emulated/0/") ||
				filePath.equals("/storage/emulated/legacy") ||
				filePath.equals("/storage/emulated/legacy/") ||
				filePath.equals("/storage/sdcard0") ||
				filePath.equals("/storage/sdcard0/") ||
				filePath.equals("/sdcard") ||
				filePath.equals("/sdcard/") ||
				filePath.equals("/mnt/sdcard") ||
				filePath.equals("/mnt/sdcard/")) {

			return Environment.getExternalStorageDirectory().toString();
		}

		return filePath;
	}

	/**
	 * Resolves the file extension for the specified file.
	 *
	 * @param fileName The name of the file (including its extension).
	 * @return The extension of the file (excluding the dot ".").
	 */
	private String getFileExtension(String fileName) {
		String fileNameArray[] = fileName.split("\\.");
		String extension = fileNameArray[fileNameArray.length - 1];

		return extension;
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

	public File getCurrentDir() {
		return mCurrentDir;
	}

}
