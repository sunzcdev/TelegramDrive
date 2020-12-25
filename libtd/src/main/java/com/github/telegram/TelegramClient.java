package com.github.telegram;

import com.github.drive.Callback;

import org.drinkless.td.libcore.telegram.DriveFile;
import org.drinkless.td.libcore.telegram.TdApi;

import java.io.File;

public class TelegramClient {
	private static final String TAG = "client";

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

	public void listFiles(DriveFile directory, Callback<DriveFile[], Void> driveFileVoidCallback) {
		message.listAll(chat.getCurrentChatId(), directory, o -> {
			TdApi.Messages messages = (TdApi.Messages) o;
			DriveFile[] files = new DriveFile[messages.totalCount];
			if (messages.totalCount > 0) {
				for (int i = 0; i < messages.messages.length; i++) {
					files[i] = new DriveFile(messages.messages[i]);
				}
			}
			driveFileVoidCallback.call(files);
			return null;
		});
	}

	public void getDriveChat(ActionCallback callback) {
		chat.GetChats(o -> {
			TdApi.Chats chats = (TdApi.Chats) o;
			if (chats.totalCount > 0) {
				chat.SearchChat("Telegram drive", o1 -> {
					TdApi.Chats chats1 = (TdApi.Chats) o1;
					if (chats1.totalCount > 0) {
						chat.GetChat(chats1.chatIds[0], o2 -> {
							TdApi.Chat currentChat = (TdApi.Chat) o2;
							chat.setCurrentChatId(currentChat.id);
							callback.call(currentChat);
							return null;
						});
					}
					return null;
				});
			}
			return null;
		});
	}

	private void uploadFile(File localFile, DriveFile destDir, ActionCallback callback) {
		DriveFile file = new DriveFile(localFile);
		file.move(destDir);
		message.UploadFile(chat.getCurrentChatId(), localFile, file.getCaption(), callback);
	}

	private void downloadFile(DriveFile file, ActionCallback callback) {
		message.DownloadFile(file, callback);
	}

	public void move(DriveFile file, DriveFile destDir, ActionCallback callback) {
		file.move(destDir);
		message.EditFile(chat.getCurrentChatId(), file.getId(), file.getCaption(), callback);
	}

	public void copy(DriveFile file, DriveFile destDir) {
	}

	public void copyDir(DriveFile dir, DriveFile destDir) {
	}

	public void rename(DriveFile file, String name, ActionCallback callback) {
		file.rename(name);
		message.EditFile(chat.getCurrentChatId(), file.getId(), file.getCaption(), callback);
	}

	public void delete(DriveFile file, ActionCallback callback) {
		message.DeleteMessages(chat.getCurrentChatId(), new DriveFile[]{file}, callback);
	}

}
