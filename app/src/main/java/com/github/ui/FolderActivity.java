package com.github.ui;

import android.app.Activity;
import android.os.Bundle;

import com.github.App;
import com.github.telegramdrive.R;
import com.psaravan.filebrowserview.lib.FileBrowserEngine.FileBrowserEngine;
import com.psaravan.filebrowserview.lib.Interfaces.NavigationInterface;
import com.psaravan.filebrowserview.lib.View.FileBrowserView;

import org.drinkless.td.libcore.telegram.DriveFile;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.Nullable;

public class FolderActivity extends Activity {
	private static final String TAG = "SUNZC";
	private FileBrowserView mFileBrowserView;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_folder);
		mFileBrowserView = findViewById(R.id.fileBrowserView);

		FileBrowserEngine engine = new FileBrowserEngine(((App) getApplication()).getClient())
				.excludeFileTypes(new ArrayList<>(), true);
		mFileBrowserView
				.setFileBrowserEngine(engine)
				.showOverflowMenus(true) //Shows the overflow menus for each item in the list.
				.showItemIcons(true)
				.setNavigationInterface(navInterface)
				.init(); //Loads the view. You MUST call this method, or the view will not be displayed.
	}

	private NavigationInterface navInterface = new NavigationInterface() {

		@Override
		public void onNewDirLoaded(DriveFile dirFile) {
			//Update the action bar title.
			getActionBar().setTitle(dirFile.getFilePath());
		}

		@Override
		public void onFileOpened(DriveFile file) {

		}

		@Override
		public void onParentDirLoaded(DriveFile dirFile) {

		}

		@Override
		public void onFileFolderOpenFailed(DriveFile file) {

		}

	};

	@Override
	public void onBackPressed() {
		if (mFileBrowserView != null) {
			mFileBrowserView.showParentDir();
		} else {
			super.onBackPressed();
		}
	}

}
