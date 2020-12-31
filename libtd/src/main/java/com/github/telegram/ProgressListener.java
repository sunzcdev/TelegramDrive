package com.github.telegram;

public interface ProgressListener<S, T> {
	void onStart(S s);

	void onProgress(float progress);

	void onStop(T t);
}
