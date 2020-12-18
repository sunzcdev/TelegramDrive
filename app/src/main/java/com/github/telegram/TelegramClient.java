package com.github.telegram;

import org.drinkless.td.libcore.telegram.TdApi;

public class TelegramClient {

	public final AuthAction auth;
	public final ChatAction chat;
	public final MessageAction message;

	public TelegramClient(TdApi.TdlibParameters parameters) {
		auth = new AuthAction(parameters);
		UserAction userAction = new UserAction(auth);
		chat = new ChatAction(auth, userAction);
		message = new MessageAction(auth);
	}

	public static TelegramClient client;

	public static TelegramClient create(TdApi.TdlibParameters parameters) {
		if (client == null) {
			return new TelegramClient(parameters);
		} else {
			return client;
		}
	}

}
