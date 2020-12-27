package com.github.telegram;

import android.util.Log;

import org.drinkless.td.libcore.telegram.DriveFile;
import org.drinkless.td.libcore.telegram.TdApi;

import java.io.File;

public class MessageAction extends TelegramAction {
	private final AuthAction authAction;
	public static final String TAG = "SUNZC";

	public MessageAction(AuthAction authAction) {
		this.authAction = authAction;
	}

	public void CreateDirectory(long chatId, String captionText, ActionCallback callback) {
		TdApi.FormattedText text = new TdApi.FormattedText(captionText, null);
		TdApi.InputMessageContent content = new TdApi.InputMessageText(text, false, true);
		TdApi.MessageSendOptions options = new TdApi.MessageSendOptions();
		TdApi.SendMessage sendMsg = new TdApi.SendMessage(chatId, 0, 0, options, null, content);
		authAction.send(TdApi.Message.CONSTRUCTOR, sendMsg, callback);
	}

	public void EditDir(long chatId, String oldCaptionText, String captionText, ActionCallback callback) {

	}

	public void EditFile(long chatId, DriveFile file, ActionCallback callback) {
		TdApi.FormattedText text = new TdApi.FormattedText(file.getCaption(), null);
		TdApi.EditMessageCaption sendMsg = new TdApi.EditMessageCaption(chatId, file.getId(), null, text);
		authAction.send(TdApi.Message.CONSTRUCTOR, sendMsg, callback);
	}

	public void DeleteMessages(long chatId, DriveFile[] files, ActionCallback callback) {
		long[] messageIds = new long[files.length];
		for (int i = 0; i < files.length; i++) {
			messageIds[i] = files[i].getId();
		}
		TdApi.DeleteMessages sendMsg = new TdApi.DeleteMessages(chatId, messageIds, true);
		authAction.send(TdApi.Ok.CONSTRUCTOR, sendMsg, callback);
	}

	private void listLast(long chatId, ActionCallback callback) {
//		TdApi.UpdateChatLastMessage function = new TdApi.GetChat(chatId);
//		authAction.send(TdApi.Chat.CONSTRUCTOR, function, new ActionCallback() {
//			@Override
//			public Void call(Object o) {
//				TdApi.Chat chat = (TdApi.Chat) o;
//				return null;
//			}
//		});
	}

	private void list(int userId, long chatId, String query, ActionCallback callback) {
		TdApi.SearchMessagesFilterEmpty filter = new TdApi.SearchMessagesFilterEmpty();
		TdApi.MessageSender sender = new TdApi.MessageSenderUser(userId);
		TdApi.SearchChatMessages function = new TdApi.SearchChatMessages(chatId, query, sender, 0, 0, 100, filter, 0);
		authAction.send(TdApi.Messages.CONSTRUCTOR, function, callback);
	}

	public void listAll(int userId, long chatId, DriveFile directory, ActionCallback callback) {
		list(userId, chatId, directory.listAllChildren(), callback);
	}

	public void listFile(int userId, long chatId, DriveFile directory, ActionCallback callback) {
		list(userId, chatId, directory.listChildrenFiles(), callback);
	}

	public void listDir(int userId, long chatId, DriveFile directory, ActionCallback callback) {
		list(userId, chatId, directory.listChildrenDirs(), callback);
	}

	private void sendFile(long chatId, int id, ActionCallback callback) {
		TdApi.InputFile inputFile1 = new TdApi.InputFileId(id);
		TdApi.InputMessageContent content = new TdApi.InputMessageDocument(inputFile1, null, true, null);
		authAction.send(TdApi.Message.CONSTRUCTOR, new TdApi.SendMessage(chatId, 0, 0, null, null, content), callback);
	}

	public void UploadFile(long chatId, File file, ActionCallback callback) {
		TdApi.InputFile inputFile = new TdApi.InputFileLocal(file.getAbsolutePath());
		TdApi.FileType fileType = new TdApi.FileTypeDocument();
		authAction.send(TdApi.File.CONSTRUCTOR, null, o -> {
			TdApi.File file1 = (TdApi.File) o;
			sendFile(chatId, file1.id, null);
			return null;
		});
		authAction.send(TdApi.UpdateMessageSendSucceeded.CONSTRUCTOR, null, callback);
		TdApi.UploadFile uploadFile = new TdApi.UploadFile(inputFile, fileType, 32);
		authAction.send(TdApi.UploadFile.CONSTRUCTOR, uploadFile, o -> {
			TdApi.UpdateFile updateFile = (TdApi.UpdateFile) o;
			Log.i(TAG, "upload--->>>" + ((float) updateFile.file.remote.uploadedSize / (float) updateFile.file.expectedSize));
			if (updateFile.file.remote.isUploadingCompleted) {
				Log.i(TAG, "上传完成");
			}
			return null;
		});
	}

	public void GetRemoteFile(String remoteId, ActionCallback callback) {
		TdApi.FileType type = new TdApi.FileTypeDocument();
		TdApi.GetRemoteFile remoteFile = new TdApi.GetRemoteFile(remoteId, type);
		authAction.send(TdApi.File.CONSTRUCTOR, remoteFile, callback);
	}

	public void DownloadFile(String remoteId, ActionCallback callback) {
		GetRemoteFile(remoteId, o -> {
			TdApi.File file = (TdApi.File) o;
			TdApi.DownloadFile downloadFile = new TdApi.DownloadFile(file.id, 32, 0, 0, false);
			authAction.send(TdApi.UpdateFile.CONSTRUCTOR, downloadFile, o1 -> {
				TdApi.UpdateFile updateFile = (TdApi.UpdateFile) o1;
				Log.i(TAG, "download--->>>" + ((float) updateFile.file.local.downloadedSize / (float) updateFile.file.expectedSize));
				if (updateFile.file.local.isDownloadingCompleted) {
					callback.call(updateFile);
					return null;
				}
				return null;
			});
			return null;
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
