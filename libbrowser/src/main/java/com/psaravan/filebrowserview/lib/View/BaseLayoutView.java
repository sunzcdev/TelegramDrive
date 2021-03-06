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
package com.psaravan.filebrowserview.lib.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;

import com.psaravan.filebrowserview.lib.FileBrowserEngine.FileBrowserEngine;
import com.psaravan.filebrowserview.lib.Interfaces.NavigationInterface;

import org.drinkless.td.libcore.telegram.DriveFile;

import java.io.File;

/**
 * The base layout that is extended by {@link com.psaravan.filebrowserview.lib.ListLayout.ListLayoutView}
 * and {@link com.psaravan.filebrowserview.lib.GridLayout.GridLayoutView}.
 *
 * @author Saravan Pantham
 */
public abstract class BaseLayoutView extends View {

	protected final FileBrowserEngine fileBrowserEngine;
	/**
	 * Context intstance.
	 */
	protected Context mContext;

	/**
	 * The ListView/GridView that displays the file system.
	 */
	protected AbsListView mAbsListView;

	/**
	 * The interface instance that provides callbacks for filesystem
	 * navigation events.
	 */
	protected NavigationInterface mNavigationInterface;

	/**
	 * Default constructor.
	 */
	public BaseLayoutView(Context context, AttributeSet attributeSet, FileBrowserEngine engine) {
		super(context, attributeSet);
		mContext = context;
		this.fileBrowserEngine = engine;
	}

	/**
	 * Override this method to implement your logic for loading a directory structure of the
	 * specified dir and to set your AbsListView's adapter.
	 *
	 * @param directory The File object that points to the directory to load.
	 */
	public void showDir(DriveFile directory) {
		if (mNavigationInterface != null)
			mNavigationInterface.onNewDirLoaded(directory);
	}

	public void showParentDir() {
		DriveFile currentDir = fileBrowserEngine.getCurrentDir();
		if (currentDir != null && !currentDir.equals(fileBrowserEngine.getDefaultDirectory())) {
			DriveFile parentDir = currentDir.getParentFile();
			if (mNavigationInterface != null)
				mNavigationInterface.onParentDirLoaded(parentDir);
			showDir(parentDir);
		}
	}

	/**
	 * 打开文件
	 *
	 * @param file 需要打开的文件
	 */
	public void openFile(DriveFile file) {
		if (mNavigationInterface != null)
			mNavigationInterface.onFileOpened(file);
		fileBrowserEngine.openFile(file);
	}

	/**
	 * Sets the navigation interface instance for this view.
	 *
	 * @param navInterface The interface instance to assign to this view.
	 */
	public void setNavigationInterface(NavigationInterface navInterface) {
		mNavigationInterface = navInterface;
	}

	/**
	 * @return The ListView/GridView that displays the file system.
	 */
	public AbsListView getAbsListView() {
		return mAbsListView;
	}

}
