package com.github.telegram;

import org.drinkless.td.libcore.telegram.TdApi;

public class ChatAction extends TelegramAction {
	private final ClientAction clientAction;
	private final AuthAction authAction;
	private final UserAction userAction;

	public ChatAction(ClientAction clientAction, AuthAction authAction, UserAction userAction) {
		super(authAction.context);
		this.clientAction = clientAction;
		this.authAction = authAction;
		this.userAction = userAction;
	}

	public void CreatePrivateChat() {
		clientAction.send(new TdApi.CreatePrivateChat(402780, true), this);
	}

	public void GetChats() {
		clientAction.send(new TdApi.GetChats(new TdApi.ChatListMain(), Long.MAX_VALUE, 0, 100), this);
	}

	public long getCurrentChatId() {
		return 0;
	}

	@Override
	public void onResult(TdApi.Object object) {
		super.onResult(object);

	}
}
