package com.github.telegram;

import org.drinkless.td.libcore.telegram.TdApi;

public class ChatAction extends TelegramAction {
	private final AuthAction authAction;
	private long currentChatId;
	public ChatAction(AuthAction authAction) {
		this.authAction = authAction;
	}

	public void CreatePrivateChat(ActionCallback callback) {
		authAction.send(TdApi.Chat.CONSTRUCTOR, new TdApi.CreatePrivateChat(402780, true), callback);
	}

	public void GetChats(ActionCallback callback) {
		authAction.send(TdApi.Chats.CONSTRUCTOR, new TdApi.GetChats(new TdApi.ChatListMain(), Long.MAX_VALUE, 0, 100), callback);
	}

	public void SearchChat(String query, ActionCallback callback) {
		authAction.send(TdApi.Chats.CONSTRUCTOR, new TdApi.SearchChats(query, 1), callback);
	}

	public void GetChat(long chatId, ActionCallback callback) {
		authAction.send(TdApi.Chat.CONSTRUCTOR, new TdApi.GetChat(chatId), callback);
	}

	public void setCurrentChatId(long currentChatId) {
		this.currentChatId = currentChatId;
	}

	public long getCurrentChatId() {
		return currentChatId;
	}

	public void getChatHistory(ActionCallback callback) {
		authAction.send(TdApi.Messages.CONSTRUCTOR, new TdApi.GetChatHistory(777000, 0, 0, 10, false), callback);
	}

}