package com.github.telegram;

import android.util.Log;

import com.github.drive.Callback;

public abstract class TelegramAction {
	private final String TAG = this.getClass().getSimpleName();
	private Callback<String, Void> logListener;
	private Callback<DialogInfo, Void> inputListener;

	protected void log(String msg) {
		Log.i(TAG, msg);
	}

	protected void show(String msg) {
		if (logListener != null) {
			logListener.call(msg);
		}
	}

	protected void input(DialogInfo info) {
		if (this.inputListener != null) {
			this.inputListener.call(info);
		}
	}

	public void setInputListener(Callback<DialogInfo, Void> inputListener) {
		this.inputListener = inputListener;
	}

	public void setLogListener(Callback<String, Void> logListener) {
		this.logListener = logListener;
	}
}
