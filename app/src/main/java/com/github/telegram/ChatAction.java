package com.github.telegram;

import org.drinkless.td.libcore.telegram.TdApi;

public class ChatAction extends TelegramAction {
	private final AuthAction authAction;
	private final UserAction userAction;

	public ChatAction(AuthAction authAction, UserAction userAction) {
		super(authAction.context);
		this.authAction = authAction;
		this.userAction = userAction;
	}

	public void CreatePrivateChat() {
		authAction.send(new TdApi.CreatePrivateChat(402780, true), this);
	}

	public void GetChats() {
		authAction.send(new TdApi.GetChats(new TdApi.ChatListMain(), Long.MAX_VALUE, 0, 100), this);
	}

	public long getCurrentChatId() {
		return 777000;
	}

	public void getChatHistory() {
		authAction.send(new TdApi.GetChatHistory(777000, 0, 0, 10, false), this);
	}

	@Override
	public void onResult(TdApi.Object object) {
		super.onResult(object);
		switch (object.getConstructor()) {
			case TdApi.Messages.CONSTRUCTOR:
				TdApi.Messages messages = (TdApi.Messages) object;
				for (TdApi.Message message : messages.messages) {
					TdApi.MessageText text = (TdApi.MessageText) message.content;
					log(text.text.text);
				}
				break;
		}
	}
}
