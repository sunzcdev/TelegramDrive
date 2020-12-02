package com.github.telegram;

import android.content.Context;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

public class ClientAction extends TelegramAction {
	protected Client client;

	ClientAction(Context context) {
		super(context);
	}

	public void send(TdApi.Function function, Client.ResultHandler handler) {
		this.client.send(function, handler);
	}

	public void create() {
		Client.execute(new TdApi.SetLogVerbosityLevel(0));
		client = Client.create(ClientAction.this, Throwable::printStackTrace, Throwable::printStackTrace);
	}
}
