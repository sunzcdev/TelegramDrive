package com.github.telegram;

import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.github.drive.Callback;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

import java.util.HashMap;
import java.util.Map;

public abstract class TelegramAction implements Client.ResultHandler {
	private final String TAG = this.getClass().getSimpleName();
	private Callback<String, Void> logListener;
	private Callback<DialogInfo, Void> inputListener;

	@Override
	public void onResult(TdApi.Object object) {
	}

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