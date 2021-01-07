package com.github.telegram;

import org.drinkless.td.libcore.telegram.DriveFile;
import org.drinkless.td.libcore.telegram.TdApi;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

	public void EditDir(long chatId, String oldCaptionText, String captionText, ActionCallback callback) {

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

	private List<TdApi.Message> historyMessages = new ArrayList<>();

	private void list(long chatId, String query, ActionCallback<List<TdApi.Message>> callback) {
		TdApi.SearchMessagesFilterEmpty filter = new TdApi.SearchMessagesFilterEmpty();
		TdApi.SearchMessages function = new TdApi.SearchMessages(null,query,0,0,0,100,filter,0,0);
//		TdApi.SearchChatMessages function = new TdApi.SearchChatMessages(chatId,query,null,0,0,100,filter,0);
		authAction.send(function, new ActionCallback<TdApi.Messages>() {
			@Override
			public void toObject(TdApi.Messages messages) {
				List<TdApi.Message> results = new ArrayList<>();
				if (messages.totalCount > 0) {
					Collections.addAll(results, messages.messages);
				}
				if (!historyMessages.isEmpty()) {
					log("历史数据中有" + historyMessages.size() + "条");
					for (TdApi.Message historyMessage : historyMessages) {
						TdApi.FormattedText caption = getCaption(historyMessage.content);
						if (caption != null && caption.text.contains(query)) {
							results.add(historyMessage);
						}
					}
				}
				callback.toObject(results);
			}
		});
	}


	public void listAll(long chatId, DriveFile directory, ActionCallback<List<TdApi.Message>> callback) {
		list(chatId, directory.listAllChildren(), callback);
	}

	public void listFile(long chatId, DriveFile directory, ActionCallback<List<TdApi.Message>> callback) {
		list(chatId, directory.listChildrenFiles(), callback);
	}

	public void listDir(long chatId, DriveFile directory, ActionCallback<List<TdApi.Message>> callback) {
		list(chatId, directory.listChildrenDirs(), callback);
	}

	private void sendFile(long chatId, int id, String captionText, ActionCallback<TdApi.Message> callback) {
		TdApi.InputFile inputFile1 = new TdApi.InputFileId(id);
		TdApi.FormattedText caption = new TdApi.FormattedText(captionText, null);
		TdApi.InputMessageContent content = new TdApi.InputMessageDocument(inputFile1, null, true, caption);
		authAction.send(new TdApi.SendMessage(chatId, 0, 0, null, null, content), callback);
	}

	public void UploadFile(long chatId, File localFile, DriveFile driveFile, ProgressListener<TdApi.Message, TdApi.File> listener) {
		TdApi.InputFile inputFile = new TdApi.InputFileLocal(localFile.getAbsolutePath());
		TdApi.FileType fileType = new TdApi.FileTypeDocument();
		TdApi.UploadFile uploadFile = new TdApi.UploadFile(inputFile, fileType, 32);
		authAction.send(uploadFile, new ActionCallback<TdApi.File>() {
			@Override
			public void toObject(TdApi.File file) {
				authAction.registerUpdateListener(new ActionCallback<TdApi.Update>() {
					@Override
					public void toObject(TdApi.Update update) {
						if (update instanceof TdApi.UpdateMessageSendSucceeded) {
							TdApi.UpdateMessageSendSucceeded message = (TdApi.UpdateMessageSendSucceeded) update;
							TdApi.FormattedText caption = getCaption(message.message.content);
							if (caption != null && caption.text.equals(driveFile.getCaption())) {
								authAction.unregisterUpdateListener(this);
								Collections.addAll(historyMessages, message.message);
								listener.onStop(file);
							}
						}
					}
				});
				authAction.registerUpdateListener(new ActionCallback<TdApi.Update>() {
					@Override
					public void toObject(TdApi.Update update) {
						if (update instanceof TdApi.UpdateFile) {
							TdApi.RemoteFile remoteFile = ((TdApi.UpdateFile) update).file.remote;
							TdApi.File file1 = ((TdApi.UpdateFile) update).file;
							if (file.id == file1.id) {
								if (file1.remote.isUploadingCompleted) {
									authAction.unregisterUpdateListener(this);
								} else {
									listener.onProgress((float) remoteFile.uploadedSize / (float) file.expectedSize);
								}
							}
						}
					}
				});
				sendFile(chatId, file.id, driveFile.getCaption(), new ActionCallback<TdApi.Message>() {
					@Override
					public void toObject(TdApi.Message message) {
						listener.onStart(message);
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

	public void DownloadFile(long chatId, DriveFile file, ProgressListener<File, File> callback) {
		list(chatId, file.getCaption(), new ActionCallback<List<TdApi.Message>>() {
			@Override
			public void toObject(List<TdApi.Message> messages) {
				if (messages.isEmpty()) {
					return;
				}
				TdApi.Message msg = messages.get(0);
				int fileId = getFileId(msg.content);
				if (fileId == 0) return;
				TdApi.DownloadFile downloadFile = new TdApi.DownloadFile(fileId, 32, 0, 0, false);
				authAction.registerUpdateListener(new ActionCallback<TdApi.Update>() {
					@Override
					public void toObject(TdApi.Update update) {
						if (update instanceof TdApi.UpdateFile) {
							TdApi.File file = ((TdApi.UpdateFile) update).file;
							TdApi.LocalFile localFile = ((TdApi.UpdateFile) update).file.local;
							if (file.id == fileId) {
								File tempFile = new File(file.local.path);
								if (localFile.isDownloadingCompleted) {
									authAction.registerUpdateListener(this);
									callback.onStop(tempFile);
								} else {
									if ((float) localFile.downloadedSize / (float) file.expectedSize == 100.0) {
										callback.onStop(tempFile);
									} else {
										callback.onProgress((float) localFile.downloadedSize / (float) file.expectedSize);
									}
								}
							}
						}
					}
				});
				authAction.send(downloadFile, new ActionCallback<TdApi.File>() {
					@Override
					public void toObject(TdApi.File file) {
						File localFile = new File(file.local.path);
						if (file.local.isDownloadingCompleted) {
							callback.onStop(localFile);
						} else {
							callback.onStart(localFile);
						}
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

	private TdApi.FormattedText getCaption(TdApi.MessageContent content) {
		if (content instanceof TdApi.MessageDocument) {
			return ((TdApi.MessageDocument) content).caption;
		} else if (content instanceof TdApi.MessagePhoto) {
			return ((TdApi.MessagePhoto) content).caption;
		} else if (content instanceof TdApi.MessageVideo) {
			return ((TdApi.MessageVideo) content).caption;
		} else if (content instanceof TdApi.MessageAnimation) {
			return ((TdApi.MessageAnimation) content).caption;
		} else if (content instanceof TdApi.MessageAudio) {
			return ((TdApi.MessageAudio) content).caption;
		}
		return null;
	}
}
