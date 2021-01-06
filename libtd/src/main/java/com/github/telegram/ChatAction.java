package com.github.telegram;

import org.drinkless.td.libcore.telegram.TdApi;

public class ChatAction extends TelegramAction {
	private final AuthAction authAction;
	private long currentChatId;
	public ChatAction(AuthAction authAction) {
		this.authAction = authAction;
	}

	public void CreateNewSupergroupChat(ActionCallback<TdApi.Chat> callback) {
		TdApi.CreateNewSupergroupChat telegram_drive = new TdApi.CreateNewSupergroupChat("Telegram Drive", false, "这是一个telegram网盘", null);
		authAction.send(telegram_drive, callback);
	}

	public void GetChats(ActionCallback<TdApi.Chats> callback) {
		authAction.send(new TdApi.GetChats(new TdApi.ChatListMain(), Long.MAX_VALUE, 0, 100), callback);
	}

	public void SearchChat(String query, ActionCallback<TdApi.Chats> callback) {
		authAction.send(new TdApi.SearchChats(query, 1), callback);
	}

	public void OpenChat(long chatId, ActionCallback<TdApi.Ok> callback) {
		authAction.send(new TdApi.OpenChat(chatId), callback);
	}
	public void CloseChat(long chatId, ActionCallback<TdApi.Ok> callback) {
		authAction.send(new TdApi.CloseChat(chatId), callback);
	}

	public void GetChat(long chatId, ActionCallback<TdApi.Chat> callback) {
		authAction.send(new TdApi.GetChat(chatId), callback);
	}

	public void setCurrentChatId(long currentChatId) {
		this.currentChatId = currentChatId;
	}

	public long getCurrentChatId() {
		return currentChatId;
	}

	public void getChatHistory(ActionCallback<TdApi.Messages> callback) {
		authAction.send(new TdApi.GetChatHistory(777000, 0, 0, 10, false), callback);
	}

}
