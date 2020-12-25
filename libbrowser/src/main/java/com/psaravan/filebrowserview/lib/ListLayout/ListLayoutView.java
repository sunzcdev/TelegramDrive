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
package com.psaravan.filebrowserview.lib.ListLayout;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.github.drive.Callback;
import com.psaravan.filebrowserview.lib.FileBrowserEngine.AdapterData;
import com.psaravan.filebrowserview.lib.R;
import com.psaravan.filebrowserview.lib.View.BaseLayoutView;
import com.psaravan.filebrowserview.lib.View.FileBrowserView;

import org.drinkless.td.libcore.telegram.DriveFile;

/**
 * List layout view implementation for the file browser.
 *
 * @author Saravan Pantham
 */
public class ListLayoutView extends BaseLayoutView {

	//Context.
	private Context mContext;

	//Parent FileBrowserView and its children.
	private FileBrowserView mFileBrowserView;
	private Handler mMainHandler;

	public ListLayoutView(Context context, AttributeSet attributeSet, FileBrowserView fileBrowserView) {
		super(context, attributeSet, fileBrowserView.getFileBrowserEngine());
		mContext = context;
		mFileBrowserView = fileBrowserView;
		mMainHandler = new Handler(mContext.getMainLooper());
	}

	/**
	 * Inflates the layout and sets the list's adapter.
	 *
	 * @param viewGroup The ViewGroup to inflate the layout into.
	 * @return A reference to this view's instance.
	 */
	public ListLayoutView init(ViewGroup viewGroup) {
		//Inflate the view from the XML resource.
		View.inflate(mContext, R.layout.simple_list_file_browser, viewGroup);
		mAbsListView = (ListView) viewGroup.findViewById(R.id.file_browser_list_view);

		//Display the default dir.
		showDir(fileBrowserEngine.getDefaultDirectory());
		return this;
	}

	/**
	 * Loads the directory structure of the specified dir and sets the ListView's adapter.
	 *
	 * @param directory The File object that points to the directory to load.
	 */
	@Override
	public void showDir(DriveFile directory) {
		super.showDir(directory);
		//Grab the directory's data to feed to the list adapter.
		fileBrowserEngine.loadDir(directory, adapterData -> {
			mMainHandler.post(new Runnable() {
				@Override
				public void run() {
					//Check if the user wants to use a custom adapter.
					if (mFileBrowserView.getFileBrowserAdapter() != null) {
						//The user called setFileBrowserAdapter() and is using a custom adapter.
						mFileBrowserView.getFileBrowserAdapter().setAdapterData(adapterData);

					} else {
						//Nope, no custom adapter, so fall back to the default adapter.
						ListLayoutAdapter adapter = new ListLayoutAdapter(mContext, mFileBrowserView, adapterData);
						mFileBrowserView.setCustomAdapter(adapter);

					}

					//Apply the adapter to the ListView.
					mAbsListView.setAdapter(mFileBrowserView.getFileBrowserAdapter());

					//Apply the click listener to the ListView.
					mAbsListView.setOnItemClickListener(onItemClickListener);
				}
			});
			return null;
		});
	}

	/**
	 * Click listener for the ListView.
	 */
	private ListView.OnItemClickListener onItemClickListener = new ListView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			DriveFile file = null;
			try {
				file = mFileBrowserView.getFileBrowserAdapter().getPathsList().get(position);
				if (file.isFile()) {
					openFile(file);
				} else {
					showDir(file);
				}

			} catch (Exception e) {
				e.printStackTrace();

				if (mNavigationInterface != null)
					mNavigationInterface.onFileFolderOpenFailed(file);

				//Display an error toast.
				if (file != null && file.isFile()) {
					Toast.makeText(mContext, R.string.unable_to_load_dir, Toast.LENGTH_SHORT).show();
				} else if (file != null && !file.isFile()) {
					Toast.makeText(mContext, R.string.unable_to_open_file, Toast.LENGTH_SHORT).show();
				}

			}

		}

	};

}
