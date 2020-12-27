package com.github.telegram;

import android.content.Context;
import android.content.SharedPreferences;

import org.drinkless.td.libcore.telegram.DriveFile;
import org.drinkless.td.libcore.telegram.TdApi;

import java.io.File;

import static android.content.Context.MODE_PRIVATE;

public class TelegramClient {
	private static final String TAG = "client";

	public final AuthAction auth;
	public final ChatAction chat;
	public final MessageAction message;
	private final SharedPreferences sp;
	private long chatId;

	public TelegramClient(Context context, TdApi.TdlibParameters parameters) {
		sp = context.getSharedPreferences("tel_drive", MODE_PRIVATE);
		if (sp.contains("chat_id")) {
			chatId = sp.getLong("chat_id", -1);
		}
		auth = new AuthAction(parameters);
		chat = new ChatAction(auth);
		message = new MessageAction(auth);
	}

	public static TelegramClient client;

	public static TelegramClient create(Context context, TdApi.TdlibParameters parameters) {
		if (client == null) {
			return new TelegramClient(context, parameters);
		} else {
			return client;
		}
	}

	public void getDriveChat(ActionCallback callback) {
//		if (chatId != -1) {
//			chat.OpenChat(chatId, callback);
//			return;
//		}
		chat.GetChats(o -> {
			TdApi.Chats chats = (TdApi.Chats) o;
			if (chats.totalCount > 0) {
				chat.SearchChat("Telegram drive", o1 -> {
					TdApi.Chats chats1 = (TdApi.Chats) o1;
					if (chats1.totalCount > 0) {
						chat.GetChat(chats1.chatIds[0], o2 -> {
							TdApi.Chat currentChat = (TdApi.Chat) o2;
							chatId = currentChat.id;
							SharedPreferences.Editor editor = sp.edit();
							editor.putLong("chat_id", chatId);
							editor.apply();
							chat.OpenChat(chatId, callback);
							return null;
						});
					}
					return null;
				});
			}
			return null;
		});
	}

	public void uploadFile(File localFile, ActionCallback callback) {
		chat.OpenChat(chatId, o -> {
			message.UploadFile(chatId, localFile, o12 -> {
				chat.CloseChat(chatId, o1 -> {
					if (callback != null)
						callback.call(o12);
					return null;
				});
				return null;
			});
			return null;
		});
	}

	public void downloadFile(String remoteId, ActionCallback callback) {
		chat.OpenChat(chatId, o -> {
			message.DownloadFile(remoteId, new ActionCallback() {
				@Override
				public Boolean call(Object o) {
					chat.CloseChat(chatId, new ActionCallback() {
						@Override
						public Boolean call(Object o1) {
							if (callback != null)
								callback.call(o);
							return null;
						}
					});
					return null;
				}
			});
			return null;
		});
	}

	public void copy(DriveFile file, DriveFile destDir) {
	}

	public void copyDir(DriveFile dir, DriveFile destDir) {
	}

	public void delete(DriveFile file, ActionCallback callback) {
		message.DeleteMessages(chatId, new DriveFile[]{file}, callback);
	}

}
