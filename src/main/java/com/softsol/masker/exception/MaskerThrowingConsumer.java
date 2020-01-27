package com.softsol.masker.exception;

@FunctionalInterface
public interface MaskerThrowingConsumer<T, E extends Exception> {
	void accept(T t) throws E;
}