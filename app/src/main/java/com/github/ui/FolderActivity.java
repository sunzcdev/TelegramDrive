package com.github.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.github.App;
import com.github.demo.DemoActivity;
import com.github.telegram.TelegramClient;
import com.github.telegramdrive.R;
import com.github.utils.ViewUtils;
import com.psaravan.filebrowserview.lib.FileBrowserEngine.FileBrowserEngine;
import com.psaravan.filebrowserview.lib.Interfaces.NavigationInterface;
import com.psaravan.filebrowserview.lib.Utils.Utils;
import com.psaravan.filebrowserview.lib.View.FileBrowserView;
import com.psaravan.filebrowserview.lib.db.DriveFileEntity;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class FolderActivity extends AppCompatActivity {
	private static final String TAG = "SUNZC";
	private static final int SELECT_FILE = 0x01;
	private FileBrowserView mFileBrowserView;
	private View mUploadBt;
	private FileBrowserEngine engine;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_folder);
		mFileBrowserView = findViewById(R.id.fileBrowserView);
		mUploadBt = findViewById(R.id.upload);
		mUploadBt.setOnClickListener(v -> upload());
		TelegramClient client = ((App) getApplication()).getClient();
		engine = new FileBrowserEngine(this, client)
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.dir_ops_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.create_dir) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("请输入文件夹名字");
			EditText editText = new EditText(this);
			editText.setInputType(InputType.TYPE_CLASS_TEXT);
			builder.setView(editText);
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String dirName = editText.getText().toString();
					engine.createDir(dirName);
					mFileBrowserView.showCurrentDir();
				}
			});
			builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
			builder.show();
		} else if (item.getItemId() == R.id.refresh) {
			engine.refresh();
		}
		return super.onOptionsItemSelected(item);
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
		engine.uploadFile(file, o -> {
			mFileBrowserView.showCurrentDir();
			return null;
		});
	}

	private NavigationInterface navInterface = new NavigationInterface() {

		@Override
		public void onNewDirLoaded(DriveFileEntity dirFile) {
			//Update the action bar title.
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					setTitle(dirFile.name);
				}
			});
		}

		@Override
		public void onFileOpened(DriveFileEntity file) {
			Utils.toast(FolderActivity.this, file.name + "下载完成");
		}

		@Override
		public void onParentDirLoaded(DriveFileEntity dirFile) {

		}

		@Override
		public void onFileFolderOpenFailed(DriveFileEntity file) {

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
