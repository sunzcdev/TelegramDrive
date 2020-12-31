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
	private Context context;

	public TelegramClient(Context context, TdApi.TdlibParameters parameters) {
		this.context = context;
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

	public void login() {
		auth.create();
	}

	public void SetAuthenticationPhoneNumber(String phoneNumber) {
		auth.SetAuthenticationPhoneNumber(phoneNumber);
	}

	public void CheckAuthenticationCode(String code) {
		auth.CheckAuthenticationCode(code);
	}

	public void CheckAuthenticationPassword(String password) {
		auth.CheckAuthenticationPassword(password);
	}

	public void getDriveChat(ActionCallback<TdApi.Ok> callback) {
//		if (chatId != -1) {
//			chat.OpenChat(chatId, callback);
//			return;
//		}
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
										SharedPreferences.Editor editor = sp.edit();
										editor.putLong("chat_id", chatId);
										editor.apply();
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

	public void uploadFile(File localFile, ActionCallback<String> callback) {
		chat.OpenChat(chatId, new ActionCallback<TdApi.Ok>() {
			@Override
			public void toObject(TdApi.Ok ok) {
				String caption = System.currentTimeMillis() + "";
				message.UploadFile(chatId, localFile, caption, new ProgressListener<TdApi.Message, TdApi.RemoteFile>() {
					@Override
					public void onStart(TdApi.Message message) {
						ViewUtils.toast(context, localFile.getName() + "开始上传");
					}

					@Override
					public void onProgress(float progress) {
						ViewUtils.toast(context, localFile.getName() + " 上传进度:" + (int) (progress * 100) + "%");
					}

					@Override
					public void onStop(TdApi.RemoteFile remoteFile) {
						ViewUtils.toast(context, localFile.getName() + " 上传完成");
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

	public void downloadFile(String remoteId, ProgressListener<File, File> callback) {
		message.DownloadFile(chatId, remoteId, callback);
	}

	public void copy(DriveFile file, DriveFile destDir) {
	}

	public void copyDir(DriveFile dir, DriveFile destDir) {
	}

	public void delete(DriveFile file, ActionCallback<TdApi.Ok> callback) {
		message.DeleteMessages(chatId, new DriveFile[]{file}, callback);
	}

	public void setLoginListener(LoginListener loginListener) {
		auth.setLoginListener(loginListener);
	}
}
