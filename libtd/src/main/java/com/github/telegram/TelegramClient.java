package com.github.telegram;

import android.util.Log;

import com.github.drive.Callback;
import com.github.utils.LogUtils;

import org.drinkless.td.libcore.telegram.DriveFile;
import org.drinkless.td.libcore.telegram.TdApi;

import java.io.File;
import java.util.Arrays;

public class TelegramClient {
	private static final String TAG = "client";

	public final AuthAction auth;
	public final ChatAction chat;
	public final MessageAction message;
	private final UserAction user;
	private long chatId;
	private int userId;

	public TelegramClient(TdApi.TdlibParameters parameters) {
		auth = new AuthAction(parameters);
		user = new UserAction(auth);
		chat = new ChatAction(auth);
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

	public void getDriveChat(ActionCallback callback) {
		chat.GetChats(o -> {
			TdApi.Chats chats = (TdApi.Chats) o;
			if (chats.totalCount > 0) {
				chat.SearchChat("Telegram drive", o1 -> {
					TdApi.Chats chats1 = (TdApi.Chats) o1;
					if (chats1.totalCount > 0) {
						chat.GetChat(chats1.chatIds[0], o2 -> {
							TdApi.Chat currentChat = (TdApi.Chat) o2;
							chatId = currentChat.id;
							user.GetMe(o3 -> {
								TdApi.User user = (TdApi.User) o3;
								userId = user.id;
								callback.call(currentChat);
								return null;
							});
							return null;
						});
					}
					return null;
				});
			}
			return null;
		});
	}

	public void listFiles(DriveFile directory, Callback<DriveFile[], Void> driveFileVoidCallback) {
		message.listAll(userId, chatId, directory, o -> {
			TdApi.Messages messages = (TdApi.Messages) o;
			int size = messages.messages.length;
			DriveFile[] files = new DriveFile[size];
			if (size > 0) {
				for (int i = 0; i < size; i++) {
					TdApi.Message message = messages.messages[i];
					files[i] = new DriveFile(message);
				}
			}
			LogUtils.printArr(TAG + "--文件", files);
			driveFileVoidCallback.call(files);
			return null;
		});
	}

	public void uploadFile(File localFile, DriveFile destDir, ActionCallback callback) {
		DriveFile file = new DriveFile(localFile);
		file.move(destDir);
		message.UploadFile(chatId, localFile, file, callback);
	}

	public void downloadFile(DriveFile file, ActionCallback callback) {
		message.DownloadFile(file, callback);
	}

	public void move(DriveFile file, DriveFile destDir, ActionCallback callback) {
		file.move(destDir);
		message.EditFile(chatId, file, callback);
	}

	public void copy(DriveFile file, DriveFile destDir) {
	}

	public void copyDir(DriveFile dir, DriveFile destDir) {
	}

	public void rename(DriveFile file, String name, ActionCallback callback) {
		file.rename(name);
		message.EditFile(chatId, file, callback);
	}

	public void delete(DriveFile file, ActionCallback callback) {
		message.DeleteMessages(chatId, new DriveFile[]{file}, callback);
	}

}
