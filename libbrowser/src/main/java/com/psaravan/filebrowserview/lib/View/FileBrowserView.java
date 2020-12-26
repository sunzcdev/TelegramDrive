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
import android.widget.AbsListView;
import android.widget.FrameLayout;

import com.psaravan.filebrowserview.lib.FileBrowserEngine.FileBrowserEngine;
import com.psaravan.filebrowserview.lib.Interfaces.NavigationInterface;
import com.psaravan.filebrowserview.lib.ListLayout.ListLayoutView;

import org.drinkless.td.libcore.telegram.DriveFile;

public class FileBrowserView extends FrameLayout {

	//Context and AttributeSet.
	private Context mContext;
	private AttributeSet mAttributeSet;
	private BaseLayoutView mFileBrowserLayout;

	//File browser engine.
	private FileBrowserEngine mFileBrowserEngine;

	//Adapter to use for the list/grid view.
	private AbstractFileBrowserAdapter mAdapter;


	//Flags to display individual item attributes in the default adapter view.
	private boolean mShowOverflowMenus = true;
	private boolean mShowIcons = true;

	//Layout type constants.
	public static final int FILE_BROWSER_LIST_LAYOUT = 0;

	//Navigation Interface.
	private NavigationInterface mNavigationInterface;

	//Whether or not tabbed browsing is enabled.
	private boolean mTabbedBrowsingEnabled = false;

	public FileBrowserView(Context context) {
		super(context);
		mContext = context;

	}

	public FileBrowserView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		mContext = context;
		mAttributeSet = attributeSet;

	}

	public FileBrowserView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).layout(l, t, r, b);
		}

	}

	/**
	 * Initializes the file browser view and fires it up for first use. Must be called to
	 * properly display the file browser.
	 */
	public void init() {
		mFileBrowserLayout = new ListLayoutView(mContext, mAttributeSet, this).init(this);
		//Apply the navigation interface.
		mFileBrowserLayout.setNavigationInterface(mNavigationInterface);

	}

	public FileBrowserView setCustomAdapter(AbstractFileBrowserAdapter adapter)
			throws IllegalArgumentException {

		if (!(adapter instanceof AbstractFileBrowserAdapter))
			throw new IllegalArgumentException("The adapter you pass into setCustomAdapter() " +
					"must extend AbstractFileBrowserAdapter.");

		mAdapter = adapter;
		return this;
	}

	/**
	 * Sets whether or not the overflow menu should be shown or not (defaults to true). Note that
	 * this method will have no effect if you use your own adapter via
	 * {@link #setCustomAdapter(AbstractFileBrowserAdapter)}.
	 *
	 * @param show Whether or not the overflow menu should be shown.
	 * @return An instance of this FileBrowserView to allow method chaining.
	 */
	public FileBrowserView showOverflowMenus(boolean show) {
		mShowOverflowMenus = show;
		return this;
	}

	/**
	 * Sets whether or not each file/folder's icon should be displayed next to the name (defaults
	 * to true). Note that this method will have no effect if you use your own adapter via
	 * {@link #setCustomAdapter(AbstractFileBrowserAdapter)}.
	 *
	 * @param show Whether or not the icon should be shown.
	 * @return An instance of this FileBrowserView to allow method chaining.
	 */
	public FileBrowserView showItemIcons(boolean show) {
		mShowIcons = show;
		return this;
	}

	/**
	 * @param navInterface The navigation interface to assign to this view.
	 * @return An instance of this FileBrowserView to allow method chaining
	 */
	public FileBrowserView setNavigationInterface(NavigationInterface navInterface) {
		mNavigationInterface = navInterface;
		return this;
	}

	/**
	 * Sets whether or not tabbed browsing should be enabled. If you pass true,
	 * a TabHost will be placed above the FileBrowserView and will allow the user
	 * to open new tabs to browse the filesystem (à la Google Chrome tab browsing).
	 *
	 * @param enable Whether or not tabbed browsing should be enabled.
	 * @return An instance of this FileBrowserView to allow method chaining.
	 */
	public FileBrowserView enableTabbedBrowsing(boolean enable) {
		mTabbedBrowsingEnabled = enable;
		return this;
	}

	/**
	 * @return The AttributeSet object associated with this view instance.
	 */
	public AttributeSet getAttributeSet() {
		return mAttributeSet;
	}

	public BaseLayoutView getFileBrowserLayout() {
		return mFileBrowserLayout;
	}

	/**
	 * @return The file browser engine instance for this view.
	 */
	public FileBrowserEngine getFileBrowserEngine() {
		return mFileBrowserEngine;
	}

	public FileBrowserView setFileBrowserEngine(FileBrowserEngine fileBrowserEngine) {
		this.mFileBrowserEngine = fileBrowserEngine;
		return this;
	}

	/**
	 * @return The adapter that backs the list/grid view for FileBrowserView instance.
	 */
	public AbstractFileBrowserAdapter getFileBrowserAdapter() {
		return mAdapter;
	}

	/**
	 * @return The list/grid view that displays the file system. Note that this method
	 * does not return the specific subclass (ListView/GridView). You must manually
	 * cast the returned object as a ListView or a GridView to fully access each
	 * view's functionality.
	 */
	public AbsListView getAbsListView() {
		return mFileBrowserLayout.getAbsListView();
	}

	/**
	 * @return Whether or not each individual item's overflow menu should be displayed in the
	 * AbsListView. The returned value has no effect if you are using a custom adapter
	 * via {@link #setCustomAdapter(AbstractFileBrowserAdapter)}.
	 */
	public boolean shouldShowOverflowMenus() {
		return mShowOverflowMenus;
	}

	/**
	 * @return Whether or not each individual item's icon should be displayed in the AbsListView.
	 * The returned value has no effect if you are using a custom adapter via
	 * {@link #setCustomAdapter(AbstractFileBrowserAdapter)}.
	 */
	public boolean shouldShowItemIcons() {
		return mShowIcons;
	}

	/**
	 * @return Whether tabbed browsing is enabled or not for this FileBrowserView instance.
	 */
	public boolean isTabbedBrowsingEnabled() {
		return mTabbedBrowsingEnabled;
	}

	public void showParentDir() {
		mFileBrowserLayout.showParentDir();
	}

	public DriveFile getCurrentDir() {
		return mFileBrowserEngine.getCurrentDir();
	}

	public void showCurrentDir() {
		mFileBrowserLayout.showDir(getCurrentDir());
	}
}
