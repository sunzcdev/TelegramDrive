package com.github.telegram;

import android.util.Log;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

public class ClientAction extends TelegramAction implements Client.ResultHandler {
	protected Client client;

	public <T> void send(TdApi.Function function, ActionCallback<T> callback) {
		if (function != null) {
			Log.i(getClass().getSimpleName(), ">>>>>:" + function.toString());
			this.client.send(function, callback);
		}
	}

	public void create() {
		Client.execute(new TdApi.SetLogVerbosityLevel(0));
		client = Client.create(this, Throwable::printStackTrace, Throwable::printStackTrace);
	}

	@Override
	public void onResult(TdApi.Object object) {

	}
}
