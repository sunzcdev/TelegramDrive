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
package com.psaravan.filebrowserview.lib.AsyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.widget.Toast;

import com.psaravan.filebrowserview.lib.Interfaces.MoveInterface;
import com.psaravan.filebrowserview.lib.R;

import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * AsyncTask that moves the specified file/folder recursively to a new location.
 *
 * @author Saravan Pantham
 */
public class AsyncMoveTask extends AsyncTask<String, Void, Boolean> {

    private Context mContext;
    private ProgressDialog pd;
	private File mSourceFile;
    private File mDestDirFile;
    private boolean mShowProgress = true;
    private MoveInterface mMoveInterface;

    public AsyncMoveTask(Context context, File source, File destDir, boolean showProgress) {
    	mContext = context;
    	mSourceFile = source;
        mShowProgress = showProgress;
        mDestDirFile = destDir;

    }

    @Override
    protected void onPreExecute() {

        if (mSourceFile==null)
            return;

        //Skip the rest of this method if the user doesn't want a progress dialog.
        if (!mShowProgress)
            return;

		pd = new ProgressDialog(mContext);
		pd.setCancelable(false);
		pd.setIndeterminate(false);
		pd.setTitle(R.string.move);
		pd.setMessage(mContext.getResources().getString(R.string.moving) + " " + mSourceFile.getName());
		pd.setButton(DialogInterface.BUTTON_NEUTRAL, mContext.getResources()
															 .getString(R.string.run_in_background), 
															 new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				pd.dismiss();
				
			}
			
		});
		
		pd.show();

        if (mMoveInterface!=null)
            mMoveInterface.preMoveStartSync();

    }
 
    @Override
    protected Boolean doInBackground(String... params) {

        if (mMoveInterface!=null)
            mMoveInterface.preMoveStartAsync();

        if (mSourceFile==null || mDestDirFile==null) {
            if (mMoveInterface!=null)
                mMoveInterface.onMoveCompleteAsync(false);

            return false;
        }
    	
    	if (mSourceFile.isDirectory()) {
    		try {
				FileUtils.moveDirectory(mSourceFile, mDestDirFile);
			} catch (Exception e) {
                if (mMoveInterface!=null)
                    mMoveInterface.onMoveCompleteAsync(false);

				return false;
			}
    		
    	} else {
    		try {
    			FileUtils.moveFile(mSourceFile, mDestDirFile);
    		} catch (Exception e) {
                if (mMoveInterface!=null)
                    mMoveInterface.onMoveCompleteAsync(false);

    			return false;
    		}
    		
    	}

        if (mMoveInterface!=null)
            mMoveInterface.onMoveCompleteAsync(true);

    	return true;
    }

    @Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

        if (mMoveInterface!=null)
            mMoveInterface.onMoveCompleteSync(result);

        if (pd!=null)
    	    pd.dismiss();

        if (result==true) {
            String message = mSourceFile.getName() + " " + mContext.getResources().getString(R.string.moved);
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();

    	} else {
            String message = mSourceFile.getName() + " " + mContext.getResources().getString(R.string.could_not_be_moved);
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();

    	}

	}

    /**
     * @param moveInterface The move interface instance to attach to this
     *                      AsyncTask.
     */
    public void setMoveInterface(MoveInterface moveInterface) {
        mMoveInterface = moveInterface;
    }

}