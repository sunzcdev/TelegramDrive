package com.github.drive;

public interface Callback<P, R> {
	R call(P p);
}
