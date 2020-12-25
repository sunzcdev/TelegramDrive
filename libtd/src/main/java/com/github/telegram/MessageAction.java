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

	public void CreateDirectory(long chatId, String captionText, ActionCallback callback) {
		TdApi.FormattedText text = new TdApi.FormattedText(captionText, null);
		TdApi.InputMessageContent content = new TdApi.InputMessageText(text, false, true);
		TdApi.MessageSendOptions options = new TdApi.MessageSendOptions();
		TdApi.SendMessage sendMsg = new TdApi.SendMessage(chatId, 0, 0, options, null, content);
		authAction.send(TdApi.Message.CONSTRUCTOR, sendMsg, callback);
	}

	public void EditDir(long chatId, String oldCaptionText, String captionText, ActionCallback callback) {

	}

	public void EditFile(long chatId, long fileId, String captionText, ActionCallback callback) {
		TdApi.FormattedText text = new TdApi.FormattedText(captionText, null);
		TdApi.EditMessageCaption sendMsg = new TdApi.EditMessageCaption(chatId, fileId, null, text);
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

	private void list(long chatId, String query, ActionCallback callback) {
		TdApi.SearchMessagesFilterEmpty filter = new TdApi.SearchMessagesFilterEmpty();
		TdApi.SearchChatMessages function = new TdApi.SearchChatMessages(chatId, query, null, 0, 0, 10, filter, 0);
		authAction.send(TdApi.Messages.CONSTRUCTOR, function, callback);
	}

	public void listAll(long chatId, DriveFile directory, ActionCallback callback) {
		list(chatId, directory.listAllChildren(), callback);
	}

	public void listFile(long chatId, DriveFile directory, ActionCallback callback) {
		list(chatId, directory.listChildrenFiles(), callback);
	}

	public void listDir(long chatId, DriveFile directory, ActionCallback callback) {
		list(chatId, directory.listChildrenDirs(), callback);
	}

	public void DownloadFile(DriveFile file, ActionCallback callback) {
		TdApi.MessageDocument content = ((TdApi.MessageDocument) file.getMessage().content);
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

}
