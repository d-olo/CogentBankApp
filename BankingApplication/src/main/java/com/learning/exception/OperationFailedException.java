package com.learning.exception;

public class OperationFailedException extends RuntimeException {
	public OperationFailedException(String e) {
		super(e);
	}

	@Override
	public String toString() {
		return super.getMessage();
	}
}
