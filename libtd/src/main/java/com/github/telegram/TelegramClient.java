package com.github.telegram;

import com.github.drive.Callback;
import com.github.utils.LogUtils;

import org.drinkless.td.libcore.telegram.DriveFile;
import org.drinkless.td.libcore.telegram.TdApi;

import java.io.File;

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

	public void getDriveChat(ActionCallback<TdApi.Ok> callback) {
		chat.GetChats(new ActionCallback<TdApi.Chats>() {
			@Override
			public void toObject(TdApi.Chats chats) {
				if (chats.totalCount > 0) {
					chat.SearchChat("Telegram drive", new ActionCallback<TdApi.Chats>() {
						@Override
						public void toObject(TdApi.Chats chats1) {
							if (chats1.totalCount > 0) {
								chat.GetChat(chats1.chatIds[0], new ActionCallback<TdApi.Chat>() {
									@Override
									public void toObject(TdApi.Chat currentChat) {
										chatId = currentChat.id;
										chat.OpenChat(chatId, callback);
									}
								});
							} else {
								chat.CreateNewSupergroupChat(new ActionCallback<TdApi.Chat>() {
									@Override
									public void toObject(TdApi.Chat currentChat) {
										chatId = currentChat.id;
										chat.OpenChat(chatId, callback);
									}
								});
							}
						}
					});
				}
			}
		});
	}

	public void listFiles(DriveFile directory, Callback<DriveFile[], Void> driveFileVoidCallback) {
		chat.OpenChat(chatId, new ActionCallback<TdApi.Ok>() {
			@Override
			public void toObject(TdApi.Ok ok) {
				message.listAll(chatId, directory, new ActionCallback<TdApi.Messages>() {
					@Override
					public void toObject(TdApi.Messages messages) {
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
					}
				});
			}
		});
	}

	public void uploadFile(File localFile, DriveFile destDir, ActionCallback<String> callback) {
		DriveFile file = new DriveFile(localFile);
		file.move(destDir);
		chat.OpenChat(chatId, new ActionCallback<TdApi.Ok>() {
			@Override
			public void toObject(TdApi.Ok ok) {
				String caption = System.currentTimeMillis() + "";
				message.UploadFile(chatId, localFile, file, new ProgressListener<TdApi.Message, TdApi.RemoteFile>() {
					@Override
					public void onStart(TdApi.Message message) {
					}

					@Override
					public void onProgress(float progress) {
					}

					@Override
					public void onStop(TdApi.RemoteFile remoteFile) {
						chat.CloseChat(chatId, new ActionCallback<TdApi.Ok>() {
							@Override
							public void toObject(TdApi.Ok ok) {
								callback.toObject(caption);
							}
						});
					}
				});
			}
		});
	}

	public void downloadFile(DriveFile file, ProgressListener<File, File> listener) {
		message.DownloadFile(chatId, file, listener);
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
