package com.github.telegram;

import android.util.Log;

import org.drinkless.td.libcore.telegram.TdApi;

import java.io.File;

public class MessageAction extends TelegramAction {
	private final AuthAction authAction;
	public static final String TAG = "SUNZC";

	public MessageAction(AuthAction authAction) {
		this.authAction = authAction;
	}

	private void sendFile(long chatId, int id, String captionText, ActionCallback callback) {
		TdApi.InputFile inputFile1 = new TdApi.InputFileId(id);
		TdApi.FormattedText caption = new TdApi.FormattedText(captionText, null);
		TdApi.InputMessageContent content = new TdApi.InputMessageDocument(inputFile1, null, false, caption);
		authAction.send(TdApi.Message.CONSTRUCTOR, new TdApi.SendMessage(chatId, 0, 0, null, null, content), callback);
	}

	public void UploadFile(long chatId, File file, String captionText, ActionCallback callback) {
		TdApi.InputFile inputFile = new TdApi.InputFileLocal(file.getAbsolutePath());
		TdApi.FileType fileType = new TdApi.FileTypeDocument();
		authAction.send(TdApi.File.CONSTRUCTOR, null, o -> {
			TdApi.File sendFile = (TdApi.File) o;
			sendFile(chatId, sendFile.id, captionText, callback);
			return null;
		});
		TdApi.UploadFile uploadFile = new TdApi.UploadFile(inputFile, fileType, 1);
		authAction.send(TdApi.UpdateFile.CONSTRUCTOR, uploadFile, o -> {
			TdApi.UpdateFile updateFile = (TdApi.UpdateFile) o;
			Log.i(TAG, "upload--->>>" + (updateFile.file.size / updateFile.file.expectedSize));
			return null;
		});
	}

	private void searchFile(long chatId, String query, ActionCallback callback) {
		TdApi.SearchMessagesFilter filter = new TdApi.SearchMessagesFilterDocument();
		TdApi.SearchChatMessages function = new TdApi.SearchChatMessages(chatId, query, null, 0, 0, 10, filter, 0);
		authAction.send(TdApi.Messages.CONSTRUCTOR, function, callback);
	}

	public void DownloadFile(long chatId, String query, ActionCallback callback) {
		searchFile(chatId, query, o -> {
			TdApi.Messages messages = (TdApi.Messages) o;
			if (messages.totalCount > 0) {
				TdApi.MessageDocument content = ((TdApi.MessageDocument) messages.messages[0].content);
				TdApi.DownloadFile downloadFile = new TdApi.DownloadFile(content.document.document.id, 2, 0, 0, false);
				authAction.send(TdApi.UpdateFile.CONSTRUCTOR, downloadFile, o1 -> {
					TdApi.UpdateFile updateFile = (TdApi.UpdateFile) o1;
					Log.i(TAG, "download--->>>" + ((float) updateFile.file.local.downloadedSize / (float) updateFile.file.expectedSize));
					if (updateFile.file.local.isDownloadingCompleted) {
						callback.call(updateFile);
					}
					return null;
				});
			}
			return null;
		});

	}
}
