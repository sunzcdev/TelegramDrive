package com.github.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.github.App;
import com.github.telegram.ActionCallback;
import com.github.telegram.ChatAction;
import com.github.telegram.MessageAction;
import com.github.telegramdrive.R;
import com.psaravan.filebrowserview.lib.FileBrowserEngine.FileBrowserEngine;
import com.psaravan.filebrowserview.lib.FileBrowserEngine.MyFileBrowserEngine;
import com.psaravan.filebrowserview.lib.Interfaces.NavigationInterface;
import com.psaravan.filebrowserview.lib.View.FileBrowserView;

import org.drinkless.td.libcore.telegram.TdApi;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.Nullable;

public class FolderActivity extends Activity {
	private static final String TAG = "SUNZC";
	private FileBrowserView mFileBrowserView;
	private MessageAction message;
	private ChatAction chat;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_folder);
		mFileBrowserView = findViewById(R.id.fileBrowserView);
		FileBrowserEngine engine = new MyFileBrowserEngine(getExternalFilesDir("file").getAbsolutePath())
				.setShowHiddenFiles(false) //Set whether or not you want to show hidden files.
				.excludeFileTypes(new ArrayList<>(), true);
		mFileBrowserView.setFileBrowserLayoutType(FileBrowserView.FILE_BROWSER_LIST_LAYOUT) //Set the type of view to use.
				.setFileBrowserEngine(engine)
				.showOverflowMenus(true) //Shows the overflow menus for each item in the list.
				.showItemIcons(true)
				.setNavigationInterface(navInterface)
				.init(); //Loads the view. You MUST call this method, or the view will not be displayed.
		chat = ((App) getApplication()).getClient().chat;
		message = ((App) getApplication()).getClient().message;
		chat.GetChats(o -> {
			TdApi.Chats chats = (TdApi.Chats) o;
			if (chats.totalCount > 0) {
				chat.SearchChat("Telegram drive", o1 -> {
					TdApi.Chats chats1 = (TdApi.Chats) o1;
					if (chats1.totalCount > 0) {
						chat.GetChat(chats1.chatIds[0], o2 -> {
							TdApi.Chat chat1 = (TdApi.Chat) o2;
//							uploadFile(chat1);
							downloadFile(chat1);
							return null;
						});
					}
					return null;
				});
			}
			return null;
		});
	}

	private void uploadFile(TdApi.Chat chat1) {
		message.UploadFile(chat1.id, new File("/sdcard/vs.zip"), "vscode", new ActionCallback() {
			@Override
			public Void call(Object o) {
				Log.i(TAG, "上传完成");
				return null;
			}
		});
	}

	private void downloadFile(TdApi.Chat chat1) {
		message.DownloadFile(chat1.id, "vscode", o3 -> {
			Log.i(TAG, "下载完成");
			return null;
		});
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
			mFileBrowserView.showParentDir();
		} else {
			super.onBackPressed();
		}
	}

}
