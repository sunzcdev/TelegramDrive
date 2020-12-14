package com.github.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.github.telegramdrive.R;
import com.psaravan.filebrowserview.lib.Interfaces.NavigationInterface;
import com.psaravan.filebrowserview.lib.View.FileBrowserView;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.Nullable;

public class FolderActivity extends Activity {
	private FileBrowserView mFileBrowserView;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_folder);
		mFileBrowserView = findViewById(R.id.fileBrowserView);
		mFileBrowserView.setFileBrowserLayoutType(FileBrowserView.FILE_BROWSER_LIST_LAYOUT) //Set the type of view to use.
				.setDefaultDirectory(new File("/sdcard/DCIM")) //Set the default directory to show.
				.setShowHiddenFiles(false) //Set whether or not you want to show hidden files.
				.excludeFileTypes(new ArrayList<>(), true)
				.showItemSizes(true) //Shows the sizes of each item in the list.
				.showOverflowMenus(true) //Shows the overflow menus for each item in the list.
				.showItemIcons(true)
				.setNavigationInterface(navInterface)
				.init(); //Loads the view. You MUST call this method, or the view will not be displayed.
	}

	/**
	 * Navigation interface for the view. Used to capture events such as a new
	 * directory being loaded, files being opened, etc. For our purposes here,
	 * we'll be using the onNewDirLoaded() method to update the ActionBar's title
	 * with the current directory's path.
	 */
	private NavigationInterface navInterface = new NavigationInterface() {

		@Override
		public void onNewDirLoaded(File dirFile) {
			//Update the action bar title.
			getActionBar().setTitle(dirFile.getAbsolutePath());
		}

		@Override
		public void onFileOpened(File file) {

		}

		@Override
		public void onParentDirLoaded(File dirFile) {

		}

		@Override
		public void onFileFolderOpenFailed(File file) {

		}

	};

	@Override
	public void onBackPressed() {
		if (mFileBrowserView != null) {
			File currentDir = mFileBrowserView.getCurrentDir();
			if (currentDir != null && !currentDir.equals(mFileBrowserView.getDefaultDirectory())) {
				mFileBrowserView.getFileBrowserLayout().showDir(currentDir.getParentFile());
			}
		} else {
			super.onBackPressed();
		}
	}

}