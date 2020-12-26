package com.github.telegram;

import android.util.Log;

import org.drinkless.td.libcore.telegram.DriveFile;
import org.drinkless.td.libcore.telegram.TdApi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

	private void sendFile(long chatId, int id, String captionText, ActionCallback callback) {
		TdApi.InputFile inputFile1 = new TdApi.InputFileId(id);
		TdApi.FormattedText caption = new TdApi.FormattedText(captionText, null);
		TdApi.InputMessageContent content = new TdApi.InputMessageDocument(inputFile1, null, true, caption);
		authAction.send(TdApi.Message.CONSTRUCTOR, new TdApi.SendMessage(chatId, 0, 0, null, null, content), callback);
	}

	public void UploadFile(long chatId, File file, DriveFile driveFile, ActionCallback callback) {
		TdApi.InputFile inputFile = new TdApi.InputFileLocal(file.getAbsolutePath());
		TdApi.FileType fileType = new TdApi.FileTypeDocument();
		authAction.send(TdApi.File.CONSTRUCTOR, null, o -> {
			TdApi.File sendFile = (TdApi.File) o;
			sendFile(chatId, sendFile.id, driveFile.getCaption(), new ActionCallback() {
				@Override
				public Void call(Object o) {
					Log.i(TAG, "文件发送完成:"+o.toString());
					return null;
				}
			});
			return null;
		});
		TdApi.UploadFile uploadFile = new TdApi.UploadFile(inputFile, fileType, 32);
		authAction.send(TdApi.UpdateFile.CONSTRUCTOR, uploadFile, o -> {
			TdApi.UpdateFile updateFile = (TdApi.UpdateFile) o;
			Log.i(TAG, "upload--->>>" + ((float) updateFile.file.remote.uploadedSize / (float) updateFile.file.expectedSize));
			if (updateFile.file.remote.isUploadingCompleted) {
				Log.i(TAG, "上传完成");
				callback.call(null);
			}
			return null;
		});
	}

	public void DownloadFile(DriveFile file, ActionCallback callback) {
		TdApi.MessageDocument content = ((TdApi.MessageDocument) file.getMessage().content);
		TdApi.DownloadFile downloadFile = new TdApi.DownloadFile(content.document.document.id, 32, 0, 0, false);
		authAction.send(TdApi.UpdateFile.CONSTRUCTOR, downloadFile, o1 -> {
			TdApi.UpdateFile updateFile = (TdApi.UpdateFile) o1;
			Log.i(TAG, "download--->>>" + ((float) updateFile.file.local.downloadedSize / (float) updateFile.file.expectedSize));
			if (updateFile.file.local.isDownloadingCompleted) {
				callback.call(updateFile);
			}
			return null;
		});
	}

}
