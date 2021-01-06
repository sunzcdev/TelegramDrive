package com.github.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.github.App;
import com.github.telegram.ActionCallback;
import com.github.telegram.TelegramClient;
import com.github.telegramdrive.R;
import com.github.utils.ViewUtils;
import com.psaravan.filebrowserview.lib.FileBrowserEngine.FileBrowserEngine;
import com.psaravan.filebrowserview.lib.Interfaces.NavigationInterface;
import com.psaravan.filebrowserview.lib.View.FileBrowserView;

import org.drinkless.td.libcore.telegram.DriveFile;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class FolderActivity extends AppCompatActivity {
	private static final String TAG = "SUNZC";
	private static final int SELECT_FILE = 0x01;
	private FileBrowserView mFileBrowserView;
	private View mUploadBt;
	private TelegramClient client;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_folder);
		mFileBrowserView = findViewById(R.id.fileBrowserView);
		mUploadBt = findViewById(R.id.upload);
		mUploadBt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				upload();
			}
		});
		client = ((App) getApplication()).getClient();
		FileBrowserEngine engine = new FileBrowserEngine(client)
				.excludeFileTypes(new ArrayList<>(), true);
		mFileBrowserView
				.setFileBrowserEngine(engine)
				.showOverflowMenus(true) //Shows the overflow menus for each item in the list.
				.showItemIcons(true)
				.setNavigationInterface(navInterface)
				.init(); //Loads the view. You MUST call this method, or the view will not be displayed.
	}

	private void upload() {
		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
		startActivityForResult(
				Intent.createChooser(intent, "选择要上传的文件"),
				SELECT_FILE
		);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SELECT_FILE) {
			if (resultCode == RESULT_CANCELED || resultCode == RESULT_FIRST_USER) {
				return;
			}
			final Uri videoUri = data.getData();
			Log.i(TAG, "所选文档路径: " + videoUri);
			if (videoUri != null) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					ViewUtils.getPathAsync(this, videoUri, file -> {
						upload(file);
						return null;
					});
				} else {
					String file = ViewUtils.getPath(this, videoUri);
					upload(new File(file));
				}
			}
		}
	}

	private void upload(File file) {
		client.uploadFile(file, mFileBrowserView.getCurrentDir(), new ActionCallback<String>() {
			@Override
			public void toObject(String s) {
				mFileBrowserView.showCurrentDir();
			}
		});
	}

	private NavigationInterface navInterface = new NavigationInterface() {

		@Override
		public void onNewDirLoaded(DriveFile dirFile) {
			//Update the action bar title.
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					setTitle(dirFile.getFilePath());
				}
			});
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
