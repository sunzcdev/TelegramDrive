package com.github.telegram;

import org.drinkless.td.libcore.telegram.DriveFile;
import org.drinkless.td.libcore.telegram.TdApi;

import java.io.File;

public class MessageAction extends TelegramAction {
	private final AuthAction authAction;
	public static final String TAG = "SUNZC";

	public MessageAction(AuthAction authAction) {
		this.authAction = authAction;
	}

	public void CreateDirectory(long chatId, String captionText, ActionCallback<TdApi.Message> callback) {
		TdApi.FormattedText text = new TdApi.FormattedText(captionText, null);
		TdApi.InputMessageContent content = new TdApi.InputMessageText(text, false, true);
		TdApi.MessageSendOptions options = new TdApi.MessageSendOptions();
		TdApi.SendMessage sendMsg = new TdApi.SendMessage(chatId, 0, 0, options, null, content);
		authAction.send(sendMsg, callback);
	}

	public void EditFile(long chatId, DriveFile file, ActionCallback<TdApi.Message> callback) {
		TdApi.FormattedText text = new TdApi.FormattedText(file.getCaption(), null);
		TdApi.EditMessageCaption sendMsg = new TdApi.EditMessageCaption(chatId, file.getId(), null, text);
		authAction.send(sendMsg, callback);
	}

	public void DeleteMessages(long chatId, DriveFile[] files, ActionCallback<TdApi.Ok> callback) {
		long[] messageIds = new long[files.length];
		for (int i = 0; i < files.length; i++) {
			messageIds[i] = files[i].getId();
		}
		TdApi.DeleteMessages sendMsg = new TdApi.DeleteMessages(chatId, messageIds, true);
		authAction.send(sendMsg, callback);
	}

	private void listLast(long chatId, ActionCallback<TdApi.Message> callback) {
//		TdApi.UpdateChatLastMessage function = new TdApi.GetChat(chatId);
//		authAction.send(TdApi.Chat.CONSTRUCTOR, function, new ActionCallback() {
//			@Override
//			public Void call(Object o) {
//				TdApi.Chat chat = (TdApi.Chat) o;
//				return null;
//			}
//		});
	}

	private void list(int userId, long chatId, String query, ActionCallback<TdApi.Message> callback) {
		TdApi.SearchMessagesFilterEmpty filter = new TdApi.SearchMessagesFilterEmpty();
		TdApi.MessageSender sender = new TdApi.MessageSenderUser(userId);
		TdApi.SearchChatMessages function = new TdApi.SearchChatMessages(chatId, query, sender, 0, 0, 100, filter, 0);
		authAction.send(function, callback);
	}

	public void listAll(int userId, long chatId, DriveFile directory, ActionCallback<TdApi.Message> callback) {
		list(userId, chatId, directory.listAllChildren(), callback);
	}

	public void listFile(int userId, long chatId, DriveFile directory, ActionCallback<TdApi.Message> callback) {
		list(userId, chatId, directory.listChildrenFiles(), callback);
	}

	public void listDir(int userId, long chatId, DriveFile directory, ActionCallback<TdApi.Message> callback) {
		list(userId, chatId, directory.listChildrenDirs(), callback);
	}

	private void sendFile(long chatId, int id, ActionCallback<TdApi.Message> callback) {
		TdApi.InputFile inputFile1 = new TdApi.InputFileId(id);
		TdApi.InputMessageContent content = new TdApi.InputMessageDocument(inputFile1, null, true, null);
		authAction.send(new TdApi.SendMessage(chatId, 0, 0, null, null, content), callback);
	}

	public void UploadFile(long chatId, File localFile, ActionCallback<TdApi.UpdateFile> callback) {
		TdApi.InputFile inputFile = new TdApi.InputFileLocal(localFile.getAbsolutePath());
		TdApi.FileType fileType = new TdApi.FileTypeDocument();
		TdApi.UploadFile uploadFile = new TdApi.UploadFile(inputFile, fileType, 32);
		authAction.send(uploadFile, new ActionCallback<TdApi.File>() {
			@Override
			public void toObject(TdApi.File file) {
				authAction.registerUpdateListener(new ActionCallback<TdApi.Update>() {
					@Override
					public void toObject(TdApi.Update update) {
						if (update instanceof TdApi.UpdateFile) {
							TdApi.RemoteFile remoteFile = ((TdApi.UpdateFile) update).file.remote;
							TdApi.File localFile = ((TdApi.UpdateFile) update).file;
							if (file.id == localFile.id && remoteFile.isUploadingCompleted) {
								authAction.unregisterUpdateListener(this);
								callback.toObject((TdApi.UpdateFile) update);
							}
						}
					}
				});
				sendFile(chatId, file.id, new ActionCallback<TdApi.Message>() {
					@Override
					public void toObject(TdApi.Message message) {

					}
				});
			}
		});
	}

	public void GetRemoteFile(String remoteId, ActionCallback<TdApi.File> callback) {
		TdApi.FileType type = new TdApi.FileTypeDocument();
		TdApi.GetRemoteFile remoteFile = new TdApi.GetRemoteFile(remoteId, type);
		authAction.send(remoteFile, callback);
	}

	public void DownloadFile(String remoteId, ActionCallback<TdApi.UpdateFile> callback) {
		GetRemoteFile(remoteId, new ActionCallback<TdApi.File>() {
			@Override
			public void toObject(TdApi.File file) {
				TdApi.DownloadFile downloadFile = new TdApi.DownloadFile(file.id, 32, 0, 0, false);
				authAction.registerUpdateListener(new ActionCallback<TdApi.Update>() {
					@Override
					public void toObject(TdApi.Update update) {
						if (update instanceof TdApi.UpdateFile) {
							TdApi.RemoteFile remoteFile = ((TdApi.UpdateFile) update).file.remote;
							TdApi.LocalFile localFile = ((TdApi.UpdateFile) update).file.local;
							if (remoteId.equals(remoteFile.id) && localFile.isDownloadingCompleted) {
								authAction.registerUpdateListener(this);
								callback.toObject((TdApi.UpdateFile) update);
							}
						}
					}
				});
				authAction.send(downloadFile, new ActionCallback<TdApi.File>() {
					@Override
					public void toObject(TdApi.File file) {

					}
				});
			}
		});
	}


	private int getFileId(TdApi.MessageContent content) {
		if (content instanceof TdApi.MessageDocument) {
			return ((TdApi.MessageDocument) content).document.document.id;
		} else if (content instanceof TdApi.MessagePhoto) {
			return ((TdApi.MessagePhoto) content).photo.sizes[0].photo.id;
		} else if (content instanceof TdApi.MessageVideo) {
			return ((TdApi.MessageVideo) content).video.video.id;
		} else if (content instanceof TdApi.MessageAnimation) {
			return ((TdApi.MessageAnimation) content).animation.animation.id;
		} else if (content instanceof TdApi.MessageAudio) {
			return ((TdApi.MessageAudio) content).audio.audio.id;
		}
		return -1;
	}
}
