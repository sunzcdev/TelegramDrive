package com.github.telegram;

import android.content.Context;
import android.util.Log;

import com.github.utils.ViewUtils;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

public abstract class TelegramAction implements Client.ResultHandler {
	private final String TAG = this.getClass().getSimpleName();
	protected Context context;

	public TelegramAction(Context context) {
		this.context = context;
	}

	@Override
	public void onResult(TdApi.Object object) {
		log(object.toString());
	}

	protected void log(String msg) {
		Log.i(TAG, msg);
	}

	protected void toast(String msg) {
		ViewUtils.toast(context, msg);
	}
}
