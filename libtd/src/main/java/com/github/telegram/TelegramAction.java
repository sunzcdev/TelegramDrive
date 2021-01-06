package com.github.telegram;

import android.util.Log;

import com.github.drive.Callback;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

public abstract class TelegramAction implements Client.ResultHandler {
	private final String TAG = this.getClass().getSimpleName();
	private Callback<String, Void> logListener;
	private Callback<DialogInfo, Void> inputListener;

	protected Client client;


	public void create() {
		Client.execute(new TdApi.SetLogVerbosityLevel(0));
		client = Client.create(this, Throwable::printStackTrace, Throwable::printStackTrace);
	}

	public <T> void send(TdApi.Function function, ActionCallback<T> callback) {
		if (function != null) {
			Log.i("sunzc", ">>>>>:" + function.toString());
			this.client.send(function, callback);
		}
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

	@Override
	public void onResult(TdApi.Object object) {

	}
}
