package com.github.telegramdrive.core;

public interface Callback<P, R> {
	R call(P p);
}
