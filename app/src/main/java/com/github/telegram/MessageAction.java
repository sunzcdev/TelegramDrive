package com.github.telegram;

import org.drinkless.td.libcore.telegram.TdApi;

public class MessageAction extends TelegramAction {
	private final ClientAction clientAction;
	private final AuthAction authAction;
	private final UserAction userAction;
	private final ChatAction chatAction;

	public MessageAction(ClientAction clientAction, AuthAction authAction, UserAction userAction, ChatAction chatAction) {
		super(clientAction.context);
		this.clientAction = clientAction;
		this.authAction = authAction;
		this.userAction = userAction;
		this.chatAction = chatAction;
	}

	public void SendMessage() {
		TdApi.InlineKeyboardButton[] row = {
				new TdApi.InlineKeyboardButton("https://telegram.org?1",
						new TdApi.InlineKeyboardButtonTypeUrl()),
				new TdApi.InlineKeyboardButton("https://telegram.org?2",
						new TdApi.InlineKeyboardButtonTypeUrl()),
				new TdApi.InlineKeyboardButton("https://telegram.org?3",
						new TdApi.InlineKeyboardButtonTypeUrl())};
		TdApi.ReplyMarkup replyMarkup = new TdApi.ReplyMarkupInlineKeyboard(new TdApi.InlineKeyboardButton[][]{row, row, row});
		TdApi.InputMessageContent content = new TdApi.InputMessageText(new TdApi.FormattedText("s", null), false, true);
		clientAction.send(new TdApi.SendMessage(chatAction.getCurrentChatId(), 0, 0, null, replyMarkup, content), this);
	}
}
